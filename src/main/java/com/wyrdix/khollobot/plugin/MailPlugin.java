package com.wyrdix.khollobot.plugin;

import com.wyrdix.khollobot.command.mail.MailChannelCommand;

@PluginInfo(id = "mail", name = "Mail", version = "1.0-SNAPSHOT", author = "Wyrdix", config = MailPlugin.MailPluginConfig.class)
public class MailPlugin implements Plugin {

    @Override
    public void onEnable() {
        addCommand(MailChannelCommand.getInstance());
    }

    @Override
    public void save() {

    }

    public static class MailPluginConfig extends Plugin.PluginConfig {
        public long channel_id;
    }
}
