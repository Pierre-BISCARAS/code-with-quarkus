package org.acme.controllers;

import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;

import java.util.Random;
import java.util.UUID;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Fuzzing "maison" sur POST /api/auth/register : on envoie des centaines de
 * payloads aleatoires/malformes et on verifie qu'on ne provoque jamais une
 * erreur serveur (500). Une entree invalide doit etre rejetee proprement (400),
 * jamais faire planter l'application.
 */
@QuarkusTest
class CustomerRegistrationFuzzTest {

    private static final int ITERATIONS = 200;

    private final Random random = new Random(1234);

    @Test
    void register_blankEmail_shouldBeRejectedWith400() {
        given().contentType(ContentType.JSON)
                .body("{\"email\":\"\",\"password\":\"secret123\",\"address\":\"Amiens\"}")
                .when().post("/api/auth/register")
                .then().statusCode(400);
    }

    @Test
    void register_blankPassword_shouldBeRejectedWith400() {
        given().contentType(ContentType.JSON)
                .body("{\"email\":\"blank-pw-" + UUID.randomUUID() + "@test.com\",\"password\":\"\",\"address\":\"Amiens\"}")
                .when().post("/api/auth/register")
                .then().statusCode(400);
    }

    @Test
    void register_missingFields_shouldBeRejectedWith400() {
        given().contentType(ContentType.JSON)
                .body("{}")
                .when().post("/api/auth/register")
                .then().statusCode(400);
    }

    @RepeatedTest(ITERATIONS)
    void register_randomPayload_shouldNeverReturn500() {
        String email = randomEmailField();
        String password = randomField();
        String address = randomField();

        String body = "{\"email\":" + jsonValue(email)
                + ",\"password\":" + jsonValue(password)
                + ",\"address\":" + jsonValue(address) + "}";

        int status = given()
                .contentType(ContentType.JSON)
                .body(body)
                .when().post("/api/auth/register")
                .then()
                .extract().statusCode();

        assertTrue(status < 500,
                "register crashed (status " + status + ") on email=" + email
                        + " password=" + password + " address=" + address);
    }

    private String randomEmailField() {
        return switch (random.nextInt(5)) {
            case 0 -> "";
            case 1 -> "not-an-email";
            case 2 -> UUID.randomUUID() + "@test.com";
            case 3 -> "a".repeat(500) + "@test.com";
            default -> "unicode-🚀-" + UUID.randomUUID() + "@test.com";
        };
    }

    private String randomField() {
        return switch (random.nextInt(4)) {
            case 0 -> "";
            case 1 -> "x".repeat(2000);
            case 2 -> "'; DROP TABLE test_user; --";
            default -> "normal value " + random.nextInt();
        };
    }

    private String jsonValue(String raw) {
        return "\"" + raw.replace("\\", "\\\\").replace("\"", "\\\"") + "\"";
    }
}
