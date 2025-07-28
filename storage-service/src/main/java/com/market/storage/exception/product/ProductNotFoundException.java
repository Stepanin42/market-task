package com.market.storage.exception.product;

public class ProductNotFoundException extends ProductException {
    public ProductNotFoundException(Long id) {
        super("Товар с id " + id + " не найден");
    }
}
