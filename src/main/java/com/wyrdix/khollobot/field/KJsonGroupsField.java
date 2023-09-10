package com.wyrdix.khollobot.field;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.wyrdix.khollobot.KUser;
import com.wyrdix.khollobot.plugin.GroupPlugin;

import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;

public class KJsonGroupsField extends KField<Set<GroupPlugin.GroupConfig>> {

    private final KJsonElementField jsonElementField;

    public KJsonGroupsField(String path) {

        jsonElementField = new KJsonElementField(path);
    }

    @Override
    public Set<GroupPlugin.GroupConfig> get(KUser user) {
        JsonElement element = jsonElementField.get(user);
        if (element == null || !element.isJsonArray() || element.isJsonNull()) return Collections.emptySet();

        return element.getAsJsonArray().asList().stream().map(GroupPlugin.GroupConfig::deserialize).collect(Collectors.toSet());
    }

    @Override
    public void set(KUser user, Set<GroupPlugin.GroupConfig> value) {
        JsonArray array = new JsonArray();

        value.forEach(s -> array.add(GroupPlugin.GroupConfig.serialize(s)));

        jsonElementField.set(user, array);
    }
}
