package java.com.example.runtrackerapp.service;

import com.example.runtrackerapp.model.Run;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import com.example.runtrackerapp.model.User;
import com.example.runtrackerapp.repository.UserRepository;
import com.example.runtrackerapp.service.UserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

//Acts as a bridge between JUnit 5 and Mockito -> creates mock objects, injects them into test classes, resets mocks between tests
//enables @Mock and @InjectMocks
@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @Mock
    UserRepository userRepository;

    @InjectMocks
    UserService userService;

    //TODO Update test to take DTO rather than user
    @Test
    void givenUser_WhenRunsAdded_ThenRunAssociatesToUser(){
        //Given
        User user = new User("sam");
        Run run1 = new Run(5, 30, LocalDate.now(),3);
        Run run2 = new Run(2, 12, LocalDate.now().minusDays(5), 4);
        user.addRuns(Arrays.asList(run1, run2));
        //When (findAll is called with any Specification and any Sort, then return this result.)
        //When save is called don't hit the db, just return the same user object that was passed ... simulating JPA save() returning the entity ... get the first argument passed into the method aka "sam" (user == saved)
        when(userRepository.save(any(User.class))).thenAnswer(inv -> inv.getArgument(0));
        //User saved = userService.saveUser(user);
        //Then
        //assertEquals("sam", saved.getUsername());
        assertSame(user, run1.getUser());
        assertSame(user, run2.getUser());
        verify(userRepository).save(user);

    }
    @Test
    void givenNoCriteria_WhenFindUsersByCriteria_ThenNoFilteringApplied(){
        //Given
        User user1 = new User ("Bob");
        User user2 = new User ("Mary");
        List<User> repoUsers = Arrays.asList(user1, user2);

        //When (findAll is called with any Specification and any Sort, then return this result.)
        when(userRepository.findAll(
                any(Specification.class),
                any(Sort.class)
        )).thenReturn(repoUsers);

        List<User> result = userService.findUsersByCriteria(null);

        //Then
        assertEquals(2, result.size());
        assertEquals(repoUsers, result);
    }
    @Test
    void givenCriteria_WhenFindUsersByCriteria_ThenFilteringApplied(){
        //Given
        User user1 = new User ("Bob");
        user1.setUser_id(1);
        User user2 = new User ("Mary");
        user2.setUser_id(2);
        List<User> repoFilteredUsers = Stream.of(user1, user2)
                .filter(user -> user.getUser_id() == 1)
                .toList();
        //When (findAll is called with any Specification and any Sort, then return this result.)
        when(userRepository.findAll(any(Specification.class), any(Sort.class))).thenReturn(repoFilteredUsers);
        List<User> result = userService.findUsersByCriteria(1L);

        //Then
        assertEquals(result, repoFilteredUsers);
        assertEquals(1, result.size());
    }

    @Test
    void givenUserDoesNotExist_WhenDeleteUserById_ThenExceptionisThrown(){
        //Given

        //When
        when(userRepository.findById(any(Long.class))).thenReturn(Optional.empty());

        //Then
        assertThrows(RuntimeException.class, () -> {
            userService.deleteUserById(5L);
        });

        verify(userRepository, never()).delete(any(User.class));
    }

    @Test
    void givenUserExists_WhenDeleteUserById_ThenExceptionisThrown(){
        //Given
        User user1 = new User("John");
        user1.setUser_id(1L);
        //When
        when(userRepository.findById(any(Long.class))).thenReturn(Optional.of(user1));
        var result = userService.deleteUserById(1L);
        //Then
        assertEquals(result, user1);
        verify(userRepository, times(1)).delete(any(User.class));
    }

}
