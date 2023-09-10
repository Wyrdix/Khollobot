package com.wyrdix.khollobot.command.calendar;

import com.wyrdix.khollobot.GlobalConfig;
import com.wyrdix.khollobot.command.KCommandImpl;
import com.wyrdix.khollobot.plugin.CalendarPlugin;
import com.wyrdix.khollobot.plugin.DefaultPlugin;
import com.wyrdix.khollobot.plugin.IdentityPlugin;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;

import java.util.Objects;

public class WeekSetCommand extends KCommandImpl {

    private static final WeekSetCommand INSTANCE = new WeekSetCommand();
    private static final String ID = "week_set";
    private static final String DESCRIPTION = "Définie le numéro de semaine actuel";

    public WeekSetCommand() {
        super(DefaultPlugin.class, ID, DESCRIPTION);
    }

    public static WeekSetCommand getInstance() {
        return INSTANCE;
    }

    @Override
    public SlashCommandData getData() {
        return super.getData().addOption(OptionType.INTEGER, "week", "Semaine actuelle", true);
    }

    @Override
    public void execute(SlashCommandInteractionEvent event) {
        User user = event.getUser();
        if (!IdentityPlugin.isBotAdmin(user.getIdLong())) {
            event.reply("Seuls les administrateurs du bot peuvent modifier cette propriété.").queue();
            return;
        }

        GlobalConfig globalConfig = GlobalConfig.getGlobalConfig();
        CalendarPlugin.CalendarPluginConfig config = (CalendarPlugin.CalendarPluginConfig) globalConfig.getConfig(CalendarPlugin.class);
        int currentWeek = CalendarPlugin.getCurrentWeek() - config.offset;
        int newOffset = Objects.requireNonNull(event.getOption("week")).getAsInt() - currentWeek;

        config.offset = newOffset;
        event.reply("La semaine a été modifié à %s.".formatted(config.offset)).queue();
        globalConfig.save();
    }
}

