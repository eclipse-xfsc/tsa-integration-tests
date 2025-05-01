#Copyright (c) 2023 Vereign AG [https://www.vereign.com]

@rest @all @tsa @signer
Feature: API - TSA - Signer key namespaces - v1/namespaces GET
  As user
  I want to see what key namespaces are available
  So I am able to use the keys in the namespace in the future

  Background:
    Given we are testing the TSA Signer Api

  Scenario: TSA - Getting all key namespaces from Singer - Positive
    When I get all key namespaces via TSA Signer API
    And the status code should be {200}
    And the response body contains {transit}
