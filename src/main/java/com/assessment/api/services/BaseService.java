package com.assessment.api.services;

import com.assessment.api.utils.ConfigManager;
import io.qameta.allure.restassured.AllureRestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;

/**
 * Base class for all "service" classes (Service Object Model).
 * Centralizes the RequestSpecification (base URI, content type, timeouts, logging)
 * so individual services only need to describe their endpoints and don't duplicate
 * REST Assured setup/configuration. The AllureRestAssured filter automatically attaches
 * the full request and response of every call to the Allure report.
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
