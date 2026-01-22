package com.example.runtrackerapp.repository;

import com.example.runtrackerapp.model.User;
import org.springframework.data.jpa.domain.Specification;

public class UserSpecification {
    //Find runs with specific rating
    public static Specification<User> hasId(Long userId) {
        return (root, query, cb) -> cb.equal(root.get("user_id"), userId);
    }
}
