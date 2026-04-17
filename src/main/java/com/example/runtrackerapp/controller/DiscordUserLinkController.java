package com.example.runtrackerapp.controller;

import com.example.runtrackerapp.dto.DiscordLinkExistingRequestDTO;
import com.example.runtrackerapp.dto.UserCreateRequestDTO;
import com.example.runtrackerapp.service.DiscordUserLinkService;
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
        return ResponseEntity.ok(discordUserLinkService.register(discordId, botKey, dto));
    }

    @PostMapping("/link-existing")
    public ResponseEntity<?> linkExistingUser(
            @RequestHeader("X-DISCORD-ID") String discordId,
            @RequestHeader("X-BOT-KEY") String botKey,
            @RequestBody DiscordLinkExistingRequestDTO dto
    ) {
        return ResponseEntity.ok(discordUserLinkService.linkExisting(discordId, botKey, dto.getUserId()));
    }

    @GetMapping("/stats")
    public ResponseEntity<?> getLinkedUserStats(
            @RequestHeader("X-DISCORD-ID") String discordId,
            @RequestHeader("X-BOT-KEY") String botKey
    ) {
        return ResponseEntity.ok(discordUserLinkService.getLinkedUserStats(discordId, botKey));
    }
}
