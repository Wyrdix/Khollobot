package com.wyrdix.khollobot;

import com.google.gson.*;
import com.wyrdix.khollobot.plugin.Plugin;
import com.wyrdix.khollobot.plugin.PluginInfo;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

public class GlobalConfig {

    private static GlobalConfig INSTANCE = null;
    private JsonObject config;

    private Map<String, Plugin.PluginConfig> configMap = new HashMap<>();

    public GlobalConfig(JsonObject config) {

        this.config = config;
    }

    public static GlobalConfig getGlobalConfig() {
        if (INSTANCE == null) {

            InputStream inputStream = GlobalConfig.class.getClassLoader().getResourceAsStream("global.json");
            assert inputStream != null;
            InputStreamReader streamReader = new InputStreamReader(inputStream);

            System.out.println("Loading login resource file, may be located at : " + new File("global.json").getAbsolutePath());

            try {
                INSTANCE = new GlobalConfig(new Gson().fromJson(streamReader, JsonObject.class));
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

    @SuppressWarnings("unchecked")
    public <T extends Plugin.PluginConfig> T getConfig(Class<? extends Plugin> plugin) {
        PluginInfo info = plugin.getAnnotation(PluginInfo.class);
        if (info == null) throw new RuntimeException("Plugin class should be annotated with @PluginInfo");

        String id = info.id();
        return (T) configMap.computeIfAbsent(id, k->
                new Gson().fromJson(config.get(k), info.config()));
    }
}
