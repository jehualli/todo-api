package com.example.todo;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;

public record ApiError(
        String error,
        String message,
        int status,
        String path,
        OffsetDateTime timestamp,
        List<FieldError> fields) {

    static ApiError of(String error, String message, int status, String path) {
        return new ApiError(error, message, status, path, OffsetDateTime.now(ZoneOffset.UTC), List.of());
    }

    static ApiError validation(String message, String path, List<FieldError> fields) {
        return new ApiError("VALIDATION_ERROR", message, 400, path, OffsetDateTime.now(ZoneOffset.UTC), fields);
    }

    public record FieldError(String field, String message) {
    }
}
