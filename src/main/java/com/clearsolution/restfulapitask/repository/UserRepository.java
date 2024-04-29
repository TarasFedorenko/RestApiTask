package com.clearsolution.restfulapitask.repository;

import com.clearsolution.restfulapitask.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    @Query("SELECT u FROM User u WHERE u.birthday BETWEEN :from AND :to")
    List<User> findUsersByBirthDateRange(LocalDate from, LocalDate to);
}
