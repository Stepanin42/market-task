package com.market.exception.order;

public class OrderProductNotFoundException extends OrderException {
    public OrderProductNotFoundException(Long productId) {
        super("Товара с id "+ productId +" нет в звказе");
    }
}
