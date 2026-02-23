package com.example.runtrackerapp.dto;

import lombok.Getter;

import java.time.LocalDate;

//Note: no id, no user, no timestamps
@Getter
public class RunCreateRequestDTO {
    private double distanceKM;
    private int durationMinutes;
    private LocalDate date;
    private int rating;
    private Long userId;
}
