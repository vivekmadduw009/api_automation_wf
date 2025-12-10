@comments @regression
Feature: Comments API Validation

  Background:
    Given I am logged in as a valid user

  Scenario: Add, verify, and delete a comment for a ticket
    When I add a comment to a ticket using ticket_id from the fetched tickets with comment details:
      | content | This is a test comment |
      | author  | admin                  |
    Then the addComment API response status code should be 201
    And verify add comment response is valid:
      | content | This is a test comment     |
      | author  | admin                      |
      | message | Comment added successfully |

    When I fetch the commented ticket by ticket_id
    Then the getComment API response status code should be 200
    And verify the added comment is present in the ticket comments

    When I delete the comment using comment_id from the added comment
    Then the deleteComment API response status code should be 200
    And verify the response should be "Comment deleted successfully" for delete comment