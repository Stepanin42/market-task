package com.market.exception.order;

public class OrderStockExceptionNoProductAmount extends OrderException {
    public OrderStockExceptionNoProductAmount(Long productId,  int requested) {
        super(String.format("Недостаточно товара с ID %d ( запрошено: %d)",
                productId, requested));
    }
}
