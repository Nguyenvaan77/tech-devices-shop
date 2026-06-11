package com.example.web.mapper;

import com.example.web.dto.cart.response.CartResponse;
import com.example.web.dto.cart.bycache.CartByCacheResponse;
import com.example.web.dto.cart.bycache.CartItemByCacheResponse;
import com.example.web.dto.cart.response.CartItemResponse;
import com.example.web.entity.Cart;
import com.example.web.entity.CartItem;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface CartMapper {

    CartResponse toResponse(Cart entity);

    
    @Mapping(source = "product.id", target = "productId")
    @Mapping(source = "product.name", target = "productName")
    @Mapping(source = "product.price", target = "price")
    @Mapping(target = "subtotal", expression = "java(java.math.BigDecimal.valueOf(entity.getQuantity()).multiply(entity.getProduct().getPrice()))")
    CartItemResponse toItemResponse(CartItem entity);

    @Mapping(source = "product.id", target = "product_id")
    @Mapping(source = "quantity",  target = "quantity")
    CartItemByCacheResponse toCartItemByCacheResponse(CartItem cartItem);

    @Mapping(source = "items", target = "listCartItems")
    @Mapping(source = "user.id", target = "userId")
    CartByCacheResponse toCartByCacheResponse(Cart cart);
}