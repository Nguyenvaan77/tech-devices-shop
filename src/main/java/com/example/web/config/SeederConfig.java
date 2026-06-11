package com.example.web.config;

import java.util.Arrays;
import java.util.List;
import java.util.Set;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.example.web.entity.Permission;
import com.example.web.entity.Role;
import com.example.web.repository.PermissionRepository;
import com.example.web.repository.RoleRepository;
import com.example.web.util.PermissionEnum;
import com.example.web.util.RoleEnum;

import lombok.RequiredArgsConstructor;

@Configuration
@RequiredArgsConstructor
public class SeederConfig {
    private final RoleRepository roleRepository;
    private final PermissionRepository permissionRepository;

    @Bean
    CommandLineRunner initData() {
        return args -> {
            if (permissionRepository.count() == 0) {

            List<Permission> permissions =
                Arrays.stream(PermissionEnum.values())
                    .map(permissionEnum ->
                        Permission.builder()
                            .permissionName(permissionEnum)
                            .build()
                    )
                    .toList();

                permissionRepository.saveAll(permissions);
            }


            if(roleRepository.count() == 0){

                roleRepository.save(
                    new Role().builder()
                    .roleName(RoleEnum.ADMIN)
                    .permissions(null)
                    .build()
                );

                roleRepository.save(
                    new Role().builder().roleName(RoleEnum.BUSINESS).build()
                );

                roleRepository.save(
                    new Role().builder().roleName(RoleEnum.CUSTOMER).build()
                );
            }

            Role admin = roleRepository
                .findByRoleName(RoleEnum.ADMIN)
                .orElseThrow();

            Role business = roleRepository
                    .findByRoleName(RoleEnum.BUSINESS)
                    .orElseThrow();

            Role customer = roleRepository
                    .findByRoleName(RoleEnum.CUSTOMER)
                    .orElseThrow();

            // BUSINESS

            business.setPermissions(Set.of(
                    getPermission(PermissionEnum.PRODUCT_READ),
                    getPermission(PermissionEnum.PRODUCT_CREATE),
                    getPermission(PermissionEnum.PRODUCT_UPDATE),
                    getPermission(PermissionEnum.PRODUCT_DELETE),

                    getPermission(PermissionEnum.USER_CREATE),
                    getPermission(PermissionEnum.USER_UPDATE),

                    getPermission(PermissionEnum.ORDER_READ),
                    getPermission(PermissionEnum.ORDER_UPDATE),
                    getPermission(PermissionEnum.ORDER_CREATE),

                    getPermission(PermissionEnum.PAYMENT_CREATE),
                    getPermission(PermissionEnum.PAYMENT_READ)
            ));

            // CUSTOMER

            customer.setPermissions(Set.of(
                    getPermission(PermissionEnum.PRODUCT_READ),

                    getPermission(PermissionEnum.USER_CREATE),
                    getPermission(PermissionEnum.USER_UPDATE),

                    getPermission(PermissionEnum.ORDER_READ),
                    getPermission(PermissionEnum.ORDER_UPDATE),
                    getPermission(PermissionEnum.ORDER_CREATE),

                    getPermission(PermissionEnum.PAYMENT_CREATE),
                    getPermission(PermissionEnum.PAYMENT_READ)
            ));

            // ADMIN

            admin.setPermissions(Set.of(

                    getPermission(PermissionEnum.PRODUCT_READ),
                    getPermission(PermissionEnum.PRODUCT_DELETE),

                    getPermission(PermissionEnum.USER_CREATE),
                    getPermission(PermissionEnum.USER_DELETE),

                    getPermission(PermissionEnum.PAYMENT_DELETE),
                    getPermission(PermissionEnum.PAYMENT_READ),

                    getPermission(PermissionEnum.ORDER_READ)
            ));

            roleRepository.saveAll(
                    List.of(admin, business, customer)
            );
        };
    }

    private Permission getPermission(PermissionEnum permissionEnum) {
        return permissionRepository
                .findByPermissionName(permissionEnum)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Permission not found: "
                                        + permissionEnum));
    }   
}
