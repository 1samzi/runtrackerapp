package com.example.runtrackerapp.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
public class UserCreateRequestDTO {
    private String username;
    private List<RunCreateRequestDTO> runs;

}
