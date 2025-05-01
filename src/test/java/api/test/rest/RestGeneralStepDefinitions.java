//Copyright (c) 2023 Vereign AG [https://www.vereign.com]

package api.test.rest;

import api.test.core.BaseStepDefinitions;
import com.github.fge.jackson.JsonLoader;
import com.google.gson.*;
import com.jayway.jsonpath.Configuration;
import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.Option;
import com.jayway.jsonpath.ReadContext;
import core.*;
import cucumber.api.java.en.And;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import exceptions.RAFException;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.net.UnknownHostException;
import java.security.KeyPair;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.*;
import static utils.RsaGenerator.encrypt;
import static utils.RsaGenerator.generateKeyPair;


public class RestGeneralStepDefinitions extends BaseStepDefinitions {
    private static final Logger logger = LogManager.getLogger(RestGeneralStepDefinitions.class.getSimpleName());
    RestSessionContainer restSessionContainer;
    Request currentRequest;


    public RestGeneralStepDefinitions(RestSessionContainer restSessionContainer, Request currentRequest, DataContainer dataContainer) throws UnknownHostException {
        super(dataContainer);
        this.restSessionContainer = restSessionContainer;
        this.currentRequest = currentRequest;

    }

    @Given("we are testing the TSA Cache Api")
    public void weAreTestingTheTSACacheApi() {
        RestClient.setDefaultEncoding("UTF8");
        RestClient.setBaseURI(JsonUtils.getTSACache());
        RestClient.appendDefaultContentCharsetToContentTypeIfUndefined(false);
        currentRequest.clear();
        currentRequest.getHeaders().put("X-Client-UserAgent", "test framework");
        currentRequest.setContentType("application/json");
    }

    @Given("we are testing the TSA Infohub Api")
    public void weAreTestingTheTSAInfohubApi() {
        RestClient.setDefaultEncoding("UTF8");
        RestClient.setBaseURI(JsonUtils.getTSAInfohub());
        RestClient.appendDefaultContentCharsetToContentTypeIfUndefined(false);
        currentRequest.clear();
        currentRequest.getHeaders().put("X-Client-UserAgent", "test framework");
        currentRequest.setContentType("application/json");
    }

    @Given("^we are testing the TSA Policy Api")
    public void we_are_testing_the_Policy_api() throws Throwable {
        RestClient.setDefaultEncoding("UTF8");
        RestClient.setBaseURI(JsonUtils.getTSAPolicy());
        RestClient.appendDefaultContentCharsetToContentTypeIfUndefined(false);
        currentRequest.clear();
        currentRequest.getHeaders().put("X-Client-UserAgent", "test framework");
        currentRequest.setContentType("application/json");
    }

    @Given("we are testing the TSA Signer Api")
    public void weAreTestingTheTSASignerApi() {
        RestClient.setDefaultEncoding("UTF8");
        RestClient.setBaseURI(JsonUtils.getTSASigner());
        RestClient.appendDefaultContentCharsetToContentTypeIfUndefined(false);
        currentRequest.clear();
        currentRequest.getHeaders().put("X-Client-UserAgent", "test framework");
        currentRequest.setContentType("application/json");
    }

    @Given("we are testing the TSA Task Api")
    public void weAreTestingTheTSATaskApi() {
        RestClient.setDefaultEncoding("UTF8");
        RestClient.setBaseURI(JsonUtils.getTSATask());
        RestClient.appendDefaultContentCharsetToContentTypeIfUndefined(false);
        currentRequest.clear();
        currentRequest.getHeaders().put("X-Client-UserAgent", "test framework");
        currentRequest.setContentType("application/json");
    }

