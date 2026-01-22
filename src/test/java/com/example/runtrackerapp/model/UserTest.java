package com.example.runtrackerapp.model;

import org.junit.jupiter.api.Test;
import java.time.LocalDate;
import static org.junit.jupiter.api.Assertions.*;

public class UserTest {
    @Test
    void constructorThrowsWhenInvalidValues(){
        assertThrows(IllegalArgumentException.class, () ->{
            new User("");
        });
    }
    @Test
    void validUserIsCreated(){
        User user = new User("Username");
        assertEquals("Username", user.getUsername());
    }
}
