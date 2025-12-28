package com.asset.car_rental.integration;

import com.asset.car_rental.dto.booking.BookingCreateRequest;
import com.asset.car_rental.entity.Car;
import com.asset.car_rental.entity.User;
import com.asset.car_rental.model.CarStatus;
import com.asset.car_rental.model.UserRole;
import com.asset.car_rental.repository.CarRepository;
import com.asset.car_rental.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@AutoConfigureMockMvc
@Transactional
public class BookingControllerIntegrationTest extends AbstractIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CarRepository carRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Test
    @WithMockUser(username = "client-http", roles = {"CLIENT"})
    void createBooking_returns409Conflict_whenDatesOverlap() throws Exception {
        // --- Arrange DB: owner ---
        User owner = new User();
        owner.setUsername("owner-http");
        owner.setPassword(passwordEncoder.encode("password"));
        owner.setRole(UserRole.OWNER);
        owner.setActive(true);
        owner = userRepository.save(owner);

        // --- Arrange DB: client (must exist because controller calls userService.getCurrentUser()) ---
        User client = new User();
        client.setUsername("client-http"); // must match @WithMockUser username
        client.setPassword(passwordEncoder.encode("password"));
        client.setRole(UserRole.CLIENT);
        client.setActive(true);
        client = userRepository.save(client);

        // --- Arrange DB: car ---
        Car car = new Car();
        car.setOwner(owner);
        car.setBrand("Toyota");
        car.setModel("Camry");
        car.setYear(2021);
        car.setPricePerDay(BigDecimal.valueOf(100));
        car.setStatus(CarStatus.AVAILABLE);
        car = carRepository.save(car);

        // Dates must be FutureOrPresent (DTO validation)
        LocalDate startA = LocalDate.now().plusDays(1);
        LocalDate endA = startA.plusDays(3); // [startA, endA)

        BookingCreateRequest reqA = new BookingCreateRequest();
        reqA.setCarId(car.getId());
        reqA.setStartDate(startA);
        reqA.setEndDate(endA);

        // --- Act + Assert 1: first booking -> 201 ---
        mockMvc.perform(post("/bookings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(reqA)))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", org.hamcrest.Matchers.containsString("/bookings/")))
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.status").value("CREATED"))
                .andExpect(jsonPath("$.carId").value(car.getId().intValue()))
                .andExpect(jsonPath("$.startDate").value(startA.toString()))
                .andExpect(jsonPath("$.endDate").value(endA.toString()));

        // Overlapping booking B: overlaps [startA, endA)
        LocalDate startB = startA.plusDays(2);
        LocalDate endB = startB.plusDays(3);

        BookingCreateRequest reqB = new BookingCreateRequest();
        reqB.setCarId(car.getId());
        reqB.setStartDate(startB);
        reqB.setEndDate(endB);

        // --- Act + Assert 2: overlap -> 409 ---
        mockMvc.perform(post("/bookings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(reqB)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.code").value("CONFLICT"))
                .andExpect(jsonPath("$.message", org.hamcrest.Matchers.containsString("already booked")));
    }
}