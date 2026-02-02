package com.example.runtrackerapp.repository;

import com.example.runtrackerapp.model.Run;
import com.example.runtrackerapp.model.User;
import org.assertj.core.api.AssertionsForClassTypes;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;


@DataJpaTest
public class RunRepositoryTest {
    @Autowired
    RunRepository runRepository;

    @Autowired
    private TestEntityManager entityManager;

    @Test
    void givenARepo_whenSavingAndFindingRun_thenRepoHasARun() {
        //Given
        User user = new User("Daryl");
        Run run = new Run(5, 45, LocalDate.now(), 5);
        user.addRun(run);
        entityManager.persistAndFlush(user);
        entityManager.clear();
        //When

        //Then
        assertThat(runRepository.findAll()).hasSize(1);
    }

    @Test
    void givenANewRun_whenRunIsSaved_thenIDIsGenerated(){
        //Given
        User user = new User("Jon Snow");
        Run run = new Run(5, 45, LocalDate.now(), 5);
        user.addRun(run);
        User savedUser = entityManager.persistAndFlush(user);
        entityManager.clear();
        //When
        Optional<Run> found = runRepository.findById(savedUser.getRuns().get(0).getRun_id());

        //Then
        assertThat(found).isPresent();
        assertThat(found.get().getUser().getUsername()).isEqualTo("Jon Snow");
    }

    @Test
    void givenEmptyRepo_whenFindByIdIsCalled_thenResultIsEmpty(){
        //Given

        //When
        Optional<Run> found = runRepository.findById(1L);

        //Then
        assertThat(found.isEmpty());
    }

    @Test
    void givenASavedRun_whenTheRunIsDeleted_thenRepoNoLongerHasRun(){
        //Given
        User user = new User("Dany");
        Run run = new Run (3, 30, LocalDate.now(), 4);
        user.addRun(run);

        entityManager.persistAndFlush(user);
        entityManager.clear();
        //When
        runRepository.delete(run);

        //Then
        AssertionsForClassTypes.assertThat(runRepository.findAll().size()).isEqualTo(0);
    }

    @Test
    void givenARunInRepo_whenUpdatedAndSaved_thenChangesAreReflectedInRepo(){
        //Given
        User user = new User("Dunk");
        Run run = new Run (10, 110, LocalDate.now(), 4);
        user.addRun(run);
        entityManager.persistAndFlush(user);
        entityManager.clear();
        //When
        run.setRating(2);
        runRepository.save(run);

        //Then
        AssertionsForClassTypes.assertThat(runRepository.findById(run.getRun_id()).get().getRating()).isEqualTo(2);
    }
}
