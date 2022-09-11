package com.wyrdix.khollobot;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.Base64;
import com.google.api.client.util.StringUtils;
import com.google.api.services.gmail.Gmail;
import com.google.api.services.gmail.model.*;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.io.*;
import java.lang.Thread;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.util.*;

public class MailManager {

    private static final String APPLICATION_NAME = "Gmail API Java Quickstart";
    private static final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();
    private static final String user = "me";
    static Gmail service = null;
    private static JsonObject loginObject;

    public static Optional<Message> getMail(){

        // Access Gmail inbox

        Message message = null;
        try {
            Gmail.Users.Messages.List request = service.users().messages().list(user);
            ListMessagesResponse messagesResponse = request.execute();
            request.setPageToken(messagesResponse.getNextPageToken());

            if(messagesResponse.getMessages() == null) return Optional.empty();

            // Get ID of the email you are looking for
            String messageId = messagesResponse.getMessages().get(messagesResponse.getMessages().size()-1).getId();

            message = service.users().messages().get(user, messageId).execute();

            service.users().messages().trash("me", messageId);
        } catch (IOException e) {
            e.printStackTrace();
        }

        if(message != null){
            try {
                service.users().messages().trash("me", message.getId()).execute();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return Optional.ofNullable(message);

    }



    public static Gmail getGmailService() throws IOException, GeneralSecurityException {
        InputStream loginStream = Thread.currentThread().getContextClassLoader().getResourceAsStream("login.json");
        MailManager.loginObject = new Gson().fromJson(new InputStreamReader(loginStream), JsonObject.class).getAsJsonObject("gmail_token");
        String data = loginObject.toString();
        GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(new JacksonFactory(), new InputStreamReader(new ByteArrayInputStream(data.getBytes(StandardCharsets.UTF_8))));

        // Credential builder

        Credential authorize = new GoogleCredential.Builder().setTransport(GoogleNetHttpTransport.newTrustedTransport())
                .setJsonFactory(JSON_FACTORY)
                .setClientSecrets(clientSecrets.getDetails().getClientId().toString(),
                        clientSecrets.getDetails().getClientSecret().toString())
                .build().setAccessToken(getAccessToken()).setRefreshToken(
                       loginObject.getAsJsonObject("web").get("refresh_token").getAsString());//Replace this

        // Create Gmail service
        final NetHttpTransport HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
        service = new Gmail.Builder(HTTP_TRANSPORT, JSON_FACTORY, authorize)
                .setApplicationName(APPLICATION_NAME).build();

        return service;
    }

    public static String getContent(Message message) {
        StringBuilder stringBuilder = new StringBuilder();
        getPlainTextFromMessageParts(message.getPayload().getParts(), stringBuilder);
        byte[] bodyBytes = Base64.decodeBase64(stringBuilder.toString());
        return new String(bodyBytes, StandardCharsets.UTF_8);
    }


    public static void getPlainTextFromMessageParts(List<MessagePart> messageParts, StringBuilder stringBuilder) {
        for (MessagePart messagePart : messageParts) {
            if (messagePart.getMimeType().equals("text/plain")) {
                stringBuilder.append(messagePart.getBody().getData());
            }

            if (messagePart.getParts() != null) {
                getPlainTextFromMessageParts(messagePart.getParts(), stringBuilder);
            }
        }
    }

    private static String getAccessToken() {

        try {
            Map<String, Object> params = new LinkedHashMap<>();
            params.put("grant_type", "refresh_token");
            JsonObject installed = MailManager.loginObject.getAsJsonObject("web");
            params.put("client_id", installed.get("client_id").getAsString()); //Replace this
            params.put("client_secret", installed.get("client_secret").getAsString()); //Replace this
            params.put("refresh_token", installed.get("refresh_token").getAsString()); //Replace this

            StringBuilder postData = new StringBuilder();
            for (Map.Entry<String, Object> param : params.entrySet()) {
                if (postData.length() != 0) {
                    postData.append('&');
                }
                postData.append(URLEncoder.encode(param.getKey(), StandardCharsets.UTF_8));
                postData.append('=');
                postData.append(URLEncoder.encode(String.valueOf(param.getValue()), StandardCharsets.UTF_8));
            }
            byte[] postDataBytes = postData.toString().getBytes(StandardCharsets.UTF_8);

            URL url = new URL("https://accounts.google.com/o/oauth2/token");
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setDoOutput(true);
            con.setUseCaches(false);
            con.setRequestMethod("POST");
            con.getOutputStream().write(postDataBytes);

            BufferedReader reader = new BufferedReader(new InputStreamReader(con.getInputStream()));
            StringBuilder buffer = new StringBuilder();
            for (String line = reader.readLine(); line != null; line = reader.readLine()) {
                buffer.append(line);
            }

            return new Gson().fromJson(buffer.toString(), JsonObject.class).get("access_token").getAsString();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }


}
