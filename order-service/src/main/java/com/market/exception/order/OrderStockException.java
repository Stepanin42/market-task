package com.market.exception.order;

public class OrderStockException extends OrderException{
    public OrderStockException(Long productId, int available, int requested) {
        super(String.format("Недостаточно товара с ID %d (в наличии: %d, запрошено: %d)",
                productId, available, requested));
    }
}
