package com.asset.car_rental.service;

import com.asset.car_rental.entity.Car;
import com.asset.car_rental.entity.User;

import java.util.List;

public interface CarService {

    Car createCar(Car car, User owner);

    List<Car> getAllAvailableCars();

    Car getById(Long id);

    Car updateCar(Long id, Car updatedCar, User owner);

    void deleteCar(Long id, User owner);
}
