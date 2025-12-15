@tickets @regression
Feature: Tickets API Validation

  Background:
    Given I am logged in as a valid user

  Scenario: Create a new ticket with assignee
    When I create a ticket with details:
      | title       | Testing tickets |
      | description | System issue2   |
      | priority    | low             |
      | source      | email           |
      | status      | open            |
      | requestor   | agent@gmail.com |
      | assign_to   | agent@gmail.com |
    Then the create ticket API response status code should be 201
    And verify the created ticket data from get tickets response

  Scenario: Create a new ticket with assignee and priority medium
    When I create a ticket with details:
      | title       | Testing tickets |
      | description | System issue2   |
      | priority    | medium          |
      | source      | email           |
      | status      | open            |
      | requestor   | agent@gmail.com |
      | assign_to   | agent@gmail.com |
    Then the create ticket API response status code should be 201
    And verify the created ticket data from get tickets response

  Scenario: Create a new ticket with assignee and priority high
    When I create a ticket with details:
      | title       | Testing tickets |
      | description | System issue2   |
      | priority    | high            |
      | source      | email           |
      | status      | open            |
      | requestor   | agent@gmail.com |
      | assign_to   | agent@gmail.com |
    Then the create ticket API response status code should be 201
    And verify the created ticket data from get tickets response

  Scenario: Fetch all tickets
    When I fetch all tickets
    Then the get tickets API response status code should be 200
    And the tickets response should be valid

  Scenario: Fetch a ticket by ID
    When I fetch all tickets
    And I fetch a ticket using ticket_id from the fetched tickets
    Then the get ticket API response status code should be 200
    And verify the fetched ticket data is valid

  Scenario: Deleting a ticket
    When I fetch all tickets
    And I delete a ticket using ticket_id from the fetched tickets
    Then the delete ticket API response status code should be 200
    And I fetch all tickets
    Then verify the ticket is deleted successfully in the tickets list

  Scenario: Create ticket without assignee and expect assign_to is null
    When I create a ticket with details:
      | title       | Testing tickets |
      | description | System issue2   |
      | priority    | low             |
      | source      | email           |
      | status      | open            |
      | requestor   | agent@gmail.com |
    Then the create ticket API response status code should be 201
    And the created ticket should have assign_to as null

  Scenario: Create ticket without status and expect default "open" status
    When I create a ticket with details:
      | title       | Testing tickets |
      | description | System issue2   |
      | priority    | low             |
      | source      | email           |
      | requestor   | agent@gmail.com |
      | assign_to   | admin@gmail.com |
    Then the create ticket API response status code should be 201
    And the created ticket status should be "open"

  Scenario: Create ticket without source and expect default "email"
    When I create a ticket with details:
      | title       | Testing tickets |
      | description | System issue2   |
      | priority    | low             |
      | requestor   | agent@gmail.com |
      | status      | open            |
      | assign_to   | admin@gmail.com |
    Then the create ticket API response status code should be 201
    And the created ticket source should be "email"

  Scenario: Create ticket without title and expect error message
    When I create a ticket with details:
      | description | System issue2   |
      | priority    | low             |
      | status      | open            |
      | requestor   | agent@gmail.com |
      | assign_to   | agent@gmail.com |
      | source      | email           |
    Then the create ticket API response status code should be 422
    And the API error message should be "Title can't be blank"

  Scenario: Create ticket without description and expect error message
    When I create a ticket with details:
      | title     | Testing tickets |
      | priority  | high            |
      | status    | open            |
      | requestor | agent@gmail.com |
      | assign_to | agent@gmail.com |
      | source    | email           |
    Then the create ticket API response status code should be 422
    And the API error message should be "Description can't be blank"

  Scenario: Create ticket without requestor and expect error message
    When I create a ticket with details:
      | title       | Testing tickets |
      | description | System issue2   |
      | priority    | medium          |
      | status      | open            |
      | assign_to   | agent@gmail.com |
      | source      | email           |
    Then the create ticket API response status code should be 422
    And the API error message should be "Requestor can't be blank"

  Scenario: Create ticket without priority and expect error message
    When I create a ticket with details:
      | title       | Testing tickets |
      | description | System issue2   |
      | status      | open            |
      | requestor   | agent@gmail.com |
      | assign_to   | agent@gmail.com |
      | source      | email           |
    Then the create ticket API response status code should be 422
    And the API error message should be "Priority must be one of: low, medium, high"

  Scenario: Create ticket with invalid status and expect error message
    When I create a ticket with details:
      | title       | Testing tickets |
      | description | System issue2   |
      | status      | asasdf          |
      | requestor   | agent@gmail.com |
      | priority    | low             |
      | assign_to   | admin@gmail.com |
    Then the create ticket API response status code should be 422
    And the API error message should be "Status must be one of: open, in_progress, on_hold, resolved, closed"

  Scenario: Verify pagination of tickets
    When I fetch all tickets
    Then the get tickets API response status code should be 200
    And verify the tickets response has pagination implemented

  Scenario: Updating details of a ticket
    When I fetch all tickets
    And I update the details of a ticket with:
      | title       | Updated Testing tickets |
      | description | Updated System issue2   |
      | priority    | high                    |
      | source      | phone                   |
      | status      | on_hold                 |
      | requestor   | agent@gmail.com         |
      | assign_to   | agent@gmail.com         |
    Then the update ticket API response status code should be 200
    And I fetch all tickets
    Then verify the ticket details are updated successfully in the tickets list