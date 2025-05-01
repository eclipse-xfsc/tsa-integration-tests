#Copyright (c) 2023 Vereign AG [https://www.vereign.com]

@rest @all @tsa @policy @phase2 @IDM.TSA.E1.00009 @IDM.TSA.E1.00060
Feature: API - TSA -Policy - {repository}/{group}/{name}/{version}/validation POST
  As user
  I want to be able to evaluate a policy output by JSON schema
  So I know if the output is as expected

  Background:
    Given we are testing the TSA Policy Api

  Scenario: TSA - Validate policy output by JSON schema - Positive
    When I load the REST request {Policy.json} with profile {valid_schema_output}
    When I validate the output of policy using POST with group {example} name {examplePolicy} version {1.4} by JSON schema
    Then the status code should be {200}
    And the field {foo} has the value {barbaz}

  @negative
  Scenario: TSA - Validate invalid policy output by JSON schema - Negative
    When I load the REST request {Policy.json} with profile {invalid_schema_output}
    When I validate the output of policy using POST with group {example} name {examplePolicy} version {1.4} by JSON schema
    Then the status code should be {500}
    And the field {message} has the value {policy output schema validation failed}

  @negative
  Scenario: TSA - Validate invalid policy output by JSON schema - Negative
    When I load the REST request {Policy.json} with profile {invalid_schema_output}
    When I validate the output of policy using POST with group {example} name {examplePolicy} version {1.2} by JSON schema
    Then the status code should be {400}
    And the field {message} has the value {validation schema for policy output is not found}
