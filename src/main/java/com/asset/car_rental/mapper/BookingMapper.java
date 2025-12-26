package com.asset.car_rental.mapper;

import com.asset.car_rental.dto.booking.BookingDto;
import com.asset.car_rental.entity.Booking;

public class BookingMapper {

    private BookingMapper() {}

    public static BookingDto toDto(Booking booking) {
        if (booking == null) return null;

        BookingDto dto = new BookingDto();
        dto.setId(booking.getId());
        dto.setCarId(booking.getCar() != null ? booking.getCar().getId() : null);
        dto.setUserId(booking.getUser() != null ? booking.getUser().getId() : null);

        dto.setStartDate(booking.getStartDate());
        dto.setEndDate(booking.getEndDate());

        dto.setStatus(booking.getStatus());
        dto.setTotalPrice(booking.getTotalPrice());

        dto.setCreatedAt(booking.getCreatedAt());
        dto.setUpdatedAt(booking.getUpdatedAt());
        return dto;
    }
}
