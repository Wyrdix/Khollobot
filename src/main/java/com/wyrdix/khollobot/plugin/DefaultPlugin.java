package com.wyrdix.khollobot.plugin;

import com.wyrdix.khollobot.command.PluginsCommand;

@PluginInfo(id = "default", name = "Défaut", version = "1.0-SNAPSHOT", author = "Wyrdix")
public class DefaultPlugin implements Plugin {

    @Override
    public void onEnable() {
        getJda().upsertCommand(PluginsCommand.getInstance().getData()).queue();
    }

    @Override
    public void save() {

    }
}
