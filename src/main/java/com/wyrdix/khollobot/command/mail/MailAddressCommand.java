package com.wyrdix.khollobot.command.mail;

import com.wyrdix.khollobot.GlobalConfig;
import com.wyrdix.khollobot.command.KCommandImpl;
import com.wyrdix.khollobot.plugin.DefaultPlugin;
import com.wyrdix.khollobot.plugin.IdentityPlugin;
import com.wyrdix.khollobot.plugin.MailPlugin;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;

import java.util.Objects;

public class MailAddressCommand extends KCommandImpl {

    private static final MailAddressCommand INSTANCE = new MailAddressCommand();
    private static final String ID = "mail_address";
    private static final String DESCRIPTION = "Configure les adresse email pour lesquels le bot transmettra les mails";

    public MailAddressCommand() {
        super(DefaultPlugin.class, ID, DESCRIPTION);
    }

    public static MailAddressCommand getInstance() {
        return INSTANCE;
    }

    @Override
    public SlashCommandData getData() {
        return super.getData()
                .addOption(OptionType.STRING, "address", "Adresse email", true);
    }

    public void execute(SlashCommandInteractionEvent event) {
        User user = event.getUser();

        if (!IdentityPlugin.isBotAdmin(user.getIdLong())) {
            event.reply("Seuls les administrateurs du bot peuvent modifier cette propriété.").queue();
            return;
        }

        String address = Objects.requireNonNull(event.getOption("address")).getAsString().trim();

        MailPlugin.MailPluginConfig config = GlobalConfig.getGlobalConfig().getConfig(MailPlugin.class);

        config.address.add(address);

        event.reply("Cette adresse a bien été ajouté à la liste des adresses email de redirection").queue();
    }
}
