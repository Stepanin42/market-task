package com.market.dto;

import com.market.enums.OrderStatus;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.NotNull;

public class OrderUpdateInfoDTO {
    @NotNull
    public String customerPhone;

    @NotNull
    public String deliveryAddress;

    @Enumerated(EnumType.STRING)
    public OrderStatus status;

    public OrderUpdateInfoDTO() {}
    public OrderUpdateInfoDTO(String customerPhone, String deliveryAddress, OrderStatus status) {
        this.customerPhone = customerPhone;
        this.deliveryAddress = deliveryAddress;
        this.status = status;
    }
}
