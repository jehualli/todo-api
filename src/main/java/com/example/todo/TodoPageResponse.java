package com.example.todo;

import java.util.List;

public record TodoPageResponse(
        List<TodoResponse> items,
        int page,
        int size,
        long totalItems,
        int totalPages,
        boolean first,
        boolean last) {

    static TodoPageResponse of(List<TodoResponse> items, int page, int size, long totalItems) {
        int totalPages = size == 0 ? 0 : (int) Math.ceil((double) totalItems / size);
        return new TodoPageResponse(
                items,
                page,
                size,
                totalItems,
                totalPages,
                page == 0,
                totalPages == 0 || page >= totalPages - 1);
    }
}
