package com.market.service;

import com.market.client.StorageClient;
import com.market.dto.*;
import com.market.exception.order.*;
import com.market.enums.OrderStatus;
import io.quarkus.test.InjectMock;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import jakarta.ws.rs.NotFoundException;
import com.market.model.Order;
import com.market.model.OrderProduct;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import com.market.repository.OrderProductRepository;
import com.market.repository.OrderRepository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@QuarkusTest
public class OrderServiceTest {
    @Inject
    OrderService orderService;

    @InjectMock
    OrderRepository orderRepository;

    @InjectMock
    @RestClient
    StorageClient storageClient;

    @InjectMock
    OrderProductRepository orderProductRepository;

    Order orderTest;

    @BeforeEach
    public void setup() {
        OrderProduct orderProduct = new OrderProduct();
        orderProduct.setId(1L);
        orderProduct.setProductId(1L);
        orderProduct.setProductName("test");
        orderProduct.setPriceAtOrder(BigDecimal.ONE);
        orderProduct.setAmount(1);
        orderProduct.setTotalPrice(BigDecimal.ONE);

        orderTest = new Order();
        orderTest.setId(1L);
        orderTest.setCreateDate(LocalDateTime.of(1,1,1,1,1));
        orderTest.setDeliveryAddress("Test delivery address");
        orderTest.setCustomerPhone("79001234567");
        orderTest.addOrderProduct(orderProduct);
        orderTest.setTotalPrice(BigDecimal.ONE);
        orderTest.setStatus(OrderStatus.CREATED);
    }

    //Тест с получениям заказов
    @Test
    public void shouldFindAllOrders() {
        when(orderRepository.listAll())
                .thenReturn(List.of(orderTest));

        List<OrderResponseDTO> orders = orderService.findAll();

        assertNotNull(orders);
        assertEquals(orders.getFirst().orderId, orderTest.getId());
        assertEquals(orders.getFirst().createDate, orderTest.getCreateDate());
    }

    //Тесты с получением заказа по id
    @Test
    public void shouldFindOrderById() {
        when(orderRepository.findByIdOptional(1L))
                .thenReturn(Optional.of( orderTest));

        OrderResponseDTO order = orderService.findById(1L);

        assertNotNull(order);
        assertEquals(order.orderId, orderTest.getId());
        assertEquals(order.createDate, orderTest.getCreateDate());
        assertEquals(order.status, orderTest.getStatus());
    }

    @Test
    public void findOrderByIdThrowsExceptionWhenOrderNotFound() {
        when(orderRepository.findByIdOptional(99L))
                .thenReturn(Optional.empty());

        assertThrows(OrderNotFoundException.class, () -> orderService.findById(99L));
    }

    //Тест с получениям заказа по номеру телефона
    @Test
    public void shouldFindOrderByCustomerPhone() {
        when(orderRepository.findByCustomerPhone("79001234567"))
                .thenReturn(List.of(orderTest));

        List<OrderResponseDTO> orders = orderService.findByCustomerPhone("79001234567");

        assertNotNull(orders);
        assertEquals(orders.getFirst().orderId, orderTest.getId());
        assertEquals(orders.getFirst().createDate, orderTest.getCreateDate());
        assertEquals(orders.getFirst().status, orderTest.getStatus());
        assertEquals(orders.getFirst().customerPhone, orderTest.getCustomerPhone());
    }

    //Тест с получениям заказов по адресу доставки
    @Test
    public void shouldFindByDeliveryAddress() {
        when(orderRepository.findByDeliveryAddress("Test delivery address"))
                .thenReturn(List.of(orderTest));

        List<OrderResponseDTO> orders = orderService.findByDeliveryAddress("Test delivery address");

        assertNotNull(orders);
        assertEquals(orders.getFirst().orderId, orderTest.getId());
        assertEquals(orders.getFirst().createDate, orderTest.getCreateDate());
        assertEquals(orders.getFirst().status, orderTest.getStatus());
        assertEquals(orders.getFirst().deliveryAddress, orderTest.getDeliveryAddress());
    }

