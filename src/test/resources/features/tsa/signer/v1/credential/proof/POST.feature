#Copyright (c) 2023 Vereign AG [https://www.vereign.com]

@rest @all @tsa @signer
Feature: API - TSA - Signer credential proof - v1/credential/proof POST
  As user
  I want to create a credential proof
  So the credential can be verified

  Background:
    Given we are testing the TSA Signer Api

  Scenario Outline: TSA - create credential proof <labelSuffix> - Positive
    When I load the REST request {Credential.json} with profile {<profileOption>}
    And I create credential proof via TSA Signer API
    Then the status code should be {200}
    And the response is valid according to the {<schema>} REST schema
    And the field {proof.verificationMethod} has the value {did:web:vault.vereign.com:tsa:policy:policy:policies:example:returnDID:1.0:evaluation#key1}

    Examples:
      | labelSuffix | profileOption        | schema                                        |
      |             | for_proof            | Signer_CredentialProof_schema.json            |
      | alumni of   | for_proof_alumni     | Signer_CredentialProof_alumni_schema.json     |
      | without ID  | for_proof_without_ID | Signer_CredentialProof_without_ID_schema.json |

  @phase2 @IDM.TSA.E1.00005 @IDM.TSA.E1.00006 @IDM.TSA.E1.00019
  Scenario: TSA - create two consecutive proofs - Positive
    When I load the REST request {Credential.json} with profile {for_proof_with_02_proof}
    And I create credential proof via TSA Signer API
    Then the status code should be {200}
    And the field {proof[0].verificationMethod} contains the value {did:web:vault.vereign.com:tsa:policy:policy:policies:example:returnDID:1.0:evaluation#key1}
    And the field {proof[1].verificationMethod} contains the value {did:web:vault.vereign.com:tsa:policy:policy:policies:example:returnDID:1.0:evaluation#key1}
    And the field {proof[2].verificationMethod} contains the value {did:web:vault.vereign.com:tsa:policy:policy:policies:example:returnDID:1.0:evaluation#key1}

  @negative @phase2 @IDM.TSA.E1.00005 @IDM.TSA.E1.00006 @IDM.TSA.E1.00019
  Scenario: TSA - create two consecutive proofs with modified issuance date - Negative
    When I load the REST request {Credential.json} with profile {for_proof_with_modified_second_proof_date}
    And I create credential proof via TSA Signer API
    Then the status code should be {403}
    And the field {message} contains the value {invalid signature}

  @negative @phase2 @IDM.TSA.E1.00005 @IDM.TSA.E1.00006 @IDM.TSA.E1.00019
  Scenario: TSA - create two consecutive proofs with modified credential subject ID - Negative
    When I load the REST request {Credential.json} with profile {for_proof_with_modified_second_proof_cred_subject}
    And I create credential proof via TSA Signer API
    Then the status code should be {403}
    And the field {message} contains the value {invalid signature}

  @negative @phase2 @IDM.TSA.E1.00005 @IDM.TSA.E1.00006 @IDM.TSA.E1.00019
  Scenario: TSA - create two consecutive proofs with modified credential subject allow - Negative
    When I load the REST request {Credential.json} with profile {for_proof_with_modified_second_proof_cred_subject_allow}
    And I create credential proof via TSA Signer API
    Then the status code should be {403}
    And the field {message} contains the value {invalid signature}

  @negative
  Scenario: TSA - create credential proof with empty body - Negative
    When I set the following request body {{}}
    And I create credential proof via TSA Signer API
    Then the status code should be {400}
    And the field {message} contains the value {"namespace" is missing from body}
    And the field {message} contains the value {"key" is missing from body}
    And the field {message} contains the value {"credential" is missing from body}

  @negative
  Scenario Outline: TSA - create credential proof with incorrect ID (<labelSuffix>)- Negative
    When I load the REST request {Credential.json} with profile {<profileOption>}
    And I create credential proof via TSA Signer API
    Then the status code should be {400}
    And the field {message} has the value {invalid subject id: must be URI}

    Examples:
      | labelSuffix | profileOption                  |
      | space       | for_proof_incorrect_ID_space   |
      | symbols     | for_proof_incorrect_ID_symbols |
