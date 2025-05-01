#Copyright (c) 2023 Vereign AG [https://www.vereign.com]

@rest @all @tsa @policy @phase2 @IDM.TSA.E1.00005 @IDM.TSA.E1.00006
Feature: API - TSA - policy - {repository}/{group}/{name}/{version}/key GET
  As user
  I want to be able to see all the information about the key
  with which a specific Policy is signed on export
  So I can later verify that the export is authentic

  Background:
    Given we are testing the TSA Policy Api


  Scenario: TSA - Export signing key information for specific policy - Positive
    When I request the export signing key information for Policy group {example} name {examplePolicy} version {1.4}
    Then the status code should be {200}
    And the field {crv} contains the value {P-256}
    And the field {kid} contains the value {key1}
    And the field {kty} contains the value {EC}
    And the field {x} is present and not empty
    And the field {y} is present and not empty

  @negative
  Scenario: TSA - Export signing key information for policy with no export config information - Negative
    When I request the export signing key information for Policy group {example} name {didResolve} version {1.0}
    Then the status code should be {403}

  @negative
  Scenario: TSA - Export signing key information for non existing policy - Negative
    When I request the export signing key information for Policy group {example} name {nonExistingPolicy} version {1.3}
    Then the status code should be {404}
    And the field {message} contains the value {policy not found}
