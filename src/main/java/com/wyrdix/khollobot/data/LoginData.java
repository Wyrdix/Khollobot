package com.wyrdix.khollobot.data;

import com.google.gson.JsonObject;
import com.wyrdix.data.json.DataJsonNode;

public class LoginData extends DataJsonNode {

    private final String token;

    public LoginData(JsonObject object) {
        super(object);

        token = object.get("token").getAsString();
    }

    public String getToken() {
        return token;
    }
}
