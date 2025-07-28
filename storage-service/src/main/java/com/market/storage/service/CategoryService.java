package com.market.storage.service;

import com.market.storage.dto.CategoryRequestDTO;
import com.market.storage.dto.CategoryResponseDTO;
import com.market.storage.exception.category.CategoryNotFoundException;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import com.market.storage.model.Category;
import com.market.storage.repository.CategoryRepository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

@ApplicationScoped
public class CategoryService {
    private static final Logger logger = LoggerFactory.getLogger(CategoryService.class);

    @Inject
    CategoryRepository categoryRepository;

    public List<CategoryResponseDTO> getAllCategories() {
        logger.info("Получение всех категорий");
        List<CategoryResponseDTO> categoryResponseDTO = categoryRepository.listAll().stream()
                .map(this::toDto)
                .toList();
        logger.info("Категорий получено: {}", categoryResponseDTO.size());
        return categoryResponseDTO;
    }

    public CategoryResponseDTO getCategoryById(Long id) {
        logger.info("Получение категории по id: {}", id);
        Category category= categoryRepository.findByIdOptional(id)
                .orElseThrow(()->{
                    logger.info("Категория не найдена id: {}", id);
                    return new CategoryNotFoundException(id);});
        logger.info("Категория получена {}", category);
        return toDto(category);
    }

    public CategoryResponseDTO getCategoriesByName(String name) {
        logger.info("Получения категории по имени: {}", name);
        Category category= categoryRepository.findByName(name)
                .orElseThrow(()-> {
                    logger.info("Категория не найдена с именем: {}",name);
                    return new CategoryNotFoundException(name);});
        logger.info("Категория получена {}", category);
        return toDto(category);
    }

    @Transactional
    public CategoryResponseDTO createCategory(CategoryRequestDTO category) {
        logger.info("Создание категории с именем: {}", category.name);
        Category categoryEntity = new Category();
        categoryEntity.setName(category.name);
        categoryRepository.persist(categoryEntity);
        logger.info("Созданная категория с id: {}", categoryEntity.getId());
        return toDto(categoryEntity);
    }

    @Transactional
    public CategoryResponseDTO updateCategory(Long id, CategoryRequestDTO categoryNew) {
        logger.info("Обновление категории с id: {}", id);
        Category categoryOld= categoryRepository.findByIdOptional(id)
                .orElseThrow(()->{
                    logger.info("Категория не найдена id: {}", id);
                    return new CategoryNotFoundException(id);});
        categoryOld.setName(categoryNew.name);
        logger.info("Категория обновленна с id: {}", categoryOld.getId());
        return toDto(categoryOld);
    }

    @Transactional
    public void deleteCategory(Long id) {
        logger.info("Удаление категории с id: {}", id);
        boolean deleted = categoryRepository.deleteById(id);
        if(!deleted) {
            logger.info("Категории c {} для удаления не найдена ", id);
            throw new CategoryNotFoundException(id);
        }
        logger.info("Категория с id {} удалена успешно", id);
    }

    private CategoryResponseDTO toDto (Category category){
        return new CategoryResponseDTO(
                category.getId(),
                category.getName()
        );
    }
}
