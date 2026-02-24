package com.example.runtrackerapp.mapper;

import com.example.runtrackerapp.dto.RunCreateRequestDTO;
import com.example.runtrackerapp.dto.RunResponseDTO;
import com.example.runtrackerapp.model.Run;
import org.springframework.stereotype.Component;

@Component
public class RunMapper {

    public RunResponseDTO runMapperToResponseDTO(Run run) {

        RunResponseDTO dto = new RunResponseDTO();

        dto.setId(run.getRun_id());
        dto.setDistanceKM(run.getDistanceKM());
        dto.setDurationMinutes(run.getDurationMinutes());
        dto.setDate(run.getDate());
        dto.setRating(run.getRating());

        if (run.getUser() != null) {
            dto.setUser_id(run.getUser().getUser_id());
        }

        return dto;
    }

    public Run runMapperToRun(RunCreateRequestDTO dto){
        Run run = new Run();
        run.setDate(dto.getDate());
        run.setRating(dto.getRating());
        run.setDurationMinutes(dto.getDurationMinutes());
        run.setDistanceKM(dto.getDistanceKM());

        return run;
    }
}