package com.market.service;

import com.market.client.StorageClient;
import com.market.dto.*;
import com.market.exception.order.*;
import com.market.dto.*;
import com.market.exception.order.*;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.NotFoundException;
import com.market.model.Order;
import com.market.model.OrderProduct;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import com.market.repository.OrderProductRepository;
import com.market.repository.OrderRepository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ApplicationScoped
public class OrderService {
    private static final Logger logger = LoggerFactory.getLogger(OrderService.class);

    @Inject
    OrderRepository orderRepository;

    @Inject
    @RestClient
    StorageClient storageClient;

    @Inject
    OrderProductRepository orderProductRepository;

    public List<OrderResponseDTO> findAll() {
        logger.info("Получение списка всех заказов");
        List<OrderResponseDTO> orders = orderRepository.listAll().stream()
                .map(this::toDTO)
                .toList();
        logger.debug("Найдено {} заказов", orders.size());
        return orders;
    }

    public OrderResponseDTO findById(Long id) {
        logger.info("Поиск заказа по ID: {}", id);
        Order order= orderRepository.findByIdOptional(id)
                .orElseThrow(() -> {
                    logger.error("Заказ с ID {} не найден", id);
                    return new OrderNotFoundException(id);});
        logger.debug("Найден заказ: {}", order);
        return toDTO(order);
    }

    public List<OrderResponseDTO> findByCustomerPhone(String phone) {
        logger.info("Поиск заказов по телефону клиента: {}", phone);
        List<OrderResponseDTO> orders = orderRepository.findByCustomerPhone(phone).stream()
                .map(this::toDTO)
                .toList();
        logger.debug("Найдено {} заказов для телефона {}", orders.size(), phone);
        return orders;
    }

    public List<OrderResponseDTO>  findByDeliveryAddress(String deliveryAddress) {
        logger.info("Поиск заказов по адресу доставки: {}", deliveryAddress);
        List<OrderResponseDTO> orders = orderRepository.findByDeliveryAddress(deliveryAddress).stream()
                .map(this::toDTO)
                .toList();
        logger.debug("Найдено {} заказов для адреса {}", orders.size(), deliveryAddress);
        return orders;
    }

    public List<OrderResponseDTO>  findByProductId(Long productId) {
        logger.info("Поиск заказов по ID товара: {}", productId);
        List<OrderResponseDTO> orders = orderRepository.findByProductId(productId).stream()
                .map(this::toDTO)
                .toList();
        logger.debug("Найдено {} заказов с товаром ID {}", orders.size(), productId);
        return orders;
    }

    public List<OrderResponseDTO>  findRecentOrders(int limit) {
        logger.info("Получение {} последних заказов", limit);
        List<OrderResponseDTO> orders = orderRepository.findRecentOrders(limit).stream()
                .map(this::toDTO)
                .toList();
        logger.debug("Возвращено {} последних заказов", orders.size());
        return orders;
    }

    @Transactional
    public OrderResponseDTO createOrder(OrderCreateDTO createOrder) {
        logger.info("Создание нового заказа: {}", createOrder);

        Order order = new Order();
        order.setCustomerPhone(createOrder.customerPhone);
        order.setDeliveryAddress(createOrder.deliveryAddress);
        order.setCreateDate(LocalDateTime.now());
        order.setTotalPrice(BigDecimal.ZERO);

        BigDecimal totalPrice = BigDecimal.ZERO;

        for(OrderCreateDTO.OrderProductDTO productOrder: createOrder.orderProducts){
            logger.debug("Обработка товара в заказе: ID {}, Количество {}", productOrder.productId, productOrder.amount);

            ProductDTO product;
            try{
                product = storageClient.getProduct(productOrder.productId);
                logger.debug("Получена информация о товаре ID {}: {}", productOrder.productId, product);
            }
            catch(NotFoundException e){
                logger.error("Товар с ID {} не найден", productOrder.productId);
                throw new OrderProductException("Товар с id " + productOrder.productId + " не найден", e);
            }

            if(!storageClient.hasStock(productOrder.productId,productOrder.amount)){
                logger.error("Недостаточно товара ID {}: (имеется {}, запрошено {})",
                        productOrder.productId,  product.amount , productOrder.amount) ;
                throw new OrderStockException(productOrder.productId, product.amount, productOrder.amount );
            }

            storageClient.orderProcess(productOrder.productId, productOrder.amount);
            logger.debug("Товар ID {} зарезервирован в количестве {}", productOrder.productId, productOrder.amount);

            //Создаем новый товар в заказе
            OrderProduct orderProduct = new OrderProduct(
                    order,
                    productOrder.productId,
                    productOrder.amount,
                    product.price,
                    product.price.multiply(new BigDecimal(productOrder.amount)),
                    product.name
            );
            totalPrice= totalPrice.add(orderProduct.getTotalPrice());
            order.addOrderProduct(orderProduct);

        }
        order.setTotalPrice(totalPrice);
        orderRepository.persist(order);
        logger.info("Создан новый заказ ID: {}, Общая сумма: {}", order.getId(), order.getTotalPrice());

        return toDTO(order);
    }

