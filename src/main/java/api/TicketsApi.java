package api;

import io.restassured.response.Response;
import org.slf4j.Logger;
import routes.TicketRoutes;
import utils.*;

import java.util.List;
import java.util.Map;

import static io.restassured.RestAssured.given;

public class TicketsApi {

    @SuppressWarnings({"FieldCanBeLocal", "unused"})
    private final ApiManager apiManager;

    public TicketsApi(ApiManager apiManager) {
        this.apiManager = apiManager;
    }

    private static final Logger logger = LoggerUtil.getLogger(TicketsApi.class);

    private Response getTicketsResponse;
    private Response getTicketResponse;
    private Response createdTicketResponse;
    private Response deletedTicketResponse;
    private Response updateTickedResponse;
    private String deletedTicketId;
    private String updatedTicketId;
    private String createdTicketId;

    public void login(Role role) {
        AuthManager.getToken(role);
        logger.info("Logged in as role: {}", role);
    }

    public Response getTickets() {
        logger.info("Fetching tickets (fresh)");
        getTicketsResponse = given()
                .spec(RequestBuilder.getAuthSpecCached())
                .when()
                .get(TicketRoutes.getTickets());
        logger.debug("Get Tickets resp: {}", getTicketsResponse.asPrettyString());
        return getTicketsResponse;
    }

    public void getTicket() {
        Response resp = getTickets();
        List<Map<String, Object>> tickets = resp.jsonPath().getList("tickets");
        String ticket_id = String.valueOf(tickets.getFirst().get("ticket_id"));
        logger.info("Fetching ticket using {}", ticket_id);
        getTicketResponse = given()
                .spec(RequestBuilder.getAuthSpecCached())
                .when()
                .get(TicketRoutes.getTicketById(ticket_id));
        logger.debug("Get Ticket resp: {}", getTicketResponse.asPrettyString());
    }

    public void validateGetTicketsStatusCode(int expected) {
        if (getTicketsResponse == null) {
            throw new IllegalStateException("No response to verify status code");
        }
        int actual = getTicketsResponse.getStatusCode();
        if (actual != expected) {
            throw new AssertionError("Status code mismatch. Expected: " + expected + " Actual: " + actual);
        }
        logger.info("Validated get tickets status code: actual = {}, expected = {}", actual, expected);
    }

    public void validateGetTicketStatusCode(int expected) {
        if (getTicketResponse == null) {
            throw new IllegalStateException("No response to verify status code");
        }
        int actual = getTicketResponse.getStatusCode();
        if (actual != expected) {
            throw new AssertionError("Status code mismatch. Expected: " + expected + " Actual: " + actual);
        }
        logger.info("Validated get ticket status code: actual = {}, expected = {}", actual, expected);
    }

    public void validateCreateTicketStatusCode(int expected) {
        if (createdTicketResponse == null) {
            throw new IllegalStateException("No response to verify status code");
        }
        int actual = createdTicketResponse.getStatusCode();
        if (actual != expected) {
            throw new AssertionError("Status code mismatch. Expected: " + expected + " Actual: " + actual);
        }
        logger.info("Validated created ticket status code: actual = {}, expected = {}", actual, expected);
    }

    public void validateDeleteTicketStatusCode(int expected) {
        if (deletedTicketResponse == null) {
            throw new IllegalStateException("No response to verify status code");
        }
        int actual = deletedTicketResponse.getStatusCode();
        if (actual != expected) {
            throw new AssertionError("Status code mismatch. Expected: " + expected + " Actual: " + actual);
        }
        logger.info("Validated delete ticket status code: actual = {}, expected = {}", actual, expected);
    }

    public void validateUpdateTicketStatusCode(int expected) {
        if (updateTickedResponse == null) {
            throw new IllegalStateException("No response to verify status code");
        }
        int actual = updateTickedResponse.getStatusCode();
        if (actual != expected) {
            throw new AssertionError("Status code mismatch. Expected: " + expected + " Actual: " + actual);
        }
        logger.info("Validated update ticket status code: actual = {}, expected = {}", actual, expected);
    }

