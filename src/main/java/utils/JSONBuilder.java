//Copyright (c) 2023 Vereign AG [https://www.vereign.com]

package utils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import exceptions.RAFException;
import exceptions.RAFIllegalState;
import org.apache.commons.lang3.StringUtils;
import org.codehaus.jackson.JsonFactory;
import org.codehaus.jackson.JsonGenerator;
import org.codehaus.jackson.JsonToken;

/**
 *  Wrapper over Jackson JSON classes which allow to create JSON with any complexity
 *  chained method of calls
 */
public class JSONBuilder {

    private static final JsonFactory JSON_FACTORY = new JsonFactory();
    // array of JSON nodes inserted in order as you read JSON string as single line from left to right
    private List<JsonItem> items = new ArrayList<>();

    public JSONBuilder(String fieldName, Object value, JsonToken token) {
        if (value instanceof Boolean) {
            if ((Boolean) value) {
                items.add(new JsonItem(fieldName, null, JsonToken.VALUE_TRUE));
            } else {
                items.add(new JsonItem(fieldName, null, JsonToken.VALUE_FALSE));
            }
        } else {
            items.add(new JsonItem(fieldName, value, token));
        }
    }

    public JSONBuilder() {
    }

    /*
    In this way we support adding fields with type object
     */
    public JSONBuilder(String fieldName, JSONBuilder value) {
        items.add(new JsonItem(fieldName, null, JsonToken.START_OBJECT));
        for (JsonItem item : value.items) {
            items.add(new JsonItem(item.getFieldName(), item.getValue(), item.getToken()));
        }
        items.add(new JsonItem(fieldName, null, JsonToken.END_OBJECT));
    }

    public JSONBuilder(String fieldName, List<JSONBuilder> jsonBuilderList) {
        items.add(new JsonItem(fieldName, null, JsonToken.START_ARRAY));
        for (JSONBuilder builderItem : jsonBuilderList) {
            this.addItem(null, builderItem);
        }
        items.add(new JsonItem(fieldName, null, JsonToken.END_ARRAY));
    }

    public JSONBuilder(String fieldName, String value) {
        items.add(new JsonItem(fieldName, value, JsonToken.VALUE_STRING));
    }

    public JSONBuilder addItem(String fieldName, JSONBuilder value) {
        items.add(new JsonItem(fieldName, null, JsonToken.START_OBJECT));
        for (JsonItem item : value.items) {
            items.add(new JsonItem(item.getFieldName(), item.getValue(), item.getToken()));
        }
        items.add(new JsonItem(fieldName, null, JsonToken.END_OBJECT));
        return this;
    }

    public JSONBuilder addItem(String fieldName, Object value, JsonToken token) {
        if (value instanceof Boolean) {
            if ((Boolean) value) {
                items.add(new JsonItem(fieldName, null, JsonToken.VALUE_TRUE));
            } else {
                items.add(new JsonItem(fieldName, null, JsonToken.VALUE_FALSE));
            }
        } else {
            items.add(new JsonItem(fieldName, value, token));
        }
        return this;
    }

    public JSONBuilder addStringItem(String fieldName, String value) {
        this.items.add(new JsonItem(fieldName, value, JsonToken.VALUE_STRING));
        return this;
    }

    public JSONBuilder addNullItem(String fieldName) {
        this.items.add(new JsonItem(fieldName, null, JsonToken.VALUE_NULL));
        return this;
    }

    public JSONBuilder addJSONBuilderList(String fieldName, List<JSONBuilder> jsonBuilderList) {
        items.add(new JsonItem(fieldName, null, JsonToken.START_ARRAY));
        for (JSONBuilder builderItem : jsonBuilderList) {
            this.addItem(null, builderItem);
        }
        items.add(new JsonItem(fieldName, null, JsonToken.END_ARRAY));
        return this;
    }

    /**
     * Use for add raw array of values "fieldName" : ["1","2","3"]
     * @param fieldName - name of array
     * @param listOfObjects - list of array items
     * @param valueType - type of arrays items (support JsonToken.VALUE_STRING and JsonToken.VALUE_NUMBER_INT)
     * @return this
     */
    public JSONBuilder addListOfValues(String fieldName,List<Object> listOfObjects,JsonToken valueType){
        if (valueType.equals(JsonToken.VALUE_STRING) || valueType.equals(JsonToken.VALUE_NUMBER_INT)) {
            items.add(new JsonItem(fieldName, null, JsonToken.START_ARRAY));
            for (Object listItem : listOfObjects) {
                items.add(new JsonItem(null, listItem, valueType));
            }
            items.add(new JsonItem(fieldName, null, JsonToken.END_ARRAY));
            return this;
        }else {
            throw new RAFIllegalState("Unsupported token type - "+valueType,JSONBuilder.class);
        }
    }

