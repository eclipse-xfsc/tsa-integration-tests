#Copyright (c) 2023 Vereign AG [https://www.vereign.com]

@rest @all @tsa @infohub
Feature: API - TSA - Infohub - v1/export POST
  As user
  I want to import data in to Infohub
  So I can export it later

  Background:
    Given we are testing the TSA Infohub Api

  Scenario: TSA - Import data to Infohub - Positive
    When I load the REST request {Infohub.json} with profile {successful_import}
    And I import data via TSA Infohub API
    Then the status code should be {200}
    And the response is valid according to the {Infohub_Import_schema.json} REST schema

  Scenario: TSA - Import data with ID to Infohub - Positive
    When I load the REST request {Infohub.json} with profile {successful_import_ID}
    And I import data via TSA Infohub API
    Then the status code should be {200}
    And the response is valid according to the {Infohub_Import_schema.json} REST schema

  @negative
  Scenario: TSA - Import data with modified ID to Infohub - Negative
    When I load the REST request {Infohub.json} with profile {modified_ID}
    And I import data via TSA Infohub API
    Then the status code should be {400}

  @negative
  Scenario: TSA - Import data with modified ID to Infohub - Negative
    When I load the REST request {Infohub.json} with profile {modified_allow}
    And I import data via TSA Infohub API
    Then the status code should be {400}

  @negative
  Scenario: TSA - Import empty data to Infohub - Negative
    When I set the following request body {{}}
    And I import data via TSA Infohub API
    And the status code should be {400}
