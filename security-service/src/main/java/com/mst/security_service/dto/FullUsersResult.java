package com.mst.security_service.dto;

import java.util.List;

public record FullUsersResult(int count, List<FullUserDTO> users) {
}
