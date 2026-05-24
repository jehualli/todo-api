package com.example.todo;

import io.quarkus.panache.common.Page;
import io.quarkus.panache.common.Sort;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.BadRequestException;
import jakarta.ws.rs.NotFoundException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@ApplicationScoped
public class TodoService {

    private static final int DEFAULT_PAGE = 0;
    private static final int DEFAULT_SIZE = 20;
    private static final int MAX_SIZE = 100;
    private static final Map<String, String> SORT_FIELDS = Map.of(
            "createdAt", "createdAt",
            "updatedAt", "updatedAt",
            "title", "title",
            "completed", "completed");

    public TodoPageResponse list(
            Boolean completed,
            String search,
            Integer page,
            Integer size,
            String sort,
            String direction) {
        int requestedPage = page == null ? DEFAULT_PAGE : page;
        int requestedSize = size == null ? DEFAULT_SIZE : size;

        if (requestedPage < 0) {
            throw new BadRequestException("page must be greater than or equal to 0");
        }
        if (requestedSize < 1 || requestedSize > MAX_SIZE) {
            throw new BadRequestException("size must be between 1 and " + MAX_SIZE);
        }

        String sortField = SORT_FIELDS.get(sort == null || sort.isBlank() ? "createdAt" : sort);
        if (sortField == null) {
            throw new BadRequestException("sort must be one of: createdAt, updatedAt, title, completed");
        }

        Sort.Direction sortDirection = parseDirection(direction);
        TodoQuery todoQuery = buildQuery(completed, search);

        List<TodoResponse> items = Todo.<Todo>find(todoQuery.query(), sort(sortField, sortDirection), todoQuery.params())
                .page(Page.of(requestedPage, requestedSize))
                .stream()
                .map(TodoResponse::from)
                .toList();
        long totalItems = Todo.count(todoQuery.query(), todoQuery.params());

        return TodoPageResponse.of(items, requestedPage, requestedSize, totalItems);
    }

    public TodoResponse get(UUID id) {
        return TodoResponse.from(findTodo(id));
    }

    @Transactional
    public TodoResponse create(CreateTodoRequest request) {
        Todo todo = new Todo();
        todo.title = request.title().trim();
        todo.description = normalize(request.description());
        todo.completed = Boolean.TRUE.equals(request.completed());
        todo.persist();
        return TodoResponse.from(todo);
    }

    @Transactional
    public TodoResponse update(UUID id, UpdateTodoRequest request) {
        Todo todo = findTodo(id);
        todo.title = request.title().trim();
        todo.description = normalize(request.description());
        todo.completed = request.completed();
        todo.touch();
        return TodoResponse.from(todo);
    }

    @Transactional
    public TodoResponse setCompleted(UUID id, CompleteTodoRequest request) {
        Todo todo = findTodo(id);
        todo.completed = request.completed();
        todo.touch();
        return TodoResponse.from(todo);
    }

    @Transactional
    public void delete(UUID id) {
        findTodo(id).delete();
    }

    private static Todo findTodo(UUID id) {
        Todo todo = Todo.findById(id);
        if (todo == null) {
            throw new NotFoundException("Todo not found: " + id);
        }
        return todo;
    }

    private static TodoQuery buildQuery(Boolean completed, String search) {
        StringBuilder query = new StringBuilder("1 = 1");
        Map<String, Object> params = new HashMap<>();

        if (completed != null) {
            query.append(" and completed = :completed");
            params.put("completed", completed);
        }

        if (search != null && !search.isBlank()) {
            query.append(" and lower(title) like :search");
            params.put("search", "%" + search.trim().toLowerCase() + "%");
        }

        return new TodoQuery(query.toString(), params);
    }

    private static Sort sort(String field, Sort.Direction direction) {
        return direction == Sort.Direction.Ascending ? Sort.ascending(field) : Sort.descending(field);
    }

    private static Sort.Direction parseDirection(String direction) {
        if (direction == null || direction.isBlank() || "desc".equalsIgnoreCase(direction)) {
            return Sort.Direction.Descending;
        }
        if ("asc".equalsIgnoreCase(direction)) {
            return Sort.Direction.Ascending;
        }
        throw new BadRequestException("direction must be either asc or desc");
    }

    private static String normalize(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        return value.trim();
    }

    private record TodoQuery(String query, Map<String, Object> params) {
    }
}
