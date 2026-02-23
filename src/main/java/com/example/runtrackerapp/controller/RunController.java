package com.example.runtrackerapp.controller;

import com.example.runtrackerapp.dto.RunCreateRequestDTO;
import com.example.runtrackerapp.dto.RunResponseDTO;
import com.example.runtrackerapp.model.Run;

import com.example.runtrackerapp.service.RunService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/runs")
public class RunController {

    private final RunService runService;

    public RunController(RunService runService) {
        this.runService = runService;
    }

    //allows for /runs and /runs?minDistance=value
    @GetMapping
    public List<RunResponseDTO> getRuns(
            //For distance
            @RequestParam(required = false) Double minDistance,

            @RequestParam(required = false) Double maxDistance,

            //For runs that meet min rating
            @RequestParam(required = false) Integer minRating,

            @RequestParam(required = false) Integer maxRating,

            //For duration
            @RequestParam(required = false) Integer maxDuration,

            //Rating
            @RequestParam(required = false) Integer exactRating,

            //For dateAfter
            @RequestParam(required = false) LocalDate dateAfter,

            //For dateBefore
            @RequestParam(required = false) LocalDate dateBefore,

            //For date on
            @RequestParam(required = false) LocalDate dateOn,

            @RequestParam (required = false) Long userId
    ) {
        return runService.findRunsByCriteria(minDistance, maxDistance, minRating, maxRating, maxDuration, exactRating, dateAfter, dateBefore, dateOn, userId);
    }


    //RequestBody required to send body via JSON RequestParam can be used as well with URL but not used
    @PostMapping
    public Run createRun(@RequestBody RunCreateRequestDTO runDTO){
        return runService.saveRun(runDTO);
    }

    //Batch save of runs
    //New mapping required because Spring does not know what object it will receive by looking at the endpoint itself
    //Considered ambiguous and stops
    @PostMapping("/batch")
    public List<Run> createRuns(@RequestBody List<RunCreateRequestDTO> runsDTO){
        return runService.saveRuns(runsDTO);
    }

    //DELETE `/runs/{id}`
    @DeleteMapping("/{id}")
    public ResponseEntity<Run> deleteRun(@PathVariable Long id){
        Run deletedRun = runService.deleteRunById(id);
        return ResponseEntity.ok(deletedRun);
    }

}

