#Copyright (c) 2023 Vereign AG [https://www.vereign.com]

@rest @all @tsa @policy @phase2 @IDM.TSA.E1.00005 @IDM.TSA.E1.00006 @IDM.TSA.E1.00011 @IDM.TSA.E1.00056
Feature: API - TSA - policy - {repository}/{group}/{name}/{version}/export GET
  As user
  I want to be able to export policy
  So I can later import it

  Background:
    Given we are testing the TSA Policy Api

  Scenario: TSA - Export specific policy - Positive
    When I add a new publicKey header to the currentRequest
    When I Export Policy group {example} name {examplePolicy} version {1.4}
    Then the status code should be {200}
    And the response {Content-Disposition} header contains the text {attachment; filename="policies_example_examplePolicy_1.4.zip"}
    And the response body contains {policy_bundle.zip}
    And the response body contains {signature.raw}

  @negative
  Scenario: TSA - Export policy with no export config information - Negative
    When I Export Policy group {example} name {didResolve} version {1.0}
    Then the status code should be {403}

  @negative
  Scenario: TSA - Export non existing policy - Negative
    When I Export Policy group {example} name {nonExistingPolicy} version {1.0}
    Then the status code should be {404}
    And the field {message} contains the value {policy not found}
