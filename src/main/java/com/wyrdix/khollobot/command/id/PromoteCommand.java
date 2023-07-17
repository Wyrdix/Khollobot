package com.wyrdix.khollobot.command.id;

import com.wyrdix.khollobot.GlobalConfig;
import com.wyrdix.khollobot.command.KCommandImpl;
import com.wyrdix.khollobot.plugin.DefaultPlugin;
import com.wyrdix.khollobot.plugin.IdentityPlugin;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;

import java.util.Objects;

public class PromoteCommand extends KCommandImpl {

    private static final PromoteCommand INSTANCE = new PromoteCommand();
    private static final String ID = "promote";
    private static final String DESCRIPTION = "Permet de définir un nouvel administrateur pour le bot";

    public PromoteCommand() {
        super(DefaultPlugin.class, ID, DESCRIPTION);
    }

    public static PromoteCommand getInstance() {
        return INSTANCE;
    }

    @Override
    public SlashCommandData getData() {
        return super.getData().addOption(OptionType.USER, "user", "Personne à promouvoir", true);
    }

    @Override
    public void execute(SlashCommandInteractionEvent event) {

        if (!IdentityPlugin.isBotAdmin(event.getUser().getIdLong())) {
            event.reply("Vous n'avez pas l'autorisation d'executer cette commande").queue();
            return;
        }

        User user = Objects.requireNonNull(event.getOption("user")).getAsUser();

        if(user.isBot() || user.isSystem()){
            event.reply("Cette entité ne peut pas être administrateur du bot").queue();
            return;
        }

        IdentityPlugin.IdentityPluginConfig config = GlobalConfig.getGlobalConfig().getConfig(IdentityPlugin.class);

        if (config.admins.contains(user.getIdLong())){
            event.reply("Cet utilisateur est déjà administrateur du bot.").queue();
        }else
        {
            config.admins.add(user.getIdLong());
            event.reply("Cet utilisateur est dorénavant administrateur du bot.").queue();
        }

    }
}
