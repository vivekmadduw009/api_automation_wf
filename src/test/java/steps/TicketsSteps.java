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
        String ticketId = api.tickets().extractAnyTicketIdFromLastGet();
        api.tickets().getTicket(ticketId);
    }

    @Then("the tickets API response status code should be {int}")
    public void validateTicketsStatusCode(int expectedStatusCode) {
        api.tickets().validateTicketsStatusCode(expectedStatusCode);
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
        String ticketId = api.tickets().extractAnyTicketIdFromLastGet();
        api.tickets().deleteTicket(ticketId);
    }

    @Then("verify the ticket is deleted successfully in the tickets list")
    public void verifyDeletion() {
        api.tickets().verifyTicketDeletionResponse();
    }

    @When("I update the status of a ticket to {string}")
    public void updateTicketStatus(String newStatus) {
        api.tickets().updateTicketStatus(newStatus);
    }

    @Then("verify the ticket status is updated successfully in the tickets list")
    public void verifyUpdatedStatus() {
        api.tickets().verifyUpdatedTicketStatus();
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

    @And("I update the assignee of a ticket to {string}")
    public void updateTicketAssignee(String assign) {
        api.tickets().updateTicketAssignee(assign);
    }

    @Then("verify the ticket assignee is updated successfully in the tickets list")
    public void verifyUpdatedAssignee() {
        api.tickets().verifyUpdatedTicketAssignee();
    }

}