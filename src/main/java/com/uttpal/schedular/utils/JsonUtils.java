package com.uttpal.schedular.utils;

import com.google.gson.Gson;
import org.springframework.stereotype.Component;

/**
 * @author Uttpal
 */
@Component
public class JsonUtils {
    private Gson gson = new Gson();

    public <T> T fromJson(String jsonString, Class<T> tClass) {
        return gson.fromJson(jsonString, tClass);
    }

    public String toJson(Object obj) {
        return gson.toJson(obj);
    }
}
