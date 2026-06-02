package com.example.runtrackerapp.dto;

import com.example.runtrackerapp.model.Run;
import lombok.Data;

import java.time.LocalDate;

// Aggregated run metrics for a single calendar period.
@Data
public class PeriodStatsResponseDTO {
    private String period;
    private LocalDate periodStart;
    private LocalDate periodEnd;
    private int totalRuns;
    private double totalDistance;
    private int totalDurationMinutes;
    private double averageDistance;
    private double averagePace;
    private double averageRating;
    private Run longestRun;
}