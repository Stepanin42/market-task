package com.market.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.util.List;

public class OrderCreateDTO {

    @NotNull
    public String customerPhone;

    @NotNull
    public String deliveryAddress;

    @NotEmpty
    public List<OrderProductDTO> orderProducts;

    public OrderCreateDTO() {}
    public OrderCreateDTO(String customerPhone, String deliveryAddress, List<OrderProductDTO> orderProducts) {
        this.customerPhone = customerPhone;
        this.deliveryAddress = deliveryAddress;
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
