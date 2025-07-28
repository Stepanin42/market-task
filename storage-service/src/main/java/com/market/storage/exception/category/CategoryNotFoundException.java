package com.market.storage.exception.category;

public class CategoryNotFoundException extends CategoryException {
    public CategoryNotFoundException(Long id) {
        super("Категория с id " + id +" отсутсвует");
    }

    public CategoryNotFoundException(String name) {
        super("Категория с именем " + name +" отсутсвует");
    }
}