    @Given("^I load the REST request \\{(.*)\\} with profile \\{(.*)\\}$")
    public void I_load_the_REST_request__with_profile_(String jsonName, String profileName) throws Throwable {
        logger.info("Loading REST json into current request body. Json file= [{}] , profile= [{}]", jsonName, profileName);
        String loadedProfileData = JsonUtils.getProfileFromJson("/REST/json/" + jsonName, profileName);
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

    @Then("^the response is valid according to the \\{(.*?)\\} REST schema$")
    public void the_response_is_valid_according_to_the_REST_schema(String schemaName) throws Throwable {
        validateJsonSchema(getLastResponse().getBody(),
                JsonLoader.fromResource("/REST/schemas/" + schemaName).toString());
    }

    @Then("^the response is valid according to the \\{(.*?)\\} REST schema profile \\{(.*?)\\}$")
    public void the_response_is_valid_according_to_the_schema_profile_(String schemaName, String profileName) throws Throwable {
        validateJsonSchema(getLastResponse().getBody(),
                JsonUtils.getProfileFromJson("/REST/schemas/" + schemaName, profileName));
    }

    /**
     * Gets the values of fields pointed by the provided json paths from the last request and response
     * <br/> and asserts they are equal.
     *
     * @param jsonPathResponseField JsonPath as string pointing to the desired field in the Response
     * @param jsonPathRequestField  JsonPath as string pointing to the desired field in the Request
     * @throws Throwable
     */
    @And("^the value of field \\{(.*?)\\} in the last response matches the value of field \\{(.*?)\\} in the last request$")
    public void theValueOfFieldInTheLastResponseMatchesTheValueOfFieldInTheLastRequest(String jsonPathResponseField, String jsonPathRequestField) throws Throwable {

        logger.info("Comparing values of '{}' response and '{}' request fields:", jsonPathResponseField, jsonPathRequestField);

        // set this Option in order to get 'null' returned if node not found in the JSON document instead of exception.
        Configuration conf = Configuration.builder().options(Option.SUPPRESS_EXCEPTIONS).build();

        ReadContext responseContext = JsonPath.using(conf).parse(getLastResponse().getBody());
        ReadContext requestContext = JsonPath.using(conf).parse(getLastRequest().getBody());

        Object actualValue = responseContext.read(jsonPathResponseField);
        Object expectedValue = requestContext.read(jsonPathRequestField);

        assertEquals("value in response doesn't match what was sent in the request!", expectedValue, actualValue);
        logger.debug("'{}' match SUCCESS. request= [{}], response= [{}]", jsonPathRequestField, expectedValue, actualValue);

    }

    /**
     * Setting a random value (Merchant ID format) to a specific key for a specific object that you can locate with JsonPath from the currentRequest
     *
     * @param fieldName the key for which you will insert random value
     * @param object    the json Object that you want to edit
     * @param jsonPath  the jsonPath of the object you want to edit
     * @throws Throwable
     */
    @And("^I set a random value to the key \\{(.*?)\\} with length \\{(.*?)\\} inside object \\{(.*?)\\} with jsonPath \\{(.*?)\\}$")
    public void i_set_a_random_value_to_the_key_with_length_inside_object_with_jsonPath(String fieldName, int length, String object, String jsonPath) throws Throwable {

        String random = (RandomStringUtils.random(length, true, false));

        JsonElement jsonElement = new Gson().fromJson(currentRequest.getBody(), JsonElement.class);
        JsonObject jsonObject = jsonElement.getAsJsonObject();
        String requestBody = currentRequest.getBody();

        Map<String, String> result = JsonPath.read(requestBody, jsonPath);
        result.put(fieldName, random);

        JsonElement newElement = new Gson().fromJson(result.toString(), JsonElement.class);
        jsonObject.add(object, newElement);

        currentRequest.setBody(new Gson().toJson(jsonObject));
    }

    /**
     * Load an already saved parameter from the DataContainer into an array in the current request body with jsonPath
     *
     * @param param    the key of the parameter saved in the DataContainer
     * @param object   the json Object that you want to edit
     * @param jsonPath the jsonPath of the object you want to edit
     * @throws Throwable
     */
    @And("^I load param from DataContainer with key \\{(.*?)\\} inside array \\{(.*?)\\} with key \\{(.*?)\\} and with jsonPath \\{(.*?)\\}$")
    public void I_load_param_from_DataContainer_with_key_inside_array_with_key_and_with_jsonPath(String param, String object, String key, String jsonPath) throws Throwable {
        String dataContainerValue = String.valueOf(getDataContainer().getObject(param));

        JsonElement jsonElement = new Gson().fromJson(currentRequest.getBody(), JsonElement.class);
        JsonObject jsonObject = jsonElement.getAsJsonObject();
        String requestBody = currentRequest.getBody();
        JsonArray jsonArray = new JsonArray();

        HashMap result = JsonPath.read(requestBody, jsonPath);
        result.put(key, dataContainerValue);

        Gson gson = new GsonBuilder().create();
        String readyObject = gson.toJson(result);
        jsonArray.add(readyObject);
        String readyArray = gson.toJson(jsonArray);
        readyArray = readyArray.replace("\\", "");
        StringBuilder array = new StringBuilder(readyArray);
        array.deleteCharAt(1);
        array.deleteCharAt(array.length() - 2);
        readyArray = array.toString();

        JsonElement newElement = new Gson().fromJson(readyArray, JsonElement.class);
        jsonObject.add(object, newElement);

        currentRequest.setBody(new Gson().toJson(jsonObject));
    }

    @Then("^I add a new publicKey header to the currentRequest$")
    public void i_add_a_new_publicKey_header_to_the_currentRequest() throws Throwable {
        KeyPair pair = generateKeyPair();
        String message = "the answer to life the universe and everything";
        String cipherText = encrypt(message, pair.getPublic());
        currentRequest.getHeaders().put("publicKey", cipherText);
    }

    @And("^I load value \\{(.*)\\} into current request HEADER \\{(.*)\\}$")
    public void iLoadValueIntoCurrentRequestHEADER(String value, String headerName) throws Throwable {
        currentRequest.getHeaders().put(headerName, value);
    }
}
