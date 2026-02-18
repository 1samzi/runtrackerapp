package com.example.runtrackerapp.dto;

import java.time.LocalDate;

//Note: no id, no user, no timestamps
public class RunRequestDTO {
    private double distanceKM;
    private int durationMinutes;
    private LocalDate date;
    private int rating;
}
