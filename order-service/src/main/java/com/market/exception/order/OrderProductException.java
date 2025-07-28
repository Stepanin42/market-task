package com.market.exception.order;

public class OrderProductException extends OrderException {
    public OrderProductException(String errorMessage, Throwable err) {
        super(errorMessage);
    }
}