    //Тесты с получениям заказов по id товара
    @Test
    public void shouldFindByProductId() {
        when(orderRepository.findByProductId(1L))
                .thenReturn(List.of(orderTest));

        List<OrderResponseDTO> orders = orderService.findByProductId(1L);

        assertNotNull(orders);
        assertEquals(orders.getFirst().orderId, orderTest.getId());
        assertEquals(orders.getFirst().createDate, orderTest.getCreateDate());
        assertEquals(orders.getFirst().status, orderTest.getStatus());
        assertEquals(orders.getFirst().orderProducts.getFirst().productId(), orderTest.getOrderProducts().getFirst().getProductId());
    }

    //Тест с получениям последних заказов
    @Test
    public void shouldFindRecentOrders(){
        when(orderRepository.findRecentOrders(1))
                .thenReturn(List.of(orderTest));

        List<OrderResponseDTO> orders = orderService.findRecentOrders(1);

        assertNotNull(orders);
        assertEquals(orders.getFirst().orderId, orderTest.getId());
        assertEquals(orders.getFirst().createDate, orderTest.getCreateDate());
        assertEquals(orders.getFirst().status, orderTest.getStatus());
    }

    //Тесты с созданием заказов
    @Test
    public void shouldCreateOrder() {
        orderTest.getOrderProducts().clear();

        //when(orderRepository.persist(any(Order.class))).thenReturn(orderTest);

        ProductDTO product = new ProductDTO(
                1L,
                "Test product",
                new ProductDTO.CategoryDTO(1L, "Test category"),
                1,
                "Test Description",
                BigDecimal.ONE
        );

        when(storageClient.getProduct(1L))
                .thenReturn(product);

        when(storageClient.hasStock(1L,1))
                .thenReturn(true);

        OrderCreateDTO orderRequest = new OrderCreateDTO(
                "79001234567",
                "Test delivery address",
                List.of (new OrderCreateDTO.OrderProductDTO(1L,1))
        );

        OrderResponseDTO createdOrder=orderService.createOrder(orderRequest);

        assertNotNull(createdOrder);
        assertEquals(createdOrder.customerPhone, orderRequest.customerPhone);
        assertEquals(createdOrder.deliveryAddress, orderRequest.deliveryAddress);
        assertEquals(createdOrder.orderProducts.getFirst().productId(), orderRequest.orderProducts.getFirst().productId);
        assertEquals(createdOrder.totalPrice, BigDecimal.ONE);
        assertEquals(createdOrder.orderProducts.getFirst().amount(), 1);
    }

    @Test
    public void shouldCreateOrderThrowsExceptionWhenProductNotFound() {
        OrderCreateDTO request = new OrderCreateDTO(
                "+123456789",
                "Address",
                List.of(new OrderCreateDTO.OrderProductDTO(1L, 2))
        );
        when(storageClient.getProduct(1L)).thenThrow(new NotFoundException());

        assertThrows(OrderProductException.class, () -> orderService.createOrder(request));
    }

    @Test
    public void shouldCreateOrderThrowsExceptionWhenAmountOver() {
        OrderCreateDTO request = new OrderCreateDTO(
                "+123456789",
                "Address",
                List.of(new OrderCreateDTO.OrderProductDTO(1L, 2))
        );

        ProductDTO product = new ProductDTO(
                1L,
                "Test product",
                new ProductDTO.CategoryDTO(1L, "Test category"),
                1,
                "Test Description",
                BigDecimal.ONE
        );

        when(storageClient.getProduct(1L))
                .thenReturn(product);

        when(storageClient.hasStock(1L,2))
                .thenReturn(false);

        assertThrows(OrderStockException.class, () -> orderService.createOrder(request));
    }

