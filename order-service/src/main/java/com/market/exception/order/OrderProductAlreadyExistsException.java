package com.market.exception.order;

public class OrderProductAlreadyExistsException extends OrderException {
    public OrderProductAlreadyExistsException(Long productId) {
        super("Товар с id "+productId + " уже есть в заказе");
    }
}
