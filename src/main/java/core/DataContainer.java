//Copyright (c) 2023 Vereign AG [https://www.vereign.com]

package core;

import java.util.HashMap;
import java.util.Map;

/**
 * The purpose of this class is to store data that needs to be passed between scenario steps. For RID is used only for
 * scenarios steps that are from different packages.
 */
public class DataContainer {
    private Map<String, Object> objectMap;

    public DataContainer() {
        objectMap = new HashMap<>();
    }

    /**
     * Store an object in the data container. This will persist for the entire scenario.
     *
     * @param key    the key used for the object
     * @param object the object that will be stored
     */
    public void addObject(String key, Object object) {
        objectMap.put(key, object);
    }

    /**
     * Retrieve and object previously stored in the data container
     *
     * @param key the key of the object
     * @return the object or null if the key does not exist
     */
    public Object getObject(String key) {
        return objectMap.get(key);
    }
}

