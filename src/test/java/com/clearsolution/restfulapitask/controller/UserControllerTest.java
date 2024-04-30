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

    private User user;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1L);
        user.setFirstName("Test");
        user.setLastName("Test");
        user.setEmail("example@org.ua");
        user.setBirthday(LocalDate.of(1990, 5, 11));
    }

    @Test
    void testCreateUser() throws Exception {
        doNothing().when(userService).createUser(any(User.class));

        mockMvc.perform(post("/v1/api/users/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"firstName\": \"Test\", \"lastName\": \"Test\", \"email\": \"example@org.ua\", \"birthday\": \"1990-05-11\"}"))
                .andExpect(status().isCreated());

        verify(userService, times(1)).createUser(any(User.class));
    }

    @Test
    void testUpdateUser() throws Exception {
        when(userService.updateUser(any(Long.class), any(User.class))).thenReturn(user);

        mockMvc.perform(put("/v1/api/users/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"firstName\": \"Test\", \"lastName\": \"Test\", \"email\": \"example@org.ua\", \"birthday\": \"1990-05-11\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.firstName").value("Test"))
                .andExpect(jsonPath("$.data.lastName").value("Test"))
                .andExpect(jsonPath("$.data.email").value("example@org.ua"))
                .andExpect(jsonPath("$.data.birthday").value("1990-05-11"));

        verify(userService, times(1)).updateUser(eq(1L), any(User.class));
    }

    @Test
    void testUpdateFields() throws Exception {
        when(userService.updateUsersFields(any(Long.class), any(User.class))).thenReturn(user);

        mockMvc.perform(patch("/v1/api/users/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"firstName\": \"Test\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.firstName").value("Test"));

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
        when(userService.findUsersByBirthdayRange(any(LocalDate.class), any(LocalDate.class))).thenReturn(Collections.singletonList(user));

        mockMvc.perform(get("/v1/api/users/search")
                        .param("from", LocalDate.now().minusDays(1).toString())
                        .param("to", LocalDate.now().plusDays(1).toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data[0].firstName").value("Test"))
                .andExpect(jsonPath("$.data[0].lastName").value("Test"))
                .andExpect(jsonPath("$.data[0].email").value("example@org.ua"))
                .andExpect(jsonPath("$.data[0].birthday").value("1990-05-11"));

        verify(userService, times(1)).findUsersByBirthdayRange(any(LocalDate.class), any(LocalDate.class));
    }
}
