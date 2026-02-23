package com.example.runtrackerapp.dto;

import lombok.Getter;
import jakarta.validation.constraints.*;
import java.time.LocalDate;

//Note: no id, no user, no timestamps
@Getter
public class RunCreateRequestDTO {

    @NotNull
    @Positive
    private double distanceKM;

    @NotNull
    @Positive
    private int durationMinutes;

    @NotNull
    private LocalDate date;

    @Min(1)
    @Max(5)
    private int rating;

    @NotNull
    private Long userId;
}
