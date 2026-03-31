package com.example.web.mapper;

import com.example.web.dto.sale.request.CreateSaleRequest;
import com.example.web.dto.sale.response.SaleResponse;
import com.example.web.entity.Sale;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface SaleMapper {
    SaleResponse toResponse(Sale entity);

    Sale toEntity(CreateSaleRequest dto);
}
