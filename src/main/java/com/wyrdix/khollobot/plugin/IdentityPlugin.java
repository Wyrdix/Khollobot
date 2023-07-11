package com.wyrdix.khollobot.plugin;

import com.wyrdix.khollobot.command.IdCommand;
import com.wyrdix.khollobot.field.KField;
import com.wyrdix.khollobot.field.KJsonStringField;

@PluginInfo(id = "id", name = "Identifiant", version = "1.0-SNAPSHOT", author = "Wyrdix")
public class IdentityPlugin implements Plugin {

    public static final KField<String> FIRST_NAME = new KJsonStringField("user.first_name"){
        @Override
        public String sanitize(String value) {
            String lower = value.toLowerCase();
            char c = Character.toUpperCase(value.charAt(0));
            return c + lower.substring(1);
        }
    };
    public static final KField<String> LAST_NAME = new KJsonStringField("user.last_name"){
        @Override
        public String sanitize(String value) {
            return value.toUpperCase();
        }
    };

    @Override
    public void onEnable() {
        addCommand(IdCommand.getInstance());
    }

    @Override
    public void save() {

    }
}
