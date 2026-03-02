package com.example.runtrackerapp.controller;

import com.example.runtrackerapp.dto.UserCreateRequestDTO;
import com.example.runtrackerapp.dto.UserFilter;
import com.example.runtrackerapp.dto.UserResponseDTO;
import com.example.runtrackerapp.dto.UserUpdateRequestDTO;
import com.example.runtrackerapp.mapper.UserMapper;
import com.example.runtrackerapp.model.Run;
import com.example.runtrackerapp.model.User;
import com.example.runtrackerapp.service.UserService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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
    public Page<UserResponseDTO> getUsers(
            UserFilter filter,
            Pageable pageable
    ) {
        return userService.findUsersByCriteria(filter, pageable);
    }

    @PostMapping
    public User createUser(
            @RequestBody UserCreateRequestDTO user){
        return userService.saveUser(user);
    }

    @PostMapping("/batch")
    public List<User> createUsers(
            @RequestBody List<UserCreateRequestDTO> users){
        return userService.saveUsers(users);
    }

    @PutMapping("/{id}")
    public ResponseEntity<UserResponseDTO> updateUser(
            @PathVariable Long id,
            @RequestBody UserUpdateRequestDTO dto
    ){
        User updatedUser = userService.updateUser(id, dto);

        UserResponseDTO response = userMapper.mapUserToUserResponseDTO(updatedUser);

        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<UserResponseDTO> patchUser(
            @PathVariable Long id,
            @RequestBody UserUpdateRequestDTO dto
    ){
        User updatedUser = userService.patchUser(id, dto);

        UserResponseDTO response = userMapper.mapUserToUserResponseDTO(updatedUser);

        return ResponseEntity.ok(response);

    }

    @DeleteMapping("/{id}")
    public ResponseEntity<UserResponseDTO> deleteUser (
            @PathVariable Long id){
        User deletedUser = userService.deleteUserById(id);
        UserResponseDTO response = userMapper.mapUserToUserResponseDTO(deletedUser);
        return ResponseEntity.ok(response);
    }

}
