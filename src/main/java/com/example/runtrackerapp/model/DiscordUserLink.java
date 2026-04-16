package com.example.runtrackerapp.model;

import jakarta.persistence.*;
import lombok.Getter;

import java.time.LocalDateTime;

@Entity
public class DiscordUserLink {

    @Id
    @GeneratedValue
    private Long id;

    @Column(unique = true)
    private String discordUserId;

    // allow for different integrations e.g. slack, telegram, or just multiple discord accounts
    @Getter
    @ManyToOne
    @JoinColumn(name = "app_user_id")
    private User user;

    private LocalDateTime createdAt;

    public void setDiscordUserId(String discordUserId) {
        this.discordUserId = discordUserId;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
