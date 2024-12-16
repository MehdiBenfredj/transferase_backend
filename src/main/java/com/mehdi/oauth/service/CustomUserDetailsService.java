package com.mehdi.oauth.service;

import com.mehdi.oauth.model.User;
import com.mehdi.oauth.repository.CustomUserRepository;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class CustomUserDetailsService {

    private final CustomUserRepository userRepository;

    public CustomUserDetailsService(CustomUserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User loadUserByEmail(String email) throws UsernameNotFoundException {
        User user;
        Optional<User> optionalUser = userRepository.findByEmail(email);

        if (optionalUser.isEmpty()) {
            return null;
        } else {
            user = optionalUser.get();
            return user;
        }
    }

    public User createUser(String email, String username) {
        User newUser = new User();
        newUser.setEmail(email);
        newUser.setUsername(username);
        return userRepository.save(newUser);
    }

}
