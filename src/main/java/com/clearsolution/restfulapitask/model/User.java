package com.clearsolution.restfulapitask.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import lombok.Data;

import java.time.LocalDate;


@Entity
@Table(name = "users")
@Data
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Email(message = "Incorrect email")
    @NotBlank(message = "Email required")
    private String email;
    @Column(name = "first_name")
    @NotBlank(message = "First name must be present")
    private String firstName;
    @Column(name = "last_name")
    @NotBlank(message = "Last name must be present")
    private String lastName;
    @Past(message = "Birth day can`t be in future or in present")
    @NotNull(message = "Birthday date must be present")
    private LocalDate birthday;

    private String address;
    @Column(name = "phone_number")
    private String phoneNumber;


}
