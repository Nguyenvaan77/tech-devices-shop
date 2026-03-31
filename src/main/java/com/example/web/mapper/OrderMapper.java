package com.example.web.mapper;

import com.example.web.dto.order.response.OrderResponse;
import com.example.web.dto.order.response.OrderItemResponse;
import com.example.web.entity.Order;
import com.example.web.entity.OrderItem;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface OrderMapper {

    OrderResponse toResponse(Order entity);

    @Mapping(source = "productItem.id", target = "productItemId")
    @Mapping(source = "productItem.productCode", target = "productCode")
    @Mapping(source = "price", target = "price")
    OrderItemResponse toItemResponse(OrderItem entity);
}