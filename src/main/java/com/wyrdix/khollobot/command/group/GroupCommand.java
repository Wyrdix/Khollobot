package com.wyrdix.khollobot.command.group;

import com.wyrdix.khollobot.GlobalConfig;
import com.wyrdix.khollobot.KUser;
import com.wyrdix.khollobot.command.KCommandImpl;
import com.wyrdix.khollobot.plugin.DefaultPlugin;
import com.wyrdix.khollobot.plugin.GroupPlugin;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;

import java.io.IOException;
import java.util.HashMap;
import java.util.Objects;

public class GroupCommand extends KCommandImpl {

    private static final GroupCommand INSTANCE = new GroupCommand();
    private static final String ID = "group";
    private static final String DESCRIPTION = "Rejoint ou quitte un groupe";

    public GroupCommand() {
        super(DefaultPlugin.class, ID, DESCRIPTION);
    }

    public static GroupCommand getInstance() {
        return INSTANCE;
    }

    @Override
    public SlashCommandData getData() {
        return super.getData()
                .addOption(OptionType.STRING, "group", "Nom du groupe", true)
                .addOption(OptionType.INTEGER, "subgroups", "Sous groupes", true);
    }

    @Override
    public void execute(SlashCommandInteractionEvent event) {
        User user = event.getUser();

        String group = Objects.requireNonNull(event.getOption("group")).getAsString().trim();
        int subgroup = Objects.requireNonNull(event.getOption("subgroups")).getAsInt();

        GroupPlugin.GroupPluginConfig config = (GroupPlugin.GroupPluginConfig) GlobalConfig.getGlobalConfig().getConfig(GroupPlugin.class);

        if (!config.groups.containsKey(group)) {
            event.reply("Ce groupe n'existe pas !").queue();
            return;
        }

        if (subgroup > config.groups.get(group).subgroup) {
            event.reply("Ce groupe ne contient pas autant de sous groupes").queue();
            return;
        }

        KUser kUser = KUser.getKUser(user.getIdLong());
        HashMap<String, GroupPlugin.GroupConfig> configs = new HashMap<>(kUser.get(GroupPlugin.USER_GROUPS));
        configs.put(group, new GroupPlugin.GroupConfig(group, subgroup));

        kUser.set(GroupPlugin.USER_GROUPS, configs);
        try {
            kUser.save();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        if (subgroup == 0) event.reply("Vous avez quitté le groupe " + group).queue();
        else event.reply("Vous avez rejoint le groupe : " + group + " numéro " + subgroup).queue();

    }
}
