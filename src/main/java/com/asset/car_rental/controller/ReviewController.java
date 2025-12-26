package com.asset.car_rental.controller;

import com.asset.car_rental.dto.review.ReviewCreateRequest;
import com.asset.car_rental.dto.review.ReviewDto;
import com.asset.car_rental.entity.Review;
import com.asset.car_rental.entity.User;
import com.asset.car_rental.mapper.ReviewMapper;
import com.asset.car_rental.service.ReviewService;
import com.asset.car_rental.service.UserService;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/cars/{carId}/reviews")
public class ReviewController {

    private final ReviewService reviewService;
    private final UserService userService;

    public ReviewController(ReviewService reviewService, UserService userService) {
        this.reviewService = reviewService;
        this.userService = userService;
    }

    // POST /cars/{carId}/reviews — CLIENT
    @PreAuthorize("hasRole('CLIENT') or hasRole('ADMIN')")
    @PostMapping
    public ReviewDto addReview(@PathVariable Long carId,
                               @RequestBody @Valid ReviewCreateRequest request) {
        User user = userService.getCurrentUser();
        Review created = reviewService.addReview(carId, user, request.getRating(), request.getComment());
        return ReviewMapper.toDto(created);
    }

    // GET /cars/{carId}/reviews — public
    @GetMapping
    public List<ReviewDto> getReviews(@PathVariable Long carId) {
        return reviewService.getReviewsForCar(carId).stream()
                .map(ReviewMapper::toDto)
                .toList();
    }
}
