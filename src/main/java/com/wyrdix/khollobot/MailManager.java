package com.wyrdix.khollobot;

import com.google.gson.JsonObject;

import javax.mail.*;
import javax.mail.internet.MimeMultipart;
import javax.naming.spi.StateFactory;
import java.io.IOException;
import java.util.Optional;
import java.util.Properties;

public class MailManager {

    public static Authenticator auth = new Authenticator() {
        @Override
        protected PasswordAuthentication getPasswordAuthentication() {

            JsonObject data = KholloBot.LOGIN_DATA.getGmail();
            return new PasswordAuthentication(data.get("username").getAsString(), data.get("password").getAsString());
        }
    };

    public static Optional<Message> getMail() throws Exception{
        Store store = getStore();
        Folder inbox = store.getFolder("INBOX");
        inbox.open(Folder.READ_ONLY);

        if(inbox.getMessageCount() == 0) return Optional.empty();

        Message message = inbox.getMessages()[0];
        new Thread(){
            @Override
            public void run() {
                long l = System.currentTimeMillis();

                while (l + 1000 > System.currentTimeMillis());

                try {
                    inbox.close(false);
                } catch (MessagingException e) {
                    e.printStackTrace();
                }
                try {
                    store.close();
                } catch (MessagingException e) {
                    e.printStackTrace();
                }

            }
        }.start();

        return Optional.ofNullable(message);
    }

    public static Store getStore() throws MessagingException {
        Properties props = new Properties();
        props.put("mail.pop3.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
        props.put("mail.pop3.socketFactory.fallback", "false");
        props.put("mail.pop3.socketFactory.port", "995");
        props.put("mail.pop3.port", "995");
        props.put("mail.pop3.host", "pop.gmail.com");
        props.put("mail.pop3.user", KholloBot.LOGIN_DATA.getGmail().get("username").getAsString());
        props.put("mail.store.protocol", "pop3");
        props.put("mail.pop3.ssl.protocols", "TLSv1.2");

        // 3. Creating mail session.
        Session session = Session.getInstance(props, auth);

        // 4. Get the POP3 store provider and connect to the store.
        Store store = session.getStore("pop3");
        store.connect("pop.gmail.com", KholloBot.LOGIN_DATA.getGmail().get("username").getAsString(), KholloBot.LOGIN_DATA.getGmail().get("password").getAsString());
        return store;
    }


    public  static String getTextFromMessage(Message message) throws MessagingException, IOException {
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
            MimeMultipart mimeMultipart)  throws MessagingException, IOException{
        String result = "";
        int count = mimeMultipart.getCount();
        for (int i = 0; i < count; i++) {
            BodyPart bodyPart = mimeMultipart.getBodyPart(i);
            if (bodyPart.isMimeType("text/plain")) {
                result = result + "\n" + bodyPart.getContent();
                break; // without break same text appears twice in my tests
            } else if (bodyPart.isMimeType("text/html")) {
                String html = (String) bodyPart.getContent();
                result = result + "\n" + html;
            } else if (bodyPart.getContent() instanceof MimeMultipart){
                result = result + getTextFromMimeMultipart((MimeMultipart)bodyPart.getContent());
            }
        }
        return result;
    }
}
