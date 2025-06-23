package com.manish.employara.validator;

import java.lang.reflect.Field;
import java.time.LocalDate;

import com.manish.employara.annotation.ValidDateRange;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class DateRangeValidator implements ConstraintValidator<ValidDateRange, Object> {
    private String startDateField;
    private String endDateField;

    @Override
    public void initialize(ValidDateRange constraintAnnotation) {
        startDateField = constraintAnnotation.startDateField();
        endDateField = constraintAnnotation.endDateField();
    }

    @Override
    public boolean isValid(Object object, ConstraintValidatorContext context) {
        try {
            Field startDateFieldObj = object.getClass().getDeclaredField(startDateField);
            Field endDateFieldObj = object.getClass().getDeclaredField(endDateField);
            startDateFieldObj.setAccessible(true);
            endDateFieldObj.setAccessible(true);

            LocalDate startDate = (LocalDate) startDateFieldObj.get(object);
            LocalDate endDate = (LocalDate) endDateFieldObj.get(object);

            if (startDate == null || endDate == null) {
                return true; // Skip validation if either date is null
            }

            return !startDate.isAfter(endDate);
        } catch (Exception e) {
            return false;
        }
    }
}
