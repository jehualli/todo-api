package com.example.todo;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreateTodoRequest(
        @NotBlank @Size(max = 120) String title,
        @Size(max = 2_000) String description,
        Boolean completed) {
}
