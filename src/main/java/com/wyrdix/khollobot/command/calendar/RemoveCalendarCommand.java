package com.wyrdix.khollobot.command.calendar;

import com.wyrdix.khollobot.KholloBot;
import com.wyrdix.khollobot.command.KCommandImpl;
import com.wyrdix.khollobot.plugin.CalendarPlugin;
import com.wyrdix.khollobot.plugin.DefaultPlugin;
import com.wyrdix.khollobot.plugin.calendar.CalendarInstance;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;
import net.dv8tion.jda.api.utils.FileUpload;

import java.io.File;
import java.io.FileInputStream;
import java.util.Map;

public class RemoveCalendarCommand extends KCommandImpl {

    private static final RemoveCalendarCommand INSTANCE = new RemoveCalendarCommand();
    private static final String ID = "calendar_remove";
    private static final String DESCRIPTION = "Supprime une configuration de calendrier";

    public RemoveCalendarCommand() {
        super(DefaultPlugin.class, ID, DESCRIPTION);
    }

    public static RemoveCalendarCommand getInstance() {
        return INSTANCE;
    }

    @Override
    public SlashCommandData getData() {
        return super.getData().addOption(OptionType.STRING, "calendar", "Calendrier que vous voulez supprimer ", true);
    }

    @Override
    public void execute(SlashCommandInteractionEvent event) {
        String calendar = event.getOption("calendar", OptionMapping::getAsString);
        assert calendar != null;
        Map<String, CalendarInstance> instances = KholloBot.getPlugin(CalendarPlugin.class).getInstances();

        if (!instances.containsKey(calendar)) {
            event.reply("Ce calendrier n'existe pas").queue();
            return;
        }

        File file = new File(CalendarInstance.CALENDAR_FOLDER, instances.get(calendar).id() + ".cal");
        if (!file.exists()) {
            event.reply("Ce calendrier n'existe pas").queue();
            return;
        }
        try (FileUpload upload = FileUpload.fromData(new FileInputStream(file), file.getName())) {
            instances.remove(calendar);
            //noinspection ResultOfMethodCallIgnored
            event.reply("Le calendrier a bien été supprimé (en voici une copie)").addFiles(upload).queue((s) -> file.delete());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}

