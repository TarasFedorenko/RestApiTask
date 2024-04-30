package com.clearsolution.restfulapitask.service.impl;

import com.clearsolution.restfulapitask.exception.UserAgeUnacceptableException;
import com.clearsolution.restfulapitask.exception.WrongDateRangeException;
import com.clearsolution.restfulapitask.model.User;
import com.clearsolution.restfulapitask.repository.UserRepository;
import com.clearsolution.restfulapitask.validation.AgeValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private AgeValidator ageValidator;

    @InjectMocks
    private UserServiceImpl userService;

    private User user;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        user = new User();
        user.setId(1L);
        user.setFirstName("Test");
        user.setLastName("Test");
        user.setEmail("test@mail.com");
        user.setBirthday(LocalDate.of(1990, 5, 15)); // Assuming the user is above the acceptable age
    }

    @Test
    void testCreateUserWithValidAge() {
        when(ageValidator.isValidAge(user.getBirthday())).thenReturn(true);

        assertDoesNotThrow(() -> userService.createUser(user));

        verify(userRepository, times(1)).save(user);
    }

    @Test
    public void testCreateUserWithInvalidAge() {
        user.setBirthday(LocalDate.now().minusYears(10)); // Assuming the user is below the acceptable age

        when(ageValidator.isValidAge(user.getBirthday())).thenReturn(false);

        assertThrows(UserAgeUnacceptableException.class, () -> userService.createUser(user));
    }

    @Test
    void testUpdateUser() {
        Long userId = 1L;

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        User updateUser = new User();
        updateUser.setFirstName("Test1");
        updateUser.setLastName("Test1");
        updateUser.setBirthday(LocalDate.of(1990, 11, 11));
        updateUser.setEmail("new@example.com");

        when(userRepository.save(user)).thenReturn(updateUser);

        User result = userService.updateUser(userId, updateUser);

        assertEquals(updateUser.getEmail(), result.getEmail());

        verify(userRepository, times(1)).findById(userId);
        verify(userRepository, times(1)).save(user);
    }

    @Test
    void testUpdateUsersFields() {
        Long userId = 1L;

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        User updateUser = new User();
        updateUser.setEmail("new@example.com");

        when(userRepository.save(user)).thenReturn(user);

        User result = userService.updateUsersFields(userId, updateUser);

        assertEquals(user.getId(), result.getId());
        assertEquals(updateUser.getEmail(), result.getEmail());

        verify(userRepository, times(1)).findById(userId);
        verify(userRepository, times(1)).save(user);
    }

    @Test
    void testDeleteUser() {
        Long userId = 1L;
        User existingUser = new User();
        existingUser.setId(userId);

        when(userRepository.findById(userId)).thenReturn(Optional.of(existingUser));

        assertDoesNotThrow(() -> userService.deleteUser(userId));

        verify(userRepository, times(1)).delete(existingUser);
    }

    @Test
    void testFindUsersByBirthdayRange() {
        LocalDate from = LocalDate.of(1990, 1, 1);
        LocalDate to = LocalDate.of(2000, 12, 31);

        assertDoesNotThrow(() -> userService.findUsersByBirthdayRange(from, to));

        verify(userRepository, times(1)).findUsersByBirthDateRange(from, to);
    }

    @Test
    void testFindUsersByWrongBirthdayRange() {
        LocalDate to = LocalDate.of(1990, 1, 1);
        LocalDate from = LocalDate.of(2000, 12, 31);

        assertThrows(WrongDateRangeException.class, () -> userService.findUsersByBirthdayRange(from, to));

        verify(userRepository, never()).findUsersByBirthDateRange(from, to);
    }
}
