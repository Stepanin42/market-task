package com.market.repository;

import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import com.market.model.Order;

import java.math.BigDecimal;
import java.util.List;

@ApplicationScoped
public class OrderRepository implements PanacheRepository<Order> {
    @PersistenceContext
    public EntityManager em;

    public List<Order> findByCustomerPhone(String phone) {
        return find("customerPhone", phone).list();
    }

    public List<Order> findByDeliveryAddress(String deliveryAddress) {
        return find("deliveryAddress", deliveryAddress).list();
    }

    public List<Order> findByProductId(Long productId) {
        return find("SELECT DISTINCT o FROM Order o JOIN o.orderProducts op WHERE op.productId= ?1", productId).list();
    }

    public List<Order> findRecentOrders(int limit) {
        return find("ORDER BY createDate DESC").range(0,limit-1).list();
    }

    public void updateTotalPrice(Long orderId) {
        BigDecimal totalPrice= (BigDecimal) em.createQuery(
                "SELECT SUM(op.amount*op.priceAtOrder) FROM OrderProduct op WHERE op.order.id=:orderId")
                .setParameter("orderId", orderId)
                .getSingleResult();
        if(totalPrice!=null)
            update("totalPrice =?1 WHERE id=?2", totalPrice, orderId);
    }


}
