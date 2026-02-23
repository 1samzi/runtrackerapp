package com.example.runtrackerapp.dto;


import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
@Setter
@Getter
public class RunResponseDTO {
    private Long id;
    private double distanceKM;
    private int durationMinutes;
    private LocalDate date;
    private int rating;
    private Long user_id;
}
