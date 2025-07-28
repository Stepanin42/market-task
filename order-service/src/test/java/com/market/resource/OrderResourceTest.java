package com.market.resource;

import com.market.dto.OrderCreateDTO;
import com.market.dto.OrderResponseDTO;
import com.market.dto.OrderUpdateDTO;
import com.market.dto.OrderUpdateInfoDTO;
import com.market.enums.OrderStatus;
import io.quarkus.test.InjectMock;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.RestAssured;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import com.market.service.OrderService;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.hamcrest.Matchers.*;

import static io.restassured.RestAssured.given;

@QuarkusTest
public class OrderResourceTest {
    @InjectMock
    OrderService orderService;

    OrderResponseDTO orderResponse;
    @BeforeEach
    public void setup() {
        RestAssured.basePath = "/api/orders";

        OrderResponseDTO.OrderProductDTO orderProductDTO = new OrderResponseDTO.OrderProductDTO(
                1L,
                1,
                BigDecimal.ONE,
                BigDecimal.ONE,
                "Test product"
        );

        List<OrderResponseDTO.OrderProductDTO> orderProductDTOList = new ArrayList<>();
        orderProductDTOList.add(orderProductDTO);

        orderResponse = new OrderResponseDTO();
        orderResponse.orderId=1L;
        orderResponse.customerPhone="12345678912";
        orderResponse.totalPrice= BigDecimal.ONE;
        orderResponse.createDate= LocalDateTime.of(2020,1,1,1,1);
        orderResponse.deliveryAddress="Test delivery address";
        orderResponse.orderProducts= orderProductDTOList;
        orderResponse.status= OrderStatus.CREATED;
    }

    @Test
    public void getAllOrders() {
        when(orderService.findAll())
                .thenReturn(List.of(orderResponse));

        given()
                .when().get()
                .then()
                .statusCode(200)
                .contentType(MediaType.APPLICATION_JSON)
                .body("size()", is(1))
                .body("[0].orderId", is(orderResponse.orderId.intValue()))
                .body("[0].customerPhone", is(orderResponse.customerPhone))
                .body("[0].orderProducts[0].productId", is(orderResponse.orderProducts.getFirst().productId().intValue()));
    }

    @Test
    public void getOrderById(){
        when(orderService.findById(orderResponse.orderId))
                .thenReturn(orderResponse);

        given()
                .pathParam("id",orderResponse.orderId.intValue())
                .when().get("/{id}")
                .then()
                .statusCode(200)
                    .body("orderId", is(orderResponse.orderId.intValue()))
                    .body("customerPhone", is(orderResponse.customerPhone))
                    .body("createDate", containsString("2020-01-01"))
                    .body("deliveryAddress", is(orderResponse.deliveryAddress))
                    .body("status", is(orderResponse.status.name()))
                    .body("orderProducts[0].productId", is(orderResponse.orderProducts.getFirst().productId().intValue()));
    }

    @Test
    public void getOrderByCustomerPhone(){
        when(orderService.findByCustomerPhone(orderResponse.customerPhone))
                .thenReturn(List.of(orderResponse));

        given()
                .queryParam("customerPhone",orderResponse.customerPhone)
                .when().get("/customer-phone")
                .then()
                .statusCode(200)
                    .body("size()", is(1))
                    .body("[0].orderId", is(orderResponse.orderId.intValue()))
                    .body("[0].customerPhone", is(orderResponse.customerPhone))
                    .body("[0].createDate", containsString("2020-01-01"))
                    .body("[0].deliveryAddress", is(orderResponse.deliveryAddress))
                    .body("[0].status", is(orderResponse.status.name()))
                    .body("[0].orderProducts[0].productId", is(orderResponse.orderProducts.getFirst().productId().intValue()));
    }

    @Test
    public void getOrderByDeliveryAddress(){
        when(orderService.findByDeliveryAddress(orderResponse.deliveryAddress))
                .thenReturn(List.of(orderResponse));

        given()
                .queryParam("deliveryAddress",orderResponse.deliveryAddress)
                .when().get("/delivery-address")
                .then()
                .statusCode(200)
                    .body("size()", is(1))
                    .body("[0].orderId", is(orderResponse.orderId.intValue()))
                    .body("[0].customerPhone", is(orderResponse.customerPhone))
                    .body("[0].createDate", containsString("2020-01-01"))
                    .body("[0].deliveryAddress", is(orderResponse.deliveryAddress))
                    .body("[0].status", is(orderResponse.status.name()))
                    .body("[0].orderProducts[0].productId", is(orderResponse.orderProducts.getFirst().productId().intValue()));
    }

