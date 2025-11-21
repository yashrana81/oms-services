package com.oms.order.controller;

import com.oms.order.dto.LoginRequest;
import com.oms.order.dto.LoginResponse;
import com.oms.order.dto.SignupRequest;
import com.oms.order.entity.User;
import com.oms.order.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;

    // Register a new user and return authentication token
    @PostMapping("/signup")
    public ResponseEntity<LoginResponse> signup(@RequestBody SignupRequest request) {
        User user = userService.signup(request.getName(), request.getMobile(), request.getPassword());
        
        LoginResponse response = LoginResponse.builder()
                .userId(user.getId())
                .name(user.getName())
                .token(user.getToken())
                .build();
        
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest request) {
        User user = userService.login(request.getMobile(), request.getPassword());
        
        LoginResponse response = LoginResponse.builder()
                .userId(user.getId())
                .name(user.getName())
                .token(user.getToken())
                .build();
        
        return ResponseEntity.ok(response);
    }
}