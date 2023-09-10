package com.wyrdix.khollobot.command.calendar;

import com.wyrdix.khollobot.KholloBot;
import com.wyrdix.khollobot.command.KCommandImpl;
import com.wyrdix.khollobot.plugin.CalendarPlugin;
import com.wyrdix.khollobot.plugin.DefaultPlugin;
import com.wyrdix.khollobot.plugin.IdentityPlugin;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

public class GenerateGroupsCommand extends KCommandImpl {

    private static final GenerateGroupsCommand INSTANCE = new GenerateGroupsCommand();
    private static final String ID = "calendar_generate_groups";
    private static final String DESCRIPTION = "Permet de générer les groupes nécessaires pour les calendriers";

    public GenerateGroupsCommand() {
        super(DefaultPlugin.class, ID, DESCRIPTION);
    }

    public static GenerateGroupsCommand getInstance() {
        return INSTANCE;
    }

    @Override
    public void execute(SlashCommandInteractionEvent event) {
        User user = event.getUser();

        if (!IdentityPlugin.isBotAdmin(user.getIdLong())) {
            event.reply("Seuls les administrateurs du bot peuvent modifier cette propriété.").queue();
            return;
        }

        KholloBot.getPlugin(CalendarPlugin.class).generateGroups();
        event.reply("Les groupes ont été générés !").queue();
    }
}
