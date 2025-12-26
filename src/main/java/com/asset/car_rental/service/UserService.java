package com.asset.car_rental.service;

import com.asset.car_rental.entity.User;

public interface UserService {

    User getCurrentUser();

    User updateCurrentUser(String newUsername, String newPassword);

    void deactivateCurrentUser();
}
