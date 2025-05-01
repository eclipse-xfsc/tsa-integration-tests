#Copyright (c) 2023 Vereign AG [https://www.vereign.com]

@rest @all @tsa @policy
Feature: API -TSA - Policy - {repository}/{group}/{name}/{version}/evaluation POST
  As user
  I want to evaluate the policy
  So I am able to execute it in the future

  Acceptance criteria:
  - HTTP endpoint to evaluate the policy
  - example policy created and committed to Git repo
  - Green test based on example committed to the system

  Background:
    Given we are testing the TSA Policy Api

  Scenario: TSA - Evaluate policy synchronously - Positive
    When I load the REST request {Policy.json} with profile {successful_message}
    And I execute the Policy group {example} name {test} version {1.0} via TSA Policy API
    Then the status code should be {200}
    And the response is valid according to the {Policy_Evaluate_schema.json} REST schema
    And the field {allow} has the value {true}

  Scenario: TSA - DID resolution - Positive
    When I load the REST request {Policy.json} with profile {did_key}
    And I execute the Policy group {example} name {resolve} version {1.0} via TSA Policy API
    Then the status code should be {200}
    And the response is valid according to the {Policy_EvaluateDID_schema.json} REST schema
    And the field {data.didDocument.id} has the value {did:key:z6Mkfriq1MqLBoPWecGoDLjguo1sB9brj6wT3qZ5BxkKpuP6}

  Scenario: TSA - using header values for policy execution - Positive
    When I load the REST request {Policy.json} with profile {empty}
    And I load value {someValue} into current request HEADER {X-Cache-Key}
    And I execute the Policy group {example} name {getHeader} version {1.0} via TSA Policy API
    Then the status code should be {200}
    And the field {key} has the value {someValue}

  Scenario: TSA - using multiple header values for policy execution - Positive
    When I load the REST request {Policy.json} with profile {empty}
    And I load value {someValue} into current request HEADER {X-Cache-Key}
    And I load value {another-value} into current request HEADER {X-Another-Header}
    And I execute the Policy group {example} name {getMultiHeaders} version {1.0} via TSA Policy API
    Then the status code should be {200}
    And the field {key} has the value {someValue}
    And the field {another} has the value {another-value}

  @phase2 @IDM.TSA.E1.00013
  Scenario: TSA - Calling external URL - Positive
    When I execute the Policy group {example} name {externalURL} version {1.0} via TSA Policy API
    Then the status code should be {200}
    And the field {body.service} has the value {policy}
    And the field {body.status} has the value {up}
    And the field {status} has the value {200 OK}

  @phase2 @IDM.TSA.E1.00013
  Scenario: TSA - DID resolution using external URL - Positive
    When I execute the Policy group {example} name {externalURL} version {1.1} via TSA Policy API
    Then the status code should be {200}
    And the field {body.data.didDocument.id} has the value {did:key:z6Mkfriq1MqLBoPWecGoDLjguo1sB9brj6wT3qZ5BxkKpuP6}

    @phase2 @IDM.TSA.E1.00014
      Scenario:  TSA - Work with database storage - Set data - Positive
      When I load the REST request {Policy.json} with profile {storageSet}
      And I execute the Policy group {example} name {storageSet} version {1.0} via TSA Policy API
      Then the status code should be {200}
      When I clear the request body
      And I load the REST request {Policy.json} with profile {storageKey}
      And I execute the Policy group {example} name {storageGet} version {1.0} via TSA Policy API
      Then the status code should be {200}
      And the field {record1} has the value {value for record 1}
      And the field {record2} has the value {true}
      And the field {record2} has the value {true}
      And the field {record3} has the value {3}
      When I clear the request body
      And I load the REST request {Policy.json} with profile {storageKey}
      And I execute the Policy group {example} name {storageDelete} version {1.0} via TSA Policy API
      Then the status code should be {200}

  @negative
  Scenario: TSA - Evaluate policy with incorrect value - Negative
    When I load the REST request {Policy.json} with profile {incorrect_message}
    And I execute the Policy group {example} name {test} version {1.0} via TSA Policy API
    Then the status code should be {200}
    And the response is valid according to the {Policy_Evaluate_schema.json} REST schema
    And the field {allow} has the value {false}

  @negative
  Scenario: TSA - Evaluate policy with missing body- Negative
    And I execute the Policy group {example} name {test} version {1.0} via TSA Policy API
    Then the status code should be {200}
    And the response is valid according to the {Policy_Evaluate_schema.json} REST schema
    And the field {allow} has the value {false}

  @negative
  Scenario: TSA - Evaluate missing policy - Negative
    When I load the REST request {Policy.json} with profile {successful_message}
    And I execute the Policy group {example} name {missing} version {1.0} via TSA Policy API
    Then the status code should be {404}
    And the response is valid according to the {Policy_Evaluate_negative_schema.json} REST schema
    And the field {message} has the value {error evaluating policy}

  @negative
  Scenario: TSA - DID resolution with incorrect did - Negative
    When I load the REST request {Policy.json} with profile {did_missing_method}
    And I execute the Policy group {example} name {resolve} version {1.0} via TSA Policy API
    Then the status code should be {200}
    And the response is valid according to the {Policy_EvaluateDID_negative_schema.json} REST schema
    And the field {data.didResolutionMetadata.error} has the value {notFound}

  @negative @phase2 @IDM.TSA.E1.00014
  Scenario:  TSA - Work with database storage - Set data with empty key - Negative
    When I load the REST request {Policy.json} with profile {emptyKey}
    And I execute the Policy group {example} name {storageSet} version {1.0} via TSA Policy API
    Then the status code should be {500}
    And the field {message} has the value {error evaluating rego query}

  @negative @phase2 @IDM.TSA.E1.00014
  Scenario:  TSA - Work with database storage - Set data with incorrect key - Negative
    When I load the REST request {Policy.json} with profile {numberAsKey}
    And I execute the Policy group {example} name {storageSet} version {1.0} via TSA Policy API
    Then the status code should be {500}
    And the field {message} has the value {error evaluating rego query}

  @negative @phase2 @IDM.TSA.E1.00014
  Scenario:  TSA - Work with database storage - Set data with empty data field - Negative
    When I load the REST request {Policy.json} with profile {emptyData}
    And I execute the Policy group {example} name {storageSet} version {1.0} via TSA Policy API
    Then the status code should be {500}
    And the field {message} has the value {error evaluating rego query}
