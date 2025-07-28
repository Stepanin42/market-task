package com.market.storage.resource;

import com.market.storage.service.ProductService;
import com.market.storage.dto.ProductRequestDTO;
import com.market.storage.dto.ProductResponseDTO;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.util.List;

import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Path("/api/products")
@Tag(name = "Products", description = "Управление товарами")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class ProductResource {
    private static final Logger logger = LoggerFactory.getLogger(ProductResource.class);

    @Inject
    ProductService productService;

    @GET
    @Operation(summary = "Получить список всех товаров")
    public List<ProductResponseDTO> getAllProducts() {
        logger.info("Получение запроса на список всех товаров");
        return productService.getAllProducts();
    }

    @GET
    @Path("/{id}")
    public ProductResponseDTO getProduct(@PathParam("id") long id) {
        logger.info("Запрос товара по ID: {}", id);
        return productService.getProductById(id);
    }

    @GET
    @Path("/{id}/category")
    public List<ProductResponseDTO> getProductCategory(@PathParam("id") long id) {
        logger.info("Запрос товаров категории с ID: {}", id);
        return productService.getProductsByCategory(id);
    }

    @GET
    @Path("/search")
    public List<ProductResponseDTO> getProductName(@QueryParam("name") String name) {
        logger.info("Запрос на поиск товаров по названию: '{}'", name);
        return productService.getProductsBySimilarName(name);
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response createProduct(@Valid ProductRequestDTO productRequestDTO) {
        logger.info("Запрос на создание нового товара: {}", productRequestDTO);
        ProductResponseDTO created = productService.createProduct(productRequestDTO);
        if (created == null) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
        }
        return Response.status(Response.Status.CREATED)
                .entity(created)
                .type(MediaType.APPLICATION_JSON)
                .build();
    }

    @PUT
    @Path("/{id}")
    public ProductResponseDTO updateProduct(@PathParam("id") long id, ProductRequestDTO productRequestDTO) {
        logger.info("Запрос на обновление товара с ID: {}, новые данные: {}", id, productRequestDTO);
        return productService.updateProduct(id, productRequestDTO);
    }

    @DELETE
    @Path("/{id}")
    public Response deleteProduct(@PathParam("id") long id) {
        logger.info("Запрос на удаление товара с ID: {}", id);
        productService.deleteProduct(id);
        return Response.status(Response.Status.NO_CONTENT).build();
    }

    @POST
    @Path("/{id}/order")
    public Response orderProduct(@PathParam("id") Long id, @QueryParam("amount") int amount) {
        logger.info("Запрос на заказ товара ID: {}, количество: {}", id, amount);
        productService.orderProduct(id, amount);
        return Response.status(Response.Status.OK).build();
    }

    @POST
    @Path("/{id}/order-cancel")
    public Response orderProductsCancel(@PathParam("id") Long id,@QueryParam("amount") int amount) {
        logger.info("Запрос на отмену заказа товара ID: {}, возврат количества: {}", id, amount);
        productService.orderProductCancel(id,amount);
        return Response.status(Response.Status.OK).build();
    }

    @GET
    @Path("/{id}/stock")
    public Response getStock(@PathParam("id") Long id, @QueryParam("amount") int amount) {
        logger.debug("Запрос на проверку наличия товара ID: {}, количество: {}", id, amount);
        boolean hasStock=productService.checkStock(id, amount);
        return Response.ok(hasStock).build();
    }
}
