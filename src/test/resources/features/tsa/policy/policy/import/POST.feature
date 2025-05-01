#Copyright (c) 2023 Vereign AG [https://www.vereign.com]

@rest @all @tsa @policy @phase2 @IDM.TSA.E1.00005 @IDM.TSA.E1.00006 @IDM.TSA.E1.00056

Feature: API - TSA - policy - /import GET
  As user
  I want to be able to export signed policy
  then I want to be able to check the signature and if the policy is not modified to import it
  So I can use it

  Background:
    Given we are testing the TSA Policy Api

  @wip
  Scenario: TSA - Import specific policy - Positive
    When I Export Policy group {example} name {examplePolicy} version {1.4}
    When I Import {policies_example_examplePolicy_1.4.zip} in to Policy
    Then the status code should be {200}
    And I execute the Policy group {example} name {examplePolicy} version {1.4} via TSA Policy API
    Then the status code should be {200}

  @negative @wip
  Scenario Outline: TSA - Import modified policy with modified file <modifiedFile> - Negative
    When I Export Policy group {example} name {examplePolicy} version {1.4}
    Then I extract the content
    And modify file <modifiedFile>
    And I archive back all files from the bundle
    When I Import {policies_example_modifiedPolicy_1.4.zip} in to Policy
    Then the status code should be {403}
    And the field {message} contains the value {failed to verify bundle}

    Examples:
      | modifiedFile       |
      | data-config.json   |
      | data.json          |
      | export-config.json |
      | export-config.json |
      | export-config.json |

  @IDM.TSA.E1.00011 @wip
  Scenario: TSA - Defining export configuration policy and configure Import - Positive
    When I define export configuration for Policy group {example} name {exmaple_policy} version {1.0} via TSA Policy API
    And I changes the policy {example_policy} in the repository {synchronised_repository} to {allow} be {true}
    And I commit the changes to {main} on {synchronised_repository}
    Then I execute the Policy group {example} name {exmaple_policy} version {1.0} via TSA Policy API
    And the status code should be {200}
    And the field {allow} has the value {true}
    # Revert bach the changes
    Then I changes the policy {example_policy} in the repository {synchronised_repository} to {allow} be {false}