    //Тесты с обновлением заказа
    @Test
    public void shouldUpdateOrder() {
        when(orderRepository.findByIdOptional(1L))
                .thenReturn(Optional.of(orderTest));

        doNothing().when(storageClient)
                .orderProcessCancel(1L,1);

        ProductDTO product = new ProductDTO(
                1L,
                "Test product",
                new ProductDTO.CategoryDTO(1L, "Test category"),
                1,
                "Test Description",
                BigDecimal.ONE
        );

        when(storageClient.getProduct(1L))
                .thenReturn(product);

        when(storageClient.hasStock(1L,1))
                .thenReturn(true);

        OrderResponseDTO updateOrder = orderService.updateOrder(1L,new OrderUpdateDTO(
                "12345678912",
                "New delivery address",
                OrderStatus.PROCESSING,
                List.of(new OrderUpdateDTO.OrderProductDTO(1L,1))
        ));

        assertNotNull(updateOrder);
        assertEquals(updateOrder.orderId, orderTest.getId());
        assertEquals(updateOrder.customerPhone, "12345678912");
        assertEquals(updateOrder.deliveryAddress, "New delivery address");
        assertEquals(updateOrder.totalPrice, BigDecimal.ONE);
        assertEquals(updateOrder.status, OrderStatus.PROCESSING);
        assertEquals(updateOrder.orderProducts.getFirst().productId(), product.id);

    }

    @Test
    public void shouldUpdateOrderThrowsExceptionWhenOrderNotFound() {
        when(orderRepository.findByIdOptional(2L))
                .thenReturn(Optional.empty());

        assertThrows(OrderNotFoundException.class, () -> orderService.updateOrder(2L,new OrderUpdateDTO()));
    }

    @Test
    public void shouldUpdateOrderThrowsExceptionWhenProductNotFound() {
        when(orderRepository.findByIdOptional(1L))
                .thenReturn(Optional.of(orderTest));

        doNothing().when(storageClient)
                .orderProcessCancel(1L,1);

        when(storageClient.getProduct(2L))
                .thenThrow(new NotFoundException());

        OrderUpdateDTO updateOrder = new OrderUpdateDTO(
                "12345678912",
                "Delivery address test",
                OrderStatus.CREATED,
                List.of(new OrderUpdateDTO.OrderProductDTO(2L, 1))
        );

        assertThrows(OrderProductException.class, () -> orderService.updateOrder(1L,updateOrder));
    }

    @Test
    public void shouldUpdateOrderThrowsExceptionWhenAmountOver() {
        when(orderRepository.findByIdOptional(1L))
                .thenReturn(Optional.of(orderTest));

        doNothing().when(storageClient)
                .orderProcessCancel(1L,1);

        ProductDTO product = new ProductDTO(
                1L,
                "Test product",
                new ProductDTO.CategoryDTO(1L, "Test category"),
                1,
                "Test Description",
                BigDecimal.ONE
        );

        when(storageClient.getProduct(1L))
                .thenReturn(product);

        when(storageClient.hasStock(1L,10))
                .thenReturn(false);

        OrderUpdateDTO updateOrder = new OrderUpdateDTO(
                "12345678912",
                "Delivery address test",
                OrderStatus.CREATED,
                List.of(new OrderUpdateDTO.OrderProductDTO(1L, 10))
        );

        assertThrows(OrderStockException.class, () -> orderService.updateOrder(1L,updateOrder));
    }

    //Тесты с обновлением информации о заказе
    @Test
    public void shouldUpdateOrderInformation(){
        when(orderRepository.findByIdOptional(1L))
                .thenReturn(Optional.of(orderTest));

        OrderUpdateInfoDTO orderUpdateInfoDTO = new OrderUpdateInfoDTO(
                "12345678912",
                "New delivery address",
                OrderStatus.SHIPPED
        );
        OrderResponseDTO updateOrder = orderService.updateOrderInformation(1L,orderUpdateInfoDTO);

        assertNotNull(updateOrder);
        assertEquals(updateOrder.orderId, orderTest.getId());
        assertEquals(updateOrder.deliveryAddress, "New delivery address");
        assertEquals(updateOrder.customerPhone, "12345678912");
        assertEquals(updateOrder.status, OrderStatus.SHIPPED);
    }

    @Test
    public void shouldUpdateOrderInformationThrowsExceptionWhenOrderNotFound() {
        when(orderRepository.findByIdOptional(1L)).thenReturn(Optional.empty());
        assertThrows(RuntimeException.class, () -> orderService.updateOrderInformation(1L,new OrderUpdateInfoDTO()));
    }

