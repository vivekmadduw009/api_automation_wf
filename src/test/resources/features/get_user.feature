Feature: Get User API

  Scenario: Get single user details
    Given I hit the Get User API with id 2
    Then the response status code should be 200
    And the user first name should be "Janet"