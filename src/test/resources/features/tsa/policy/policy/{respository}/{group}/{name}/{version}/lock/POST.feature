#Copyright (c) 2023 Vereign AG [https://www.vereign.com]

@rest @all @tsa @policy @lock
Feature: API - TSA -Policy - {repository}/{group}/{name}/{version}/lock POST
  As user
  I want to evaluate the policy
  So I am able to execute it in the future

  Acceptance criteria:
  - HTTP endpoint to evaluate the policy
  - example policy created and committed to Git repo
  - Green test based on example committed to the system

  Background:
    Given we are testing the TSA Policy Api

  Scenario: TSA - Lock policy - Positive
    When I lock the Policy group {example} name {test} version {1.0}
    Then the status code should be {200}
    When I load the REST request {Policy.json} with profile {successful_message}
    And I execute the Policy group {example} name {test} version {1.0} via TSA Policy API
    Then the status code should be {403}
    And the field {message} has the value {error evaluating policy}
    And I unlock the policy group {example} name {test} version {1.0}

  @negative
  Scenario: TSA - Lock already locked policy - Negative
    When I lock the Policy group {example} name {test} version {1.0}
    When I lock the Policy group {example} name {test} version {1.0}
    And the status code should be {403}
    And the field {message} has the value {policy is already locked}
    And I unlock the policy group {example} name {test} version {1.0}
