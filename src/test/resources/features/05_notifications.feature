Feature: Notifications API Validation
  Background:
    Given I am logged in as a valid user


  Scenario: Create new ticket to generate  notification
    When Create a ticket and assignee as Agent for notification with details:
      | title       | Testing tickets |
      | description | System issue2   |
      | priority    | low             |
      | source      | email           |
      | status      | open            |
      | requestor   | admin@gmail.com |
      | assign_to   | agent@gmail.com |
    And the create ticket API response in notifications status code should be 201
    And  Log in with Agent user
    And  Fetch all notification
    Then the get notification API response status code should be 200
    And verify the created ticket has created notification in response

  Scenario: Mark Read single Notification
     When Create a ticket and assignee as Agent for notification with details:
       | title       | Testing tickets |
       | description | System issue2   |
       | priority    | low             |
       | source      | email           |
       | status      | open            |
       | requestor   | admin@gmail.com |
       | assign_to   | agent@gmail.com |
    Given  Log in with Agent user
    When Fetch all notification
    And  Fetch first notification whose read=false
    And  Mark that notification as read
    Then the mark-read notification API response status code should be 200
    And verify mark read api in response
    And Fetch all notification
    And verify read status in get Notification API


  Scenario: Mark Read All Notification
    When Create a ticket and assignee as Agent for notification with details:
      | title       | Testing tickets |
      | description | System issue2   |
      | priority    | low             |
      | source      | email           |
      | status      | open            |
      | requestor   | admin@gmail.com |
      | assign_to   | agent@gmail.com |
    Given  Log in with Agent user
    When Fetch all notification
    And Get and store read value of all notification
    And  Mark all notification as read
    Then the mark-all-read notification API response status code should be 200
    And verify mark all read api in response
    And Fetch all notification
    And verify all notification read status after marking all read


  Scenario: Verify if user is getting notification when status is changed
    When I fetch all tickets
    And Get ticket details where requestor admin, assignee is agent and status is open
    And  Update the status to inprogress with following details:
      | title       | Updated Testing tickets |
      | description | Updated System issue2   |
      | priority    | low                     |
      | source      | email                   |
      | status      | in_progress             |
      | requestor   | admin@gmail.com         |
      | assign_to   | agent@gmail.com         |
    And Log in with Agent user
    And Fetch all notification
    Then the get notification API response status code should be 200
    And Verify in get response that notification is created for status update













