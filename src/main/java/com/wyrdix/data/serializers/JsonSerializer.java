package com.wyrdix.data.serializers;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.wyrdix.data.json.DataJsonNode;
import com.wyrdix.data.utils.ReflectionUtils;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Optional;
import java.util.function.Supplier;

public final class JsonSerializer {
    private static final Gson GSON = new Gson();

    private JsonSerializer() {
    }

    public static <T extends DataJsonNode> void serialize(T object, File file) {
        JsonObject obj = object.collect();

        //noinspection ResultOfMethodCallIgnored
        file.getParentFile().mkdirs();

        String json = GSON.toJson(obj);

        FileWriter fileWriter = null;
        BufferedWriter bufferedWriter = null;

        try {

            //noinspection ResultOfMethodCallIgnored
            file.createNewFile();

            fileWriter = new FileWriter(file);
            bufferedWriter = new BufferedWriter(fileWriter);

            bufferedWriter.write(json);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (bufferedWriter != null) {
                try {
                    bufferedWriter.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            if (fileWriter != null) {
                try {
                    fileWriter.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static <T> Optional<T> deserialize(Class<T> clazz, File file, Object... args) {
        try {
            return deserialize(clazz, GSON.fromJson(Files.readString(file.toPath()), JsonObject.class), args);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }

    public static <T> Optional<T> deserialize(Class<T> clazz, JsonObject obj, Object... args) {
        if (DataJsonNode.class.isAssignableFrom(clazz)) {
            Object[] objects = new Object[args.length + 1];
            objects[0] = obj;
            System.arraycopy(args, 0, objects, 1, args.length);
            return ReflectionUtils.getGenerator(clazz, objects).map(Supplier::get);
        }

        return Optional.empty();
    }
}
