package com.mst.security_service.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record UpdateUserRequestDTO(
        @Pattern(regexp = "^[0-9+\\-() ]*$", message = "Invalid phone number")
        String phoneNumber,

        @Size(max = 50, message = "First name cannot exceed 50 characters")
        String firstName,

        @Size(max = 50, message = "Last name cannot exceed 50 characters")
        String lastName,

        // --- Credentials Fields ---
        @Size(min = 3, max = 50, message = "Username must be between 3 and 50 characters")
        String userName,

        @Email(message = "Invalid email address")
        @Size(max = 100, message = "Email cannot exceed 100 characters")
        String email
) {}