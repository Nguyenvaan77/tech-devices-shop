package com.example.web.mapper;

import com.example.web.dto.cart.response.CartResponse;
import com.example.web.dto.cart.response.CartItemResponse;
import com.example.web.entity.Cart;
import com.example.web.entity.CartItem;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface CartMapper {

    @Mapping(source = "id", target = "cartId")
    CartResponse toResponse(Cart entity);

    @Mapping(source = "product.id", target = "productId")
    @Mapping(source = "product.name", target = "productName")
    @Mapping(source = "product.price", target = "price")
    CartItemResponse toItemResponse(CartItem entity);
}