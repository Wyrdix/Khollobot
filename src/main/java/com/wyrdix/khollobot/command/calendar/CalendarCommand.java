package com.wyrdix.khollobot.command.calendar;

import com.wyrdix.khollobot.KholloBot;
import com.wyrdix.khollobot.command.KCommandImpl;
import com.wyrdix.khollobot.plugin.CalendarPlugin;
import com.wyrdix.khollobot.plugin.DefaultPlugin;
import com.wyrdix.khollobot.plugin.Plugin;
import com.wyrdix.khollobot.plugin.PluginInfo;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

public class CalendarCommand extends KCommandImpl {

    private static final CalendarCommand INSTANCE = new CalendarCommand();
    private static final String ID = "calendar";
    private static final String DESCRIPTION = "Renvoie votre emploie du temps de la semaine";

    public CalendarCommand() {
        super(DefaultPlugin.class, ID, DESCRIPTION);
    }

    public static CalendarCommand getInstance() {
        return INSTANCE;
    }

    @Override
    public void execute(SlashCommandInteractionEvent event) {
        CalendarPlugin.sendCalendar(event, 1); //TODO get the current week
    }
}
