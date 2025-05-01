package api.test.rest.tsa.infohub;

import api.test.core.BaseStepDefinitions;
import api.test.rest.RestGeneralStepDefinitions;
import api.test.rest.RestSessionContainer;
import com.jayway.jsonpath.JsonPath;
import core.DataContainer;
import core.Request;
import core.Response;
import core.RestClient;
import cucumber.api.java.en.And;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;

public class InfohubStepDefinitions extends BaseStepDefinitions {
    private static final Logger logger = LogManager.getLogger(RestGeneralStepDefinitions.class.getSimpleName());
    RestSessionContainer restSessionContainer;
    Request currentRequest;

    public InfohubStepDefinitions(RestSessionContainer restSessionContainer, Request currentRequest, DataContainer dataContainer) {
        super(dataContainer);
        this.restSessionContainer = restSessionContainer;
        this.currentRequest = currentRequest;
    }

    @And("^I export the \\{(.*)\\} via TSA Infohub API$")
    public void iExportTheData(String configuration) {
        currentRequest.setPath("/v1/export/" + configuration);

        Response response = RestClient.get(currentRequest);
        addRequest(currentRequest);
        addResponse(response);
    }

    @And("I import data via TSA Infohub API")
    public void iImportData() {
        currentRequest.setPath("/v1/import");

        Response response = RestClient.post(currentRequest);
        addRequest(currentRequest);
        addResponse(response);

        if (getLastResponse().getStatusCode() == 200) {
            String responseBody = getLastResponse().getBody();

            List<String> importResult = JsonPath.read(responseBody, "$.importIds");
            restSessionContainer.setImportIDs(importResult);
        }
    }
}
