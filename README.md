# API Test Automation — FakeRESTApi Bookstore

## Overview
This project automates test coverage for the `/api/v1/Books` endpoints of the
[FakeRESTApi Bookstore](https://fakerestapi.azurewebsites.net) using **Java, REST Assured,
TestNG and Maven**, structured with a **Service Object Model**.

Scenarios covered:
1. **Happy path** — `GET /api/v1/Books` returns a non-empty list of well-formed book objects.
2. **Happy path** — `POST /api/v1/Books` accepts a new book and echoes back the submitted fields.
3. **Edge / negative** — `GET /api/v1/Books/{id}` for a non-existent id is expected to return
   `404 Not Found`. FakeRESTApi is a mock backend and has historically been observed to
   instead return `200 OK` with a synthetically generated book for out-of-range ids; the
   test intentionally asserts the *correct* REST semantics so this discrepancy surfaces as a
   failing test rather than being silently accepted. See the Javadoc on
   `getBookById_withNonExistentId_shouldReturnNotFound` for details.

## Design highlights
- **Service Object Model**: `BooksService` encapsulates every HTTP interaction with the Books
  endpoint (GET all, GET by id, POST, PUT, DELETE) behind readable method names. Test classes
  never build raw REST Assured requests themselves.
- **BaseService**: builds the shared `RequestSpecification` (base URI, content type, request /
  response logging) once, so services don't duplicate setup.
- **Fluent design**: `BooksService` methods are built on REST Assured's fluent
  `given()/when()/then()` chain, and return the raw `Response` so tests can apply whatever
  assertions (status code, JSON path, schema) fit the scenario.
- **POJO model with builder**: `Book` uses Lombok `@Builder`/`@Data` for concise, readable test
  data construction and Jackson (de)serialization.
- **Externalized configuration & data**: the base URI and endpoint path live in
  `src/main/resources/config.properties`; request payloads live in
  `src/test/resources/testdata/books.json`; expected/boundary values (existing id,
  non-existent id, expected status codes) live in `src/test/resources/testdata.properties`.
  No URLs, ids or status codes are hard-coded inside test methods.
- **TestDataLoader**: a single utility loads and maps `books.json` into `Book` objects, so
  JSON parsing logic isn't duplicated across tests.

## Project structure
```
src/main/java/com/assessment/api/
  models/    -> Book (POJO)
  services/  -> BaseService, BooksService (Service Object Model)
  utils/     -> ConfigManager, PropertiesReader, TestDataLoader
src/main/resources/config.properties
src/test/java/com/assessment/api/
  base/      -> BaseApiTest
  tests/     -> BooksApiTest
src/test/resources/
  testng.xml
  testdata.properties
  testdata/books.json
```

## Prerequisites
- Java 17+
- Maven 3.8+
- Internet access to `fakerestapi.azurewebsites.net`

## How to run
```bash
mvn clean test
```
This runs the suite defined in `src/test/resources/testng.xml`.

### Run a single test
```bash
mvn test -Dtest=BooksApiTest#getAllBooks_shouldReturnListOfBooks
```

## Reports & artifacts
- TestNG's default HTML/XML reports are generated under `target/surefire-reports`.
- Request/response logging is enabled on every call (`RequestLoggingFilter` /
  `ResponseLoggingFilter`) so console output shows exactly what was sent and received —
  useful when investigating the API's mock/non-persistent behaviour.

## Notes on FakeRESTApi behaviour
FakeRESTApi is a demo/mock API: `POST`, `PUT`, and `DELETE` calls are accepted and return a
plausible response, but nothing is actually persisted server-side. This means:
- A `POST` followed by a `GET` for the same id will **not** return the data just created.
- Payload validation is minimal — the API generally accepts malformed data (e.g. blank
  titles, negative page counts) without rejecting it.

These are documented here rather than treated as defects, since they're inherent to using a
public mock API; the edge-case test above is written to fail loudly if the specific
not-found behaviour deviates from correct REST semantics, since that is the kind of anomaly
worth flagging to a real API's owners.
