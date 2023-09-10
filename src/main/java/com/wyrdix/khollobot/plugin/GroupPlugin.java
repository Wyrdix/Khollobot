package com.wyrdix.khollobot.plugin;

import com.google.gson.*;
import com.wyrdix.khollobot.command.group.AddGroupCommand;
import com.wyrdix.khollobot.command.group.GroupCommand;
import com.wyrdix.khollobot.command.group.GroupsCommand;
import com.wyrdix.khollobot.field.KField;
import com.wyrdix.khollobot.field.KJsonGroupsField;

import java.lang.reflect.Type;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

@PluginInfo(id = "group", name = "Groupe", version = "1.0-SNAPSHOT", author = "Wyrdix", config = GroupPlugin.GroupPluginConfig.class)
public class GroupPlugin implements Plugin {

    public static final KField<Map<String, GroupConfig>> USER_GROUPS = new KJsonGroupsField("groups");

    @Override
    public void onEnable() {
        addCommand(AddGroupCommand.getInstance());
        addCommand(GroupCommand.getInstance());
        addCommand(GroupsCommand.getInstance());
    }

    @Override
    public void save() {

    }

    public static final class GroupPluginConfig extends PluginConfig implements JsonDeserializer<GroupPluginConfig> {

        public final Set<GroupConfig> groups = new HashSet<>();

        public GroupPluginConfig(PluginConfig config) {
            super(config);
        }

        @Override
        public GroupPluginConfig deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {

            PluginConfig config = context.deserialize(json, PLUGIN_CONFIG_TYPE);
            GroupPluginConfig pluginConfig = new GroupPluginConfig(config);

            JsonElement element = json.getAsJsonObject().get("groups");
            if (element != null && element.isJsonArray()) {
                JsonArray groups = json.getAsJsonObject().getAsJsonArray("groups");
                groups.forEach(s -> this.groups.add(GroupConfig.deserialize(s)));
            }

            return pluginConfig;
        }

        public void add(String group, int subgroups) {
            groups.add(new GroupConfig(group, subgroups));
        }
    }

    public static final class GroupConfig {
        public String name;
        public int subgroup;

        public GroupConfig(String name, int subgroups) {
            this.name = name;
            this.subgroup = subgroups;
        }

        public GroupConfig() {
        }

        public static JsonElement serialize(GroupConfig s) {
            JsonObject object = new JsonObject();
            object.addProperty("name", s.name);
            object.addProperty("subgroup", s.subgroup);

            return object;
        }

        public static GroupConfig deserialize(JsonElement element) {
            assert element.isJsonObject();

            String name = element.getAsJsonObject().get("name").getAsString();
            int subgroup = element.getAsJsonObject().get("subgroup").getAsInt();

            return new GroupConfig(name, subgroup);
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            GroupConfig that = (GroupConfig) o;
            return Objects.equals(name, that.name);
        }

        @Override
        public int hashCode() {
            return Objects.hash(name);
        }
    }
}