    public void validateTicketResponse() {
        Map<String, Object> ticket = getTicketResponse.jsonPath().getMap("ticket");
        if (ticket == null) {
            throw new AssertionError("'ticket' object missing in response: " + getTicketResponse.asPrettyString());
        }
        List<String> requiredFields = List.of(
                "id",
                "assign_to",
                "created_at",
                "description",
                "priority",
                "requestor",
                "source",
                "status",
                "ticket_id",
                "title",
                "updated_at"
        );
        for (String field : requiredFields) {
            if (!ticket.containsKey(field)) {
                throw new AssertionError(
                        "Missing field '" + field + "' in ticket: " + ticket
                );
            }
            if (field.equals("ticket_id") || field.equals("title")) {
                Object value = ticket.get(field);
                if (value == null || value.toString().isBlank()) {
                    throw new AssertionError(
                            "Field '" + field + "' is null/blank in ticket: " + ticket
                    );
                }
            }
        }
        logger.info("Fetched ticket {} validated successfully with all fields {}.", ticket.get("ticket_id"), requiredFields);
    }

    public void validateTicketsResponse() {
        List<Map<String, Object>> tickets = getTicketsResponse.jsonPath().getList("tickets");
        if (tickets == null || tickets.isEmpty()) {
            logger.error("No tickets returned. Body: {}", getTicketsResponse.asPrettyString());
            throw new AssertionError("Ticket list empty");
        }
        logger.info("Validating {} tickets...", tickets.size());
        List<String> requiredFields = List.of(
                "id",
                "assign_to",
                "created_at",
                "description",
                "priority",
                "requestor",
                "source",
                "status",
                "ticket_id",
                "title",
                "updated_at"
        );
        int index = 0;
        for (Map<String, Object> ticket : tickets) {
            logger.info("Validating ticket {} with ticket_id={}", index, ticket.get("ticket_id"));
            for (String field : requiredFields) {
                if (!ticket.containsKey(field)) {
                    throw new AssertionError(
                            "Missing field '" + field + "' in ticket: " + ticket
                    );
                }
                if (field.equals("ticket_id") || field.equals("title")) {
                    Object value = ticket.get(field);
                    if (value == null || value.toString().isBlank()) {
                        throw new AssertionError(
                                "Field '" + field + "' is null/blank in ticket: " + ticket
                        );
                    }
                }
            }
            logger.info("Ticket {} validated successfully with all fields {}.", ticket.get("ticket_id"), requiredFields);
            index++;
        }
        logger.info("All {} tickets validated successfully.", tickets.size());
    }

    public void createTicket(Map<String, String> ticketData) {
        logger.info("Creating ticket with: {}", ticketData);
        createdTicketResponse = given()
                .spec(RequestBuilder.getAuthSpecCached())
                .body(ticketData)
                .when()
                .post(TicketRoutes.createTicket());
        logger.debug("Create response: {}", createdTicketResponse.asPrettyString());
    }

    public void validateCreatedTicketFromGetTicketsResponse(Map<String, String> expectedInputData) {
        if (createdTicketResponse == null) {
            throw new IllegalStateException("No created ticket response to validate");
        }
        Map<String, Object> ticket = createdTicketResponse.jsonPath().getMap("ticket");
        if (ticket == null) {
            throw new AssertionError("'ticket' object missing in response: " + createdTicketResponse.asPrettyString());
        }
        if (!ticket.containsKey("ticket_id")) {
            throw new AssertionError("Created ticket missing ticket_id: " + ticket);
        }
        this.createdTicketId = String.valueOf(ticket.get("ticket_id"));

        if (createdTicketId == null) {
            throw new IllegalStateException("Created ticket_id is null");
        }
        Response resp = getTickets();
        List<Map<String, Object>> tickets = resp.jsonPath().getList("tickets");
        Map<String, Object> found = tickets.stream()
                .filter(t -> createdTicketId.equals(String.valueOf(t.get("ticket_id"))))
                .findFirst()
                .orElseThrow(() ->
                        new AssertionError("Created ticket_id " + createdTicketId + " NOT found in GET response"));

        logger.info("Found created ticket {} in GET response", createdTicketId);
        for (String key : expectedInputData.keySet()) {
            String expected = expectedInputData.get(key);
            String actual = String.valueOf(found.get(key));
            if (!expected.equals(actual)) {
                throw new AssertionError(
                        "GET mismatch for '" + key + "'. Expected: " + expected + " Actual: " + actual
                );
            }
        }
        logger.info("GET response matches created ticket {} successfully", createdTicketId);
    }

    public void deleteTicket() {
        Response resp = getTickets();
        List<Map<String, Object>> tickets = resp.jsonPath().getList("tickets");
        String ticket_id = String.valueOf(tickets.getFirst().get("ticket_id"));
        logger.info("Deleting ticket: {}", ticket_id);
        this.deletedTicketId = ticket_id;
        deletedTicketResponse = given()
                .spec(RequestBuilder.getAuthSpecCached())
                .when()
                .delete(TicketRoutes.deleteTicket(ticket_id));
        logger.debug("Delete response: {}", deletedTicketResponse.asPrettyString());
    }