    //Тесты с удалением заказа
    @Test
    public void shouldDeleteOrder() {
        when(orderRepository.findByIdOptional(1L))
                .thenReturn(Optional.of(orderTest));

        doNothing().when(storageClient).orderProcessCancel(1L,1);

        when(orderRepository.deleteById(1L)).thenReturn(true);

        orderService.deleteOrder(1L);

        verify(storageClient).orderProcessCancel(1L,1);
    }

    @Test
    public void shouldDeleteOrderThrowsExceptionWhenOrderNotFound() {
        when(orderRepository.findByIdOptional(1L)).thenReturn(Optional.empty());
        assertThrows(OrderNotFoundException.class, () -> orderService.deleteOrder(1L));
    }

    //Тесты с добавлением товара в заказ
    @Test
    public void shouldAddProduct(){
        when(orderRepository.findByIdOptional(1L))
                .thenReturn(Optional.of(orderTest));

        ProductDTO product = new ProductDTO(
                2L,
                "Test product2",
                new ProductDTO.CategoryDTO(1L, "Test category"),
                2,
                "Test Description2",
                BigDecimal.ONE
        );

        when(storageClient.getProduct(2L))
                .thenReturn(product);

        when(storageClient.hasStock(2L,2))
                .thenReturn(true);

        OrderUpdateDTO.OrderProductDTO orderProductDTO = new OrderUpdateDTO.OrderProductDTO(
                2L,
                2
        );
        OrderResponseDTO order = orderService.addProduct(1L, orderProductDTO);

        assertNotNull(order);
        assertEquals(order.orderId, orderTest.getId());
        assertEquals(order.orderProducts.get(1).productId(), product.id);
        assertEquals(order.orderProducts.get(1).amount(), 2);
        assertEquals(order.orderProducts.get(1).productName(), "Test product2");
        assertEquals(order.totalPrice, BigDecimal.valueOf(3));

    }

    @Test
    public void shouldAddProductThrowsExceptionWhenOrderNotFound() {
        when(orderRepository.findByIdOptional(1L)).thenReturn(Optional.empty());
        OrderUpdateDTO.OrderProductDTO orderProductDTO = new OrderUpdateDTO.OrderProductDTO(
                2L,
                2
        );
        assertThrows(OrderNotFoundException.class, ()->orderService.addProduct(1L, orderProductDTO));
    }

    @Test
    public void shouldAddProductThrowsExceptionWhenProductNotFound() {
        when(orderRepository.findByIdOptional(1L))
                .thenReturn(Optional.of(orderTest));

        when(storageClient.getProduct(1L)).thenThrow(NotFoundException.class);

        OrderUpdateDTO.OrderProductDTO orderProductDTO = new OrderUpdateDTO.OrderProductDTO(
                1L,
                2
        );
        assertThrows(OrderProductException.class, ()->orderService.addProduct(1L, orderProductDTO));
    }

    @Test
    public void shouldAddProductThrowsExceptionWhenProductAlreadyExists() {
        when(orderRepository.findByIdOptional(1L))
                .thenReturn(Optional.of(orderTest));

        ProductDTO product = new ProductDTO(
                1L,
                "Test product2",
                new ProductDTO.CategoryDTO(1L, "Test category"),
                2,
                "Test Description2",
                BigDecimal.ONE
        );

        when(storageClient.getProduct(1L))
                .thenReturn(product);

        OrderUpdateDTO.OrderProductDTO orderProductDTO = new OrderUpdateDTO.OrderProductDTO(
                1L,
                2
        );

        assertThrows(OrderProductAlreadyExistsException.class, ()->orderService.addProduct(1L, orderProductDTO));
    }

