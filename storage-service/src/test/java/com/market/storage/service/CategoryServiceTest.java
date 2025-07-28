package com.market.storage.service;

import com.market.storage.dto.CategoryRequestDTO;
import com.market.storage.dto.CategoryResponseDTO;
import com.market.storage.exception.category.CategoryNotFoundException;
import io.quarkus.test.InjectMock;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import com.market.storage.model.Category;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import com.market.storage.repository.CategoryRepository;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@QuarkusTest
public class CategoryServiceTest {
    @Inject
    CategoryService categoryService;

    @InjectMock
    CategoryRepository categoryRepository;

    private Category categoryTest;

    @BeforeEach
    public void setUp(){
        categoryTest = new Category();
        categoryTest.setId(1L);
        categoryTest.setName("Test Category");
    }

    @Test
    public void shouldGetAllCategories(){
        when(categoryRepository.listAll())
                .thenReturn(List.of(categoryTest));
        List<CategoryResponseDTO> categories = categoryService.getAllCategories();

        assertNotNull(categories);
        assertEquals(categoryTest.getId(), categories.get(0).id);
    }

    @Test
    public void shouldGetCategoryById(){
        when(categoryRepository.findByIdOptional(1L))
                .thenReturn(Optional.of(categoryTest));

        CategoryResponseDTO category = categoryService.getCategoryById(1L);

        assertNotNull(category);
        assertEquals(category.name, "Test Category");
    }

    @Test
    public void getCategoryByIdNotFound(){
        when(categoryRepository.findByIdOptional(1L))
                .thenReturn(Optional.empty());

        assertThrows(CategoryNotFoundException.class,
                () -> categoryService.getCategoryById(1L));
    }

    @Test
    public void shouldGetCategoryByName(){
        when(categoryRepository.findByName("Test Category"))
                .thenReturn(Optional.of(categoryTest));

        CategoryResponseDTO category=categoryService.getCategoriesByName("Test Category");

        assertNotNull(category);
        assertEquals(category.name, "Test Category");
    }

    @Test
    public void getCategoryByNameNotFound(){
        when(categoryRepository.findByName("Test Category"))
                .thenReturn(Optional.empty());

        assertThrows(CategoryNotFoundException.class,
                () -> categoryService.getCategoriesByName("Test Category"));
    }

    @Test
    public void shouldCreateCategory(){
        doNothing().when(categoryRepository)
                .persist(any(Category.class));


        CategoryResponseDTO category = categoryService.createCategory(new CategoryRequestDTO(categoryTest.getName()));

        assertNotNull(category);
        assertEquals(category.name, "Test Category");
        verify(categoryRepository, times(1)).persist(any(Category.class));
    }

    @Test
    public void shouldUpdateCategory(){
        when(categoryRepository.findByIdOptional(1L))
                .thenReturn(Optional.of(categoryTest));

        CategoryRequestDTO newCategory=new CategoryRequestDTO("Test NewCategory");

        CategoryResponseDTO updatedCategory= categoryService.updateCategory(1L, newCategory);

        assertNotNull(updatedCategory);
        assertEquals(updatedCategory.name, "Test NewCategory");
    }

    @Test
    public void updateCategoryNotFound(){
        when(categoryRepository.findByIdOptional(1L))
                .thenReturn(Optional.empty());

        CategoryRequestDTO newCategory=new CategoryRequestDTO("Test NewCategory");

        assertThrows(CategoryNotFoundException.class, () -> categoryService.updateCategory(1L, newCategory));
    }


}
