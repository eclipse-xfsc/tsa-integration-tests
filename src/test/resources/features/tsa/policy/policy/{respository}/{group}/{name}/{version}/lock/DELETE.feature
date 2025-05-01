#Copyright (c) 2023 Vereign AG [https://www.vereign.com]

@rest @all @tsa @policy @lock
Feature: API - TSA - Policy - {repository}/{group}/{name}/{version}/lock DELETE
  As user
  I want to unlock a policy
  So I can evaluate it

  Background:
    Given we are testing the TSA Policy Api

  Scenario: TSA - Unlock policy - Positive
    When I lock the Policy group {example} name {test} version {1.0}
    And I unlock the policy group {example} name {test} version {1.0}
    And the status code should be {200}
    # Check if the policy can be executed
    When I load the REST request {Policy.json} with profile {successful_message}
    And I execute the Policy group {example} name {test} version {1.0} via TSA Policy API
    Then the status code should be {200}

  @negative
  Scenario: TSA - Unlock none existing policy - Negative
    And I unlock the policy group {example} name {non_existing} version {1.0}
    And the status code should be {404}
    And the field {message} has the value {policy not found}
