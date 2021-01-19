package com.mps.project.api.service;

import com.mps.project.api.model.User;

import java.util.List;
import java.util.Optional;

public interface UserService {
    void save(User user);
    Optional<User> findByUsername(String username);
    List<User> findAll();
    User updateUser(User user);
    Optional<User> findById(Long id);
}