    public void verifyTicketDeletionResponse() {
        Response resp = getTickets();
        List<Map<String, Object>> tickets = resp.jsonPath().getList("tickets");
        boolean exists = tickets.stream()
                .anyMatch(t -> deletedTicketId.equals(String.valueOf(t.get("ticket_id"))));
        if (exists) {
            throw new AssertionError("Ticket still present after delete: " + deletedTicketId);
        }
        logger.info("Deletion verified for ticket_id={} from the ticket list", deletedTicketId);
    }

    public void verifyAssignToIsNull() {
        Map<String, Object> ticket = createdTicketResponse.jsonPath().getMap("ticket");
        Object assignTo = ticket.get("assign_to");

        if (assignTo != null) {
            throw new AssertionError("Expected assign_to to be null but was: " + assignTo);
        }
        logger.info("assign_to correctly returned as null.");
    }

    public void verifyCreatedTicketStatus(String expected) {
        Map<String, Object> ticket = createdTicketResponse.jsonPath().getMap("ticket");
        String actual = String.valueOf(ticket.get("status"));
        if (!expected.equals(actual)) {
            throw new AssertionError("Status mismatch. Expected: " + expected + " Actual: " + actual);
        }
        logger.info("Ticket status validated: {}", actual);
    }

    public void verifyCreatedTicketSource(String expected) {
        Map<String, Object> ticket = createdTicketResponse.jsonPath().getMap("ticket");
        String actual = String.valueOf(ticket.get("source"));
        if (!expected.equals(actual)) {
            throw new AssertionError("Source mismatch. Expected: " + expected + " Actual: " + actual);
        }
        logger.info("Ticket source validated: {}", actual);
    }

    public void verifyErrorMessage(String expected) {
        List<String> errors = createdTicketResponse.jsonPath().getList("errors");
        if (errors == null || errors.isEmpty()) {
            throw new AssertionError("Expected errors array but got none. Response: " + createdTicketResponse.asPrettyString());
        }

        if (!errors.contains(expected)) {
            throw new AssertionError("Expected error '" + expected + "' but got: " + errors);
        }
        logger.info("Error message validated: {}", expected);
    }

    public void validateTicketsPagination() {
        logger.info("Validating tickets pagination...");
        Map<String, Object> pagination = getTicketsResponse.jsonPath().getJsonObject("meta");
        if (pagination == null) {
            throw new AssertionError("Pagination info missing in response: " + getTicketsResponse.asPrettyString());
        }

        List<String> requiredFields = List.of(
                "current_page",
                "next_page",
                "prev_page",
                "total_pages",
                "total_count"
        );
        for (String field : requiredFields) {
            if (!pagination.containsKey(field)) {
                throw new AssertionError("Missing pagination field '" + field + "' in meta: " + pagination);
            }
        }
        logger.info("Pagination validated successfully with fields: {}", requiredFields);
    }

    public void updateTicketDetails(Map<String, String> data) {
        Response resp = getTickets();
        List<Map<String, Object>> tickets = resp.jsonPath().getList("tickets");
        if (tickets == null || tickets.isEmpty()) {
            throw new AssertionError("No tickets to update details");
        }

        Map<String, Object> targetTicket = tickets.getFirst();
        this.updatedTicketId = String.valueOf(targetTicket.get("ticket_id"));

        updateTickedResponse = given()
                .spec(RequestBuilder.getAuthSpecCached())
                .body(data)
                .when()
                .put(TicketRoutes.updateTicket(updatedTicketId));
        logger.debug("Update details response: {}", updateTickedResponse.asPrettyString());
    }

    public void verifyUpdatedTicketDetails(Map<String, String> expectedData) {
        Response fresh = getTickets();
        List<Map<String, Object>> tickets = fresh.jsonPath().getList("tickets");
        Map<String, Object> ticket = tickets.stream()
                .filter(t -> updatedTicketId.equals(String.valueOf(t.get("ticket_id"))))
                .findFirst()
                .orElseThrow(() -> new AssertionError("Updated ticket not found: " + updatedTicketId));

        for (String key : expectedData.keySet()) {
            String expected = expectedData.get(key);
            String actual = String.valueOf(ticket.get(key));
            if (!expected.equals(actual)) {
                throw new AssertionError(
                        "Field '" + key + "' mismatch. Expected: " + expected + " Actual: " + actual
                );
            }
        }
        logger.info("Details update verified for {} with data {}", updatedTicketId, expectedData);
    }
}