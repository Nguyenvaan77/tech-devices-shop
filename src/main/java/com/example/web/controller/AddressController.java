package com.example.web.controller;

import com.example.web.dto.ApiResponse;
import com.example.web.dto.address.request.CreateAddressRequest;
import com.example.web.dto.address.response.AddressResponse;
import com.example.web.service.inter.AddressService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/addresses")
@RequiredArgsConstructor
@Tag(name = "Addresses", description = "User address management APIs")
public class AddressController {

    private final AddressService addressService;

    @PostMapping("/{userId}")
    @Operation(summary = "Create new address", description = "Add a new address for a user")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "Address created successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Invalid address data"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "User not found")
    })
    public ResponseEntity<ApiResponse<AddressResponse>> createAddress(
            @PathVariable Long userId,
            @RequestBody CreateAddressRequest request) {
        AddressResponse address = addressService.createAddress(userId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.created(address));
    }

    @GetMapping("/user/{userId}")
    @Operation(summary = "Get user's addresses", description = "Retrieve all addresses for a specific user")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Addresses retrieved successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Invalid user ID"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "User not found")
    })
    public ResponseEntity<ApiResponse<List<AddressResponse>>> getAddresses(@PathVariable Long userId) {
        List<AddressResponse> addresses = addressService.getUserAddresses(userId);
        return ResponseEntity.ok(ApiResponse.success(addresses));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete address", description = "Remove an address by ID")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "204", description = "Address deleted successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Invalid address ID"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Address not found")
    })
    public ResponseEntity<Void> deleteAddress(@PathVariable Long id) {
        addressService.deleteAddress(id);
        return ResponseEntity.noContent().build();
    }
}