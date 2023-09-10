package com.wyrdix.khollobot.command.group;

import com.wyrdix.khollobot.GlobalConfig;
import com.wyrdix.khollobot.KUser;
import com.wyrdix.khollobot.command.KCommandImpl;
import com.wyrdix.khollobot.plugin.DefaultPlugin;
import com.wyrdix.khollobot.plugin.GroupPlugin;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class GroupsCommand extends KCommandImpl {

    private static final GroupsCommand INSTANCE = new GroupsCommand();
    private static final String ID = "groups";
    private static final String DESCRIPTION = "Liste les groupes dans lesquels le joueur est";

    public GroupsCommand() {
        super(DefaultPlugin.class, ID, DESCRIPTION);
    }

    public static GroupsCommand getInstance() {
        return INSTANCE;
    }

    @Override
    public void execute(SlashCommandInteractionEvent event) {
        User user = event.getUser();
        KUser kUser = KUser.getKUser(user.getIdLong());
        Set<GroupPlugin.GroupConfig> configs = new HashSet<>(kUser.get(GroupPlugin.USER_GROUPS));

        Map<String, Integer> map = configs.stream().collect(Collectors.toMap(s -> s.name, s -> s.subgroup));
        GroupPlugin.GroupPluginConfig config = GlobalConfig.getGlobalConfig().getConfig(GroupPlugin.class);
        config.groups.forEach(s -> {
            if (!map.containsKey(s.name)) map.put(s.name, 0);
        });

        EmbedBuilder builder = new EmbedBuilder();
        builder.setTitle("Liste des groupes");

        for (String name : map.keySet()) {
            int subgroup = map.get(name);
            builder.addField(String.format("%s : ", name), String.format("(%s)", subgroup == 0 ? "AUCUN" : subgroup), false);
        }

        event.replyEmbeds(builder.build()).queue();
    }
}
