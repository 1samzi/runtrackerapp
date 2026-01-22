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
        user.getRuns().forEach(run -> run.setUser(user));
        return repo.save(user);
    }

    public List<User> saveUsers(List<User> users){
        for (User user : users){
            if (user.getRuns() != null){
                user.getRuns().forEach(run -> run.setUser(user));
            }
        }
        return repo.saveAll(users);
    }


}
