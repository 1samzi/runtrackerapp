package com.example.runtrackerapp.service;

import com.example.runtrackerapp.dto.UserCreateRequestDTO;
import com.example.runtrackerapp.dto.UserResponseDTO;
import com.example.runtrackerapp.exception.ResourceNotFoundException;
import com.example.runtrackerapp.mapper.UserMapper;
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
    private final UserMapper userMapper;

    public UserService(UserRepository repository, UserMapper userMapper){
        this.repo = repository;
        this.userMapper = userMapper;
    }

    public List<UserResponseDTO> findUsersByCriteria(Long userId){
        //init specification to TRUE
        Specification<User> spec = (root, query, cb) -> cb.conjunction();

        if (userId != null){
            spec = spec.and(UserSpecification.hasId(userId));
        }

        List<User> users = repo.findAll(
                spec,
                Sort.by(Sort.Direction.DESC, "createdAt"));
        return users.stream().map(userMapper::mapUserToUserResponseDTO).toList();
    }

    public User saveUser(UserCreateRequestDTO dto){
        User user = userMapper.mapUserRequestDTOToUser(dto);
        return repo.save(user);
    }

    public List<User> saveUsers(List<UserCreateRequestDTO> dtos){
        List<User> users = dtos.stream()
                .map(userMapper::mapUserRequestDTOToUser)
                .toList();
        return repo.saveAll(users);
    }

    public User deleteUserById(Long id){
        User userToDelete = repo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found (id): " + id));

        repo.deleteById(id);

        return userToDelete;
    }

}
