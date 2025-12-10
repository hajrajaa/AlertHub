package com.mst.userservice.contoller;

import com.mst.userservice.dto.*;
import com.mst.userservice.service.UserProfileService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user-profiles")
public class UserProfileController {

    @Autowired
    UserProfileService userProfileService;

    @PostMapping("/create")
    public ResponseEntity<UserProfileDTO> createUserProfile(@Valid @RequestBody UserProfileRequestDTO request){
        UserProfileDTO result = userProfileService.createUserProfile(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(result);
    }

    @DeleteMapping("/{auth_id}")
    public ResponseEntity<Void> deleteUserProfileByAuthId(@PathVariable("auth_id") Long id){
        userProfileService.deleteUserProfileByAuthId(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }


    @GetMapping
    public ResponseEntity<GetUserProfilesResult> getAllUsersProfiles(){
        return ResponseEntity.status(HttpStatus.OK).body(userProfileService.getAllUsersProfiles());
    }

    @GetMapping("/{auth_id}")
    public ResponseEntity<UserProfileDTO> getUserProfileById(@PathVariable(name = "auth_id") Long id){
        UserProfileDTO userProfile = userProfileService.getUserProfileByAuthId(id);
        return ResponseEntity.ok(userProfile);
    }

    @PutMapping("/{authId}")
    public ResponseEntity<UserProfileDTO> updateUserProfile(
            @PathVariable Long authId,
            @RequestBody UserProfileRequestDTO request
    ) {
        UserProfileDTO result = userProfileService.updateUserProfile(authId, request);
        return ResponseEntity.ok(result);
    }
}