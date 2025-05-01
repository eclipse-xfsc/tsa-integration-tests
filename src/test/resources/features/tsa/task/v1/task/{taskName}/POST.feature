#Copyright (c) 2023 Vereign AG [https://www.vereign.com]

@rest @all @tsa @task
Feature: API -TSA - Task - v1/task POST
  As user
  I want to evaluate the policy asynchronously
  So I am able to execute the developed Rego code in the future non-blocking

  Acceptance criteria:
  - HTTP endpoints to evaluate the policy asynchronously and get the result
  - example of long-running policy committed to Git repo
  - Green test based on example committed to the system

  Background:
    Given we are testing the TSA Task Api

  Scenario: TSA - Executing Task with DID resolver - Positive
    When I load the REST request {Policy.json} with profile {did_key}
    And I execute the Task {didResolve} via TSA Task API
    Then the status code should be {200}
    And the response is valid according to the {Task_Execute_schema.json} REST schema
    Then I clear the request body
    And I wait for {2000} mseconds
    Then I get the current Task Result via TSA Task API
    And the status code should be {200}
    And the response is valid according to the {Task_ExecuteDID_schema.json} REST schema
    And the field {data.didDocument.id} has the value {did:key:z6Mkfriq1MqLBoPWecGoDLjguo1sB9brj6wT3qZ5BxkKpuP6}

  Scenario: TSA - Executing Task with method and url - Positive
    When I set the following request body {{"message": "hello"}}
    And I execute the Task {exampleTask} via TSA Task API
    Then the status code should be {200}
    And the response is valid according to the {Task_Execute_schema.json} REST schema
    And I wait for {2000} mseconds
    And I clear the request body
    Then I get the current Task Result via TSA Task API
    And the status code should be {200}
    And the field {title} has the value {delectus aut autem}

  @negative
  Scenario: TSA - Executing Task with non existing task template - Negative
    When I load the REST request {Policy.json} with profile {did_key}
    And I execute the Task {non_existing} via TSA Task API
    Then the response is valid according to the {Policy_Evaluate_negative_schema.json} REST schema
    And the field {message} has the value {task template not found}
