package com.example.runtrackerapp.service;

import com.example.runtrackerapp.dto.RunFilter;
import com.example.runtrackerapp.dto.RunResponseDTO;
import com.example.runtrackerapp.mapper.RunMapper;
import com.example.runtrackerapp.model.Run;
import com.example.runtrackerapp.model.User;

import com.example.runtrackerapp.repository.RunRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;


@ExtendWith(MockitoExtension.class)
public class RunServiceTest {
    //If it’s injected in the constructor > mock it.
    //Could add userRepo to test if attached user loses specified run but lazy :D
    @Mock
    RunRepository runRepository;

    @Mock
    private RunMapper runMapper;

    @InjectMocks
    RunService runService;

    @Test
    void givenNoCriteria_whenFindRunsByCriteria_ThenNoFilterApplied(){
        // Given
        Run run1 = new Run(1, 12, LocalDate.now(), 3);
        Run run2 = new Run(5, 20, LocalDate.now(), 4);

        List<Run> runRepo = List.of(run1, run2); // no criteria applied

        Page<Run> page = new PageImpl<>(runRepo);

        Pageable pageable = PageRequest.of(0, 10);

        RunFilter filter = new RunFilter();

        // When
        //Mock repo
        when(runRepository.findAll(
                any(Specification.class),
                any(Pageable.class)
        )).thenReturn(page);
        //Mock mapper
        when(runMapper.runMapperToResponseDTO(any(Run.class)))
                .thenAnswer(inv -> {
                    Run r = inv.getArgument(0);

                    RunResponseDTO dto = new RunResponseDTO();
                    dto.setDistanceKM(r.getDistanceKM());

                    return dto;
                });

        Page<RunResponseDTO> result =
                runService.findRunsByCriteria(filter, pageable);

        // Then
        assertEquals(2, result.getTotalElements());
        assertEquals(2, result.getContent().size());



    }

    @Test
    void givenACriteria_whenFindRunsByCriteria_ThenNoFilterApplied(){
        // Given
        Run run1 = new Run(1, 12, LocalDate.now(), 3);
        Run run2 = new Run(5, 20, LocalDate.now(), 4);
        List<Run> filteredRuns = Stream.of(run1, run2)
                .filter(run -> run.getDistanceKM() > 3)
                .toList();

        Page<Run> page = new PageImpl<>(filteredRuns);

        Pageable pageable = PageRequest.of(0, 10);

        RunFilter filter = new RunFilter(); //filters applied, must set
        filter.setMinDistance(3D);

        // When
        // Mock repo
        when(runRepository.findAll(
                any(Specification.class),
                any(Pageable.class)
        )).thenReturn(page);
        // Mock mapper
        when(runMapper.runMapperToResponseDTO(any(Run.class)))
                .thenAnswer(inv -> {
                    Run r = inv.getArgument(0);

                    RunResponseDTO dto = new RunResponseDTO();
                    dto.setDistanceKM(r.getDistanceKM());

                    return dto;
                });
        var result = runService.findRunsByCriteria(filter, pageable);

        assertEquals(1, result.getTotalElements());
        RunResponseDTO dto = result.getContent().getFirst();
        assertTrue(dto.getDistanceKM() > 3);

    }

    @Test
    void givenNoRuns_whenDeleteRunById_ThenExceptionThrown(){
        // Given

        // When
        when(runRepository.findById(any(Long.class))).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> {
            runService.deleteRunById(5L);
        });
        verify(runRepository, never()).delete(any(Run.class));
    }

    @Test
    void givenARun_whenDeleteRunById_ThenRunIsDeleted(){
        // Given
        Run run = new Run(5, 20, LocalDate.now(), 4);
        run.setRun_id(5L);
        List<Run> runRepo = List.of(run);
        // When
        when(runRepository.findById(any(Long.class))).thenReturn(Optional.of(run));
        var result = runService.deleteRunById(5L);

        assertEquals(result, run);
        verify(runRepository, times(1)).delete(any(Run.class));
    }

}
