package com.example.runtrackerapp.service;

import com.example.runtrackerapp.dto.RunCreateRequestDTO;
import com.example.runtrackerapp.dto.RunResponseDTO;
import com.example.runtrackerapp.dto.UserCreateRequestDTO;
import com.example.runtrackerapp.dto.UserResponseDTO;
import com.example.runtrackerapp.model.Run;
import com.example.runtrackerapp.model.User;
import com.example.runtrackerapp.repository.UserRepository;
import com.example.runtrackerapp.repository.UserSpecification;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {
    private final UserRepository repo;

    UserService(UserRepository repository){
        this.repo = repository;
    }

    public List<UserResponseDTO> findUsersByCriteria(Long userId){
        //init specification to TRUE
        Specification<User> spec = (root, query, cb) -> cb.conjunction();

        if (userId != null){
            spec = spec.and(UserSpecification.hasId(userId));
        }

        List<User> users = repo.findAll(
                spec,
                Sort.by(Sort.Direction.DESC, "createdAt"));
        return users.stream().map(this::mapUserToUserResponseDTO).toList();
    }

    public User saveUser(UserCreateRequestDTO dto){
        User user = mapUserRequestDTOToUser(dto);
        return repo.save(user);
    }

    public List<User> saveUsers(List<UserCreateRequestDTO> dtos){
        List<User> users = dtos.stream()
                .map(this::mapUserRequestDTOToUser)
                .toList();
        return repo.saveAll(users);
    }

    public User deleteUserById(Long id){
        User user = repo.findById(id)
                .orElseThrow(() -> new RuntimeException("Run not found"));

        repo.delete(user);
        return user;
    }



    //Mappers for DTO
    private Run mapRunRequestDTOToRun(RunCreateRequestDTO dto){
        Run run = new Run();

        run.setDistanceKM(dto.getDistanceKM());
        run.setDurationMinutes(dto.getDurationMinutes());
        run.setDate(dto.getDate());
        run.setRating(dto.getRating());

        return run;
    }

    private User mapUserRequestDTOToUser(UserCreateRequestDTO dto){
        User user = new User();

        user.setUsername(dto.getUsername());

        List<Run> runs = dto.getRuns().stream()
                .map(this::mapRunRequestDTOToRun)
                .toList();

        runs.forEach(r -> r.setUser(user));
        user.setRuns(runs);
        return user;
    }

    private UserResponseDTO mapUserToUserResponseDTO(User user){
        UserResponseDTO dto = new UserResponseDTO();
        dto.setRuns(user.getRuns().stream().map(this::runMapperToResponseDTO).toList());
        dto.setUser_id(user.getUser_id());
        dto.setUsername(user.getUsername());

        return dto;
    }

    public RunResponseDTO runMapperToResponseDTO(Run run){

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
}
