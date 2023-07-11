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
import java.util.Objects;

public class IdCommand extends KCommandImpl {

    private static final IdCommand INSTANCE = new IdCommand();
    private static final String ID = "id";
    private static final String DESCRIPTION = "Permet de s'enregistrer auprés du KholloBot";

    public IdCommand() {
        super(DefaultPlugin.class, ID, DESCRIPTION);
    }

    public static IdCommand getInstance() {
        return INSTANCE;
    }

    @Override
    public SlashCommandData getData() {
        return super.getData().addOption(OptionType.STRING, "nom", "Ecrivez ici votre nom de famille", true)
                .addOption(OptionType.STRING, "prénom", "Ecrivez ici votre prénom", true);
    }

    @Override
    public void execute(SlashCommandInteractionEvent event) {
        String lastName = Objects.requireNonNull(event.getOption("nom", OptionMapping::getAsString));
        String name = Objects.requireNonNull(event.getOption("prénom", OptionMapping::getAsString));

        KUser user = KUser.getKUser(event.getUser().getIdLong());
        user.set(IdentityPlugin.FIRST_NAME, name);
        user.set(IdentityPlugin.LAST_NAME, lastName);
        try {
            user.save();
        } catch (IOException e) {
            e.printStackTrace();
        }

        name = user.get(IdentityPlugin.FIRST_NAME);
        lastName = user.get(IdentityPlugin.LAST_NAME);

        event.reply(String.format("Vous vous êtes enregistré en tant que %s %s.",name, lastName)).queue();
    }
}
