package steps;

import api.ApiManager;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Then;

public class AttachmentsSteps {

    private final ApiManager api = new ApiManager();

    @And("I upload a file as attachment to a ticket using ticket_id from the fetched tickets")
    public void uploadAttachmentToTicket() {
        api.attachments().uploadAttachmentToTicket();
    }

    @Then("the addAttachments API response status code should be {int}")
    public void validateAddAttachmentsStatusCode(int expectedStatusCode) {
        api.attachments().validateAddAttachmentsStatusCode(expectedStatusCode);
    }

    @Then("the getAttachments API response status code should be {int}")
    public void validateGetAttachmentsStatusCode(int expectedStatusCode) {
        api.attachments().validateGetAttachmentsStatusCode(expectedStatusCode);
    }

    @Then("the deleteAttachment API response status code should be {int}")
    public void validateDeleteAttachmentStatusCode(int expectedStatusCode) {
        api.attachments().validateDeleteAttachmentStatusCode(expectedStatusCode);
    }

    @And("verify the uploaded attachment from get attachments response for the ticket")
    public void validateUploadedAttachment() {
        api.attachments().validateUploadedAttachment();
    }

    @Then("I fetch get attachment for the ticket using ticket_id")
    public void getAttachmentForTicket() {
        api.attachments().getAttachmentsForTicket();
    }

    @Then("I delete the uploaded attachment using ticket_id")
    public void deleteAttachmentForTicket() {
        api.attachments().deleteAttachmentForTicket();
    }

    @And("validate the delete Attachment response message {string}")
    public void validateDeleteAttachmentResponseMessage(String expectedMessage) {
        api.attachments().validateDeleteAttachmentResponseMessage(expectedMessage);
    }

    @And("verify that the get attachment response message {string}")
    public void validateGetAttachmentResponseMessage(String expectedMessage) {
        api.attachments().validateGetAttachmentResponseMessage(expectedMessage);
    }
}
