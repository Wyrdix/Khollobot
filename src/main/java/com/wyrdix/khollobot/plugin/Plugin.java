package com.wyrdix.khollobot.plugin;

import com.wyrdix.khollobot.KholloBot;
import net.dv8tion.jda.api.JDA;

public interface Plugin {
    void onEnable();
    void save();

    default JDA getJda(){
        return KholloBot.getJDA();
    }
}
