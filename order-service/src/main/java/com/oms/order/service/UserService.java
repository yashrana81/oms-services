package com.oms.order.service;

import com.oms.order.entity.User;
import com.oms.order.exception.OrderServiceException;
import com.oms.order.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    // Authenticates user and generates new active token
    @Transactional
    public User login(String mobile, String password) {
        User user = userRepository.findByMobile(mobile)
                .orElseThrow(() -> new OrderServiceException("Invalid mobile number or password", HttpStatus.UNAUTHORIZED));
        
        if (!user.getPassword().equals(password)) {
            throw new OrderServiceException("Invalid password", HttpStatus.UNAUTHORIZED);
        }

        String token = UUID.randomUUID().toString();
        user.setToken(token);
        user.setTokenActive(true);
        user.setUpdatedAt(LocalDateTime.now());
        userRepository.save(user);

        return user;
    }

    // Validates token and returns user ID
    public Long validateToken(String token) {
        if (token == null || token.isBlank()) {
            return null;
        }
        return userRepository.findByToken(token)
                .filter(User::getTokenActive)
                .map(User::getId)
                .orElse(null);
    }

    // Deactivates user token
    @Transactional
    public void logout(String token) {
        User user = userRepository.findByToken(token)
                .orElseThrow(() -> new OrderServiceException("Invalid token", HttpStatus.UNAUTHORIZED));
        
        user.setTokenActive(false);
        user.setUpdatedAt(LocalDateTime.now());
        userRepository.save(user);
    }

    // Creates new user account with initial active authentication token
    @Transactional
    public User signup(String name, String mobile, String password) {
        Optional<User> existingUser = userRepository.findByMobile(mobile);
        
        if (existingUser.isPresent()) {
            throw new OrderServiceException("Mobile number already registered", HttpStatus.BAD_REQUEST);
        }

        User user = User.builder()
                .name(name)
                .mobile(mobile)
                .password(password)
                .token(UUID.randomUUID().toString())
                .tokenActive(true)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        return userRepository.save(user);
    }
}