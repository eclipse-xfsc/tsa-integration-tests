#Copyright (c) 2023 Vereign AG [https://www.vereign.com]

@rest @all @tsa @task
Feature: API -TSA - Task - v1/taskList GET
  As user
  I want to execute the list of tasks
  So I am able to bring the consistency to running multiple asynchronous tasks

  Background:
    Given we are testing the TSA Task Api

  Scenario: TSA - Get status from Task List execution - Positive
    When I load the REST request {Policy.json} with profile {did_key}
    And I execute the taskList {testList} via TSA Task API
    Then the status code should be {200}
    And the response is valid according to the {TaskList_Execute_schema.json} REST schema
    And I wait for {1000} mseconds
    Then I get the current taskList Status via TSA Task API
    Then the status code should be {200}
    Then I get the current taskList Status via TSA Task API
    And the response is valid according to the {TaskList_Status_schema.json} REST schema
    And the field {status} has the value {done}
    And the field {$..tasks..status} has the value {["done","done"]}

  Scenario: TSA - Get status from Task List with three synchronous tasks second fails - Negative
    When I set the following request body {{}}
    And I execute the taskList {failTestListSync} via TSA Task API
    Then the status code should be {200}
    And the response is valid according to the {TaskList_Execute_schema.json} REST schema
    And I wait for {1000} mseconds
    Then I get the current taskList Status via TSA Task API
    Then the status code should be {207}
    Then I get the current taskList Status via TSA Task API
    And the response is valid according to the {TaskList_Status_schema.json} REST schema
    And the field {status} has the value {failed}
    And the field {$..tasks..status} has the value {["done","failed","failed"]}

  Scenario: TSA - Get status from Task List with three asynchronous tasks second fails - Negative
    When I set the following request body {{}}
    And I execute the taskList {failTestListAsync} via TSA Task API
    Then the status code should be {200}
    And the response is valid according to the {TaskList_Execute_schema.json} REST schema
    And I wait for {1000} mseconds
    Then I get the current taskList Status via TSA Task API
    Then the status code should be {207}
    Then I get the current taskList Status via TSA Task API
    And the response is valid according to the {TaskList_Status_schema.json} REST schema
    And the field {status} has the value {failed}
    And the field {$..tasks..status} has one of the values
      | ["done","failed","done"] | ["failed","done","done"] | ["done","done","failed"] |
