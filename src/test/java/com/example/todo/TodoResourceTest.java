package com.example.todo;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.notNullValue;

import io.quarkus.test.junit.QuarkusTest;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

@QuarkusTest
class TodoResourceTest {

    @BeforeEach
    @Transactional
    void cleanDatabase() {
        Todo.deleteAll();
    }

    @Test
    void createsAndReadsTodo() {
        String id = given()
                .contentType("application/json")
                .body("""
                        {
                          "title": "Build the API",
                          "description": "Create the Quarkus REST service"
                        }
                        """)
                .when()
                .post("/api/todos")
                .then()
                .statusCode(201)
                .header("Location", notNullValue())
                .body("id", notNullValue())
                .body("title", equalTo("Build the API"))
                .body("completed", equalTo(false))
                .extract()
                .path("id");

        given()
                .when()
                .get("/api/todos/{id}", id)
                .then()
                .statusCode(200)
                .body("id", equalTo(id))
                .body("description", equalTo("Create the Quarkus REST service"));
    }

    @Test
    void listsUpdatesCompletesAndDeletesTodo() {
        String id = given()
                .contentType("application/json")
                .body("""
                        {
                          "title": "Write tests"
                        }
                        """)
                .when()
                .post("/api/todos")
                .then()
                .statusCode(201)
                .extract()
                .path("id");

        given()
                .when()
                .get("/api/todos?completed=false&q=tests&page=0&size=10&sort=title&direction=asc")
                .then()
                .statusCode(200)
                .body("items", hasSize(1))
                .body("items[0].id", equalTo(id))
                .body("page", equalTo(0))
                .body("size", equalTo(10))
                .body("totalItems", equalTo(1))
                .body("totalPages", equalTo(1))
                .body("first", equalTo(true))
                .body("last", equalTo(true));

        given()
                .contentType("application/json")
                .body("""
                        {
                          "title": "Write API tests",
                          "description": "Cover CRUD flow",
                          "completed": false
                        }
                        """)
                .when()
                .put("/api/todos/{id}", id)
                .then()
                .statusCode(200)
                .body("title", equalTo("Write API tests"))
                .body("description", equalTo("Cover CRUD flow"));

        given()
                .contentType("application/json")
                .body("""
                        {
                          "completed": true
                        }
                        """)
                .when()
                .patch("/api/todos/{id}/complete", id)
                .then()
                .statusCode(200)
                .body("completed", equalTo(true));

        given()
                .when()
                .delete("/api/todos/{id}", id)
                .then()
                .statusCode(204);

        given()
                .when()
                .get("/api/todos/{id}", id)
                .then()
                .statusCode(404)
                .body("error", equalTo("NOT_FOUND"))
                .body("status", equalTo(404));
    }

    @Test
    void rejectsBlankTitle() {
        given()
                .contentType("application/json")
                .body("""
                        {
                          "title": " "
                        }
                        """)
                .when()
                .post("/api/todos")
                .then()
                .statusCode(400)
                .body("error", equalTo("VALIDATION_ERROR"))
                .body("fields", hasSize(1))
                .body("fields[0].field", equalTo("title"));
    }

    @Test
    void rejectsInvalidPagination() {
        given()
                .when()
                .get("/api/todos?page=-1")
                .then()
                .statusCode(400)
                .body("error", equalTo("BAD_REQUEST"))
                .body("message", equalTo("page must be greater than or equal to 0"));
    }
}
