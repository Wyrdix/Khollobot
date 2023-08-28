package com.wyrdix.khollobot.plugin;

import com.google.gson.*;
import com.wyrdix.khollobot.GlobalConfig;
import com.wyrdix.khollobot.KholloBot;
import com.wyrdix.khollobot.LoginConfig;
import com.wyrdix.khollobot.command.mail.MailAddressCommand;
import com.wyrdix.khollobot.command.mail.MailChannelCommand;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.utils.FileUpload;
import org.jetbrains.annotations.NotNull;

import javax.mail.*;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMultipart;
import javax.mail.internet.MimeUtility;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.*;
import java.util.stream.Collectors;

@PluginInfo(id = "mail", name = "Mail", version = "1.0-SNAPSHOT", author = "Wyrdix", config = MailPlugin.MailPluginConfig.class)
public class MailPlugin implements Plugin {

    public static List<String> downloadAttachments(File name, javax.mail.Message message) throws IOException, MessagingException {
        List<String> downloadedAttachments = new ArrayList<>();
        Multipart multiPart = (Multipart) message.getContent();
        int numberOfParts = multiPart.getCount();
        for (int partCount = 0; partCount < numberOfParts; partCount++) {
            MimeBodyPart part = (MimeBodyPart) multiPart.getBodyPart(partCount);
            if (Part.ATTACHMENT.equalsIgnoreCase(part.getDisposition())) {
                String file = MimeUtility.decodeText(part.getFileName());
                if (part.getSize() >= 10000000) continue;
                File[] files = name.listFiles();
                String[] split = (file + "\\").split("\\\\");
                String fileName = split[split.length - 1];
                part.saveFile(new File(name, fileName));
                downloadedAttachments.add(file);
            }
        }
        return downloadedAttachments;
    }

    public static boolean isWhitelisted(String address){
        return ((MailPluginConfig) GlobalConfig.getGlobalConfig().getConfig(MailPlugin.class)).address.contains(address);
    }

    public static @NotNull
    MessageEmbed getMailMessage(javax.mail.Message message) throws MessagingException, IOException {
        String line = getTextFromMessage(message);
        String title = message.getSubject();
        String from = message.getFrom()[0].toString();
        String date = message.getSentDate().toString();

        if (message.getFrom().length == 0 || !isWhitelisted(message.getFrom()[0].getType())) {
            //noinspection ConstantConditions
            return null;
        }

        line = line.replaceAll("Le.*?écrit:.*?--End of Post--", "");

        line = line.substring(0, Math.min(line.length(), 4000));

        return new EmbedBuilder()
                .addBlankField(false)
                .setAuthor(from + "\nTitre : " + title + "")
                .setDescription(line)
                .setFooter(date, null)
                .build();


    }

    public static String getTextFromMessage(Message message) throws MessagingException, IOException {
        String result = "";
        if (message.isMimeType("text/plain")) {
            result = message.getContent().toString();
        } else if (message.isMimeType("multipart/*")) {
            MimeMultipart mimeMultipart = (MimeMultipart) message.getContent();
            result = getTextFromMimeMultipart(mimeMultipart);
        }
        return result;
    }

    public static String getTextFromMimeMultipart(
            MimeMultipart mimeMultipart) throws MessagingException, IOException {
        StringBuilder result = new StringBuilder();
        int count = mimeMultipart.getCount();
        for (int i = 0; i < count; i++) {
            BodyPart bodyPart = mimeMultipart.getBodyPart(i);
            if (bodyPart.isMimeType("text/plain")) {
                result.append("\n").append(bodyPart.getContent());
                break; // without break same text appears twice in my tests
            } else if (bodyPart.isMimeType("text/html")) {
                String html = (String) bodyPart.getContent();
                result.append("\n").append(html);
            } else if (bodyPart.getContent() instanceof MimeMultipart) {
                result.append(getTextFromMimeMultipart((MimeMultipart) bodyPart.getContent()));
            }
        }
        return result.toString();
    }

    @Override
    public void onEnable() {
        addCommand(MailChannelCommand.getInstance());
        addCommand(MailAddressCommand.getInstance());

        MailThread thread = new MailThread();
        thread.start();
    }

