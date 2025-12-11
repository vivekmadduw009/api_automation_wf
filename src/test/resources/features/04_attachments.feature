@attachments @regression
Feature: Attachment API validation

  Background:
    Given I am logged in as a valid user

  Scenario: Validation of file upload as attachment for ticket
    And I upload a file as attachment to a ticket using ticket_id from the fetched tickets
    Then the addAttachments API response status code should be 201
    And I fetch get attachment for the ticket using ticket_id
    Then the getAttachments API response status code should be 200
    And verify the uploaded attachment from get attachments response for the ticket
    When I delete the uploaded attachment using ticket_id
    Then the deleteAttachment API response status code should be 200
    And validate the delete Attachment response message "Attachment removed successfully"
    When I fetch get attachment for the ticket using ticket_id
    Then the getAttachments API response status code should be 404
    And verify that the get attachment response message "No attachment found for this ticket"