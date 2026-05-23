package com.example.todo;

import jakarta.validation.constraints.NotNull;

public record CompleteTodoRequest(@NotNull Boolean completed) {
}
