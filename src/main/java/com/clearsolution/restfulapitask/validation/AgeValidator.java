package com.clearsolution.restfulapitask.validation;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
public class AgeValidator {

    private final int legalAge;

    public AgeValidator(@Value("${app.validation.age}") int legalAge) {
        this.legalAge = legalAge;
    }

    public boolean isValidAge(LocalDate dateOfBirth) {
        LocalDate legalAgeDate = LocalDate.now().minusYears(legalAge);
        return dateOfBirth.isBefore(legalAgeDate);
    }
}