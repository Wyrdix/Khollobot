package com.wyrdix.khollobot.command.group;

import com.wyrdix.khollobot.GlobalConfig;
import com.wyrdix.khollobot.command.KCommandImpl;
import com.wyrdix.khollobot.plugin.DefaultPlugin;
import com.wyrdix.khollobot.plugin.GroupPlugin;
import com.wyrdix.khollobot.plugin.IdentityPlugin;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;

import java.util.Objects;

public class AddGroupCommand extends KCommandImpl {

    private static final AddGroupCommand INSTANCE = new AddGroupCommand();
    private static final String ID = "add_group";
    private static final String DESCRIPTION = "Ajoute un groupe";

    public AddGroupCommand() {
        super(DefaultPlugin.class, ID, DESCRIPTION);
    }

    public static AddGroupCommand getInstance() {
        return INSTANCE;
    }

    @Override
    public SlashCommandData getData() {
        return super.getData()
                .addOption(OptionType.STRING, "group", "Nom du groupe", true)
                .addOption(OptionType.INTEGER, "subgroups", "Quantité de sous groupes", true);
    }

    public void execute(SlashCommandInteractionEvent event) {
        User user = event.getUser();

        if (!IdentityPlugin.isBotAdmin(user.getIdLong())) {
            event.reply("Seuls les administrateurs du bot peuvent modifier cette propriété.").queue();
            return;
        }

        String group = Objects.requireNonNull(event.getOption("group")).getAsString().trim();
        int subgroup = Objects.requireNonNull(event.getOption("subgroups")).getAsInt();

        if (subgroup <= 0) {
            event.reply("Vous devez créer au moins un sous groupe").queue();
            return;
        }

        GroupPlugin.GroupPluginConfig config = GlobalConfig.getGlobalConfig().getConfig(GroupPlugin.class);

        config.add(group, subgroup);

        event.reply("Ce groupe a bien été ajouté à la liste des groupes disponibles").queue();
    }
}
