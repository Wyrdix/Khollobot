package com.wyrdix.khollobot.plugin;

import com.wyrdix.khollobot.command.def.PluginsCommand;

@PluginInfo(id = "default", name = "Défaut", version = "1.0-SNAPSHOT", author = "Wyrdix")
public class DefaultPlugin implements Plugin {

    @Override
    public void onEnable() {
        addCommand(PluginsCommand.getInstance());
    }

    @Override
    public void save() {

    }
}
