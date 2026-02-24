//Anything that doesn't do with HTTP orchestration (request/response handling) must go here
package com.example.runtrackerapp.service;
import com.example.runtrackerapp.dto.RunCreateRequestDTO;
import com.example.runtrackerapp.dto.RunResponseDTO;
import com.example.runtrackerapp.exception.ResourceNotFoundException;
import com.example.runtrackerapp.mapper.RunMapper;
import com.example.runtrackerapp.model.Run;
import com.example.runtrackerapp.model.User;
import com.example.runtrackerapp.repository.RunRepository;

import com.example.runtrackerapp.repository.RunSpecification;
import com.example.runtrackerapp.repository.UserRepository;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
public class RunService{
    private final RunRepository repo;
    private final UserRepository userRepository;
    private final RunMapper runMapper;

    public RunService(RunRepository repository, UserRepository userRepository, RunMapper runMapper) {
        this.repo = repository;
        this.userRepository = userRepository;
        this.runMapper = runMapper;
    }

    public List<RunResponseDTO> findRunsByCriteria(
            Double minDistance, Double maxDistance,
            Integer minRating,Integer maxRating,
            Integer maxDuration, Integer exactRating,
            LocalDate dateAfter, LocalDate dateBefore,
            LocalDate dateOn,
            Long userId){

        Specification<Run> spec = (root, query, cb) -> cb.conjunction();

        if (minDistance != null) {
            spec = spec.and(RunSpecification.distanceGreaterThanOrEqual(minDistance));
        }
        //Practice: Find all runs that are both shorter than a given duration (in minutes) and less than a certain distance (in km).
        //aka "short and easy run"
        if (maxDistance != null) {
            spec = spec.and(RunSpecification.distanceLessThanOrEqual(maxDistance));
        }

        if (minRating != null) {
            spec = spec.and(RunSpecification.ratingIsGreaterThanOrEqual(minRating));
        }

        if (maxRating != null) {
            spec = spec.and(RunSpecification.ratingIsGreaterLessThanOrEqual(maxRating));
        }


        if (maxDuration != null) {
            spec = spec.and(RunSpecification.durationLessThan(maxDuration));

        }

        if (exactRating != null) {
            spec = spec.and(RunSpecification.hasRating(exactRating));
        }

        if (dateAfter != null) {
            spec = spec.and(RunSpecification.dateIsAfter(dateAfter));
        }

        if (dateBefore != null) {
            spec = spec.and(RunSpecification.dateIsBefore(dateBefore));
        }

        //Practice: "Runs on a Specific Day": Find all runs that occurred on a single, specific calendar day.
        if (dateOn != null){
            spec = spec.and(RunSpecification.dateIsOn(dateOn));
        }

        if (userId != null){
            if (!userRepository.existsById(userId)){
                throw new ResourceNotFoundException(
                        "User not found (No user with id): " + userId
                );
            }
            spec = spec.and(RunSpecification.hasUser(userId));
        }

        //Execute the combined specification
        List<Run> runs = repo.findAll(
                spec,
                Sort.by(Sort.Direction.DESC, "date"));

        return runs.stream()
                .map(runMapper::runMapperToResponseDTO)
                .toList();
    }

    public Run saveRun(RunCreateRequestDTO dto){
        User user = userRepository.findById(dto.getUserId())
                .orElseThrow(()-> new RuntimeException("User not found"));

        Run run = runMapper.runMapperToRun(dto);

        run.setUser(user);
        return repo.save(run);
    }

    public List<Run> saveRuns(List<RunCreateRequestDTO> dtos){
        List<Run> runs = new ArrayList<>();
        for (RunCreateRequestDTO dto: dtos){
            User user = userRepository.findById(dto.getUserId())
                    .orElseThrow(()-> new RuntimeException("User not found"));
            Run run = runMapper.runMapperToRun(dto);
            run.setUser(user);
            runs.add(run);
        }
        return repo.saveAll(runs);
    }

    public Run deleteRunById(Long id){
        Run runToDelete = repo.findById(id).orElseThrow(
                () -> new ResourceNotFoundException("Run not found (id): " + id)
        );

        repo.delete(runToDelete);

        return runToDelete;
    }

}