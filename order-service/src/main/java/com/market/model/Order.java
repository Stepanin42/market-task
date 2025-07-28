package com.market.model;

import com.market.enums.OrderStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;


import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name="orders")
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Column(name="customer_phone", length=15)
    private String customerPhone;

    @NotNull
    @PositiveOrZero
    @Column(name="total_price", precision=10, scale=2)
    private BigDecimal totalPrice;

    @Column(name="create_date")
    private LocalDateTime createDate;

    @NotNull
    @Column(name="delivery_address")
    private String deliveryAddress;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval=true)
    private List<OrderProduct> orderProducts= new ArrayList<>();

    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    @Column(columnDefinition = "order_status")
    private OrderStatus status;


    @PrePersist
    protected void onCreate() {
        this.createDate = LocalDateTime.now();
        this.status = OrderStatus.CREATED;
    }

    public Order() {}
    public Order(String customerPhone, BigDecimal totalPrice, LocalDateTime createDate, String deliveryAddress) {
        this.customerPhone = customerPhone;
        this.totalPrice = totalPrice;
        this.createDate = createDate;
        this.deliveryAddress = deliveryAddress;
    }

    public Long getId() {return id;}
    public void setId(Long id) {this.id = id;}
    public String getCustomerPhone() {return customerPhone;}
    public void setCustomerPhone(String customerPhone) {this.customerPhone = customerPhone;}
    public BigDecimal getTotalPrice() {return totalPrice;}
    public void setTotalPrice(BigDecimal totalPrice) {this.totalPrice = totalPrice;}
    public LocalDateTime getCreateDate() {return createDate;}
    public void setCreateDate(LocalDateTime createDate) {this.createDate = createDate;}
    public String getDeliveryAddress() {return deliveryAddress;}
    public void setDeliveryAddress(String deliveryAddress) {this.deliveryAddress = deliveryAddress;}
    public List<OrderProduct> getOrderProducts() {return orderProducts;}
    public void setOrderProducts(List<OrderProduct> orderProducts) {this.orderProducts = orderProducts;}
    public OrderStatus getStatus() {return status;}
    public void setStatus(OrderStatus status) {this.status = status;}

    public void addOrderProduct(OrderProduct orderProduct) {orderProducts.add(orderProduct);}
    public void deleteOrderProduct(OrderProduct orderProduct) {orderProducts.remove(orderProduct);}
}
