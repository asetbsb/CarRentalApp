package com.asset.car_rental.repository;

import com.asset.car_rental.entity.Booking;
import com.asset.car_rental.model.BookingStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {

    List<Booking> findByUserId(Long userId);

    List<Booking> findByCarId(Long carId);

    List<Booking> findByStatus(BookingStatus status);
}
