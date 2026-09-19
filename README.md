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
   failing test rather than being silently accepted.

## Design highlights
- **Service Object Model** (`Services` package): `BooksService` wraps every HTTP interaction
  with the Books endpoint (GET all, GET by id, POST, PUT, DELETE) behind readable method
  names. Test classes never build raw REST Assured requests themselves.
- **`BaseService`** builds the shared `RequestSpecification` once (base URI, content type,
  request/response logging, and the Allure REST Assured filter), so individual service
  methods don't repeat that setup.
- **Fluent design**: `BooksService` methods are built on REST Assured's fluent
  `given()/when()/then()` chain and return the raw `Response`, so tests apply whatever
  assertions (status code, JSON path, schema) fit the scenario.
- **`Models.Book`** uses Lombok `@Builder`/`@Data` for readable test-data construction and
  Jackson (de)serialization.
- **`BasesAndConfig` package** — shared utilities, same idea as the GUI project's:
  - `ConfigReader` / `ConfigManager` — generic classpath properties loader + typed
    accessors for the base URI, endpoint path, and timeout.
  - `TestDataLoader` — loads and maps `testdata/books.json` into `Book` objects, so JSON
    parsing isn't duplicated across tests.
  - `LogUtil` — a small static wrapper over log4j2.
- **`Listeners.TestListener`** clears stale Allure results before a run starts and logs each
  test's outcome. It's wired once via `@Listeners(TestListener.class)` on `tests.BaseTest`,
  so every test class inherits it without repeating the annotation.
- **Externalized configuration & data**: the base URI and endpoint path live in
  `src/main/resources/config.properties`; request payloads live in
  `src/test/resources/testdata/books.json`; expected/boundary values (existing id,
  non-existent id, expected status codes) live in `src/test/resources/testdata.properties`.
  No URLs, ids or status codes are hard-coded inside test methods.
- **Allure reporting**: `allure-rest-assured` automatically attaches the full request and
  response of every call to the report, and `@Step` annotations on `BooksService` methods
  show each API call as a readable step.

## Project structure
```
src/main/java/
  BasesAndConfig/  -> ConfigReader, ConfigManager, TestDataLoader, LogUtil
  Services/        -> BaseService, BooksService (Service Object Model)
  Models/          -> Book (POJO)
  Listeners/       -> TestListener
src/main/resources/
  config.properties
  log4j2.properties
  allure.properties
src/test/java/tests/
  BaseTest.java    -> wires up BooksService, @Listeners
  BooksApiTest.java
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
- Log files are written to `target/logs` (see `log4j2.properties`).
- Request/response logging is enabled on every call, so console output shows exactly what
  was sent and received — useful when investigating the API's mock/non-persistent behaviour.
- **Allure report**: results are written to `target/allure-results` on every `mvn test` run.
  To view the report:
  ```bash
  mvn allure:serve
  ```
  This downloads the Allure commandline automatically (no separate install needed), builds
  the report from `target/allure-results`, and opens it in your browser. Use `mvn allure:report`
  instead if you just want the static HTML written to `target/site/allure-maven-plugin`
  without opening a browser.

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
