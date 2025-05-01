//Copyright (c) 2023 Vereign AG [https://www.vereign.com]

package core;

import java.util.HashMap;
import java.util.Map;

public class Request {
    private String path;
    private Map<String, Object> params;
    private Map<String, String> headers;
    private Map<String, String> queryParams;
    private Map<String, String> formParams;
    private Map<String, String> pathParams;
    private String body;
    private String contentType;
    private String version;
    private String verb;

    public Request() {
        path = "";
        body = "";
        version = "";
        verb = "";
        params = new HashMap<>();
        headers = new HashMap<>();
        queryParams = new HashMap<>();
        formParams = new HashMap<>();
        pathParams = new HashMap<>();
    }

    public Request(String path, Map<String, Object> params, Map<String, String> headers, Map<String, String> queryParams, Map<String, String> formParams, Map<String, String> pathParams, String body, String contentType, String version, String verb) {
        this.path = path;
        this.params = params;
        this.headers = headers;
        this.queryParams = queryParams;
        this.formParams = formParams;
        this.pathParams = pathParams;
        this.body = body;
        this.contentType = contentType;
        this.version = version;
        this.verb = verb;
    }

    public Request(Request requestParam) {
        this.path = requestParam.path;
        this.params = new HashMap<>(requestParam.params);
        this.headers = new HashMap<>(requestParam.headers);
        this.queryParams = new HashMap<>(requestParam.queryParams);
        this.formParams = new HashMap<>(requestParam.formParams);
        this.pathParams = new HashMap<>(requestParam.pathParams);
        this.body = requestParam.body;
        this.contentType = requestParam.contentType;
        this.version = requestParam.version;
        this.verb = requestParam.verb;
    }

    public static Request generate(String body, String path) {
        Request request = new Request();
        request.getHeaders().put("X-Client-Id", "rms-ui");
        request.setContentType("application/json");
        request.setBody(body);
        request.setPath(path);
        return request;
    }

    /**
     * Returns the request path
     *
     * @return the request path
     */
    public String getPath() {
        return path;
    }

    /**
     * Sets the request path
     *
     * @param path the request path not including the base url
     */
    public void setPath(String path) {
        this.path = path;
    }

    /**
     * Returns the request parameters
     *
     * @return the request parameters HashMap
     */
    public Map<String, Object> getParams() {
        return params;
    }

    /**
     * Sets the request parameters
     *
     * @param params hashmap of params
     */
    public void setParams(Map<String, Object> params) {
        this.params = params;
    }

    /**
     * Returns the headers HashMap
     * @return the headers HashMap
     */
    public Map<String, String> getHeaders() {
        return headers;
    }

    /**
     * Sets the headers HashMap. Use this only if you want to replace all the existing headers.
     * @param headers the HashMap that will be set
     */
    public void setHeaders(Map<String, String> headers) {
        this.headers = headers;
    }

    /**
     * Gets the query parameters HashMap
     *
     * @return the query parameters HashMap
     */
    public Map<String, String> getQueryParams() {
        return queryParams;
    }

    /**
     * Sets the query parameters HashMap. Use this only if you want to replace all the existing query parameters
     *
     * @param queryParams the HashMap that will be set
     */
    public void setQueryParams(Map<String, String> queryParams) {
        this.queryParams = queryParams;
    }

    /**
     * Returns the request body
     * @return the request body
     */
    public String getBody() {
        return body;
    }

    /**
     * Sets the body of the request
     * @param body the body of the request. Currently it is implemented to only work with JSON
     */
    public void setBody(String body) {
        this.body = body;
    }

    /**
     * Gets the content type of the request
     * @return the content type of the request
     */
    public String getContentType() {
        return contentType;
    }

    /**
     * Sets the content type of the request. No validation is done to check if it is a valid value.
     * @param contentType the content type as string
     */
    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    /**
     * Gets the version of the endpoint that the request is made to.
     * @return the version
     */
    public String getVersion() {
        return version;
    }

    /**
     * Sets the version of the api that will be used for the current request
     *
     * This is needed because for some apis there are multiple versions for the same endpoint. Set the version to an
     * empty string it is not the case for the api you are testing.
     * @param version the version as string
     */
    public void setVersion(String version) {
        this.version = version;
    }

    /**
     * Adds the passed value to the Authorization header.
     *
     * Don't use this if you are adding the Authorization header using getHeaders().put()
     * @param authString the token as string
     */
    public void addAuthorization(String authString) {
        headers.put("Authorization", authString);
    }

    /**
     * Removes the Authorization header
     */
    public void deleteAuthorization() {
        headers.remove("Authorization");
    }

    /**
     * Returns the form parameters hashmap
     *
     * @return the form parameters hashmap
     */
    public Map<String, String> getFormParams() {
        return formParams;
    }

    /**
     * Sets the form parameters hashmap
     * Don't use this unless you want to replace all the form parameters
     *
     * @param formParams parameters as map
     */
    public void setFormParams(Map<String, String> formParams) {
        this.formParams = formParams;
    }

    /**
     * Returns the path parameters map
     *
     * @return the path parameters map
     */
    public Map<String, String> getPathParams() {
        return pathParams;
    }

    /**
     * Sets path parameters map
     * @param pathParams Map with path params key=paramID, value=paramValue
     */
    public void setPathParams(Map<String, String> pathParams) {
        this.pathParams = pathParams;
    }

    public String getVerb() {
        return verb;
    }

    public void setVerb(String verb) {
        this.verb = verb;
    }

    /**
     * Clears the request
     *
     * Clears the headers, query params, path params, path and body
     */
    public void clear() {
        path = "";
        params.clear();
        headers.clear();
        queryParams.clear();
        formParams.clear();
        body = "";
    }

    @Override
    public String toString() {
        return String.format("---- Request ----\n" +
                        "Method(verb): %s\n" +
                        "Path:         %s\n" +
                        "Headers:      %s\n" +
                        "Body:\n%s\n" +
                        "Params:       %s\n" +
                        "Path parameters:%s\n" +
                        "QueryParams:  %s\n" +
                        "Version:      %s\n" +
                        "--------------------------------------------------------------",
                verb, path, JsonUtils.prettyPrintMap(headers),
                (headers.get("Content-Type") != null ? headers.get("Content-Type") : "null")
                        .contains("application/json") ? JsonUtils.prettyPrintJson(body) : body,
                JsonUtils.prettyPrintMap(params), JsonUtils.prettyPrintMap(pathParams),
                JsonUtils.prettyPrintMap(queryParams), version);
    }
}
