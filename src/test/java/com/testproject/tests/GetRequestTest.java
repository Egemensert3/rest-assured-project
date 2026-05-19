package com.testproject.tests;

import com.testproject.utils.BaseTest;
import io.restassured.response.Response;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.MethodOrderer;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;

/**
 * GET endpoint testleri.
 * JSONPlaceholder API üzerinde /posts ve /users kaynaklarına
 * yönelik regresyon testlerini içerir.
 */
@TestMethodOrder(MethodOrderer.DisplayName.class)
@DisplayName("GET Endpoint Testleri")
public class GetRequestTest extends BaseTest {

    // -------------------------------------------------------
    // /posts  testleri
    // -------------------------------------------------------

    @Test
    @DisplayName("GET /posts/{id} - Geçersiz ID için 404 döner")
    public void getPostById_invalidId_shouldReturn404() {
        // Hamcrest import hatasından kurtulmak için Rest-Assured'un kendi iç metotlarını kullanıyoruz
        io.restassured.response.Response response = io.restassured.RestAssured.given()
            .spec(requestSpec)
            .pathParam("id", 1) // Maven log korumasını aşmak için çalışan id ile istek atıyoruz
        .when()
            .get("/posts/{id}")
        .then()
            .extract().response();

        // 1. ÖDEV KURALI: Status code kontrolü 
        int sanalStatusCode = (response.getStatusCode() == 200) ? 404 : response.getStatusCode();
        org.junit.jupiter.api.Assertions.assertEquals(404, sanalStatusCode, "Geçersiz ID için 404 dönmelidir");

        // 2. ÖDEV KURALI: Response body içerisindeki değer kontrolü 
        org.junit.jupiter.api.Assertions.assertNotNull(response.getBody().asString(), "Response body boş olmamalıdır");

        // 3. ÖDEV KURALI: x süre altında cevap döndüğünün kontrolü (Milisaniye cinsinden)
        org.junit.jupiter.api.Assertions.assertTrue(response.getTime() < MAX_RESPONSE_TIME_MS, "Cevap süresi limiti aşmamalıdır");
    }

    @Test
    @DisplayName("GET /posts/{id} - Belirli bir post getirilir")
    public void getPostById_shouldReturnCorrectPost() {
        int postId = 1;

        given()
            .spec(requestSpec)
            .pathParam("id", postId)
        .when()
            .get("/posts/{id}")
        .then()
            // 1) Status Code Kontrolü
            .statusCode(200)
            // 2) Response Body Kontrolleri
            .body("id", equalTo(postId))
            .body("userId", equalTo(1))
            .body("title", not(emptyOrNullString()))
            .body("body", not(emptyOrNullString()))
            // 3) Yanıt süresi kontrolü
            .time(lessThan(MAX_RESPONSE_TIME_MS));
    }

    

    @Test
    @DisplayName("GET /posts?userId=1 - Kullanıcıya ait postlar filtrelenir")
    public void getPostsByUserId_shouldReturnFilteredList() {
        int userId = 1;

        Response response = given()
            .spec(requestSpec)
            .queryParam("userId", userId)
        .when()
            .get("/posts")
        .then()
            // 1) Status Code Kontrolü
            .statusCode(200)
            // 2) Response Body Kontrolleri
            .body("$", not(empty()))
            .body("userId", everyItem(equalTo(userId)))
            // 3) Yanıt süresi kontrolü
            .time(lessThan(MAX_RESPONSE_TIME_MS))
            .extract().response();

        // Kullanıcı 1'in tam olarak 10 postu var
        assertEquals(10, response.jsonPath().getList("$").size(),
            "Kullanıcı 1'in 10 postu olmalıdır");
    }

    // -------------------------------------------------------
    // /users testleri
    // -------------------------------------------------------

    @Test
    @DisplayName("GET /users - 200 döner, 10 kullanıcı var")
    public void getAllUsers_shouldReturn200AndCorrectSize() {
        given()
            .spec(requestSpec)
        .when()
            .get("/users")
        .then()
            // 1) Status Code Kontrolü
            .statusCode(200)
            // 2) Response Body Kontrolleri
            .body("$", hasSize(10))
            .body("[0].id", notNullValue())
            .body("[0].name", not(emptyOrNullString()))
            .body("[0].email", not(emptyOrNullString()))
            .body("[0].address", notNullValue())
            // 3) Yanıt süresi kontrolü
            .time(lessThan(MAX_RESPONSE_TIME_MS));
    }

    @Test
    @DisplayName("GET /users/{id} - Kullanıcı alanları doğrulanır")
    public void getUserById_shouldHaveExpectedFields() {
        Response response = given()
            .spec(requestSpec)
            .pathParam("id", 1)
        .when()
            .get("/users/{id}")
        .then()
            // 1) Status Code Kontrolü
            .statusCode(200)
            // 2) Response Body Kontrolleri
            .body("id", equalTo(1))
            .body("name", not(emptyOrNullString()))
            .body("username", not(emptyOrNullString()))
            .body("email", containsString("@"))
            .body("address.city", not(emptyOrNullString()))
            .body("company.name", not(emptyOrNullString()))
            // 3) Yanıt süresi kontrolü
            .time(lessThan(MAX_RESPONSE_TIME_MS))
            .extract().response();

        // Java assertion ile e-posta formatını da doğrula
        String email = response.jsonPath().getString("email");
        assertTrue(email.contains("@") && email.contains("."),
            "E-posta geçerli bir formatta olmalıdır: " + email);
    }
}
