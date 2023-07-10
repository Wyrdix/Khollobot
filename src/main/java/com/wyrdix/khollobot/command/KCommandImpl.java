package com.wyrdix.khollobot.command;

import com.wyrdix.khollobot.KholloBot;
import com.wyrdix.khollobot.plugin.Plugin;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.Commands;

public class KCommandImpl implements KCommand {

    private final String id;
    private final String description;
    private final Plugin plugin;

    public KCommandImpl(Plugin plugin, String id, String description) {
        this.id = id;
        this.description = description;
        this.plugin = plugin;

        COMMAND_MAP.put(id, this);
    }

    public KCommandImpl(Class<? extends Plugin> pluginClazz, String id, String description) {
        this.id = id;
        this.description = description;
        this.plugin = KholloBot.getPlugin(pluginClazz);

        COMMAND_MAP.put(id, this);
    }

    @Override
    public CommandData getData() {
        return Commands.slash(id, description);
    }

    @Override
    public String command() {
        return id;
    }

    @Override
    public Plugin plugin() {
        return plugin;
    }

    @Override
    public void execute(SlashCommandInteractionEvent event) {

    }
}
