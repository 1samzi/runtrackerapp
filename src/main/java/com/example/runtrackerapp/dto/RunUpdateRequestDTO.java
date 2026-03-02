package com.example.runtrackerapp.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;
import java.time.LocalDate;

@Data
public class RunUpdateRequestDTO {
    @NotNull
    private Long runId;


    @Positive
    private Double distanceKM;


    @Positive
    private Integer durationMinutes;

    @NotNull
    private LocalDate date;

    @Min(1)
    @Max(5)
    private Integer rating;

    @NotNull
    private Long userId;
}
