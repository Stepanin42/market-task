package com.market.storage.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

import java.math.BigDecimal;


public class ProductResponseDTO {
    @JsonProperty
    public Long id;

    @NotBlank
    @JsonProperty
    public String name;

    @NotNull
    @JsonProperty
    public CategoryDTO category;

    @PositiveOrZero
    @JsonProperty
    public int amount;

    @JsonProperty
    public String description;

    @NotNull
    @JsonProperty
    public BigDecimal price;

    public ProductResponseDTO() {}
    public ProductResponseDTO(Long id, String name, CategoryDTO category, int amount, String description, BigDecimal price) {
        this.id = id;
        this.name = name;
        this.category = category;
        this.amount = amount;
        this.description = description;
        this.price = price;
    }

    public record CategoryDTO(Long id, String name){}

}
