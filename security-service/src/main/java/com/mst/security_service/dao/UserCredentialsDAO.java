package com.mst.security_service.dao;

import com.mst.security_service.model.UserCredentials;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserCredentialsDAO extends JpaRepository<UserCredentials, Long> {
    Optional<UserCredentials> getByUserName(String userName);
    Optional<UserCredentials> getByEmail(String email);
    Optional<UserCredentials> getByUserNameOrEmail(String userName, String email);
}
