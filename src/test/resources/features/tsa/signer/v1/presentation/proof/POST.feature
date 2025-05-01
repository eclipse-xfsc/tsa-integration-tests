#Copyright (c) 2023 Vereign AG [https://www.vereign.com]

@rest @all @tsa @signer
Feature: API - TSA - Signer presentation proof - v1/presentation/proof POST
  As user
  I want to create a presentation proof
  So the presentation can be verified

  Background:
    Given we are testing the TSA Signer Api

  Scenario Outline: TSA - create presentation proof <labelSuffix> - Positive
    When I load the REST request {Presentation.json} with profile {<profileOption>}
    And I create presentation proof via TSA Signer API
    Then the status code should be {200}
    And the response is valid according to the {<schema>} REST schema
    And the field {proof.verificationMethod} contains the value {<verificationMethod>}

    Examples:
      | labelSuffix | profileOption        | schema                                      | verificationMethod                                                                |
      |             | for_proof            | Signer_PresentationProof_schema.json        | did:web:gaiax.vereign.com:tsa:policy:policy:example:returnDID:1.0:evaluation#key1 |
      | alumni of   | for_proof_alumni     | Signer_PresentationProof_schema_alumni.json | https://example.edu/issuers/565049#key1                                           |
      | without ID  | for_proof_without_ID | Signer_PresentationProof_schema.json        | did:web:gaiax.vereign.com:tsa:policy:policy:example:returnDID:1.0:evaluation#key1 |
#      | with proofs | for_proof_with_proofs | Signer_PresentationWithProof_schema.json    | vereign.com:tsa:policy:policy:example:returnDID:1.0:evaluation#key1 |

  @negative
  Scenario: TSA - validate presentation proof with empty body - Negative
    When I set the following request body {{}}
    And I create presentation proof via TSA Signer API
    Then the status code should be {400}
    And the field {message} contains the value {"issuer" is missing from body}
    And the field {message} contains the value {"namespace" is missing from body}
    And the field {message} contains the value {"key" is missing from body}
    And the field {message} contains the value {"presentation" is missing from body}

  @negative
  Scenario Outline: TSA - create presentation proof with incorrect ID (<labelSuffix>) - Negative
    When I load the REST request {Presentation.json} with profile {<profileOption>}
    And I create presentation proof via TSA Signer API
    Then the status code should be {400}
    And the field {message} contains the value {invalid subject id}

    Examples:
      | labelSuffix | profileOption                  |
      | space       | for_proof_incorrect_ID_space   |
      | symbols     | for_proof_incorrect_ID_symbols |

  @negative
  Scenario Outline: TSA - create presentation proof with tampered credential - Negative
    When I load the REST request {Presentation.json} with profile {<profileOption>}
    And I create presentation proof via TSA Signer API
    Then the status code should be {400}
    And the field {message} contains the value {error validating credential}

    Examples:
      | labelSuffix | profileOption                           |
      | space       | credential_tampered_verification_method |
      | space       | credential_tampered_created             |
      | space       | credential_tampered_credentialSubject   |
