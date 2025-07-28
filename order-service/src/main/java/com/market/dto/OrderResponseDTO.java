package com.market.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.market.enums.OrderStatus;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public class OrderResponseDTO {

    @NotNull
    @Positive
    @JsonProperty
    public Long orderId;

    @NotNull
    @JsonProperty
    public String customerPhone;

    @NotNull
    @PositiveOrZero
    @JsonProperty
    public BigDecimal totalPrice;

    @NotNull
    @JsonProperty
    public LocalDateTime createDate;

    @NotNull
    @JsonProperty
    public String deliveryAddress;

    @JsonProperty
    public List<OrderProductDTO> orderProducts;

    @JsonProperty
    public OrderStatus status;

    public OrderResponseDTO() {}

    public OrderResponseDTO(Long orderId, String customerPhone, BigDecimal totalPrice, LocalDateTime createDate, String deliveryAddress, List<OrderProductDTO> orderProducts, OrderStatus status) {
        this.orderId = orderId;
        this.customerPhone = customerPhone;
        this.totalPrice = totalPrice;
        this.createDate = createDate;
        this.deliveryAddress = deliveryAddress;
        this.orderProducts = orderProducts;
        this.status = status;
    }

    public record OrderProductDTO (
        Long productId,
        int amount,
        BigDecimal price,
        BigDecimal totalPrice,
        String productName
    ) {}

}
