package steps;

import api.ApiManager;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import java.util.Map;

public class CommentsSteps {

    private final ApiManager api = new ApiManager();

    @Then("the addComment API response status code should be {int}")
    public void theAddCommentApiResponseStatusCode(int statusCode) {
        api.comments().validateAddCommentsStatusCode(statusCode);
    }

    @Then("the getComment API response status code should be {int}")
    public void theGetCommentApiResponseStatusCode(int statusCode) {
        api.comments().validateGetCommentsStatusCode(statusCode);
    }

    @Then("the deleteComment API response status code should be {int}")
    public void theDeleteCommentApiResponseStatusCode(int statusCode) {
        api.comments().validateDeleteCommentsStatusCode(statusCode);
    }
    @And("I add a comment to a ticket using ticket_id from the fetched tickets with comment details:")
    public void addCommentToTicket(Map<String, String> data) {
        api.comments().addCommentToATicket(data);
    }

    @And("verify add comment response is valid:")
    public void verifyAddCommentResponseIsValid(Map<String, String> expectedData) {
        api.comments().validateAddCommentResponse(expectedData);
    }

    @Then("I fetch the commented ticket by ticket_id")
    public void iFetchTheCommentedTicketByTicket_id() {
        api.comments().getCommentedTicket();
    }

    @And("verify the added comment is present in the ticket comments")
    public void verifyTheAddedCommentIsPresentInTheTicketComments() {
        api.comments().validateAddedCommentInGetComments();
    }

    @When("I delete the comment using comment_id from the added comment")
    public void iDeleteTheCommentUsingCommentIdFromTheAddedComment() {
        api.comments().deleteCommentFromTicket();
    }

    @And("verify the response should be {string} for delete comment")
    public void verifyTheResponseShouldBeForDeleteComment(String message) {
        api.comments().validateDeleteCommentResponse(message);
    }
}
