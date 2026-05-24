package com.example.todo;

import jakarta.validation.ConstraintViolationException;
import jakarta.ws.rs.BadRequestException;
import jakarta.ws.rs.NotFoundException;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriInfo;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;
import java.util.List;

@Provider
class ConstraintViolationMapper implements ExceptionMapper<ConstraintViolationException> {

    @Context
    UriInfo uriInfo;

    @Override
    public Response toResponse(ConstraintViolationException exception) {
        List<ApiError.FieldError> fields = exception.getConstraintViolations().stream()
                .map(violation -> new ApiError.FieldError(fieldName(violation.getPropertyPath().toString()), violation.getMessage()))
                .sorted((left, right) -> left.field().compareTo(right.field()))
                .toList();

        return Response.status(Response.Status.BAD_REQUEST)
                .type(MediaType.APPLICATION_JSON)
                .entity(ApiError.validation("Request validation failed", path(), fields))
                .build();
    }

    private static String fieldName(String propertyPath) {
        int dot = propertyPath.lastIndexOf('.');
        return dot >= 0 ? propertyPath.substring(dot + 1) : propertyPath;
    }

    private String path() {
        return uriInfo == null ? null : uriInfo.getPath();
    }
}

@Provider
class BadRequestMapper implements ExceptionMapper<BadRequestException> {

    @Context
    UriInfo uriInfo;

    @Override
    public Response toResponse(BadRequestException exception) {
        return Response.status(Response.Status.BAD_REQUEST)
                .type(MediaType.APPLICATION_JSON)
                .entity(ApiError.of("BAD_REQUEST", exception.getMessage(), 400, path()))
                .build();
    }

    private String path() {
        return uriInfo == null ? null : uriInfo.getPath();
    }
}

@Provider
class NotFoundMapper implements ExceptionMapper<NotFoundException> {

    @Context
    UriInfo uriInfo;

    @Override
    public Response toResponse(NotFoundException exception) {
        return Response.status(Response.Status.NOT_FOUND)
                .type(MediaType.APPLICATION_JSON)
                .entity(ApiError.of("NOT_FOUND", exception.getMessage(), 404, path()))
                .build();
    }

    private String path() {
        return uriInfo == null ? null : uriInfo.getPath();
    }
}

@Provider
class WebApplicationExceptionMapper implements ExceptionMapper<WebApplicationException> {

    @Context
    UriInfo uriInfo;

    @Override
    public Response toResponse(WebApplicationException exception) {
        int status = exception.getResponse().getStatus();
        String error = status >= 500 ? "SERVER_ERROR" : "HTTP_ERROR";
        return Response.status(status)
                .type(MediaType.APPLICATION_JSON)
                .entity(ApiError.of(error, exception.getMessage(), status, path()))
                .build();
    }

    private String path() {
        return uriInfo == null ? null : uriInfo.getPath();
    }
}
