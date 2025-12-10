package com.mst.security_service.controller;

import com.mst.security_service.dto.FullUserDTO;
import com.mst.security_service.dto.FullUsersResult;
import com.mst.security_service.dto.UpdateUserRequestDTO;
import com.mst.security_service.dto.UserCredentialsDTO;
import com.mst.security_service.service.UserCredentialsService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/users")
public class UserCredentialsController {

    @Autowired
    UserCredentialsService userCredentialsService;

    // user credentials

    @GetMapping
    public ResponseEntity<FullUsersResult> getAllUsersCredentials(){
        return ResponseEntity.status(HttpStatus.OK).body(userCredentialsService.getAllUsers());
    }

    @GetMapping("/{id}")
    public ResponseEntity<FullUserDTO> getUserById(@PathVariable(name = "id") Long userId){
        FullUserDTO user = userCredentialsService.getUserById(userId);
        return ResponseEntity.ok(user);
    }

    @PutMapping("/{id}")
    public ResponseEntity<FullUserDTO> updateUser(
            @PathVariable Long id,
            @RequestBody @Valid UpdateUserRequestDTO request
    ) {
        return ResponseEntity.ok(userCredentialsService.updateFullUser(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteUserDetailsById(@PathVariable(name = "id") Long id){
        userCredentialsService.deleteUserCredentialById(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    // roles

    @PostMapping("/{id}/roles")
    public ResponseEntity<String> assignRoles(
            @PathVariable Long id,
            @RequestBody @NotEmpty(message = "Roles list cannot be empty") List<@NotBlank String> roles
    ) {
        userCredentialsService.assignRoles(id, roles);
        return ResponseEntity.ok("Roles assigned successfully");
    }

    @DeleteMapping("/{id}/roles/{role}")
    public ResponseEntity<String> revokeRole(
            @PathVariable Long id,
            @PathVariable @NotBlank(message = "Role name cannot be blank") String role
    ) {
        userCredentialsService.revokeRole(id, role);
        return ResponseEntity.ok("Role revoked successfully");
    }

    // permissions

    @PostMapping("/{id}/permissions")
    public ResponseEntity<String> assignPermissions(
            @PathVariable Long id,
            @RequestBody @NotEmpty(message = "Permissions list cannot be empty") List<@NotBlank String> permissions
    ) {
        userCredentialsService.assignPermissions(id, permissions);
        return ResponseEntity.ok("Permissions assigned successfully");
    }

    @DeleteMapping("/{id}/permissions/{permission}")
    public ResponseEntity<String> revokePermission(
            @PathVariable Long id,
            @PathVariable @NotBlank(message = "Permission name cannot be blank") String permission
    ) {
        userCredentialsService.revokePermission(id, permission);
        return ResponseEntity.ok("Permission revoked successfully");
    }


}
