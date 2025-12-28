package com.asset.car_rental.mapper;

import com.asset.car_rental.dto.user.UserMeResponse;
import com.asset.car_rental.entity.User;

public final class UserMapper {

    private UserMapper() {
        // utility class
    }

    public static UserMeResponse toMeResponse(User user) {
        if (user == null) {
            return null;
        }

        return new UserMeResponse(
                user.getId(),
                user.getUsername(),
                user.getRole() != null ? user.getRole().name() : null,
                user.isActive(),                       // <- simplified
                user.getCreatedAt(),
                user.getUpdatedAt()
        );
    }
}
