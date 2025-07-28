package com.market.storage.dto;

public class CategoryResponseDTO {
    public Long id;
    public String name;

    public CategoryResponseDTO(){}
    public CategoryResponseDTO(Long id, String name){
        this.id = id;
        this.name = name;
    }
}
