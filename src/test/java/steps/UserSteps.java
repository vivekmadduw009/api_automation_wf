package steps;

import api.UserApi;
import io.cucumber.java.en.*;

public class UserSteps {

    private final UserApi api = new UserApi();

    @Given("I hit the Get User API with id {int}")
    public void hitApi(int id) {
        api.getUserById(id);
    }

    @Then("the response status code should be {int}")
    public void verifyStatus(int code) {
        api.verifyStatusCode(code);
    }

    @Then("the user first name should be {string}")
    public void verifyName(String expected) {
        api.verifyFirstName(expected);
    }
}