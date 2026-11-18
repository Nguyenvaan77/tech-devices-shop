package com.example.web.service.imple;

import com.example.web.dto.sale.request.CreateSaleRequest;
import com.example.web.dto.sale.response.SaleResponse;
import com.example.web.entity.Sale;
import com.example.web.exception.ResourceNotFoundException;
import com.example.web.mapper.SaleMapper;
import com.example.web.repository.SaleRepository;
import com.example.web.service.inter.SaleService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class SaleServiceImpl implements SaleService {

    private final SaleRepository saleRepository;
    private final SaleMapper saleMapper;

    @Override
    public SaleResponse create(CreateSaleRequest request) {
        Sale sale = saleMapper.toEntity(request);
        Sale saved = saleRepository.save(sale);
        return saleMapper.toResponse(saved);
    }

    @Override
    public List<SaleResponse> getAll() {
        return saleRepository.findAll()
                .stream()
                .map(saleMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public SaleResponse getById(Long id) {
        Sale sale = saleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Sale not found with id: " + id));
        return saleMapper.toResponse(sale);
    }
}
