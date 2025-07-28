package com.market.storage.repository;

import com.market.storage.model.Product;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.List;

@ApplicationScoped
public class ProductRepository implements PanacheRepository<Product> {
    public List<Product> findByCategory(Long categoryId) {
        return find("category.id = ?1", categoryId).list();
    }

    public List<Product> findAllStock(){
        return find("amount > 0").list();
    }

    public List<Product> findBySimilarName(String similarName){
        return find("LOWER(name) LIKE LOWER(?1)", "%" + similarName + "%").list();
    }

    public boolean hasStock(Long id, int amount){
        return find("id= ?1 and amount >= ?2", id, amount)
                .firstResultOptional().isPresent();
    }
}
