package com.wyrdix.khollobot;

import com.google.api.client.util.Base64;
import com.google.api.client.util.StringUtils;
import com.google.api.services.gmail.model.MessagePartHeader;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.wyrdix.khollobot.calendar.CalendarFrame;
import com.wyrdix.khollobot.calendar.CalendarManager;
import com.wyrdix.khollobot.data.DataFile;
import com.wyrdix.khollobot.data.LoginData;
import com.wyrdix.khollobot.data.UserData;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.AnnotatedEventManager;
import net.dv8tion.jda.api.hooks.SubscribeEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.ChunkingFilter;
import net.dv8tion.jda.api.utils.FileUpload;

import javax.imageio.ImageIO;
import javax.security.auth.login.LoginException;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.GeneralSecurityException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collector;
import java.util.stream.Collectors;

public class KholloBot {

    public static JDA jda;
    public static Collector<MessagePartHeader, ?, Map<String, String>>
            partToMapCollector = Collectors.toMap(MessagePartHeader::getName, MessagePartHeader::getValue);
    ;

    public static void main(String[] args) throws LoginException {
        InputStream loginStream = Thread.currentThread().getContextClassLoader().getResourceAsStream("login.json");
        if (loginStream == null) {
            System.out.println("Could not load login informations");
            return;
        }
        JsonObject loginObject = new Gson().fromJson(new InputStreamReader(loginStream), JsonObject.class);
        LoginData loginData = new LoginData(loginObject);

        jda = JDABuilder.createDefault(loginData.getToken())
                .setChunkingFilter(ChunkingFilter.ALL) // enable member chunking for all guilds
                .enableIntents(GatewayIntent.DIRECT_MESSAGE_REACTIONS)
                .build();

        jda.upsertCommand("register", "S'enregistre dans la base de donnée")
                .addOption(OptionType.STRING, "last_name", "Votre nom", true, false)
                .addOption(OptionType.STRING, "first_name", "Votre prénom", true, false)
                .addOption(OptionType.INTEGER, "group", "Votre groupe", true, false)
                .queue();

        jda.upsertCommand("edp", "Récupère votre emploi du temps").queue();

        jda.upsertCommand("mail_channel", "Définis le channel ou poster les mails").queue();
        jda.upsertCommand("mail_private", "Définis si vous voulez recevoir les mails par channel privé")
                .addOption(OptionType.BOOLEAN, "mail", "Recevoir les mails en dm", true, false)
                .queue();

        jda.setEventManager(new AnnotatedEventManager());
        jda.addEventListener(new KholloBot());

        try {
            MailManager.getGmailService();
            CalendarManager.load();
            CalendarFrame.init();
        } catch (IOException | GeneralSecurityException e) {
            e.printStackTrace();
        }

        new Thread() {

            final long DELAY = 5000;
            int state = 0;
            long last = -1;

            @Override
            public void run() {
                while (true) {
                    try {
                        if (System.currentTimeMillis() - last < DELAY) continue;
                        last = System.currentTimeMillis();

                        state++;

                        MailManager.getMail().ifPresent(s -> {

                            MessageEmbed messageEmbed = getMailMessage(s);

                            DataFile.getInstance().getUsers().forEach(data->{
                                if(!data.getDmMail()) return;
                                jda.retrieveUserById(data.getId_long())
                                        .queue((user-> {
                                            System.out.println("SENDING mail to "+user.getName());
                                            user.openPrivateChannel()
                                                    .queue(a -> a.sendMessageEmbeds(messageEmbed).queue());
                                        }));

                            });

                            long mailChannel = DataFile.getInstance().getMailChannel();

                            if(mailChannel != -1){
                                TextChannel textChannelById = jda.getTextChannelById(mailChannel);
                                assert textChannelById != null;
                                System.out.println("SENDING mail to mail channel");
                                textChannelById.sendMessageEmbeds(messageEmbed).queue();
                            }


                        });

                        DataFile.save();
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }
            }
        }.start();
    }

    public static MessageEmbed getMailMessage(com.google.api.services.gmail.model.Message message) {
        Map<String, String> headers = new HashMap<>();
        message.getPayload().getHeaders().forEach(s->headers.put(s.getName(), s.getValue()));
        String line = MailManager.getContent(message);
        String title = headers.get("Subject");
        String from = headers.get("From");
        String date = headers.get("Date");

        return new EmbedBuilder()
                .addBlankField(false)
                .setAuthor(from+"\nTitre : "+title+"")
                .setDescription(line)
                .setFooter(date, null)
                .build();


    }

    @SubscribeEvent
    public void onMailChannel(SlashCommandInteractionEvent event){
        if (!event.getCommandPath().equals("mail_channel")) return;

        DataFile.getInstance().setMailChannel(event.getChannel().getIdLong());

        event.reply("Channel set").queue();
    }

    @SubscribeEvent
    public void onMailPrivateChannel(SlashCommandInteractionEvent event){
        if(!event.getCommandPath().equals("mail_private")) return;
        Optional<UserData> user = DataFile.getInstance().getUser(event.getUser().getIdLong());
        if(user.isEmpty()){
            event.reply("Vous ne vous êtes pas enregistré").queue();
            return;
        }
        boolean dm_mail = event.getOption("mail", (OptionMapping::getAsBoolean));

        if(dm_mail){
            event.reply("Vous receverez les mails en dm").queue();
        }else{
            event.reply("Vous ne receverez plus les mails en dm").queue();
        }

        UserData userData = user.get();
        userData.setDmMail(dm_mail);


    }

    @SubscribeEvent
    public void onCalendarRetrieve(SlashCommandInteractionEvent event){
        if (!event.getCommandPath().equals("edp")) return;

        Optional<UserData> user = DataFile.getInstance().getUser(event.getUser().getIdLong());
        if(user.isEmpty()){
            event.reply("Vous ne vous êtes pas enregistré").queue();
            return;
        }

        UserData userData = user.get();
        BufferedImage image = CalendarFrame.getCalendar(userData, CalendarManager.getCurrentWeek());

        new File("data").mkdirs();

        File file = new File("data/" + userData.getId_long() + ".png");
        try {
            ImageIO.write(image, "PNG", file);
        } catch (IOException e) {
            e.printStackTrace();
        }
        FileUpload upload = FileUpload.fromData(file);
        event.reply("Voici votre emploi du temps de la semaine :").queue();
        event.getChannel().sendFiles(upload).queue((s)-> {
            file.delete();
        });
    }

    @SubscribeEvent
    public void onRegisterCommand(SlashCommandInteractionEvent event) {
        if (!event.getCommandPath().equals("register")) return;
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
        embedBuilder.setAuthor(data.getLast_name() + " " + data.getFirst_name(), null, user.getAvatarUrl());
        embedBuilder.addField("Classe", "MPI", true);
        embedBuilder.addField("Groupe", String.valueOf(data.getGroup()), true);
        event.replyEmbeds(embedBuilder.build()).queue();

    }
}
