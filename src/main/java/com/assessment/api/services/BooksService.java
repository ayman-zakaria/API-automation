package com.assessment.api.services;

import com.assessment.api.models.Book;
import com.assessment.api.utils.ConfigManager;
import io.restassured.response.Response;

import static io.restassured.RestAssured.given;

/**
 * Service Object encapsulating every interaction with the /api/v1/Books endpoint.
 * Test classes call these methods instead of building raw REST Assured requests,
 * which keeps HTTP details (path, method, serialization) in one reusable place.
 */
public class BooksService extends BaseService {

    private final String booksPath = ConfigManager.booksPath();

    public Response getAllBooks() {
        return given()
                .spec(requestSpec())
                .when()
                .get(booksPath);
    }

    public Response getBookById(int id) {
        return given()
                .spec(requestSpec())
                .pathParam("id", id)
                .when()
                .get(booksPath + "/{id}");
    }

    public Response createBook(Book book) {
        return given()
                .spec(requestSpec())
                .body(book)
                .when()
                .post(booksPath);
    }

    public Response updateBook(int id, Book book) {
        return given()
                .spec(requestSpec())
                .pathParam("id", id)
                .body(book)
                .when()
                .put(booksPath + "/{id}");
    }

    public Response deleteBook(int id) {
        return given()
                .spec(requestSpec())
                .pathParam("id", id)
                .when()
                .delete(booksPath + "/{id}");
    }
}
