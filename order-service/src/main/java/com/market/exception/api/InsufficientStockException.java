package com.market.exception.api;

public class InsufficientStockException extends ApiException {
    public InsufficientStockException(Long id) {
        super(String.format("Недостаточное количество продукта с ID " + id), 400);
    }
}
