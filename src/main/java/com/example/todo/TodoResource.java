package com.example.todo;

import io.quarkus.panache.common.Sort;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.NotFoundException;
import jakarta.ws.rs.PATCH;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriInfo;
import java.net.URI;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.parameters.Parameter;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

@Path("/api/todos")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Tag(name = "Todos", description = "Todo CRUD operations")
public class TodoResource {

    @GET
    @Operation(summary = "List todos", description = "Returns todos ordered by newest first. Results can be filtered by completion status and title search.")
    @APIResponse(responseCode = "200", description = "Todos returned")
    public List<TodoResponse> list(
            @Parameter(description = "Filter by completion status") @QueryParam("completed") Boolean completed,
            @Parameter(description = "Case-insensitive title search") @QueryParam("q") String search) {
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

        return Todo.<Todo>find(query.toString(), Sort.descending("createdAt"), params)
                .stream()
                .map(TodoResponse::from)
                .toList();
    }

    @GET
    @Path("/{id}")
    @Operation(summary = "Get a todo")
    @APIResponse(responseCode = "200", description = "Todo found")
    @APIResponse(responseCode = "404", description = "Todo not found")
    public TodoResponse get(@Parameter(description = "Todo ID") @PathParam("id") UUID id) {
        return TodoResponse.from(findTodo(id));
    }

    @POST
    @Transactional
    @Operation(summary = "Create a todo")
    @APIResponse(
            responseCode = "201",
            description = "Todo created",
            content = @Content(schema = @Schema(implementation = TodoResponse.class)))
    @APIResponse(responseCode = "400", description = "Invalid request")
    public Response create(@Valid CreateTodoRequest request, @Context UriInfo uriInfo) {
        Todo todo = new Todo();
        todo.title = request.title().trim();
        todo.description = normalize(request.description());
        todo.completed = Boolean.TRUE.equals(request.completed());
        todo.persist();

        URI location = uriInfo.getAbsolutePathBuilder().path(todo.id.toString()).build();
        return Response.created(location).entity(TodoResponse.from(todo)).build();
    }

    @PUT
    @Path("/{id}")
    @Transactional
    @Operation(summary = "Replace a todo")
    @APIResponse(responseCode = "200", description = "Todo updated")
    @APIResponse(responseCode = "400", description = "Invalid request")
    @APIResponse(responseCode = "404", description = "Todo not found")
    public TodoResponse update(@Parameter(description = "Todo ID") @PathParam("id") UUID id, @Valid UpdateTodoRequest request) {
        Todo todo = findTodo(id);
        todo.title = request.title().trim();
        todo.description = normalize(request.description());
        todo.completed = request.completed();
        todo.touch();
        return TodoResponse.from(todo);
    }

    @PATCH
    @Path("/{id}/complete")
    @Transactional
    @Operation(summary = "Update todo completion status")
    @APIResponse(responseCode = "200", description = "Todo completion status updated")
    @APIResponse(responseCode = "400", description = "Invalid request")
    @APIResponse(responseCode = "404", description = "Todo not found")
    public TodoResponse setCompleted(@Parameter(description = "Todo ID") @PathParam("id") UUID id, @Valid CompleteTodoRequest request) {
        Todo todo = findTodo(id);
        todo.completed = request.completed();
        todo.touch();
        return TodoResponse.from(todo);
    }

    @DELETE
    @Path("/{id}")
    @Transactional
    @Operation(summary = "Delete a todo")
    @APIResponse(responseCode = "204", description = "Todo deleted")
    @APIResponse(responseCode = "404", description = "Todo not found")
    public Response delete(@Parameter(description = "Todo ID") @PathParam("id") UUID id) {
        Todo todo = findTodo(id);
        todo.delete();
        return Response.noContent().build();
    }

    private static Todo findTodo(UUID id) {
        Todo todo = Todo.findById(id);
        if (todo == null) {
            throw new NotFoundException("Todo not found: " + id);
        }
        return todo;
    }

    private static String normalize(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        return value.trim();
    }
}
