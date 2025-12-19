package steps;

import api.ApiManager;
import io.cucumber.java.en.*;
import io.restassured.response.Response;

import utils.Role;

import java.util.Map;

public class NotificationsSteps {

    private final ApiManager api = new ApiManager();
    private Map<String, String> lastTicketData;
    public NotificationsSteps() {}
    private Response getNotificationResponse;
    private Integer getId;


    @And("Log in with Agent user")
    public void login() {
        api.tickets().login(Role.AGENT);
    }

    @When("Create a ticket and assignee as Agent for notification with details:")
    public void createTicketNotification(Map<String, String> data)
    {
        lastTicketData = data;
        api.tickets().createTicket(data);
    }

    @And("the create ticket API response in notifications status code should be {int}")
    public void validateCreateTicketStatusCode(int expectedStatusCode) {
        api.tickets().validateCreateTicketStatusCode(expectedStatusCode);
    }

    @And("Fetch all notification")
    public void getNotificationApi() {
        api.notifications().getNotifications();
    }

    @Then("the get notification API response status code should be {int}")
    public void validateNotificationStatus(int expectedStatusCode)
    {
        api.notifications().validateNotificationStatusCode(expectedStatusCode);
    }

    @And("verify the created ticket has created notification in response")
    public void validateGetNotificationResponse()
    {
        api.notifications().validateNewNotificationResponseInGetNotification(api.tickets().getCreatedTicketResponse());
    }

    @And("Fetch first notification whose read=false")
    public void fetchFirstUnreadNotification()
    {
        getNotificationResponse=api.notifications().getFirstUnreadNotification(api.notifications().getNotifications());
        getId=getNotificationResponse.jsonPath().getInt("id");
    }

    @And("Mark that notification as read")
    public void markReadNotification()
    {
    api.notifications().markReadNotification(getId);
    }

    @Then("the mark-read notification API response status code should be {int}")
    public void validateStatusCodeMarkRead(int expectedStatusCode)
    {
        api.notifications().validateMarkReadStatusCode(expectedStatusCode);
    }

    @And("verify mark read api in response")
    public void validateMarkReadResponse()
    {
        api.notifications().validateMarkReadResponse();
    }

    @And("verify read status in get Notification API")
    public void validateGetNotificationAfterMarkRead()
    {
        Response resp =api.notifications().getNotifications();
        api.notifications().validateNotificationResponseAfterMarkRead(resp,getId);
    }
}

