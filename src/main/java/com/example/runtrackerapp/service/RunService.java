//Anything that doesn't do with HTTP orchestration (request/response handling) must go here
package com.example.runtrackerapp.service;
import com.example.runtrackerapp.model.Run;
import com.example.runtrackerapp.model.User;
import com.example.runtrackerapp.repository.RunRepository;

import com.example.runtrackerapp.repository.RunSpecification;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class RunService{
    private final RunRepository repo;

    public RunService(RunRepository repository) {
        this.repo = repository;
    }

    public List<Run> findRunsByCriteria(
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
        return repo.findAll(
                spec,
                Sort.by(Sort.Direction.DESC, "date"));
    }

    public Run saveRun(Run run){
        return repo.save(run);
    }

    public List<Run> saveRuns(List<Run> runs){
        return repo.saveAll(runs);
    }

    public Run deleteRunById(Long id){
        Run run = repo.findById(id)
                .orElseThrow(() -> new RuntimeException("Run not found"));

        repo.delete(run);
        return run;
    }

}