package com.market.storage.service;

import com.market.storage.dto.ProductRequestDTO;
import com.market.storage.dto.ProductResponseDTO;
import com.market.storage.exception.category.CategoryNotFoundException;
import com.market.storage.exception.product.InsufficientStockException;
import com.market.storage.exception.product.ProductNotFoundException;
import io.quarkus.test.InjectMock;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import com.market.storage.model.Category;
import com.market.storage.model.Product;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import com.market.storage.repository.CategoryRepository;
import com.market.storage.repository.ProductRepository;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.*;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@QuarkusTest
public class ProductServiceTest {

    @Inject
    ProductService productService;

    @InjectMock
    ProductRepository productRepository;

    @InjectMock
    CategoryRepository categoryRepository;

    private Product testProduct;

    @BeforeEach
    void setUp() {
        testProduct = new Product();
        testProduct.setId(1L);
        testProduct.setName("testProduct");
        testProduct.setPrice(BigDecimal.valueOf(100.0));
        testProduct.setAmount(1);
        testProduct.setDescription("Test Product description");

        Category oldCategory = new Category();
        oldCategory.setName("oldCategory");
        oldCategory.setId(1L);
        testProduct.setCategory(oldCategory);

    }

    @Test
    public void shouldGetAllProducts() {
        Mockito.when(productRepository.listAll()).thenReturn(List.of(testProduct));

        List<ProductResponseDTO> products = productService.getAllProducts();

        assertNotNull(products);
        assertEquals(1, products.size());
        ProductResponseDTO product = products.get(0);
        assertNotNull(product);
        assertEquals(product.id, testProduct.getId());
    }

    @Test
    public void shouldGetProductById() {
        Mockito.when(productRepository.findByIdOptional(1L)).thenReturn(Optional.of(testProduct));

        ProductResponseDTO product = productService.getProductById(1L);

        assertNotNull(product);
        assertEquals(product.id, testProduct.getId());
        assertEquals(product.name, "testProduct");

    }

    @Test
    public void shouldThrowExceptionIfProductNotFound() {
        Mockito.when(productRepository.findByIdOptional(99L)).thenReturn(Optional.empty());

        assertThrows(ProductNotFoundException.class, () -> productService.getProductById(99L));
    }

    @Test
    public void shouldGetProductByCategoryId() {
        Category category = new Category();
        category.setId(1L);
        category.setName("category");

        when(categoryRepository.findByIdOptional(1L))
                .thenReturn(Optional.of(category));

        when(productRepository.findByCategory(1L))
                .thenReturn(List.of(testProduct));

        List<ProductResponseDTO> productResponseDTO = productService.getProductsByCategory(1L);

        assertNotNull(productResponseDTO);
        assertEquals(1, productResponseDTO.size());
        ProductResponseDTO product = productResponseDTO.get(0);
        assertNotNull(product);
        assertEquals(product.name, testProduct.getName());
        assertEquals(product.id, testProduct.getId());
    }

    @Test
    public void getProductByCategoryIdThrowExceptionIfCategoryNotFound() {
        when(categoryRepository.findByIdOptional(1L))
                .thenReturn(Optional.empty());

        assertThrows(CategoryNotFoundException.class,
                () -> productService.getProductsByCategory(1L));
    }

    @Test
    public void shouldCreateProduct() {
        Category testCategory = new Category();
        testCategory.setName("testCategory");
        testCategory.setId(1L);

        ProductRequestDTO createdProduct = new ProductRequestDTO();
        createdProduct.name = "testProduct";
        createdProduct.description = "Test Product description";
        createdProduct.price = BigDecimal.valueOf(100.0);
        createdProduct.amount = 1;
        createdProduct.categoryId= testCategory.getId();


        when(categoryRepository.findByIdOptional(1L))
                .thenReturn(Optional.of(testCategory));

        doNothing().when(productRepository).persist(any(Product.class));

        ProductResponseDTO product = productService.createProduct(createdProduct);

        assertNotNull(product);
        assertEquals(product.name, createdProduct.name);
        verify(categoryRepository, times(1)).findByIdOptional(1L);
        verify(productRepository, times(1)).persist(any(Product.class));
    }

