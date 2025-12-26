package com.asset.car_rental.repository;

import com.asset.car_rental.entity.Review;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReviewRepository extends JpaRepository<Review, Long> {

    List<Review> findByCarId(Long carId);

    List<Review> findByUserId(Long userId);
}
