Feature: Tickets API Validation

  Background:
    Given I am logged in as a valid user

  Scenario: Fetch all tickets
    When I fetch all tickets
    Then the tickets API response status code should be 200
    And the tickets response should be valid

  Scenario: Fetch a ticket by ID
    When I fetch all tickets
    And I fetch a ticket using ticket_id from the fetched tickets
    Then the tickets API response status code should be 200
    And verify the fetched ticket data is valid

  Scenario: Create a new ticket with assignee
    When I create a ticket with details:
      | title       | Testing tickets      |
      | description | System issue2        |
      | priority    | low                  |
      | source      | Desktop              |
      | status      | open                 |
      | requestor   | testing              |
      | assign_to   | assignTest           |
    Then the tickets API response status code should be 201
    And verify the created ticket data from get tickets response

  Scenario: Deleting a ticket
    When I fetch all tickets
    And I delete a ticket using ticket_id from the fetched tickets
    Then the tickets API response status code should be 200
    And I fetch all tickets
    Then verify the ticket is deleted successfully in the tickets list

  Scenario: Updating a ticket status
    When I fetch all tickets
    And I update the status of a ticket to "InProgress"
    Then the tickets API response status code should be 200
    And I fetch all tickets
    Then verify the ticket status is updated successfully in the tickets list

  Scenario: Create ticket without assignee and expect assign_to is null
    When I create a ticket with details:
      | title       | Testing tickets      |
      | description | System issue2        |
      | priority    | low                  |
      | source      | Desktop              |
      | status      | open                 |
      | requestor   | testing              |
    Then the tickets API response status code should be 201
    And the created ticket should have assign_to as null

  Scenario: Create ticket without status and expect default "open" status
    When I create a ticket with details:
      | title       | Testing tickets      |
      | description | System issue2        |
      | priority    | low                  |
      | source      | Desktop              |
      | requestor   | testing              |
      | assign_to   | assignTest           |
    Then the tickets API response status code should be 201
    And the created ticket status should be "open"

  Scenario: Create ticket without source and expect default "email"
    When I create a ticket with details:
      | title       | Testing tickets      |
      | description | System issue2        |
      | priority    | low                  |
      | requestor   | testing              |
      | status      | open                 |
      | assign_to   | assignTest           |
    Then the tickets API response status code should be 201
    And the created ticket source should be "email"

  Scenario: Create ticket without title and expect error message
    When I create a ticket with details:
      | description | System issue2        |
      | priority    | low                  |
      | status      | open                 |
      | requestor   | testing              |
      | assign_to   | assignTest           |
      | source      | Desktop              |
    Then the tickets API response status code should be 422
    And the API error message should be "Title can't be blank"

  Scenario: Create ticket without description and expect error message
    When I create a ticket with details:
      | title       | Testing tickets      |
      | priority    | high                 |
      | status      | open                 |
      | requestor   | testing              |
      | assign_to   | assignTest           |
      | source      | Desktop              |
    Then the tickets API response status code should be 422
    And the API error message should be "Description can't be blank"

  Scenario: Create ticket without requestor and expect error message
    When I create a ticket with details:
      | title       | Testing tickets      |
      | description | System issue2        |
      | priority    | medium               |
      | status      | open                 |
      | assign_to   | assignTest           |
      | source      | Desktop              |
    Then the tickets API response status code should be 422
    And the API error message should be "Requestor can't be blank"

  Scenario: Create ticket without priority and expect error message
    When I create a ticket with details:
      | title       | Testing tickets      |
      | description | System issue2        |
      | status      | open                 |
      | requestor   | testing              |
      | assign_to   | assignTest           |
      | source      | Desktop              |
    Then the tickets API response status code should be 422
    And the API error message should be "Priority is invalid. Allowed values: low, medium, high"

  Scenario: Create ticket with invalid status and expect error message
    When I create a ticket with details:
      | title       | Testing tickets      |
      | description | System issue2        |
      | status      | asasdf               |
      | requestor   | testing              |
      | priority    | low                  |
      | assign_to   | assignTest           |
    Then the tickets API response status code should be 422
    And the API error message should be "Status is invalid. Allowed values: open, InProgress, OnHold, resolved"

  Scenario: Updating assignee of a ticket
    When I fetch all tickets
    And I update the assignee of a ticket to "testAssignee"
    Then the tickets API response status code should be 200
    And I fetch all tickets
    Then verify the ticket assignee is updated successfully in the tickets list
