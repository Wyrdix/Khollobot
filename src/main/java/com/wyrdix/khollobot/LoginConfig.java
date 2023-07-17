package com.wyrdix.khollobot;

import com.google.gson.Gson;
import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class LoginConfig {

    private static LoginConfig INSTANCE = null;
    public String discord_token;
    public String mail_password;
    public String mail_username;

    public LoginConfig() {

    }

    public static LoginConfig getLogin() {
        if (INSTANCE == null) {

            InputStream inputStream = LoginConfig.class.getClassLoader().getResourceAsStream("login.json");
            assert inputStream != null;
            InputStreamReader streamReader = new InputStreamReader(inputStream);

            System.out.println("Loading login resource file, may be located at : " + new File("login.json").getAbsolutePath());

            try {
                INSTANCE = new Gson().fromJson(streamReader, LoginConfig.class);
            } catch (JsonSyntaxException | JsonIOException e) {
                throw new RuntimeException(e);
            }

            try {
                streamReader.close();
                inputStream.close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        return INSTANCE;
    }

    public String getDiscordToken() {
        return discord_token;
    }

    public String getMail_password() {
        return mail_password;
    }

    public String getMailUsername() {
        return mail_username;
    }
}
