package com.market.storage.resource;

import com.market.storage.dto.CategoryRequestDTO;
import com.market.storage.dto.CategoryResponseDTO;
import io.quarkus.test.InjectMock;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.RestAssured;
import jakarta.inject.Inject;
import com.market.storage.model.Category;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import com.market.storage.service.CategoryService;

import java.util.List;

import static org.mockito.Mockito.*;
import static org.hamcrest.Matchers.*;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.*;

@QuarkusTest
public class CategoryResourceTest {
    @Inject
    CategoryResource categoryResource;

    @InjectMock
    CategoryService categoryService;

    private CategoryResponseDTO categoryTest;

    @BeforeEach
    public void setup() {
        RestAssured.basePath="/api/categories";

        categoryTest = new CategoryResponseDTO();
        categoryTest.id =1L;
        categoryTest.name ="Test Category";
    }

    @Test
    public void getAllCategories() {

        CategoryResponseDTO categoryTest2 = new CategoryResponseDTO();
        categoryTest2.id =2L;
        categoryTest2.name= "Test Category2";

        when(categoryService.getAllCategories())
                .thenReturn(List.of(categoryTest, categoryTest2));

        given()
                .contentType("application/json")
                .when().get()
                .then()
                    .statusCode(200)
                    .body("size()", equalTo(2))
                    .body("[0].name", equalTo(categoryTest.name))
                    .body("[1].name", equalTo(categoryTest2.name));
    }

    @Test
    public void getCategoryById() {
        assertNotNull(categoryTest);

        when(categoryService.getCategoryById(eq(1L)))
                .thenReturn(categoryTest);

        given()
                .contentType("application/json")
                .pathParam("id", 1L)
                .when().get("/{id}")
                .then()
                .log().all()
                    .statusCode(200)
                    .body("name", equalTo(categoryTest.name));
    }

    @Test
    public void getCategoryByName() {
        assertNotNull(categoryTest);

        when(categoryService.getCategoriesByName(eq("Test Category")))
                .thenReturn(categoryTest);

        given()
                .contentType("application/json")
                .pathParam("name", "Test Category")
                .when().get("/by-name/{name}")
                .then()
                    .statusCode(200)
                    .body("name", equalTo(categoryTest.name));
    }

    @Test
    public void createCategory() {
        when(categoryService.createCategory(Mockito.any( CategoryRequestDTO.class)))
                .thenReturn(categoryTest);

        given()
                .contentType("application/json")
                .body(new CategoryRequestDTO(categoryTest.name))
                .when().post()
                .then()
                    .statusCode(201)
                    .body("name", equalTo(categoryTest.name));
    }

    @Test
    public void updateCategory() {
        CategoryRequestDTO newCategory=new CategoryRequestDTO("Test Category");

        when(categoryService.updateCategory(eq(1L), Mockito.any(CategoryRequestDTO.class)))
                .thenReturn(categoryTest);

        given()
                .contentType("application/json")
                .pathParam("id", 1L)
                .body(newCategory)
                .when().put("/{id}")
                .then()
                    .statusCode(200)
                    .body("name", equalTo(categoryTest.name));
    }

    @Test
    public void deleteCategory() {
        doNothing().when(categoryService).deleteCategory(1L);

        given()
                .pathParam("id", 1L)
                .when().delete("/{id}")
                .then()
                    .statusCode(204);
    }
}
