//Copyright (c) 2023 Vereign AG [https://www.vereign.com]

package core;

import io.restassured.RestAssured;
import io.restassured.config.ConnectionConfig;
import io.restassured.http.ContentType;
import io.restassured.response.ExtractableResponse;
import io.restassured.specification.RequestSpecification;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import static io.restassured.RestAssured.config;
import static io.restassured.RestAssured.given;
import static io.restassured.config.EncoderConfig.encoderConfig;

public class RestClient {
    private static final Logger logger = LogManager.getLogger(RestClient.class.getSimpleName());
    private static boolean automaticRedirects = false;

    public RestClient() {
    }

    /**
     * Sends the current request using PUT
     *
     * @param request the current request
     * @return the response received
     */
    public static Response put(Request request) {
        request.setVerb("PUT");
        logger.info("Sending PUT request {}", request);
        ExtractableResponse response = given()
                .redirects().follow(automaticRedirects)
                .contentType(request.getContentType())
                .headers(request.getHeaders())
                .queryParams(request.getQueryParams())
                .pathParams(request.getPathParams())
                .body(request.getBody())
                .log().all()
                .put(request.getVersion() + request.getPath())
                .then()
                .log().all()
                .extract();
        Response resp = new Response(response.statusCode(), response.body().asString(), response.headers());
        logger.info("Received response {}", resp);
        return resp;
    }

    /**
     * Sends the current request using propfind
     *
     * @param request the current request
     * @return the response received
     */
    public static Response propfind(Request request) {
        request.setVerb("PROPFIND");
        logger.info("Sending PROPFIND request {}", request);
        ExtractableResponse response = given()
                .redirects().follow(automaticRedirects)
                .contentType(request.getContentType())
                .headers(request.getHeaders())
                .queryParams(request.getQueryParams())
                .pathParams(request.getPathParams())
                .body(request.getBody())
                .log().all()
                .when()
                .request("propfind",request.getPath())
                .then()
                .log().all()
                .extract();
        Response resp = new Response(response.statusCode(), response.body().asString(), response.headers());

        logger.info("Received response {}", resp);
        return resp;
    }


    /**
     * Sends the current request using GET
     *
     * @param request the current request
     * @return the response received
     */
    public static Response get(Request request) {
        request.setVerb("GET");
        logger.info("Sending GET request {}", request);
        ExtractableResponse response = given()
                .redirects().follow(automaticRedirects)
                .contentType(request.getContentType())
                .headers(request.getHeaders())
                .queryParams(request.getQueryParams())
                .pathParams(request.getPathParams())
                .log().all()
                .get(request.getVersion() + request.getPath())
                .then()
                .log().all()
                .extract();
        Response resp = new Response(response.statusCode(), response.body().asString(), response.headers());
        logger.info("Received response {}", resp);
        return resp;
    }

    /**
     * Sends the current request using POST
     *
     * @param request the current request
     * @return the response received
     */
    public static Response post(Request request) {
        request.setVerb("POST");
        RequestSpecification rs = given()
                .config(config().connectionConfig(new ConnectionConfig().closeIdleConnectionsAfterEachResponse()))
                .config(config().encoderConfig(encoderConfig().encodeContentTypeAs("application/json", ContentType.TEXT)))
                .redirects().follow(automaticRedirects)
                .contentType(request.getContentType())
                .headers(request.getHeaders());

        if (!request.getFormParams().isEmpty()) {
            //use form params and no body;
            rs.formParams(request.getFormParams());
        } else if (!request.getParams().isEmpty()) {
            //encode the params map and send it in the request body
            rs.body(JsonUtils.mapToUrlEncodedString(request.getParams()));
        } else {
            // send just the body, usually used when the content-type is application/json
            rs.body(request.getBody());
        }

        rs
                .queryParams(request.getQueryParams())
                .pathParams(request.getPathParams())
                .log().all();


        ExtractableResponse response = rs
                .post(request.getVersion() + request.getPath())
                .then()
                .log().all()
                .extract();
        Response resp = new Response(response.statusCode(), response.body().asString(), response.headers());
        logger.info("Received response {}", resp);
        return resp;
    }

    /**
     * Sends the current request using DELETE
     *
     * @param request the current request
     * @return the response received
     */
    public static Response delete(Request request) {
        request.setVerb("DELETE");
        ExtractableResponse response = given()
                .redirects().follow(automaticRedirects)
                .contentType(request.getContentType())
                .headers(request.getHeaders())
                .log().all()
                .delete(request.getVersion() + request.getPath())
                .then()
                .log().all()
                .extract();
        Response resp = new Response(response.statusCode(), response.body().asString(), response.headers());
        logger.info("Received response {}", resp);
        return resp;
    }

