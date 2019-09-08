package com.costs.newcosts.stores.abstraction;

import java.util.HashMap;
import java.util.Map;

/**
 * TODO: Add a class header comment
 */
public class Payload {
    private Map<String, Object> mMap;


    public Payload() {
        mMap = new HashMap<>();
    }

    public void set(String key, Object value) {
        mMap.put(key, value);
    }

    public Object get(String key) {
        if (mMap.containsKey(key)) {
            return mMap.get(key);
        }

        return null;
    }
}
