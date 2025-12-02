package api;

import io.restassured.response.Response;
import org.slf4j.Logger;
import utils.LoggerUtil;
import utils.RequestBuilder;
import utils.AuthManager;
import utils.Role;

import java.util.List;
import java.util.Map;

import static io.restassured.RestAssured.given;

public class TicketsApi {

    private final ApiManager apiManager;

    public TicketsApi(ApiManager apiManager) {
        this.apiManager = apiManager;
    }

    private static final Logger logger = LoggerUtil.getLogger(TicketsApi.class);

    private Response getTicketsResponse;
    private Response getTicketResponse;
    private Response createdTicketResponse;
    private String deletedTicketId;
    private String updatedTicketId;
    private String updatedStatus;
    private String createdTicketId;
    private String assignedTicketId;
    private String newAssignee;
    private String oldAssignee;

    public void login(Role role) {
        AuthManager.getToken(role);
        logger.info("Logged in as role: {}", role);
    }

    public Response getTickets() {
        logger.info("Fetching tickets (fresh)");
        getTicketsResponse = given()
                .spec(RequestBuilder.getAuthSpecCached())
                .when()
                .get("/api/version1/tickets");
        logger.debug("Get Tickets resp: {}", getTicketsResponse.asPrettyString());
        return getTicketsResponse;
    }

    public void getTicket(String ticketId) {
        getTicketResponse = null;
        logger.info("Fetching ticket using {}", ticketId);
        getTicketResponse = given()
                .spec(RequestBuilder.getAuthSpecCached())
                .when()
                .get("/api/version1/tickets/" + ticketId);
        logger.debug("Get Ticket resp: {}", getTicketResponse.asPrettyString());
    }

    public void validateTicketsStatusCode(int expected) {
        Response responseToCheck = null;
        if (getTicketResponse != null) {
            responseToCheck = getTicketResponse;
        } else if (getTicketsResponse != null) {
            responseToCheck = getTicketsResponse;
        } else if (createdTicketResponse != null) {
            responseToCheck = createdTicketResponse;
        }
        if (responseToCheck == null) {
            throw new IllegalStateException("No response to verify status code");
        }
        int actual = responseToCheck.getStatusCode();
        if (actual != expected) {
            throw new AssertionError("Status code mismatch. Expected: " + expected + " Actual: " + actual);
        }
        logger.info("Validated status code: actual = {}, expected = {}", actual, expected);
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
                .post("/api/version1/tickets");
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

    public String extractAnyTicketIdFromLastGet() {
        List<Map<String, Object>> tickets = getTicketsResponse.jsonPath().getList("tickets");
        if (tickets == null || tickets.isEmpty()) {
            throw new AssertionError("No tickets to extract");
        }
        Map<String, Object> first = tickets.getFirst();
        String id = String.valueOf(first.get("ticket_id"));
        logger.info("Extracted ticket_id: {}", id);
        return id;
    }

    public void deleteTicket(String ticketId) {
        logger.info("Deleting ticket: {}", ticketId);
        this.deletedTicketId = ticketId;
        Response deletedTicketResponse = given()
                .spec(RequestBuilder.getAuthSpecCached())
                .when()
                .delete("/api/version1/tickets/" + ticketId);
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

    public void updateTicketStatus(String status) {
        Response resp = getTickets();
        List<Map<String, Object>> tickets = resp.jsonPath().getList("tickets");
        if (tickets == null || tickets.isEmpty()) {
            throw new AssertionError("No tickets to update");
        }
        this.updatedTicketId = String.valueOf(tickets.getFirst().get("ticket_id"));
        this.updatedStatus = status;
        Response updatedStatusResponse = given()
                .spec(RequestBuilder.getAuthSpecCached())
                .body(Map.of("status", status))
                .when()
                .patch("/api/version1/tickets/" + updatedTicketId + "/status");
        logger.debug("Update response: {}", updatedStatusResponse.asPrettyString());
    }

    public void verifyUpdatedTicketStatus() {
        Response fresh = getTickets();
        List<Map<String, Object>> tickets = fresh.jsonPath().getList("tickets");
        Map<String, Object> ticket = tickets.stream()
                .filter(t -> updatedTicketId.equals(String.valueOf(t.get("ticket_id"))))
                .findFirst()
                .orElseThrow(() -> new AssertionError("Updated ticket not found: " + updatedTicketId));
        String actualStatus = String.valueOf(ticket.get("status"));
        if (!updatedStatus.equals(actualStatus)) {
            throw new AssertionError("Status mismatch. Expected: " + updatedStatus + " Actual: " + actualStatus);
        }
        logger.info("Status update verified for {} -> {}", updatedTicketId, actualStatus);
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

    public void updateTicketAssignee(String assign) {
        Response resp = getTickets();
        List<Map<String, Object>> tickets = resp.jsonPath().getList("tickets");
        if (tickets == null || tickets.isEmpty()) {
            throw new AssertionError("No tickets to update assignee");
        }

        Map<String, Object> firstTicket = tickets.getFirst();
        this.assignedTicketId = String.valueOf(firstTicket.get("ticket_id"));
        this.oldAssignee = String.valueOf(firstTicket.get("assign_to"));
        this.newAssignee = assign;
        Response updatedAssigneeResponse = given()
                .spec(RequestBuilder.getAuthSpecCached())
                .body(Map.of("assign_to", newAssignee))
                .when()
                .patch("/api/version1/tickets/" + assignedTicketId + "/assign");
        logger.debug("Update assignee response: {}", updatedAssigneeResponse.asPrettyString());
    }

    public void verifyUpdatedTicketAssignee() {
        Response fresh = getTickets();
        List<Map<String, Object>> tickets = fresh.jsonPath().getList("tickets");
        Map<String, Object> ticket = tickets.stream()
                .filter(t -> assignedTicketId.equals(String.valueOf(t.get("ticket_id"))))
                .findFirst()
                .orElseThrow(() -> new AssertionError("Assigned ticket not found: " + assignedTicketId));
        String actualAssignee = String.valueOf(ticket.get("assign_to"));
        if (!newAssignee.equals(actualAssignee)) {
            throw new AssertionError("Assignee mismatch. Expected: " + newAssignee + " Actual: " + actualAssignee);
        }
        logger.info("Updated Assignee for the ticketId: {}, newAssignee = {}, oldAssignee = {}", assignedTicketId, actualAssignee, oldAssignee);
    }
}