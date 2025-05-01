#Copyright (c) 2023 Vereign AG [https://www.vereign.com]

@rest @all @tsa @task
Feature: API -TSA - Task - v1/taskList POST
  As user
  I want to execute the list of tasks
  So I am able to bring the consistency to running multiple asynchronous tasks

  Background:
    Given we are testing the TSA Task Api

  Scenario: TSA - Executing Task List with two synchronous tasks - Positive
    When I load the REST request {Policy.json} with profile {did_key}
    And I execute the taskList {testList} via TSA Task API
    Then the status code should be {200}
    And the response is valid according to the {TaskList_Execute_schema.json} REST schema
