//Copyright (c) 2023 Vereign AG [https://www.vereign.com]

package utils;

import exceptions.RAFException;
import exceptions.RAFIllegalState;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.node.ObjectNode;

import java.io.IOException;
import java.util.StringTokenizer;


/**
 * Allow to browse over JSON string to find field in specified position
 */
public class JSONParser {

    private ObjectMapper mapper = new ObjectMapper();

    /**
     * Replace field value in specified position
     * @param path - represent path which uniquely identify field in any JSON
     *             with format "field1>field2>field3"
     * @param json - target JSON string to modify
     * @param value - new value for field
     * @return modified JSON
     */
    public String replaceValue(String path, String json, String value) {
        try {
            JsonNode rootNode = mapper.readTree(json);
            StringTokenizer tokens = new StringTokenizer(path, ">");
            String fieldName = tokens.nextToken();
            JsonNode toReplace = null;
            while (tokens.hasMoreTokens()) {
                if (toReplace == null) {
                    toReplace = rootNode.get(fieldName);
                } else {
                    toReplace = toReplace.get(fieldName);
                }
                if (toReplace == null) {
                    throw new RAFIllegalState("Wrong path", JSONParser.class);
                }
                fieldName = tokens.nextToken();
            }
            if (toReplace == null) {
                if (rootNode.has(fieldName)) {
                    ((ObjectNode) rootNode).put(fieldName, value);
                } else {
                    throw new RAFIllegalState("Wrong path", JSONParser.class);
                }
            } else {
                ((ObjectNode) toReplace).put(fieldName, value);
            }
            return mapper.writeValueAsString(rootNode);
        } catch (IOException e) {
            throw new RAFException(e, JSONParser.class);
        }
    }

    /**
     * Add field with value in specified position
     *
     * @param path  - represent path which uniquely identify field in any JSON
     *              with format "field1>field2>field3"
     * @param json  - target JSON string to modify
     * @param fieldName - new field name
     * @param value - new value for field
     * @return modified JSON
     */
    public String addValue(String path, String json,String fieldName, String value) {
        try {
            JsonNode rootNode = mapper.readTree(json);
            StringTokenizer tokens = new StringTokenizer(path, ">");
            String pathField = tokens.nextToken();
            JsonNode toReplace = null;
            while (tokens.hasMoreTokens()) {
                if (toReplace == null) {
                    toReplace = rootNode.get(pathField);
                } else {
                    toReplace = toReplace.get(pathField);
                }
                if (toReplace == null) {
                    throw new RAFIllegalState("Wrong path", JSONParser.class);
                }
                pathField = tokens.nextToken();
            }
            if (toReplace == null) {
                if (rootNode.has(pathField)) {
                    ((ObjectNode) rootNode).put(fieldName, value);
                } else {
                    throw new RAFIllegalState("Wrong path", JSONParser.class);
                }
            } else {
                ((ObjectNode) toReplace).put(fieldName, value);
            }
            return mapper.writeValueAsString(rootNode);
        } catch (IOException e) {
            throw new RAFException(e, JSONParser.class);
        }
    }

}

