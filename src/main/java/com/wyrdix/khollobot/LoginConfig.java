package com.wyrdix.khollobot;

import com.google.gson.Gson;
import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

public class LoginConfig {

    private static LoginConfig INSTANCE = null;

    public String getDiscordToken() {
        return discord_token;
    }

    public String discord_token;
    public LoginConfig() {

    }

    public static LoginConfig getLogin(){
        if(INSTANCE == null){
            System.out.println("Loading login file located at : "+new File("login.json").getAbsolutePath());
            FileReader reader;
            try {
                reader = new FileReader("login.json");
            } catch (FileNotFoundException e) {
                throw new RuntimeException(e);
            }

            try {
                INSTANCE = new Gson().fromJson(reader, LoginConfig.class);
            } catch (JsonSyntaxException | JsonIOException e) {
                throw new RuntimeException(e);
            }

            try {
                reader.close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        return INSTANCE;
    }
}
