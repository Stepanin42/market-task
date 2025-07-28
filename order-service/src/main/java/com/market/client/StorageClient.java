package com.market.client;

import com.market.dto.ProductDTO;
import com.market.exception.api.ApiException;
import com.market.exception.api.InsufficientStockException;
import com.market.exception.api.ProductNotFoundException;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

@ApplicationScoped
@RegisterRestClient(configKey = "storage-api")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public interface StorageClient {

    @GET
    @Path("/{id}")
    ProductDTO getProduct(@PathParam("id") Long id) throws ProductNotFoundException, ApiException;

    @GET
    @Path("/{id}/stock")
    boolean hasStock(@PathParam("id") Long id, @QueryParam("amount") int amount) throws ProductNotFoundException, InsufficientStockException, ApiException;

    @POST
    @Path("/{id}/order")
    void orderProcess(@PathParam("id") Long id, @QueryParam("amount") int amount) throws ProductNotFoundException, InsufficientStockException, ApiException;

    @POST
    @Path("/{id}/order-cancel")
    void orderProcessCancel(@PathParam("id") Long id, @QueryParam("amount") int amount) throws ProductNotFoundException, ApiException;

}
