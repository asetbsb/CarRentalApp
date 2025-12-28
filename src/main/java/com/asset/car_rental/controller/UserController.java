package com.asset.car_rental.controller;

import com.asset.car_rental.dto.user.UserMeResponse;
import com.asset.car_rental.dto.user.UserUpdateRequest;
import com.asset.car_rental.entity.User;
import com.asset.car_rental.mapper.UserMapper;
import com.asset.car_rental.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/users/me")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    /**
     * GET /users/me
     * Returns info about the currently authenticated user.
     */
    @PreAuthorize("isAuthenticated()")
    @GetMapping
    public ResponseEntity<UserMeResponse> getMe() {
        User currentUser = userService.getCurrentUser();
        UserMeResponse response = UserMapper.toMeResponse(currentUser);
        return ResponseEntity.ok(response);
    }

    /**
     * PUT /users/me
     * Updates current user's username and/or password.
     * Any field left null is not changed.
     */
    @PreAuthorize("isAuthenticated()")
    @PutMapping
    public ResponseEntity<UserMeResponse> updateMe(
            @Valid @RequestBody UserUpdateRequest request
    ) {
        User updated = userService.updateCurrentUser(
                request.getUsername(),
                request.getPassword()
        );
        UserMeResponse response = UserMapper.toMeResponse(updated);
        return ResponseEntity.ok(response);
    }

    /**
     * DELETE /users/me
     * Deactivates the current user's account.
     */
    @PreAuthorize("isAuthenticated()")
    @DeleteMapping
    public ResponseEntity<Void> deleteMe() {
        userService.deactivateCurrentUser();
        return ResponseEntity.noContent().build();
    }
}
