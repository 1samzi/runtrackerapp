package com.example.runtrackerapp.mapper;

import com.example.runtrackerapp.dto.UserCreateRequestDTO;
import com.example.runtrackerapp.dto.UserResponseDTO;
import com.example.runtrackerapp.model.Run;
import com.example.runtrackerapp.model.User;
import org.springframework.stereotype.Component;

import java.util.List;
@Component
public class UserMapper {
    private final RunMapper runMapper;

    public UserMapper(RunMapper runMapper) {
        this.runMapper = runMapper;
    }
    public User mapUserRequestDTOToUser(UserCreateRequestDTO dto){
        User user = new User();

        user.setUsername(dto.getUsername());

        List<Run> runs = dto.getRuns().stream()
                .map(runMapper::runMapperToRun)
                .toList();

        runs.forEach(r -> r.setUser(user));
        user.setRuns(runs);
        return user;
    }

    public UserResponseDTO mapUserToUserResponseDTO(User user){
        UserResponseDTO dto = new UserResponseDTO();
        dto.setRuns(user.getRuns().stream().map(runMapper::runMapperToResponseDTO).toList());
        dto.setUser_id(user.getUser_id());
        dto.setUsername(user.getUsername());

        return dto;
    }
}
