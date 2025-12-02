package utils;

import io.restassured.response.Response;
import org.slf4j.Logger;
import java.util.EnumMap;
import java.util.Map;

import static io.restassured.RestAssured.given;

public class AuthManager {

    private static final Logger logger = LoggerUtil.getLogger(AuthManager.class);

    private static final Map<Role, String> tokenCache = new EnumMap<>(Role.class);
    private static Role currentRole = null;  // NEW

    private static final Object lock = new Object();

    public static String getToken(Role role) {
        synchronized (lock) {

            currentRole = role;  // track active role

            if (tokenCache.containsKey(role)) {
                return tokenCache.get(role);
            }

            logger.info("No cached token for {} — performing login", role);

            String email = ConfigReader.get(role.name().toLowerCase() + ".email");
            String password = ConfigReader.get(role.name().toLowerCase() + ".password");

            if (email == null || password == null) {
                throw new RuntimeException("Credentials missing for role: " + role);
            }

            String token = performLogin(email, password);
            tokenCache.put(role, token);

            logger.info("Token cached for role {}", role);
            return token;
        }
    }

    public static String setToken() {
        if (currentRole == null) {
            throw new IllegalStateException("No active role selected. Call getToken(Role) first.");
        }
        return tokenCache.get(currentRole);
    }

    private static String performLogin(String email, String password) {
        Response response = given()
                .spec(RequestBuilder.getRequestSpec())
                .body("{\"email\":\"" + email + "\",\"password\":\"" + password + "\"}")
                .post("/auth/login");

        if (response.getStatusCode() != 200) {
            logger.error("Login failed: {}", response.asPrettyString());
            throw new RuntimeException("Login failed. Status=" + response.getStatusCode());
        }

        String token = response.jsonPath().getString("access_token");

        if (token == null || token.isBlank()) {
            throw new RuntimeException("Login response missing access_token");
        }

        return token;
    }
}