package com.clearsolution.restfulapitask.service.impl;

import com.clearsolution.restfulapitask.exception.UserAgeUnacceptableException;
import com.clearsolution.restfulapitask.exception.UserNotFoundException;
import com.clearsolution.restfulapitask.exception.WrongDateRangeException;
import com.clearsolution.restfulapitask.model.User;
import com.clearsolution.restfulapitask.repository.UserRepository;
import com.clearsolution.restfulapitask.service.UserService;
import com.clearsolution.restfulapitask.validation.AgeValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final AgeValidator ageValidator;
    private static final Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);

    public UserServiceImpl(UserRepository userRepository, AgeValidator ageValidator) {
        this.userRepository = userRepository;
        this.ageValidator = ageValidator;
    }

    @Override
    public void createUser(User user) {
        if (ageValidator.isValidAge(user.getBirthday())) {
            userRepository.save(user);
            logger.info("User created: {}", user);
        } else {
            logger.error("User creation failed due to unacceptable age: {}", user);
            throw new UserAgeUnacceptableException("User age is less than the acceptable limit ");
        }
    }

    @Override
    public User updateUser(Long userId, User user) {
        User currentUser = userRepository.findById(userId).orElseThrow(() -> {
            logger.error("User update failed: User not found with ID {}", userId);
            return new UserNotFoundException("User not found");
        });

        currentUser.setEmail(user.getEmail());
        currentUser.setFirstName(user.getFirstName());
        currentUser.setLastName(user.getLastName());
        currentUser.setBirthday(user.getBirthday());
        currentUser.setAddress(user.getAddress());
        currentUser.setPhoneNumber(user.getPhoneNumber());
        User updatedUser = userRepository.save(currentUser);
        logger.info("User updated: {}", updatedUser);
        return updatedUser;
    }

    @Override
    public User updateUsersFields(Long userId, User user) {
        User currentUser = userRepository.findById(userId).orElseThrow(() -> {
            logger.error("User update failed: User not found with ID {}", userId);
            return new UserNotFoundException("User not found");
        });
        currentUser.setId(userId);
        if (user.getEmail() != null) {
            currentUser.setEmail(user.getEmail());
        }
        if (user.getFirstName() != null) {
            currentUser.setFirstName(user.getFirstName());
        }
        if (user.getLastName() != null) {
            currentUser.setLastName(user.getLastName());
        }
        if (user.getBirthday() != null) {
            currentUser.setBirthday(user.getBirthday());
        }
        if (user.getAddress() != null) {
            currentUser.setAddress(user.getAddress());
        }
        if (user.getPhoneNumber() != null) {
            currentUser.setPhoneNumber(user.getPhoneNumber());
        }
        User updatedUser = userRepository.save(currentUser);
        logger.info("User fields updated: {}", updatedUser);
        return updatedUser;
    }

    @Override
    public void deleteUser(Long userId) {
        User currentUser = userRepository.findById(userId).orElseThrow(() -> {
            logger.error("User deletion failed: User not found with ID {}", userId);
            return new UserNotFoundException("User not found");
        });
        userRepository.delete(currentUser);
        logger.info("User deleted: {}", currentUser);
    }

    @Override
    public List<User> findUsersByBirthdayRange(LocalDate from, LocalDate to) {
        if (from.isAfter(to)) {
            logger.error("Search failed: Wrong date range provided - from: {}, to: {}", from, to);
            throw new WrongDateRangeException("Wrong range of birth date");
        }
        List<User> users = userRepository.findUsersByBirthDateRange(from, to);
        logger.info("Users found by birthday range: from {} to {}: {}", from, to, users);
        return users;
    }
}