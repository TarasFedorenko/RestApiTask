package com.clearsolution.restfulapitask.service;

import com.clearsolution.restfulapitask.model.User;

import java.time.LocalDate;
import java.util.List;

public interface UserService {
    void createUser(User user);

    User updateUser(Long userId, User user);

    User updateUsersFields(Long userId, User user);

    void deleteUser(Long userId);

    List<User> findUsersByBirthdayRange(LocalDate from, LocalDate to);
}