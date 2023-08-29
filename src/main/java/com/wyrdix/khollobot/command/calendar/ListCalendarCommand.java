package com.wyrdix.khollobot.command.calendar;

import com.wyrdix.khollobot.KholloBot;
import com.wyrdix.khollobot.command.KCommandImpl;
import com.wyrdix.khollobot.plugin.CalendarPlugin;
import com.wyrdix.khollobot.plugin.DefaultPlugin;
import com.wyrdix.khollobot.plugin.calendar.CalendarInstance;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

public class ListCalendarCommand extends KCommandImpl {

    private static final ListCalendarCommand INSTANCE = new ListCalendarCommand();
    private static final String ID = "calendar_list";
    private static final String DESCRIPTION = "Liste les configurations de calendrier";

    public ListCalendarCommand() {
        super(DefaultPlugin.class, ID, DESCRIPTION);
    }

    public static ListCalendarCommand getInstance() {
        return INSTANCE;
    }

    @Override
    public void execute(SlashCommandInteractionEvent event) {
        EmbedBuilder builder = new EmbedBuilder();
        builder.setTitle("Liste des configurations");

        for (CalendarInstance instance : KholloBot.getPlugin(CalendarPlugin.class).getInstances().values()) {
            builder.addField(String.format("%s : ", instance.id()), String.format("(%s)", instance.getTemplates().keySet().stream().reduce((a, b) -> a + ", " + b).orElse("NONE")), false);
        }

        event.replyEmbeds(builder.build()).queue();
    }
}

