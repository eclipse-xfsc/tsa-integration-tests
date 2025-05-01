package api.test.rest.tsa.signer;

import api.test.core.BaseStepDefinitions;
import api.test.rest.RestGeneralStepDefinitions;
import api.test.rest.RestSessionContainer;
import core.*;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.When;
import exceptions.RAFException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class SignerStepDefinitions  extends BaseStepDefinitions {
    private static final Logger logger = LogManager.getLogger(RestGeneralStepDefinitions.class.getSimpleName());
    RestSessionContainer restSessionContainer;
    Request currentRequest;
    private String body;

    public SignerStepDefinitions(RestSessionContainer restSessionContainer, Request currentRequest, DataContainer dataContainer) {
        super(dataContainer);
        this.restSessionContainer = restSessionContainer;
        this.currentRequest = currentRequest;
    }

    @When("I get all key namespaces via TSA Signer API")
    public void iGetAllKeyNamespacesViaTSASignerAPI() {
        currentRequest.setPath("/v1/namespaces");
        Response response = RestClient.get(currentRequest);
        addRequest(currentRequest);
        addResponse(response);
    }

    @When("^I get all keys from namespace \\{(.*)\\} via TSA Signer API$")
    public void iGetAllKeysFromNamespaceViaTSASignerAPI(String namespace) {
        currentRequest.setPath("/v1/namespaces/" + namespace + "/keys");
        Response response = RestClient.get(currentRequest);
        addRequest(currentRequest);
        addResponse(response);
    }

    @When("^I get key \\{(.*)\\} from namespace \\{(.*)\\} in JWK format via TSA Signer API$")
    public void iGetKeyFromNamespaceInJWKformatViaTSASignerAPI(String key, String namespace) {
        currentRequest.setPath("/v1/jwk/" + namespace + "/" + key);
        Response response = RestClient.get(currentRequest);
        addRequest(currentRequest);
        addResponse(response);
    }

    @When("^I get all keys from namespace \\{(.*)\\} using issuer \\{(.*)\\} via TSA Signer API$")
    public void iGetAllKeysViaTSASignerAPI(String namespace, String did) {
        currentRequest.setPath("/v1/verification-methods/" + namespace + "/" + did);
        Response response = RestClient.get(currentRequest);
        addRequest(currentRequest);
        addResponse(response);
    }

    @When("^I get key \\{(.*)\\} from namespace \\{(.*)\\} using issuer \\{(.*)\\} via TSA Signer API$")
    public void iGetKeyFromNamespaceUsingIssuerViaTSASignerAPI(String key, String namespace, String did) {
        currentRequest.setPath("/v1/verification-methods/" + namespace + "/" + key + "/" + did);
        Response response = RestClient.get(currentRequest);
        addRequest(currentRequest);
        addResponse(response);
    }


    @When("I create presentation from JSON via TSA Signer API")
    public void iCreatePresentationFromJSONViaTSASignerAPI() {
        currentRequest.setPath("/v1/presentation");
        Response response = RestClient.post(currentRequest);
        addRequest(currentRequest);
        addResponse(response);
    }

    @When("I create presentation proof via TSA Signer API")
    public void iCreatePresentationProofViaTSASignerAPI() {
        currentRequest.setPath("/v1/presentation/proof");
        Response response = RestClient.post(currentRequest);
        addRequest(currentRequest);
        addResponse(response);
    }

    @When("I create credential proof via TSA Signer API")
    public void iCreateCredentialProofViaTSASignerAPI() {
        currentRequest.setPath("/v1/credential/proof");
        Response response = RestClient.post(currentRequest);
        addRequest(currentRequest);
        addResponse(response);
    }

    @When("I verify presentation proof via TSA Signer API")
    public void iVerifyPresentationProofViaTSASignerAPI() {
        currentRequest.setPath("/v1/presentation/verify");
        Response response = RestClient.post(currentRequest);
        addRequest(currentRequest);
        addResponse(response);
    }

    @When("I verify credential proof via TSA Signer API")
    public void iVerifyCredentialProofViaTSASignerAPI() {
        currentRequest.setPath("/v1/credential/verify");
        Response response = RestClient.post(currentRequest);
        addRequest(currentRequest);
        addResponse(response);
    }

    @Given("^I load \\{(.*)\\} request for environment \\{(.*)\\} via TSA$")
    public void I_load__request_for_environment_via_TSA(String jsonName, String profileName) throws Throwable {
        logger.info("Loading REST json into current request body. Json file= [{}] , profile= [{}]", jsonName, profileName);
        String fullProfileName = profileName + "_" + System.getProperty("baseUrl").replace("https://", "").replace(".vereign.com/tsa", "");
        String loadedProfileData = JsonUtils.getProfileFromJson("/REST/json/" + jsonName, fullProfileName);
        if (!loadedProfileData.equals("null")) {
            currentRequest.setBody(loadedProfileData);
            logger.info("SUCCESS - profile loaded.");
        } else {
            throw new RAFException(String.format("Profile loading FAILED. Value is \"null\". File= [%s] , profile= [%s]",
                    jsonName,
                    profileName)
                    , RestGeneralStepDefinitions.class);
        }
    }
}
