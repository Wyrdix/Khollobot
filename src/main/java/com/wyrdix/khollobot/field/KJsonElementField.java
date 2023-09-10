package com.wyrdix.khollobot.field;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.wyrdix.khollobot.KUser;

public class KJsonElementField extends KField<JsonElement> {

    private final String path;

    public KJsonElementField(String path) {
        this.path = path;
    }

    @Override
    public JsonElement get(KUser user) {
        JsonObject data = user.getData();
        data = getDirectParent(data, path);
        return data.get(path.substring(path.lastIndexOf('.') + 1));
    }

    @Override
    public void set(KUser user, JsonElement value) {
        JsonObject data = user.getData();
        data = getDirectParent(data, path);
        data.add(path.substring(path.lastIndexOf('.') + 1), value);
    }

    private JsonObject getDirectParent(JsonObject object, String path) {
        if (!path.contains(".")) return object;
        String value = path.substring(0, path.indexOf('.'));
        String rec = path.substring(path.indexOf('.') + 1);

        if (object.has(value) && object.get(value).isJsonObject()) {
            return getDirectParent(object.get(value).getAsJsonObject(), rec);
        }

        JsonObject jsObj = new JsonObject();
        JsonObject parent = getDirectParent(jsObj, rec);

        object.add(value, jsObj);

        return parent;
    }
}
