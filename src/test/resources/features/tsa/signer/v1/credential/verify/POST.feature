#Copyright (c) 2023 Vereign AG [https://www.vereign.com]

@rest @all @tsa @signer
Feature: API - TSA - Signer credential verify - v1/credential/verify POST
  As user
  I want to verify a credential
  So I know it is valid

  Background:
    Given we are testing the TSA Signer Api

  Scenario: TSA - verify credential proof - Positive
    When I load {Credential.json} request for environment {for_proof_happypath} via TSA
    And I create credential proof via TSA Signer API
    Then the status code should be {200}
    And the response is valid according to the {Signer_CredentialProof_schema.json} REST schema
    And the field {proof.verificationMethod} contains the value {vereign.com:tsa:policy:policy:policies:example:returnDID:1.0:evaluation#key1}
    Then I get the last response body and load it to the current request body
    And I verify credential proof via TSA Signer API
    And the status code should be {200}
    And the field {valid} has the value {true}

  @negative
  Scenario Outline: TSA - verify credential proof with <labelSuffix> - Negative
    When I load the REST request {Credential.json} with profile {<profileOption>}
    And I verify credential proof via TSA Signer API
    Then the status code should be {400}
    And the field {message} contains the value {<messageText>}

    Examples:
      | labelSuffix                 | profileOption                    | messageText                                   |
      | missing proof               | missing_proof                    | verifiable credential must have proof section |
      | modified ID                 | modified_ID                      | invalid signature                             |
      | modified issuanceDate field | modified_issuanceDate            | invalid signature                             |
      | modified allow field        | modified_credentialSubject_allow | invalid signature                             |
      | modified array field        | modified_credentialSubject_array | invalid signature                             |
