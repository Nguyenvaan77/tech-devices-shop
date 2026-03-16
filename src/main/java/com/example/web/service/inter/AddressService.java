package com.example.web.service.inter;

import com.example.web.dto.address.request.CreateAddressRequest;
import com.example.web.dto.address.response.AddressResponse;

import java.util.List;

public interface AddressService {

    AddressResponse createAddress(Long userId, CreateAddressRequest request);

    List<AddressResponse> getUserAddresses(Long userId);

    void deleteAddress(Long addressId);

}