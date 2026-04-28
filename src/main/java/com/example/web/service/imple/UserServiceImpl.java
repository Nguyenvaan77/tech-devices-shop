package com.example.web.service.imple;

import com.example.web.dto.auth.LoginRequest;
import com.example.web.dto.auth.RegisterRequest;
import com.example.web.dto.user.request.UpdateUserRequest;
import com.example.web.dto.user.response.UserResponse;
import com.example.web.entity.Address;
import com.example.web.entity.User;
import com.example.web.exception.BadRequestException;
import com.example.web.exception.ConflictException;
import com.example.web.exception.ResourceNotFoundException;
import com.example.web.exception.redis.RedisException;
import com.example.web.mapper.UserMapper;
import com.example.web.repository.AddressRepository;
import com.example.web.repository.UserRepository;
import com.example.web.service.inter.UserService;

import ch.qos.logback.classic.Logger;
import lombok.RequiredArgsConstructor;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final String USER_CACHE_KEY_WITH_ID = "users:id:";
    private final String USER_CACHE_KEY_WITH_EMAIL = "users:email:";

    private final UserRepository userRepository;
    private final AddressRepository addressRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final RedisTemplate<String, Object> redisTemplate;

    

    @Override
    public UserResponse getUserByEmail(String email) {
        String cacheKeyWithEmail = USER_CACHE_KEY_WITH_EMAIL + email;
        
        try {
            Long userId = (Long) redisTemplate.opsForValue().get(cacheKeyWithEmail);

            if(userId != null) {
                return getUser(userId);
            }
        } catch (RuntimeException e) {
            System.err.println("Redis is unconnected");
        }

        User user = userRepository.findByEmail(email)
        .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        UserResponse response = userMapper.toResponse(user);

        try {
            redisTemplate.opsForValue().set(cacheKeyWithEmail, user.getId());
            redisTemplate.opsForValue().set(USER_CACHE_KEY_WITH_ID + user.getId(), response);
        } catch(RuntimeException e) {
            System.err.println("Cannot write cache");
        }

        return response;
    }

    @Override
    public UserResponse register(RegisterRequest request) {
        if (request == null) {
            throw new BadRequestException("Register request cannot be null");
        }

        if (request.getEmail() == null || request.getEmail().isBlank()) {
            throw new BadRequestException("Email is required");
        }

        if (request.getPassword() == null || request.getPassword().isBlank()) {
            throw new BadRequestException("Password is required");
        }

        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new ConflictException("Email already registered");
        }

        User user = userMapper.toEntity(request);
        user.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        user = userRepository.save(user);

        return userMapper.toResponse(user);
    }

    @Override
    @Transactional(readOnly = true)
    public String login(LoginRequest request) {
        if (request == null) {
            throw new BadRequestException("Login request cannot be null");
        }

        if (request.getEmail() == null || request.getEmail().isBlank()) {
            throw new BadRequestException("Email is required");
        }

        if (request.getPassword() == null || request.getPassword().isBlank()) {
            throw new BadRequestException("Password is required");
        }

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + request.getEmail()));

        if (!passwordEncoder.matches(request.getPassword(), user.getPasswordHash())) {
            throw new BadRequestException("Invalid password");
        }

        return "LOGIN_SUCCESS";
    }

    @Override
    @Transactional(readOnly = true)
    public UserResponse getUser(Long id) {
        if (id == null || id <= 0) {
            throw new BadRequestException("Invalid user ID");
        }

        String cacheKey = USER_CACHE_KEY_WITH_ID + id;

        try {    
            LinkedHashMap linkedHashMap = (LinkedHashMap) redisTemplate.opsForValue().get(cacheKey);
            UserResponse cachedUser = null;
            if (linkedHashMap != null) {
                cachedUser = new UserResponse();
                cachedUser.setId(((Number) linkedHashMap.get("id")).longValue());
                cachedUser.setEmail((String) linkedHashMap.get("email"));
                cachedUser.setFullName((String) linkedHashMap.get("fullName"));
                cachedUser.setPhone((String) linkedHashMap.get("phone"));
                // cachedUser.setRole((String) linkedHashMap.get("role"));
                // Note: Address and other nested objects would require additional mapping
            }

            if (cachedUser != null) {
                return cachedUser;
            }
        } catch (RuntimeException e) {
            System.out.println("Redis is unconnected");
        }

        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));

        UserResponse response = userMapper.toResponse(user);

        try {
            redisTemplate.opsForValue().set(cacheKey, response, Duration.ofMinutes(10));
        } catch (RuntimeException e) {
            System.out.println("Redis can not write");
        }
        
        return response;
    }

    @Override
    @Transactional(readOnly = true)
    public UserResponse getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new BadRequestException("User not authenticated");
        }

        String email = authentication.getName();

        String cacheKeyWithEmail = USER_CACHE_KEY_WITH_EMAIL + email;

        User user = null;

        try {
            Long userId =(Long) redisTemplate.opsForValue().get(cacheKeyWithEmail);
            if (userId != null) {
                return getUser(userId);
            }
        } catch (RuntimeException exception) {
            System.err.println("redis unvaiable");
        }
        
        user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + email));

        try {
            redisTemplate.opsForValue().set(cacheKeyWithEmail, user.getId(), Duration.ofMinutes(10));
        } catch (RuntimeException e) {
            System.out.println("Redis can not write");
        }

        return userMapper.toResponse(user);
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserResponse> getAllUsers() {
        List<UserResponse> users = userRepository.findAll()
                .stream()
                .map(userMapper::toResponse)
                .toList();

        return users;
    }

    @Override
    public UserResponse updateUser(Long id, UpdateUserRequest request) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));

        if (request.getFullName() != null) {
            user.setFullName(request.getFullName());
        }

        if (request.getPhone() != null) {
            user.setPhone(request.getPhone());
        }

        if (request.getAddressId() != null) {
            Address address = addressRepository.findById(request.getAddressId())
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Address not found with id: " + request.getAddressId()));
            user.setAddress(address);
        }

        User updated = userRepository.save(user);
        UserResponse response = userMapper.toResponse(updated);

        try {
            String cacheKeyWithId = USER_CACHE_KEY_WITH_ID + id;
            redisTemplate.opsForValue().set(cacheKeyWithId, response, Duration.ofMinutes(10));

            String cacheKeyWithEmail = USER_CACHE_KEY_WITH_EMAIL + updated.getEmail();
            redisTemplate.opsForValue().set(cacheKeyWithEmail, updated.getId(), Duration.ofMinutes(10));
            
        } catch (RedisException e) {
            System.out.println("Redis can't write");
        } catch (RuntimeException e) {
            System.out.println("Redis can't write");   
        }
        return response;
    }

    @Override
    public void deleteUser(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));
        user.setIsDeleted(true);
        userRepository.save(user);
        String cacheKeyWithId = USER_CACHE_KEY_WITH_ID + id;
        String cacheKeyWithEmail = USER_CACHE_KEY_WITH_EMAIL + user.getEmail();
        try {
        redisTemplate.delete(cacheKeyWithId);
        redisTemplate.delete(cacheKeyWithEmail);
        }catch (RedisException e) {
            System.out.println("Redis can't delete this element");
        } catch (RuntimeException e) {
            System.out.println("Redis can't delete this element");   
        }
    }

    @Override
    public Boolean existsById(Long userId) {
        return userRepository.existsById(userId);
    }

    @Override
    public Boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    
}
