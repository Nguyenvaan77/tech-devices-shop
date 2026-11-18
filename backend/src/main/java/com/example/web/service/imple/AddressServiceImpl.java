package com.example.web.service.imple;

import com.example.web.dto.address.request.CreateAddressRequest;
import com.example.web.dto.address.response.AddressResponse;
import com.example.web.entity.Address;
import com.example.web.entity.User;
import com.example.web.exception.BadRequestException;
import com.example.web.exception.ResourceNotFoundException;
import com.example.web.mapper.AddressMapper;
import com.example.web.repository.AddressRepository;
import com.example.web.repository.UserRepository;
import com.example.web.service.inter.AddressService;

import lombok.RequiredArgsConstructor;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class AddressServiceImpl implements AddressService {

    private final AddressRepository addressRepository;
    private final UserRepository userRepository;
    private final AddressMapper addressMapper;
    private final RedisTemplate<String, Object> redisTemplate;

    @Override

    public AddressResponse createAddress(Long userId, CreateAddressRequest request) {
        if (userId == null || userId <= 0) {
            throw new BadRequestException("Invalid user ID");
        }

        if (request == null) {
            throw new BadRequestException("Address request cannot be null");
        }

        if (request.getDetail() == null || request.getDetail().isBlank()) {
            throw new BadRequestException("Address detail is required");
        }

        if (request.getProvince() == null || request.getProvince().isBlank()) {
            throw new BadRequestException("Province is required");
        }

        if (request.getDistrict() == null || request.getDistrict().isBlank()) {
            throw new BadRequestException("District is required");
        }

        

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));

        Address address = addressMapper.toEntity(request);
        address.setUser(user);

        address = addressRepository.save(address);

        // Clear cache for user addresses
        String cacheKey = "user_addresses:" + userId;
        redisTemplate.delete(cacheKey);

        return addressMapper.toResponse(address);
    }

    @Override
    public AddressResponse getAddressById(Long id) {
        if (id == null || id <= 0) {
            throw new BadRequestException("Invalid address ID");
        }

        Address address = addressRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Address not found with id: " + id));

        return addressMapper.toResponse(address);
    }

    @Override
    @Transactional(readOnly = true)
    public List<AddressResponse> getUserAddresses(Long userId) {
        if (userId == null || userId <= 0) {
            throw new BadRequestException("Invalid user ID");
        }

        String cacheKey = "user_addresses:" + userId;
        @SuppressWarnings("unchecked")
        List<AddressResponse> cachedAddresses = (List<AddressResponse>) redisTemplate.opsForValue().get(cacheKey);
        if (cachedAddresses != null && !cachedAddresses.isEmpty()) {
            return cachedAddresses;
        }

        // Verify user exists
        if (!userRepository.existsById(userId)) {
            throw new ResourceNotFoundException("User not found with id: " + userId);
        }

        List<AddressResponse> addresses = addressRepository.findByUserId(userId)
                .stream()
                .map(addressMapper::toResponse)
                .collect(Collectors.toList());
        redisTemplate.opsForValue().set(cacheKey, addresses, Duration.ofMinutes(10));
        return addresses;
    }

    @Override

    public AddressResponse updateAddress(Long id, CreateAddressRequest request) {
        if (id == null || id <= 0) {
            throw new BadRequestException("Invalid address ID");
        }

        Address address = addressRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Address not found with id: " + id));

        if (request.getReceiverName() != null) {
            address.setReceiverName(request.getReceiverName());
        }
        if (request.getPhone() != null) {
            address.setPhone(request.getPhone());
        }
        if (request.getProvince() != null) {
            address.setProvince(request.getProvince());
        }
        if (request.getDistrict() != null) {
            address.setDistrict(request.getDistrict());
        }
        if (request.getWard() != null) {
            address.setWard(request.getWard());
        }
        if (request.getDetail() != null) {
            address.setDetail(request.getDetail());
        }

        address = addressRepository.save(address);
        // Clear cache for user addresses
        String cacheKey = "user_addresses:" + address.getUser().getId();
        redisTemplate.delete(cacheKey);
        return addressMapper.toResponse(address);
    }

    @Override
    public void deleteAddress(Long addressId) {
        if (addressId == null || addressId <= 0) {
            throw new BadRequestException("Invalid address ID");
        }

        Address address = addressRepository.findById(addressId)
                .orElseThrow(() -> new ResourceNotFoundException("Address not found with id: " + addressId));

        addressRepository.deleteById(addressId);

        // Clear cache for user addresses
        String cacheKey = "user_addresses:" + address.getUser().getId();
        redisTemplate.delete(cacheKey);
    }
}