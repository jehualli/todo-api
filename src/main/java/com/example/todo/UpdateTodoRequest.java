package com.example.todo;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record UpdateTodoRequest(
        @NotBlank @Size(max = 120) String title,
        @Size(max = 2_000) String description,
        @NotNull Boolean completed) {
}
