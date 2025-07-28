package com.market.dto;

import com.market.enums.OrderStatus;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.util.List;

public class OrderUpdateDTO {
    @NotNull
    public String customerPhone;

    @NotNull
    public String deliveryAddress;

    @NotEmpty
    public List<OrderUpdateDTO.OrderProductDTO> orderProducts;

    @Enumerated(EnumType.STRING)
    public OrderStatus status;

    public OrderUpdateDTO() {}
    public OrderUpdateDTO(String customerPhone, String deliveryAddress, OrderStatus status, List<OrderUpdateDTO.OrderProductDTO> orderProducts) {
        this.customerPhone = customerPhone;
        this.deliveryAddress = deliveryAddress;
        this.status = status;
        this.orderProducts = orderProducts;
    }

    public static class OrderProductDTO {
        @NotNull
        public Long productId;

        @NotNull
        @Positive
        public int amount;

        public OrderProductDTO() {}
        public OrderProductDTO(Long productId, int amount) {
            this.productId = productId;
            this.amount = amount;
        }
    }
}
