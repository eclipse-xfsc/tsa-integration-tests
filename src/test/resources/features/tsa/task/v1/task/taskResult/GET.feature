#Copyright (c) 2023 Vereign AG [https://www.vereign.com]

@rest @all @tsa @task
Feature: API -TSA - Task - v1/taskList GET
  As user
  I want to execute the list of tasks
  So I am able to bring the consistency to running multiple asynchronous tasks

  Background:
    Given we are testing the TSA Task Api

  Scenario: TSA - Executing Task with DID resolver and checking the Task Results - Positive
  #Execute Task
    When I load the REST request {Policy.json} with profile {did_key}
    And I execute the Task {didResolve} via TSA Task API
    Then the status code should be {200}
    And the response is valid according to the {Task_Execute_schema.json} REST schema
  #GET Task Result
    Then I clear the request body
    And I wait for {2000} mseconds
    And I get the current Task Result via TSA Task API
    Then the status code should be {200}
    And the response is valid according to the {Task_ExecuteDID_schema.json} REST schema
    And the field {data.didDocument.id} has the value {did:key:z6Mkfriq1MqLBoPWecGoDLjguo1sB9brj6wT3qZ5BxkKpuP6}

  Scenario: TSA - Get result from Task List with two synchronous tasks - Positive
    When I set the following request body {{}}
    And I execute the taskList {testList} via TSA Task API
    Then the status code should be {200}
    And the response is valid according to the {TaskList_Execute_schema.json} REST schema
    And I wait for {1000} mseconds
    Then I get the current taskList Status via TSA Task API
    And the status code should be {200}
    And I get the result of Task {0}
    And the status code should be {200}
    And I get the result of Task {1}
    And the status code should be {200}
    And the response is valid according to the {Policy_EvaluateDID_schema.json} REST schema
    And the field {data.didDocument.id} has the value {did:key:z6Mkfriq1MqLBoPWecGoDLjguo1sB9brj6wT3qZ5BxkKpuP6}
