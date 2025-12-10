package com.mst.security_service.dto.Client;

import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record UserProfileRequestDTO(

        Long authId,

        @Pattern(regexp = "^[0-9+\\-() ]*$", message = "Invalid phone number")
        String phoneNumber,


        @Size(max = 50, message = "First name cannot exceed 50 characters")
        String firstName,

        @Size(max = 50, message = "Last name cannot exceed 50 characters")
        String lastName
) {}