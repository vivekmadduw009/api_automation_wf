package steps;

import api.ApiManager;
import io.cucumber.java.en.*;
import io.restassured.response.Response;

import utils.Role;

import java.util.Map;

public class NotificationsSteps {

    private final ApiManager api = new ApiManager();
    private Map<String, String> lastTicketData;
    private Response getNotificationResponse;
    private Integer getId;
    private Response markRead;
    private Response markAllRead;
    private Map<String, String> updateTicketDtata;


    @And("Log in with Agent user")
    public void login() {
        api.tickets().login(Role.AGENT);
    }

    @When("Create a ticket and assignee as Agent for notification with details:")
    public void createTicketNotification(Map<String, String> data)
    {
        lastTicketData = data;
        api.tickets().createTicket(lastTicketData);
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
        markRead= api.notifications().markReadNotification(getId);
    }

    @Then("the mark-read notification API response status code should be {int}")
    public void validateStatusCodeMarkRead(int expectedStatusCode)
    {

        api.notifications().validateMarkReadStatusCode(markRead,expectedStatusCode);
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

    @And("Get and store read value of all notification")
    public void storeAllNotification()
    {
        Response response=api.notifications().getNotifications();
        api.notifications().beforeNotificationData(response);
    }

   @And("Mark all notification as read")
    public void getMarkAllNotification()
   {
       markAllRead=api.notifications().getMarkAllRead();
   }

   @Then("the mark-all-read notification API response status code should be {int}")
   public void validateStatusCodeMarkAllRead(int expectedStatusCode)
   {

       api.notifications().validateStatusCodeMarkReadAll(markAllRead,expectedStatusCode);
   }

   @And("verify mark all read api in response")
   public void validateMarkAllResponse()
   {
        api.notifications().validateMarkAllReadResponse(markAllRead);
   }
   @And("verify all notification read status after marking all read")
    public void validateDataAfterMarkAllReadNotification()
   {
       Response response=api.notifications().getNotifications();
       api.notifications().afterNotificationData(response);
   }

   @And("Get ticket details where requestor admin, assignee is agent and status is open")
    public void ticketForStatusUpdate()
   {
       Response response=api.tickets().getTickets();
        api.notifications().getDataOfTicket(response);

   }

   @And("Update the status to inprogress with following details:")
    public void updateStatus(Map<String, String> data)
   {
       updateTicketDtata=data;
        api.notifications().updateStatus(updateTicketDtata);
   }

   @And("Verify in get response that notification is created for status update")
    public void validateNotificationAfterStatusUpdate()
   {
       Response response=api.notifications().getNotifications();
       api.notifications().statusUpdateGetNotification(response);

   }

}

