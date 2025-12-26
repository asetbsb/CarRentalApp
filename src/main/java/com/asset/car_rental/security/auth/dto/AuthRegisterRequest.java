package com.asset.car_rental.security.auth.dto;

import com.asset.car_rental.entity.User;
import com.asset.car_rental.model.UserRole;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class AuthRegisterRequest {

    @NotBlank
    @Size(max = 100)
    private String username;

    @NotBlank
    @Size(min = 6, max = 255)
    private String password;

    @NotNull
    private UserRole role;

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public UserRole getRole() { return role; }
    public void setRole(UserRole role) { this.role = role; }
}
