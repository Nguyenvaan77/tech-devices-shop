package com.example.web.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.web.entity.Role;
import com.example.web.util.RoleEnum;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long>{
    Optional<Role> findByRoleName(RoleEnum roleEnum);
}
