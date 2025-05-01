//Copyright (c) 2023 Vereign AG [https://www.vereign.com]

package core;


import io.restassured.http.Header;
import io.restassured.http.Headers;

import java.util.HashMap;

/**
 * Class used to store the response received.
 * It only stores the status code, the response body and the headers
 */
public class Response {
    private int statusCode;
    private String body;
    private HashMap<String, String> headers;

    public Response(int statusCode, String body, Headers headers) {
        this.statusCode = statusCode;
        this.body = body;
        this.headers = new HashMap<>();
        for (Header header : headers)
            this.headers.put(header.getName(), header.getValue());
    }

    /**
     * Get the headers of the response
     *
     * @return the headers hashmap
     */
    public HashMap<String, String> getHeaders() {
        return headers;
    }

    /**
     * Get the status code of the response
     *
     * @return the status code of the response
     */
    public int getStatusCode() {
        return statusCode;
    }

    /**
     * Get the body of the response
     *
     * @return the body of the response
     */
    public String getBody() {
        return body;
    }

//    @Override
//    public String toString() {
//
//        return String.format("----Response----\nStatus code: %d\nHeaders:%s\nBody:\n%s\n" +
//                        "\n-------------------------------------------------------------",
//                statusCode, JsonUtils.prettyPrintMap(headers),
//                (headers.get("Content-Type") != null ? headers.get("Content-Type") : "null")
//                        .contains("application/json") ? JsonUtils.prettyPrintJson(body) : body);
//    }
}
