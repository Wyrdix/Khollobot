package com.wyrdix.khollobot;

import com.google.gson.*;
import com.wyrdix.khollobot.plugin.Plugin;
import com.wyrdix.khollobot.plugin.PluginInfo;

import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class GlobalConfig {

    private static GlobalConfig INSTANCE = null;
    private final JsonObject config;

    private final Map<String, Plugin.PluginConfig> configMap = new HashMap<>();

    public GlobalConfig(JsonObject config) {

        this.config = config;
    }

    public static GlobalConfig getGlobalConfig() {
        if (INSTANCE == null) {

            InputStream inputStream = null;
            File globalFile = new File("global.json");
            try {

                if(!globalFile.exists()){
                    InputStream stream = LoginConfig.class.getClassLoader().getResourceAsStream("global.json");
                    assert stream != null;
                    Files.copy(stream, globalFile.toPath());
                }

                inputStream = new FileInputStream("global.json");
            } catch (IOException e) {
                e.printStackTrace();
            }
            assert inputStream != null;
            InputStreamReader streamReader = new InputStreamReader(inputStream);

            System.out.println("Loading global config resource file, may be located at : " + globalFile.getAbsolutePath());

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
        return (T) configMap.computeIfAbsent(id, k-> {

            Gson gson;

            if (info.config().isAssignableFrom(JsonDeserializer.class)) {
                try {
                    Class<? extends JsonDeserializer<?>> config = (Class<? extends JsonDeserializer<?>>) info.config();
                    gson = new GsonBuilder().
                            registerTypeAdapter(config, config.getConstructor().newInstance()).create();
                } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
                    e.printStackTrace();
                    gson = new Gson();
                }
            }else gson = new Gson();

            return gson.fromJson(config.get(k), info.config());
        });
    }

    public void save() {

        Gson gson = new Gson();

        for (Map.Entry<String, Plugin.PluginConfig> entry : configMap.entrySet()) {
            String key = entry.getKey();
            Plugin.PluginConfig value = entry.getValue();
            JsonElement serialized = gson.toJsonTree(value);

            config.add(key, serialized);
        }

        String raw = gson.toJson(config);
        try {
            Files.write(Path.of("global.json"), Collections.singleton(raw));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
