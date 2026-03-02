package com.example.runtrackerapp.service;

import com.example.runtrackerapp.dto.RunFilter;
import com.example.runtrackerapp.dto.RunResponseDTO;
import com.example.runtrackerapp.dto.UserFilter;
import com.example.runtrackerapp.dto.UserResponseDTO;
import com.example.runtrackerapp.mapper.UserMapper;
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
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

//Acts as a bridge between JUnit 5 and Mockito -> creates mock objects, injects them into test classes, resets mocks between tests
//enables @Mock and @InjectMocks
@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @Mock
    UserRepository userRepository;

    @Mock
    UserMapper userMapper;

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

        Page<User> page = new PageImpl<>(repoUsers);

        Pageable pageable = PageRequest.of(0, 10);

        UserFilter filter = new UserFilter(); //no filters applied > no setters

        //When (findAll is called with any Specification and any Sort, then return this result.)
        //mock repo
        when(userRepository.findAll(
                any(Specification.class),
                any(Pageable.class)
        )).thenReturn(page);
        //mock mapper
        when(userMapper.mapUserToUserResponseDTO(any(User.class)))
                .thenAnswer(inv -> {
                    User user = inv.getArgument(0);

                    UserResponseDTO dto = new UserResponseDTO();
                    dto.setUsername(user.getUsername());

                    return dto;
                });
        Page<UserResponseDTO> result = userService.findUsersByCriteria(filter, pageable);

        //Then
        assertEquals(2, result.getTotalElements());
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
        Page<User> page = new PageImpl<>(repoFilteredUsers);

        Pageable pageable = PageRequest.of(0, 10);

        UserFilter filter = new UserFilter(); //filters applied
        filter.setUserId(1L);

        //When (findAll is called with any Specification and any Sort, then return this result.)
        //mock repo
        when(userRepository.findAll(
                any(Specification.class),
                any(Pageable.class)
        )).thenReturn(page);
        //mock mapper
        when(userMapper.mapUserToUserResponseDTO(any(User.class)))
                .thenAnswer(inv -> {
                    User user = inv.getArgument(0);

                    UserResponseDTO dto = new UserResponseDTO();
                    dto.setUsername(user.getUsername());
                    dto.setUser_id(user.getUser_id());
                    return dto;
                });
        Page<UserResponseDTO> result = userService.findUsersByCriteria(filter, pageable);

        //Then
        assertEquals(1, result.getTotalElements());

        UserResponseDTO dto = result.getContent().getFirst();
        verify(userRepository)
                .findAll(any(Specification.class), eq(pageable));
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
