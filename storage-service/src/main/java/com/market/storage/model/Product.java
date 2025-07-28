package com.market.storage.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

import java.math.BigDecimal;

@Entity
@Table(name = "products")
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Column(nullable = false, length = 150)
    private String name;

    @ManyToOne
    @JoinColumn(name = "category")
    private Category category;

    @PositiveOrZero
    @Column(nullable = false)
    private int amount=0;

    private String description;

    @NotNull
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal price;

    public Product() {}

    public Product(String name, Category category, int amount, String description, BigDecimal price) {
        this.name = name;
        this.category = category;
        this.amount = amount;
        this.description = description;
        this.price = price;
    }

    public Long getId() {return id;}
    public void setId(Long id) {this.id = id;}//?
    public String getName() {return name;}
    public void setName(String name) {this.name = name;}
    public Category getCategory() {return category;}
    public void setCategory(Category category) {this.category = category;}
    public int getAmount() {return amount;}
    public void setAmount(int amount) {this.amount = amount;}
    public String getDescription() {return description;}
    public void setDescription(String description) {this.description = description;}
    public BigDecimal getPrice() {return price;}
    public void setPrice(BigDecimal price) {this.price = price;}

}
