package com.market.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;

import java.math.BigDecimal;

public class OrderProductCreateUpdateDTO {
    @NotNull
    public Long orderId;

    @NotNull
    public Long productId;

    @NotNull
    @Positive
    public int amount;

    @PositiveOrZero
    public BigDecimal priceAtOrder;

    public OrderProductCreateUpdateDTO() {}
    public OrderProductCreateUpdateDTO(Long orderId, Long productId, int amount, BigDecimal price) {
        this.orderId = orderId;
        this.productId = productId;
        this.amount = amount;
        this.priceAtOrder = price;
    }
}
