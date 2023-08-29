package com.wyrdix.khollobot.command.calendar;

import com.wyrdix.khollobot.KholloBot;
import com.wyrdix.khollobot.command.KCommandImpl;
import com.wyrdix.khollobot.plugin.CalendarPlugin;
import com.wyrdix.khollobot.plugin.DefaultPlugin;
import com.wyrdix.khollobot.plugin.IdentityPlugin;
import com.wyrdix.khollobot.plugin.calendar.CalendarInstance;
import com.wyrdix.khollobot.plugin.calendar.impl.CalendarInstanceImpl;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Map;
import java.util.concurrent.ExecutionException;

public class AddCalendarCommand extends KCommandImpl {

    private static final AddCalendarCommand INSTANCE = new AddCalendarCommand();
    private static final String ID = "calendar_add";
    private static final String DESCRIPTION = "Ajoute un configuration de calendrier";

    public AddCalendarCommand() {
        super(DefaultPlugin.class, ID, DESCRIPTION);
    }

    public static AddCalendarCommand getInstance() {
        return INSTANCE;
    }

    @Override
    public SlashCommandData getData() {
        return super.getData().addOption(OptionType.ATTACHMENT, "calendar", "Calendrier que vous voulez ajouter ", true);
    }

    @Override
    public void execute(SlashCommandInteractionEvent event) {
        User user = event.getUser();

        if (!IdentityPlugin.isBotAdmin(user.getIdLong())) {
            event.reply("Seuls les administrateurs du bot peuvent modifier cette propriété.").queue();
            return;
        }
        Message.Attachment attachment = event.getOption("calendar", OptionMapping::getAsAttachment);
        assert attachment != null;
        String name = attachment.getFileName();
        if (!attachment.getFileName().endsWith(".cal")) {
            event.reply("Le calendrier doit avoir une extension en .cal.").queue();
            return;
        }
        name = name.substring(0, name.lastIndexOf(".cal"));

        Map<String, CalendarInstance> instances = KholloBot.getPlugin(CalendarPlugin.class).getInstances();
        if (instances.containsKey(name)) {
            event.reply("Un calendrier avec ce nom existe déjà.").queue();
            return;
        }

        try {
            Files.copy(attachment.getProxy().download().get(), new File(CalendarInstance.CALENDAR_FOLDER, attachment.getFileName()).toPath());
        } catch (InterruptedException | ExecutionException | IOException e) {
            throw new RuntimeException(e);
        }
        System.out.println("Loading calendar : " + name);
        CalendarInstanceImpl instance = new CalendarInstanceImpl(name);
        instances.put(name, instance);
        instance.load();

        event.reply("Le calendrier %s a été ajouté !".formatted(name)).queue();
    }
}

