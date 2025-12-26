package com.asset.car_rental.security.auth;

import com.asset.car_rental.entity.User;
import com.asset.car_rental.model.UserRole;
import com.asset.car_rental.repository.UserRepository;
import com.asset.car_rental.security.auth.dto.AuthLoginRequest;
import com.asset.car_rental.security.auth.dto.AuthRegisterRequest;
import com.asset.car_rental.security.auth.dto.AuthResponse;
import com.asset.car_rental.security.jwt.JwtService;
import jakarta.persistence.EntityExistsException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

@Service
@Transactional
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    public AuthService(UserRepository userRepository,
                       PasswordEncoder passwordEncoder,
                       AuthenticationManager authenticationManager,
                       JwtService jwtService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
    }

    public User register(AuthRegisterRequest req) {
        userRepository.findByUsername(req.getUsername())
                .ifPresent(u -> { throw new EntityExistsException("Username already taken"); });

        User user = new User();
        user.setUsername(req.getUsername());
        user.setPassword(passwordEncoder.encode(req.getPassword()));
        user.setRole(req.getRole() != null ? req.getRole() : UserRole.CLIENT);
        user.setActive(true);

        return userRepository.save(user);
    }

    public AuthResponse login(AuthLoginRequest req) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(req.getUsername(), req.getPassword())
        );

        // token claims (optional)
        String token = jwtService.generateToken(req.getUsername(), Map.of());
        return new AuthResponse(token);
    }
}
