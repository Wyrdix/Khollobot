package com.wyrdix.khollobot.field;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.wyrdix.khollobot.KUser;
import com.wyrdix.khollobot.plugin.GroupPlugin;

import java.util.Collections;
import java.util.Map;
import java.util.stream.Collectors;

public class KJsonGroupsField extends KField<Map<String, GroupPlugin.GroupConfig>> {

    private final KJsonElementField jsonElementField;

    public KJsonGroupsField(String path) {

        jsonElementField = new KJsonElementField(path);
    }

    @Override
    public Map<String, GroupPlugin.GroupConfig> get(KUser user) {
        JsonElement element = jsonElementField.get(user);
        if (element == null || !element.isJsonArray() || element.isJsonNull()) return Collections.emptyMap();

        return element.getAsJsonArray().asList().stream().map(GroupPlugin.GroupConfig::deserialize).collect(Collectors.toMap(s -> s.name, s -> s));
    }

    @Override
    public void set(KUser user, Map<String, GroupPlugin.GroupConfig> value) {
        JsonArray array = new JsonArray();

        value.values().forEach(s -> array.add(GroupPlugin.GroupConfig.serialize(s)));

        jsonElementField.set(user, array);
    }
}
