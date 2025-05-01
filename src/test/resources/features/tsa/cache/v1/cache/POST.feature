#Copyright (c) 2023 Vereign AG [https://www.vereign.com]

@rest @all @tsa @cache
Feature: API -TSA - Cache - v1/cache POST
  As policy administrator
  I want to have distributed cache feature provided
  So I am able to use cache functionality in my custom policies

  Acceptance criteria:
  - The plugin for rego language to get/set values is ready to use
  - The working example how to use the plugin
  - Green test based on example committed to the system

  Background:
    Given we are testing the TSA Cache Api

  Scenario: TSA - Setting Cache - Positive
    When I load the REST request {Cache.json} with profile {successful_set}
    And I load value {test} into current request HEADER {x-cache-key}
    And I send the Cache POST request via TSA Cache API
    And the status code should be {201}

  @negative
  Scenario: TSA - Setting Cache with missing header - x-cache-key - Negative
    When I load the REST request {Cache.json} with profile {missing_body}
    And I send the Cache POST request via TSA Cache API
    Then the status code should be {400}
    And the response is valid according to the {Cache_negative_schema.json} REST schema
    And the field {message} has the value {"key" is missing from header}

  @negative
  Scenario: TSA - Setting Cache with missing body - Negative
    When I send the Cache POST request via TSA Cache API
    Then the status code should be {400}
    And the response is valid according to the {Cache_negative_schema.json} REST schema
    And the field {message} has the value {missing required payload}
