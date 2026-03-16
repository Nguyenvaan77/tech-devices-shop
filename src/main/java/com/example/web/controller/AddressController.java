package com.example.web.controller;

import com.example.web.dto.ApiResponse;
import com.example.web.dto.address.request.CreateAddressRequest;
import com.example.web.service.inter.AddressService;

import lombok.RequiredArgsConstructor;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/addresses")
@RequiredArgsConstructor
public class AddressController {

    private final AddressService addressService;

    @PostMapping("/{userId}")
    public Object createAddress(
            @PathVariable Long userId,
            @RequestBody CreateAddressRequest request
    ) {

        return ApiResponse.created(
                addressService.createAddress(userId, request)
        );
    }

    @GetMapping("/user/{userId}")
    public Object getAddresses(@PathVariable Long userId) {

        return ApiResponse.success(
                addressService.getUserAddresses(userId)
        );
    }

    @DeleteMapping("/{id}")
    public Object deleteAddress(@PathVariable Long id) {

        addressService.deleteAddress(id);

        return ApiResponse.noContent();
    }
}