package com.clearsolution.restfulapitask.controller;

import com.clearsolution.restfulapitask.model.User;
import com.clearsolution.restfulapitask.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@WebMvcTest(UserController.class)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @InjectMocks
    private UserController userController;

    @Test
    void testCreateUser() throws Exception {
        User user = new User();
        user.setId(1L);
        user.setFirstName("First Test");
        user.setLastName("Last Test");
        user.setEmail("example@org.ua");
        user.setBirthday(LocalDate.of(1990, 5, 11));

        doNothing().when(userService).createUser(any(User.class));

        mockMvc.perform(post("/v1/api/users/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"firstName\": \"First Test\", \"lastName\": \"Last Test\", \"email\": \"example@org.ua\", \"birthday\": \"1990-05-11\"}"))
                .andExpect(status().isCreated());

        verify(userService, times(1)).createUser(any(User.class));
    }

    @Test
    void testUpdateUser() throws Exception {
        User updatedUser = new User();
        updatedUser.setId(1L);
        updatedUser.setFirstName("New Test");
        updatedUser.setLastName("New Test");
        updatedUser.setEmail("example1@org.ua");
        updatedUser.setBirthday(LocalDate.of(1991, 5, 11));

        when(userService.updateUser(any(Long.class), any(User.class))).thenReturn(updatedUser);

        mockMvc.perform(put("/v1/api/users/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"firstName\": \"New Test\", \"lastName\": \"New Test\", \"email\": \"example1@org.ua\", \"birthday\": \"1991-05-11\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.firstName").value("New Test"))
                .andExpect(jsonPath("$.data.lastName").value("New Test"))
                .andExpect(jsonPath("$.data.email").value("example1@org.ua"))
                .andExpect(jsonPath("$.data.birthday").value("1991-05-11"));

        verify(userService, times(1)).updateUser(eq(1L), any(User.class));

    }

    @Test
    void testUpdateFields() throws Exception {
        User updatedUser = new User();
        updatedUser.setId(1L);
        updatedUser.setFirstName("New One Test");

        when(userService.updateUsersFields(any(Long.class), any(User.class))).thenReturn(updatedUser);

        mockMvc.perform(patch("/v1/api/users/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"firstName\": \"New One Test\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.firstName").value("New One Test"));

        verify(userService, times(1)).updateUsersFields(eq(1L), any(User.class));
    }

    @Test
    void testDeleteUser() throws Exception {

        doNothing().when(userService).deleteUser(any(Long.class));

        mockMvc.perform(delete("/v1/api/users/1"))
                .andExpect(status().isOk());
    }

    @Test
    void testSearchUserByBirthdayRange() throws Exception {
        User user = new User();
        user.setId(1L);
        user.setFirstName("Search Test");
        user.setLastName("Search Test");
        user.setEmail("example2@org.ua");
        user.setBirthday(LocalDate.of(1992, 5, 11));

        when(userService.findUsersByBirthdayRange(any(LocalDate.class), any(LocalDate.class))).thenReturn(Collections.singletonList(user));

        mockMvc.perform(get("/v1/api/users/search")
                        .param("from", LocalDate.now().minusDays(1).toString())
                        .param("to", LocalDate.now().plusDays(1).toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data[0].firstName").value("Search Test"))
                .andExpect(jsonPath("$.data[0].lastName").value("Search Test"))
                .andExpect(jsonPath("$.data[0].email").value("example2@org.ua"))
                .andExpect(jsonPath("$.data[0].birthday").value("1992-05-11"));

        verify(userService, times(1)).findUsersByBirthdayRange(any(LocalDate.class), any(LocalDate.class));
    }
}