package com.example.todo;

import java.time.OffsetDateTime;
import java.util.UUID;

public record TodoResponse(
        UUID id,
        String title,
        String description,
        boolean completed,
        OffsetDateTime createdAt,
        OffsetDateTime updatedAt) {

    static TodoResponse from(Todo todo) {
        return new TodoResponse(
                todo.id,
                todo.title,
                todo.description,
                todo.completed,
                todo.createdAt,
                todo.updatedAt);
    }
}
