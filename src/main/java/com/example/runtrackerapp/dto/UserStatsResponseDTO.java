package com.example.runtrackerapp.dto;

import com.example.runtrackerapp.model.Run;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

//Aggregated metrics of given user
@Data
public class UserStatsResponseDTO {
    private Long userId;
    private int totalRuns;
    private double totalDistance;
    private int totalDurationMinutes;
    private double averageDistance;
    private double averageDurationMinutes;
    private double averagePace;
    private double averageRating;
    private Run longestRun;
    private Run shortestRun;
    private Run fastestRun;
    private List<PeriodStatsResponseDTO> weeklyStats;
    private List<PeriodStatsResponseDTO> monthlyStats;
    private List<PeriodStatsResponseDTO> yearlyStats;
}