    @Test
    public void getOrderByProductId(){
        when(orderService.findByProductId(orderResponse.orderProducts.getFirst().productId()))
                .thenReturn(List.of(orderResponse));

        given()
                .queryParam("productId",orderResponse.orderProducts.getFirst().productId().intValue())
                .when().get("/product-id")
                .then()
                .statusCode(200)
                    .body("size()", is(1))
                    .body("[0].orderId", is(orderResponse.orderId.intValue()))
                    .body("[0].customerPhone", is(orderResponse.customerPhone))
                    .body("[0].createDate", containsString("2020-01-01"))
                    .body("[0].deliveryAddress", is(orderResponse.deliveryAddress))
                    .body("[0].status", is(orderResponse.status.name()))
                    .body("[0].orderProducts[0].productId", is(orderResponse.orderProducts.getFirst().productId().intValue()));
    }

    @Test
    public void getRecentOrders(){
        when(orderService.findRecentOrders(1))
                .thenReturn(List.of(orderResponse));

        given()
                .queryParam("limit",1)
                .when().get("/recent")
                .then()
                .statusCode(200)
                    .body("size()", is(1))
                    .body("[0].orderId", is(orderResponse.orderId.intValue()))
                    .body("[0].customerPhone", is(orderResponse.customerPhone))
                    .body("[0].createDate", containsString("2020-01-01"))
                    .body("[0].deliveryAddress", is(orderResponse.deliveryAddress))
                    .body("[0].status", is(orderResponse.status.name()))
                    .body("[0].orderProducts[0].productId", is(orderResponse.orderProducts.getFirst().productId().intValue()));
    }

    @Test
    public void createOrder(){
        OrderCreateDTO orderCreateDTO = new OrderCreateDTO(
                "12345678912",
                "Test delivery address",
                List.of(new OrderCreateDTO.OrderProductDTO(
                        1L,
                        1
                    )
                )

        );

        when(orderService.createOrder(Mockito.any(OrderCreateDTO.class)))
                .thenReturn(orderResponse);

        given()
                .body(orderCreateDTO)
                .contentType("application/json")
                .when().post()
                .then()
                .statusCode(Response.Status.CREATED.getStatusCode())
                .contentType(MediaType.APPLICATION_JSON)
                    .body("orderId", is(orderResponse.orderId.intValue()))
                    .body("customerPhone", is(orderResponse.customerPhone))
                    .body("createDate", containsString("2020-01-01"))
                    .body("deliveryAddress", is(orderResponse.deliveryAddress))
                    .body("status", is(orderResponse.status.name()))
                    .body("orderProducts[0].productId", is(orderResponse.orderProducts.getFirst().productId().intValue()));
    }

    @Test
    public void updateOrder(){
        OrderUpdateDTO orderUpdateDTO = new OrderUpdateDTO(
                "12345678912",
                "Test delivery address",
                OrderStatus.DELIVERED,
                List.of(new OrderUpdateDTO.OrderProductDTO(
                        1L,
                        1
                    ))
        );

        orderResponse.status=OrderStatus.DELIVERED;

        when(orderService.updateOrder(eq(1L), Mockito.any(OrderUpdateDTO.class)))
                .thenReturn(orderResponse);

        given()
                .body(orderUpdateDTO)
                .contentType("application/json")
                .pathParam("id", 1)
                .when().put("/{id}")
                .then()
                .statusCode(Response.Status.OK.getStatusCode())
                .contentType(MediaType.APPLICATION_JSON)
                    .body("orderId", is(orderResponse.orderId.intValue()))
                    .body("customerPhone", is(orderResponse.customerPhone))
                    .body("createDate", containsString("2020-01-01"))
                    .body("deliveryAddress", is(orderResponse.deliveryAddress))
                    .body("status", is(orderResponse.status.name()))
                    .body("orderProducts[0].productId", is(orderResponse.orderProducts.getFirst().productId().intValue()));
    }

