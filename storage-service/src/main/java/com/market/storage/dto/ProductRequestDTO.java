package com.market.storage.dto;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

import java.math.BigDecimal;

public class ProductRequestDTO {

    @NotBlank
    public String name;

    @NotNull
    public Long categoryId;

    @PositiveOrZero
    public int amount;


    public String description;

    @NotNull
    public BigDecimal price;

    public ProductRequestDTO() {}
    public ProductRequestDTO(String name, Long categoryId, int amount, String description, BigDecimal price) {
        this.name = name;
        this.categoryId = categoryId;
        this.amount = amount;
        this.description = description;
        this.price = price;

    }
    public String getName() {return name;}
    public void setName(String name) {this.name = name;}
    public Long getCategoryId() {return categoryId;}
    public void setCategoryId(Long categoryId) {this.categoryId = categoryId;}
    public int getAmount() {return amount;}
    public void setAmount(int amount) {this.amount = amount;}
    public String getDescription() {return description;}
    public void setDescription(String description) {this.description = description;}
    public BigDecimal getPrice() {return price;}
    public void setPrice(BigDecimal price) {this.price = price;}

}
