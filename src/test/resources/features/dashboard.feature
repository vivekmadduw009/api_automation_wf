@dashboard @regression
Feature: Dashboard API Validation

  Background:
    Given I am logged in as a valid user

  Scenario: Fetch dashboard summary
    When I fetch the dashboard summary
    And the dashboard summary API response status code should be 200
    Then verify the dashboard summary response is valid from the tickets data

  Scenario: Fetch dashboard charts
    When I fetch the dashboard charts
    And the dashboard charts API response status code should be 200
    Then verify the dashboard charts response is valid from the tickets data

  @testdb
  Scenario: Testing DB connection
    When I execute a query to fetch ticket data
    Then the ticket data should be retrieved successfully