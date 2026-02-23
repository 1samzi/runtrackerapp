package com.example.runtrackerapp.dto;


import lombok.Getter;
import lombok.Setter;

import java.util.List;
@Setter
@Getter
public class UserResponseDTO {
    private Long user_id;
    private String username;
    private List<RunResponseDTO> runs;
}
