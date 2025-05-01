//Copyright (c) 2023 Vereign AG [https://www.vereign.com]

package utils;

import exceptions.RAFException;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.util.Properties;

/**
 * Helper for reading value from java-like properties files
 */
public final class RAFProperties {

    private static final Properties properties = new Properties();

    private static Logger log = LogManager.getLogger(RAFProperties.class.getSimpleName());

    static {
        try {
            properties.load(RAFProperties.class.getResourceAsStream("/main.properties"));
        } catch (IOException e) {
            throw new RAFException(e, RAFProperties.class);
        }
    }

    private RAFProperties() {
    }

    /**
     * Treat value as integer
     *
     * @param keyName      - key name of integer value
     * @param defaultValue - value to return in case value not exists or not digit
     * @return integer representation of value if key exists and value is digit or default otherwise
     */
    public static Integer getIntegerPropertyOrReturnDefault(String keyName, Integer defaultValue) {
        Integer value = getIntegerProperty(keyName);
        if (value == null) {
            log.debug("Key '{}' not exists return default {}", keyName, defaultValue);
            return defaultValue;
        }
        return value;
    }

    /**
     * Treat value as integer
     *
     * @param keyName - key name of integer value
     * @return integer representation of value if key exists and value is digit or null otherwise
     */
    public static Integer getIntegerProperty(String keyName) {
        String value = getProperty(keyName);
        if (StringUtils.isBlank(value)) {
            return null;
        }
        if (StringUtils.isNumeric(value)) {
            return Integer.valueOf(value);
        }
        return null;
    }

    /**
     * Read property with defined keyName from properties file
     *
     * @param keyName - name of property to read
     * @return property value or null if no property exists
     */
    public static String getProperty(String keyName) {
        if (properties.containsKey(keyName)) {
            return properties.getProperty(keyName);
        }
        log.debug("Key '{}' not exists return null", keyName);
        return null;
    }

    /**
     * Treat value as boolean
     *
     * @param keyName      - key name of boolean value
     * @param defaultValue - value to return in case value not exists
     * @return true - if value equals '1' , false in all other cases or defaultValue in case key not exists
     */
    public static Boolean getBooleanPropertyOrReturnDefault(String keyName, Boolean defaultValue) {
        Boolean value = getBooleanProperty(keyName);
        if (value == null) {
            log.debug("Key '{}' not exists return default {}", keyName, defaultValue);
            return defaultValue;
        }
        return value;
    }

    /**
     * Treat value as boolean
     *
     * @param keyName - key name of boolean value
     * @return true - if value equals '1' , false in all other cases or null if case not exists
     */
    public static Boolean getBooleanProperty(String keyName) {
        String value = getProperty(keyName);
        if (StringUtils.isBlank(value)) {
            return null;
        }
        if (value.equals("1")) {
            return true;
        }
        return false;
    }

    /**
     * Read property with defined keyName from properties file
     *
     * @param keyName      - name of property to read
     * @param defaultValue - value to return if property not exists
     * @return property value or default if key not exists
     */
    public static String getPropertyOrReturnDefault(String keyName, String defaultValue) {
        String propertyValue = getProperty(keyName);
        if (StringUtils.isEmpty(propertyValue)) {
            log.debug("Key '{}' not exists return default {}", keyName, defaultValue);
            return defaultValue;
        }
        return propertyValue;
    }
}

