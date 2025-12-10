package com.mst.userservice.service;

import com.mst.userservice.dao.UserProfileDAO;
import com.mst.userservice.dto.*;
import com.mst.userservice.model.UserProfile;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.*;

@Service
public class UserProfileService {

    @Autowired
    UserProfileDAO userProfileDAO;

    public UserProfileDTO createUserProfile(UserProfileRequestDTO userProfileDTO){

        if (userProfileDAO.existsByAuthUserId(userProfileDTO.authId())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "User with this authId already exists");
        }

        UserProfile userProfile = UserProfile
                .builder()
                .authUserId(userProfileDTO.authId())
                .phoneNumber(userProfileDTO.phoneNumber())
                .firstName(userProfileDTO.firstName())
                .lastName(userProfileDTO.lastName())
                .build();

        UserProfile createdUserProfile = userProfileDAO.save(userProfile);

        return UserProfileDTO.fromEntity(createdUserProfile);
    }

    public void deleteUserProfileByAuthId(Long authId) {
        Optional<UserProfile> existingUser = userProfileDAO.getByAuthUserId(authId);

        if (existingUser.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User profile not found");
        }

        userProfileDAO.delete(existingUser.get());
    }

    public GetUserProfilesResult getAllUsersProfiles(){
        List<UserProfile> existingUserProfiles = userProfileDAO.findAll();
        List<UserProfileDTO> mappedUsers = existingUserProfiles.stream().map(UserProfileDTO::fromEntity).toList();
        return new GetUserProfilesResult(existingUserProfiles.size(), mappedUsers) ;
    }

    public UserProfileDTO getUserProfileByAuthId(Long authId){
        return userProfileDAO.getByAuthUserId(authId)
                .map(UserProfileDTO::fromEntity)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "User profile not found for authId: " + authId
                ));
    }


    public UserProfileDTO updateUserProfile(Long authId, UserProfileRequestDTO request) {
        UserProfile existingUser = userProfileDAO.getByAuthUserId(authId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "User not found with auth ID: " + authId
                ));

        if (request.phoneNumber() != null) existingUser.setPhoneNumber(request.phoneNumber());
        if (request.firstName() != null) existingUser.setFirstName(request.firstName());
        if (request.lastName() != null) existingUser.setLastName(request.lastName());

        UserProfile updatedUser = userProfileDAO.save(existingUser);

        return UserProfileDTO.fromEntity(updatedUser);
    }

}
