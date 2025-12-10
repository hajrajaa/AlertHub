package com.mst.security_service.dto;

import com.mst.security_service.dto.Client.UserProfileDTO;

public record FullUserDTO(UserProfileDTO profileDTO, UserCredentialsDTO credentialsDTO) {
}
