// Copyright (c) 2023 Vereign AG [https://www.vereign.com]

package api.test.rest.tsa.policy;

import api.test.core.BaseStepDefinitions;
import api.test.rest.RestGeneralStepDefinitions;
import api.test.rest.RestSessionContainer;
import core.*;
import cucumber.api.java.en.And;
import cucumber.api.java.en.When;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class PolicyStepDefinitions extends BaseStepDefinitions {

  private static final Logger logger =
      LogManager.getLogger(RestGeneralStepDefinitions.class.getSimpleName());
  RestSessionContainer restSessionContainer;
  Request currentRequest;

  public PolicyStepDefinitions(
      RestSessionContainer restSessionContainer,
      Request currentRequest,
      DataContainer dataContainer) {
    super(dataContainer);
    this.restSessionContainer = restSessionContainer;
    this.currentRequest = currentRequest;
  }

  @And(
      "^I execute the Policy group \\{(.*)\\} name \\{(.*)\\} version \\{(.*)\\} via TSA Policy API$")
  public void iExecuteThePolicyGroupNameVersionTSAPolicyAPI(
      String group, String name, String version) throws Throwable {
    currentRequest.setPath(
        "/policy/policies/" + group + "/" + name + "/" + version + "/evaluation");
    Response response = RestClient.post(currentRequest);
    addRequest(currentRequest);
    addResponse(response);
  }

  @When("^I lock the Policy group \\{(.*)\\} name \\{(.*)\\} version \\{(.*)\\}$")
  public void iLockThePolicyGroupNameVersion(String group, String name, String version)
      throws Throwable {
    currentRequest.setPath("/policy/policies/" + group + "/" + name + "/" + version + "/lock");
    Response response = RestClient.post(currentRequest);
    addRequest(currentRequest);
    addResponse(response);
  }

  @When("^I unlock the policy group \\{(.*)\\} name \\{(.*)\\} version \\{(.*)\\}$")
  public void iUnlockThePolicy(String group, String name, String version) throws Throwable {
    currentRequest.setPath("/policy/policies/" + group + "/" + name + "/" + version + "/lock");
    Response response = RestClient.delete(currentRequest);
    addRequest(currentRequest);
    addResponse(response);
  }

  @When("I request all policies via TSA Policy API")
  public void iRequestAllPoliciesViaTSAPolicyAPI() {
    currentRequest.setPath("/v1/policies");
    Response response = RestClient.get(currentRequest);
    addRequest(currentRequest);
    addResponse(response);
  }

  @When(
      "^I request the Policy Admin API with parameters locked \\{(.*)\\}, policy name \\{(.*)\\}, rego \\{(.*)\\}, data \\{(.*)\\}, dataConfig\\{(.*)\\}$")
  public void iRequestThePolicyAdminAPIWithParametersLockedRegoDataDataConfig(
      String locked, String policyName, String rego, String data, String dataConfig) {
    currentRequest.setPath(
        "/v1/policies?locked="
            + locked
            + "&policyName="
            + policyName
            + "&rego="
            + rego
            + "&data="
            + data
            + "&dataConfig="
            + dataConfig);
    Response response = RestClient.get(currentRequest);
    addRequest(currentRequest);
    addResponse(response);
  }

  @When(
      "^I request the export signing key information for Policy group \\{(.*)\\} name \\{(.*)\\} version \\{(.*)\\}$")
  public void iRequestTheExportSigningKeyInformationForPolicy(
      String group, String name, String version) throws Throwable {
    currentRequest.setPath("/policy/policies/" + group + "/" + name + "/" + version + "/key");
    Response response = RestClient.get(currentRequest);
    addRequest(currentRequest);
    addResponse(response);
  }

  @When("^I Export Policy group \\{(.*)\\} name \\{(.*)\\} version \\{(.*)\\}$")
  public void iExportPolicyGroupExampleNameExamplePolicyVersion(
      String group, String name, String version) {
    currentRequest.setPath("/policy/policies/" + group + "/" + name + "/" + version + "/export");
    Response response = RestClient.get(currentRequest);
    addRequest(currentRequest);
    addResponse(response);
  }

  @When("^I validate the output of policy using (GET|POST) with group \\{(.*)\\} name \\{(.*)\\} version \\{(.*)\\} by JSON schema$")
  public void iValidateTheOutputOfPolicyWithGroupExampleNameTestVersionByJSONSchema(String method, String group, String name, String version) {
    currentRequest.setPath("/policy/policies/" + group + "/" + name + "/" + version + "/validation");

    addRequest(currentRequest);

    if (method.equals("GET")) {
      Response response = RestClient.get(currentRequest);
      addResponse(response);
    } else if (method.equals("POST")) {
      Response response = RestClient.post(currentRequest);
      addResponse(response);
    }
  }
}
