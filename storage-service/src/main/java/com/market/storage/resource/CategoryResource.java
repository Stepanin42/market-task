package com.market.storage.resource;

import com.market.storage.service.CategoryService;
import com.market.storage.dto.CategoryRequestDTO;
import com.market.storage.dto.CategoryResponseDTO;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import com.market.storage.model.Category;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Path("/api/categories")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class CategoryResource {
    private static final Logger logger = LoggerFactory.getLogger(CategoryResource.class);

    @Inject
    private CategoryService categoryService;

    @GET
    public List<CategoryResponseDTO> getAllCategory() {
        logger.info("Запрос на получение всех категорий");
        return categoryService.getAllCategories();
    }

    @GET
    @Path("/{id}")
    public CategoryResponseDTO getCategoryById(@PathParam("id") Long id) {
        logger.info("Запрос категории по ID: {}", id);
        return categoryService.getCategoryById(id);
    }

    @GET
    @Path("/by-name/{name}")
    public CategoryResponseDTO getCategoryByName(@PathParam("name") String name) {
        logger.info("Запрос категории по имени: {}", name);
        return categoryService.getCategoriesByName(name);
    }

    @POST
    public Response createCategory(@Valid CategoryRequestDTO category) {
        logger.info("Запрос на создание новой категории: {}", category);
        categoryService.createCategory(category);
        return Response.status(Response.Status.CREATED).entity(category).build();
    }

    @PUT
    @Path("/{id}")
    public Response updateCategory(@PathParam("id") Long id, @Valid CategoryRequestDTO category) {
        logger.info("Запрос на обновление категории с ID: {}, новые данные: {}", id, category);
        categoryService.updateCategory(id, category);
        return Response.status(Response.Status.OK).entity(category).build();
    }

    @DELETE
    @Path("/{id}")
    public Response deleteCategory(@PathParam("id") Long id) {
        logger.info("Запрос на удаление категории с ID: {}", id);
        categoryService.deleteCategory(id);
        return Response.status(Response.Status.NO_CONTENT).build();
    }
}
