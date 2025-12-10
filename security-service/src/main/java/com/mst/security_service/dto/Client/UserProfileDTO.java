package com.mst.security_service.dto.Client;

public record UserProfileDTO(
        Long authUserId,
        String phoneNumber,
        String firstName,
        String lastName
)  {}