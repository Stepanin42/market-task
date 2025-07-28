package com.market.exception.api;

public class ProductNotFoundException extends ApiException{
    public ProductNotFoundException(Long id) {
        super("Продукт с "+ id + " не найден", 404);
    }
}
