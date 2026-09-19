package tests;

import BasesAndConfig.ConfigReader;
import BasesAndConfig.TestDataLoader;
import Models.Book;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import io.restassured.response.Response;
import org.testng.Assert;
import org.testng.annotations.Test;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.notNullValue;

// validate the shape of what you send it (see the invalid-payload test below,
// confirmed against the live API rather than assumed).
@Epic("FakeRESTApi - API Assessment")
@Feature("Books")
public class BooksApiTest extends BaseTest {

    private static final ConfigReader TEST_DATA = new ConfigReader("testdata.properties");

    @Test(description = "GET /Books returns a non-empty list of well-formed book objects")
    @Severity(SeverityLevel.CRITICAL)
    public void getAllBooks_shouldReturnListOfBooks() {
        Response response = booksService.getAllBooks();

        response.then()
                .statusCode(Integer.parseInt(TEST_DATA.get("http.status.ok")))
                .body("size()", greaterThan(0))
                .body("[0].id", notNullValue())
                .body("[0].title", notNullValue());
    }

    @Test(description = "POST /Books accepts a new book and echoes back the submitted fields")
    @Severity(SeverityLevel.CRITICAL)
    public void createBook_shouldReturnSubmittedBookDetails() {
        Book newBook = TestDataLoader.newBook();

        Response response = booksService.createBook(newBook);

        response.then()
                .statusCode(Integer.parseInt(TEST_DATA.get("http.status.created")))
                .body("id", equalTo(newBook.getId()))
                .body("title", equalTo(newBook.getTitle()))
                .body("pageCount", equalTo(newBook.getPageCount()));

        /* worth knowing: a GET on this same id afterwards won't return what we just
         *posted - FakeRESTApi doesn't persist anything, it just echoes the payload
         * back on the call that submitted it. Not a defect, just how the mock works.
        */
    }

    // negative case - correct REST semantics say a non-existent id should 404.
    @Test(description = "GET /Books/{id} for a non-existent id should return 404 Not Found")
    @Severity(SeverityLevel.NORMAL)
    public void getBookById_withNonExistentId_shouldReturnNotFound() {
        int nonExistentId = Integer.parseInt(TEST_DATA.get("books.nonExistentId"));

        Response response = booksService.getBookById(nonExistentId);

        Assert.assertEquals(response.getStatusCode(), Integer.parseInt(TEST_DATA.get("http.status.notFound")),
                "Expected 404 for a non-existent book id - if the API returned 200 with a "
                        + "generated payload instead, that's the anomaly to report, not a bug in this test");
    }

    /* negative case - sending a deliberately broken payload (blank title, negative id
     * and page count, garbage date string). Turns out FakeRESTApi actually does validate
     * this - it comes back with a 400, which is the correct behaviour for a real API.
     */
    @Test(description = "POST /Books with an invalid payload should be rejected with 400 Bad Request")
    @Severity(SeverityLevel.NORMAL)
    public void createBook_withInvalidPayload_shouldReturnBadRequest() {
        Book invalidBook = TestDataLoader.invalidBook();

        Response response = booksService.createBook(invalidBook);

        Assert.assertEquals(response.getStatusCode(),
                Integer.parseInt(TEST_DATA.get("http.status.invalidPayload")),
                "Expected FakeRESTApi to reject this invalid payload with 400 Bad Request");
    }

    // negative case - deleting something that was never there in the first place.
    @Test(description = "DELETE /Books/{id} for a non-existent id should ideally be a 404, not a silent success")
    @Severity(SeverityLevel.MINOR)
    public void deleteBook_withNonExistentId_shouldExposeMissingExistenceCheck() {
        int nonExistentId = Integer.parseInt(TEST_DATA.get("books.nonExistentId"));

        Response response = booksService.deleteBook(nonExistentId);

        Assert.assertEquals(response.getStatusCode(),
                Integer.parseInt(TEST_DATA.get("http.status.deleteNonExistentBook")),
                "FakeRESTApi is expected to return 200 here even though the id was never "
                        + "created - a real API should probably 404 instead");
    }
}