    @Test
    public void createProductShouldThrowExceptionIfCategoryNotFound() {
        when(categoryRepository.findByIdOptional(99L))
                .thenReturn(Optional.empty());

        ProductRequestDTO createdProduct = new ProductRequestDTO();
        createdProduct.name = "testProduct";
        createdProduct.description = "Test Product description";
        createdProduct.price = BigDecimal.valueOf(100.0);
        createdProduct.amount = 1;
        createdProduct.categoryId= 99L;

        doNothing().when(productRepository).persist(any(Product.class));

        assertThrows(CategoryNotFoundException.class, () -> productService.createProduct(createdProduct));
    }

    @Test
    public void shouldUpdateProduct() {


        Category testCategory = new Category();
        testCategory.setName("testCategory");
        testCategory.setId(1L);

        ProductRequestDTO updateProduct = new ProductRequestDTO();
        updateProduct.name = "testProduct2";
        updateProduct.description = "Test Product description2";
        updateProduct.price = BigDecimal.valueOf(102.0);
        updateProduct.amount = 1;
        updateProduct.categoryId= testCategory.getId();

        when(categoryRepository.findByIdOptional(1L))
                .thenReturn(Optional.of(testCategory));

        when(productRepository.findByIdOptional(1L))
                .thenReturn(Optional.of(testProduct));

        ProductResponseDTO product = productService.updateProduct(1L, updateProduct);

        assertNotNull(product);
        assertEquals(product.name, updateProduct.name);
        assertEquals(product.description, updateProduct.description);
        assertEquals(product.price, updateProduct.price);
        assertEquals(product.amount, updateProduct.amount);
        assertEquals(product.category.id(), updateProduct.categoryId);
        verify(categoryRepository, times(1)).findByIdOptional(1L);

    }

    @Test
    public void updateProductShouldThrowExceptionIfProductNotFound() {
        Category testCategory = new Category();
        testCategory.setName("testCategory");
        testCategory.setId(1L);

        ProductRequestDTO updateProduct = new ProductRequestDTO();
        updateProduct.name = "testProduct";
        updateProduct.description = "Test Product description";
        updateProduct.price = BigDecimal.valueOf(100.0);
        updateProduct.amount = 1;
        updateProduct.categoryId= testCategory.getId();

        when(productRepository.findByIdOptional(1L))
                .thenReturn(Optional.empty());

        assertThrows(ProductNotFoundException.class, () -> productService.updateProduct(1L, updateProduct));

    }

    @Test
    public void updateProductShouldThrowExceptionIfCategoryNotFound() {
        Category testCategory = new Category();
        testCategory.setName("testCategory");
        testCategory.setId(1L);

        ProductRequestDTO updateProduct = new ProductRequestDTO();
        updateProduct.name = "testProduct";
        updateProduct.description = "Test Product description";
        updateProduct.price = BigDecimal.valueOf(100.0);
        updateProduct.amount = 1;
        updateProduct.categoryId= testCategory.getId();

        when(productRepository.findByIdOptional(1L))
                .thenReturn(Optional.of(testProduct));

        when(categoryRepository.findByIdOptional(1L))
                .thenReturn(Optional.empty());

        verify(categoryRepository, times(0)).findByIdOptional(1L);
        assertThrows(CategoryNotFoundException.class,
                () -> productService.updateProduct(1L, updateProduct));
    }

    @Test
    public void shouldOrderProduct(){
        when(productRepository.findByIdOptional(1L)).thenReturn(Optional.of(testProduct));

        productService.orderProduct(1L, 1);

        verify(productRepository, times(1)).findByIdOptional(1L);
        assertEquals(0, testProduct.getAmount());
    }

    @Test
    public void orderProductShouldThrowExceptionIfProductNotFound() {
        when(productRepository.findByIdOptional(1L)).thenReturn(Optional.empty());

        assertThrows(ProductNotFoundException.class,
                () -> productService.orderProduct(1L, 1));
    }

    @Test
    public void orderProductShouldThrowExceptionIfLargeAmount() {
        when(productRepository.findByIdOptional(1L)).thenReturn(Optional.of(testProduct));

        assertThrows(InsufficientStockException.class, () -> productService.orderProduct(1L, 3));
    }
    /*
    @Test
    public void shouldGetProductByName() {

    }

     */
}
