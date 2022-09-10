package com.wyrdix.khollobot;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.wyrdix.khollobot.data.DataFile;
import com.wyrdix.khollobot.data.LoginData;
import com.wyrdix.khollobot.data.UserData;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.AnnotatedEventManager;
import net.dv8tion.jda.api.hooks.SubscribeEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

import javax.security.auth.login.LoginException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class KholloBot {

    public static JDA jda;

    public static void main(String[] args) throws LoginException {
        InputStream loginStream = Thread.currentThread().getContextClassLoader().getResourceAsStream("login.json");
        if(loginStream == null){
            System.out.println("Could not load login informations");
            return;
        }
        JsonObject loginObject = new Gson().fromJson(new InputStreamReader(loginStream), JsonObject.class);
        LoginData loginData = new LoginData(loginObject);

        jda = JDABuilder.createDefault(loginData.getToken()).build();

        jda.upsertCommand("register", "S'enregistre dans la base de donnée")
                .addOption(OptionType.STRING, "last_name", "Votre nom", true, false)
                .addOption(OptionType.STRING, "first_name", "Votre prénom", true, false)
                .addOption(OptionType.INTEGER, "group", "Votre groupe", true, false)
                //.addOptions(new OptionData(OptionType.STRING, "classe", "Votre classe")
                //        .addChoice("MPI", "MPI")
                //)
                .queue();

        jda.setEventManager(new AnnotatedEventManager());
        jda.addEventListener(new KholloBot());

        new Thread(){

            long last = -1;
            final long DELAY = 5000;

            @Override
            public void run() {
                if(System.currentTimeMillis() - last < DELAY) return;
                last = System.currentTimeMillis();

                DataFile.save();
            }
        }.start();
    }

    @SubscribeEvent
    public void onRegisterCommand(SlashCommandInteractionEvent event){
        if(!event.getCommandPath().equals("register")) return;
        String first_name = event.getOption("first_name", (OptionMapping::getAsString));
        String last_name = event.getOption("last_name", (OptionMapping::getAsString));
        //noinspection ConstantConditions
        int group = event.getOption("group", (OptionMapping::getAsInt));

        assert first_name != null && last_name != null;

        User user = event.getUser();
        DataFile.getInstance().newUser(user.getIdLong(), user.getName());
        //noinspection OptionalGetWithoutIsPresent
        UserData data = DataFile.getInstance().getUser(user.getIdLong()).get();
        data.setName(first_name, last_name);
        data.setGroup(group);
        DataFile.save();

        EmbedBuilder embedBuilder = new EmbedBuilder();
        embedBuilder.setAuthor(data.getLast_name()+" "+data.getFirst_name(), null, user.getAvatarUrl());
        embedBuilder.addField("Classe", "MPI", true);
        embedBuilder.addField("Groupe", String.valueOf(data.getGroup()), true);
        event.replyEmbeds(embedBuilder.build()).queue();

    }
}