    @Test
    public void updateOrderInformation(){
        OrderUpdateInfoDTO orderUpdateDTO = new OrderUpdateInfoDTO(
                "12345678912",
                "Test delivery address",
                OrderStatus.DELIVERED
        );

        orderResponse.status=OrderStatus.DELIVERED;

        when(orderService.updateOrderInformation(eq(1L), Mockito.any(OrderUpdateInfoDTO.class)))
                .thenReturn(orderResponse);

        given()
                .body(orderUpdateDTO)
                .contentType("application/json")
                .pathParam("id", 1)
                .when().put("/{id}/information")
                .then()
                .statusCode(Response.Status.OK.getStatusCode())
                .contentType(MediaType.APPLICATION_JSON)
                    .body("orderId", is(orderResponse.orderId.intValue()))
                    .body("customerPhone", is(orderResponse.customerPhone))
                    .body("createDate", containsString("2020-01-01"))
                    .body("deliveryAddress", is(orderResponse.deliveryAddress))
                    .body("status", is(OrderStatus.DELIVERED.name()))
                    .body("orderProducts[0].productId", is(orderResponse.orderProducts.getFirst().productId().intValue()));
    }

    @Test
    public void addProductToOrder(){
        OrderUpdateDTO.OrderProductDTO orderProduct =new OrderUpdateDTO.OrderProductDTO(
                2L,
                1
        );

        OrderResponseDTO.OrderProductDTO orderProductDTO = new OrderResponseDTO.OrderProductDTO(
                2L,
                1,
                BigDecimal.ONE,
                BigDecimal.ONE,
                "Product test 2"
        );
        orderResponse.orderProducts.add(orderProductDTO);

        when(orderService.addProduct(eq(1L), Mockito.any(OrderUpdateDTO.OrderProductDTO.class)))
                .thenReturn(orderResponse);

        given()
                .pathParam("id", 1)
                .contentType("application/json")
                .body(orderProduct)
                .when().put("/{id}/add-product")
                .then()
                .statusCode(Response.Status.OK.getStatusCode())
                .contentType(MediaType.APPLICATION_JSON)
                    .body("orderId", is(orderResponse.orderId.intValue()))
                    .body("customerPhone", is(orderResponse.customerPhone))
                    .body("createDate", containsString("2020-01-01"))
                    .body("deliveryAddress", is(orderResponse.deliveryAddress))
                    .body("status", is(OrderStatus.CREATED.name()))
                    .body("orderProducts[0].productId", is(orderResponse.orderProducts.getFirst().productId().intValue()))
                    .body("orderProducts[1].productId", is(orderResponse.orderProducts.get(1).productId().intValue()));
    }

    @Test
    public void deleteProductFromOrder(){
        when(orderService.deleteProduct(1L,1L))
                .thenReturn(orderResponse);

        given()
                .pathParam("id", 1)
                .contentType("application/json")
                .queryParam("idProduct", 1)
                .when().put("/{id}/delete-product")
                .then()
                .statusCode(Response.Status.OK.getStatusCode())
                .contentType(MediaType.APPLICATION_JSON)
                    .body("orderId", is(orderResponse.orderId.intValue()))
                    .body("customerPhone", is(orderResponse.customerPhone))
                    .body("createDate", containsString("2020-01-01"))
                    .body("deliveryAddress", is(orderResponse.deliveryAddress))
                    .body("status", is(OrderStatus.CREATED.name()))
                    .body("orderProducts[0].productId", is(orderResponse.orderProducts.getFirst().productId().intValue()));
    }

    @Test
    public void changeOrderAmount(){
        when(orderService.updateProductOrderAmount(1L,1L,1))
                .thenReturn(orderResponse);

        given()
                .pathParam("id", 1)
                .contentType("application/json")
                .queryParam("productId", 1)
                .queryParam("amount", 1)
                .when().put("/{id}/change-amount")
                .then()
                .statusCode(Response.Status.OK.getStatusCode())
                .contentType(MediaType.APPLICATION_JSON)
                    .body("orderId", is(orderResponse.orderId.intValue()))
                    .body("customerPhone", is(orderResponse.customerPhone))
                    .body("createDate", containsString("2020-01-01"))
                    .body("deliveryAddress", is(orderResponse.deliveryAddress))
                    .body("status", is(OrderStatus.CREATED.name()))
                    .body("orderProducts[0].productId", is(orderResponse.orderProducts.getFirst().productId().intValue()));
    }

    @Test
    public void deleteOrder(){
        doNothing().when(orderService).deleteOrder(1L);

        given()
                .pathParam("id", 1)
                .when().delete("/{id}")
                .then()
                .statusCode(Response.Status.NO_CONTENT.getStatusCode());

    }
}
