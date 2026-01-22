package com.example.runtrackerapp.model;

import org.junit.jupiter.api.Test;
import java.time.LocalDate;
import static org.junit.jupiter.api.Assertions.*;

public class RunTest {

    @Test
    void constructorThrowsWhenInvalidValues(){
        assertThrows(IllegalArgumentException.class, () -> {
            new Run (0, 0, LocalDate.now(), 3);
        });

    }

    @Test
    void validRunIsCreated(){
        Run run = new Run(5, 30, LocalDate.now(), 3);
        assertEquals(5, run.getDistanceKM());
        assertEquals(30, run.getDurationMinutes());
        assertEquals(3, run.getRating());
    }
}
