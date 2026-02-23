package com.example.runtrackerapp.dto;

import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
public class UserCreateRequestDTO {
    @NotBlank
    @Size(min = 3, max = 30)
    private String username;
    private List<RunCreateRequestDTO> runs;

}
