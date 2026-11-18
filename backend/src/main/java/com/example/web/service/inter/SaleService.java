package com.example.web.service.inter;

import com.example.web.dto.sale.request.CreateSaleRequest;
import com.example.web.dto.sale.response.SaleResponse;

import java.util.List;

public interface SaleService {
    SaleResponse create(CreateSaleRequest request);

    List<SaleResponse> getAll();

    SaleResponse getById(Long id);
}
