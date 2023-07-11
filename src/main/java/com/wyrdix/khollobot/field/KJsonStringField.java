package com.wyrdix.khollobot.field;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import com.wyrdix.khollobot.KUser;

import java.io.IOException;
import java.util.Optional;

public class KJsonStringField extends KField<String> {

    private final KJsonElementField jsonElementField;

    public KJsonStringField(String path) {

        jsonElementField = new KJsonElementField(path);
    }

    @Override
    public String get(KUser user) {
        JsonElement element = jsonElementField.get(user);
        return Optional.ofNullable(element).filter(JsonElement::isJsonPrimitive)
                .map(JsonElement::getAsString).orElse(null);
    }

    @Override
    public void set(KUser user, String value) {
        JsonPrimitive jsValue = new JsonPrimitive(value);
        jsonElementField.set(user, jsValue);
        try {
            user.save();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
