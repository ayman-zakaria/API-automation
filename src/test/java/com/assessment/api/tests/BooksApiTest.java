package com.assessment.api.tests;

import com.assessment.api.base.BaseApiTest;
import com.assessment.api.models.Book;
import com.assessment.api.utils.TestDataLoader;
import io.restassured.response.Response;
import org.testng.Assert;
import org.testng.annotations.Test;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.notNullValue;

/**
 * Test coverage for the FakeRESTApi Bookstore /api/v1/Books endpoint.
 *
 * NOTE: FakeRESTApi is a mock service. Create/update/delete calls are accepted and echoed
 * back but are NOT persisted server-side, and it is known to be lenient about validating
 * payloads. Where the assessment asked to "validate the returned API response and note
 * any unexpected behaviour", the relevant observations are called out inline below.
 */
public class BooksApiTest extends BaseApiTest {

    // ---------- Happy path #1 ----------

    @Test(description = "GET /Books returns a non-empty list of well-formed book objects")
    public void getAllBooks_shouldReturnListOfBooks() {
        Response response = booksService.getAllBooks();

        response.then()
                .statusCode(Integer.parseInt(TEST_DATA.get("http.status.ok")))
                .body("size()", greaterThan(0))
                .body("[0].id", notNullValue())
                .body("[0].title", notNullValue());
    }

    // ---------- Happy path #2 ----------

    @Test(description = "POST /Books accepts a new book and echoes back the submitted fields")
    public void createBook_shouldReturnSubmittedBookDetails() {
        Book newBook = TestDataLoader.newBook();

        Response response = booksService.createBook(newBook);

        response.then()
                .statusCode(Integer.parseInt(TEST_DATA.get("http.status.created")))
                .body("id", equalTo(newBook.getId()))
                .body("title", equalTo(newBook.getTitle()))
                .body("pageCount", equalTo(newBook.getPageCount()));

        // Observation: FakeRESTApi does not persist created resources. A subsequent GET for
        // this id will return whatever the mock backend generates, NOT the payload above.
        // This is expected mock-server behaviour, not a defect, but is worth flagging to
        // stakeholders relying on this API for stateful test scenarios.
    }

    // ---------- Edge / negative case ----------

    @Test(description = "GET /Books/{id} for a non-existent id should return 404 Not Found")
    public void getBookById_withNonExistentId_shouldReturnNotFound() {
        int nonExistentId = Integer.parseInt(TEST_DATA.get("books.nonExistentId"));

        Response response = booksService.getBookById(nonExistentId);

        int actualStatus = response.getStatusCode();
        int expectedStatus = Integer.parseInt(TEST_DATA.get("http.status.notFound"));

        // Unexpected-behaviour note: FakeRESTApi has been observed in the past to return
        // 200 OK with a synthetically generated book object for ids outside its in-memory
        // range, instead of a 404. Correct REST semantics call for 404 here, so the
        // assertion below intentionally enforces that expectation. If this test fails
        // against the live environment, that is the anomaly to report - the API is
        // returning a fabricated object for a resource that does not exist rather than
        // signalling "not found".
        Assert.assertEquals(actualStatus, expectedStatus,
                "Expected 404 Not Found for a non-existent book id; if the API instead "
                        + "returns 200 with a generated payload, that is a notable API "
                        + "design/validation gap to report (see test Javadoc).");
    }
}
