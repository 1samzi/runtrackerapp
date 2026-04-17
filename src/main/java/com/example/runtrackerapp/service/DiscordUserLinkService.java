package com.example.runtrackerapp.service;

import com.example.runtrackerapp.dto.UserCreateRequestDTO;
import com.example.runtrackerapp.dto.UserResponseDTO;
import com.example.runtrackerapp.dto.UserStatsResponseDTO;
import com.example.runtrackerapp.mapper.UserMapper;
import com.example.runtrackerapp.model.DiscordUserLink;
import com.example.runtrackerapp.model.User;
import com.example.runtrackerapp.repository.UserRepository;
import com.example.runtrackerapp.repository.DiscordUserLinkRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;

@Service
public class DiscordUserLinkService {

    private final DiscordUserLinkRepository repo;
    private final UserRepository userRepository;
    private final UserService userService;
    private final UserMapper userMapper;
    private final String botApiKey;

    public DiscordUserLinkService(
            DiscordUserLinkRepository repo,
            UserRepository userRepository,
            UserService userService,
            UserMapper userMapper,
            @Value("${bot.api.key}") String botApiKey){
        this.repo = repo;
        this.userRepository = userRepository;
        this.userService = userService;
        this.userMapper = userMapper;
        this.botApiKey = botApiKey;
    }

    public void linkUser(String discordId, User user) {
        DiscordUserLink link = new DiscordUserLink();
        link.setDiscordUserId(discordId);
        link.setUser(user);
        link.setCreatedAt(LocalDateTime.now());
        repo.save(link);
    }

    public User getUserFromDiscordId(String discordId) {
        return repo.findByDiscordUserId(discordId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Discord account not linked"))
                .getUser();
    }

    public boolean isLinked(String discordId) {
        return repo.findByDiscordUserId(discordId).isPresent();
    }

    public UserResponseDTO register(String discordId, String providedBotApiKey, UserCreateRequestDTO dto) {
        validateBotApiKey(providedBotApiKey);
        if (isLinked(discordId)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Discord account already linked");
        }
        User user = userService.saveUser(dto);
        linkUser(discordId, user);
        return userMapper.mapUserToUserResponseDTO(user);
    }

    public UserResponseDTO linkExisting(String discordId, String providedBotApiKey, Long userId) {
        validateBotApiKey(providedBotApiKey);
        if (isLinked(discordId)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Discord account already linked");
        }

        User existingUser = userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        linkUser(discordId, existingUser);
        return userMapper.mapUserToUserResponseDTO(existingUser);
    }

    public UserStatsResponseDTO getLinkedUserStats(String discordId, String providedBotApiKey) {
        validateBotApiKey(providedBotApiKey);
        User linkedUser = getUserFromDiscordId(discordId);
        return userService.getUserStats(linkedUser.getUser_id());
    }

    private void validateBotApiKey(String providedBotApiKey) {
        if (!botApiKey.equals(providedBotApiKey)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Invalid bot key");
        }
    }
}
