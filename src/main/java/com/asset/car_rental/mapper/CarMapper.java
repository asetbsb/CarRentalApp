package com.asset.car_rental.mapper;

import com.asset.car_rental.dto.car.CarCreateRequest;
import com.asset.car_rental.dto.car.CarDto;
import com.asset.car_rental.dto.car.CarUpdateRequest;
import com.asset.car_rental.entity.Car;

public final class CarMapper {

    private CarMapper() {}

    public static CarDto toResponse(Car car) {
        if (car == null) return null;

        CarDto dto = new CarDto();
        dto.setId(car.getId());
        dto.setOwnerId(car.getOwner() != null ? car.getOwner().getId() : null);

        dto.setBrand(car.getBrand());
        dto.setModel(car.getModel());
        dto.setYear(car.getYear());

        dto.setPricePerDay(car.getPricePerDay());
        dto.setStatus(car.getStatus());

        dto.setDescription(car.getDescription());
        dto.setCreatedAt(car.getCreatedAt());
        dto.setUpdatedAt(car.getUpdatedAt());
        return dto;
    }

    public static Car fromCreateRequest(CarCreateRequest req) {
        Car car = new Car();
        car.setBrand(req.getBrand());
        car.setModel(req.getModel());
        car.setYear(req.getYear());
        car.setPricePerDay(req.getPricePerDay());
        car.setDescription(req.getDescription());
        // status will be defaulted in service if null
        return car;
    }

    /**
     * Applies partial update semantics: only non-null fields overwrite.
     */
    public static void applyUpdate(Car target, CarUpdateRequest req) {
        if (req.getBrand() != null) target.setBrand(req.getBrand());
        if (req.getModel() != null) target.setModel(req.getModel());
        if (req.getYear() != null) target.setYear(req.getYear());
        if (req.getPricePerDay() != null) target.setPricePerDay(req.getPricePerDay());
        if (req.getDescription() != null) target.setDescription(req.getDescription());
        if (req.getStatus() != null) target.setStatus(req.getStatus());
    }
}
