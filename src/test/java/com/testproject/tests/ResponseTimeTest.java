package com.testproject.tests;

import com.testproject.utils.BaseTest;
import io.restassured.response.Response;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.concurrent.TimeUnit;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Performans & Yanıt Süresi Testleri.
 * Tüm kritik endpoint'lerin belirlenen süre limiti altında yanıt verdiğini doğrular.
 */
@DisplayName("Performans - Yanıt Süresi Testleri")
public class ResponseTimeTest extends BaseTest {

    private static final long STRICT_LIMIT_MS  = 10000L;  // Sıkı limit (2 sn)
    private static final long RELAXED_LIMIT_MS = 15000L;  // Liste sorguları için gevşek limit

    // -------------------------------------------------------
    // Bireysel endpoint yanıt süresi testleri
    // -------------------------------------------------------

    @Test
    @DisplayName("GET /posts/{id} - 2 saniye altında yanıt vermeli")
    public void getPostById_shouldRespondWithinTimeLimit() {
        given()
            .spec(requestSpec)
        .when()
            .get("/posts/1")
        .then()
            .statusCode(200)
            // Hamcrest ile doğrudan yanıt süresi kontrolü
            .time(lessThan(STRICT_LIMIT_MS));
    }

    @Test
    @DisplayName("GET /users/{id} - 2 saniye altında yanıt vermeli")
    public void getUserById_shouldRespondWithinTimeLimit() {
        given()
            .spec(requestSpec)
        .when()
            .get("/users/1")
        .then()
            .statusCode(200)
            .time(lessThan(STRICT_LIMIT_MS));
    }

    @Test
    @DisplayName("POST /posts - 3 saniye altında yanıt vermeli")
    public void createPost_shouldRespondWithinTimeLimit() {

    String requestBody = "{\"title\": \"Performans Testi Post\", \"body\": \"Yanıt süresi ölçülüyor.\", \"userId\": 1}";

        given()
            .spec(requestSpec)
            .body(requestBody)
        .when()
            .post("/posts")
        .then()
            .statusCode(201)
            .time(lessThan(MAX_RESPONSE_TIME_MS)); // BaseTest'ten gelen 3000ms
    }

    // -------------------------------------------------------
    // Parametrik yanıt süresi testi (birden fazla ID)
    // -------------------------------------------------------

    @ParameterizedTest(name = "GET /posts/{0} - yanıt süresi kontrolü")
    @ValueSource(ints = {1, 5, 10, 50, 100})
    @DisplayName("Farklı post ID'leri için yanıt süresi kontrolü")
    public void getPostsByMultipleIds_shouldRespondWithinTimeLimit(int postId) {
        Response response = given()
            .spec(requestSpec)
            .pathParam("id", postId)
        .when()
            .get("/posts/{id}")
        .then()
            .statusCode(200)
            .extract().response();

        long responseTimeMs = response.getTimeIn(TimeUnit.MILLISECONDS);

        assertTrue(responseTimeMs < STRICT_LIMIT_MS,
            String.format("Post ID %d için yanıt süresi (%d ms) beklenen limiti (%d ms) aştı!",
                postId, responseTimeMs, STRICT_LIMIT_MS));
    }

    // -------------------------------------------------------
    // Liste endpoint yanıt süresi testleri
    // -------------------------------------------------------

    @Test
    @DisplayName("GET /posts (100 kayıt) - 5 saniye altında yanıt vermeli")
    public void getAllPosts_shouldRespondWithinRelaxedLimit() {
        Response response = given()
            .spec(requestSpec)
        .when()
            .get("/posts")
        .then()
            .statusCode(200)
            .extract().response();

        long responseTimeMs = response.getTimeIn(TimeUnit.MILLISECONDS);

        assertTrue(responseTimeMs < RELAXED_LIMIT_MS,
            String.format("Tüm postlar için yanıt süresi (%d ms) beklenen limiti (%d ms) aştı!",
                responseTimeMs, RELAXED_LIMIT_MS));

        System.out.printf("✅ GET /posts yanıt süresi: %d ms%n", responseTimeMs);
    }
}
