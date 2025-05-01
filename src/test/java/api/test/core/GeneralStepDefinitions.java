//Copyright (c) 2023 Vereign AG [https://www.vereign.com]

package api.test.core;

import api.test.rest.RestSessionContainer;
import com.google.gson.*;
import com.jayway.jsonpath.*;
import com.jayway.jsonpath.Configuration;
import core.*;
import cucumber.api.Scenario;
import cucumber.api.java.After;
import cucumber.api.java.Before;
import cucumber.api.java.en.*;
import exceptions.RAFException;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Assert;
import utils.RAFRandomize;
import java.io.IOException;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.*;
import java.util.List;

import static org.junit.Assert.*;

public class GeneralStepDefinitions extends BaseStepDefinitions {
    private static final Logger logger = LogManager.getLogger(GeneralStepDefinitions.class.getSimpleName());
    private static String lastRandomizedValue;
    Request currentRequest;
    RestSessionContainer restSessionContainer;
    private Scenario scenario;


    public GeneralStepDefinitions(Request currentRequest, DataContainer dataContainer, RestSessionContainer restSessionContainer) {
        super(dataContainer);
        this.currentRequest = currentRequest;
        this.restSessionContainer = restSessionContainer;
    }

    /**
     * Returns true/false depending on what "result" is contained in the last response.
     * E.g.:
     * <br/> {
     * <br/>      "result": false,
     * <br/>      "error_code": "1011",
     * <br/>      "security_parameters": {}
     * <br/> }
     *
     * @return The value of the "result" node if found inside last response. FALSE otherwise.
     */
    @And("^I get last result$")
    public static boolean getLastResult() throws Throwable {

        boolean bResult;
        String responseBody = getLastResponse().getBody();
        Object result = JsonPath.read(responseBody, "$..result");

        if ((result instanceof List)) {
            List lResult = (List) result;
            int size = lResult.size();
            if (size != 0) {

                if (size > 1) {
                    throw new Throwable("Response has more than 1 'result' nodes: " + size);
                }

                bResult = Boolean.parseBoolean(lResult.get(0).toString());
                return bResult;
            }
        }

        bResult = Boolean.parseBoolean(result.toString());

        return bResult;
    }

    public static String getLastRandomizedValue() {
        System.out.println("Getting lastRandomizedValue = " + lastRandomizedValue);
        return lastRandomizedValue;
    }

    public static void setLastRandomizedValue(String value) {
        lastRandomizedValue = value;
        logger.debug("lastRandomizedValue= {}", lastRandomizedValue);
    }

    @Before
    public void beforeScenario(Scenario scenario) throws Throwable {
        this.scenario = scenario;
        logger.info("-------------------------------------------=================== * ===================-------------------------------------------" +
                "\nScenario START: {}", scenario.getName());
    }

    @After
    public void afterScenario() throws Throwable {
        if (scenario.getStatus().equals("failed")) {

            scenario.write("Printing last request and response:");

            if (getLastRequest() != null)
                scenario.write(getLastRequest().toString());

            if (getLastResponse() != null)
                scenario.write(getLastResponse().toString());

        }
        logger.info("Scenario END. Status: [{}]" +
                "\n-------------------------------------------=================== * ===================-------------------------------------------", scenario.getStatus());
    }


    @Then("^the status code should be \\{(\\d+)\\}$")
    public void the_status_code_should_be(int statusCode) throws Throwable {
        assertEquals(statusCode, getLastResponse().getStatusCode());
    }

    @Then("^the http status code of the last response is not \\{(\\d+)\\}$")
    public void the_status_code_is_not(int unexpectedStatusCode) throws Throwable {
        int actualHttpStatusCode = getLastResponse().getStatusCode();
        assertNotEquals(unexpectedStatusCode, actualHttpStatusCode);
    }

    @Then("^the error message should be \\{(.*)\\}$")
    public void the_error_message_should_be(String arg1) throws Throwable {
        Object jsonValue = JsonUtils.getValueOfKeyFromJson("error_description", getLastResponse().getBody());
        assertEquals(arg1, jsonValue.toString());
    }

    @Given("^I load the request from json \\{(.*)\\}$")
    public void I_load_the_request_from_json(String jsonName) throws Throwable {
        currentRequest.setBody(JsonUtils.loadJson(jsonName));
    }

    /**
     * Modifies the current request by adding the values from the table.
     * If the current request is empty, then sets the values from the table as the current request.
     * <br/> Values are of type JSON <b>string</b>
     *
     * @param table The key value pairs from the feature file
     * @throws Throwable
     */
    @Given("^I set the request fields$")
    public void I_set_the_request_fields(Map<String, String> table) throws Throwable {
        Gson gson = new Gson();
        if (currentRequest.getBody().equals("")) {
            currentRequest.setBody(gson.toJson(table));
        } else {
            JsonElement jsonElement = gson.fromJson(currentRequest.getBody(), JsonElement.class);
            logger.debug("I_set_the_request_fields - currentRequest.getBody()= {}", currentRequest.getBody());
            JsonObject jsonObject = jsonElement.getAsJsonObject();
            for (Map.Entry<String, String> entry : table.entrySet())
                jsonObject.addProperty(entry.getKey(), entry.getValue());
            currentRequest.setBody(gson.toJson(jsonObject));
            logger.debug("I_set_the_request_fields - NEW currentRequest.getBody()= {}", currentRequest.getBody());
        }
    }

    /**
     * Modifies the current request by adding the values from the table.
     * If the current request is empty, then sets the values from the table as the current request.
     *
     * @param table         The key value pairs from the feature file
     * @param primitiveType the primitive type of the JSON property
     * @throws Throwable
     */
    @Given("^I set the request fields with type (boolean|number)$")
    public void I_set_the_request_fields(String primitiveType, Map<String, String> table) throws Throwable {
        Gson gson = new Gson();
        JsonElement jsonElement = gson.fromJson(currentRequest.getBody(), JsonElement.class);
        // only try to load body as JsonObject if it is not null.
        JsonObject jsonObject = new JsonObject();
        if (jsonElement != null) jsonObject = jsonElement.getAsJsonObject();

        logger.debug("I set the request fields with type (boolean|number) - currentRequest.getBody()= {}", currentRequest.getBody());

        for (Map.Entry<String, String> entry : table.entrySet()) {
            JsonPrimitive element;
            if (primitiveType.equals("number")) { // add as Number
                Number entryAsNumber = NumberFormat.getInstance().parse(entry.getValue());
                element = new JsonPrimitive(entryAsNumber);
                jsonObject.add(entry.getKey(), element);
            } else if (primitiveType.equals("boolean")) { // add as Boolean
                Boolean entryAsBoolean = Boolean.parseBoolean(entry.getValue());
                element = new JsonPrimitive(entryAsBoolean);
                jsonObject.add(entry.getKey(), element);
            } else { // add as String
                jsonObject.addProperty(entry.getKey(), entry.getValue());
            }

        }

        currentRequest.setBody(gson.toJson(jsonObject));
        logger.debug("I set the request fields with type (boolean|number) - NEW currentRequest.getBody()= {}", currentRequest.getBody());

    }

    @And("I load object with key \\{(.*?)\\} from DataContainer into currentRequest Body with key \\{(.*?)\\}$")
    public void load_object_with_key_from_DataContainer_into_currentRequest_Body_param(String keyDataContainer,
                                                                                       String keyRequestParam) throws Throwable {
        Map<String, String> fields = new HashMap<>();
        fields.put(keyRequestParam, String.valueOf(getDataContainer().getObject(keyDataContainer)));

        I_set_the_request_fields(fields);

    }

