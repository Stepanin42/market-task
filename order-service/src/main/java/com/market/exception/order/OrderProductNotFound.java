package com.market.exception.order;

public class OrderProductNotFound extends OrderException {
    public OrderProductNotFound(Long productId) {
        super("Товар с id " + productId + " не найден в заказе");
    }
}
