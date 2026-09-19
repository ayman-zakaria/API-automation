package Services;

import BasesAndConfig.ConfigManager;
import io.qameta.allure.restassured.AllureRestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;

/* Every service class builds its requests off this. Keeps the base URI, content
 * type and logging/Allure filters in one spot instead of repeated in each service.
 */
public abstract class BaseService {

    protected RequestSpecification requestSpec() {
        return new RequestSpecBuilder()
                .setBaseUri(ConfigManager.baseUri())
                .setContentType(ContentType.JSON)
                .setAccept(ContentType.JSON)
                .addFilter(new RequestLoggingFilter())
                .addFilter(new ResponseLoggingFilter())
                .addFilter(new AllureRestAssured())
                .build();
    }
}
