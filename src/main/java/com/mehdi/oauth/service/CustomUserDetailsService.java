package com.mehdi.oauth.service;

import com.mehdi.oauth.model.User;
import com.mehdi.oauth.repository.CustomUserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final CustomUserRepository userRepository;
    private final CustomUserRepository customUserRepository;

    public CustomUserDetailsService(CustomUserRepository userRepository, CustomUserRepository customUserRepository) {
        this.userRepository = userRepository;
        this.customUserRepository = customUserRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        UserDetails userDetails = org.springframework.security.core.userdetails.User
                .withUsername(user.getUsername())
                .build();

        return userDetails;
    }

    public UserDetails loadUserByEmail(String email) throws UsernameNotFoundException {
        User user;
        Optional<User> optionalUserser = userRepository.findByEmail(email);

        if (optionalUserser.isEmpty()) {
            return null;
        } else {
            user = optionalUserser.get();
        }

        //TODO: Implement the logic to load the user by email
        UserDetails userDetails = org.springframework.security.core.userdetails.User
                .withUsername(user.getUsername())
                .build();

        return userDetails;
    }

    public User createUser(String email, String username) {
        User newUser = new User();
        newUser.setEmail(email);
        newUser.setUsername(username);
        return userRepository.save(newUser);
    }

}
