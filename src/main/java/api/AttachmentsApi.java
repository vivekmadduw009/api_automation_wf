package api;

import io.restassured.response.Response;
import org.slf4j.Logger;
import routes.TicketRoutes;
import utils.ConfigReader;
import utils.LoggerUtil;
import utils.RequestBuilder;

import java.io.File;

import static io.restassured.RestAssured.given;

public class AttachmentsApi {

    private final ApiManager apiManager;

    public AttachmentsApi(ApiManager apiManager) {
        this.apiManager = apiManager;
    }

    private static final Logger logger = LoggerUtil.getLogger(AttachmentsApi.class);

    private Response addAttachmentsResponse;
    private Response getAttachmentsResponse;
    private Response deleteAttachmentResponse;
    private String ticket_id;

    public void validateAddAttachmentsStatusCode(int expected) {
        if (addAttachmentsResponse == null) {
            throw new IllegalStateException("No add attachments response to verify status code");
        }
        int actual = addAttachmentsResponse.getStatusCode();
        if (actual != expected) {
            throw new AssertionError("Status code mismatch. Expected: " + expected + " Actual: " + actual);
        }
        logger.info("Validated add attachments status code: actual = {}, expected = {}", actual, expected);
    }

    public void validateGetAttachmentsStatusCode(int expected) {
        if (getAttachmentsResponse == null) {
            throw new IllegalStateException("No get attachments response to verify status code");
        }
        int actual = getAttachmentsResponse.getStatusCode();
        if (actual != expected) {
            throw new AssertionError("Status code mismatch. Expected: " + expected + " Actual: " + actual);
        }
        logger.info("Validated get Attachments status code: actual = {}, expected = {}", actual, expected);
    }

    public void validateDeleteAttachmentStatusCode(int expected) {
        if (deleteAttachmentResponse == null) {
            throw new IllegalStateException("No delete attachments response to verify status code");
        }
        int actual = deleteAttachmentResponse.getStatusCode();
        if (actual != expected) {
            throw new AssertionError("Status code mismatch. Expected: " + expected + " Actual: " + actual);
        }
        logger.info("Validated delete Attachments status code: actual = {}, expected = {}", actual, expected);
    }

    public void uploadAttachmentToTicket() {
        Response ticketsResponse = apiManager.tickets().getTickets();
        ticket_id = ticketsResponse.jsonPath().getString("tickets[0].ticket_id");
        logger.info("Uploading attachment to ticket with ID: {}", ticket_id);

        String filePath = ConfigReader.get("attachment_file_path");
        File file = new File(filePath);

        if (!file.exists() || !file.isFile()) {
            throw new IllegalStateException("Attachment file not found at path: " + filePath);
        }

        addAttachmentsResponse = given()
                .spec(RequestBuilder.getAuthSpecCached())
                .contentType("multipart/form-data")
                .multiPart("attachment", file)
                .when()
                .post(TicketRoutes.addAttachment(ticket_id));
        logger.debug("Get add attachments resp: {}", addAttachmentsResponse.asPrettyString());
    }

    public void getAttachmentsForTicket() {
        logger.info("Fetching attachments for ticket ID: {}", ticket_id);
        getAttachmentsResponse = given()
                .spec(RequestBuilder.getAuthSpecCached())
                .when()
                .get(TicketRoutes.getAttachment(ticket_id));
        logger.info("Get attachments resp: {}", getAttachmentsResponse.asPrettyString());
    }

    public void validateUploadedAttachment() {

        String fileName = getAttachmentsResponse.jsonPath().getString("filename");
        String contentType = getAttachmentsResponse.jsonPath().getString("content_type");
        int byteSize = getAttachmentsResponse.jsonPath().getInt("byte_size");
        String url = getAttachmentsResponse.jsonPath().getString("url");
        String createdAt = getAttachmentsResponse.jsonPath().getString("created_at");

        if (fileName == null || fileName.isEmpty()) {
            throw new AssertionError("filename is missing in attachment response");
        }

        if (contentType == null || contentType.isEmpty()) {
            throw new AssertionError("content_type is missing for attachment");
        }

        if (!contentType.equals("application/pdf")) {
            throw new AssertionError("Attachment must be a PDF. Found: " + contentType);
        }

        if (byteSize <= 0) {
            throw new AssertionError("Invalid byte_size. Received: " + byteSize);
        }

        if (url == null || url.isEmpty()) {
            throw new AssertionError("url is missing in attachment response");
        }

        if (!url.startsWith("http")) {
            throw new AssertionError("Invalid attachment URL format: " + url);
        }

        if (createdAt == null || createdAt.isEmpty()) {
            throw new AssertionError("created_at is missing in attachment response");
        }

        logger.info("Attachment validated successfully:");
        logger.info("Filename: {}", fileName);
        logger.info("Content-Type: {}", contentType);
        logger.info("Size (bytes): {}", byteSize);
        logger.info("URL: {}", url);
        logger.info("Created At: {}", createdAt);
    }

    public void deleteAttachmentForTicket() {
        logger.info("Deleting attachment for ticket ID: {}", ticket_id);
        deleteAttachmentResponse = given()
                .spec(RequestBuilder.getAuthSpecCached())
                .when()
                .delete(TicketRoutes.deleteAttachment(ticket_id));
        logger.debug("Delete attachment resp: {}", deleteAttachmentResponse.asPrettyString());
    }

    public void validateDeleteAttachmentResponseMessage(String expectedMessage) {
        if (deleteAttachmentResponse == null) {
            throw new IllegalStateException("No delete attachment response to verify message");
        }
        String actualMessage = deleteAttachmentResponse.jsonPath().getString("message");
        if (!expectedMessage.equals(actualMessage)) {
            throw new AssertionError("Delete attachment message mismatch. Expected: " + expectedMessage + " Actual: " + actualMessage);
        }
        logger.info("Validated delete attachment message: actual = {}, expected = {}", actualMessage, expectedMessage);
    }

    public void validateGetAttachmentResponseMessage(String expectedMessage) {
        if (getAttachmentsResponse == null) {
            throw new IllegalStateException("No get attachment response to verify message");
        }
        String actualMessage = getAttachmentsResponse.jsonPath().getString("error");
        if (!expectedMessage.equals(actualMessage)) {
            throw new AssertionError("Get attachment message mismatch. Expected: " + expectedMessage + " Actual: " + actualMessage);
        }
        logger.info("Validated get attachment message: actual = {}, expected = {}", actualMessage, expectedMessage);
    }
}
