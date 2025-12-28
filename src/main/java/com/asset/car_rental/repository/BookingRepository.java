package com.asset.car_rental.repository;

import com.asset.car_rental.entity.Booking;
import com.asset.car_rental.model.BookingStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {

    List<Booking> findByUserId(Long userId);

    List<Booking> findByCarId(Long carId);

    List<Booking> findByStatus(BookingStatus status);

    /**
     * Overlap check for half-open intervals: [startDate, endDate)
     * Overlap exists iff:
     *   existing.start < requested.end AND existing.end > requested.start
     *
     * We only consider "active" statuses (e.g. CREATED, APPROVED).
     */
    boolean existsByCarIdAndStatusInAndStartDateLessThanAndEndDateGreaterThan(
            Long carId,
            Collection<BookingStatus> statuses,
            LocalDate endDate,
            LocalDate startDate
    );
}
