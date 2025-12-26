package com.asset.car_rental.dto.car;

import com.asset.car_rental.model.CarStatus;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

public class CarUpdateRequest {

    @Size(max = 100)
    private String brand;

    @Size(max = 100)
    private String model;

    private Integer year;

    @DecimalMin("0.0")
    private BigDecimal pricePerDay;

    @Size(max = 1000)
    private String description;

    private CarStatus status;

    public String getBrand() { return brand; }
    public void setBrand(String brand) { this.brand = brand; }

    public String getModel() { return model; }
    public void setModel(String model) { this.model = model; }

    public Integer getYear() { return year; }
    public void setYear(Integer year) { this.year = year; }

    public BigDecimal getPricePerDay() { return pricePerDay; }
    public void setPricePerDay(BigDecimal pricePerDay) { this.pricePerDay = pricePerDay; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public CarStatus getStatus() { return status; }
    public void setStatus(CarStatus status) { this.status = status; }
}
