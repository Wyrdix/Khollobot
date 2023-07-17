package com.wyrdix.khollobot;


import com.wyrdix.khollobot.command.KCommand;
import com.wyrdix.khollobot.plugin.*;
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
        addPlugin(new IdentityPlugin());
        addPlugin(new MailPlugin());
        addPlugin(new CafeteriaPlugin());

        getJDA().addEventListener(new ListenerAdapter() {
            @Override
            public void onSlashCommandInteraction(@NotNull SlashCommandInteractionEvent event) {
                long commandId = event.getCommandIdLong();

                KCommand command = KCommand.COMMAND_MAP_BY_ID.get(commandId);
                if (command != null) command.execute(event);
            }
        });

        PLUGIN_MAP.values().stream().filter(Plugin::isEnable).forEach(Plugin::onEnable);
        getJDA().updateCommands().addCommands(KCommand.COMMAND_MAP.values().stream().map(KCommand::getData).toList()).queue(a->a.forEach(s->{
            String name = s.getFullCommandName();
            long id = s.getIdLong();

            KCommand.COMMAND_MAP_BY_ID.put(id, KCommand.COMMAND_MAP.getOrDefault(name, null));
        }));
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
