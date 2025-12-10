package com.mst.security_service.client;

import com.mst.security_service.dto.Client.GetUserProfileResult;
import com.mst.security_service.dto.Client.UserProfileDTO;
import com.mst.security_service.dto.Client.UserProfileRequestDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

@FeignClient(name = "user-service", url = "http://user-service:8082")
public interface UserServiceClient {
    @PostMapping("/user-profiles/create")
    UserProfileDTO createUserProfile(@RequestBody UserProfileRequestDTO userProfileDTO);

    @DeleteMapping("/user-profiles/{id}")
    void deleteUserProfileById(@PathVariable("id") Long id);

    @GetMapping("/user-profiles/{id}")
    UserProfileDTO getUserProfileById(@PathVariable("id") Long id);

    @GetMapping("/user-profiles")
    GetUserProfileResult getUsersProfiles();

    @PutMapping("/user-profiles/{id}")
    UserProfileDTO updateUserProfile(
            @PathVariable("id") Long id,
            @RequestBody UserProfileRequestDTO request
    );
}
