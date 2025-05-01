#Copyright (c) 2023 Vereign AG [https://www.vereign.com]

@rest @all @tsa @policy @phase2 @IDM.TSA.E1.00007 @IDM.TSA.E1.00008 @wip
Feature: API - TSA -Policy - {repository}/{group}/{name}/{version}/notifychange POST
  As user
  I want to be able to subscribe for events for the Policy service
  and follow the policy notifications
  So I can see when a policy is changed and make adaptations if needed

  Background:
    Given we are testing the TSA Policy Api

  Scenario: TSA - Get notifications from policy notifier - Positive
    # If using natscli and run services locally
    # nats --server localhost:4222 sub policy_notifier
    When I subscribe on URL {nats.ocm-ui:4222} for subject {policy_notifier}
    And I lock the Policy group {example} name {test} version {1.0}
    Then the status code should be {200}
    When I check the received messages on the subscribed subject
    Then I should see a message with field {name} which has value {test}
    And The same message should have field {type} which has value {update_policy}
    And the response is valid according to the {update_policy_message_schema.json} REST schema
    Then I unlock the policy group {example} name {test} version {1.0}

  Scenario: TSA - Get notifications from Webhook for policy change - Positive
    When I load the REST request {Webhook.json} with profile {successful_address}
    And I send the request to the notify change
    Then the status code should be {200}
    And I lock the Policy group {example} name {test} version {1.0}
    Then the status code should be {200}
    Then I should see a message with field {repository} which has value {policies}
    Then I should see a message with field {name} which has value {locked}
    Then I should see a message with field {version} which has value {1.0}
    Then I should see a message with field {group} which has value {example}

  @negative
  Scenario: TSA - Subscribe for second time to the Webhook for policy change - Negative
    When I load the REST request {Webhook.json} with profile {successful_address}
    And I send the request to the notify change with group {example} name {test} version {1.0} via TSA Policy API
    Then the status code should be {404}
    Then the field {message} has the value {subscriber already exists}

  @negative
  Scenario: TSA - Get notifications from Webhook for policy change with missing url - Negative
    When I load the REST request {Webhook.json} with profile {missing_url}
    And I send the request to the notify change with group {example} name {test} version {1.0} via TSA Policy API
    Then the status code should be {400}
    Then the field {message} has the value {"webhook_url" is missing from body}

  @negative
  Scenario: TSA - Get notifications from Webhook for policy change with missing subscriber - Negative
    When I load the REST request {Webhook.json} with profile {missing_subscriber}
    And I send the request to the notify change with group {example} name {test} version {1.0} via TSA Policy API
    Then the status code should be {400}
    Then the field {message} has the value {"subscriber" is missing from body}

  @negative
  Scenario: TSA - Get notifications from Webhook for non-existing policy - Negative
    When I load the REST request {Webhook.json} with profile {successful_address}
    And I send the request to the notify change with group {example} name {non_existing} version {1.0} via TSA Policy API
    Then the status code should be {409}
    Then the field {message} has the value {mongo: no documents in result}
