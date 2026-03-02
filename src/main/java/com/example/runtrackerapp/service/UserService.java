package com.example.runtrackerapp.service;

import com.example.runtrackerapp.dto.UserCreateRequestDTO;
import com.example.runtrackerapp.dto.UserFilter;
import com.example.runtrackerapp.dto.UserResponseDTO;
import com.example.runtrackerapp.dto.UserUpdateRequestDTO;
import com.example.runtrackerapp.exception.ResourceNotFoundException;
import com.example.runtrackerapp.mapper.UserMapper;
import com.example.runtrackerapp.model.User;
import com.example.runtrackerapp.repository.UserRepository;
import com.example.runtrackerapp.repository.UserSpecification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Pageable;
import java.util.List;

@Service
public class UserService {
    private final UserRepository repo;
    private final UserMapper userMapper;

    public UserService(UserRepository repository, UserMapper userMapper){
        this.repo = repository;
        this.userMapper = userMapper;
    }

    public Page<UserResponseDTO> findUsersByCriteria(UserFilter filter, Pageable pageable){
        //init specification to TRUE
        Specification<User> spec = (root, query, cb) -> cb.conjunction();

        if (filter.getUserId() != null){
            spec = spec.and(UserSpecification.hasId(filter.getUserId()));
        }
        if (filter.getUsername() != null){
            spec = spec.and(UserSpecification.hasUsername(filter.getUsername()));
        }

        return repo.findAll(spec, pageable).map(userMapper::mapUserToUserResponseDTO);
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

    public User updateUser(Long id, UserUpdateRequestDTO dto){
        User user = repo.findById(id)
                .orElseThrow(()-> new ResourceNotFoundException("User not found (id): " + id));

        user.setUsername(dto.getUsername());

        return repo.save(user);
    }

    public User patchUser(Long id, UserUpdateRequestDTO dto){
        User user = repo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found (id): " + id));

        if (dto.getUsername() != null){
            user.setUsername(dto.getUsername());
        }

        return repo.save(user);
    }


    public User deleteUserById(Long id){
        User userToDelete = repo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found (id): " + id));

        repo.deleteById(id);

        return userToDelete;
    }

}
