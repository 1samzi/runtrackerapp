package com.example.runtrackerapp.service;

import com.example.runtrackerapp.dto.UserCreateRequestDTO;
import com.example.runtrackerapp.mapper.UserMapper;
import com.example.runtrackerapp.model.DiscordUserLink;
import com.example.runtrackerapp.model.User;
import com.example.runtrackerapp.repository.DiscordUserLinkRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class DiscordUserLinkService {

    private final DiscordUserLinkRepository repo;
    private final UserService userService;
    private final UserMapper userMapper;

    public DiscordUserLinkService(DiscordUserLinkRepository repo, UserService userService, UserMapper userMapper) {
        this.repo = repo;
        this.userService = userService;
        this.userMapper = userMapper;
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
                .orElseThrow(() -> new RuntimeException("User not linked"))
                .getUser();
    }

    public boolean isLinked(String discordId) {
        return repo.findByDiscordUserId(discordId).isPresent();
    }

    public Object register(String discordId, UserCreateRequestDTO dto) {
        if (isLinked(discordId)) {
            throw new RuntimeException("Discord account already registered");
        }
        User user = userService.saveUser(dto);
        linkUser(discordId, user);
        return userMapper.mapUserToUserResponseDTO(user);
    }
}
