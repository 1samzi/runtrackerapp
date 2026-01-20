package com.example.runtrackerapp.service;

import com.example.runtrackerapp.model.Run;
import com.example.runtrackerapp.model.User;
import com.example.runtrackerapp.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {
    private final UserRepository repo;

    UserService(UserRepository repository){
        this.repo = repository;
    }


    public List<User> findUsers(){
        return repo.findAll();
    }

    //TODO add some criteria for users
    public List<User> findUsersByCriteria(){
        return null;
    }

    public User saveUser(User user){
        user.getRuns().forEach(user::addRun);
        return repo.save(user);
    }

    public List<User> saveUsers(List<User> users){
        for (User user : users){
            user.getRuns().forEach(user::addRun);
        }
        return repo.saveAll(users);
    }


}
