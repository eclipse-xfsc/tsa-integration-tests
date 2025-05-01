//Copyright (c) 2023 Vereign AG [https://www.vereign.com]

package core;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.github.fge.jackson.JsonLoader;
import com.github.fge.jsonschema.core.exceptions.ProcessingException;
import com.github.fge.jsonschema.core.report.ProcessingReport;
import com.github.fge.jsonschema.main.JsonSchema;
import com.github.fge.jsonschema.main.JsonSchemaFactory;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;
import exceptions.RAFException;
import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URLEncodedUtils;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class JsonUtils {

    /**
     * Validate a JSON against a JSON schema
     *
     * @param jsonData   the JSON as string
     * @param jsonSchema the JSON schema as string
     * @return true if the validation passes, false otherwise
     * @throws IOException
     * @throws ProcessingException
     */
    public static boolean validateJsonSchema(String jsonData, String jsonSchema) throws IOException, ProcessingException {
        JsonNode schemaNode = JsonLoader.fromString(jsonSchema);
        JsonNode dataNode = JsonLoader.fromString(jsonData);

        JsonSchemaFactory factory = JsonSchemaFactory.byDefault();
        JsonSchema schema = factory.getJsonSchema(schemaNode);
        ProcessingReport report = schema.validate(dataNode);

        System.out.println(report.toString());
        return report.isSuccess();
    }

    /**
     * @param key  The key you want to find inside the JSON
     * @param json The JSON you want to search in
     * @return NULL if specified key is not found inside the JSON provided. Otherwise the Value of the key provided.
     * @throws IOException
     */
    public static Object getValueOfKeyFromJson(String key, String json) throws IOException {

        ObjectNode object = (ObjectNode) (JsonLoader.fromString(json));

        JsonNode valueOfKey = object.get(key);
        if (valueOfKey == null) {
            return null;
        } else {
            return valueOfKey.asText();
        }
    }

    /**
     * Removes the key from the JSON
     *
     * @param key  the key that will be removed
     * @param json the json as String
     * @return the JSON as String after the key was removed
     * @throws IOException
     */
    public static String removeKeyFromJson(String key, String json) throws IOException {
        ObjectNode object = (ObjectNode) (JsonLoader.fromString(json));
        object.remove(key);
        return object.toString();
    }

    /**
     * Loads a map from a json file
     * The json file should be located in main/resources
     * <p>
     * We are using multiple "profiles" in a json in order to easily manage different test data
     * used by the same scenario.The json format should be:
     * {
     * "profile_name1" : {some json},
     * "profile_name2" : {some other json}
     * }
     *
     * @param jsonName    the name of the json file. If the file is in a subfolder, include the folder in the name
     *                    Example: "/REST.GID.schemas/example_schema.json"
     * @param profileName the name of the json "profile"
     * @return a map with the key value pairs found in the json "profile"
     * @throws IOException
     */
    public static Map<String, String> loadMapFromResource(String jsonName, String profileName) throws IOException {
        Gson gson = new GsonBuilder().disableHtmlEscaping().serializeNulls().create();
        return gson.fromJson(getProfileFromJson(jsonName, profileName),
                new TypeToken<HashMap<String, String>>() {
                }.getType());
    }

    /**
     * Gets a "profile" from a json
     * <p>
     * We are using multiple "profiles" in a json in order to easily manage different test data
     * used by the same scenario.The json format should be:
     * {
     * "profile_name1" : {some json},
     * "profile_name2" : {some other json}
     * }
     *
     * @param jsonName    the name of the json file. If the file is in a subfolder, include the folder in the name
     *                    Example: "/test data/.json"
     * @param profileName the name of the json "profile"
     * @return the loaded JSON "profile" as string
     * @throws IOException
     */
    public static String getProfileFromJson(String jsonName, String profileName) throws IOException {
        String json = loadJson(jsonName);

        Gson gson = new GsonBuilder().disableHtmlEscaping().serializeNulls().create();
        Map<String, JsonElement> retMap = gson.fromJson(json, new TypeToken<HashMap<String, JsonElement>>() {
        }.getType());
        return gson.toJson(retMap.get(profileName));
    }

    /**
     * Loads a json from a file
     * @param jsonName the name of the json file. If the file is in a subfolder, include the folder in the name
     *                 Example: "/test data/.json"
     * @return the loaded JSON as string
     * @throws IOException
     */
    public static String loadJson(String jsonName) throws IOException {
        return JsonLoader.fromResource(jsonName).toString();
    }

    /**
     * Maps the response body to a Java object
     *
     * Example: Member currentMember = getResourceFromResponse(response, Member.class);
     * @param response the Response object
     * @param resourceClass the class of object
     * @param <T> the type of the object being returned
     * @return an object of the specified type
     */
    public static <T> T getResourceFromResponse(final Response response, final Class<T> resourceClass) {
        return new Gson().fromJson(response.getBody(), resourceClass);
    }

    /**
     * Maps the response body to a Java object
     *
     * Example: Member currentMember = getResourceFromRequest(currentRequest, Member.class);
     * @param request the Response object
     * @param resourceClass the class of object
     * @param <T> the type of the object being returned
     * @return an object of the specified type
     */
    public static <T> T getResourceFromRequest(final Request request, final Class<T> resourceClass) {
        return new Gson().fromJson(request.getBody(), resourceClass);
    }

    /**
     * Get the base uri from the "env" property
     * @return the base uri
     */
    public static String getBaseUri() {
        return System.getProperty("env");
    }

    /**
     * Get the RoundCube test instance from the "env" property
     * @return the roundcube url
     */
    public static String getRoundcubeUrl() {
        return System.getProperty("roundcube");
    }


    /**
     * Get the project location
     * @return the uri
     */
    public static String getProjectLocation() {
        return System.getProperty("projectLocation");
    }

    /**
     * Get the project location
     * @return the uri
     */
    public static String getChromeProfileLocation() {
        return System.getProperty("chromeProfileLocation");
    }



    /**
     * Get the size of the screen that should be set when the isMaximized is set to true
     * @return the size of the window
     */
    public static String getDeviceType(){
        return System.getProperty("deviceType");
    }


    /**
     * Get the url for "TSA policy"
     * @return the uri
     */
    public static String getTSAPolicy() {
        return System.getProperty("baseUrl") + "/policy";
    }

    /**
     * Get the url for "TSA Cache"
     * @return the uri
     */
    public static String getTSACache() {
        return System.getProperty("baseUrl") + "/cache";
    }

    /**
     * Get the url for "TSA Infohub"
     * @return the uri
     */
    public static String getTSAInfohub() {
        return System.getProperty("baseUrl") + "/infohub";
    }

    /**
     * Get the url for "TSA Signer"
     * @return the uri
     */
    public static String getTSASigner() {
        return System.getProperty("baseUrl") + "/signer";
    }

    /**
     * Get the url for "TSA Task"
     * @return the uri
     */
    public static String getTSATask() {
        return System.getProperty("baseUrl") + "/task";
    }

    /**
     * UTF8 encodes a string
     *
     * @param stringToEncode the string that will be encoded
     * @return the encoded string
     */
    public static String encodeUTF8(String stringToEncode) {
        String encodedString;

        try {
            encodedString = URLEncoder.encode(stringToEncode, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            throw new RAFException(e, JsonUtils.class);
        }

        return encodedString;
    }

    /**
     * Decode a UTF8 encoded string
     *
     * @param stringToDecode the string to be decoded
     * @return the decoded string
     */
    public static String decodeUTF8(String stringToDecode) {
        String decodedString;

        try {
            decodedString = URLDecoder.decode(stringToDecode, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            throw new RAFException(e, JsonUtils.class);
        }

        return decodedString;
    }

    /**
     * Encodes the values from the Map using UTF-8.
     *
     * @param map The Map that will be converted.
     * @return String representation of the map in format:
     * key1=value1&key2=value2&key3=value3
     */
    public static String mapToUrlEncodedString(Map<?, ?> map) {

        StringBuilder sb = new StringBuilder();
        String urlencodedPairs; // store all params in one string (appended with &); values are Encoded. keys are NOT for now.

        for (Map.Entry currentEntry : map.entrySet()) {
            sb.append("&").append(currentEntry.getKey()).append("=").append(JsonUtils.encodeUTF8(currentEntry.getValue().toString()));
        }

        urlencodedPairs = sb.toString().substring(1); //remove the 1st character '&'

        return urlencodedPairs;
    }

    /**
     * Converts an URL encoded (UTF-8) key value pairs String into a Map with keys and values (Decoded).
     * @param keyValuePairsAsEncodedString The string that has to be converted to map. E.g.
     *                                     key1=value1&key2=value2&email=mail%40example.com&key4=value+4
     * @return Map with decoded entries. E.g.:
     * <br/> key1 : value1
     * <br/> key2 : value2
     * <br/> email : mail@example.com
     * <br/> key4 : value 4
     *
     */
    public static Map<String, String> urlEncodedStringToMap(String keyValuePairsAsEncodedString) {
        Map<String, String> keyValueMap = new HashMap<>();


        List<NameValuePair> listOfNameValuePairs = URLEncodedUtils.parse(keyValuePairsAsEncodedString, Charset.forName("UTF-8"));
        for (NameValuePair pair : listOfNameValuePairs) {
            keyValueMap.put(pair.getName(), pair.getValue());
        }


        return keyValueMap;
    }

    /**
     * Takes the Query param part from the URL, decodes it using UTF-8 and converts it into a map which contains the key/value paris.
     * @param url String representation of valid URL. E.g. "http://domain.com/error?error=E02&detail=pr_email%3dblank%26pr_dob_yyyy%3dinvalid&key+3=value+3"
     * @return Map with decoded entries. E.g.:
     * <br/> error : E02
     * <br/> detail : pr_email=blank&pr_dob_yyyy=invalid
     * <br/> key 3 : value 3
     * @throws URISyntaxException
     */
    public static Map<String, String> urlQueryParamsToMap(String url) throws URISyntaxException {
        Map<String, String> keyValueMap = new HashMap<>();

        URI uri = new URI(url);

        List<NameValuePair> listOfNameValuePairs = URLEncodedUtils.parse(uri,"UTF-8");

        for (NameValuePair pair : listOfNameValuePairs) {
            keyValueMap.put(pair.getName(), pair.getValue());
        }

        return keyValueMap;
    }

    /**
     * Encodes Map keys and values using UTF-8 encoding.
     *
     * @param mapToEncode the map to encode
     * @return The Map with encoded keys and values.
     */
    public static Map<String, Object> encodeMap(Map<String, Object> mapToEncode) {
        Map<String, Object> objectHashMap = new HashMap<>();

        mapToEncode.entrySet().stream().forEach(entry -> objectHashMap.put(
                encodeUTF8(entry.getKey()),
                encodeUTF8(entry.getValue().toString() ) ));

        return objectHashMap;
    }

    /**
     * Formats the map for printing
     *
     * @param mapTopPrint the map to format
     * @return a String containing a line for each key-value
     */
    public static String prettyPrintMap(Map<?, ?> mapTopPrint) {
        if (mapTopPrint.isEmpty())
            return "none";
        StringBuilder sb = new StringBuilder();
        mapTopPrint.entrySet().forEach(pair -> sb.append(String.format("\n  %s:%s", pair.getKey(), pair.getValue())));
        return sb.toString();
    }

    /**
     * Formats the json string for printing
     *
     * @param json the json to be formatted
     * @return a String containing the formatted json
     */
    public static String prettyPrintJson(String json) {
        Gson gson = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().serializeNulls().setLenient().create();
        JsonParser jp = new JsonParser();
        JsonElement je = jp.parse(json);

        return gson.toJson(je);
    }

    /**
     * Get the roundcubeUsername from the build.gradle file
     * @return the roundcube username
     */
    public static String getRoundcubeUsername() {
        return System.getProperty("roundcubeUsername");
    }

    /**
     * Get the roundcubePassword from the build.gradle file
     * @return the roundcube password
     */
    public static String getRoundcubePassword() {
        return System.getProperty("roundcubePassword");
    }

    /**
     * Get the secondRoundcubeUsername from the build.gradle file
     * @return the roundcube username
     */
    public static String getSecondRoundcubeUsername() {
        return System.getProperty("secondRoundcubeUsername");
    }

    /**
     * Get the secondRoundcubePassword from the build.gradle file
     * @return the roundcube password
     */
    public static String getSecondRoundcubePassword() {
        return System.getProperty("secondRoundcubePassword");
    }

    /**
     * Get the gmailPassword from the build.gradle file
     * @return the gmail password
     */
    public static String getGmailPassword(){
        return System.getProperty("gmailPassword");
    }

    /**
     * Get the outlookPassword from the build.gradle file
     * @return the outlook password
     */
    public static String getOutlookPassword(){
        return System.getProperty("outlookPassword");
    }

    /**
     * Get the outlookManifestfrom the build.gradle file
     * @return the outlook Manifest
     */
    public static String getOutlookManifest(){
        return System.getProperty("outlookManifest");
    }

    public static boolean getIsHeadless(){
        if(System.getProperty("isHeadless").equalsIgnoreCase("true")){
            return true;
        }else{
            return false;
        }

    }
}
