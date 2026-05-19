package com.testproject.tests;

import com.testproject.utils.BaseTest;
import io.restassured.response.Response;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.MethodOrderer;

import java.util.HashMap;
import java.util.Map;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;

/**
 * POST endpoint testleri.
 * JSONPlaceholder API üzerinde /posts ve /comments kaynaklarına
 * JSON request body ile yapılan POST çağrılarının regresyon testlerini içerir.
 */
@TestMethodOrder(MethodOrderer.DisplayName.class)
@DisplayName("POST Endpoint Testleri")
public class PostRequestTest extends BaseTest {

    // -------------------------------------------------------
    // /posts  POST testleri
    // -------------------------------------------------------

    @Test
    @DisplayName("POST /posts - Yeni post oluşturulur, 201 döner")
    public void createPost_shouldReturn201AndCreatedResource() {

        // Request Body (JSON Map)
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("title", "Otomatik Test ile Oluşturulmuş Post");
        requestBody.put("body", "Bu post REST Assured kullanılarak JUnit testi ile oluşturulmuştur.");
        requestBody.put("userId", 1);

        given()
            .spec(requestSpec)
            .body(requestBody)
        .when()
            .post("/posts")
        .then()
            // 1) Status Code Kontrolü
            .statusCode(201)
            // 2) Response Body Kontrolleri
            .body("id", notNullValue())
            .body("title", equalTo("Otomatik Test ile Oluşturulmuş Post"))
            .body("body", equalTo("Bu post REST Assured kullanılarak JUnit testi ile oluşturulmuştur."))
            .body("userId", equalTo(1))
            // 3) Yanıt süresi kontrolü
            .time(lessThan(MAX_RESPONSE_TIME_MS));
    }

    @Test
    @DisplayName("POST /posts - Oluşturulan kaynak ID alır")
    public void createPost_shouldReturnGeneratedId() {

        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("title", "Test Başlığı");
        requestBody.put("body", "Test içeriği buraya gelecek.");
        requestBody.put("userId", 2);

        Response response = given()
            .spec(requestSpec)
            .body(requestBody)
        .when()
            .post("/posts")
        .then()
            // 1) Status Code Kontrolü
            .statusCode(201)
            // 3) Yanıt süresi kontrolü
            .time(lessThan(MAX_RESPONSE_TIME_MS))
            .extract().response();

        // 2) Response Body Kontrolleri (Java assertion)
        int generatedId = response.jsonPath().getInt("id");
        assertTrue(generatedId > 0,
            "Oluşturulan kaynağa pozitif bir ID atanmalıdır, alınan: " + generatedId);

        String returnedTitle = response.jsonPath().getString("title");
        assertEquals("Test Başlığı", returnedTitle,
            "Dönen title gönderilen title ile aynı olmalıdır");
    }

    // -------------------------------------------------------
    // /comments  POST testleri (XML request body örneği)
    // -------------------------------------------------------

    @Test
    @DisplayName("POST /comments - JSON body ile yorum oluşturulur")
    public void createComment_withJsonBody_shouldReturn201() {

        // Request Body (JSON Map - iç içe yapı)
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("postId", 1);
        requestBody.put("name", "Test Yorumcusu");
        requestBody.put("email", "test@example.com");
        requestBody.put("body", "Bu bir otomatik test yorumudur. REST Assured ile oluşturuldu.");

        given()
            .spec(requestSpec)
            .body(requestBody)
        .when()
            .post("/comments")
        .then()
            // 1) Status Code Kontrolü
            .statusCode(201)
            // 2) Response Body Kontrolleri
            .body("id", notNullValue())
            .body("postId", equalTo(1))
            .body("name", equalTo("Test Yorumcusu"))
            .body("email", equalTo("test@example.com"))
            .body("body", containsString("otomatik test"))
            // 3) Yanıt süresi kontrolü
            .time(lessThan(MAX_RESPONSE_TIME_MS));
    }

    @Test
    @DisplayName("POST /posts - Eksik alan ile gönderimde yanıt incelenir")
    public void createPost_withMinimalBody_shouldStillReturn201() {
        // JSONPlaceholder esnek bir mock API olduğundan eksik alanı da kabul eder;
        // gerçek bir API'de 400 beklenir — bu test davranışı belgeleme amaçlıdır.
        Map<String, Object> minimalBody = new HashMap<>();
        minimalBody.put("userId", 5);

        Response response = given()
            .spec(requestSpec)
            .body(minimalBody)
        .when()
            .post("/posts")
        .then()
            // 1) Status Code Kontrolü
            .statusCode(201)
            // 3) Yanıt süresi kontrolü
            .time(lessThan(MAX_RESPONSE_TIME_MS))
            .extract().response();

        // 2) Response Body Kontrolü
        assertNotNull(response.jsonPath().get("id"),
            "Mock API'den bile ID dönmelidir");
    }
}
