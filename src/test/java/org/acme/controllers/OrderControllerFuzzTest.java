package org.acme.controllers;

import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import org.acme.testsupport.FakeClipperResource;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;

import java.util.Random;
import java.util.UUID;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.anyOf;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Fuzzing "maison" sur les endpoints /api/orders : corps JSON aleatoires et
 * malformes (jamais de 500), et verification que l'ownership ne fuite jamais
 * les donnees d'un autre client. Clipper est remplace par FakeClipperResource
 * (petit serveur HTTP JDK) pour que le test ne depende pas d'une vraie
 * instance Clipper.
 */
@QuarkusTest
@QuarkusTestResource(FakeClipperResource.class)
class OrderControllerFuzzTest {

    private final Random random = new Random(7);

    private String registerAndReturnEmail() {
        String email = "fuzz-" + UUID.randomUUID() + "@test.com";
        given().contentType(ContentType.JSON)
                .body("{\"email\":\"" + email + "\",\"password\":\"secret123\",\"address\":\"Amiens\"}")
                .when().post("/api/auth/register")
                .then().statusCode(201);
        return email;
    }

    @Test
    void orders_withoutAuth_shouldReturn401() {
        given().when().get("/api/orders").then().statusCode(401);
    }

    @RepeatedTest(100)
    void placeOrder_randomMalformedBody_shouldNeverReturn500() {
        String email = registerAndReturnEmail();
        String body = randomOrderJson();

        int status = given()
                .auth().preemptive().basic(email, "secret123")
                .contentType(ContentType.JSON)
                .body(body)
                .when().post("/api/orders")
                .then()
                .extract().statusCode();

        assertTrue(status < 500, "order crashed (status " + status + ") on body=" + body);
    }

    private String randomOrderJson() {
        return switch (random.nextInt(6)) {
            case 0 -> "{}";
            case 1 -> "{\"storeId\":1,\"quantity\":-5,\"deliveryType\":\"BASIC\"}";
            case 2 -> "{\"storeId\":1,\"quantity\":0,\"deliveryType\":\"ULTRA\"}";
            case 3 -> "{\"storeId\":1,\"quantity\":3,\"deliveryType\":\"NOT_A_TYPE\"}";
            case 4 -> "{\"storeId\":\"not-a-number\",\"quantity\":3,\"deliveryType\":\"BASIC\"}";
            default -> "{\"storeId\":1,\"quantity\":" + (1 + random.nextInt(1000)) + ",\"deliveryType\":\"EXPRESS\"}";
        };
    }

    @Test
    void getOrder_anotherCustomersOrder_shouldNotLeakData() {
        String ownerEmail = registerAndReturnEmail();
        int orderId = given()
                .auth().preemptive().basic(ownerEmail, "secret123")
                .contentType(ContentType.JSON)
                .body("{\"storeId\":1,\"quantity\":2,\"deliveryType\":\"BASIC\"}")
                .when().post("/api/orders")
                .then().statusCode(201)
                .extract().path("id");

        String attackerEmail = registerAndReturnEmail();
        given()
                .auth().preemptive().basic(attackerEmail, "secret123")
                .when().get("/api/orders/" + orderId)
                .then().statusCode(403);
    }

    @RepeatedTest(30)
    void getOrder_randomId_shouldNeverReturn500OrLeakUnownedData() {
        String email = registerAndReturnEmail();
        long randomId = random.nextInt(100_000);

        given()
                .auth().preemptive().basic(email, "secret123")
                .when().get("/api/orders/" + randomId)
                .then().statusCode(anyOf(is(403), is(404)));
    }
}
