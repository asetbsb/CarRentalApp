package com.asset.car_rental.dto.booking;

import com.asset.car_rental.model.BookingStatus;
import jakarta.validation.constraints.NotNull;

public class BookingStatusUpdateRequest {

    @NotNull
    private BookingStatus status;

    public BookingStatus getStatus() { return status; }
    public void setStatus(BookingStatus status) { this.status = status; }
}
