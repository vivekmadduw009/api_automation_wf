package steps;

import api.ApiManager;
import io.cucumber.java.en.*;
import utils.Role;
import java.util.Map;

public class TicketsSteps {

    private final ApiManager api = new ApiManager();
    private Map<String, String> lastTicketData;

    @Given("I am logged in as a valid user")
    public void login() {
        api.tickets().login(Role.ADMIN);
    }

    @When("I fetch all tickets")
    public void getTicketsApi() {
        api.tickets().getTickets();
    }

    @And("verify the fetched ticket data is valid")
    public void validateFetchedTicketData() {
        api.tickets().validateTicketResponse();
    }

    @And("I fetch a ticket using ticket_id from the fetched tickets")
    public void getTicketFromFetchedList() {
        api.tickets().getTicket();
    }

    @Then("the create ticket API response status code should be {int}")
    public void validateCreateTicketStatusCode(int expectedStatusCode) {
        api.tickets().validateCreateTicketStatusCode(expectedStatusCode);
    }

    @Then("the get tickets API response status code should be {int}")
    public void validateGetTicketsStatusCode(int expectedStatusCode) {
        api.tickets().validateGetTicketsStatusCode(expectedStatusCode);
    }

    @Then("the delete ticket API response status code should be {int}")
    public void validateDeleteTicketStatusCode(int expectedStatusCode) {
        api.tickets().validateDeleteTicketStatusCode(expectedStatusCode);
    }

    @Then("the update ticket API response status code should be {int}")
    public void validateUpdateTicketStatusCode(int expectedStatusCode) {
        api.tickets().validateUpdateTicketStatusCode(expectedStatusCode);
    }

    @Then("the get ticket API response status code should be {int}")
    public void validateGetTicketStatusCode(int expectedStatusCode) {
        api.tickets().validateGetTicketStatusCode(expectedStatusCode);
    }

    @Then("the tickets response should be valid")
    public void validateTickets() {
        api.tickets().validateTicketsResponse();
    }

    @When("I create a ticket with details:")
    public void createTicket(Map<String, String> data) {
        lastTicketData = data;
        api.tickets().createTicket(data);
    }

    @Then("verify the created ticket data from get tickets response")
    public void validateCreatedTicket() {
        api.tickets().validateCreatedTicketFromGetTicketsResponse(lastTicketData);
    }

    @And("I delete a ticket using ticket_id from the fetched tickets")
    public void deleteTicketFromFetchedList() {
        api.tickets().deleteTicket();
    }

    @Then("verify the ticket is deleted successfully in the tickets list")
    public void verifyDeletion() {
        api.tickets().verifyTicketDeletionResponse();
    }

    @Then("the created ticket should have assign_to as null")
    public void verifyAssignToIsNull() {
        api.tickets().verifyAssignToIsNull();
    }

    @Then("the created ticket status should be {string}")
    public void verifyCreatedStatusIs(String expected) {
        api.tickets().verifyCreatedTicketStatus(expected);
    }

    @Then("the created ticket source should be {string}")
    public void verifyCreatedSourceIs(String expected) {
        api.tickets().verifyCreatedTicketSource(expected);
    }

    @Then("the API error message should be {string}")
    public void verifyErrorMessage(String expected) {
        api.tickets().verifyErrorMessage(expected);
    }

    @And("verify the tickets response has pagination implemented")
    public void verifyTicketsPagination() {
        api.tickets().validateTicketsPagination();
    }

    @And("I update the details of a ticket with:")
    public void updateTicketDetails(Map<String, String> data) {
        lastTicketData = data;
        api.tickets().updateTicketDetails(data);
    }

    @Then("verify the ticket details are updated successfully in the tickets list")
    public void verifyUpdatedTicketDetails() {
        api.tickets().verifyUpdatedTicketDetails(lastTicketData);
    }

}