package com.wyrdix.khollobot.command;

import com.wyrdix.khollobot.plugin.Plugin;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;

import java.util.HashMap;
import java.util.Map;

public interface KCommand {

    Map<String, KCommand> COMMAND_MAP = new HashMap<>();

    CommandData getData();
    String command();
    Plugin plugin();

    void execute(SlashCommandInteractionEvent event);
}
