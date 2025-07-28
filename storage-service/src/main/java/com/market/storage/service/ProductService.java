package com.market.storage.service;

import com.market.storage.exception.product.InsufficientStockException;
import com.market.storage.exception.product.InvalidAmountException;
import com.market.storage.exception.product.ProductNotFoundException;
import com.market.storage.model.Product;
import com.market.storage.dto.ProductRequestDTO;
import com.market.storage.dto.ProductResponseDTO;
import com.market.storage.exception.category.CategoryNotFoundException;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import com.market.storage.model.Category;
import com.market.storage.repository.CategoryRepository;
import com.market.storage.repository.ProductRepository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

@ApplicationScoped
public class ProductService {

    private static final Logger logger = LoggerFactory.getLogger(ProductService.class);

    @Inject
    ProductRepository productRepository;

    @Inject
    CategoryRepository categoryRepository;

    public List<ProductResponseDTO> getAllProducts() {
        logger.info("Получение списка всех товаров");
        List<ProductResponseDTO> productResponseDTOS=  productRepository.listAll().stream()
                .map(this::toDTO)
                .toList();
        logger.debug("Найдено {} товаров", productResponseDTOS.size());
        return productResponseDTOS;
    }

    public ProductResponseDTO getProductById(Long id) {
        logger.info("Поиск товара по ID: {}", id);
        Product product = productRepository.findByIdOptional(id)
                .orElseThrow(() -> {
                    logger.error("Товар с ID {} не найден", id);
                    return new ProductNotFoundException(id);});
        logger.debug("Найден товар: {}", product);
        return toDTO(product);
    }

    public List<ProductResponseDTO> getProductsByCategory(Long categoryId) {
        logger.info("Поиск товаров по категории с ID: {}", categoryId);
        categoryRepository.findByIdOptional(categoryId)
                .orElseThrow(()->{
                    logger.error("Категория с ID {} не найдена", categoryId);
                    return new CategoryNotFoundException(categoryId);});

        List<ProductResponseDTO> productResponseDTOS= productRepository.findByCategory(categoryId).stream()
                .map(this::toDTO)
                .toList();
        logger.debug("Найдено {} товаров в категории {}", productResponseDTOS.size(), categoryId);
        return productResponseDTOS;
    }

    public List<ProductResponseDTO> getProductsBySimilarName(String name) {
        logger.info("Поиск товаров по схожему названию: {}", name);
        List<ProductResponseDTO> products = productRepository.findBySimilarName(name).stream()
                .map(this::toDTO)
                .toList();
        logger.debug("Найдено {} товаров по запросу '{}'", products.size(), name);
        return products;
    }

    @Transactional
    public ProductResponseDTO createProduct(ProductRequestDTO productRequestDTO) {
        logger.info("Создание нового товара: {}", productRequestDTO.name);
        Product product=new Product();

        Category category=categoryRepository.findByIdOptional(productRequestDTO.categoryId)
                .orElseThrow(()->{
                    logger.error("Категория с ID {} не найдена", productRequestDTO.categoryId);
                    return new CategoryNotFoundException(productRequestDTO.categoryId);});

        product.setName(productRequestDTO.name);
        product.setDescription(productRequestDTO.description);
        product.setPrice(productRequestDTO.price);
        product.setCategory(category);
        product.setAmount(productRequestDTO.amount);

        productRepository.persist(product);
        logger.info("Создан новый товар с ID: {}", product.getId());
        return toDTO(product);
    }

    @Transactional
    public ProductResponseDTO updateProduct(Long id, ProductRequestDTO productRequestDTO) {
        logger.info("Обновление товара с ID: {}", id);
        Product product=productRepository.findByIdOptional(id)
                .orElseThrow(()->{
                    logger.error("Товар с ID {} не найден", id);
                    return new ProductNotFoundException(id);});

        Category category=categoryRepository.findByIdOptional(productRequestDTO.categoryId)
                .orElseThrow(()-> {
                    logger.error("Категория с ID {} не найдена", productRequestDTO.categoryId);
                    return new CategoryNotFoundException(productRequestDTO.categoryId);});

        logger.debug("Обновление данных товара: старые данные - {}, новые данные - {}", product, productRequestDTO);
        product.setName(productRequestDTO.name);
        product.setDescription(productRequestDTO.description);
        product.setPrice(productRequestDTO.price);
        product.setAmount(productRequestDTO.amount);
        product.setCategory(category);

        logger.info("Товар с ID {} успешно обновлен", id);
        return toDTO(product);
    }

    @Transactional
    public void deleteProduct(Long id) {
        logger.info("Удаление товара с ID: {}", id);
        boolean deleted= productRepository.deleteById(id);
        if(!deleted) {
            logger.error("Товар с ID {} не найден для удаления", id);
            throw new ProductNotFoundException(id);
        }
        logger.info("Товар с ID {} успешно удален", id);
    }

    @Transactional
    public void orderProduct(Long id, int amount) {
        logger.info("Оформление заказа товара с ID: {}, количество: {}", id, amount);
        Product product=productRepository.findByIdOptional(id)
                .orElseThrow(()-> {
                    logger.error("Товар с ID {} не найден при оформлении заказа", id);
                    return new ProductNotFoundException(id);});

        if(amount<1) {
            logger.error("Некорректное количество товара для заказа: {}", amount);
            throw new InvalidAmountException(amount);
        }
        if(product.getAmount()<amount) {
            logger.error("Недостаточно товара на складе. ID: {}, доступно: {}, запрошено: {}",
                    id, product.getAmount(), amount);
            throw new InsufficientStockException(id, product.getAmount(), amount);
        }

        product.setAmount(product.getAmount()-amount);
        logger.info("Заказ товара с ID {} выполнен. Остаток на складе: {}", id, product.getAmount());
    }

    @Transactional
    public void orderProductCancel(Long id, int amount) {
        logger.info("Отмена заказа товара с ID: {}, возврат количества: {}", id, amount);
        Product product=productRepository.findByIdOptional(id)
                .orElseThrow(()-> {
                    logger.error("Товар с ID {} не найден при отмене заказа", id);
                    return new ProductNotFoundException(id);});

        product.setAmount(product.getAmount()+amount);
        logger.info("Отмена заказа товара с ID {} выполнена. Новый остаток на складе: {}", id, product.getAmount());
    }

    public boolean checkStock(Long id, int amount) {
        logger.debug("Проверка наличия товара с ID: {}, необходимое количество: {}", id, amount);
        boolean hasStock= productRepository.hasStock(id, amount);
        logger.debug("Результат проверки наличия товара с ID {}: {}", id, hasStock);
        return hasStock;
    }

    private ProductResponseDTO toDTO(Product product) {
        return new ProductResponseDTO(
                product.getId(),
                product.getName(),
                new ProductResponseDTO.CategoryDTO(product.getCategory().getId(), product.getCategory().getName()),
                product.getAmount(),
                product.getDescription(),
                product.getPrice()
        );
    }
}
