package com.example.web.repository;

import java.util.List;
import java.util.Optional;

import org.aspectj.apache.bcel.generic.LOOKUPSWITCH;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.web.entity.Permission;
import com.example.web.util.PermissionEnum;

@Repository
public interface PermissionRepository extends JpaRepository<Permission, Long>{
    Optional<Permission> findByPermissionName(PermissionEnum permissionEnum);
}
