package com.example.runtrackerapp.dto;

import com.example.runtrackerapp.model.Run;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.math.BigDecimal;

//Aggregated metrics of given user
@Data
public class UserStatsResponseDTO {
    private Long userId;
    private int totalRuns;
    @JsonFormat(shape = JsonFormat.Shape.NUMBER_FLOAT, pattern = "#.00")
    private double totalDistance;
    @JsonFormat(shape = JsonFormat.Shape.NUMBER_FLOAT, pattern = "#.00")
    private double averageDistance;
    @JsonFormat(shape = JsonFormat.Shape.NUMBER_FLOAT, pattern = "#.00")
    private double averagePace;
    private Run longestRun;
}
