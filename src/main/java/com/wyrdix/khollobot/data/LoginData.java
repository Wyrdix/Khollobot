package com.wyrdix.khollobot.data;

import com.google.common.io.Files;
import com.google.gson.JsonObject;
import com.wyrdix.data.json.DataJsonNode;
import com.wyrdix.khollobot.KholloBot;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class LoginData extends DataJsonNode {

    private final String token;
    private final JsonObject gmail;


    public LoginData(JsonObject object) {
        super(object);

        token = object.get("token").getAsString();
        gmail = object.get("gmail").getAsJsonObject();
    }

    public String getToken() {
        return token;
    }

    public JsonObject getGmail() {
        return gmail;
    }

    public void save() {
        String raw = collect().toString();
        byte[] bytes = raw.getBytes(StandardCharsets.UTF_8);
        try {
            //noinspection UnstableApiUsage
            Files.write(bytes, KholloBot.LOGIN_FILE);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
