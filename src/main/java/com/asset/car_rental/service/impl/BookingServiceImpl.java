package com.asset.car_rental.service.impl;

import com.asset.car_rental.entity.Booking;
import com.asset.car_rental.entity.Car;
import com.asset.car_rental.entity.User;
import com.asset.car_rental.model.BookingStatus;
import com.asset.car_rental.repository.BookingRepository;
import com.asset.car_rental.repository.CarRepository;
import com.asset.car_rental.service.BookingService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
@Transactional
public class BookingServiceImpl implements BookingService {

    private final BookingRepository bookingRepository;
    private final CarRepository carRepository;

    public BookingServiceImpl(BookingRepository bookingRepository,
                              CarRepository carRepository) {
        this.bookingRepository = bookingRepository;
        this.carRepository = carRepository;
    }

    @Override
    public Booking createBooking(Long carId, User client,
                                 LocalDate startDate, LocalDate endDate) {
        Car car = carRepository.findById(carId)
                .orElseThrow(() -> new EntityNotFoundException("Car not found: " + carId));

        if (endDate.isBefore(startDate)) {
            throw new IllegalArgumentException("End date must be after start date");
        }

        long days = ChronoUnit.DAYS.between(startDate, endDate);
        if (days <= 0) {
            days = 1; // minimal charge
        }

        BigDecimal total = car.getPricePerDay()
                .multiply(BigDecimal.valueOf(days));

        Booking booking = new Booking();
        booking.setCar(car);
        booking.setUser(client);
        booking.setStartDate(startDate);
        booking.setEndDate(endDate);
        booking.setStatus(BookingStatus.CREATED);
        booking.setTotalPrice(total);

        return bookingRepository.save(booking);
    }

    @Override
    @Transactional(readOnly = true)
    public Booking getById(Long id) {
        return bookingRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Booking not found: " + id));
    }

    @Override
    public Booking changeStatus(Long id, BookingStatus newStatus, User owner) {
        Booking booking = getById(id);
        if (!booking.getCar().getOwner().getId().equals(owner.getId())) {
            throw new SecurityException("Not allowed to change booking status");
        }

        booking.setStatus(newStatus);
        return bookingRepository.save(booking);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Booking> getBookingsForUser(User user) {
        return bookingRepository.findByUserId(user.getId());
    }
}
