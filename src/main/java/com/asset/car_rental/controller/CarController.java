package com.asset.car_rental.controller;

import com.asset.car_rental.dto.car.CarCreateRequest;
import com.asset.car_rental.dto.car.CarDto;
import com.asset.car_rental.dto.car.CarUpdateRequest;
import com.asset.car_rental.entity.Car;
import com.asset.car_rental.entity.User;
import com.asset.car_rental.mapper.CarMapper;
import com.asset.car_rental.service.CarService;
import com.asset.car_rental.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/cars")
public class CarController {

    private final CarService carService;
    private final UserService userService;

    public CarController(CarService carService, UserService userService) {
        this.carService = carService;
        this.userService = userService;
    }

    // POST /cars (OWNER)
    @PreAuthorize("hasRole('OWNER')")
    @PostMapping
    public ResponseEntity<CarDto> create(@RequestBody @Valid CarCreateRequest request) {
        User owner = userService.getCurrentUser();

        Car car = CarMapper.fromCreateRequest(request);
        Car created = carService.createCar(car, owner);

        CarDto body = CarMapper.toResponse(created);
        return ResponseEntity.created(URI.create("/cars/" + body.getId())).body(body);
    }

    // GET /cars (available) - public
    @PreAuthorize("permitAll()")
    @GetMapping
    public List<CarDto> getAvailable() {
        return carService.getAllAvailableCars()
                .stream()
                .map(CarMapper::toResponse)
                .toList();
    }

    // GET /cars/{id} - public
    @PreAuthorize("permitAll()")
    @GetMapping("/{id}")
    public CarDto getById(@PathVariable Long id) {
        return CarMapper.toResponse(carService.getById(id));
    }

    // PUT /cars/{id} (OWNER)
    @PreAuthorize("hasRole('OWNER')")
    @PutMapping("/{id}")
    public CarDto update(@PathVariable Long id, @RequestBody @Valid CarUpdateRequest request) {
        User owner = userService.getCurrentUser();

        Car updatedCar = new Car();
        CarMapper.applyUpdate(updatedCar, request);

        Car updated = carService.updateCar(id, updatedCar, owner);
        return CarMapper.toResponse(updated);
    }

    // DELETE /cars/{id} (OWNER)
    @PreAuthorize("hasRole('OWNER')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        User owner = userService.getCurrentUser();
        carService.deleteCar(id, owner);
        return ResponseEntity.noContent().build();
    }
}
