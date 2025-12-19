package api;

import io.restassured.builder.ResponseBuilder;
import io.restassured.http.ContentType;
import org.slf4j.Logger;
import routes.TicketRoutes;
import io.restassured.response.Response;
import utils.LoggerUtil;
import utils.RequestBuilder;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static io.restassured.RestAssured.given;

public class NotificationApi {


    private final ApiManager apiManager;
    private Response getNotificationResponse;
    private String createdTicketId;
    private Integer createdId;
    private Map<String, Object> firstUnReadNotification;
    private Response markReadResponse;
    private Integer idFound;
    private Map<Integer, Boolean> notificationsBefore;
    private Response markAllReadResponse;

    public NotificationApi(ApiManager apiManager) {
        this.apiManager = apiManager;
    }

    private static final Logger logger = LoggerUtil.getLogger(NotificationApi.class);


    public Response getNotifications() {
        logger.info("Api {}", TicketRoutes.getNotifications());
        logger.info("Fetching Notification (fresh)");
        getNotificationResponse = given()
                .spec(RequestBuilder.getAuthSpecCached())
                .when()
                .get(TicketRoutes.getNotifications());
        logger.debug("Get Notification resp: {}", getNotificationResponse.asPrettyString());
        logger.info("Response for GET Notification: " + getNotificationResponse.asPrettyString());
        return getNotificationResponse;
    }

    public Response getFirstUnreadNotification(Response resp) {
        List<Map<String, Object>> notifications = resp.jsonPath().getList("");
        firstUnReadNotification = null;
        for (Map<String, Object> notification : notifications) {

            boolean readStatus = (Boolean) notification.get("read");
            if (!readStatus) {
                firstUnReadNotification = notification;
                break; // ✅ stop loop
            }

        }

        if (firstUnReadNotification == null) {
            throw new IllegalStateException("No notification found with read=false");
        }


        // ✅ convert Map → JSON String
        ResponseBuilder builder = new ResponseBuilder();
        builder.setStatusCode(200);
        builder.setContentType(ContentType.JSON);
        try {
            String jsonBody = new ObjectMapper()
                    .writeValueAsString(firstUnReadNotification);
            builder.setBody(jsonBody);
        } catch (Exception e) {
            throw new RuntimeException("Failed to convert Map to JSON", e);
        }
        logger.info("Response Created for First Un read Notification: " + builder.build().asPrettyString());
        return builder.build();


    }


    public void validateNotificationStatusCode(int expected) {
        if (getNotificationResponse == null) {
            throw new IllegalStateException("No response to verify status code");
        }
        int actual = getNotificationResponse.getStatusCode();
        if (actual != expected) {
            throw new IllegalStateException("Status code mismatch. Expected: " + expected + " Actual: " + actual);
        }
        logger.info("Validated get tickets status code: actual = {}, expected = {}", actual, expected);
    }


    public void validateNewNotificationResponseInGetNotification(Response createdTicketResponse) {
        createdId = createdTicketResponse.jsonPath().getInt("ticket.id");
        createdTicketId = createdTicketResponse.jsonPath().get("ticket.ticket_id");

        List<Map<String, Object>> notifications =
                getNotificationResponse.jsonPath().getList("");

        if (notifications == null || notifications.isEmpty()) {
            logger.error("No notifications returned. Body: {}", getNotificationResponse.asPrettyString());
            throw new AssertionError("Notification list empty");
        }
        List<String> requiredFields = List.of(

                "id",
                "message",
                "read",
                "created_at",
                "notifiable_id",
                "notifiable_type"

        );
        for (Map<String, Object> notification : notifications) {
            for (String field : requiredFields) {
                if (!notification.containsKey(field)) {
                    throw new AssertionError(
                            "Missing field '" + field + "' in notification: " + notification
                    );
                }
            }
        }

        boolean createdTicketNotification = false;
        for (Map<String, Object> notification : notifications) {

            Integer id = (Integer) notification.get("notifiable_id");

            if (id != null && createdId.equals(id)) {

                String message = (String) notification.get("message");
                String ticketId = message.replaceAll(".*Ticket #", "");
                createdTicketNotification = true;
                logger.info("Notification is generated");
                Boolean read = (Boolean) notification.get("read");
                if (!Boolean.FALSE.equals(read)) {
                    throw new IllegalStateException(
                            "Notification is marked as read for notification id " + createdId);
                }
                logger.info("Notification is unread as expected");

                if (ticketId.equals(createdTicketId)) {
                    logger.info("Showing correct ticket_id {}", createdTicketId + ticketId);
                } else {
                    throw new IllegalStateException(
                            "Notification message does not contain correct ticket id {}");

                }
                String notifiableType =
                        (String) notification.get("notifiable_type");
                if (!"Ticket".equals(notifiableType)) {
                    throw new IllegalStateException(
                            "Invalid notifiable_type: " + notifiableType);
                }
                if (!createdTicketId.equals(ticketId)) {
                    throw new IllegalStateException(
                            "Incorrect ticket id in notification message. Expected="
                                    + createdTicketId + ", Actual=" + ticketId);
                }

                logger.info("Notification validated successfully for ticket id {}", createdTicketId);

                return;
            }

        }

        throw new IllegalStateException(
                "Created Ticket notification (notification id=" + createdId + ") not found in response");

    }