    /**
     * Add array of {@link JSONBuilderAware} instances which means that JSONBuilder "knows" how
     * to convert them into items
     * @param fieldName - field name for JSON node which will store all data
     * @param objectsList - list of JSONBuilderAware POJO's to convert
     * @return this
     */
    public JSONBuilder addArray(String fieldName, List<JSONBuilderAware> objectsList) {
        items.add(new JsonItem(fieldName, null, JsonToken.START_ARRAY));
        for (JSONBuilderAware item : objectsList) {
            this.addItem(null, item.addItem());
        }
        items.add(new JsonItem(fieldName, null, JsonToken.END_ARRAY));
        return this;
    }

    /**
     * main method which suppose to generate JSON according previously inserted data
     * @return generated JSON String
     */
    public String build() {
        if (items.isEmpty()) {
            return "";
        }
        String json;
        try (OutputStream byteArrayOutputStream = new ByteArrayOutputStream();
             JsonGenerator generator = JSON_FACTORY.createJsonGenerator(byteArrayOutputStream)) {
            generator.writeStartObject();
            for (JsonItem item : items) {
                switch (item.getToken()) {
                    case VALUE_STRING:
                        if(item.getFieldName()==null){
                            generator.writeString(item.getValue().toString());
                        }else {
                            generator.writeStringField(item.getFieldName(), item.getValue().toString());
                        }
                        break;
                    case VALUE_NUMBER_INT:
                        if (item.getFieldName() == null) {
                            generator.writeNumber(Integer.valueOf(item.getValue().toString()));
                        }else {
                            generator.writeNumberField(item.getFieldName(), Integer.valueOf(item.getValue().toString()));
                        }
                        break;
                    case START_OBJECT:
                        if (StringUtils.isBlank(item.getFieldName())) {
                            generator.writeStartObject();
                        } else {
                            generator.writeObjectFieldStart(item.getFieldName());
                        }
                        break;
                    case END_OBJECT:
                        generator.writeEndObject();
                        break;
                    case VALUE_TRUE:
                        generator.writeBooleanField(item.getFieldName(), true);
                        break;
                    case VALUE_FALSE:
                        generator.writeBooleanField(item.getFieldName(), false);
                        break;
                    case START_ARRAY:
                        generator.writeFieldName(item.getFieldName());
                        generator.writeStartArray();
                        break;
                    case END_ARRAY:
                        generator.writeEndArray();
                        break;
                    case VALUE_NULL:
                        generator.writeNullField(item.fieldName);
                        break;
                    default:
                        throw new RAFIllegalState("Not supported token type - " + item.getToken(), JSONBuilder.class);
                }
            }
            generator.writeEndObject();
            generator.flush();
            json = byteArrayOutputStream.toString();
        } catch (IOException ignored) {
            throw new RAFException(ignored, JSONBuilder.class);
        }
        return json;
    }

    /**
     * search over inserted field names to find a match
     * Note: in case several fields with same name exists first one found will be returned
     * @param fieldName - target field name
     * @return {@link Object} representing value of field with specified name
     */
    public Object getItemValueByFieldName(String fieldName) {
        for (JsonItem item : this.items) {
            if (item.getFieldName().equals(fieldName)) {
                return item.getValue();
            }
        }
        return null;
    }

    /**
     *  private class representing structure used to store inserted JSON nodes
     */
    private static class JsonItem {

        JsonToken token;
        Object value;
        String fieldName;

        private JsonItem(String fieldName, Object value, JsonToken token) {
            if (token == null) {
                throw new RAFIllegalState("Token can not be null", JsonItem.class);
            }
            if (((token == JsonToken.VALUE_STRING) || (token == JsonToken.VALUE_NUMBER_INT)) && (value == null)) {
                throw new RAFIllegalState("Value can not be null", JsonItem.class);
            }
            this.token = token;
            this.value = value;
            this.fieldName = fieldName;
        }

        Object getValue() {
            return value;
        }

        String getFieldName() {
            return fieldName;
        }

        JsonToken getToken() {
            return token;
        }
    }

}
