package com.asset.car_rental.controller;

import com.asset.car_rental.dto.booking.BookingCreateRequest;
import com.asset.car_rental.dto.booking.BookingDto;
import com.asset.car_rental.dto.booking.BookingStatusUpdateRequest;
import com.asset.car_rental.entity.Booking;
import com.asset.car_rental.entity.User;
import com.asset.car_rental.mapper.BookingMapper;
import com.asset.car_rental.service.BookingService;
import com.asset.car_rental.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping
public class BookingController {

    private final BookingService bookingService;
    private final UserService userService;

    public BookingController(BookingService bookingService, UserService userService) {
        this.bookingService = bookingService;
        this.userService = userService;
    }

    // POST /bookings — CLIENT creates booking
    @PreAuthorize("hasRole('CLIENT')")
    @PostMapping("/bookings")
    public ResponseEntity<BookingDto> createBooking(@RequestBody @Valid BookingCreateRequest request) {
        User client = userService.getCurrentUser();

        Booking created = bookingService.createBooking(
                request.getCarId(),
                client,
                request.getStartDate(),
                request.getEndDate()
        );

        BookingDto body = BookingMapper.toDto(created);
        return ResponseEntity
                .created(URI.create("/bookings/" + body.getId()))
                .body(body);
    }

    // GET /bookings/{id} — authenticated users (simple rule for now)
    @PreAuthorize("isAuthenticated()")
    @GetMapping("/bookings/{id}")
    public BookingDto getBookingById(@PathVariable Long id) {
        return BookingMapper.toDto(bookingService.getById(id));
    }

    // PUT /bookings/{id}/status — OWNER changes status
    @PreAuthorize("hasRole('OWNER')")
    @PutMapping("/bookings/{id}/status")
    public BookingDto changeStatus(@PathVariable Long id,
                                   @RequestBody @Valid BookingStatusUpdateRequest request) {
        User owner = userService.getCurrentUser();
        return BookingMapper.toDto(bookingService.changeStatus(id, request.getStatus(), owner));
    }

    // GET /users/me/bookings — authenticated users
    @PreAuthorize("isAuthenticated()")
    @GetMapping("/users/me/bookings")
    public List<BookingDto> getMyBookings() {
        User user = userService.getCurrentUser();
        return bookingService.getBookingsForUser(user)
                .stream()
                .map(BookingMapper::toDto)
                .toList();
    }
}
