package com.market.exception.order;

public class OrderNotFoundException extends OrderException {
    public OrderNotFoundException(Long id) {
        super("Заказ с id " + id +" отсутсвует");
    }
}
