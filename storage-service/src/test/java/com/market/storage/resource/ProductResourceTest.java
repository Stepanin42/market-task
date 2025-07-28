package com.market.storage.resource;

import com.market.storage.dto.ProductRequestDTO;
import com.market.storage.dto.ProductResponseDTO;
import io.quarkus.test.InjectMock;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import jakarta.inject.Inject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import com.market.storage.service.ProductService;

import java.math.BigDecimal;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.hamcrest.Matchers.*;

import static io.restassured.RestAssured.given;

@QuarkusTest
public class ProductResourceTest {
    @Inject
    ProductResource productResource;

    @InjectMock
    ProductService productService;

    private ProductResponseDTO productTest;

    @BeforeEach
    public void setup(){
        RestAssured.basePath = "/api/products";

        productTest = new ProductResponseDTO();
        productTest.id = 1L;
        productTest.name="testProduct";
        productTest.description="testProduct";
        productTest.amount=1;
        productTest.price= BigDecimal.valueOf(100);
        productTest.category=new ProductResponseDTO.CategoryDTO(1L, "test");

    }

    @Test
    public void testGetAllProducts() {
        ProductResponseDTO productTest2= new ProductResponseDTO();
        productTest2.id = 2L;
        productTest2.name = "testProduct2";
        productTest2.description="testProduct2";
        productTest2.amount = 2;
        productTest2.price= BigDecimal.valueOf(200);
        productTest2.category= new ProductResponseDTO.CategoryDTO(1L, "test");

        when(productService.getAllProducts())
                .thenReturn(List.of(productTest,productTest2));



       RestAssured.given()
                .when().get()
                .then()
                    .statusCode(200)
                    .body("size()", is(2))
                    .body("[0].name", equalTo("testProduct"))
                    .body("[1].name", equalTo("testProduct2"));

    }

    @Test
    public void testGetProductById() {
        when(productService.getProductById(1L))
                .thenReturn(productTest);

        RestAssured.given()
                .pathParam("id", 1L)
                .when().get("/{id}")
                .then()
                    .statusCode(200)
                    .body("name", equalTo("testProduct"));

    }

    @Test
    public void testGetProductByCategory() {
        ProductResponseDTO productTest2= new ProductResponseDTO();
        productTest2.id = 2L;
        productTest2.name = "testProduct2";
        productTest2.description="testProduct2";
        productTest2.amount = 2;
        productTest2.price= BigDecimal.valueOf(200);
        productTest2.category= new ProductResponseDTO.CategoryDTO(1L, "test");

        when(productService.getProductsByCategory(1L))
                .thenReturn(List.of(productTest,productTest2));

        given()
                .pathParam("id", 1L)
                .when().get("/{id}/category")
                .then()
                    .statusCode(200)
                    .body("size()", is(2))
                    .body("[0].name", equalTo("testProduct"))
                    .body("[1].name", equalTo("testProduct2"));
    }

    @Test
    public void getProductByName() {
        when(productService.getProductsBySimilarName("testProduct"))
                .thenReturn(List.of(productTest));

        RestAssured.given()
                .queryParam("name", "testProduct")
                .when().get("/search")
                .then()
                    .statusCode(200)
                    .body("size()", is(1))
                    .body("[0].name", equalTo("testProduct"));
    }

    @Test
    public void createProduct() {
        ProductRequestDTO createProductDTO = new ProductRequestDTO();
        createProductDTO.name="testProduct";
        createProductDTO.description="testProduct";
        createProductDTO.amount=1;
        createProductDTO.price= BigDecimal.valueOf(100);
        createProductDTO.categoryId=1L;


        Mockito.when(productService.createProduct(Mockito.any(ProductRequestDTO.class)))
                .thenReturn(productTest);

        //ProductResponseDTO test=productService.createProduct(createProductDTO);

        given()
                .contentType(ContentType.JSON)
                .body(createProductDTO)
                .when().post()
                .then()
                    .log().all()
                    .statusCode(201)
                    .contentType(ContentType.JSON)
                    .body("id", equalTo(1))
                    .body("name", equalTo("testProduct"))
                    .body("description", equalTo("testProduct"))
                    .body("amount", equalTo(1))
                    .body("price", equalTo(100))
                    .body("category.id", equalTo(1));
    }

    @Test
    public void createProductValidation(){
        ProductRequestDTO createProductDTO = new ProductRequestDTO();
        createProductDTO.name="";
        createProductDTO.description="testProduct";
        createProductDTO.amount=-10;
        createProductDTO.price= BigDecimal.valueOf(-100);
        createProductDTO.categoryId=1L;

        Mockito.when(productService.createProduct(Mockito.any(ProductRequestDTO.class)))
                .thenReturn(productTest);

        given()
                .contentType("application/json")
                .body(createProductDTO)
                .when().post()
                .then()
                .statusCode(400)
                .body("parameterViolations.size()", greaterThan(0))
                .body("parameterViolations.find {it.path.contains('name')}.message",
                        equalTo("не должно быть пустым"))
                .body("parameterViolations.find {it.path.contains('amount')}.message",
                        equalTo("должно быть больше или равно 0"));

    }

    @Test
    public void updateProduct() {
        ProductRequestDTO createProductDTO = new ProductRequestDTO();
        createProductDTO.name="testProduct";
        createProductDTO.description="testProduct";
        createProductDTO.amount=1;
        createProductDTO.price= BigDecimal.valueOf(100);
        createProductDTO.categoryId=1L;

        when(productService.updateProduct(eq(1L),Mockito.any(ProductRequestDTO.class)))
                .thenReturn(productTest);

        given()
        .contentType("application/json")
                .body(createProductDTO)
                .pathParam("id", 1L)
                .when().put("/{id}")
                .then()
                    .statusCode(200)
                    .body("name", equalTo("testProduct"))
                    .body("description", equalTo("testProduct"));
    }

    @Test
    public void deleteProduct() {
        doNothing().when(productService).deleteProduct(1L);

        given()
                .pathParam("id", 1L)
                .when().delete("/{id}")
                .then()
                    .statusCode(204);
    }

    @Test
    public void orderProduct(){
        doNothing().when(productService).orderProduct(eq(1L),eq(1));

        given()
                .contentType("application/json")
                .pathParam("id", 1L)
                .queryParam("amount",1)
                .when().post("/{id}/order")
                .then()
                    .statusCode(200);
    }


}
