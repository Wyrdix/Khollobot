package com.wyrdix.khollobot;

import com.google.common.io.Files;
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
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.hooks.AnnotatedEventManager;
import net.dv8tion.jda.api.hooks.SubscribeEvent;
import net.dv8tion.jda.api.interactions.callbacks.IReplyCallback;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.components.ItemComponent;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.ChunkingFilter;
import net.dv8tion.jda.api.utils.FileUpload;

import javax.imageio.ImageIO;
import javax.mail.*;
import javax.mail.internet.MimeBodyPart;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class KholloBot {

    public static JDA jda;
    public static File LOGIN_FILE;
    public static LoginData LOGIN_DATA;

    public static void main(String[] args) {
        InputStream loginStream = Thread.currentThread().getContextClassLoader().getResourceAsStream("login.json");
        if (loginStream == null) {
            System.out.println("Could not load login informations");
            return;
        } else {
            LOGIN_FILE = new File("login.json");
            try {
                if (LOGIN_FILE.createNewFile()) {
                    try {
                        //noinspection UnstableApiUsage
                        Files.write(loginStream.readAllBytes(), LOGIN_FILE);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                loginStream = new FileInputStream(LOGIN_FILE);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }


        }
        JsonObject loginObject = new Gson().fromJson(new InputStreamReader(loginStream), JsonObject.class);
        LOGIN_DATA = new LoginData(loginObject);

        try {
            jda = JDABuilder.createDefault(LOGIN_DATA.getToken())
                    .setChunkingFilter(ChunkingFilter.ALL) // enable member chunking for all guilds
                    .enableIntents(GatewayIntent.DIRECT_MESSAGE_REACTIONS)
                    .build();
        } catch (Exception e) {
            e.printStackTrace();
        }

        jda.upsertCommand("register", "S'enregistre dans la base de donnée")
                .addOption(OptionType.STRING, "last_name", "Votre nom", true, false)
                .addOption(OptionType.STRING, "first_name", "Votre prénom", true, false)
                .addOption(OptionType.INTEGER, "group", "Votre groupe", true, false)
                .addOption(OptionType.BOOLEAN, "lv2", "Avez-vous une lv2", false, false)
                .queue();

        jda.upsertCommand("edt", "Récupère votre emploi du temps")
                .addOption(OptionType.INTEGER, "week", "La semaine de kholle que vous souhaiter voir", false, false)
                .queue();
        jda.upsertCommand("edp", "Récupère votre emploi du temps")
                .addOption(OptionType.INTEGER, "week", "La semaine de kholle que vous souhaiter voir", false, false)
                .queue();

        jda.upsertCommand("mail_channel", "Définis le channel ou poster les mails").queue();
        jda.upsertCommand("mail_private", "Définis si vous voulez recevoir les mails par channel privé")
                .addOption(OptionType.BOOLEAN, "mail", "Recevoir les mails en dm", true, false)
                .queue();

        jda.setEventManager(new AnnotatedEventManager());
        jda.addEventListener(new KholloBot());

        try {
            CalendarManager.load();
            CalendarFrame.init();
        } catch (IOException e) {
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

                        if (state % 10 == 0) {
                            File data = new File("data");
                            File[] files = data.listFiles();
                            if (files == null) continue;
                            for (File file : files) {
                                if (!file.getName().startsWith("mail_")) continue;
                                String raw = file.getName().substring(5);
                                if (last - Long.parseLong(raw) < 10000) continue;

                                delete(file);
                            }
                        }

                        Store store = MailManager.getStore();
                        Folder inbox = store.getFolder("INBOX");
                        inbox.open(Folder.READ_ONLY);

                        if (inbox.getMessageCount() == 0) continue;

                        javax.mail.Message message = inbox.getMessages()[0];

                        MessageEmbed messageEmbed = null;
                        try {
                            messageEmbed = getMailMessage(message);
                        } catch (MessagingException | IOException e) {
                            e.printStackTrace();
                            return;
                        }


                        if (messageEmbed == null) return;

                        File datafolder = new File("data/mail_" + last);
                        datafolder.mkdir();

                        List<String> names = downloadAttachments(datafolder, message);

                        List<File> files = names.stream().map(s -> new File("data/mail_" + last + "/" + s)).toList();

                        List<FileUpload> uploads = files.stream().map(FileUpload::fromData).toList();

                        MessageEmbed finalMessageEmbed = messageEmbed;
                        DataFile.getInstance().getUsers().forEach(data -> {
                            if (!data.getDmMail()) return;
                            jda.retrieveUserById(data.getId_long())
                                    .queue((user -> {
                                        System.out.println("SENDING mail to " + user.getName());
                                        user.openPrivateChannel()
                                                .queue(a -> a.sendMessageEmbeds(finalMessageEmbed).queue(b -> {
                                                    for (FileUpload upload : uploads) {
                                                        a.sendFiles(upload).queue();
                                                    }
                                                }));
                                    }));
                        });

                        long mailChannel = DataFile.getInstance().getMailChannel();

                        if (mailChannel != -1) {
                            System.out.println("SENDING mail to mail channel");
                            Objects.requireNonNull(jda.getTextChannelById(mailChannel)).
                                    sendMessageEmbeds(messageEmbed).setFiles(uploads).queue();
                        }

                        message.setFlag(Flags.Flag.DELETED, true);
                        inbox.close(true);
                        store.close();

                        DataFile.save();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }

            private void delete(File file) {
                if (file.isDirectory()) {
                    File[] files = file.listFiles();
                    if (files != null)
                        for (File listFile : files) {
                            delete(listFile);
                        }
                }
                //noinspection ResultOfMethodCallIgnored
                file.delete();
            }
        }.start();
    }

    public static List<String> downloadAttachments(File name, javax.mail.Message message) throws IOException, MessagingException {
        List<String> downloadedAttachments = new ArrayList<String>();
        Multipart multiPart = (Multipart) message.getContent();
        int numberOfParts = multiPart.getCount();
        for (int partCount = 0; partCount < numberOfParts; partCount++) {
            MimeBodyPart part = (MimeBodyPart) multiPart.getBodyPart(partCount);
            if (Part.ATTACHMENT.equalsIgnoreCase(part.getDisposition())) {
                String file = part.getFileName();
                if (part.getSize() >= 10000000) continue;
                part.saveFile(name.toPath() + File.separator + part.getFileName());
                downloadedAttachments.add(file);
            }
        }
        return downloadedAttachments;
    }

    public static MessageEmbed getMailMessage(javax.mail.Message message) throws MessagingException, IOException {
        String line = MailManager.getTextFromMessage(message);
        String title = message.getSubject();
        String from = message.getFrom()[0].toString();
        String date = message.getSentDate().toString();

        if (from.contains("no-reply@accounts.google.com")) return null;

        line = line.replaceFirst("Le (\\n|.)*\\.(\\n|.)*à(\\n|.)*", "");
        line = (line + "a écrit:").split("a écrit:")[0];

        line = line.substring(0, Math.min(line.length(), 4000));

        return new EmbedBuilder()
                .addBlankField(false)
                .setAuthor(from + "\nTitre : " + title + "")
                .setDescription(line)
                .setFooter(date, null)
                .build();


    }

    @SubscribeEvent
    public void onMailChannel(SlashCommandInteractionEvent event) {
        if (!event.getCommandPath().equals("mail_channel")) return;

        DataFile.getInstance().setMailChannel(event.getChannel().getIdLong());

        event.reply("Channel set").queue();
    }

    @SubscribeEvent
    public void onMailPrivateChannel(SlashCommandInteractionEvent event) {
        if (!event.getCommandPath().equals("mail_private")) return;
        Optional<UserData> user = DataFile.getInstance().getUser(event.getUser().getIdLong());
        if (user.isEmpty()) {
            event.reply("Vous ne vous êtes pas enregistré").queue();
            return;
        }
        boolean dm_mail = Boolean.TRUE.equals(event.getOption("mail", (OptionMapping::getAsBoolean)));

        if (dm_mail) {
            event.reply("Vous receverez les mails en dm").queue();
        } else {
            event.reply("Vous ne receverez plus les mails en dm").queue();
        }

        UserData userData = user.get();
        userData.setDmMail(dm_mail);


    }

    @SubscribeEvent
    public void onCalendarRetrieve(SlashCommandInteractionEvent event) {
        if (!event.getCommandPath().equals("edp") && !event.getCommandPath().equals("edt")) return;

        Optional<UserData> user = DataFile.getInstance().getUser(event.getUser().getIdLong());
        if (user.isEmpty()) {
            event.reply("Vous ne vous êtes pas enregistré").queue();
            return;
        }


        int week = Optional.ofNullable(event.getOption("week", OptionMapping::getAsInt)).orElse(CalendarManager.getCurrentWeek());

        UserData userData = user.get();

        sendCalendar(event, userData, week);


    }

    private void sendCalendar(IReplyCallback event, UserData data, int week) {
        BufferedImage image = CalendarFrame.getCalendar(data, week);

        //noinspection ResultOfMethodCallIgnored
        new File("data").mkdirs();

        File file = new File("data/" + data.getId_long() + ".png");
        try {
            ImageIO.write(image, "PNG", file);
        } catch (IOException e) {
            e.printStackTrace();
        }
        FileUpload upload = FileUpload.fromData(file);
        //noinspection ResultOfMethodCallIgnored
        event.reply("Voici votre emploi du temps de la semaine :").setFiles(upload)
                .addActionRow(generateButton(week)).queue((s) -> file.delete());
    }

    private ItemComponent[] generateButton(int week) {

        int size = week < 3 ? 1 : 2;

        if (size == 1) {
            return new ItemComponent[]{Button.primary("khollobot_week_" + (week - 1), "Semaine Précédente")};
        } else {
            return new ItemComponent[]{Button.primary("khollobot_week_" + (week - 1), "Semaine Précédente"), Button.primary("khollobot_week_" + (week + 1), "Semaine Suivante")};
        }
    }


    @SubscribeEvent
    public void onButtonClicked(ButtonInteractionEvent event) {
        Button button = event.getButton();
        if (button.getId() == null || !button.getId().startsWith("khollobot_week_")) return;
        Message.Interaction reference = event.getMessage().getInteraction();
        System.out.println(reference);
        if (reference == null || reference.getUser().getIdLong() != event.getInteraction().getUser().getIdLong()) {
            event.reply("Vous ne pouvez pas modifier cette demande").complete().setEphemeral(true);
            return;
        }

        int week = Integer.parseInt(button.getId().substring("khollobot_week_".length()));

        Optional<UserData> user = DataFile.getInstance().getUser(event.getUser().getIdLong());
        if (user.isEmpty()) {
            event.reply("Vous ne vous êtes pas enregistré").queue();
            return;
        }


        UserData userData = user.get();

        BufferedImage image = CalendarFrame.getCalendar(userData, week);

        //noinspection ResultOfMethodCallIgnored
        new File("data").mkdirs();

        File file = new File("data/" + userData.getId_long() + ".png");
        try {
            ImageIO.write(image, "PNG", file);
        } catch (IOException e) {
            e.printStackTrace();
        }
        FileUpload upload = FileUpload.fromData(file);

        //noinspection ResultOfMethodCallIgnored
        event.getMessage().editMessageAttachments(upload).setActionRow(generateButton(week)).queue((s) -> file.delete());

        event.reply("Actualisé").queue();

        for (Message message : event.getChannel().getIterableHistory()) {
            if (message.getContentRaw().equals("Actualisé")) {
                try {
                    message.delete().queue();
                } catch (Exception ignored) {
                }
            }
        }
    }


    @SubscribeEvent
    public void onRegisterCommand(SlashCommandInteractionEvent event) {
        if (!event.getCommandPath().equals("register")) return;
        String first_name = event.getOption("first_name", (OptionMapping::getAsString));
        String last_name = event.getOption("last_name", (OptionMapping::getAsString));
        //noinspection ConstantConditions
        int group = event.getOption("group", (OptionMapping::getAsInt));
        OptionMapping lv21 = event.getOption("lv2");
        boolean lv2 = lv21 == null || lv21.getAsBoolean();

        assert first_name != null && last_name != null;

        User user = event.getUser();
        DataFile.getInstance().newUser(user.getIdLong(), user.getName());
        //noinspection OptionalGetWithoutIsPresent
        UserData data = DataFile.getInstance().getUser(user.getIdLong()).get();
        data.setName(first_name, last_name);
        data.setGroup(group);
        data.setLv2(lv2);
        DataFile.save();

        EmbedBuilder embedBuilder = new EmbedBuilder();
        embedBuilder.setAuthor(data.getLast_name() + " " + data.getFirst_name(), null, user.getAvatarUrl());
        embedBuilder.addField("Classe", "MPI", true);
        embedBuilder.addField("Groupe", String.valueOf(data.getGroup()), true);
        embedBuilder.addField("LV2", String.valueOf(data.getLv2()), true);
        event.replyEmbeds(embedBuilder.build()).queue();

    }
}
