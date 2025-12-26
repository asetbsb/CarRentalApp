package com.asset.car_rental.service.impl;

import com.asset.car_rental.entity.Car;
import com.asset.car_rental.entity.Review;
import com.asset.car_rental.entity.User;
import com.asset.car_rental.repository.CarRepository;
import com.asset.car_rental.repository.ReviewRepository;
import com.asset.car_rental.service.ReviewService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional
public class ReviewServiceImpl implements ReviewService {

    private final ReviewRepository reviewRepository;
    private final CarRepository carRepository;

    public ReviewServiceImpl(ReviewRepository reviewRepository,
                             CarRepository carRepository) {
        this.reviewRepository = reviewRepository;
        this.carRepository = carRepository;
    }

    @Override
    public Review addReview(Long carId, User user, int rating, String comment) {
        if (rating < 1 || rating > 5) {
            throw new IllegalArgumentException("Rating must be between 1 and 5");
        }

        Car car = carRepository.findById(carId)
                .orElseThrow(() -> new EntityNotFoundException("Car not found: " + carId));

        Review review = new Review();
        review.setCar(car);
        review.setUser(user);
        review.setRating(rating);
        review.setComment(comment);
        review.setCreatedAt(LocalDateTime.now());

        return reviewRepository.save(review);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Review> getReviewsForCar(Long carId) {
        return reviewRepository.findByCarId(carId);
    }
}
