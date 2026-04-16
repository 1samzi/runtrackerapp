package com.example.runtrackerapp.repository;

import com.example.runtrackerapp.model.DiscordUserLink;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface DiscordUserLinkRepository extends JpaRepository<DiscordUserLink, Long> {
    Optional<DiscordUserLink> findByDiscordUserId(String discordUserId);
}