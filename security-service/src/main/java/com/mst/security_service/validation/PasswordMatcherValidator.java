package com.mst.security_service.validation;


import com.mst.security_service.dto.RegisterUserRequest;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class PasswordMatcherValidator implements ConstraintValidator<PasswordMatches, RegisterUserRequest> {

    @Override
    public boolean isValid(RegisterUserRequest request, ConstraintValidatorContext context) {
        if (request == null) {
            return true;
        }
        String password = request.password();
        String confirmation = request.passwordConfirmation();
        return password != null && password.equals(confirmation);
    }
}