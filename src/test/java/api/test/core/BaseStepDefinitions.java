//Copyright (c) 2023 Vereign AG [https://www.vereign.com]

package api.test.core;

import com.fasterxml.jackson.databind.JsonNode;
import com.github.fge.jackson.JsonLoader;
import com.github.fge.jsonschema.core.exceptions.ProcessingException;
import com.github.fge.jsonschema.core.report.ProcessingReport;
import com.github.fge.jsonschema.main.JsonSchema;
import com.github.fge.jsonschema.main.JsonSchemaFactory;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import core.*;
import exceptions.*;
import io.restassured.RestAssured;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.util.Map;

public class BaseStepDefinitions {
    private static final Logger logger = LogManager.getLogger(BaseStepDefinitions.class.getSimpleName());
    private DataContainer dataContainer;

    public BaseStepDefinitions(DataContainer dataContainer) {
        this.dataContainer = dataContainer;
    }

    public BaseStepDefinitions() {
    }

    protected static Response getLastResponse() {
        return ResponseContainer.getInstance().getLastResponse();
    }

    protected static Request getLastRequest() {
        return RequestContainer.getInstance().getLastRequest();
    }

    public static void setProxy(String proxyUrl, int port) {
        RestAssured.proxy(proxyUrl, port);
    }

    public DataContainer getDataContainer() {
        return dataContainer;
    }

    protected void addResponse(Response response) {
        ResponseContainer.getInstance().addResponse(response);
    }

    protected void addRequest(Request request) {
        RequestContainer.getInstance().addRequest(request);
    }

    public void validateJsonSchema(String jsonToValidate, String jsonSchema) throws IOException, ProcessingException {
        JsonNode schemaNode = JsonLoader.fromString(jsonSchema);
        JsonNode dataNode = JsonLoader.fromString(jsonToValidate);

        JsonSchemaFactory factory = JsonSchemaFactory.byDefault();
        JsonSchema schema = factory.getJsonSchema(schemaNode);
        ProcessingReport report = schema.validate(dataNode);

        //throw exception if validation fails. This way it will appear in the Cucumber report
        if (!report.isSuccess()) {
            throw new RAFException("JSON schema validation failed:\n" + report.toString(), BaseStepDefinitions.class);
        }
    }

    /**
     * Loads data provided as input into a Map of type String, String and returns the Map.
     * <br/> IMPORTANT: This method only supports JSON input consisting of only primitive types (string, boolean, number).
     * Objects and arrays are not supported. If there is such in the JSON, exception will be thrown.
     * @param json The JSON which will be converted to Map. Should be simple key:value pairs, so it can be transferred/converted
     *             to String, String;
     * @return The generated Map object.
     * @throws IOException
     */
    protected Map<String, String> loadMapFromJson(String json) throws IOException {
        Gson gson = new GsonBuilder().disableHtmlEscaping().serializeNulls().create();
        return gson.fromJson(json,
                new TypeToken<Map<String, String>>() {
                }.getType()
        );
    }
}
