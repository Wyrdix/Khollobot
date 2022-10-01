package com.wyrdix.khollobot.data;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.wyrdix.data.json.DataJsonNode;
import com.wyrdix.data.utils.JsonOptional;

public class UserData extends DataJsonNode {

    private final long id_long;
    private boolean lv2;
    private final String discord_name;
    private String first_name;
    private String last_name;
    private int group;
    private int state = 0;
    private boolean dmMail;

    public UserData(JsonObject object) {
        super(object);

        id_long = object.get("id_long").getAsLong();
        discord_name = object.get("discord_name").getAsString();
        first_name = object.get("first_name").getAsString();
        last_name = object.get("second_name").getAsString();
        group = object.get("group").getAsInt();
        dmMail = JsonOptional.ofNullable(object.get("dm_mail")).map(JsonElement::getAsBoolean).orElse(false);
        lv2 = JsonOptional.ofNullable(object.get("lv2")).map(JsonElement::getAsBoolean).orElse(true);
    }

    public UserData(long id_long, String discord_name) {
        super(new JsonObject());
        this.id_long = id_long;
        this.discord_name = discord_name;
        this.first_name = "";
        this.last_name = "";
        this.group = -1;
        this.dmMail = false;
    }

    public long getId_long() {
        return id_long;
    }

    public String getFirst_name() {
        return first_name;
    }

    public String getLast_name() {
        return last_name;
    }

    @Override
    public JsonObject collect() {
        JsonObject collect = super.collect();

        collect.addProperty("id_long", id_long);
        collect.addProperty("discord_name", discord_name);
        collect.addProperty("first_name", first_name);
        collect.addProperty("second_name", last_name);
        collect.addProperty("group", group);
        collect.addProperty("dm_mail", dmMail);
        collect.addProperty("lv2", lv2);


        return collect;
    }

    public void setName(String first_name, String last_name) {
        this.first_name = first_name;
        this.last_name = last_name.toUpperCase();
    }

    public int getGroup() {
        return group;
    }

    public void setGroup(int group) {
        this.group = group;
    }

    @Override
    public String toString() {
        return "UserData{" +
               "id_long=" + id_long +
               ", discord_name='" + discord_name + '\'' +
               ", first_name='" + first_name + '\'' +
               ", second_name='" + last_name + '\'' +
               ", group=" + group +
               '}';
    }

    public int getState() {
        return state;
    }

    public UserData setState(int state) {
        this.state = state;
        return this;
    }

    public void setDmMail(boolean dmMail) {
        this.dmMail = dmMail;
    }

    public boolean getDmMail() {
        return dmMail;
    }

    public boolean getLv2() {
        return lv2;
    }

    public void setLv2(boolean lv2) {
        this.lv2 = lv2;
    }
}
