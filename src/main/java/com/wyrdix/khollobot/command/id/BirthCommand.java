package com.wyrdix.khollobot.command.id;

import com.wyrdix.khollobot.KUser;
import com.wyrdix.khollobot.command.KCommandImpl;
import com.wyrdix.khollobot.plugin.DefaultPlugin;
import com.wyrdix.khollobot.plugin.IdentityPlugin;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;

import java.io.IOException;
import java.util.Calendar;
import java.util.Locale;
import java.util.Objects;

public class BirthCommand extends KCommandImpl {

    private static final BirthCommand INSTANCE = new BirthCommand();
    private static final String ID = "birth";
    private static final String DESCRIPTION = "Permet de renseigner sa date de naissance au KholloBot";

    public BirthCommand() {
        super(DefaultPlugin.class, ID, DESCRIPTION);
    }

    public static BirthCommand getInstance() {
        return INSTANCE;
    }

    @Override
    public SlashCommandData getData() {
        return super.getData().addOption(OptionType.INTEGER, "jour", "Jour de votre naissance", true)
                .addOption(OptionType.INTEGER, "mois", "Mois de votre naissance", true)
                .addOption(OptionType.INTEGER, "année", "Année de votre naissance", true);
    }

    @Override
    public void execute(SlashCommandInteractionEvent event) {
        int day = Objects.requireNonNull(event.getOption("jour", OptionMapping::getAsInt));
        int month = Objects.requireNonNull(event.getOption("mois", OptionMapping::getAsInt));
        int year = Objects.requireNonNull(event.getOption("année", OptionMapping::getAsInt));

        if(year < 100) year += 2000;

        Calendar date = Calendar.getInstance(Locale.FRANCE);
        date.clear();
        //noinspection MagicConstant
        date.set(year, month - 1, day, 12, 0);

        KUser user = KUser.getKUser(event.getUser().getIdLong());
        user.set(IdentityPlugin.BIRTH_DATE, date);

        try {
            user.save();
        } catch (IOException e) {
            e.printStackTrace();
        }

        date = user.get(IdentityPlugin.BIRTH_DATE);

        event.reply(String.format("Vous avez défini votre date de naissance au %d/%d/%d.", date.get(Calendar.DAY_OF_MONTH), date.get(Calendar.MONTH) + 1, date.get(Calendar.YEAR))).queue();
    }
}
