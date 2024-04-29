package com.clearsolution.restfulapitask.controller;

import com.clearsolution.restfulapitask.data.DataContainer;
import com.clearsolution.restfulapitask.model.User;
import com.clearsolution.restfulapitask.service.UserService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;


import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/v1/api/users")
@Validated
public class UserController {

    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/create")
    public ResponseEntity<DataContainer<User>> create(@Valid @RequestBody User user) {
        logger.info("Creating user: {}", user);
        userService.createUser(user);
        return ResponseEntity.status(HttpStatus.CREATED).body(new DataContainer<>(user));
    }

    @PutMapping("/{id}")
    public ResponseEntity<DataContainer<User>> update(@PathVariable Long id, @Valid @RequestBody User user) {
        logger.info("Updating user with ID {}", id);
        User updatedUser = userService.updateUser(id, user);
        return ResponseEntity.ok(new DataContainer<>(updatedUser));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<DataContainer<User>> updateFields(@PathVariable Long id, @RequestBody User user) {
        logger.info("Partial updating user with ID {}", id);
        User updatedUser = userService.updateUsersFields(id, user);
        return ResponseEntity.ok(new DataContainer<>(updatedUser));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        logger.info("Deleting user with ID {}", id);
        userService.deleteUser(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/search")
    public ResponseEntity<DataContainer<List<User>>> searchByBirthdayRange(@RequestParam LocalDate from, @RequestParam LocalDate to) {
        logger.info("Searching users by birthday range from {} to {}", from, to);
        List<User> users = userService.findUsersByBirthdayRange(from, to);
        return ResponseEntity.ok(new DataContainer<>(users));
    }
}