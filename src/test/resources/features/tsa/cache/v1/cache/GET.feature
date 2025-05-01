#Copyright (c) 2023 Vereign AG [https://www.vereign.com]

@rest @all @tsa @cache
Feature: API -TSA - Cache - v1/cache GET
  As policy administrator
  I want to have distributed cache feature provided
  So I am able to use cache functionality in my custom policies

  Acceptance criteria:
  - The plugin for rego language to get/set values is ready to use
  - The working example how to use the plugin
  - Green test based on example committed to the system

  Background:
    Given we are testing the TSA Cache Api

  Scenario: TSA - Working with Cache - Positive
    When I load the REST request {Cache.json} with profile {successful_set}
    And I load value {test} into current request HEADER {x-cache-key}
    And I send the Cache POST request via TSA Cache API
    And the status code should be {201}
    Then I clear the request body
    And I send the Cache GET request via TSA Cache API
    Then the status code should be {200}
    And the field {msg} has the value {successful setting the cache}

  @phase2 @IDM.TSA.E1.00020
  Scenario: TSA - Get merged Content Access - Positive
    # Set the first cache record
    When I load the REST request {Cache.json} with profile {content_access_01}
    And I load value {did:web:example.com} into current request HEADER {x-cache-key}
    And I load value {Login} into current request HEADER {x-cache-namespace}
    And I load value {01} into current request HEADER {x-cache-scope}
    And I load value {10} into current request HEADER {x-cache-ttl}
    And I send the Cache POST request via TSA Cache API
    And the status code should be {201}
    Then I clear the request body
    And I clear ALL headers
    # Set the second cache record
    And I load the REST request {Cache.json} with profile {content_access_02}
    And I load value {did:web:example.com} into current request HEADER {x-cache-key}
    And I load value {Login} into current request HEADER {x-cache-namespace}
    And I load value {02} into current request HEADER {x-cache-scope}
    And I load value {10} into current request HEADER {x-cache-ttl}
    And I send the Cache POST request via TSA Cache API
    And the status code should be {201}
    Then I clear the request body
    And I clear ALL headers
    # Set the third cache record
    And I load the REST request {Cache.json} with profile {content_access_03}
    And I load value {did:web:example.com} into current request HEADER {x-cache-key}
    And I load value {Login} into current request HEADER {x-cache-namespace}
    And I load value {03} into current request HEADER {x-cache-scope}
    And I load value {10} into current request HEADER {x-cache-ttl}
    And I send the Cache POST request via TSA Cache API
    And the status code should be {201}
    Then I clear the request body
    And I clear ALL headers
    # Get the merged cache result
    And I load value {merge} into current request HEADER {x-cache-flatten-strategy}
    And I load value {did:web:example.com} into current request HEADER {x-cache-key}
    And I load value {Login} into current request HEADER {x-cache-namespace}
    And I load value {01,02,03} into current request HEADER {x-cache-scope}
    And I send the Cache GET request via TSA Cache API
    Then the status code should be {200}
    And the field {data_1} has the value {data value 01}
    And the field {data_2} has the value {data value 02}
    And the field {data_3} has the value {data value 03}
    And the field {key_1} has the value {value 01}
    And the field {key_2} has the value {value 02}
    And the field {key_3} has the value {value 03}

  @phase2 @IDM.TSA.E1.00020
  Scenario: TSA - Get the first cache from merged - Content Access - Positive
    # Set the first cache record
    When I load the REST request {Cache.json} with profile {content_access_01}
    And I load value {did:web:example.com} into current request HEADER {x-cache-key}
    And I load value {Login} into current request HEADER {x-cache-namespace}
    And I load value {01} into current request HEADER {x-cache-scope}
    And I load value {10} into current request HEADER {x-cache-ttl}
    And I send the Cache POST request via TSA Cache API
    And the status code should be {201}
    Then I clear the request body
    And I clear ALL headers
    # Set the second cache record
    And I load the REST request {Cache.json} with profile {content_access_02}
    And I load value {did:web:example.com} into current request HEADER {x-cache-key}
    And I load value {Login} into current request HEADER {x-cache-namespace}
    And I load value {02} into current request HEADER {x-cache-scope}
    And I load value {10} into current request HEADER {x-cache-ttl}
    And I send the Cache POST request via TSA Cache API
    And the status code should be {201}
    Then I clear the request body
    And I clear ALL headers
    # Get the merged cache result and show just the first one
    And I load value {first} into current request HEADER {x-cache-flatten-strategy}
    And I load value {did:web:example.com} into current request HEADER {x-cache-key}
    And I load value {Login} into current request HEADER {x-cache-namespace}
    And I load value {01,02} into current request HEADER {x-cache-scope}
    And I send the Cache GET request via TSA Cache API
    Then the status code should be {200}
    And the field {data} has the value {data value 01}
    And the field {key} has the value {value 01}

  @phase2 @IDM.TSA.E1.00020
  Scenario: TSA - Get the last cache from merged - Content Access - Positive
    # Set the first cache record
    When I load the REST request {Cache.json} with profile {content_access_01}
    And I load value {did:web:example.com} into current request HEADER {x-cache-key}
    And I load value {Login} into current request HEADER {x-cache-namespace}
    And I load value {01} into current request HEADER {x-cache-scope}
    And I load value {10} into current request HEADER {x-cache-ttl}
    And I send the Cache POST request via TSA Cache API
    And the status code should be {201}
    Then I clear the request body
    And I clear ALL headers
    # Set the second cache record
    And I load the REST request {Cache.json} with profile {content_access_02}
    And I load value {did:web:example.com} into current request HEADER {x-cache-key}
    And I load value {Login} into current request HEADER {x-cache-namespace}
    And I load value {02} into current request HEADER {x-cache-scope}
    And I load value {10} into current request HEADER {x-cache-ttl}
    And I send the Cache POST request via TSA Cache API
    And the status code should be {201}
    Then I clear the request body
    And I clear ALL headers
    # Get the merged cache result and show just the last one
    And I load value {last} into current request HEADER {x-cache-flatten-strategy}
    And I load value {did:web:example.com} into current request HEADER {x-cache-key}
    And I load value {Login} into current request HEADER {x-cache-namespace}
    And I load value {01,02} into current request HEADER {x-cache-scope}
    And I send the Cache GET request via TSA Cache API
    Then the status code should be {200}
    And the field {data} has the value {data value 02}
    And the field {key} has the value {value 02}

  @phase2 @IDM.TSA.E1.00020
  Scenario: TSA - Get existing and non existing cache - Content Access - Positive
    # Set the cache record
    When I load the REST request {Cache.json} with profile {content_access_01}
    And I load value {did:web:example.com} into current request HEADER {x-cache-key}
    And I load value {Login} into current request HEADER {x-cache-namespace}
    And I load value {01} into current request HEADER {x-cache-scope}
    And I load value {10} into current request HEADER {x-cache-ttl}
    And I send the Cache POST request via TSA Cache API
    And the status code should be {201}
    Then I clear the request body
    And I clear ALL headers
    # Get the merged cache result
    When I load value {merge} into current request HEADER {x-cache-flatten-strategy}
    And I load value {did:web:example.com} into current request HEADER {x-cache-key}
    And I load value {Login} into current request HEADER {x-cache-namespace}
    And I load value {01, nonExisting} into current request HEADER {x-cache-scope}
    And I send the Cache GET request via TSA Cache API
    Then the status code should be {200}
    And the field {data} has the value {data value 01}
    And the field {key} has the value {value 01}

  @negative
  Scenario: TSA - Setting 10 seconds time to live and verifying it expires - Negative
    When I load the REST request {Cache.json} with profile {successful_set}
    And I load value {test} into current request HEADER {x-cache-key}
    And I load value {10} into current request HEADER {x-cache-ttl}
    And I send the Cache POST request via TSA Cache API
    And the status code should be {201}
    Then I clear the request body
    And I send the Cache GET request via TSA Cache API
    Then the status code should be {200}
    When I wait for {11000} mseconds
    And I send the Cache GET request via TSA Cache API
    Then the status code should be {404}

  @negative
  Scenario: TSA - Access non existing Cache - Negative
    Given I load value {NEGATIVE} into current request HEADER {x-cache-key}
    And I send the Cache GET request via TSA Cache API
    Then the status code should be {404}
    And the response is valid according to the {Cache_negative_schema.json} REST schema
    And the field {message} has the value {key not found in cache}

  @negative
  Scenario: TSA - Access Cache without header x-cache-key - Negative
    Given I send the Cache GET request via TSA Cache API
    Then the status code should be {400}
    And the response is valid according to the {Cache_negative_schema.json} REST schema
    And the field {message} has the value {"key" is missing from header}

  Scenario: TSA - Executing Task with DID resolver and Evaluate the Cache - Positive
    Given we are testing the TSA Task Api
    When I load the REST request {Policy.json} with profile {did_key}
    Then I execute the Task {didResolve} via TSA Task API
    Then the status code should be {200}
    And I wait for {2000} mseconds
    Given we are testing the TSA Cache Api
    And I get the value of {taskID} from the last response and store it in the DataContainer with key {taskID}
    Then I load object with key {taskID} from DataContainer into currentRequest HEADER {x-cache-key}
    When I send the Cache GET request via TSA Cache API
    Then the status code should be {200}
    And the response is valid according to the {Task_ExecuteDID_schema.json} REST schema
    And the field {data.didDocument.id} has the value {did:key:z6Mkfriq1MqLBoPWecGoDLjguo1sB9brj6wT3qZ5BxkKpuP6}

  Scenario: TSA - Check the Cache after importing data to Infohub - Positive
    Given we are testing the TSA Infohub Api
    When I load the REST request {Infohub.json} with profile {successful_import_ID}
    And I import data via TSA Infohub API
    Then the status code should be {200}
    ## Checking the cache service with Import IDs
    Given we are testing the TSA Cache Api
    Then I load element {0} from Info SessionContainer into currentRequest HEADER {x-cache-key}
    When I send the Cache GET request via TSA Cache API
    And the field {allow} has the value {true}
    And the field {id} has the value {did:web:vault.vereign.com:tsa:policy:policy:policies:example:returnDID:1.0:evaluation}
    Then I load element {1} from Info SessionContainer into currentRequest HEADER {x-cache-key}
    When I send the Cache GET request via TSA Cache API
    And the field {allow} has the value {true}
    And the field {id} has the value {did:web:vault.vereign.com:tsa:policy:policy:policies:example:returnDID:1.0:evaluation}
