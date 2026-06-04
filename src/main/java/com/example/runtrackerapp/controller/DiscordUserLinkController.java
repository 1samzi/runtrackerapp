package com.example.runtrackerapp.controller;

import com.example.runtrackerapp.dto.DiscordLinkExistingRequestDTO;
import com.example.runtrackerapp.dto.DiscordLogRunRequestDTO;
import com.example.runtrackerapp.dto.UserCreateRequestDTO;
import com.example.runtrackerapp.service.DiscordUserLinkService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/bot")
public class DiscordUserLinkController {

    private final DiscordUserLinkService discordUserLinkService;

    public DiscordUserLinkController(DiscordUserLinkService discordUserLinkService) {
        this.discordUserLinkService = discordUserLinkService;
    }

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(
            @RequestHeader("X-DISCORD-ID") String discordId,
            @RequestHeader("X-BOT-KEY") String botKey,
            @RequestBody UserCreateRequestDTO dto
    ) {
        return createUserFromDiscord(discordId, botKey, dto);
    }

    @PostMapping("/create-user")
    public ResponseEntity<?> createUserFromDiscord(
            @RequestHeader("X-DISCORD-ID") String discordId,
            @RequestHeader("X-BOT-KEY") String botKey,
            @RequestBody UserCreateRequestDTO dto
    ) {
        return ResponseEntity.ok(discordUserLinkService.createUserFromDiscord(discordId, botKey, dto));
    }

    @PostMapping("/link-existing")
    public ResponseEntity<?> linkExistingUser(
            @RequestHeader("X-DISCORD-ID") String discordId,
            @RequestHeader("X-BOT-KEY") String botKey,
            @RequestBody DiscordLinkExistingRequestDTO dto
    ) {
        return ResponseEntity.ok(discordUserLinkService.linkExisting(discordId, botKey, dto.getUserId()));
    }

    @PostMapping("/runs")
    public ResponseEntity<?> logRun(
            @RequestHeader("X-DISCORD-ID") String discordId,
            @RequestHeader("X-BOT-KEY") String botKey,
            @Valid @RequestBody DiscordLogRunRequestDTO dto
    ) {
        return ResponseEntity.ok(discordUserLinkService.logRun(discordId, botKey, dto));
    }

    @GetMapping("/stats")
    public ResponseEntity<?> getLinkedUserStats(
            @RequestHeader("X-DISCORD-ID") String discordId,
            @RequestHeader("X-BOT-KEY") String botKey
    ) {
        return ResponseEntity.ok(discordUserLinkService.getLinkedUserStats(discordId, botKey));
    }

    @DeleteMapping("/unlink")
    public ResponseEntity<?> unlinkDiscordAccount(
            @RequestHeader("X-DISCORD-ID") String discordId,
            @RequestHeader("X-BOT-KEY") String botKey
    ) {
        return ResponseEntity.ok(discordUserLinkService.unlinkUser(discordId, botKey));
    }
}
