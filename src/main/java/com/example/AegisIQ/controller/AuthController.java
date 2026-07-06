package com.example.AegisIQ.controller;

import com.example.AegisIQ.dto.LoginRequest;
import com.example.AegisIQ.entity.User;
import com.example.AegisIQ.service.UserService;
import com.example.AegisIQ.security.JwtUtils;
import com.example.AegisIQ.service.RefreshTokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")
public class AuthController {
    
    @Autowired
    private UserService userService;

    @Autowired
    private JwtUtils jwtUtils;

    @Autowired
    private RefreshTokenService refreshTokenService;
    
    /**
     * Register a new user
     */
    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody User user) {
        try {
            User registeredUser = userService.registerUser(user);
            
            Map<String, Object> response = new HashMap<>();
            response.put("message", "User registered successfully");
            response.put("userId", registeredUser.getUserId());
            response.put("email", registeredUser.getEmail());
            response.put("name", registeredUser.getName());
            response.put("role", registeredUser.getRole());
            
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(Map.of("error", e.getMessage()));
        }
    }
    
    /**
     * Login user
     */
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest) {
        try {
            User user = userService.authenticateUser(loginRequest.getEmail(), loginRequest.getPassword());
            String userId = String.valueOf(user.getUserId());
            String accessToken = jwtUtils.generateAccessToken(userId);
            String refreshToken = jwtUtils.generateRefreshToken(userId);

            // store refresh token server-side for revocation/validation
            refreshTokenService.storeTokenString(refreshToken, userId, jwtUtils.getRefreshExpirationMs());

            Map<String, Object> response = new HashMap<>();
            response.put("message", "Login successful");
            response.put("userId", user.getUserId());
            response.put("email", user.getEmail());
            response.put("name", user.getName());
            response.put("role", user.getRole());
            response.put("accessToken", accessToken);
            response.put("refreshToken", refreshToken);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(Map.of("error", "Invalid credentials"));
        }
    }

    @PostMapping("/refresh")
    public ResponseEntity<?> refresh(@RequestBody Map<String, String> body) {
        String refreshToken = body.get("refreshToken");
        if (refreshToken == null) return ResponseEntity.badRequest().body(Map.of("error", "refreshToken required"));

        String userId = refreshTokenService.validateAndGetUserId(refreshToken);
        if (userId == null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "Invalid or expired refresh token"));

        // optionally validate JWT signature as well
        if (!jwtUtils.validateToken(refreshToken)) {
            refreshTokenService.revoke(refreshToken);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "Invalid refresh token"));
        }

        String newAccess = jwtUtils.generateAccessToken(userId);
        String newRefresh = jwtUtils.generateRefreshToken(userId);
        // rotate refresh token
        refreshTokenService.revoke(refreshToken);
        refreshTokenService.storeTokenString(newRefresh, userId, jwtUtils.getRefreshExpirationMs());

        return ResponseEntity.ok(Map.of("accessToken", newAccess, "refreshToken", newRefresh));
    }
}
