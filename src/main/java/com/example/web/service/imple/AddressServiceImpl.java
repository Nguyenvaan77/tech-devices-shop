package com.example.web.service.imple;

import com.example.web.dto.address.request.CreateAddressRequest;
import com.example.web.dto.address.response.AddressResponse;
import com.example.web.entity.Address;
import com.example.web.entity.User;
import com.example.web.mapper.AddressMapper;
import com.example.web.repository.AddressRepository;
import com.example.web.repository.UserRepository;
import com.example.web.service.inter.AddressService;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AddressServiceImpl implements AddressService {

    private final AddressRepository addressRepository;
    private final UserRepository userRepository;
    private final AddressMapper addressMapper;

    @Override
    public AddressResponse createAddress(Long userId, CreateAddressRequest request) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Address address = addressMapper.toEntity(request);

        address.setUser(user);

        address = addressRepository.save(address);

        return addressMapper.toResponse(address);
    }

    @Override
    public List<AddressResponse> getUserAddresses(Long userId) {

        return addressRepository.findByUserId(userId)
                .stream()
                .map(addressMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteAddress(Long addressId) {

        addressRepository.deleteById(addressId);
    }
}