package com.market.storage.exception.product;

public class InsufficientStockException extends ProductException {
    public InsufficientStockException(Long productId, int available, int requested) {
        super(String.format("Недостаточно товара с id %d (в наличии: %d, запрошено %d)",
                productId, available, requested));
    }
}
