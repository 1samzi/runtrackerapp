package com.example.runtrackerapp.service;

import com.example.runtrackerapp.model.Run;
import java.time.LocalDate;
import java.util.Arrays;

import com.example.runtrackerapp.model.User;
import com.example.runtrackerapp.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

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

    @Test
    void saveUserSetsUserOnRuns(){
        //ARRANGE
        User user = new User("sam");
        Run run1 = new Run(5, 30, LocalDate.now(),3);
        Run run2 = new Run(2, 12, LocalDate.now().minusDays(5), 4);
        user.addRuns(Arrays.asList(run1, run2));
        //ACTION
        //When save is called don't hit the db, just return the same user object that was passed ... simulating JPA save() returning the entity ... get the first argument passed into the method aka "sam" (user == saved)
        when(userRepository.save(any(User.class))).thenAnswer(inv -> inv.getArgument(0));
        User saved = userService.saveUser(user);
        //ASSERTION
        assertEquals("sam", saved.getUsername());
        assertSame(user, run1.getUser());
        assertSame(user, run2.getUser());
        verify(userRepository).save(user);

    }
}
