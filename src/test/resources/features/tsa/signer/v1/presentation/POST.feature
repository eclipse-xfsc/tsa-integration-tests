#Copyright (c) 2023 Vereign AG [https://www.vereign.com]

@rest @all @tsa @signer
Feature: API - TSA - Signer create presentation from JSON - v1/presentation POST
  As user
  I want to create verifiable presentation from JSON
  So the presentation can be verified

  Background:
    Given we are testing the TSA Signer Api

  Scenario Outline: TSA - create presentation from JSON <labelSuffix> - Positive
    When I load the REST request {Data.json} with profile {<profileOption>}
    And I create presentation from JSON via TSA Signer API
    Then the status code should be {200}
    And the response is valid according to the {<schema>} REST schema
    And the field {proof.verificationMethod} has the value {did:web:gaiax.vereign.com:tsa:policy:policy:example:returnDID:1.0:evaluation#key1}

    Examples:
      | labelSuffix  | profileOption | schema                          |
      | single data  | single        | Signer_JSON_single_schema.json  |
      | multi data   | multi         | Signer_JSON_multi_schema.json   |
      | with context | context       | Signer_JSON_context_schema.json |
      | with context | context_no_at | Signer_JSON_context_schema.json |

  @negative
  Scenario Outline: TSA - create presentation from JSON <labelSuffix> - Negative
    When I load the REST request {Data.json} with profile {<profileOption>}
    And I create presentation from JSON via TSA Signer API
    Then the status code should be {400}
    And the response is valid according to the {Signer_CreatePresentation_negative_schema.json} REST schema
    And the field {message} contains the value {<errMessage>}

    Examples:
      | labelSuffix        | profileOption | errMessage                                         |
      | blank              | blank         | "data" is missing from body                        |
      | missing data field | missing_data  | "data" is missing from body                        |
      | blank data         | blank_data    | "issuer" is missing from body                      |
      | wrong format       | data_format   | json: cannot unmarshal string into Go struct field |

  @negative
  Scenario: TSA - create presentation from JSON with invalid body - Negative
    When I set the following request body {{"data":[{"text": "some text"}}}
    And I create presentation from JSON via TSA Signer API
    Then the status code should be {400}
    And the response is valid according to the {Signer_CreatePresentation_negative_schema.json} REST schema
    And the field {message} contains the value {invalid character}
