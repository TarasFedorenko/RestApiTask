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

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testCreateUserWithValidAge() {
            User user = new User();
            user.setBirthday(LocalDate.of(1990, 5, 15)); // Assuming the user is above the acceptable age

            when(ageValidator.isValidAge(user.getBirthday())).thenReturn(true);
            userService.createUser(user);

            verify(userRepository, times(1)).save(user);
        }
    @Test
    public void testCreateUserWithInvalidAge() {
        User user = new User();
        user.setId(1L);
        user.setFirstName("Test");
        user.setLastName("Test");
        user.setEmail("test@mail.com");
        user.setBirthday(LocalDate.now().minusYears(10));

        when(ageValidator.isValidAge(user.getBirthday())).thenReturn(false);

        assertThrows(UserAgeUnacceptableException.class, () -> userService.createUser(user));
    }

    @Test
    void testUpdateUser() {
        Long userId = 1L;
        User existingUser = new User();
        existingUser.setId(userId);
        existingUser.setFirstName("Test");
        existingUser.setLastName("Test");
        existingUser.setEmail("old@example.com");
        existingUser.setBirthday(LocalDate.of(1991,11, 11));

        when(userRepository.findById(userId)).thenReturn(Optional.of(existingUser));

        User updateUser = new User();
        updateUser.setFirstName("Test1");
        updateUser.setLastName("Test1");
        updateUser.setBirthday(LocalDate.of(1990,11, 11));
        updateUser.setEmail("new@example.com");

        when(userRepository.findById(userId)).thenReturn(java.util.Optional.of(existingUser));
        when(userRepository.save(existingUser)).thenReturn(updateUser);


        User result = userService.updateUser(userId, updateUser);


        assertEquals(updateUser.getEmail(), result.getEmail());

        verify(userRepository, times(1)).findById(userId);
        verify(userRepository, times(1)).save(existingUser);
    }

    @Test
    void testUpdateUsersFields() {
        Long userId = 1L;
        User existingUser = new User();
        existingUser.setId(userId);
        existingUser.setFirstName("Test");
        existingUser.setLastName("Test");
        existingUser.setEmail("old@example.com");
        existingUser.setBirthday(LocalDate.of(1991,11, 11));

        when(userRepository.findById(userId)).thenReturn(Optional.of(existingUser));

        User updateUser = new User();
        updateUser.setEmail("new@example.com");

        when(userRepository.findById(userId)).thenReturn(java.util.Optional.of(existingUser));
        when(userRepository.save(existingUser)).thenReturn(existingUser);

        User result = userService.updateUsersFields(userId, updateUser);

        assertEquals(existingUser.getId(), result.getId());
        assertEquals(updateUser.getEmail(), result.getEmail());

        verify(userRepository, times(1)).findById(userId);
        verify(userRepository, times(1)).save(existingUser);
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