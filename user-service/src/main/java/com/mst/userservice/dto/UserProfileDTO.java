package com.mst.userservice.dto;

import com.mst.userservice.model.UserProfile;

import java.time.LocalDateTime;

public record UserProfileDTO(
        Long authUserId,
        String phoneNumber,
        String firstName,
        String lastName
) {
    // Convert User entity to DTO
    public static UserProfileDTO fromEntity(UserProfile userProfile) {
        return new UserProfileDTO(
                userProfile.getAuthUserId(),
                userProfile.getPhoneNumber(),
                userProfile.getFirstName(),
                userProfile.getLastName()
        );
    }
}