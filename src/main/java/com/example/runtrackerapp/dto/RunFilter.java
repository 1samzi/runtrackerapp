package com.example.runtrackerapp.dto;

import jakarta.validation.constraints.*;
import lombok.Data;

import java.time.LocalDate;
@Data
public class RunFilter {
    @Min(0)
    private Double minDistance;
    private Double maxDistance;

    @Min(1)
    private Integer minRating;
    @Max(5)
    private Integer maxRating;
    private Integer exactRating;

    private Integer maxDuration;

    private LocalDate dateAfter;
    private LocalDate dateBefore;
    private LocalDate dateOn;

    private Long userId;

}
