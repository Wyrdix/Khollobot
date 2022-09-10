package com.wyrdix.khollobot.data;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.wyrdix.data.json.DataJsonNode;
import com.wyrdix.data.utils.JsonOptional;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.*;

public class DataFile extends DataJsonNode {

    private static DataFile INSTANCE = null;

    private final Map<Long, UserData> users = new HashMap<>();

    public DataFile(JsonObject object) {
        super(object);

        JsonOptional.ofNullable(object.getAsJsonArray("users"))
                .ifPresent(array -> users.putAll(getMap(obj->obj.get("id_long").getAsLong(), UserData.class, array, true)));
    }

    public Optional<UserData> getUser(long id){
        return Optional.ofNullable(users.get(id));
    }

    public boolean newUser(long id, String discord_name){
        if(users.containsKey(id)) return false;
        users.put(id, new UserData(id, discord_name));
        save();
        return true;
    }

    @Override
    public JsonObject collect() {
        JsonObject collect = super.collect();

        JsonArray usersArray = new JsonArray();
        users.values().forEach(s->usersArray.add(s.collect()));
        collect.add("users", usersArray);

        return collect;
    }

    public static DataFile getInstance(){
        if(INSTANCE == null) load();
        return INSTANCE;
    }

    private static void load() {
        File file = new File("data.json");
        try {
            JsonObject obj = new Gson().fromJson(Files.readAllLines(file.toPath()).stream().reduce("", String::concat), JsonObject.class);
            INSTANCE = new DataFile(obj);
        } catch (IOException e) {
            INSTANCE = new DataFile(new JsonObject());
        }
    }

    public static void save(){
        File file = new File("data.json");
        String raw = new Gson().toJson(getInstance().collect());
        try {
            Files.write(file.toPath(), raw.getBytes(StandardCharsets.UTF_8));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}