    @Test
    public void shouldAddProductThrowsExceptionWhenProductNotStock(){
        when(orderRepository.findByIdOptional(1L))
                .thenReturn(Optional.of(orderTest));

        ProductDTO product = new ProductDTO(
                2L,
                "Test product2",
                new ProductDTO.CategoryDTO(1L, "Test category"),
                2,
                "Test Description2",
                BigDecimal.ONE
        );

        when(storageClient.getProduct(2L))
                .thenReturn(product);

        when(storageClient.hasStock(2L,3))
                .thenReturn(false);

        OrderUpdateDTO.OrderProductDTO orderProductDTO = new OrderUpdateDTO.OrderProductDTO(
                2L,
                3
        );

        assertThrows(OrderStockException.class, ()->orderService.addProduct(1L, orderProductDTO));
    }

    //Тесты с с удалением товара из заказа
    @Test
    public void shouldDeleteProduct(){
        when(orderRepository.findByIdOptional(1L))
                .thenReturn(Optional.of(orderTest));

        doNothing().when(storageClient).orderProcessCancel(1L,1);

        OrderResponseDTO order= orderService.deleteProduct(1L,1L);

        assertNotNull(order);
        assertEquals(order.orderId, orderTest.getId());
        assertEquals(order.orderProducts.size(), 0);
    }

    @Test
    public void shouldDeleteProductThrowsExceptionWhenOrderNotFound() {
        when(orderRepository.findByIdOptional(1L)).thenReturn(Optional.empty());
        assertThrows(OrderNotFoundException.class, ()->orderService.deleteProduct(1L,1L));
    }

    @Test
    public void shouldDeleteProductThrowsExceptionWhenProductNotFound() {
        when(orderRepository.findByIdOptional(1L))
                .thenReturn(Optional.of(orderTest));

        assertThrows(OrderProductNotFoundException.class, ()->orderService.deleteProduct(1L,2L));
    }

    //Тесты с изменением количетсва товара

    @Test
    public void shouldUpdateProductOrderAmountAdd(){
        when(orderRepository.findByIdOptional(1L))
                .thenReturn(Optional.of(orderTest));


        when(storageClient.hasStock(1L,1))
                .thenReturn(true);


        OrderResponseDTO order = orderService.updateProductOrderAmount(1L, 1L,2);

        assertNotNull(order);
        verify(storageClient, times(1)).hasStock(1L,1);
        verify(storageClient, never()).orderProcessCancel(anyLong(),anyInt());
        assertEquals(order.orderId, orderTest.getId());
        assertEquals(order.orderProducts.getFirst().amount(), 2);
    }

    @Test
    public void shouldUpdateProductOrderAmountDel(){
        orderTest.getOrderProducts().getFirst().setAmount(2);

        when(orderRepository.findByIdOptional(1L))
                .thenReturn(Optional.of(orderTest));

        doNothing().when(storageClient)
                .orderProcessCancel(1L,1);

        OrderResponseDTO order = orderService.updateProductOrderAmount(1L, 1L,1);

        assertNotNull(order);
        verify(storageClient, never()).hasStock(anyLong(),anyInt());
        verify(storageClient, times(1)).orderProcessCancel(1L,1);
        assertEquals(order.orderId, orderTest.getId());
        assertEquals(order.orderProducts.getFirst().amount(), 1);
    }

    @Test
    public void shouldUpdateProductAmountThrowsExceptionWhenOrderNotFound(){
        when(orderRepository.findByIdOptional(2L)).thenReturn(Optional.empty());

        assertThrows(OrderNotFoundException.class, ()->orderService.updateProductOrderAmount(2L, 1L,1));
    }

    @Test
    public void shouldUpdateProductAmountThrowsExceptionWhenProductNotFound(){
        when(orderRepository.findByIdOptional(1L))
                .thenReturn(Optional.of(orderTest));

        assertThrows(OrderProductNotFoundException.class, ()-> orderService.updateProductOrderAmount(1L, 10L,1));
    }

    @Test
    public void shouldUpdateProductAmountThrowsExceptionWhenAmountOver(){
        when(orderRepository.findByIdOptional(1L))
                .thenReturn(Optional.of(orderTest));


        when(storageClient.hasStock(1L,1))
                .thenReturn(false);

        assertThrows(OrderStockExceptionNoProductAmount.class, ()->orderService.updateProductOrderAmount(1L, 1L,10));
    }

}
