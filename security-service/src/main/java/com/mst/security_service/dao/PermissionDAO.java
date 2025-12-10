package com.mst.security_service.dao;

import com.mst.security_service.model.Permission;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PermissionDAO extends JpaRepository<Permission, Long> {
    Optional<Permission> getByName(String name);
    List<Permission> getByNameIn(List<String> permissionNames);
}
