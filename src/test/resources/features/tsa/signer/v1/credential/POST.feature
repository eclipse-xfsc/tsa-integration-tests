#2023 Vereign AG [https://www.vereign.com]

@rest @all @tsa @signer @phase2 @IDM.TSA.E1.00005 @IDM.TSA.E1.00006 @wip
Feature: API - TSA - Signer create credential - v1/credential POST
  As user
  I want to be able to create credential from plain JSON
  So the credential can be verified

  Background:
    Given we are testing the TSA Signer Api

  Scenario: TSA - create credential from plain JSON - Positive
    When I load the REST request {Credential.json} with profile {plain_JSON}
    And I create credential from plain JSON via TSA Signer API
    Then the status code should be {200}
    And the response is valid according to the {Signer_plain_JSON_credential_schema.json} REST schema
    And the field {proof.verificationMethod} has the value {did:web:vault.vereign.com:tsa:policy:policy:example:returnDID:1.0:evaluation#key1}

  Scenario: TSA - create credential from plain JSON with two credential subjects - Positive
    When I load the REST request {Credential.json} with profile {plain_JSON_with_two_subjects}
    And I create credential from plain JSON via TSA Signer API
    Then the status code should be {200}
    And the response is valid according to the {Signer_plain_JSON_credential_schema.json} REST schema
    And the field {proof.verificationMethod} has the value {did:web:vault.vereign.com:tsa:policy:policy:example:returnDID:1.0:evaluation#key1}

  @negative
  Scenario: TSA - create credential from blank JSON - Negative
    When I load the REST request {Credential.json} with profile {blank_JSON}
    And I create credential from plain JSON via TSA Signer API
    Then the status code should be {400}
    And the field {message} contains the value {"namespace" is missing from body}
    And the field {message} contains the value {"key" is missing from body}
    And the field {message} contains the value {"credential" is missing from body}

  @negative
  Scenario: TSA - create credential with missing body - Negative
    When I load the REST request {Credential.json} with profile {missing_JSON}
    And I create credential from plain JSON via TSA Signer API
    Then the status code should be {400}
    And the field {message} contains the value {"namespace" is missing from body}
    And the field {message} contains the value {"key" is missing from body}
    And the field {message} contains the value {"credential" is missing from body}

  @negative
  Scenario: TSA - create credential from JSON with missing closing bracket - Negative
    When I load the REST request {Credential.json} with profile {closing_bracket_JSON}
    And I create credential from plain JSON via TSA Signer API
    Then the status code should be {400}
    And the field {message} contains the value {"namespace" is missing from body}
    And the field {message} contains the value {"key" is missing from body}
    And the field {message} contains the value {"credential" is missing from body}