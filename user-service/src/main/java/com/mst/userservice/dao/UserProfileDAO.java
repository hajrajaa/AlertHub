package com.mst.userservice.dao;

import com.mst.userservice.model.UserProfile;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserProfileDAO extends JpaRepository<UserProfile, Long> {
    Optional<UserProfile> getByAuthUserId(Long authUserId);
    boolean existsByAuthUserId(Long authUserId);
    void deleteByAuthUserId(Long authUserId);

}
