package com.market.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;

import java.math.BigDecimal;

public class OrderProductResponseDTO {
    @NotNull
    public Long id;

    @NotNull
    public Long orderId;

    @NotNull
    public Long productId;

    @NotNull
    @Positive
    public int amount;

    @PositiveOrZero
    public BigDecimal priceAtOrder;

    @PositiveOrZero
    public BigDecimal totalPrice;

    @NotNull
    public String productName;

    public OrderProductResponseDTO() {}
    public OrderProductResponseDTO(Long id, Long orderId, Long productId, int amount, BigDecimal price, BigDecimal totalPrice, String productName) {
        this.id = id;
        this.orderId = orderId;
        this.productId = productId;
        this.amount = amount;
        this.priceAtOrder = price;
        this.totalPrice = totalPrice;
        this.productName = productName;
    }
}
