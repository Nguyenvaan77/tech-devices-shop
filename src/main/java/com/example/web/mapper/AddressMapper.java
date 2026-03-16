package com.example.web.mapper;

import com.example.web.dto.address.request.CreateAddressRequest;
import com.example.web.dto.address.response.AddressResponse;
import com.example.web.entity.Address;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface AddressMapper {

    AddressResponse toResponse(Address entity);

    Address toEntity(CreateAddressRequest request);
}