package com.wyrdix.khollobot.command.mail;

import com.wyrdix.khollobot.GlobalConfig;
import com.wyrdix.khollobot.command.KCommandImpl;
import com.wyrdix.khollobot.plugin.DefaultPlugin;
import com.wyrdix.khollobot.plugin.MailPlugin;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.channel.unions.GuildMessageChannelUnion;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;
import net.dv8tion.jda.internal.utils.PermissionUtil;

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
        return super.getData().setGuildOnly(true);
    }

    @Override
    public void execute(SlashCommandInteractionEvent event) {
        Member member = event.getMember();
        if (event.getGuild() == null || member == null) {
            event.reply("Cette commande est réservé aux serveurs !").queue();
            return;
        }

        if (!PermissionUtil.checkPermission(member, Permission.ADMINISTRATOR)) {
            event.reply("Seuls les administrateurs de serveur peuvent modifier cette propriété.").queue();
            return;
        }

        MailPlugin.MailPluginConfig config = GlobalConfig.getGlobalConfig().getConfig(MailPlugin.class);
        GuildMessageChannelUnion channel = event.getGuildChannel();
        config.channel_id = channel.getIdLong();

        GlobalConfig.getGlobalConfig().save();
        event.reply("Ce salon a bien été configuré pour recevoir les mails.").queue();
    }
}
