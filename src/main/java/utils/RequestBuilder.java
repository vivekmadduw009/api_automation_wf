package utils;

import io.restassured.builder.RequestSpecBuilder;
import io.restassured.specification.RequestSpecification;

public class RequestBuilder {

    private static final String BASE_URL = ConfigReader.get("baseUrl");

    private static final RequestSpecification baseSpec = new RequestSpecBuilder()
            .setBaseUri(BASE_URL)
            .addHeader("Content-Type", "application/json")
            .log(io.restassured.filter.log.LogDetail.ALL)
            .build();

    public static RequestSpecification getRequestSpec() {
        return baseSpec;
    }

    public static RequestSpecification getAuthSpec(String token) {
        return new RequestSpecBuilder()
                .addRequestSpecification(baseSpec)
                .addHeader("Authorization", "Bearer " + token)
                .build();
    }

    public static RequestSpecification getAuthSpecCached() {
        String token = AuthManager.setToken();
        return getAuthSpec(token);
    }
}