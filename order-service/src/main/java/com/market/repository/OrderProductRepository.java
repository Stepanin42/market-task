package com.market.repository;

import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import com.market.model.OrderProduct;

import java.util.List;

@ApplicationScoped
public class OrderProductRepository implements PanacheRepository<OrderProduct> {
    @PersistenceContext
    EntityManager em;

    public List<OrderProduct> findByOrderId(Long orderId) {
        return find("order.id ", orderId).list();
    }

}
