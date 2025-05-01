#Copyright (c) 2023 Vereign AG [https://www.vereign.com]

@rest @all @tsa @signer
Feature: API - TSA - Signer presentation verify - v1/presentation/verify POST
  As user
  I want to verify a presentation
  So I know it is valid

  Background:
    Given we are testing the TSA Signer Api

  Scenario: TSA - verify presentation proof - Positive
    When I load {Presentation.json} request for environment {for_proof_happypath} via TSA
    When I create presentation proof via TSA Signer API
    And the status code should be {200}
    And the response is valid according to the {Signer_PresentationProof_schema.json} REST schema
    And the field {proof.verificationMethod} contains the value {vereign.com:tsa:policy:policy:policies:example:returnDID:1.0:evaluation#key1}
    Then I get the last response body and load it to the current request body
    And I verify presentation proof via TSA Signer API
    And the status code should be {200}
    And the field {valid} has the value {true}

  @negative
  Scenario Outline: TSA - verify presentation proof with <labelSuffix> - Negative
    When I load the REST request {Presentation.json} with profile {<profileOption>}
    And I verify presentation proof via TSA Signer API
    And the status code should be {400}
    And the field {message} contains the value {<messageText>}

    Examples:
      | labelSuffix                                | profileOption                      | messageText                                     |
      | missing proof                              | missing_proof                      | verifiable presentation must have proof section |
      | modified ID                                | modified_ID                        | invalid signature                               |
      | modified issuanceDate field                | modified_issuanceDate              | invalid signature                               |
      | modified first allow field                 | modified_credentialSubject_1_allow | invalid signature                               |
      | modified second allow field                | modified_credentialSubject_2_allow | invalid signature                               |
      | modified first credentialSubject.ID field  | modified_credentialSubject_1_ID    | invalid signature                               |
      | modified second credentialSubject.ID field | modified_credentialSubject_2_ID    | invalid signature                               |
