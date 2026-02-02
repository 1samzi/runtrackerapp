package com.example.runtrackerapp.repository;

import com.example.runtrackerapp.model.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;

//This annotation is the core of the test setup. It does the following:
    //Configures an in-memory embedded database (like H2) by default, so you don't affect your real development database.
    //Scans for @Entity classes and configures Spring Data JPA repositories.
    //Applies only relevant JPA configurations, making the test context lightweight and fast.
    //By default, tests are transactional and roll back after each test, ensuring a clean database state for the next test method.


@DataJpaTest
public class UserRepositoryTest {

    //This is used to inject the UserRepository bean into the test class.
    //Since @DataJpaTest sets up a limited Spring application context,
    //it can successfully find and inject the repository instance.
    @Autowired
    UserRepository userRepository;

    @Autowired
    private TestEntityManager entityManager;


    @Test
    void givenARepo_whenSavingAndFindingUser_thenRepoHasAUser() {
        User user = new User("sam");
        userRepository.save(user);
        assertThat(userRepository.findAll()).hasSize(1);
    }

    @Test
    void givenANewUser_whenUserisSaved_thenIDIsGenerated(){
        User user = new User("Jon Snow");
        User savedUser = entityManager.persistAndFlush(user);
        entityManager.clear();
        Optional<User> found = userRepository.findById(savedUser.getUser_id());

        assertThat(savedUser.getUser_id()).isNotNull();
        assertThat(found.get().getUsername()).isEqualTo("Jon Snow");
    }

    @Test
    void givenNewUsers_whenAllUsersAreSaved_thenfindAllReturnsAllUsers(){
        //Given
        User user1 = new User("Davos");
        User user2 = new User("Stannis");
        entityManager.persist(user1);
        entityManager.persist(user2);
        entityManager.flush();
        entityManager.clear();

        //When
        List<User> foundUsers = userRepository.findAll();

        //Then
        assertThat(foundUsers).isNotEmpty();
        assertThat(foundUsers.size()).isEqualTo(2);

    }

    @Test
    void givenEmptyRepo_whenFindByIdIsCalled_thenResultIsEmpty(){
        //Given

        //When
        Optional<User> found = userRepository.findById(1L);

        //Then
        assertThat(found.isEmpty());
    }

    @Test
    void givenASavedUser_whenTheUserIsDeleted_thenRepoNoLongerHasUser(){
        //Given
        User user = new User("Dany");
        entityManager.persistAndFlush(user);
        entityManager.clear();

        //When
        userRepository.delete(user);

        //Then
        assertThat(userRepository.findById(user.getUser_id())).isEmpty();
    }

    @Test
    void givenAUserInRepo_whenUpdatedAndSaved_thenChangesAreReflectedInRepo(){
        //Given
        User user = new User("Dunk");
        entityManager.persistAndFlush(user);
        entityManager.clear();
        //When
        user.setUsername("John");
        userRepository.save(user);

        //Then
        assertThat(userRepository.findById(user.getUser_id()).get().getUsername()).isEqualTo("John");
    }

}
