package com.mehdi.oauth.repository;

import com.mehdi.oauth.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CustomUserRepository extends JpaRepository<User, String> {
    //Create
    User save(User user);
    //Read
    Optional<User> findByEmail(String email);
    Optional<User> findByUsername(String username);
    //Delete
    void delete(User user);
}
