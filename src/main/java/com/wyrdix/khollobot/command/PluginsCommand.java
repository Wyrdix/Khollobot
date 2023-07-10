package com.wyrdix.khollobot.command;

import com.wyrdix.khollobot.KholloBot;
import com.wyrdix.khollobot.plugin.DefaultPlugin;
import com.wyrdix.khollobot.plugin.Plugin;
import com.wyrdix.khollobot.plugin.PluginInfo;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

public class PluginsCommand extends KCommandImpl {

    private static final PluginsCommand INSTANCE = new PluginsCommand();
    private static final String ID = "plugins";
    private static final String DESCRIPTION = "Renvoie la liste des plugins actifs";

    public PluginsCommand() {
        super(DefaultPlugin.class, ID, DESCRIPTION);
    }

    public static PluginsCommand getInstance() {
        return INSTANCE;
    }

    @Override
    public void execute(SlashCommandInteractionEvent event) {
        EmbedBuilder builder = new EmbedBuilder();
        builder.setTitle("Liste des plugins");

        for (Plugin plugin : KholloBot.getPluginMap().values()) {
            PluginInfo info = plugin.getInfo();

            builder.addField(String.format("%s                               __État : %s__", info.name(), plugin.isEnable() ? "Activé":"Désactivé"),
                    String.format("**Auteur : %s** *Version : %s*" , String.join(" ", info.author()), info.version()), false);
        }

        event.replyEmbeds(builder.build()).queue();
    }
}
