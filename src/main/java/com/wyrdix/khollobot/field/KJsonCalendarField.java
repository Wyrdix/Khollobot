package com.wyrdix.khollobot.field;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import com.wyrdix.khollobot.KUser;

import java.io.IOException;
import java.util.Calendar;
import java.util.Locale;
import java.util.Optional;

public class KJsonCalendarField extends KField<Calendar> {

    private final KJsonElementField jsonElementField;

    public KJsonCalendarField(String path) {

        jsonElementField = new KJsonElementField(path);
    }

    @Override
    public Calendar get(KUser user) {
        JsonElement element = jsonElementField.get(user);
        return Optional.ofNullable(element).filter(JsonElement::isJsonPrimitive)
                .map(JsonElement::getAsLong).map(time-> {
                    Calendar instance = Calendar.getInstance(Locale.FRANCE);
                    instance.setTimeInMillis(time);
                    return instance;
                }).orElse(null);
    }

    @Override
    public void set(KUser user, Calendar value) {
        JsonPrimitive jsValue = new JsonPrimitive(value.getTimeInMillis());
        jsonElementField.set(user, jsValue);
        try {
            user.save();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
