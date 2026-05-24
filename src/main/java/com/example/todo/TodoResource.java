package com.example.todo;

import jakarta.validation.Valid;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
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

    private final TodoService todoService;

    public TodoResource(TodoService todoService) {
        this.todoService = todoService;
    }

    @GET
    @Operation(summary = "List todos", description = "Returns todos ordered by newest first. Results can be filtered by completion status and title search.")
    @APIResponse(
            responseCode = "200",
            description = "Todos returned",
            content = @Content(schema = @Schema(implementation = TodoPageResponse.class)))
    @APIResponse(
            responseCode = "400",
            description = "Invalid pagination or sorting request",
            content = @Content(schema = @Schema(implementation = ApiError.class)))
    public TodoPageResponse list(
            @Parameter(description = "Filter by completion status") @QueryParam("completed") Boolean completed,
            @Parameter(description = "Case-insensitive title search") @QueryParam("q") String search,
            @Parameter(description = "Zero-based page number") @QueryParam("page") Integer page,
            @Parameter(description = "Page size, from 1 to 100") @QueryParam("size") Integer size,
            @Parameter(description = "Sort field: createdAt, updatedAt, title, completed") @QueryParam("sort") String sort,
            @Parameter(description = "Sort direction: asc or desc") @QueryParam("direction") String direction) {
        return todoService.list(completed, search, page, size, sort, direction);
    }

    @GET
    @Path("/{id}")
    @Operation(summary = "Get a todo")
    @APIResponse(responseCode = "200", description = "Todo found")
    @APIResponse(
            responseCode = "404",
            description = "Todo not found",
            content = @Content(schema = @Schema(implementation = ApiError.class)))
    public TodoResponse get(@Parameter(description = "Todo ID") @PathParam("id") UUID id) {
        return todoService.get(id);
    }

    @POST
    @Operation(summary = "Create a todo")
    @APIResponse(
            responseCode = "201",
            description = "Todo created",
            content = @Content(schema = @Schema(implementation = TodoResponse.class)))
    @APIResponse(
            responseCode = "400",
            description = "Invalid request",
            content = @Content(schema = @Schema(implementation = ApiError.class)))
    public Response create(@Valid CreateTodoRequest request, @Context UriInfo uriInfo) {
        TodoResponse todo = todoService.create(request);

        URI location = uriInfo.getAbsolutePathBuilder().path(todo.id().toString()).build();
        return Response.created(location).entity(todo).build();
    }

    @PUT
    @Path("/{id}")
    @Operation(summary = "Replace a todo")
    @APIResponse(responseCode = "200", description = "Todo updated")
    @APIResponse(
            responseCode = "400",
            description = "Invalid request",
            content = @Content(schema = @Schema(implementation = ApiError.class)))
    @APIResponse(
            responseCode = "404",
            description = "Todo not found",
            content = @Content(schema = @Schema(implementation = ApiError.class)))
    public TodoResponse update(@Parameter(description = "Todo ID") @PathParam("id") UUID id, @Valid UpdateTodoRequest request) {
        return todoService.update(id, request);
    }

    @PATCH
    @Path("/{id}/complete")
    @Operation(summary = "Update todo completion status")
    @APIResponse(responseCode = "200", description = "Todo completion status updated")
    @APIResponse(
            responseCode = "400",
            description = "Invalid request",
            content = @Content(schema = @Schema(implementation = ApiError.class)))
    @APIResponse(
            responseCode = "404",
            description = "Todo not found",
            content = @Content(schema = @Schema(implementation = ApiError.class)))
    public TodoResponse setCompleted(@Parameter(description = "Todo ID") @PathParam("id") UUID id, @Valid CompleteTodoRequest request) {
        return todoService.setCompleted(id, request);
    }

    @DELETE
    @Path("/{id}")
    @Operation(summary = "Delete a todo")
    @APIResponse(responseCode = "204", description = "Todo deleted")
    @APIResponse(
            responseCode = "404",
            description = "Todo not found",
            content = @Content(schema = @Schema(implementation = ApiError.class)))
    public Response delete(@Parameter(description = "Todo ID") @PathParam("id") UUID id) {
        todoService.delete(id);
        return Response.noContent().build();
    }
}
