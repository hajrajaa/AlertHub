package com.mst.security_service.service;

import com.mst.security_service.client.UserServiceClient;
import com.mst.security_service.dao.PermissionDAO;
import com.mst.security_service.dao.RoleDAO;
import com.mst.security_service.dao.UserCredentialsDAO;
import com.mst.security_service.dto.Client.GetUserProfileResult;
import com.mst.security_service.dto.Client.UserProfileDTO;
import com.mst.security_service.dto.Client.UserProfileRequestDTO;
import com.mst.security_service.dto.FullUserDTO;
import com.mst.security_service.dto.FullUsersResult;
import com.mst.security_service.dto.UpdateUserRequestDTO;
import com.mst.security_service.dto.UserCredentialsDTO;
import com.mst.security_service.model.Permission;
import com.mst.security_service.model.Role;
import com.mst.security_service.model.UserCredentials;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class UserCredentialsService {

    @Autowired
    UserCredentialsDAO userCredentialsDAO;

    @Autowired
    RoleDAO roleDAO;

    @Autowired
    PermissionDAO permissionDAO;

    @Autowired
    UserServiceClient userServiceClient;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    // user credentials -----------------------------------------------------------

    public FullUsersResult getAllUsers() {
        try {
            List<UserCredentials> userCredentialsList = userCredentialsDAO.findAll();
            List<UserCredentialsDTO> userDTOs = userCredentialsList.stream()
                    .map(UserCredentialsDTO::fromEntity)
                    .toList();

            GetUserProfileResult profilesResult = userServiceClient.getUsersProfiles();
            if (profilesResult == null || profilesResult.users() == null) {
                throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to fetch user profiles");
            }

            Map<Long, UserProfileDTO> profileMap = profilesResult.users().stream()
                    .collect(Collectors.toMap(UserProfileDTO::authUserId, Function.identity()));

            List<FullUserDTO> combinedList = userDTOs.stream()
                    .map(cred -> new FullUserDTO(profileMap.get(cred.id()), cred))
                    .toList();

            return new FullUsersResult(userDTOs.size(), combinedList);

        } catch (Exception ex) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,
                    "Failed to retrieve users: " + ex.getMessage(), ex);
        }
    }

    public FullUserDTO getUserById(Long id) {
        UserCredentials user = userCredentialsDAO.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "User not found with id " + id
                ));

        UserCredentialsDTO credentialsDTO = UserCredentialsDTO.fromEntity(user);

        UserProfileDTO profileDTO = userServiceClient.getUserProfileById(id);
        if (profileDTO == null) {
            throw new ResponseStatusException(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    "Failed to retrieve profile for user with id " + id
            );
        }

        return new FullUserDTO(profileDTO, credentialsDTO);
    }

    @Transactional
    public FullUserDTO updateFullUser(Long id, UpdateUserRequestDTO request) {

        UserCredentials existingUser = userCredentialsDAO.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "User not found with id " + id
                ));

        if (request.userName() != null && !request.userName().isBlank()) {
            existingUser.setUserName(request.userName());
        }
        if (request.email() != null && !request.email().isBlank()) {
            existingUser.setEmail(request.email());
        }

        UserCredentials updatedUser = userCredentialsDAO.save(existingUser);
        UserCredentialsDTO credentialsDTO = UserCredentialsDTO.fromEntity(updatedUser);

        UserProfileRequestDTO profileRequest = new UserProfileRequestDTO(
                updatedUser.getId(),
                request.phoneNumber(),
                request.firstName(),
                request.lastName()
        );

        UserProfileDTO profileDTO = userServiceClient.updateUserProfile(updatedUser.getId(), profileRequest);
        if (profileDTO == null) {
            throw new ResponseStatusException(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    "Failed to update user profile"
            );
        }

        return new FullUserDTO(profileDTO, credentialsDTO);
    }

    @Transactional
    public void deleteUserCredentialById(Long id) {
        UserCredentials existingUser = userCredentialsDAO.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "User not found with id " + id
                ));

        try {
            existingUser.getPermissions().clear();
            existingUser.getRoles().clear();
            userCredentialsDAO.save(existingUser);

            userServiceClient.deleteUserProfileById(id);

            userCredentialsDAO.delete(existingUser);

        } catch (Exception ex) {
            throw new ResponseStatusException(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    "Failed to delete user with id " + id + ": " + ex.getMessage(),
                    ex
            );
        }
    }


    // roles -----------------------------------------------------------

    @Transactional
    public void assignRoles(Long userId, List<String> roleNames) {

        UserCredentials user = userCredentialsDAO.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "User not found with id " + userId
                ));

        List<Role> roles = roleDAO.getByNameIn(roleNames);
        if (roles.isEmpty()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "No valid roles found for the given role names"
            );
        }

        for (Role role : roles) {
            if (!user.getRoles().contains(role)) {
                user.getRoles().add(role);
            }
        }

        userCredentialsDAO.save(user);
        redisTemplate.delete("user:roles:" + userId);
    }

    @Transactional
    public void revokeRole(Long userId, String roleName) {

        if ("USER".equalsIgnoreCase(roleName)) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Cannot revoke default role: USER"
            );
        }

        UserCredentials user = userCredentialsDAO.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "User not found with id " + userId
                ));

        Role role = roleDAO.getByName(roleName)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.BAD_REQUEST,
                        "Role not found: " + roleName
                ));

        if (user.getRoles().contains(role)) {
            user.getRoles().remove(role);
            userCredentialsDAO.save(user);
            redisTemplate.delete("user:roles:" + userId);
        } else {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "User does not have the role: " + roleName
            );
        }
    }

// permissions -----------------------------------------------------------

    @Transactional
    public void assignPermissions(Long userId, List<String> permissionNames) {

        UserCredentials user = userCredentialsDAO.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "User not found with id " + userId
                ));

        List<Permission> permissions = permissionDAO.getByNameIn(permissionNames);
        if (permissions.isEmpty()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "No valid permissions found for the given permission names"
            );
        }

        for (Permission permission : permissions) {
            if (!user.getPermissions().contains(permission)) {
                user.getPermissions().add(permission);
            }
        }

        userCredentialsDAO.save(user);
        redisTemplate.delete("user:roles:" + userId);
    }

    @Transactional
    public void revokePermission(Long userId, String permissionName) {

        if ("READ".equalsIgnoreCase(permissionName)) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Cannot revoke default permission: READ"
            );
        }

        UserCredentials user = userCredentialsDAO.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "User not found with id " + userId
                ));

        Permission permission = permissionDAO.getByName(permissionName)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.BAD_REQUEST,
                        "Permission not found: " + permissionName
                ));

        if (user.getPermissions().contains(permission)) {
            user.getPermissions().remove(permission);
            userCredentialsDAO.save(user);
            redisTemplate.delete("user:roles:" + userId);
        } else {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "User does not have the permission: " + permissionName
            );
        }
    }
}

