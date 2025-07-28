package com.market.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import jakarta.validation.constraints.PositiveOrZero;

import java.math.BigDecimal;

@Entity
@Table(name="order_products")
public class OrderProduct {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name="order_id")
    private Order order;

    @Column(name="product_id")
    private Long productId;

    @NotNull
    @Positive
    private int amount;

    @NotNull
    @PositiveOrZero
    @Column(name="price_at_order", precision=10, scale=2)
    private BigDecimal priceAtOrder;

    @NotNull
    @PositiveOrZero
    @Column(name="total_price")
    private BigDecimal totalPrice;

    @NotNull
    @Column(name="product_name")
    private String productName;

    public OrderProduct() {}
    public OrderProduct(Order order, Long productId, int amount, BigDecimal priceAtOrder, BigDecimal totalPrice, String productName) {
        this.order = order;
        this.productId = productId;
        this.amount = amount;
        this.priceAtOrder = priceAtOrder;
        this.totalPrice = totalPrice;
        this.productName = productName;
    }

    public Long getId() {return id;}
    public void setId(Long id) {this.id = id;}
    public Order getOrder() {return order;}
    public void setOrder(Order order) {this.order = order;}
    public Long getProductId() {return productId;}
    public void setProductId(Long productId) {this.productId = productId;}
    public int getAmount() {return amount;}
    public void setAmount(int amount) {this.amount = amount;}
    public BigDecimal getPriceAtOrder() {return priceAtOrder;}
    public void setPriceAtOrder(BigDecimal priceAtOrder) {this.priceAtOrder = priceAtOrder;}
    public BigDecimal getTotalPrice() {return totalPrice;}
    public void setTotalPrice(BigDecimal totalPrice) {this.totalPrice = totalPrice;}
    public String getProductName() {return productName;}
    public void setProductName(String productName) {this.productName = productName;}
}
