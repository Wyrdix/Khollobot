package com.wyrdix.data.json;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.wyrdix.data.SimpleData;
import com.wyrdix.data.utils.JsonOptional;
import com.wyrdix.data.utils.JsonUtils;
import com.wyrdix.data.utils.ReflectionUtils;

import java.util.*;
import java.util.function.Function;
import java.util.function.Supplier;

public class DataJsonNode extends SimpleData<DataJsonNode> implements JsonSerializable {

    private JsonObject object;

    public DataJsonNode(JsonObject object) {
        this.object = object;
    }

    public JsonObject getJsonObject() {
        return object;
    }

    protected void setJsonObject(JsonObject object) {
        if (this.object == null) this.object = new JsonObject();
        for (Map.Entry<String, JsonElement> entry : object.entrySet())
            object.add(entry.getKey(), entry.getValue());
    }

    protected <S extends DataJsonNode> S createIfAbsent(Class<S> clazz, JsonObject object) {
        Optional<? extends Supplier<S>> generator = ReflectionUtils.getGenerator(clazz, object);
        Supplier<S> supplier = generator.orElseThrow(() -> new RuntimeException("Missing constructor containing only json object"));
        return supplier.get();
    }

    @Override
    protected <S extends DataJsonNode> S createIfAbsent(Class<S> clazz) {
        return createIfAbsent(clazz, object);
    }

    @Override
    public JsonObject collect() {
        JsonObject obj = JsonUtils.deepClone(getJsonObject());
        for (DataJsonNode member : members().values()) {
            if (member == null) continue;
            obj.add(member.id(), member.collect());
        }
        return obj;
    }

    protected <S extends DataJsonNode> List<S> getList(Class<S> clazz, JsonArray array) {
        return getList(clazz, array, false);
    }

    protected <S extends DataJsonNode> List<S> getList(Class<S> clazz, JsonArray array, boolean silent) {
        List<S> list = new ArrayList<>();
        for (JsonElement element : array) {
            JsonOptional.ofNullable(element).mapToJson(JsonElement::getAsJsonObject)
                    .ifPresent((object) -> {
                        S val;
                        try {
                            val = createIfAbsent(clazz, object);
                        } catch (Exception e) {
                            if (!silent) e.printStackTrace();
                            return;
                        }
                        list.add(val);
                    });
        }

        list.removeIf(Objects::isNull);
        return list;
    }

    protected <S extends DataJsonNode, T> Map<T, S> getMap(Function<JsonObject, T> keyGetter, Class<S> clazz, JsonArray array, boolean silent) {
        Map<T, S> map = new HashMap<>();
        for (JsonElement element : array) {
            JsonOptional.ofNullable(element).mapToJson(JsonElement::getAsJsonObject)
                    .ifPresent((object) -> {
                        T id;
                        S val;
                        try {
                            id = keyGetter.apply(object);
                            val = createIfAbsent(clazz, object);

                            assert id != null;
                            assert val != null;
                        } catch (Exception e) {
                            if (!silent) e.printStackTrace();
                            return;
                        }
                        map.put(id, val);
                    });
        }
        return map;
    }
}
