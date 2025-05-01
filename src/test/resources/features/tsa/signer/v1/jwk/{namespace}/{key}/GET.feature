#Copyright (c) 2023 Vereign AG [https://www.vereign.com]

@rest @all @tsa @signer @phase2 @IDM.TSA.E1.00006
Feature: API - TSA - Signer key namespaces - v1/jwk/{namespace}/{key} GET
  As user
  I want to get a key from namespaces in jwk format
  So I can use services tha require this format

  Background:
    Given we are testing the TSA Signer Api

  Scenario: TSA - Getting a key from specific namespace in JWK format Singer - Positive
    When I get key {key1} from namespace {transit} in JWK format via TSA Signer API
    Then the status code should be {200}
    And the field {kty} contains the value {EC}
    And the field {kid} contains the value {key1}
    And the field {crv} contains the value {P-256}

  @negative
  Scenario: TSA - Getting a non existing key from specific namespace in JWK format Singer - Negative
    When I get key {none_existing} from namespace {transit} in JWK format via TSA Signer API
    Then the status code should be {404}
