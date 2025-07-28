package com.market.storage.repository;

import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;
import com.market.storage.model.Category;

import java.util.List;
import java.util.Optional;

@ApplicationScoped
public class CategoryRepository implements PanacheRepository<Category> {
    public List<Category> listAllName(){
        return list("name");
    }

    public Optional<Category> findByName(String name){
        return find("name ", name).firstResultOptional();
    }
}
