package com.mst.security_service.controller;

import com.mst.security_service.dto.*;
import com.mst.security_service.model.Role;
import com.mst.security_service.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<FullUserDTO> register(@Valid @RequestBody RegisterUserRequest registerUserRequest){
        FullUserDTO result = authService.register(registerUserRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(result);
    }

    @PostMapping("/login")
    public ResponseEntity<TokenDTO> login(@RequestBody LoginRequest loginRequest){
        TokenDTO token = authService.login(loginRequest);
        return ResponseEntity.ok(token);
    }

    @GetMapping("/{userId}/roles")
    public ResponseEntity<List<String>> getRoleNamesById(@PathVariable(name = "userId") Long id){
        List<String> userRoles = authService.getUserRoleNames(id);
        return ResponseEntity.ok(userRoles);
    }

    @GetMapping("/me")
    public ResponseEntity<UserCredentialsDTO> getMe(){
        return ResponseEntity.status(HttpStatus.OK).body(authService.getMe());
    }
}
