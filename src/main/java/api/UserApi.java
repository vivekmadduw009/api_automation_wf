package api;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import utils.ConfigReader;
import utils.LoggerUtil;
import org.slf4j.Logger;

public class UserApi {

    private static final Logger logger = LoggerUtil.getLogger(UserApi.class);
    private Response response;

    private final String baseUrl = ConfigReader.get("baseUrl");

    public void getUserById(int id) {

        logger.info("Calling GET {} /api/users/{}", baseUrl, id);

        response = RestAssured
                .given()
                .baseUri(baseUrl)
                .log().uri()
                .when()
                .get("/api/users/" + id);
        logger.info("Response status: {}", response.getStatusCode());
        logger.debug("Response body: {}", response.getBody().asPrettyString());
    }

    public void verifyStatusCode(int expectedStatus) {
        int actual = response.getStatusCode();
        logger.info("Validating status code: expected={}, actual={}", expectedStatus, actual);
        if (actual != expectedStatus) {
            logger.error("Status code mismatch! Expected {}, got {}", expectedStatus, actual);
        }
    }

    public void verifyFirstName(String expectedFirstName) {
        String actual = response.jsonPath().getString("data.first_name");

        logger.info("Validating first name: expected={}, actual={}", expectedFirstName, actual);

        if (!expectedFirstName.equals(actual)) {
            logger.error("First name mismatch! Expected {}, got {}", expectedFirstName, actual);
        }
    }
}