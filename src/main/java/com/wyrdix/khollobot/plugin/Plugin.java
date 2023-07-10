package com.wyrdix.khollobot.plugin;

import com.wyrdix.khollobot.GlobalConfig;
import com.wyrdix.khollobot.KholloBot;
import net.dv8tion.jda.api.JDA;

public interface Plugin {
    void onEnable();

    void save();

    default boolean isEnable() {
        return GlobalConfig.getGlobalConfig().getConfig(getClass()).isEnabled();
    }

    default JDA getJda() {
        return KholloBot.getJDA();
    }

    default PluginInfo getInfo(){
        PluginInfo info = getClass().getAnnotation(PluginInfo.class);
        if (info == null) throw new RuntimeException("Plugin class should be annotated with @PluginInfo");
        return info;
    }

    class PluginConfig {
        boolean enabled = true;

        public PluginConfig() {
        }

        public boolean isEnabled() {
            return enabled;
        }
    }
}
