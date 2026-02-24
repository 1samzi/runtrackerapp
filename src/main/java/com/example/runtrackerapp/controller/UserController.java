package com.example.runtrackerapp.controller;

import com.example.runtrackerapp.dto.UserCreateRequestDTO;
import com.example.runtrackerapp.dto.UserResponseDTO;
import com.example.runtrackerapp.mapper.UserMapper;
import com.example.runtrackerapp.model.Run;
import com.example.runtrackerapp.model.User;
import com.example.runtrackerapp.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/users")
public class UserController {
    private final UserService userService;
    private final UserMapper userMapper;

    public UserController(UserService userService, UserMapper userMapper){
        this.userService = userService;
        this.userMapper = userMapper;
    }

    @GetMapping
    public List<UserResponseDTO> getUsers(
            @RequestParam(required = false) Long userId
    ) {
        return userService.findUsersByCriteria(userId);
    }

    @PostMapping
    public User createUser(@RequestBody UserCreateRequestDTO user){
        return userService.saveUser(user);
    }

    @PostMapping("/batch")
    public List<User> createUsers(@RequestBody List<UserCreateRequestDTO> users){
        return userService.saveUsers(users);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<UserResponseDTO> deleteUser (@PathVariable Long id){
        User deletedUser = userService.deleteUserById(id);
        UserResponseDTO dto = userMapper.mapUserToUserResponseDTO(deletedUser);
        return ResponseEntity.ok(dto);
    }

}
