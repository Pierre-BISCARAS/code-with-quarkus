package org.acme.testsupport;

import com.sun.net.httpserver.HttpServer;
import io.quarkus.test.common.QuarkusTestResourceLifecycleManager;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.Map;

/**
 * Demarre un tout petit serveur HTTP JDK qui joue le role de Clipper pendant
 * les tests : renvoie des reponses fixes et valides pour /api/stores/* et
 * /api/geo/distance. Evite de dependre d'une vraie instance Clipper, et
 * contourne les subtilites de mock CDI sur les beans @RestClient.
 */
public class FakeClipperResource implements QuarkusTestResourceLifecycleManager {

    private static final String STORE_JSON =
            "{\"id\":1,\"name\":\"STORE\",\"stock\":1000000,\"address\":\"Paris\",\"latitude\":48.0,\"longitude\":2.0}";
    private static final String DISTANCE_JSON =
            "{\"fromAddress\":\"Paris\",\"fromLatitude\":48.0,\"fromLongitude\":2.0,"
                    + "\"toAddress\":\"Amiens\",\"toLatitude\":49.0,\"toLongitude\":2.3,\"distanceKm\":130.0}";

    private HttpServer server;

    @Override
    public Map<String, String> start() {
        try {
            server = HttpServer.create(new InetSocketAddress("localhost", 0), 0);
            server.createContext("/api/stores", exchange -> respond(exchange, STORE_JSON));
            server.createContext("/api/geo/distance", exchange -> respond(exchange, DISTANCE_JSON));
            server.start();
            int port = server.getAddress().getPort();
            return Map.of("quarkus.rest-client.clipper-api.url", "http://localhost:" + port);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void respond(com.sun.net.httpserver.HttpExchange exchange, String body) throws IOException {
        byte[] bytes = body.getBytes(StandardCharsets.UTF_8);
        exchange.getResponseHeaders().add("Content-Type", "application/json");
        exchange.sendResponseHeaders(200, bytes.length);
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(bytes);
        }
    }

    @Override
    public void stop() {
        if (server != null) {
            server.stop(0);
        }
    }
}
