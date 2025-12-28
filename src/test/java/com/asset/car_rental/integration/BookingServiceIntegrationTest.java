package com.asset.car_rental.integration;

import com.asset.car_rental.entity.Booking;
import com.asset.car_rental.entity.Car;
import com.asset.car_rental.entity.User;
import com.asset.car_rental.model.BookingStatus;
import com.asset.car_rental.model.CarStatus;
import com.asset.car_rental.model.UserRole;
import com.asset.car_rental.repository.BookingRepository;
import com.asset.car_rental.repository.CarRepository;
import com.asset.car_rental.repository.UserRepository;
import com.asset.car_rental.service.BookingService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@Transactional
public class BookingServiceIntegrationTest extends AbstractIntegrationTest {

    @Autowired
    private BookingService bookingService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CarRepository carRepository;

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Test
    void createBooking_persistsBookingWithCorrectPriceAndStatus() {
        // Arrange: create client user
        User client = new User();
        client.setUsername("test-client");
        client.setPassword(passwordEncoder.encode("password"));
        client.setRole(UserRole.CLIENT);
        client.setActive(true);
        client.setCreatedAt(LocalDateTime.now());
        client.setUpdatedAt(LocalDateTime.now());
        final User savedClient = userRepository.save(client);

        // Arrange: create owner user
        User owner = new User();
        owner.setUsername("test-owner");
        owner.setPassword(passwordEncoder.encode("password"));
        owner.setRole(UserRole.OWNER);
        owner.setActive(true);
        owner.setCreatedAt(LocalDateTime.now());
        owner.setUpdatedAt(LocalDateTime.now());
        final User savedOwner = userRepository.save(owner);

        // Arrange: create car
        Car car = new Car();
        car.setBrand("Toyota");
        car.setModel("Corolla");
        car.setYear(2020);
        car.setPricePerDay(BigDecimal.valueOf(100)); // 100 per day
        car.setStatus(CarStatus.AVAILABLE);
        car.setOwner(savedOwner);
        car.setCreatedAt(LocalDateTime.now());
        car.setUpdatedAt(LocalDateTime.now());
        final Car savedCar = carRepository.save(car);

        LocalDate start = LocalDate.of(2025, 1, 10);
        LocalDate end = start.plusDays(3); // 3 days

        // Act
        Booking booking = bookingService.createBooking(
                savedCar.getId(),
                savedClient,
                start,
                end
        );

        // Assert: booking persisted
        Booking fromDb = bookingRepository.findById(booking.getId())
                .orElseThrow(() -> new IllegalStateException("Booking not found in DB"));

        assertThat(fromDb.getUser().getId()).isEqualTo(savedClient.getId());
        assertThat(fromDb.getCar().getId()).isEqualTo(savedCar.getId());
        assertThat(fromDb.getStatus()).isEqualTo(BookingStatus.CREATED);

        // Price: 100 * 3 = 300
        assertThat(fromDb.getTotalPrice()).isEqualByComparingTo(BigDecimal.valueOf(300));
    }

    @Test
    void createBooking_throwsConflict_whenDatesOverlapActiveBooking() {
        // Arrange: create client user
        User client = new User();
        client.setUsername("client-overlap");
        client.setPassword(passwordEncoder.encode("password"));
        client.setRole(UserRole.CLIENT);
        client.setActive(true);
        client.setCreatedAt(LocalDateTime.now());
        client.setUpdatedAt(LocalDateTime.now());
        final User savedClient = userRepository.save(client);

        // Arrange: create owner user
        User owner = new User();
        owner.setUsername("owner-overlap");
        owner.setPassword(passwordEncoder.encode("password"));
        owner.setRole(UserRole.OWNER);
        owner.setActive(true);
        owner.setCreatedAt(LocalDateTime.now());
        owner.setUpdatedAt(LocalDateTime.now());
        final User savedOwner = userRepository.save(owner);

        // Arrange: create car
        Car car = new Car();
        car.setBrand("BMW");
        car.setModel("X5");
        car.setYear(2022);
        car.setPricePerDay(BigDecimal.valueOf(150));
        car.setStatus(CarStatus.AVAILABLE);
        car.setOwner(savedOwner);
        car.setCreatedAt(LocalDateTime.now());
        car.setUpdatedAt(LocalDateTime.now());
        final Car savedCar = carRepository.save(car);

        // Booking A: [Jan 10, Jan 13)
        LocalDate aStart = LocalDate.of(2025, 1, 10);
        LocalDate aEnd = LocalDate.of(2025, 1, 13);

        bookingService.createBooking(savedCar.getId(), savedClient, aStart, aEnd);
        bookingRepository.flush(); // make sure it's visible before the overlap query

        // Booking B overlaps: [Jan 12, Jan 15) overlaps [Jan 10, Jan 13)
        LocalDate bStart = LocalDate.of(2025, 1, 12);
        LocalDate bEnd = LocalDate.of(2025, 1, 15);

        assertThatThrownBy(() -> bookingService.createBooking(savedCar.getId(), savedClient, bStart, bEnd))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("already booked");

        // Adjacent booking should be allowed: [Jan 13, Jan 15) does NOT overlap [Jan 10, Jan 13)
        LocalDate cStart = LocalDate.of(2025, 1, 13);
        LocalDate cEnd = LocalDate.of(2025, 1, 15);

        Booking ok = bookingService.createBooking(savedCar.getId(), savedClient, cStart, cEnd);
        assertThat(ok.getId()).isNotNull();
    }
}
