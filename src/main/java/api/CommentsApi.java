package api;

import io.restassured.response.Response;
import org.slf4j.Logger;
import routes.TicketRoutes;
import utils.LoggerUtil;
import utils.RequestBuilder;

import java.util.List;
import java.util.Map;

import static io.restassured.RestAssured.given;

public class CommentsApi {

    private final ApiManager apiManager;

    public CommentsApi(ApiManager apiManager) {
        this.apiManager = apiManager;
    }

    private static final Logger logger = LoggerUtil.getLogger(CommentsApi.class);

    private Response addCommentsResponse;
    private Response getCommentedTicketResponse;
    private Response deleteCommentsResponse;
    private String ticket_id;

    public void validateAddCommentsStatusCode(int expected) {
        if (addCommentsResponse == null) {
            throw new IllegalStateException("No response to verify status code");
        }
        int actual = addCommentsResponse.getStatusCode();
        if (actual != expected) {
            throw new AssertionError("Status code mismatch. Expected: " + expected + " Actual: " + actual);
        }
        logger.info("Validated addComments status code: actual = {}, expected = {}", actual, expected);
    }

    public void validateGetCommentsStatusCode(int expected) {
        if (getCommentedTicketResponse == null) {
            throw new IllegalStateException("No response to verify status code");
        }
        int actual = getCommentedTicketResponse.getStatusCode();
        if (actual != expected) {
            throw new AssertionError("Status code mismatch. Expected: " + expected + " Actual: " + actual);
        }
        logger.info("Validated getComments status code: actual = {}, expected = {}", actual, expected);
    }

    public void validateDeleteCommentsStatusCode(int expected) {
        if (deleteCommentsResponse == null) {
            throw new IllegalStateException("No response to verify status code");
        }
        int actual = deleteCommentsResponse.getStatusCode();
        if (actual != expected) {
            throw new AssertionError("Status code mismatch. Expected: " + expected + " Actual: " + actual);
        }
        logger.info("Validated deleteComments status code: actual = {}, expected = {}", actual, expected);
    }

    public void addCommentToATicket(Map<String, String> commentsData) {
        Response resp = apiManager.tickets().getTickets();
        ticket_id = resp.jsonPath().getString("tickets[0].ticket_id");
        logger.info("Adding comment to ticket with ID: {}", ticket_id);
        addCommentsResponse = given()
                .spec(RequestBuilder.getAuthSpecCached())
                .body(commentsData)
                .when()
                .post(TicketRoutes.addComment(ticket_id));
        logger.debug("Add comments response: {}", addCommentsResponse.asPrettyString());
    }

    public void validateAddCommentResponse(Map<String, String> expectedData) {
        String addedContent = addCommentsResponse.jsonPath().getString("comment.content");
        String addedAuthor = addCommentsResponse.jsonPath().getString("comment.author");
        String responseMessage = addCommentsResponse.jsonPath().getString("message");

        if (!expectedData.get("content").equals(addedContent)) {
            throw new AssertionError("Comment content mismatch. Expected: " + expectedData.get("content") + " Actual: " + addedContent);
        }

        if (!expectedData.get("author").equals(addedAuthor)) {
            throw new AssertionError("Comment author mismatch. Expected: " + expectedData.get("author") + " Actual: " + addedAuthor);
        }

        if (!expectedData.get("message").equals(responseMessage)) {
            throw new AssertionError("Response message mismatch. Expected: " + expectedData.get("message") + " Actual: " + responseMessage);
        }

        logger.info("Validated add comment response successfully with content: {} and author: {}", addedContent, addedAuthor);
    }

    public void getCommentedTicket() {
        logger.info("Fetching ticket with ID: {}", ticket_id);
        getCommentedTicketResponse = given()
                .spec(RequestBuilder.getAuthSpecCached())
                .when()
                .get(TicketRoutes.getComments(ticket_id));
        logger.debug("Get ticket response: {}", getCommentedTicketResponse.asPrettyString());
    }

    public void validateAddedCommentInGetComments() {
        String addedComment = addCommentsResponse.jsonPath().getString("comment.content");
        int addedCommentId = addCommentsResponse.jsonPath().getInt("comment.id");
        List<Map<String, Object>> getComments = getCommentedTicketResponse.jsonPath().getList("comments");

        String getAddedComment = null;
        for (Map<String, Object> comment : getComments) {
            int commentId = (int) comment.get("id");
            if (commentId == addedCommentId) {
                getAddedComment = (String) comment.get("content");
                break;
            }
        }

        if (!addedComment.equals(getAddedComment)) {
            throw new AssertionError("Comment text mismatch. Added: " + addedComment + " Fetched: " + getAddedComment);
        }
        logger.info("Validated added comment is present in fetched comments: {}", addedComment);
    }

    public void deleteCommentFromTicket() {
        String comment_id = addCommentsResponse.jsonPath().getString("comment.id");
        logger.info("Deleting comment with ID: {} from ticket ID: {}", comment_id, ticket_id);
        deleteCommentsResponse = given()
                .spec(RequestBuilder.getAuthSpecCached())
                .when()
                .delete(TicketRoutes.deleteComment(ticket_id) + comment_id);
        logger.debug("Delete comment response: {}", deleteCommentsResponse.asPrettyString());
    }

    public void validateDeleteCommentResponse(String expectedMessage) {
        String actualMessage = deleteCommentsResponse.jsonPath().getString("message");
        if (!expectedMessage.equals(actualMessage)) {
            throw new AssertionError("Delete comment message mismatch. Expected: " + expectedMessage + " Actual: " + actualMessage);
        }
        logger.info("Validated delete comment response message: {}", actualMessage);
    }
}
