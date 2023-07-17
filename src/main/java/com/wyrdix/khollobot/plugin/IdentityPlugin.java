package com.wyrdix.khollobot.plugin;

import com.google.gson.*;
import com.wyrdix.khollobot.GlobalConfig;
import com.wyrdix.khollobot.command.id.BirthCommand;
import com.wyrdix.khollobot.command.id.IdCommand;
import com.wyrdix.khollobot.command.id.PromoteCommand;
import com.wyrdix.khollobot.field.KField;
import com.wyrdix.khollobot.field.KJsonCalendarField;
import com.wyrdix.khollobot.field.KJsonStringField;

import java.lang.reflect.Type;
import java.util.Calendar;
import java.util.List;
import java.util.stream.Collectors;

@PluginInfo(id = "id", name = "Identifiant", version = "1.0-SNAPSHOT", author = "Wyrdix", config = IdentityPlugin.IdentityPluginConfig.class)
public class IdentityPlugin implements Plugin {

    public static final KField<String> FIRST_NAME = new KJsonStringField("id.first_name"){
        @Override
        public String sanitize(String value) {
            String lower = value.toLowerCase();
            char c = Character.toUpperCase(value.charAt(0));
            return c + lower.substring(1);
        }
    };
    public static final KField<String> LAST_NAME = new KJsonStringField("id.last_name"){
        @Override
        public String sanitize(String value) {
            return value.toUpperCase();
        }
    };

    public static final KField<Calendar> BIRTH_DATE = new KJsonCalendarField("id.birth_date");

    public static boolean isBotAdmin(long id){
        return ((IdentityPluginConfig) GlobalConfig.getGlobalConfig().getConfig(IdentityPlugin.class)).admins.contains(id);
    }

    @Override
    public void onEnable() {
        addCommand(IdCommand.getInstance());
        addCommand(BirthCommand.getInstance());
        addCommand(PromoteCommand.getInstance());
    }

    @Override
    public void save() {

    }

    public static class IdentityPluginConfig extends PluginConfig implements JsonDeserializer<IdentityPluginConfig> {
        public List<Long> admins;

        public IdentityPluginConfig(PluginConfig config) {
            super(config);
        }

        @Override
        public IdentityPluginConfig deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {

            PluginConfig config = context.deserialize(json, PLUGIN_CONFIG_TYPE);
            IdentityPluginConfig pluginConfig = new IdentityPluginConfig(config);

            JsonArray admins = json.getAsJsonObject().getAsJsonArray("admins");

            pluginConfig.admins = admins.asList().stream().map(JsonElement::getAsLong).collect(Collectors.toList());

            return pluginConfig;
        }
    }
}