    @Transactional
    public OrderResponseDTO updateOrder(Long id, OrderUpdateDTO updateOrder) {
        logger.info("Обновление заказа ID: {}, Новые данные: {}", id, updateOrder);
        Order oldOrder = orderRepository.findByIdOptional(id)
                .orElseThrow(()->{
                    logger.error("Заказ с ID {} не найден", id);
                    return new OrderNotFoundException(id);});

        oldOrder.setCustomerPhone(updateOrder.customerPhone);
        oldOrder.setDeliveryAddress(updateOrder.deliveryAddress);
        oldOrder.setStatus(updateOrder.status);

        logger.debug("Возврат товаров предыдущего заказа");
        //Возвращаем товары
        for(OrderProduct orderProduct : oldOrder.getOrderProducts()){
            storageClient.orderProcessCancel(orderProduct.getProductId(),orderProduct.getAmount());
            logger.debug("Товар ID {} возвращен в количестве {}", orderProduct.getProductId(), orderProduct.getAmount());
        }

        oldOrder.getOrderProducts().clear();
        BigDecimal totalPrice = BigDecimal.ZERO;

        for(OrderUpdateDTO.OrderProductDTO productOrder: updateOrder.orderProducts){
            logger.debug("Добавление товара в заказ: ID {}, Количество {}", productOrder.productId, productOrder.amount);

            ProductDTO product;
            try{
                product = storageClient.getProduct(productOrder.productId);
            }
            catch(NotFoundException e){
                logger.error("Товар с ID {} не найден", productOrder.productId);
                throw new OrderProductException("Товар с id " + productOrder.productId + " не найден", e);
            }

            boolean hasStock = storageClient.hasStock(productOrder.productId,productOrder.amount);
            if(!hasStock){
                logger.error("Недостаточно товара ID {}: (имеется {}, запрошено {})",
                        productOrder.productId,  product.amount , productOrder.amount);
                throw new OrderStockException(productOrder.productId, product.amount, productOrder.amount );
            }

            storageClient.orderProcess(productOrder.productId, productOrder.amount);
            logger.debug("Товар ID {} зарезервирован в количестве {}", productOrder.productId, productOrder.amount);
            //Создаем новый товар в заказе
            OrderProduct orderProduct = new OrderProduct(
                    oldOrder,
                    productOrder.productId,
                    productOrder.amount,
                    product.price,
                    product.price.multiply(new BigDecimal(productOrder.amount)),
                    product.name
            );
            totalPrice= totalPrice.add(orderProduct.getTotalPrice());
            oldOrder.addOrderProduct(orderProduct);
        }

        oldOrder.setTotalPrice(totalPrice);
        logger.info("Заказ ID {} успешно обновлен. Общая сумма: {}", id, oldOrder.getTotalPrice());

        return toDTO(oldOrder);
    }

    @Transactional
    public OrderResponseDTO updateOrderInformation(Long id, OrderUpdateInfoDTO updateOrder) {
        Order oldOrder = orderRepository.findByIdOptional(id)
                .orElseThrow(()->new OrderNotFoundException(id));

        oldOrder.setCustomerPhone(updateOrder.customerPhone);
        oldOrder.setDeliveryAddress(updateOrder.deliveryAddress);
        oldOrder.setStatus(updateOrder.status);

        return toDTO(oldOrder);
    }

    @Transactional
    public void deleteOrder(Long id) {
        Order order = orderRepository.findByIdOptional(id)
                .orElseThrow(()->new OrderNotFoundException(id));
        //Возвращаем товары на склад
        for(OrderProduct orderProduct : order.getOrderProducts()){
            storageClient.orderProcessCancel(orderProduct.getProductId(),orderProduct.getAmount());
        }

        orderRepository.deleteById(id);

    }

