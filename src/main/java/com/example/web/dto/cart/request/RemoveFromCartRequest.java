package com.example.web.dto.cart.request;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class RemoveFromCartRequest {
    public Long productId;
    public Integer quantity;    
}
