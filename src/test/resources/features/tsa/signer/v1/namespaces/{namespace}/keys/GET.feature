#Copyright (c) 2023 Vereign AG [https://www.vereign.com]

@rest @all @tsa @signer
Feature: API - TSA - Signer key namespaces - v1/namespaces/{namespace}/keys GET
  As user
  I want to see what keys a namespaces have
  So I am able to use the keys in them in the future

  Background:
    Given we are testing the TSA Signer Api

  Scenario: TSA - Getting all keys from specific namespace Singer - Positive
    When I get all keys from namespace {transit} via TSA Signer API
    And the status code should be {200}
    And the response body contains {key1}

  @Negative
  Scenario: TSA - Getting keys from non existing namespace Singer - Negative
    When I get all keys from namespace {non_existing} via TSA Signer API
    And the status code should be {404}
    And the field {message} contains the value {no keys found in namespace}
