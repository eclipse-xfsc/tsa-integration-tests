package api.test.rest.tsa.cache;

import api.test.core.BaseStepDefinitions;
import api.test.rest.RestGeneralStepDefinitions;
import api.test.rest.RestSessionContainer;
import core.*;
import cucumber.api.java.en.And;
import cucumber.api.java.en.Then;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class CacheStepDefinitions extends BaseStepDefinitions {
    private static final Logger logger = LogManager.getLogger(RestGeneralStepDefinitions.class.getSimpleName());
    RestSessionContainer restSessionContainer;
    Request currentRequest;

    public CacheStepDefinitions(RestSessionContainer restSessionContainer, Request currentRequest, DataContainer dataContainer) {
        super(dataContainer);
        this.restSessionContainer = restSessionContainer;
        this.currentRequest = currentRequest;
    }

    @And("^I send the Cache (POST|GET) request via TSA Cache API$")
    public void iSendTheCachePOSTRequest(String method) {
        currentRequest.setPath("/v1/cache");

        if (method.equals("POST")) {
            Response response = RestClient.post(currentRequest);
            addRequest(currentRequest);
            addResponse(response);
        } else if (method.equals("GET")) {
            Response response = RestClient.get(currentRequest);
            addRequest(currentRequest);
            addResponse(response);
        }
    }

    @And("^I send (POST|GET) request to external cache via TSA Cache API$")
    public void iSendPOSTRequestToExternalCacheViaTSACacheAPI(String method) {
        currentRequest.setPath("/v1/external/cache");

        if (method.equals("POST")) {
            Response response = RestClient.post(currentRequest);
            addRequest(currentRequest);
            addResponse(response);
        } else if (method.equals("GET")) {
            Response response = RestClient.get(currentRequest);
            addRequest(currentRequest);
            addResponse(response);
        }
    }

    @And("I load element \\{(\\d+)\\} from Info SessionContainer into currentRequest HEADER \\{(.*?)\\}$")
    public void load_element_from_Info_SessionContainer_into_currentRequest_Header_(int id, String headerName) throws Throwable {
        currentRequest.getHeaders().put(headerName, restSessionContainer.getImportIDs().get(id));

    }

    @Then("^I clean up the Cache and set it to default value$")
    public void iCleanUpTheCacheAndSetItToDefaultValue() {
        iSendTheCachePOSTRequest("POST");
    }
}
