package com.market.exception.api;

import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.Provider;
import org.eclipse.microprofile.rest.client.ext.ResponseExceptionMapper;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Provider
public class StorageClientErrorHandler implements ResponseExceptionMapper<RuntimeException> {
    @Override
    public RuntimeException toThrowable(Response response) {
        String errorMsg = response.readEntity(String.class);
        int status = response.getStatus();

        return switch (status) {
            case 404 -> new ProductNotFoundException(extractIdFromError(errorMsg));
            case 400 -> new InsufficientStockException(extractIdFromError(errorMsg));
            default -> new ApiException("Unexpected error: " + errorMsg, status);
        };
    }

    private Long extractIdFromError(String error) {
        try {
            Pattern pattern = Pattern.compile("id:?\\s?(\\d+)");
            Matcher matcher = pattern.matcher(error);
            return matcher.find() ? Long.parseLong(matcher.group(1)) : null;
        } catch (Exception e) {
            return null;
        }
    }
}
