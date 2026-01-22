package com.example.runtrackerapp.controller;

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

    public UserController(UserService userService){
        this.userService = userService;
    }

    @GetMapping
    public List<User> getUsers(
            @RequestParam(required = false) Long userId
    ) {
        return userService.findUsersByCriteria(userId);
    }

    @PostMapping
    public User createUser(@RequestBody User user){
        return userService.saveUser(user);
    }

    @PostMapping("/batch")
    public List<User> createUsers(@RequestBody List<User> users){
        return userService.saveUsers(users);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<User> deleteUser (@PathVariable Long id){
        User deletedUser = userService.deleteUserById(id);
        return ResponseEntity.ok(deletedUser);
    }

}
