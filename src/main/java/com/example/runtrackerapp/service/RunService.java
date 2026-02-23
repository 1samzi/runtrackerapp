//Anything that doesn't do with HTTP orchestration (request/response handling) must go here
package com.example.runtrackerapp.service;
import com.example.runtrackerapp.dto.RunCreateRequestDTO;
import com.example.runtrackerapp.dto.RunResponseDTO;
import com.example.runtrackerapp.model.Run;
import com.example.runtrackerapp.model.User;
import com.example.runtrackerapp.repository.RunRepository;

import com.example.runtrackerapp.repository.RunSpecification;
import com.example.runtrackerapp.repository.UserRepository;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class RunService{
    private final RunRepository repo;
    private final UserRepository userRepository;

    public RunService(RunRepository repository, UserRepository userRepository) {
        this.repo = repository;
        this.userRepository = userRepository;
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
            spec = spec.and(RunSpecification.hasUser(userId));
        }

        //Execute the combined specification
        List<Run> runs = repo.findAll(
                spec,
                Sort.by(Sort.Direction.DESC, "date"));

        return runs.stream()
                .map(this::runMapperToResponseDTO)
                .toList();
    }

    public Run saveRun(RunCreateRequestDTO dto){
        Run run = runMapperToRun(dto);
        return repo.save(run);
    }

    public List<Run> saveRuns(List<RunCreateRequestDTO> dtos){

        List<Run> runs = dtos.stream()
                .map(this::runMapperToRun)
                .toList();

        return repo.saveAll(runs);
    }

    public Run deleteRunById(Long id){
        Run run = repo.findById(id)
                .orElseThrow(() -> new RuntimeException("Run not found"));

        repo.delete(run);
        return run;
    }

    public Run runMapperToRun(RunCreateRequestDTO dto){

        User user = userRepository.findById(dto.getUserId())
                .orElseThrow(()-> new RuntimeException("User not found"));

        Run run = new Run();
        run.setDate(dto.getDate());
        run.setRating(dto.getRating());
        run.setDurationMinutes(dto.getDurationMinutes());
        run.setDistanceKM(dto.getDistanceKM());
        run.setUser(user);

        return run;
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