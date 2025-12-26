package com.asset.car_rental.repository;

import com.asset.car_rental.entity.Car;
import com.asset.car_rental.model.CarStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CarRepository extends JpaRepository<Car, Long> {

    List<Car> findByStatus(CarStatus status);

    List<Car> findByOwnerId(Long ownerId);
}
