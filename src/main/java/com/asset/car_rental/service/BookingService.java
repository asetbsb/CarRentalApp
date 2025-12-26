package com.asset.car_rental.service;

import com.asset.car_rental.entity.Booking;
import com.asset.car_rental.entity.User;
import com.asset.car_rental.model.BookingStatus;

import java.time.LocalDate;
import java.util.List;

public interface BookingService {

    Booking createBooking(Long carId, User client, LocalDate startDate, LocalDate endDate);

    Booking getById(Long id);

    Booking changeStatus(Long id, BookingStatus newStatus, User owner);

    List<Booking> getBookingsForUser(User user);
}
