#Copyright (c) 2023 Vereign AG [https://www.vereign.com]

@rest @all @tsa @policy @phase2 @IDM.TSA.E1.00012
Feature: API - TSA - policy - {repository}/{group}/{name}/{version}/policy GET
  As user
  I want to export all policies from DB
  So I can see what policies are active,
  when they ware changed, what is the REGO code and the data, dataConfig used.

  Background:
    Given we are testing the TSA Policy Api


  Scenario: TSA - Export all policies - Positive
    When I request all policies via TSA Policy API
    Then the status code should be {200}
    And the field {policies[*].repository} contains the value {policies}
    And the field {policies[*].policyName} contains the value {createProofVC}
    And the field {policies[*].policyName} contains the value {returnDID}
    And the field {policies[*].policyName} contains the value {didResolve}
    And the field {policies[*].group} contains the value {example}
    And the field {policies[*].version} contains the value {1.0}
    And the field {policies[*].version} contains the value {2.0}
    And the field {policies[*].locked} contains the value {false}

  Scenario: TSA - Export all locked policies - Positive
    When I request the Policy Admin API with parameters locked {true}, policy name {}, rego {false}, data {false}, dataConfig{false}
    Then the status code should be {200}
    And the field {policies[*].repository} contains the value {policies}
    And the field {policies[*].policyName} contains the value {locked}
    And the field {policies[*].group} contains the value {example}
    And the field {policies[*].version} contains the value {1.0}
    And the field {policies[*].locked} contains the value {true}

  Scenario: TSA - Export specific policy - Positive
    When I request the Policy Admin API with parameters locked {}, policy name {didResolve}, rego {true}, data {}, dataConfig{}
    Then the status code should be {200}
    And the field {policies[*].repository} contains the value {policies}
    And the field {policies[*].policyName} contains the value {didResolve}
    And the field {policies[*].group} contains the value {example}
    And the field {policies[*].version} contains the value {1.0}
    And the field {policies[*].locked} contains the value {false}

  Scenario: TSA - Export all locked policies and include the REGO field - Positive
    When I request the Policy Admin API with parameters locked {true}, policy name {}, rego {true}, data {false}, dataConfig{false}
    Then the status code should be {200}
    And the field {policies[*].repository} contains the value {policies}
    And the field {policies[*].policyName} contains the value {locked}
    And the field {policies[*].group} contains the value {example}
    And the field {policies[*].version} contains the value {1.0}
    And the field {policies[*].rego} contains the value {package example.locked}
    And the field {policies[*].locked} contains the value {true}

  Scenario: TSA - Export all locked policies and include the REGO, data and dataConfig fields - Positive
    When I request the Policy Admin API with parameters locked {true}, policy name {}, rego {true}, data {true}, dataConfig{true}
    Then the status code should be {200}
    And the field {policies[*].repository} contains the value {policies}
    And the field {policies[*].policyName} contains the value {locked}
    And the field {policies[*].group} contains the value {example}
    And the field {policies[*].version} contains the value {1.0}
    And the field {policies[*].rego} contains the value {package example.locked}
    And the field {policies[*].data} contains the value {trustedEmails}
    And the field {policies[*].dataConfig} contains the value {some data for the config}
    And the field {policies[*].locked} contains the value {true}

  Scenario: TSA - Export all policies and include the REGO, data and dataConfig fields - Positive
    When I request the Policy Admin API with parameters locked {false}, policy name {}, rego {true}, data {true}, dataConfig{true}
    Then the status code should be {200}
    And the field {policies[*].repository} contains the value {policies}
    And the field {policies[*].policyName} contains the value {returnDID}
    And the field {policies[*].group} contains the value {example}
    And the field {policies[*].version} contains the value {1.0}
    And the field {policies[*].rego} contains the value {package example.returnDID}
    And the field {policies[*].data} contains the value {trustedEmails}
    And the field {policies[*].locked} contains the value {false}

  @negative
  Scenario: TSA - Export all policies with wrong value for locked parameter - Negative
    When I request the Policy Admin API with parameters locked {notBoolean}, policy name {}, rego {true}, data {true}, dataConfig{true}
    Then the status code should be {400}
    And the field {message} has the value {invalid value "notBoolean" for "locked", must be a boolean}

  @negative
  Scenario: TSA - Export all policies with wrong value for REGO parameter - Negative
    When I request the Policy Admin API with parameters locked {false}, policy name {}, rego {notBoolean}, data {true}, dataConfig{true}
    Then the status code should be {400}
    And the field {message} has the value {invalid value "notBoolean" for "rego", must be a boolean}

  @negative
  Scenario: TSA - Export all policies with wrong value for data parameter - Negative
    When I request the Policy Admin API with parameters locked {false}, policy name {}, rego {true}, data {notBoolean}, dataConfig{true}
    Then the status code should be {400}
    And the field {message} has the value {invalid value "notBoolean" for "data", must be a boolean}

  @negative
  Scenario: TSA - Export all policies with wrong value for dataConfig parameter - Negative
    When I request the Policy Admin API with parameters locked {false}, policy name {}, rego {true}, data {true}, dataConfig{notBoolean}
    Then the status code should be {400}
    And the field {message} has the value {invalid value "notBoolean" for "dataConfig", must be a boolean}

  @negative
  Scenario: TSA - Export specific non existing policy - Positive
    When I request the Policy Admin API with parameters locked {}, policy name {nonExistingPolicy}, rego {true}, data {}, dataConfig{}
    Then the status code should be {200}
    And the field {policies} contains the value {}