    public Response markReadNotification(Integer id) {
        Map<String, Object> body = new HashMap<>();
        body.put("id", id);
        markReadResponse = given().spec(RequestBuilder.getAuthSpecCached()).body(body).when().patch(TicketRoutes.markReadNotification());
        logger.debug("Mark Read Notification response: {}", markReadResponse.asPrettyString());
        return markReadResponse;
    }

    public void validateMarkReadStatusCode(Response resp,int expected) {
        markReadResponse=resp;
        if (markReadResponse == null) {
            throw new IllegalStateException("No response to verify status code");
        }
        int actual = markReadResponse.getStatusCode();
        if (actual != expected) {
            throw new IllegalStateException("Status code mismatch. Expected: " + expected + " Actual: " + actual);
        }
        logger.info("Validated get mark read status code: actual = {}, expected = {}", actual, expected);


    }

    public void validateMarkReadResponse() {

        if (markReadResponse == null) {
            throw new IllegalStateException("Response is empty");
        }
        boolean success = markReadResponse.jsonPath().getBoolean("success");

        if (success) {
            logger.info("Mark read response is validated");
        } else if (success == false) {
            throw new AssertionError("Expected success=true but found false");
        } else {
            throw new AssertionError("Expected success=true but found success=" + success);
        }
    }

    public void validateNotificationResponseAfterMarkRead(Response resp, Integer id) {
        if (resp == null) {
            throw new IllegalStateException("Response is getting null");
        }
        List<Map<String, Object>> notifications = resp.jsonPath().getList("");

        idFound = id;
        boolean ifIdFound = false;
        for (Map<String, Object> notification : notifications) {

            Integer notificationId = (Integer) notification.get("id");
            if (notificationId.equals(idFound)) {
                ifIdFound = true;
                logger.info("Found Notification");
                Boolean read = (Boolean) notification.get("read");
                if (read == true) {
                    logger.info("Notification is read as expected for " + notificationId);
                } else {
                    throw new IllegalStateException(
                            "Notification is wrong for notification id " + idFound + " it is showing as read" + read);
                }
                logger.info("Notification is read as expected");

                String notifiableType =
                        (String) notification.get("notifiable_type");
                if (!"Ticket".equals(notifiableType)) {
                    throw new IllegalStateException(
                            "Invalid notifiable_type: " + notifiableType);
                }


                logger.info("Notification validated successfully for id {}", notificationId);

                return;
            }
        }
    }

    public void beforeNotificationData(Response response) {

        List<Map<String, Object>> notifications =
                response.jsonPath().getList("");
        notificationsBefore = new HashMap<>();
        for (Map<String, Object> notification : notifications) {
            Integer id = (Integer) notification.get("id");
            Boolean read = (Boolean) notification.get("read");
            notificationsBefore.put(id, read);
        }

    }

    public Response getMarkAllRead() {

        markAllReadResponse = given().spec(RequestBuilder.getAuthSpecCached()).when().patch(TicketRoutes.markAllReadNotification());
        logger.debug("Mark All Read Notification response: {}", markAllReadResponse.asPrettyString());
        return markAllReadResponse;

    }

    public void validateStatusCodeMarkReadAll(Response resp,int expected)
    {
        markAllReadResponse=resp;
        if (markAllReadResponse == null) {
            throw new IllegalStateException("No response to verify status code");
        }
        int actual = markAllReadResponse.getStatusCode();
        if (actual != expected) {
            throw new IllegalStateException("Status code mismatch. Expected: " + expected + " Actual: " + actual);
        }
        logger.info("Validated get mark all read status code: actual = {}, expected = {}", actual, expected);

    }

    public void validateMarkAllReadResponse(Response resp) {
        markAllReadResponse=resp;
        if (markAllReadResponse == null) {
            throw new IllegalStateException("Response is empty");
        }
        Boolean success = (Boolean) markAllReadResponse.jsonPath().get("success");

        if (success) {
            logger.info("Mark All read response is validated");
        } else if (success == false) {
            throw new AssertionError("Expected success=true but found false");
        } else {
            throw new AssertionError("Expected success=true but found success=" + success);
        }
    }

    public void afterNotificationData(Response response) {
        List<Map<String, Object>> notificationsAfter =
                response.jsonPath().getList("");

        for (Map<String, Object> notification : notificationsAfter) {
            Integer id = (Integer) notification.get("id");
            Boolean readAfter = (Boolean) notification.get("read");
            Boolean readBefore = notificationsBefore.get(id);

            if (readBefore == null) {
                throw new AssertionError("Unexpected notification id: " + id);
            }
            if (!Boolean.TRUE.equals(readAfter)) {
                throw new AssertionError(
                        "Notification with id " + id + " is not read after mark all operation");
            }

        }
    }
}

    
