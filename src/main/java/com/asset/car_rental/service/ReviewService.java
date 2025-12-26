package com.asset.car_rental.service;

import com.asset.car_rental.entity.Review;
import com.asset.car_rental.entity.User;

import java.util.List;

public interface ReviewService {

    Review addReview(Long carId, User user, int rating, String comment);

    List<Review> getReviewsForCar(Long carId);
}
