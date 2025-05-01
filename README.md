# Testing framework

1. [Introduction](#introduction)
1. [Setup](#setup)
1. [Description](#description)


# Introduction

This repository holds the test automation framework based on Java and used for testing TSA. Some of the tests require the environment on which they will be run against to be deployed with database and this database to have specific structure mentioned below on [MongoDB](###mongodb)

# Setup

### Prerequisites

- Install Java version > 17

- Install gradle version > 7.4.1 - https://gradle.org/install/

- Favorite IDE (I recommend IntelliJ IDEA - https://www.jetbrains.com/idea/)

- If using IntelliJ - Install Cucumber for Java & Gherkin addons

### Execute Tests

Example how to run the tests using Itellij IDEA:
- meet all the requirements in the [Prerequisites](#prerequisites)
- create new Gradle [Run Configuraiton](https://www.jetbrains.com/help/idea/run-debug-configuration.html) useing the run options from the example below
```gradle
regressionSuite -PbaseUrl={BASE_URL} -Dcucumber.tags="@{TAG}, not @wip" -Dcourgette.threads=1 -Dcourgette.runLevel=Scenario -Dcourgette.rerunFailedScenarios=false -Dcourgette.rerunAttempts=1
```
_{BASE_URL} - should be replaced with the path where the services are deployed:  
example: http://localhost_  
_{TAG} - Enter which tests to be execute. Valid options are - 'tsa' 'cache', 'infohub', 'policy', 'signer', 'task', 'negative'._

## Specifications for different database implementations
### MongoDB
The database should have specific structure and imported actual data.
The structure and the data should follow the examples in **test_data** folder.
Part of the data is also available in the Policies repository.  
_-The ID fields can be ignored, they are usually created automatically by the database._  
_-The JSON extensions of the files are just for formatting purposes._

### Memory Storage
If the depoloyment [instructions](https://gitlab.eclipse.org/eclipse/xfsc/tsa/policy/-/blob/main/doc/memory-storage.md)
are followed correctly and the linked repository in `POLICY_REPOSITORY_CLONE_URL`
environment variable contains all policies from **test_data** folder, all **policy** tests should pass.
There will be failing tests in the other services due to the missing data needed for
infohub and tasks services.

# Manual execution
To simplify manual tests execution we have a postman collection with the list of supported endpoints, you can find [here](postman/TSA.postman_collection.json)

## License
<hr/>
