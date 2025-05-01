#Copyright (c) 2023 Vereign AG [https://www.vereign.com]

@rest @all @tsa @cache @phase2 @IDM.TSA.E1.00017
Feature: API -TSA - Cache - v1/external/cache POST
  As policy administrator
  I want to listen for data update events in the distributable cache and when they occur,
  I want the task controller to execute a policy which evaluates whether any further tasks must be created.
  If any task is configured, the tasks MUST be created for execution. The task has the same metadata as the event.

  Background:
    Given we are testing the TSA Cache Api
    And I load the REST request {Cache.json} with profile {default_set}
    And I load value {resultKey} into current request HEADER {x-cache-key}
    And I load value {resultNamespace} into current request HEADER {x-cache-namespace}
    And I load value {resultScope} into current request HEADER {x-cache-scope}
    Then I clean up the Cache and set it to default value

  Scenario: TSA - Cache Event Subscription - Positive
    When I load the REST request {Cache.json} with profile {successful_set}
    And I load value {did:web:did.actor:alice} into current request HEADER {x-cache-key}
    And I load value {Login} into current request HEADER {x-cache-namespace}
    And I load value {Administration} into current request HEADER {x-cache-scope}
    And I send POST request to external cache via TSA Cache API
    And the status code should be {200}
    And I wait for {1000} mseconds
    Then I clear the request body
    And I load value {resultKey} into current request HEADER {x-cache-key}
    And I load value {resultNamespace} into current request HEADER {x-cache-namespace}
    And I load value {resultScope} into current request HEADER {x-cache-scope}
    Then I send the Cache GET request via TSA Cache API
    And the status code should be {200}
    And the field {msg} has the value {successful setting the cache}