    @Transactional
    public OrderResponseDTO addProduct(Long idOrder, OrderUpdateDTO.OrderProductDTO addProductOrder) {
        Order order = orderRepository.findByIdOptional(idOrder)
                .orElseThrow(()-> new OrderNotFoundException(idOrder));

        ProductDTO product;
        try{
            product = storageClient.getProduct(addProductOrder.productId);
        }
        catch(NotFoundException e){
            throw new OrderProductException("Товар с id " + addProductOrder.productId + " не найден", e);
        }

        //Проверка на то что товар уже есть в заказе
        boolean productExist = order.getOrderProducts().stream()
                .anyMatch(p->p.getProductId().equals(addProductOrder.productId));
        if(productExist){
            throw new OrderProductAlreadyExistsException(addProductOrder.productId);
        }

        if(!storageClient.hasStock(addProductOrder.productId,addProductOrder.amount)){
            throw new OrderStockException(addProductOrder.productId, product.amount, addProductOrder.amount );
        }

        storageClient.orderProcess(addProductOrder.productId, addProductOrder.amount);

        OrderProduct orderProduct = new OrderProduct(
                order,
                addProductOrder.productId,
                addProductOrder.amount,
                product.price,
                product.price.multiply(new BigDecimal(addProductOrder.amount)),
                product.name
        );

        order.addOrderProduct(orderProduct);
        order.setTotalPrice(order.getTotalPrice().add(orderProduct.getTotalPrice()));
        return toDTO(order);
    }

    @Transactional
    public OrderResponseDTO deleteProduct(Long idOrder, Long idOrderProduct) {
        Order order = orderRepository.findByIdOptional(idOrder)
                .orElseThrow(()-> new OrderNotFoundException(idOrder));

        //Проверяем есть ли товара в заказе
        Optional<OrderProduct> productExists = order.getOrderProducts().stream()
                .filter(p-> p.getProductId().equals(idOrderProduct))
                .findFirst();

        if(productExists.isEmpty()){
            throw new OrderProductNotFoundException(idOrderProduct);
        }

        storageClient.orderProcessCancel(productExists.get().getProductId(),productExists.get().getAmount());

        order.setTotalPrice(order.getTotalPrice().subtract(productExists.get().getTotalPrice()));

        order.deleteOrderProduct(productExists.get());
        return toDTO(order);
    }

    @Transactional
    public OrderResponseDTO updateProductOrderAmount(Long idOrder, Long productId, int amount ) {
        Order order = orderRepository.findByIdOptional(idOrder)
                .orElseThrow(()-> new OrderNotFoundException(idOrder));

        Optional <OrderProduct> orderProduct = order.getOrderProducts().stream()
                .filter(p->p.getProductId().equals(productId)).findFirst();

        if(orderProduct.isEmpty()){
            throw new OrderProductNotFoundException(productId);
        }



        int changeAmount = amount - orderProduct.get().getAmount();

        if(changeAmount < 0){
            storageClient.orderProcessCancel(orderProduct.get().getProductId(), changeAmount * -1);
        }
        else if(changeAmount > 0){
            if(!storageClient.hasStock(orderProduct.get().getProductId(),changeAmount)){
                throw new OrderStockExceptionNoProductAmount(productId, changeAmount );
            }
            storageClient.orderProcess(orderProduct.get().getProductId(),changeAmount);
        }
        else {
            return toDTO(order);
        }

        BigDecimal totalPrice=order.getTotalPrice().subtract(orderProduct.get().getTotalPrice());

        orderProduct.get().setAmount(amount);
        orderProduct.get().setTotalPrice(orderProduct.get().getPriceAtOrder().multiply(new BigDecimal(amount)));

        order.setTotalPrice(totalPrice.add(orderProduct.get().getTotalPrice()));

        return toDTO(order);
    }

    private OrderResponseDTO toDTO(Order order) {
        return new OrderResponseDTO(
                order.getId(),
                order.getCustomerPhone(),
                order.getTotalPrice(),
                order.getCreateDate(),
                order.getDeliveryAddress(),
                order.getOrderProducts().stream().map(this::toProductDTO).toList(),
                order.getStatus()
        );
    }

    private OrderResponseDTO.OrderProductDTO toProductDTO(OrderProduct orderProduct) {
        return new OrderResponseDTO.OrderProductDTO(
                orderProduct.getProductId(),
                orderProduct.getAmount(),
                orderProduct.getPriceAtOrder(),
                orderProduct.getTotalPrice(),
                orderProduct.getProductName()
        );
    }
}
