package com.example.web.exception;

/**
 * Exception thrown when product stock is insufficient for requested quantity
 */
public class InsufficientStockException extends RuntimeException {
    
    private Long productId;
    private Integer requestedQuantity;
    private Integer availableQuantity;

    public InsufficientStockException(String message) {
        super(message);
    }

    public InsufficientStockException(String message, Long productId, Integer requested, Integer available) {
        super(message);
        this.productId = productId;
        this.requestedQuantity = requested;
        this.availableQuantity = available;
    }

    public InsufficientStockException(String message, Throwable cause) {
        super(message, cause);
    }

    public Long getProductId() {
        return productId;
    }

    public Integer getRequestedQuantity() {
        return requestedQuantity;
    }

    public Integer getAvailableQuantity() {
        return availableQuantity;
    }
}
