package com.asset.car_rental.mapper;

import com.asset.car_rental.dto.review.ReviewDto;
import com.asset.car_rental.entity.Review;

public class ReviewMapper {

    private ReviewMapper() {}

    public static ReviewDto toDto(Review r) {
        if (r == null) return null;

        ReviewDto dto = new ReviewDto();
        dto.setId(r.getId());
        dto.setCarId(r.getCar() != null ? r.getCar().getId() : null);
        dto.setUserId(r.getUser() != null ? r.getUser().getId() : null);
        dto.setRating(r.getRating());
        dto.setComment(r.getComment());
        dto.setCreatedAt(r.getCreatedAt());
        return dto;
    }
}
