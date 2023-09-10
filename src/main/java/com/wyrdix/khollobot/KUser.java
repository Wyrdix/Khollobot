package com.wyrdix.khollobot;

import com.google.gson.Gson;
import com.google.gson.JsonIOException;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import com.wyrdix.khollobot.field.KField;

import java.io.*;
import java.util.Map;
import java.util.TreeMap;

public class KUser {

    private static final Map<Long, KUser> KUSER_MAP = new TreeMap<>();
    private final long discord_id;
    private JsonObject data = new JsonObject();

    public KUser(long discord_id) {
        this.discord_id = discord_id;
    }

    public static KUser getKUser(long discord_id) {
        return KUSER_MAP.computeIfAbsent(discord_id, discord_id1 -> {
            KUser user = new KUser(discord_id1);
            user.load();
            return user;
        });
    }

    public JsonObject getData() {
        return data;
    }

    public <T> void set(KField<T> field, T value) {
        field.set(this, field.sanitize(value));
        try {
            save();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public <T> T get(KField<T> field) {
        return field.get(this);
    }


    private void load() {
        Gson gson = new Gson();
        FileReader reader;
        try {
            reader = new FileReader("data/users/" + getDiscordId() + ".json");
        } catch (FileNotFoundException e) {
            return;
        }

        try {
            data = gson.fromJson(reader, JsonObject.class);
        } catch (JsonSyntaxException | JsonIOException e) {
            data = new JsonObject();
            return;
        }

        try {
            reader.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void save() throws IOException {
        new File("data/users").mkdirs();
        FileWriter writer = new FileWriter("data/users/" + getDiscordId() + ".json");

        writer.write(data.toString());

        writer.close();
    }

    public long getDiscordId() {
        return discord_id;
    }
}
