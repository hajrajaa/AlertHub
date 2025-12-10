package com.mst.security_service.dto.Client;

import java.util.List;

public record GetUserProfileResult(int userCount, List<UserProfileDTO> users) {
}
