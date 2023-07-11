package com.wyrdix.khollobot.command;

import com.wyrdix.khollobot.KholloBot;
import com.wyrdix.khollobot.plugin.Plugin;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;

public class KCommandImpl implements KCommand {

    private final String id;
    private final String description;
    private final Plugin plugin;

    private Long registered_id = null;

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
    public SlashCommandData getData() {
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
    public long getRegisteredId() {
        if(registered_id == null) throw new RuntimeException("Could not get registered id as this command isn't registered yet");
        return registered_id;
    }

    @Override
    public void setRegisteredId(long idLong) {
        if(this.registered_id != null) throw new RuntimeException("This Command is already registered");
        this.registered_id = idLong;

        COMMAND_MAP_BY_ID.put(idLong, this);
    }

    @Override
    public void execute(SlashCommandInteractionEvent event) {

    }
}
