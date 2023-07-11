package com.wyrdix.khollobot.field;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import com.wyrdix.khollobot.KUser;

import java.io.IOException;
import java.util.Optional;

public class KJsonIntegerField extends KField<Integer> {

    private final KJsonElementField jsonElementField;

    public KJsonIntegerField(String path) {

        jsonElementField = new KJsonElementField(path);
    }

    @Override
    public Integer get(KUser user) {
        JsonElement element = jsonElementField.get(user);
        return Optional.ofNullable(element).filter(JsonElement::isJsonPrimitive)
                .map(JsonElement::getAsInt).orElse(null);
    }

    @Override
    public void set(KUser user, Integer value) {
        JsonPrimitive jsValue = new JsonPrimitive(value);
        jsonElementField.set(user, jsValue);
        try {
            user.save();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
