package com.mst.security_service.dto;

import com.mst.security_service.model.Permission;
import com.mst.security_service.model.Role;
import com.mst.security_service.model.UserCredentials;

import java.util.Set;
import java.util.stream.Collectors;

public record UserCredentialsDTO (Long id, String userName, String email, Set<String> roles, Set<String> permissions){
    public static UserCredentialsDTO fromEntity(UserCredentials userCredentials){
        Set<String> roles = userCredentials.getRoles().stream().map(Role::getName).collect(Collectors.toSet());
        Set<String> permissions = userCredentials.getPermissions().stream().map(Permission::getName).collect(Collectors.toSet());
        return  new UserCredentialsDTO(userCredentials.getId(), userCredentials.getUsername(),userCredentials.getEmail(), roles, permissions);
    }
}
