package com.example.runtrackerapp.service;

import com.example.runtrackerapp.model.Run;
import com.example.runtrackerapp.model.User;
import com.example.runtrackerapp.repository.UserRepository;
import com.example.runtrackerapp.repository.UserSpecification;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {
    private final UserRepository repo;

    UserService(UserRepository repository){
        this.repo = repository;
    }

    public List<User> findUsersByCriteria(Long userId){
        //init specification to TRUE
        Specification<User> spec = (root, query, cb) -> cb.conjunction();

        if (userId != null){
            spec = spec.and(UserSpecification.hasId(userId));
        }

        return repo.findAll(
                spec,
                Sort.by(Sort.Direction.DESC, "createdAt"));
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

    public User deleteUserById(Long id){
        User user = repo.findById(id)
                .orElseThrow(() -> new RuntimeException("Run not found"));

        repo.delete(user);
        return user;
    }

}
