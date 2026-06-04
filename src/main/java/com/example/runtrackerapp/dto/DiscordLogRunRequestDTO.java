package com.example.runtrackerapp.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class DiscordLogRunRequestDTO {
    @NotNull
    @Positive
    private Double distanceKM;

    @NotNull
    @Positive
    private Integer durationMinutes;
}