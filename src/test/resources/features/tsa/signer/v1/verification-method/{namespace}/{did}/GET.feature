#Copyright (c) 2023 Vereign AG [https://www.vereign.com]

@rest @all @tsa @signer
Feature: API - TSA - Signer keys - v1/verification-methods/{namespace}/{did} GET
  As user
  I want to see what keys are available
  So I am able to use them in the future

  Background:
    Given we are testing the TSA Signer Api

  Scenario: TSA - Getting all key namespaces from Singer - Positive
    When I get all key namespaces via TSA Signer API
    And the status code should be {200}
    And the response body contains {transit}

  Scenario: TSA - Getting all key from namespace - Positive
    When I get all keys from namespace {transit} via TSA Signer API
    And the status code should be {200}
    Then the response body contains {key1}

  @Negative
  Scenario: TSA - Getting all key from non-existing namespace - Negative
    When I get all keys from namespace {non-existing} via TSA Signer API
    And the status code should be {404}
    Then the field {message} has the value {no keys found in namespace}

  @wip
  Scenario: TSA - Getting all keys from namespace using DID from Singer - Positive
    When I get all keys from namespace {transit} using issuer {did:web:gaiax.vereign.com:tsa:policy:policy:policies:example:returnDID:1.0:evaluation} via TSA Signer API
    And the status code should be {200}
    And the field {[0].id} has the value {key1#key1}
    And the field {[0].type} has the value {JsonWebKey2020}
    And the field {[0].controller} has the value {key1}
    And the field {[0].publicKeyJwk.kty} has the value {EC}
    Then the field {[0].publicKeyJwk.crv} has the value {P-256}

  @wip @Negative
  Scenario: TSA - Getting missing key from namespace from Singer - Negative
    When I get all keys from namespace {transit} using issuer {missing-key} via TSA Signer API
    And the status code should be {404}

  @wip
  Scenario: TSA - Getting key using DID from Singer - Positive
    When I get key {key1} from namespace {transit} using issuer {did:web:gaiax.vereign.com:tsa:policy:policy:example:returnDID:1.0:evaluation} via TSA Signer API
    And the status code should be {200}
    And the response is valid according to the {Signer_GetKey_schema.json} REST schema
    And the field {id} has the value {key1#key1}
    And the field {type} has the value {JsonWebKey2020}
    And the field {controller} has the value {key1}
    And the field {publicKeyJwk.kty} has the value {EC}
    Then the field {publicKeyJwk.crv} has the value {P-256}

  @wip @Negative
  Scenario: TSA - Getting key using non-existing DID from Singer - Negative
    When I get key {key1} from namespace {transit} using issuer {non-existing} via TSA Signer API
    And the status code should be {200}
    And the response is valid according to the {Signer_GetKey_schema.json} REST schema
    And the field {id} has the value {key1#key1}
    And the field {type} has the value {JsonWebKey2020}
    And the field {controller} has the value {key1}
    And the field {publicKeyJwk.kty} has the value {EC}
    Then the field {publicKeyJwk.crv} has the value {P-256}
