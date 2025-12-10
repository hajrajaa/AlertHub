package com.mst.userservice.dto;

import java.util.List;

public record GetUserProfilesResult(int userCount, List<UserProfileDTO> users) {
}
