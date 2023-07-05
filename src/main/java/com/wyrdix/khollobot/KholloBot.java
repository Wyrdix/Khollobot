package com.wyrdix.khollobot;


import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;

public class KholloBot {

    public static void main(String[] args) {
        JDA api = JDABuilder.createDefault(LoginConfig.getLogin().getDiscordToken()).build();
    }
}
