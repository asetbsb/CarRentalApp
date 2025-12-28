package com.asset.car_rental.controller;

import com.asset.car_rental.dto.car.CarDto;
import com.asset.car_rental.dto.user.UserMeResponse;
import com.asset.car_rental.entity.Car;
import com.asset.car_rental.entity.User;
import com.asset.car_rental.mapper.CarMapper;
import com.asset.car_rental.mapper.UserMapper;
import com.asset.car_rental.repository.CarRepository;
import com.asset.car_rental.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/admin")
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    private final UserRepository userRepository;
    private final CarRepository carRepository;

    public AdminController(UserRepository userRepository,
                           CarRepository carRepository) {
        this.userRepository = userRepository;
        this.carRepository = carRepository;
    }

    /**
     * GET /admin/users
     * Returns all users in the system.
     */
    @GetMapping("/users")
    public ResponseEntity<List<UserMeResponse>> getAllUsers() {
        List<User> users = userRepository.findAll();

        List<UserMeResponse> result = users.stream()
                .map(UserMapper::toMeResponse)
                .toList();

        return ResponseEntity.ok(result);
    }

    /**
     * GET /admin/cars
     * Returns all cars (any status, any owner).
     */
    @GetMapping("/cars")
    public ResponseEntity<List<CarDto>> getAllCars() {
        List<Car> cars = carRepository.findAll();

        List<CarDto> result = cars.stream()
                .map(CarMapper::toResponse)
                .toList();

        return ResponseEntity.ok(result);
    }

    /**
     * PATCH /admin/users/{id}/deactivate
     * Deactivates a user by id (sets active = false).
     * This is a "soft ban" – their data stays in DB.
     */
    @PatchMapping("/users/{id}/deactivate")
    public ResponseEntity<Void> deactivateUser(@PathVariable Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("User not found with id: " + id));

        user.setActive(false);
        userRepository.save(user);

        return ResponseEntity.noContent().build();
    }

    /**
     * DELETE /admin/cars/{id}
     * Force-deletes a car by id (ignores who the owner is).
     * This is different from /cars/{id} in CarController,
     * where only the owner can delete their own car.
     */
    @DeleteMapping("/cars/{id}")
    public ResponseEntity<Void> forceDeleteCar(@PathVariable Long id) {
        if (!carRepository.existsById(id)) {
            throw new EntityNotFoundException("Car not found with id: " + id);
        }

        carRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
