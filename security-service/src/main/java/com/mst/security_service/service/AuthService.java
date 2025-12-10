package com.mst.security_service.service;

import com.mst.security_service.client.UserServiceClient;
import com.mst.security_service.dao.PermissionDAO;
import com.mst.security_service.dao.RoleDAO;
import com.mst.security_service.dao.UserCredentialsDAO;
import com.mst.security_service.dto.*;
import com.mst.security_service.dto.Client.UserProfileDTO;
import com.mst.security_service.dto.Client.UserProfileRequestDTO;
import com.mst.security_service.model.Permission;
import com.mst.security_service.model.Role;
import com.mst.security_service.model.UserCredentials;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class AuthService {

    @Autowired
    UserCredentialsDAO userCredentialsDAO;

    @Autowired
    RoleDAO roleDAO;

    @Autowired
    PermissionDAO permissionDAO;

    @Autowired
    BCryptPasswordEncoder passwordEncoder;

    @Autowired
    UserServiceClient userServiceClient;

    @Autowired
    AuthenticationManager authenticationManager;

    @Autowired
    TokenService tokenService;

    @Transactional
    public FullUserDTO register(RegisterUserRequest req) {

        Optional<UserCredentials> foundUser = userCredentialsDAO.getByUserNameOrEmail(req.userName(), req.email());

        foundUser.ifPresent(user -> {
            String message;
            if (user.getUsername().equals(req.userName()) && user.getEmail().equals(req.email())) {
                message = "Username and Email are already taken";
            } else if (user.getUsername().equals(req.userName())) {
                message = "Username is already taken";
            } else {
                message = "Email is already in use";
            }
            throw new ResponseStatusException(HttpStatus.CONFLICT, message);
        });

        String passwordHash = passwordEncoder.encode(req.password());

        Role defaultRole = roleDAO.getByName("USER")
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.INTERNAL_SERVER_ERROR,
                        "Default role not found"
                ));

        Permission defaultPermission = permissionDAO.getByName("READ")
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.INTERNAL_SERVER_ERROR,
                        "Default permission not found"
                ));

        UserCredentials userCredentials = UserCredentials.builder()
                .userName(req.userName())
                .email(req.email())
                .passwordHash(passwordHash)
                .roles(Set.of(defaultRole))
                .permissions(Set.of(defaultPermission))
                .build();

        UserCredentials createdUser = userCredentialsDAO.save(userCredentials);

        UserProfileDTO profileResponse = userServiceClient.createUserProfile(
                new UserProfileRequestDTO(
                        createdUser.getId(),
                        req.phoneNumber(),
                        req.firstName(),
                        req.lastName()
                )
        );

        if (profileResponse == null) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to create user profile");
        }

        return new FullUserDTO(profileResponse, UserCredentialsDTO.fromEntity(createdUser));
    }

    public TokenDTO login(LoginRequest loginRequest) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginRequest.userName(),
                            loginRequest.password()
                    )
            );

            UserCredentials userCredentials = (UserCredentials) authentication.getPrincipal();
            return tokenService.generateToken(userCredentials);

        } catch (BadCredentialsException ex) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid username or password");
        } catch (Exception ex) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Authentication failed: " + ex.getMessage());
        }
    }

    public UserCredentialsDTO getMe() {
        String userIdStr = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Long userId = Long.valueOf(userIdStr);
        UserCredentials user = userCredentialsDAO.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.UNAUTHORIZED,
                        "User not found with ID: " + userId));

        return UserCredentialsDTO.fromEntity(user);
    }

    public List<String> getUserRoleNames(Long userId) {
        UserCredentials userCredentials = userCredentialsDAO
                .findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "user not found with Id " + userId));
        return userCredentials.getAuthorities().stream().map(grantedAuthority -> grantedAuthority.getAuthority().substring(5)).toList();
    }
}
