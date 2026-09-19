package Services;

import BasesAndConfig.ConfigManager;
import Models.Book;
import io.qameta.allure.Step;
import io.restassured.response.Response;

import static io.restassured.RestAssured.given;

// wraps every call to /api/v1/Books behind readable method names. Tests call these
// instead of building raw REST Assured requests themselves.
public class BooksService extends BaseService {

    private final String booksPath = ConfigManager.booksPath();

    @Step("GET all books")
    public Response getAllBooks() {
        return given()
                .spec(requestSpec())
                .when()
                .get(booksPath);
    }

    @Step("GET book by id: {id}")
    public Response getBookById(int id) {
        return given()
                .spec(requestSpec())
                .pathParam("id", id)
                .when()
                .get(booksPath + "/{id}");
    }

    @Step("POST new book: {book.title}")
    public Response createBook(Book book) {
        return given()
                .spec(requestSpec())
                .body(book)
                .when()
                .post(booksPath);
    }

    @Step("PUT update book id: {id}")
    public Response updateBook(int id, Book book) {
        return given()
                .spec(requestSpec())
                .pathParam("id", id)
                .body(book)
                .when()
                .put(booksPath + "/{id}");
    }

    @Step("DELETE book id: {id}")
    public Response deleteBook(int id) {
        return given()
                .spec(requestSpec())
                .pathParam("id", id)
                .when()
                .delete(booksPath + "/{id}");
    }
}
