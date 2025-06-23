package com.manish.employara.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.manish.employara.validator.CertificateLinkOrDocumentsValidator;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

@Target({ ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = CertificateLinkOrDocumentsValidator.class)
@Documented
public @interface CertificateLinkOrDocumentsRequired {
    String message() default "Either certificateLink or documents must be provided";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
