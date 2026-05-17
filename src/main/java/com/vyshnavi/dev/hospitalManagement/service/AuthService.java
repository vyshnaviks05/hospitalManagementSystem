package com.vyshnavi.dev.hospitalManagement.service;

import com.vyshnavi.dev.hospitalManagement.dto.AuthRequest;
import com.vyshnavi.dev.hospitalManagement.dto.AuthResponse;
import com.vyshnavi.dev.hospitalManagement.entity.User;
import com.vyshnavi.dev.hospitalManagement.exception.DuplicateResourceException;
import com.vyshnavi.dev.hospitalManagement.repository.UserRepository;
import com.vyshnavi.dev.hospitalManagement.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public String register(AuthRequest request) {

        if (userRepository.findByUsername(request.getUsername()).isPresent()) {

            throw new DuplicateResourceException(
                    "Username already exists"
            );
        }

        User user = new User();

        user.setUsername(request.getUsername());

        user.setPassword(
                passwordEncoder.encode(request.getPassword())
        );

        user.setRole(User.Role.PATIENT);

        userRepository.save(user);

        return "User registered successfully";
    }

    @Transactional(readOnly = true)
    public AuthResponse login(AuthRequest request) {

        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getUsername(),
                        request.getPassword()
                )
        );

        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() ->
                        new UsernameNotFoundException("User not found"));

        String token = jwtUtil.generateToken(
                user.getUsername(),
                user.getRole().name()
        );

        return new AuthResponse(
                token,
                user.getRole().name()
        );
    }
}