    @Override
    public void save() {

    }

    private void onMailReceived(Message mail) {
        MailThread.canContinue = false;

        try {

            MailPluginConfig config = GlobalConfig.getGlobalConfig().getConfig(MailPlugin.class);
            TextChannel mail_channel = getJda().getTextChannelById(config.channel_id);

            if (mail_channel != null) {
                MessageEmbed message = getMailMessage(mail);


                File dataFolder = new File("data/" + System.currentTimeMillis() + "/");
                //noinspection ResultOfMethodCallIgnored
                dataFolder.mkdirs();

                List<String> names = downloadAttachments(dataFolder, mail);

                List<File> files = names.stream().map(s -> new File(dataFolder, s)).toList();

                List<FileUpload> uploads = files.stream().map(FileUpload::fromData).toList();


                mail_channel.sendMessageEmbeds(message).queue(a -> mail_channel.sendFiles(uploads).queue(s -> {
                    //noinspection ResultOfMethodCallIgnored
                    files.forEach(File::delete);
                    //noinspection ResultOfMethodCallIgnored
                    dataFolder.delete();
                }));

            }

        } catch (Throwable e) {
            e.printStackTrace();
        } finally {
            try {
                mail.setFlag(Flags.Flag.DELETED, true);
            } catch (MessagingException e) {
                e.printStackTrace();
            }
            MailThread.canContinue = true;
        }
    }

    public static class MailPluginConfig extends Plugin.PluginConfig implements JsonDeserializer<MailPluginConfig> {
        public long channel_id;

        public Set<String> address;

        public MailPluginConfig(PluginConfig config) {
            super(config);
        }

        @Override
        public MailPluginConfig deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {

            PluginConfig config = context.deserialize(json, PLUGIN_CONFIG_TYPE);
            MailPluginConfig pluginConfig = new MailPluginConfig(config);

            JsonArray admins = json.getAsJsonObject().getAsJsonArray("address");

            pluginConfig.address = admins.asList().stream().map(JsonElement::getAsString).collect(Collectors.toSet());

            return pluginConfig;
        }
    }

    private class MailThread extends Thread {
        public static Authenticator auth = new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {

                LoginConfig login = LoginConfig.getLogin();

                return new PasswordAuthentication(login.getMailUsername(), login.getMail_password());
            }
        };
        private static Store store;
        private static Folder inbox;
        private static boolean canContinue = true;

        public static Optional<Message> getMail() throws Exception {

            try {
                if (store != null && store.isConnected()) store.close();
                if (inbox != null && inbox.isSubscribed() && inbox.isOpen()) inbox.close();
            } catch (Throwable ignored) {

            }

            store = getStore();
            inbox = store.getFolder("INBOX");
            inbox.open(Folder.READ_ONLY);

            if (inbox.getMessageCount() == 0) return Optional.empty();

            Message message = inbox.getMessages()[0];

            return Optional.ofNullable(message);
        }

        public static Store getStore() throws MessagingException {
            Properties props = new Properties();
            props.put("mail.pop3.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
            props.put("mail.pop3.socketFactory.fallback", "false");
            props.put("mail.pop3.socketFactory.port", "995");
            props.put("mail.pop3.port", "995");
            props.put("mail.pop3.host", "pop.gmail.com");
            props.put("mail.pop3.user", LoginConfig.getLogin().getMailUsername());
            props.put("mail.store.protocol", "pop3");
            props.put("mail.pop3.ssl.protocols", "TLSv1.2");

            // 3. Creating mail session.
            Session session = Session.getInstance(props, auth);

            // 4. Get the POP3 store provider and connect to the store.
            Store store = session.getStore("pop3");
            store.connect();
            return store;
        }

        @Override
        public void run() {
            long l = -1;
            while (KholloBot.getJDA() != null) {
                //noinspection StatementWithEmptyBody
                while (l + 1000 > System.currentTimeMillis() || !canContinue) ;
                l = System.currentTimeMillis();

                try {
                    getMail().ifPresent(mail -> {
                        System.out.println(mail);
                        MailPlugin.this.onMailReceived(mail);
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }


        }
    }
}
