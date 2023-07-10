package com.wyrdix.khollobot;


import com.wyrdix.khollobot.command.KCommand;
import com.wyrdix.khollobot.plugin.DefaultPlugin;
import com.wyrdix.khollobot.plugin.Plugin;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

public class KholloBot {

    private static final Map<Class<? extends Plugin>, Plugin> PLUGIN_MAP = new HashMap<>();
    private static net.dv8tion.jda.api.JDA JDA = null;

    public static Map<Class<? extends Plugin>, Plugin> getPluginMap() {
        return PLUGIN_MAP;
    }

    public static void main(String[] args) {
        setJDA(JDABuilder.createDefault(LoginConfig.getLogin().getDiscordToken()).build());

        addPlugin(new DefaultPlugin());

        getJDA().addEventListener(new ListenerAdapter() {
            @Override
            public void onSlashCommandInteraction(@NotNull SlashCommandInteractionEvent event) {
                String commandId = event.getInteraction().getCommandString().substring(1);

                KCommand command = KCommand.COMMAND_MAP.get(commandId);
                if (command != null) command.execute(event);
            }
        });

        PLUGIN_MAP.values().stream().filter(Plugin::isEnable).forEach(Plugin::onEnable);
    }

    private static void addPlugin(Plugin plugin) {
        PLUGIN_MAP.put(plugin.getClass(), plugin);
    }

    public static net.dv8tion.jda.api.JDA getJDA() {
        return JDA;
    }

    private static void setJDA(net.dv8tion.jda.api.JDA JDA) {
        KholloBot.JDA = JDA;
    }

    @SuppressWarnings("unchecked")
    public static <T extends Plugin> T getPlugin(Class<T> pluginClazz) {
        return (T) PLUGIN_MAP.get(pluginClazz);
    }
}
