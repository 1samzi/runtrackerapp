package com.example.runtrackerapp.controller;

import com.example.runtrackerapp.dto.*;
import com.example.runtrackerapp.mapper.RunMapper;
import com.example.runtrackerapp.model.Run;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import com.example.runtrackerapp.service.RunService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/runs")
public class RunController {

    private final RunService runService;
    private final RunMapper runMapper;

    public RunController(RunService runService, RunMapper runMapper) {
        this.runService = runService;
        this.runMapper = runMapper;
    }

    //allows for /runs and /runs?minDistance=value
    @GetMapping
    public Page<RunResponseDTO> getRuns(
            @Valid RunFilter filter,
            Pageable pageable
    ) {
        return runService.findRunsByCriteria(filter, pageable);
    }


    //RequestBody required to send body via JSON RequestParam can be used as well with URL but not used
    @PostMapping
    public Run createRun(@Valid @RequestBody RunCreateRequestDTO runDTO){
        return runService.saveRun(runDTO);
    }

    //Batch save of runs
    //New mapping required because Spring does not know what object it will receive by looking at the endpoint itself
    //Considered ambiguous and stops
    @PostMapping("/batch")
    public List<Run> createRuns(@Valid @RequestBody List<RunCreateRequestDTO> runsDTO){
        return runService.saveRuns(runsDTO);
    }

    @PutMapping ("/{id}")
    public ResponseEntity<RunResponseDTO> putRun(
            @PathVariable Long id,
            @RequestBody RunUpdateRequestDTO dto){
        Run run = runService.putRun(id, dto);
        RunResponseDTO response = runMapper.runMapperToResponseDTO(run);
        return ResponseEntity.ok(response);

    }

    @PatchMapping("/{id}")
    public ResponseEntity <RunResponseDTO> patchRun(
            @PathVariable Long id,
            @RequestBody RunUpdateRequestDTO dto
    ){
        Run run = runService.patchRun(id, dto);
        RunResponseDTO response = runMapper.runMapperToResponseDTO(run);
        return ResponseEntity.ok(response);
    }


    //DELETE `/runs/{id}`
    @DeleteMapping("/{id}")
    public ResponseEntity<RunResponseDTO> deleteRun(@PathVariable Long id){
        Run deletedRun = runService.deleteRunById(id);
        RunResponseDTO dto = runMapper.runMapperToResponseDTO(deletedRun);
        return ResponseEntity.ok(dto);
    }

}

