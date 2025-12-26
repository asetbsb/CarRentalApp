package com.asset.car_rental.service.impl;

import com.asset.car_rental.entity.Car;
import com.asset.car_rental.entity.User;
import com.asset.car_rental.model.CarStatus;
import com.asset.car_rental.repository.CarRepository;
import com.asset.car_rental.service.CarService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class CarServiceImpl implements CarService {

    private final CarRepository carRepository;

    public CarServiceImpl(CarRepository carRepository) {
        this.carRepository = carRepository;
    }

    @Override
    public Car createCar(Car car, User owner) {
        car.setOwner(owner);
        if (car.getStatus() == null) {
            car.setStatus(CarStatus.AVAILABLE);
        }
        return carRepository.save(car);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Car> getAllAvailableCars() {
        // either custom repo method or filter in memory
        return carRepository.findByStatus(CarStatus.AVAILABLE);
    }

    @Override
    @Transactional(readOnly = true)
    public Car getById(Long id) {
        return carRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Car not found: " + id));
    }

    @Override
    public Car updateCar(Long id, Car updatedCar, User owner) {
        Car existing = getById(id);
        if (!existing.getOwner().getId().equals(owner.getId())) {
            throw new SecurityException("Not allowed to edit this car");
        }

        if (updatedCar.getBrand() != null) existing.setBrand(updatedCar.getBrand());
        if (updatedCar.getModel() != null) existing.setModel(updatedCar.getModel());
        if (updatedCar.getYear() != null) existing.setYear(updatedCar.getYear());
        if (updatedCar.getPricePerDay() != null) existing.setPricePerDay(updatedCar.getPricePerDay());
        if (updatedCar.getStatus() != null) existing.setStatus(updatedCar.getStatus());
        if (updatedCar.getDescription() != null) existing.setDescription(updatedCar.getDescription());

        return carRepository.save(existing);
    }

    @Override
    public void deleteCar(Long id, User owner) {
        Car existing = getById(id);
        if (!existing.getOwner().getId().equals(owner.getId())) {
            throw new SecurityException("Not allowed to delete this car");
        }
        carRepository.delete(existing);
    }
}
