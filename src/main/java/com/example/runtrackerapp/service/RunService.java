//Anything that doesn't do with HTTP orchestration (request/response handling) must go here
package com.example.runtrackerapp.service;
import com.example.runtrackerapp.dto.RunCreateRequestDTO;
import com.example.runtrackerapp.dto.RunFilter;
import com.example.runtrackerapp.dto.RunResponseDTO;
import com.example.runtrackerapp.dto.RunUpdateRequestDTO;
import com.example.runtrackerapp.exception.ResourceNotFoundException;
import com.example.runtrackerapp.mapper.RunMapper;
import com.example.runtrackerapp.model.Run;
import com.example.runtrackerapp.model.User;
import com.example.runtrackerapp.repository.RunRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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

    public Page<RunResponseDTO> findRunsByCriteria(
            RunFilter filter,
            Pageable pageable){

        Specification<Run> spec = (root, query, cb) -> cb.conjunction();

        if (filter.getMinDistance() != null) {
            spec = spec.and(RunSpecification.distanceGreaterThanOrEqual(filter.getMinDistance()));
        }
        //Practice: Find all runs that are both shorter than a given duration (in minutes) and less than a certain distance (in km).
        //aka "short and easy run"
        if (filter.getMaxDistance() != null) {
            spec = spec.and(RunSpecification.distanceLessThanOrEqual(filter.getMaxDistance()));
        }

        if (filter.getMinRating() != null) {
            spec = spec.and(RunSpecification.ratingIsGreaterThanOrEqual(filter.getMinRating()));
        }

        if (filter.getMaxRating() != null) {
            spec = spec.and(RunSpecification.ratingIsGreaterLessThanOrEqual(filter.getMaxRating()));
        }


        if (filter.getMaxDuration() != null) {
            spec = spec.and(RunSpecification.durationLessThan(filter.getMaxDuration()));

        }

        if (filter.getExactRating() != null) {
            spec = spec.and(RunSpecification.hasRating(filter.getExactRating()));
        }

        if (filter.getDateAfter() != null) {
            spec = spec.and(RunSpecification.dateIsAfter(filter.getDateAfter() ));
        }

        if (filter.getDateBefore() != null) {
            spec = spec.and(RunSpecification.dateIsBefore(filter.getDateBefore()));
        }

        //Practice: "Runs on a Specific Day": Find all runs that occurred on a single, specific calendar day.
        if (filter.getDateOn() != null){
            spec = spec.and(RunSpecification.dateIsOn(filter.getDateOn()));
        }

        if (filter.getUserId() != null){
            if (!userRepository.existsById(filter.getUserId())){
                throw new ResourceNotFoundException(
                        "User not found (No user with id): " + filter.getUserId()
                );
            }
            spec = spec.and(RunSpecification.hasUser(filter.getUserId()));
        }

        //Execute the combined specification
        return repo.findAll(spec, pageable).map(runMapper::runMapperToResponseDTO);


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

    public Run putRun(Long id, RunUpdateRequestDTO dto){
        Run runToUpdate = repo.findById(id)
                .orElseThrow(() -> new RuntimeException("Run not found (id): " + id));

        runToUpdate.setDistanceKM(dto.getDistanceKM());
        runToUpdate.setDurationMinutes(dto.getDurationMinutes());
        runToUpdate.setRating(dto.getRating());

        return repo.save(runToUpdate);
    }

    public Run patchRun(Long id, RunUpdateRequestDTO dto){
        Run runToUpdate= repo.findById(id)
                .orElseThrow(() -> new RuntimeException("Run not found (id): " + id));
        if (dto.getDistanceKM() != null){
            runToUpdate.setDistanceKM(dto.getDistanceKM());
        }

        if (dto.getDurationMinutes() != null){
            runToUpdate.setDurationMinutes(dto.getDurationMinutes());
        }

        if (dto.getRating() != null){
            runToUpdate.setRating(dto.getRating());
        }

        return repo.save(runToUpdate);

    }


    public Run deleteRunById(Long id){
        Run runToDelete = repo.findById(id).orElseThrow(
                () -> new ResourceNotFoundException("Run not found (id): " + id)
        );

        repo.delete(runToDelete);

        return runToDelete;
    }

}