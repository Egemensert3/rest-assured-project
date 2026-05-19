package com.testproject.utils;

import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;
import org.junit.jupiter.api.BeforeAll;

/**
 * Tüm test sınıfları için ortak yapılandırmayı içeren temel sınıf.
 * Base URL, Content Type ve loglama ayarları burada tanımlanır.
 */
public class BaseTest {

    protected static final String BASE_URL = "https://jsonplaceholder.typicode.com";

    // Beklenen maksimum yanıt süresi (milisaniye)
    protected static final long MAX_RESPONSE_TIME_MS = 3000L;

    protected static RequestSpecification requestSpec;

    @BeforeAll
    public static void setup() {
        RestAssured.baseURI = BASE_URL;

        requestSpec = new RequestSpecBuilder()
                .setBaseUri(BASE_URL)
                .setContentType(ContentType.JSON)
                .setAccept(ContentType.JSON)
                .addFilter(new RequestLoggingFilter())   // İstek logları
                .addFilter(new ResponseLoggingFilter())  // Yanıt logları
                .build();
    }
}
