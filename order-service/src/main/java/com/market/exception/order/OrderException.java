package com.market.exception.order;

public class OrderException extends RuntimeException{
    public OrderException(String errorMessage) {
        super(errorMessage);
    }

}
