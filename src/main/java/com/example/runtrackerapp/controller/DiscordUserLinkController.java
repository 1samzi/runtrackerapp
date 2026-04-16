package com.example.runtrackerapp.controller;

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
            @RequestBody UserCreateRequestDTO dto
    ) {
        return ResponseEntity.ok(discordUserLinkService.register(discordId, dto));
    }
}