    @And("I load object with key \\{(.*?)\\} from DataContainer into currentRequest HEADER \\{(.*?)\\}$")
    public void load_object_with_key_from_DataContainer_into_currentRequest_Header__(String keyDataContainer, String headerName) throws Throwable {
        currentRequest.getHeaders().put(headerName, (String) getDataContainer().getObject(keyDataContainer));

    }

    @And("I load object with key \\{(.*?)\\} from DataContainer into currentRequest as Request Param with key \\{(.*?)\\}$")
    public void load_object_with_key_from_DataContainer_into_currentRequest_as_RequestParam(String keyDataContainer,
                                                                                            String keyRequestParam) throws Throwable {
        currentRequest.getParams().put(keyRequestParam, getDataContainer().getObject(keyDataContainer));
    }

    /**
     * Removes a List of key/value pair(s) from request Body{}
     *
     * @param cucumberTable List of Keys we want to remove from request body
     *                      e.g. |password|
     *                      |username|
     */
    @Given("^I remove the following Keys from current request Body:$")
    public void i_remove_the_following_keys_from_current_request_Body(List<String> cucumberTable) throws IOException {
        cucumberTable.stream().forEach(field -> {
            try {
                currentRequest.setBody(JsonUtils.removeKeyFromJson(field, currentRequest.getBody()));
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

    }

    /**
     * Removes a List of key/value pair(s) from request Parameters list.
     *
     * @param cucumberTable List of Keys we want to remove from the list of request parameters
     * @throws IOException
     */
    @Given("^I remove the following Parameters from current request:$")
    public void i_remove_the_following_parameters_from_current_request(List<String> cucumberTable) throws IOException {
        cucumberTable.stream().forEach(field -> currentRequest.getParams().remove(field));

    }

    @And("^the response body is empty$")
    public void the_response_body_is_empty() throws Throwable {
        assertTrue(getLastResponse().getBody().equals(""));
    }

    @Then("^the response body contains \\{(.*?)\\}$")
    public void the_response_body_contains(String value) throws Throwable {
        assertTrue(getLastResponse().getBody().contains(value));
    }

    /**
     * Adds the supplied cucumber table to the parameter map used for url encoded requests
     * Clears the map before
     *
     * @param paramsTable the cucumber table
     * @throws Throwable
     */
    @Given("^I set the parameters$")
    public void I_set_the_parameters(Map<String, String> paramsTable) throws Throwable {
        currentRequest.setBody("");
        currentRequest.getParams().putAll(paramsTable);
    }

    /**
     * Adds the supplied cucumber table to the headers map
     *
     * @param headersTable the cucumber table
     * @throws Throwable
     */
    @And("^I set the headers$")
    public void I_set_the_headers(Map<String, String> headersTable) throws Throwable {
        currentRequest.getHeaders().putAll(headersTable);
//        System.out.println(currentRequest.getHeaders());
    }

    /**
     * Clears the headers map
     *
     * @throws Throwable
     */
    @And("^I clear ALL headers$")
    public void I_clear_the_headers() throws Throwable {
        logger.info("Clearing headers of the current request.");
        currentRequest.getHeaders().clear();
        logger.debug("Headers after clearing: {}", currentRequest.getHeaders());
    }

    /**
     * Sets the version used for subsequent requests
     *
     * @param version the version that will be set
     * @throws Throwable
     */
    @And("^I set the version to \\{(.*?)\\}$")
    public void I_set_the_version_to_v_(String version) throws Throwable {
        currentRequest.setVersion(version);
    }

    /**
     * Adds the provided Cucumber table to the url params map that will be used in a subsequent request
     *
     * @param table the table passed from the cucumber feature file
     * @throws Throwable
     */
    @And("^I set the query parameters$")
    public void I_set_the_query_parameters(Map<String, String> table) throws Throwable {
        currentRequest.getQueryParams().putAll(table);
    }

    /**
     * Sets the content type for the subsequent request.
     * The dafault type is application/json.
     *
     * @param contentType content type that will be set
     * @throws Throwable
     */
    @And("^I set content type to \\{(.*?)\\}$")
    public void I_set_content_type_to_(String contentType) throws Throwable {
        currentRequest.setContentType(contentType);
    }

    /**
     * Sets the default parameters, loading them from the Json file provided
     *
     * @param jsonName the name of the JSON file found in /main/resources/
     * @throws Throwable
     */
    @And("^I load the default parameters from \\{(.*?)\\} profile \\{(.*?)\\}$")
    public void the_default_parameters__from_profile(String jsonName, String profileName) throws Throwable {
        currentRequest.getParams().clear();
        currentRequest.getParams().putAll(JsonUtils.loadMapFromResource(jsonName, profileName));
    }

    @And("^I set a new request with parameters")
    public void I_set_a_new_request_with_parameters(Map<String, String> cucumberTable) throws Throwable {
        clearRequest();
        I_set_the_parameters(cucumberTable);
    }

    private void clearRequest() {
        currentRequest.setBody("");
        currentRequest.getParams().clear();
    }

    @And("^the response \\{(.*?)\\} header value is equal to \\{(.*?)\\}$")
    public void theResponseHeaderXvalueIsEqualToY(String headerName, String expectedHeaderValue) throws Throwable {
        assertEquals("Header [" + headerName + "] value doesn't equal expected value: ", expectedHeaderValue, getLastResponse().getHeaders().get(headerName));
    }


    @And("^the response \\{(.*?)\\} header contains the text \\{(.*?)\\}$")
    public void theResponseHeaderXcontainsTextY(String headerName, String expectedText) throws Throwable {
        String headerValue = getLastResponse().getHeaders().get(headerName);
        assertTrue("Header [" + headerName + "] = " + headerValue + " doesn't contain the expected text: [" + expectedText + "]", headerValue.contains(expectedText));
    }

    /**
     * Asserts given Header <b>IS</b> present in the last Response.
     *
     * @param headerName Name of header to check.
     * @throws Throwable
     */
    @And("^the header \\{(.*?)\\} is present in the response$")
    public void the_header_is_present_in_response(String headerName) throws Throwable {
        assertTrue("Header \"" + headerName + "\" is not present in the response! ", (getLastResponse().getHeaders().get(headerName) != null));
    }

    /**
     * Asserts given Header is <b>NOT</b> Present in the last Response.
     *
     * @param headerName Name of header to check.
     * @throws Throwable
     */
    @And("^the header \\{(.*?)\\} is NOT present in the response$")
    public void the_header_is_not_present_in_response(String headerName) throws Throwable {
        assertTrue("Header \"" + headerName + "\" is present in the response! ", (getLastResponse().getHeaders().get(headerName) == null));
    }

    @And("^I delete the headers$")
    public void I_delete_the_headers(List<String> headerList) throws Throwable {
        for (String header : headerList)
            currentRequest.getHeaders().remove(header);
    }

    @And("^I set a random string to the field \\{(.*?)\\}$")
    public void I_set_a_random_string_to_the_field_(String fieldName) throws Throwable {
        String random = RandomStringUtils.random(12, true, true);
        JsonElement jsonElement = new Gson().fromJson(currentRequest.getBody(), JsonElement.class);
        JsonObject jsonObject = jsonElement.getAsJsonObject();
        jsonObject.addProperty(fieldName, random);
        currentRequest.setBody(new Gson().toJson(jsonObject));
    }

    /**
     * Setting random value to a key for a specific object that you can locate with JsonPath from the currentRequest
     * @param fieldName the key for which you will insert random value
     * @param jsonPath the jsonPath of the object you want to edit
     * @throws Throwable
     */
    @And("^I set a random string to the key \\{(.*?)\\} inside object \\{(.*?)\\} with jsonPath \\{(.*?)\\}$")
    public void ISetARandomStringToTheKeyInsideObjectWithJsonPath(String fieldName,String object,String jsonPath) throws Throwable {
        String random = (RandomStringUtils.random(12, true, true) + "@example.com");

        JsonElement jsonElement = new Gson().fromJson(currentRequest.getBody(), JsonElement.class);
        JsonObject jsonObject = jsonElement.getAsJsonObject();
        String requestBody = currentRequest.getBody();

        Map<String, String> result = JsonPath.read(requestBody, jsonPath);
        result.put(fieldName, random);

        JsonElement newElement = new Gson().fromJson(result.toString(), JsonElement.class);
        jsonObject.add(object, newElement);

        currentRequest.setBody(new Gson().toJson(jsonObject));
    }

    @And("^I clear the request body$")
    public void I_clear_the_request_body() throws Throwable {
        currentRequest.setBody("");
    }

    @And("^I clear the request parameters$")
    public void I_clear_the_request_parameters() throws Throwable {
        currentRequest.getParams().clear();
    }

    //    DataContainer methods START:
    @And("I store key \\{(.*?)\\} with value \\{(.*?)\\} in the data container$")
    public void store_key_with_value_in_dataContainer(String key, String value) {

        getDataContainer().addObject(key, value);

    }

    @And("^I store last randomized value in DataContainer with key \\{(.*?)\\}$")
    public void iStoreLastRandomizedValueInDataContainerWithKey(String key) throws Throwable {
        if (getDataContainer() != null) {
            if (lastRandomizedValue != null) {
                getDataContainer().addObject(key, getLastRandomizedValue());
            } else {
                System.out.println("lastRandomizedValue = null : " + getLastRandomizedValue());
                throw new RAFException("lastRandomizedValue is null", GeneralStepDefinitions.class);
            }
        } else {
            System.out.println("---------- DataContainer is NULL -----------");
            throw new RAFException("DataContainer is null", GeneralStepDefinitions.class);
        }
    }

    /**
     * Gets a given value, described by jsonPath, from the last response
     * and stores it DataContainer with the given key.
     *
     * @param jsonPath JsonPath which is used to identify the element/node inside the response. For example: <i>$.error.id</i>
     * @param key      the key which will be used to store the value inside the Data Container.
     * @throws Throwable
     */
    @And("^I get the value of \\{(.*?)\\} from the last response and store it in the DataContainer with key \\{(.*?)\\}$")
    public void iGetTheValueOfFromTheLastResponseAndStoreItInTheDataContainerWithKey(String jsonPath, String key) throws Throwable {

        String responseBody = getLastResponse().getBody();
        Object result = JsonPath.read(responseBody, jsonPath);

        getDataContainer().addObject(key, result);
        System.out.println("Element's value [" + result + "] identified by jsonPath = [" + jsonPath + "], stored " +
                "in DataContainer with key = [" + key + "]");

    }

    @And("^I get the value of \\{(.*?)\\} from the (last|current) Request Body and store it in the DataContainer with key \\{(.*?)\\}$")
    public void iGetTheValueOf__RequestAndStoreItInTheDataContainerWithKey(String jsonPath, String lastORcurrent, String key) throws Throwable {

        String requestBody;
        if (lastORcurrent.equals("last")) {
            requestBody = getLastRequest().getBody();
        } else {
            requestBody = currentRequest.getBody();
        }

        Object result = JsonPath.read(requestBody, jsonPath);

        getDataContainer().addObject(key, result);
        logger.debug("Element's value [{}] in the {} Request Body identified by jsonPath = [{}] was stored " +
                "in DataContainer with key = [{}]", result, lastORcurrent, jsonPath, key);

    }

    @And("^I get the value of the \\{(.*?)\\} HEADER from the (last|current) (request|response) and store it in DataContainer using key \\{(.*?)\\}$")
    public void iGetTheValueOfThe__HeaderFromThe___AndStoreItInDataContainerUsingKey(String header, String lastORcurrent, String requestORresponse, String key) throws Throwable {
        Map<String, String> headers;

        if (requestORresponse.equals("request")) { // request:
            if (lastORcurrent.equals("last")) {
                headers = getLastRequest().getHeaders();
            } else {
                headers = currentRequest.getHeaders();
            }
        } else { // response:
            headers = getLastResponse().getHeaders();
        }

        if (!headers.containsKey(header)) {
            throw new NoSuchFieldError(String.format("[%s] HEADER is not present in the %s %s!", header, lastORcurrent, requestORresponse));
        }

        String result = headers.get(header);
        getDataContainer().addObject(key, result);

        System.out.println("The value " + getDataContainer().getObject(key) + " of the " + header + " header from the " + lastORcurrent + " " + requestORresponse + " was stored in the DataContainer with key '" + key + "'");

        logger.debug("The value [{}] of the [{}] header from the {} {} was stored in the DataContainer with key '{}'",
                getDataContainer().getObject(key), header, lastORcurrent, requestORresponse, key);

    }

    /**
     * Gets the value of object in DataContainer identified by key, converts it into LOWERCASE|UPPERCASE and stores the new value into
     * new object with key <b>[oldKey]_lowercase</b> or <b>[oldKey]_uppercase</b> depending on the chosen option.
     *
     * @param key The key of the original object.
     * @throws Throwable
     */
    @And("^I convert the value of object stored in DataContainer with key \\{(.*?)\\} into (lowercase|uppercase)$")
    public void iConvertTheValueOfObjectInDataContainer__IntoLowercase(String key, String option) throws Throwable {

        String value = (String) getObjectFromDataContainer(key);
        if (option.equals("lowercase")) {
            getDataContainer().addObject(key + "_lowercase", value.toLowerCase());

        } else if (option.equals("uppercase")) {
            getDataContainer().addObject(key + "_uppercase", value.toUpperCase());

        } else {
            logger.info("Unrecognized option '{}'. Skipping action.", option);
            return;
        }

        logger.debug("Value [{}] of object in DataContainer with key [{}] was converted to {}: [{}]", value, key, option, getObjectFromDataContainer(key + "_" + option));
        logger.debug("the new value is stored with key [{}]", key + "_" + option);

    }

    private Object getObjectFromDataContainer(String key) {

        return getDataContainer().getObject(key);
    }

//    DataContainer methods END


    @And("^I send the current request as (POST|GET|PUT|DELETE|HEAD) to endpoint \\{(.*?)\\}$")
    public void I_send_the_current_request_to_endpoint(String method, String endpoint) throws Throwable {
        currentRequest.setPath(endpoint);

        if (method.equals("POST")) {
            Response response = RestClient.post(currentRequest);
            addRequest(currentRequest);
            addResponse(response);
        } else if (method.equals("GET")) {
            Response response = RestClient.get(currentRequest);
            addRequest(currentRequest);
            addResponse(response);
        } else if (method.equals("PUT")) {
            Response response = RestClient.put(currentRequest);
            addRequest(currentRequest);
            addResponse(response);
        } else if (method.equals("DELETE")) {
            Response response = RestClient.delete(currentRequest);
            addRequest(currentRequest);
            addResponse(response);
        } else if (method.equals("HEAD")) {
            Response response = RestClient.head(currentRequest);
            addRequest(currentRequest);
            addResponse(response);
        }
    }

    /**
     * Compares the values of a json array with the supplied cucumber table
     *
     * @param jsonPath the jsonPath for the array
     * @param values   the cucumber table
     * @throws Throwable
     * @see <a href="https://github.com/jayway/JsonPath">https://github.com/jayway/JsonPath</a>
     */
    @And("^the field \\{(.*?)\\} has the values$")
    public void the_field__has_the_values_(String jsonPath, List<String> values) throws Throwable {
        String responseBody = getLastResponse().getBody();
        List<Object> results = JsonPath.read(responseBody, jsonPath);

        assertEquals(values, results);
    }

    /**
     * Compares the values of a json object with the supplied cucumber table (a map)
     *
     * @param jsonPath the jsonPath for the object
     * @param values   the cucumber table with 2 columns
     * @throws Throwable
     * @see <a href="https://github.com/jayway/JsonPath">https://github.com/jayway/JsonPath</a>
     */
    @And("^the field \\{(.*?)\\} has the values from the map$")
    public void the_field__has_the_values_from_the_map_(String jsonPath, Map<String, Object> values) throws Throwable {
        String responseBody = getLastResponse().getBody();
        Map<String, Object> results = JsonPath.read(responseBody, jsonPath);

        assertEquals(values.entrySet(), results.entrySet());
    }

    /**
     * Checks if expected entries are part of the actual map being checked.
     * (actual map contains expected)
     *
     * @param jsonPath        the jsonPath pointing to the actual JSON object containing map (key/value pairs)
     * @param expectedEntries the cucumber table with 2 columns providing the expected entries
     * @throws Throwable
     * @see <a href="https://github.com/jayway/JsonPath">https://github.com/jayway/JsonPath</a>
     */
    @And("^the field \\{(.*?)\\} contains the values from the map:$")
    public void the_field__contains_the_values_from_the_map_(String jsonPath, Map<String, Object> expectedEntries) throws Throwable {
        String responseBody = getLastResponse().getBody();
        Map<String, Object> actualEntries = JsonPath.read(responseBody, jsonPath);

        assertTrue("Actual map doesn't contain expected entries. Actual map=[" + actualEntries + "]. Expected entries=[" + expectedEntries + "]",
                actualEntries.entrySet().containsAll(expectedEntries.entrySet()));
    }

    /**
     * Compare the expectedValues of json Array with the supplied cucumber table (a map)
     *
     * @param jsonPath       the jsonPath for the array. If this value contains the text <b>skip_validation</b> the validation will be skipped
     * @param expectedValues the cucumber table with 2 columns
     * @throws Throwable
     * @see <a href="https://github.com/jayway/JsonPath">https://github.com/jayway/JsonPath</a>
     */
    @Then("^the array \\{(.*?)\\} has the values from the map$")
    public void the_array_has_the_values_from_the_map(String jsonPath, Map<String, Object> expectedValues) throws Throwable {

        if (jsonPath.contains("skip_validation")) {
            logger.info("Special text `skip_validation` found in jsonPath param. Step will be skipped");
            return;
        }

        String responseBody = getLastResponse().getBody();
        ArrayList<Object> results = JsonPath.read(responseBody, jsonPath);

        logger.info("Starting validation if Expected values: {}\nare contained inside the Actual response:{}", expectedValues, results);
        assertTrue("The Array " + results + " doesn't contains the expected values provided: " + expectedValues, results.contains(expectedValues));
        logger.debug("Success - expected values are contained withing the response!");
    }

    /**
     * Compares a single value from a json with the supplied expectedResultAsString
     * Finding the value pointed by the jsonPath and converting it to String first,
     * before compare it with the expectedResultAsString.
     *
     * @param jsonPath               the jsonPath pointing to the actual value to be compared within the body fo the LastResponse
     * @param expectedResultAsString the expectedResultAsString to be compared to
     * @throws Throwable
     * @see <a href="https://github.com/jayway/JsonPath">https://github.com/jayway/JsonPath</a>
     */
    @And("^the field \\{(.*?)\\} contains the value \\{(.*?)\\}$")
    public void the_field_contains_the_value_(String jsonPath, String expectedResultAsString) throws Throwable {

        Configuration conf = Configuration.builder().options(Option.SUPPRESS_EXCEPTIONS).build();
        ReadContext responseContext = JsonPath.using(conf).parse(getLastResponse().getBody());

        Object parsedResult = responseContext.read(jsonPath);
        String actualResultAsString = String.valueOf(parsedResult);

        assertTrue("Asserting that the ActualResult contains the ExpectedResult: ",actualResultAsString.contains(expectedResultAsString));
        logger.debug("Success: the given expected value [{}], matches the actual value [{}] pointed by jsonPath '{}' ", expectedResultAsString, actualResultAsString, jsonPath);
    }


    /**
     * Compares a single value from a json with the supplied expectedResultAsString
     * Finding the value pointed by the jsonPath and converting it to String first,
     * before compare it with the expectedResultAsString.
     *
     * @param jsonPath               the jsonPath pointing to the actual value to be compared within the body fo the LastResponse
     * @param expectedResultAsString the expectedResultAsString to be compared to
     * @throws Throwable
     * @see <a href="https://github.com/jayway/JsonPath">https://github.com/jayway/JsonPath</a>
     */
    @And("^the field \\{(.*?)\\} has the value \\{(.*?)\\}$")
    public static void the_field_has_the_value_(String jsonPath, String expectedResultAsString) throws Throwable {

        Configuration conf = Configuration.builder().options(Option.SUPPRESS_EXCEPTIONS).build();
        ReadContext responseContext = JsonPath.using(conf).parse(getLastResponse().getBody());

        Object parsedResult = responseContext.read(jsonPath);
        String actualResultAsString = String.valueOf(parsedResult);

        assertEquals("Expected doesn't match actual: ", expectedResultAsString, actualResultAsString);
        logger.debug("Success: the given expected value [{}], matches the actual value [{}] pointed by jsonPath '{}' ", expectedResultAsString, actualResultAsString, jsonPath);
    }

    /**
     * Asserts that the expected field is present and its not null
     *
     * @param jsonPath the jsonPath pointing to the tested field
     * @throws Throwable
     * @see <a href="https://github.com/jayway/JsonPath">https://github.com/jayway/JsonPath</a>
     */
    @And("^the field \\{(.*?)\\} is present and not empty$")
    public void the_field_is_present_and_not_empty(String jsonPath) throws Throwable {
        Configuration conf = Configuration.builder().options(Option.SUPPRESS_EXCEPTIONS).build();
        ReadContext responseContext = JsonPath.using(conf).parse(getLastResponse().getBody());

        Object parsedResult = responseContext.read(jsonPath);

        assertNotNull("'Expected' in response is null", parsedResult);
    }

    /**
     * Asserts that the given field is <b>NOT</b> present in the last response JSON body.
     *
     * @param jsonPath the jsonPath pointing to the given field
     * @throws Throwable
     * @see <a href="https://github.com/jayway/JsonPath">https://github.com/jayway/JsonPath</a>
     */
    @And("^the field \\{(.*?)\\} is NOT present in the last response$")
    public void the_field_is_NOT_present(String jsonPath) throws Throwable {
        logger.info("Asserting that node {} is not present in last response JSON body...", jsonPath);

        Configuration conf = Configuration.builder().options(Option.SUPPRESS_EXCEPTIONS).build();
        ReadContext responseContext = JsonPath.using(conf).parse(getLastResponse().getBody());

        assertNull("Field is present when we expect not to be!", responseContext.read(jsonPath));
        logger.info("Success!");
    }

    /**
     * Compares a single value from a json with the supplied expectedResultAsString that its not contained.
     * Finding the value pointed by the jsonPath and converting it to String first,
     * before compare it with the expectedResultAsString.
     *
     * @param jsonPath               the jsonPath pointing to the actual value to be compared within the body fo the LastResponse
     * @param expectedResultAsString the expectedResultAsString to be compared to
     * @throws Throwable
     * @see <a href="https://github.com/jayway/JsonPath">https://github.com/jayway/JsonPath</a>
     */
    @And("^the field \\{(.*?)\\} is not containing the value \\{(.*?)\\}$")
    public void the_field_is_not_containing_the_value_(String jsonPath, String expectedResultAsString) throws Throwable {

        Configuration conf = Configuration.builder().options(Option.SUPPRESS_EXCEPTIONS).build();
        ReadContext responseContext = JsonPath.using(conf).parse(getLastResponse().getBody());

        Object parsedResult = responseContext.read(jsonPath);

        String actualResultAsString = String.valueOf(parsedResult);

        assertTrue("The Response field contains the provided value: ", !actualResultAsString.contains(expectedResultAsString));

    }

    @And("^the field \\{(.*?)\\} is not equal to the value \\{(.*?)\\}$")
    public void the_field_is_not_equal_to_the_value_(String jsonPath, String expectedResultAsString) throws Throwable {

        Configuration conf = Configuration.builder().options(Option.SUPPRESS_EXCEPTIONS).build();
        ReadContext responseContext = JsonPath.using(conf).parse(getLastResponse().getBody());

        Object parsedResult = responseContext.read(jsonPath);

        String actualResultAsString = String.valueOf(parsedResult);

        assertNotEquals("The Response field equals the provided value: ", actualResultAsString, expectedResultAsString);
    }

    @And("^the field \\{(.*?)\\} has the value stored in DataContainer with key \\{(.*?)\\}$")
    public void theFieldHasTheValueStoredInDataContainerWithKey(String jsonPath, String dataContainerKey) throws Throwable {
        the_field_has_the_value_(jsonPath, String.valueOf(getObjectFromDataContainer(dataContainerKey)));
    }

    @And("^the field \\{(.*?)\\} is containing the value stored in DataContainer with key \\{(.*?)\\}$")
    public void theFieldIsContainingTheValueStoredInDataContainerWithKey(String jsonPath, String dataContainerKey) throws Throwable {
        the_field_value_should_contain_the_text(jsonPath, String.valueOf(getObjectFromDataContainer(dataContainerKey)));
    }

    @And("^the field \\{(.*?)\\} is not containing the value stored in DataContainer with key \\{(.*?)\\}$")
    public void theFieldIsntContainingTheValueStoredInDataContainerWithKey(String jsonPath, String dataContainerKey) throws Throwable {
        the_field_is_not_containing_the_value_(jsonPath, String.valueOf(getObjectFromDataContainer(dataContainerKey)));
    }

    @And("^the array \\{(.*?)\\} contains the value stored in DataContainer with key \\{(.*?)\\}$")
    public void theArrayContainsTheValueStoredInDataContainerWithKey(String jsonPath, String dataContainerKey) throws Throwable {
        the_field_value_should_contain_the_text(jsonPath, String.valueOf(getObjectFromDataContainer(dataContainerKey)));
    }


    /**
     * Check if expectedText is CONTAINED within the value of the JSON node located by jsonPath
     * ! doesn't assert they are equal !
     *
     * @param jsonPath     @see <a href="https://github.com/jayway/JsonPath">https://github.com/jayway/JsonPath</a>
     * @param expectedText The text asserted to be contained within the node value.
     * @throws Throwable
     */
    @Then("^the value of field \\{(.*)\\} should contain the text \\{(.*)\\}$")
    public void the_field_value_should_contain_the_text(String jsonPath, String expectedText) throws Throwable {
        logger.debug("jsonPath = [{}]", jsonPath);

        String responseBody = getLastResponse().getBody();
        Object result = JsonPath.read(responseBody, jsonPath);

        if (result == null)
            result = "null";
        else
            result = result.toString();

        logger.debug("result= {} ; expectedText= {}", result, expectedText);
        assertTrue("The expected text: <" + expectedText + "> is not contained within the field [" + jsonPath + "]'s value: <" + result + ">",
                result.toString().contains(expectedText));
    }

    /**
     * Compare if number of items inside an JSON Array element pointed by a jsonPath, match the expected.
     *
     * @param jsonPath                 jsonPath which identifies the array element inside the JSON body of the last response.
     * @param expectedNumberOfElements Expected number of items inside the array.
     * @throws Throwable
     */
    @And("^the field \\{(.*?)\\} contains \\{(\\d+)\\} (?:element|elements)$")
    public void the_field_contains_elements(String jsonPath, int expectedNumberOfElements) throws Throwable {
        String responseBody = getLastResponse().getBody();
        ArrayList<Object> list = JsonPath.read(responseBody, jsonPath);
        int actualNumberOfElements = list.size();
        System.out.println("jsonPath = [" + jsonPath + "], \n" +
                "value = [" + list.toString() + "], \n" +
                "Expected number of elements = [" + expectedNumberOfElements + "] \n" +
                "Actual number of elements = [" + actualNumberOfElements + "]");

        assertEquals("Number of elements doesn't match expected", expectedNumberOfElements, actualNumberOfElements);

    }

    @And("^Verify the response value with the value in the datacontainer$")
    public void Verify_the_response_value_with_the_value_in_the_datacontainer(Map<String, String> cucumberTable) throws Throwable {

        for (String key : cucumberTable.keySet()) {

            the_field_has_the_value_(key, String.valueOf(getObjectFromDataContainer(cucumberTable.get(key))));
        }
    }

    @And("^the Location header URL is OK$")
    public void theLocationHeaderURLIsOK() throws Throwable {

        if (!getLastResponse().getHeaders().containsKey("Location")) {
            throw new NoSuchFieldError("Location HEADER is not present in the last response!");
        }

        String locationHeaderValue = getLastResponse().getHeaders().get("Location");

        assertTrue("Location header values is not 'OK' : [" + locationHeaderValue + "]", locationHeaderValue.substring(locationHeaderValue.length() - 3).contains("ok"));

    }

    /**
     * Request Body
     */
    @And("^I set random value with format \\{(.*?)\\} to field \\{(.*?)\\} inside Request Body$")
    public void setRandomValueFieldInsideRequestBody(String inputString, String key) throws Throwable {
        logger.debug("Setting random value with format: [{}] to field:[{}] in request body(JSON)", inputString, key);

        if (key.equals("")) {// if key is empty string, skip addition
            logger.debug("Empty key. Skipping step...");
        } else {

            String value = RAFRandomize.retainSpecialCharsAndRandomizeAlphanumeric(inputString);
            setLastRandomizedValue(value);

            Map<String, String> map = new HashMap<>();
            map.put(key, value);
            I_set_the_request_fields(map);
        }
    }

    /**
     * Request Body
     */
    @And("^I set random value with UUID format to field \\{(.*?)\\} inside Request Body$")
    public void setRandomValueFieldInsideRequestBody(String key) throws Throwable {
        String randomUuid = RAFRandomize.getUUID();
        logger.debug("Setting random value with format: [{}] to field:[{}] in request body(JSON)",randomUuid , key);

        if (key.equals("")) {// if key is empty string, skip addition
            logger.debug("Empty key. Skipping step...");
        } else {

            setLastRandomizedValue(randomUuid);

            Map<String, String> map = new HashMap<>();
            map.put(key, randomUuid);
            I_set_the_request_fields(map);
        }
    }


    /**
     * Request Param
     */
    @And("^I set random value with format \\{(.*?)\\} to field \\{(.*?)\\} as Request Param$")
    public void setRandomValueFieldAsRequestParam(String inputString, String key) throws Throwable {

        String value = RAFRandomize.retainSpecialCharsAndRandomizeAlphanumeric(inputString);
        System.out.println("value = " + value);

        setLastRandomizedValue(value);

        currentRequest.getParams().put(key, value);
    }

    /**
     * Query param
     */
    @And("^I set random value with format \\{(.*?)\\} to field \\{(.*?)\\} as Query Param$")
    public void setRandomValueFieldAsQueryParam(String inputString, String key) throws Throwable {

        String value = RAFRandomize.retainSpecialCharsAndRandomizeAlphanumeric(inputString);
        setLastRandomizedValue(value);

        currentRequest.getQueryParams().put(key, value);
    }

    /**
     * <b>Example usage:</b>
     * <pre>
     * And I set {3} random string elements in array {arrayOfStrings} to the current REST request body using formats as follows:
     *  |format1|
     *  |formatttt2|
     *  |formattttttt3|
     * OR
     * And I set {3} random number elements in array {arrayOfNumbers} to the current REST request body using formats as follows:
     *  |1.1|
     *  |1111|
     *  |222.33|
     * </pre>
     * <p> The above examples would produce <b>output</b> as follows:</p>
     * <pre>
     *      {
     *          "arrayOfStrings": ["XlvEyi7", "cnNdgTnra1", "qJCYYyIysFgr7"],
     *          "arrayOfNumbers": [6.3, 7267, 959.15]
     *      }
     *  </pre>
     *
     * @param numberOfElements the number of elements to be added to the given array.
     * @param primitiveType    type of elements to be added - "string" or "number".
     * @param propertyName     property/node name for this array inside the JSON.
     * @param formats          List of formats (example strings, where alphanumeric chars are randomized, and special chars are retained)
     * @throws ParseException
     */
    @And("^I set \\{(\\d+)\\} random (string|number) elements in array \\{(.*?)\\} to the current REST request body using formats as follows:$")
    public void iSetNumberOfRandomElementsInArrayCurrentRequestBodyUsingFormats(int numberOfElements, String primitiveType, String propertyName, List<String> formats) throws ParseException {
        logger.debug("Generating array of {}s: '{}'", primitiveType, propertyName);

        Gson gson = new Gson();
        JsonArray jsonArray = new JsonArray();

        JsonElement jsonElement = gson.fromJson(currentRequest.getBody(), JsonElement.class);
        // only try to load body as JsonObject if it is not null.
        JsonObject jsonObject = new JsonObject();
        if (jsonElement != null) jsonObject = jsonElement.getAsJsonObject();

        for (int i = 0; i < numberOfElements; i++) {
            String rndValue = RAFRandomize.retainSpecialCharsAndRandomizeAlphanumeric(formats.get(i));
            if (primitiveType.equals("string")) {
                JsonPrimitive element = new JsonPrimitive(rndValue);
                jsonArray.add(element);
            } else { // if number:
                Number rndAsNumber = NumberFormat.getInstance().parse(rndValue);
                JsonPrimitive element = new JsonPrimitive(rndAsNumber);
                jsonArray.add(element);
            }

        }

        jsonObject.add(propertyName, jsonArray);
        currentRequest.setBody(gson.toJson(jsonObject));
    }

    /**
     * Add items or Set new list of items into a JSON array inside the current request body
     *
     * @param operation     <b>add</b> or <b>set</b> depending on what you want - add values to existing ones or set brand new values
     * @param primitiveType the type of the items to be added to the array - string, number or boolean
     * @param propertyName  the property name of the array element inside the JSON body
     * @param values        list of values of the respective type to be added inside the array
     * @throws ParseException
     */
    @And("^I (set|add) the following (string|number|boolean) elements inside array \\{(.*?)\\} to the current REST request body:$")
    public void iSetAddTheFollowingElementsInsideArrayInCurrentRequestBody(String operation, String primitiveType, String propertyName, List<Object> values) throws ParseException {
        logger.debug("Creating array of {}s: '{}', operation= '{}'", primitiveType, propertyName, operation);

        boolean isAdd = operation.equals("add");

        Gson gson = new Gson();
        JsonArray jsonArray = new JsonArray();

        JsonElement jsonElement = gson.fromJson(currentRequest.getBody(), JsonElement.class);
        // only try to load body as JsonObject if it is not null.
        JsonObject jsonObject = new JsonObject();
        if (jsonElement != null) jsonObject = jsonElement.getAsJsonObject();

        // If we want to ADD the values to already loaded/existing ones:
        if (jsonObject.getAsJsonArray(propertyName) != null && isAdd) {
            jsonArray = jsonObject.getAsJsonArray(propertyName);
        }

        for (int i = 0; i < values.size(); i++) {
            if (primitiveType.equals("string")) { // if string:
                JsonPrimitive element = new JsonPrimitive(String.valueOf(values.get(i)));
                jsonArray.add(element);
            } else if (primitiveType.equals("number")) { // if number:
                Number valueAsNumber = NumberFormat.getInstance().parse(String.valueOf(values.get(i)));
                JsonPrimitive element = new JsonPrimitive(valueAsNumber);
                jsonArray.add(element);
            } else if (primitiveType.equals("boolean")) { // if boolean:
                JsonPrimitive element = new JsonPrimitive(Boolean.valueOf(String.valueOf(values.get(i))));
                jsonArray.add(element);
            }

        }

        jsonObject.add(propertyName, jsonArray);
        currentRequest.setBody(gson.toJson(jsonObject));

    }

    @And("^I set \\{(\\d+)\\} random (boolean) elements in array \\{(.*?)\\} to the current REST request body$")
    public void iSetRandomBooleanElementsInArrayCurrentRequestBody(int numberOfElements, String primitiveType, String propertyName) {
        logger.debug("Generating array of {}s: '{}'", primitiveType, propertyName);
        Gson gson = new Gson();
        JsonArray jsonArray = new JsonArray();

        JsonElement jsonElement = gson.fromJson(currentRequest.getBody(), JsonElement.class);
        // only try to load body as JsonObject if it is not null.
        JsonObject jsonObject = new JsonObject();
        if (jsonElement != null) jsonObject = jsonElement.getAsJsonObject();

        for (int i = 0; i < numberOfElements; i++) {
            JsonPrimitive element = new JsonPrimitive(new Random().nextBoolean());
            jsonArray.add(element);
        }

        jsonObject.add(propertyName, jsonArray);
        currentRequest.setBody(gson.toJson(jsonObject));
    }

    @And("^the member_id query param in the response Location header match DataContainer value for key \\{(.*?)\\}$")
    public void theMember_idQueryParamInTheResponseLocationHeaderMatchDataContainerValueForKeyDOIMember_id(String dataContainerKey) throws Throwable {

        String actualMemberId = JsonUtils.urlQueryParamsToMap(getLastResponse().getHeaders().get("Location")).get("member_id");

        assertEquals("member_id doesn't match expected: ", getDataContainer().getObject(dataContainerKey), actualMemberId);
    }

    /**
     * Returns true/false depending on what "success" is contained in the last response.
     * E.g.:
     * <br/> {
     * <br/>      "success": false,
     * <br/>      "error_code": "1011",
     * <br/>      "security_parameters": {}
     * <br/> }
     *
     * @return The value of the "success" node if found inside last response. FALSE otherwise.
     */
    public boolean getLastSuccess() throws Throwable {
        boolean bSuccess;
        String responseBody = getLastResponse().getBody();
        Object result = JsonPath.read(responseBody, "$..success");

        if ((result instanceof List)) {
            List lResult = (List) result;
            int size = lResult.size();
            if (size != 0) {

                if (size > 1) {
                    throw new Throwable("Response has more than 1 'success' nodes: " + size);
                }

                bSuccess = Boolean.parseBoolean(lResult.get(0).toString());
                return bSuccess;
            }
        }

        bSuccess = Boolean.parseBoolean(result.toString());

        return bSuccess;

    }

    @And("^the Success in the last response is \\{(.*?)\\}$")
    public void theSuccessInTheLastResponseIs(boolean expectedRes) throws Throwable {

        assertEquals("Expected 'success' doesn't match actual: ", expectedRes, getLastSuccess());

    }

    /**
     * Sleep
     *
     * @param millis milliseconds to sleep
     * @throws Throwable
     */
    @And("^I wait for \\{(\\d+)\\} mseconds$")
    public void I_wait_for_mseconds(int millis) throws Throwable {
        if (millis > 0) {
            logger.info("I wait for " + millis + " msec.");
            Thread.sleep(millis);
        }
    }

    /**
     * Compares sets of data. Delimiter used is the <i>delimiter</i> param.
     *
     * @param headerName   Header which value should be compared to the provided list of values.
     * @param listOfValues list of values separated by <i>delimiter</i>
     * @param delimiter    the delimiter to be used
     * @throws Throwable
     */
    @And("^the response \\{(.*?)\\} header contains the following set of values \\{(.*?)\\}. delimiter:\\{(.*?)\\}$")
    public void theResponseHeaderContainsTheFollowingSetOfValues(String headerName, String listOfValues, String delimiter) throws Throwable {
        String headerValue = getLastResponse().getHeaders().get(headerName);

        if (headerValue == null) {
            throw new RAFException(String.format("Header [%s] not present in response", headerName), GeneralStepDefinitions.class);
        }

        String[] headerArray = headerValue.split(delimiter);
        for (int i = 0; i < headerArray.length; i++) {
            headerArray[i] = headerArray[i].trim();

        }
        Set<String> headerSet = new HashSet<>(Arrays.asList(headerArray));

        String[] expectedArray = listOfValues.split(delimiter);
        for (int i = 0; i < expectedArray.length; i++) {
            expectedArray[i] = expectedArray[i].trim();
        }
        Set<String> expectedSet = new HashSet<>(Arrays.asList(expectedArray));

        logger.debug("headerSet= {} ; expectedSet= {}", headerSet, expectedSet);
        logger.info("headerSet.equals(expectedSet) = {}", headerSet.equals(expectedSet));

        assertEquals("Both sets are not equal.", expectedSet, headerSet);
    }


    @And("^I generate random alphabetic with length between \\{(\\d+)\\} and \\{(\\d+)\\} and store it in DataContainer with key \\{(.*?)\\}$")
    public String iGenerateRandomAlphabeticWithLengthBetweenAndAndStoreItInDataContainerWithKey(int min, int max, String dcKey) throws Throwable {
        logger.info("Generating random alphabetic with length between [{}] and [{}] and storing it into DataContainer, key= [{}] ...", min, max, dcKey);

        String generatedString = RAFRandomize.getAlphabetic(min, max);
        getDataContainer().addObject(dcKey, generatedString);

        logger.info("Generated random alphabetic= [{}], stored into DataContainer, key= [{}]", getDataContainer().getObject(dcKey), dcKey);

        return generatedString;
    }


    /**
     * <b>Preserves</b> the existing JSON body, loads object from DataContainer and <b>adds</b> it as new array element into
     * <br/> current request Body, using provided property name.
     *
     * @param keyDc        Key of the element to load from DataContainer
     * @param propertyName property name to be used for the array inside the JSON body.
     * @throws Throwable
     */
    @And("^I load object with key \\{(.*?)\\} from DataContainer into JSON array \\{(.*?)\\} inside Request Body$")
    public void iLoadValueFromDataContainerIntoJSONArrayInsideRequestBody(String keyDc, String propertyName) throws Throwable {
        Gson gson = new Gson();
        JsonArray jsonArray = new JsonArray();

        JsonElement initialBody = gson.fromJson(currentRequest.getBody(), JsonElement.class);

        // only try to load body as JsonObject if it is not null.
        JsonObject newBody = new JsonObject();
        if (initialBody != null) newBody = initialBody.getAsJsonObject();

        JsonPrimitive element = new JsonPrimitive((String) getDataContainer().getObject(keyDc));
        jsonArray.add(element);
        newBody.add(propertyName, jsonArray);

        currentRequest.setBody(gson.toJson(newBody));
//        logger.debug("currentRequest.Body= [{}]", currentRequest.getBody());
    }

    /**
     * <b>Preserves</b> the existing JSON body, loads object from DataContainer , appends the given text to it and <b>adds</b> it as new array element into
     * <br/> current request Body, using provided property name.
     *
     * @param keyDc        Key of the element to load from DataContainer
     * @param propertyName property name to be used for the array inside the JSON body.
     * @param textToAppend The text which will be appended to the value loaded from the DataContainer before it gets added into the array.
     * @throws Throwable
     */
    @And("^I load object with key \\{(.*?)\\} from DataContainer into JSON array \\{(.*?)\\} inside Request Body and append the text \\{(.*?)\\}$")
    public void iLoadValueFromDataContainerIntoJSONArrayInsideRequestBodyAndAppendText(String keyDc, String propertyName, String textToAppend) throws Throwable {
        Gson gson = new Gson();
        JsonArray jsonArray = new JsonArray();

        JsonElement initialBody = gson.fromJson(currentRequest.getBody(), JsonElement.class);

        // only try to load body as JsonObject if it is not null.
        JsonObject newBody = new JsonObject();
        if (initialBody != null) newBody = initialBody.getAsJsonObject();

        JsonPrimitive element = new JsonPrimitive(((String) getDataContainer().getObject(keyDc)) + textToAppend);
        jsonArray.add(element);
        newBody.add(propertyName, jsonArray);

        currentRequest.setBody(gson.toJson(newBody));

    }

    @And("^I print the current request body$")
    public void iPrintTheCurrentRequestBody() throws Throwable {
        logger.info("Current Request body: \n{}", JsonUtils.prettyPrintJson(currentRequest.getBody()));
    }

    /**
     * Adds the kye/value pairs from the supplied Map into the current request body.
     * <br/> Elements are added as JSON string.
     * <br/> Node names are placed without change, where values provided are considered as format and characters and numbers are randomized
     * (only special chars remain unchanged)
     *
     * @param jsonBodyInputParams Map containing key/value pairs to be added to the currentRequest
     * @throws Throwable
     */
    @When("^I add list of random string nodes to the current request JSON body using property names and formats as follows:$")
    public void iAddListOfRandomStringNodesToTheCurrentRequestJSONBodyUsingPropertyNamesAndFormatsAsFollows(Map<String, String> jsonBodyInputParams) throws Throwable {

        Gson gson = new Gson();
        JsonArray jsonArray = new JsonArray();

        //load existing currentRequest body:
        JsonElement initialBody = gson.fromJson(currentRequest.getBody(), JsonElement.class);

        // only try to load body as JsonObject if it is not null.
        JsonObject newBody = new JsonObject();
        if (initialBody != null) newBody = initialBody.getAsJsonObject();

        // Add supplied nodes to the current request body using random values (preserve format):
        for (Map.Entry<String, String> entry : jsonBodyInputParams.entrySet()) {
            newBody.addProperty(entry.getKey(), RAFRandomize.retainSpecialCharsAndRandomizeAlphanumeric(entry.getValue()));
        }
        currentRequest.setBody(gson.toJson(newBody));

    }

    /**
     * Removes provided nodes from the current request JSON body.
     *
     * @param jsonPaths List of JsonPaths pointing to the nodes that need to be removed from the document.
     * @throws Throwable
     */
    @When("^I remove the following nodes from the current request JSON body, using JSON paths:$")
    public void iRemoveTheFollowingNodesFromTheCurrentRequestJSONBodyUsingJSONPaths(List<String> jsonPaths) throws Throwable {

        if (StringUtils.isBlank(currentRequest.getBody())) {
            logger.warn("Current request body is empty: [{}]. Skipping nodes removal...", currentRequest.getBody());
        } else {
            logger.debug("Initiating removal of the following nodes from the current request JSON body: {}", jsonPaths);

            logger.debug("Original request body (before node removal):\n [{}]", currentRequest.getBody());

            // set this Option in order to get 'null' returned if node not found in the JSON document instead of exception.
            Configuration conf = Configuration.builder().options(Option.SUPPRESS_EXCEPTIONS).build();
            DocumentContext requestContext = JsonPath.using(conf).parse(currentRequest.getBody());

            for (String node : jsonPaths) {
                if (requestContext.read(node) != null) {
                    requestContext = requestContext.delete(node);
                    logger.debug("Node [{}] removed.", node);
                } else {
                    logger.debug("Node [{}] doesn't exist in currentRequest body. Removal was ignored.", node);
                }
            }

            currentRequest.setBody(requestContext.jsonString());

            logger.debug("Updated request body (after node removal):\n [{}]", currentRequest.getBody());
        }

    }

    /**
     * Asserts resource keys sent in request are ignored and not returned in response (fresh keys are generated by server)
     *
     * @param keys List of keys to be checked (those you expect to appear in the Response).
     * @throws Throwable
     */
    @And("^the following resource keys in response are not the same as sent in request body, if any:$")
    public void theFollowingResourceKeysInResponseAreNotTheSameAsSentInRequestBodyIfAny(List<String> keys) throws Throwable {
        // make sure resource keys sent in request are ignored and not returned in response (fresh keys are generated by server)

        // set this Option in order to get 'null' returned if node not found in the JSON document instead of exception.
        Configuration conf = Configuration.builder().options(Option.SUPPRESS_EXCEPTIONS).build();
        ReadContext requestContext = JsonPath.using(conf).parse(getLastRequest().getBody());
        ReadContext responseContext = JsonPath.using(conf).parse(getLastResponse().getBody());

        for (String key : keys) {
            assertNotEquals("resource keys '" + key + "' are equal", requestContext.read("$." + key, String.class), responseContext.read("$." + key, String.class));
        }

    }

    /**
     * Check whether Default Headers are present. Also make sure value
     * is not null
     *
     * @param HeaderParameter list of Environment Dependent Default Header parameter that
     *                        need to be Part of the Response Headers
     */
    @Then("^I validate the response Default header parameters$")
    public void the_response_header_has_all_the_Default_Header_paramters_list(
            List<String> HeaderParameter) {

        for (String Hname : HeaderParameter) {

            assertTrue(
                    "The following header is not part of the response headers: " + Hname,
                    getLastResponse().getHeaders().containsKey(Hname));

            assertNotNull("The value of the following header is null: " + Hname, getLastResponse().getHeaders().get(Hname));
        }
    }

    /**
     * @param cucumberTable Key Value combination of header parameter to be asserted and
     *                      the parameters are not Environment dependent
     */
    @Then("^I validate the headers in the response with the following data$")
    public void Verify_content_type_oauthScopes(
            Map<String, String> cucumberTable) {

        for (String key : cucumberTable.keySet()) {

            assertEquals("Header [" + key
                            + "] value doesn't equal expected value: ",
                    cucumberTable.get(key),
                    getLastResponse().getHeaders().get(key));
        }
    }

    /**
     * Check whether Dependent Header Parameter is present and value
     * is not null
     *
     * @param list Comma separated list of Header names, which need to be part of the Response
     *             Headers map. <br/> For example (ETag, Expires, Last-Modified, X-Error, X-Error-Code)
     */
    @Then("^I validate the response to check whether Dependent Header Parameter is listed:((?:\\S|\\W|,)+)$")
    public void the_response_header_has_all_the_Dependent_Header_parameters_list(List<String> list) {

        for (String Hname : list) {

            assertTrue("Header [" + Hname + "] is not present in the response headers list! ",
                    getLastResponse().getHeaders().containsKey(Hname));

            Assert.assertNotNull("The value of header [" + Hname + "] is null", (getLastResponse().getHeaders().get(Hname)));
        }
    }

    /**
     * Checks whether a provided header contains a provided char sequence.
     *
     * @param headerName The name of the header that we are going to use.
     * @param list       List of char sequences (Strings) that will be asserted whether they appear in the header data.
     */
    @And("^I validate that the header \\{(.*?)\\} in the response contain the following data$")
    public void iValidateThatTheHeaderInTheResponseContainTheFollowingData(String headerName, List<String> list) throws Throwable {

        for (String charSequence : list) {
            if (getLastResponse().getHeaders().get(headerName).contains(charSequence)) {
                logger.debug("SUCESS :: The header [{}] contains the char sequence [{}]", headerName, charSequence);
            }
            assertTrue("FAILED :: The header doesn't contain the provided char sequence.",
                    getLastResponse().getHeaders().get(headerName).contains(charSequence));
        }
    }

    @And("^I clear the query parameters$")
    public void iClearTheQueryParameters() throws Throwable {
        currentRequest.getQueryParams().clear();
    }

    @And("^I set the following request body \\{(.*?)\\}$")
    public void iSetTheFollowingRequestBody(String newBody) throws Throwable {
        currentRequest.setBody(newBody);
    }

    /**
     * Setting value to a key for a specific object that you can locate with JsonPath from the currentRequest
     * @param fieldName the key for which you will insert value
     * @param value the value of the key
     * @param jsonPath the jsonPath of the object you want to edit
     * @throws Throwable
     */
    @And("^I set the key \\{(.*?)\\} and value \\{(.*?)\\} inside object \\{(.*?)\\} with jsonPath \\{(.*?)\\}$")
    public void ISetTheKeyAndValueInsideObjectWithJsonPath(String fieldName,String value,String object,String jsonPath) throws Throwable {
        JsonElement jsonElement = new Gson().fromJson(currentRequest.getBody(), JsonElement.class);
        JsonObject jsonObject = jsonElement.getAsJsonObject();
        String requestBody = currentRequest.getBody();

        Map<String, String> result = JsonPath.read(requestBody, jsonPath);
        result.put(fieldName, value);

        JsonElement newElement = new Gson().fromJson(result.toString(), JsonElement.class);
        jsonObject.add(object, newElement);

        currentRequest.setBody(new Gson().toJson(jsonObject));
    }

    @And("^the field \\{(.*?)\\} has one of the values$")
    public static void theFieldHasOneOfTheValues(String jsonPath, List<String> expectedResultAsList) throws Throwable {

        Configuration conf = Configuration.builder().options(Option.SUPPRESS_EXCEPTIONS).build();
        ReadContext responseContext = JsonPath.using(conf).parse(getLastResponse().getBody());

        Object parsedResult = responseContext.read(jsonPath);
        String actualResultAsString = String.valueOf(parsedResult);

        for(String x: expectedResultAsList) {
            if (x.equals(actualResultAsString)){
                return;
            }
        }

        assertEquals("Expected values doesn't match actual: ", expectedResultAsList, actualResultAsString);
    }

    @Then("I get the last response body and load it to the current request body")
    public void iGetTheLastResponseBodyAndLoadItToTheCurrentRequestBody() {
        currentRequest.setBody(getLastResponse().getBody());
    }
}
