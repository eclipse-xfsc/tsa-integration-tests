#Copyright (c) 2023 Vereign AG [https://www.vereign.com]

@rest @all @tsa @infohub
Feature: API - TSA - Infohub - v1/export GET
  As user
  I want to export data wrapped as Verifiable Credential or Presentation
  So I can have the data with proof

  Background:
    Given we are testing the TSA Infohub Api

  Scenario: TSA - Export through Infohub - Positive
    When I export the {testexport} via TSA Infohub API
    Then the status code should be {200}
    ## If the result is not in the cache the first call returns the below result
    ## And the field {result} has the value {export request is accepted}
    When I export the {testexport} via TSA Infohub API
    Then the status code should be {200}
    And the response is valid according to the {Infohub_Export_schema.json} REST schema
    And the field {proof.verificationMethod} contains the value {vereign.com:tsa:policy:policy:policies:example:returnDID:1.0:evaluation#key1}
    And the field {proof.type} has the value {JsonWebSignature2020}

  @negative
  Scenario: TSA - Export missing export through Infohub - Negative
    When I export the {missing_export} via TSA Infohub API
    Then the status code should be {404}
    And the response is valid according to the {Policy_Evaluate_negative_schema.json} REST schema
    And the field {message} has the value {export configuration not found}