    /**
     * Sends the current request using HEAD
     *
     * @param request the current request
     * @return the response received
     */
    public static Response head(Request request) {
        request.setVerb("HEAD");
        ExtractableResponse response = given()
                .redirects().follow(automaticRedirects)
                .contentType(request.getContentType())
                .headers(request.getHeaders())
                .log().all()
                .head(request.getVersion() + request.getPath())
                .then()
                .log().all()
                .extract();
        Response resp = new Response(response.statusCode(), response.body().asString(), response.headers());
        logger.info("Received response {}", resp);
        return resp;
    }

    /**
     * Sets the default encoding used in all the requests
     *
     * @param charset the encoding
     */
    public static void setDefaultEncoding(String charset) {
        logger.info("Setting default encoding to {}", charset);
        config = config().encoderConfig(encoderConfig().defaultContentCharset(charset).
                appendDefaultContentCharsetToContentTypeIfUndefined(false));
    }

    /**
     * Sets the base URI that will be used by all the http calls.
     * This should be the base URI of the api
     *
     * @param baseURI the base url
     */
    public static void setBaseURI(String baseURI) {
        logger.info("Setting baseURI to {}", baseURI);
        RestAssured.baseURI = baseURI;
    }

    /**
     * Whether RestClient should automatically append the content charset to the content-type header if not defined explicitly
     *
     * @param value value to be set
     */
    public static void appendDefaultContentCharsetToContentTypeIfUndefined(boolean value) {
        config = config().encoderConfig(encoderConfig().
                appendDefaultContentCharsetToContentTypeIfUndefined(value));
    }

    /**
     * Set to true if you want the http calls to follow redirects (not recommended for testing)
     *
     * @param value value to be set
     */
    public static void setAutomaticRedirects(boolean value) {
        automaticRedirects = value;
    }


    /**
     * Sends the current request using POST, while attaching a file
     *
     * @param request the current request
     * @return the response received
     */
    public Response postFile(Request request, String filePath) throws IOException {
        request.setVerb("POST");
        RequestSpecification rs = given()
                .config(config().encoderConfig(encoderConfig().encodeContentTypeAs("application/pdf", ContentType.TEXT)))
                .redirects().follow(automaticRedirects)
                .contentType(request.getContentType())
                .headers(request.getHeaders());

        if (!request.getFormParams().isEmpty()) {
            //use form params and no body;
            rs.formParams(request.getFormParams());
        } else if (!request.getParams().isEmpty()) {
            //encode the params map and send it in the request body
            rs.body(JsonUtils.mapToUrlEncodedString(request.getParams()));
        } else {
            rs.body(Files.readAllBytes(Paths.get(filePath)));
        }

        rs
                .queryParams(request.getQueryParams())
                .pathParams(request.getPathParams())
                .log().all();


        ExtractableResponse response = rs
                .post(request.getVersion() + request.getPath())
                .then()
                .log().all()
                .extract();
        Response resp = new Response(response.statusCode(), response.body().asString(), response.headers());
        logger.info("Received response {}", resp);
        return resp;
    }

    /**
     * Sends the current request using PUT, while attaching a file
     *
     * @param request the current request
     * @return the response received
     */
    public static Response putFile(Request request,String filePath) throws IOException{
        request.setVerb("PUT");
        RequestSpecification rs = given()
                .config(config().encoderConfig(encoderConfig().encodeContentTypeAs("application/pdf", ContentType.TEXT)))
                .redirects().follow(automaticRedirects)
                .contentType(request.getContentType())
                .headers(request.getHeaders());

        if (!request.getFormParams().isEmpty()) {
            //use form params and no body;
            rs.formParams(request.getFormParams());
        } else if (!request.getParams().isEmpty()) {
            //encode the params map and send it in the request body
            rs.body(JsonUtils.mapToUrlEncodedString(request.getParams()));
        } else {
            rs.body(Files.readAllBytes(Paths.get(filePath)));
        }

        rs
                .queryParams(request.getQueryParams())
                .pathParams(request.getPathParams())
                .log().all();

        ExtractableResponse response = rs
                .put(request.getVersion() + request.getPath())
                .then()
                .log().all()
                .extract();
        Response resp = new Response(response.statusCode(), response.body().asString(), response.headers());
        return resp;
    }
}

