package com.wyrdix.data.utils;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

public class JsonUtils {
    private JsonUtils() {
    }

    public static JsonObject deepClone(JsonObject object) {
        Gson gson = new Gson();
        String json = gson.toJson(object);
        return gson.fromJson(json, JsonObject.class);
    }
}
