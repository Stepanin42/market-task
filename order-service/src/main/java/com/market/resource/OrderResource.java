package com.market.resource;

import com.market.dto.OrderCreateDTO;
import com.market.dto.OrderResponseDTO;
import com.market.dto.OrderUpdateDTO;
import com.market.dto.OrderUpdateInfoDTO;
import com.market.service.OrderService;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Path("/api/orders")
public class OrderResource {
    private static final Logger logger = LoggerFactory.getLogger(OrderResource.class);

    @Inject
    private OrderService orderService;

    @GET
    public List<OrderResponseDTO> getAllOrders() {
        logger.info("Запрос на получение всех заказов");
        return orderService.findAll();
    }

    @GET
    @Path("/{id}")
    public OrderResponseDTO getOrderById(@PathParam("id") Long id) {
        logger.info("Запрос заказа по ID: {}", id);
        return orderService.findById(id);
    }

    @GET
    @Path("/customer-phone")
    public List<OrderResponseDTO> getOrderByCustomerPhone(@QueryParam("customerPhone") String customerPhone) {
        logger.info("Запрос заказов по телефону клиента: {}", customerPhone);
        return orderService.findByCustomerPhone(customerPhone);
    }

    @GET
    @Path("/delivery-address")
    public List<OrderResponseDTO> getOrderByDeliveryAddress(@QueryParam("deliveryAddress") String deliveryAddress) {
        logger.info("Запрос заказов по адресу доставки: {}", deliveryAddress);
        return orderService.findByDeliveryAddress(deliveryAddress);
    }

    @GET
    @Path("/product-id")
    public List<OrderResponseDTO> getOrderByProductId(@QueryParam("productId") Long productId) {
        logger.info("Запрос заказов по ID товара: {}", productId);
        return orderService.findByProductId(productId);
    }

    @GET
    @Path("/recent")
    public List<OrderResponseDTO> getRecentOrders(@QueryParam("limit") int limit) {
        logger.info("Запрос {} последних заказов", limit);
        return orderService.findRecentOrders(limit);
    }

    @POST
    public Response createOrder(@Valid OrderCreateDTO orderCreateDTO) {
        logger.info("Запрос на создание нового заказа: {}", orderCreateDTO);
        OrderResponseDTO order = orderService.createOrder(orderCreateDTO);

        if (order == null) {
            return Response.status(Response.Status.BAD_REQUEST).build();
        }
        return Response.status(Response.Status.CREATED)
                .entity(order)
                .type(MediaType.APPLICATION_JSON)
                .build();
    }

    @PUT
    @Path("/{id}")
    public Response updateOrder(@PathParam("id") Long id, @Valid OrderUpdateDTO orderUpdateDTO) {
        logger.info("Запрос на обновление заказа ID: {}. Новые данные: {}", id, orderUpdateDTO);
        OrderResponseDTO order = orderService.updateOrder(id, orderUpdateDTO);
        if (order == null) {
            return Response.status(Response.Status.BAD_REQUEST).build();
        }
        return Response.status(Response.Status.OK).entity(order).build();
    }

    @PUT
    @Path("/{id}/information")
    public Response updateOrderInformation(@PathParam("id") Long id, @Valid OrderUpdateInfoDTO orderUpdateDTO) {
        logger.info("Запрос на обновление информации о заказе ID: {}. Новые данные: {}", id, orderUpdateDTO);
        OrderResponseDTO order = orderService.updateOrderInformation(id, orderUpdateDTO);
        if (order == null) {
            return Response.status(Response.Status.BAD_REQUEST).build();
        }
        return Response.status(Response.Status.OK).entity(order).build();
    }

    @PUT
    @Path("/{id}/add-product")
    public Response addProductOrder(@PathParam("id") Long id, @Valid OrderUpdateDTO.OrderProductDTO orderRequestDTO) {
        logger.info("Запрос на добавление товара в заказ ID: {}. Товар ID: {}, Количество: {}", id, orderRequestDTO.productId, orderRequestDTO.amount);
        OrderResponseDTO order = orderService.addProduct(id,orderRequestDTO);
        if (order == null) {
            return Response.status(Response.Status.BAD_REQUEST).build();
        }
        return Response.status(Response.Status.OK).entity(order).build();
    }

    @PUT
    @Path("/{id}/delete-product")
    public Response deleteProductOrder(@PathParam("id") Long id, @QueryParam("idProduct") Long idOrderProduct) {
        logger.info("Запрос на удаление товара из заказа. Заказ ID: {}, Товар ID: {}", id, idOrderProduct);
        OrderResponseDTO order = orderService.deleteProduct(id,idOrderProduct);
        if (order == null) {
            return Response.status(Response.Status.BAD_REQUEST).build();
        }
        return Response.status(Response.Status.OK).entity(order).build();
    }

    @PUT
    @Path("/{id}/change-amount")
    public Response changeOrderAmount(
            @PathParam("id") Long id,
            @QueryParam("productId") Long productId,
            @QueryParam("amount") int amount)
    {
        logger.info("Запрос на изменение количества товара в заказе. Заказ ID: {}, Товар ID: {}, Новое количество: {}",
                id, productId, amount);
        OrderResponseDTO order= orderService.updateProductOrderAmount(id, productId, amount);
        if (order == null) {
            return Response.status(Response.Status.BAD_REQUEST).build();
        }
        return Response.status(Response.Status.OK).entity(order).build();
    }

    @DELETE
    @Path("/{id}")
    public Response deleteOrder(@PathParam("id") Long id) {
        logger.info("Запрос на удаление заказа ID: {}", id);
        orderService.deleteOrder(id);
        return Response.status(Response.Status.NO_CONTENT).build();
    }
}
