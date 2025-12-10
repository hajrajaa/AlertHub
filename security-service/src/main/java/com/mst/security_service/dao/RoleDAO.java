package com.mst.security_service.dao;

import com.mst.security_service.model.Role;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface RoleDAO extends JpaRepository<Role, Long> {
    Optional<Role> getByName(String name);
    List<Role> getByNameIn(List<String> roles);
}
