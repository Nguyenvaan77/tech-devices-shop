package com.example.web.mapper;

import com.example.web.dto.cart.response.CartResponse;
import com.example.web.dto.cart.response.CartItemResponse;
import com.example.web.entity.Cart;
import com.example.web.entity.CartItem;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface CartMapper {

    CartResponse toResponse(Cart entity);

    
    CartItemResponse toItemResponse(CartItem entity);
}