package com.wyrdix.data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class SimpleData<T extends SimpleData<T>> implements Data<T> {

    protected final Map<Class<? extends T>, T> members = new HashMap<>();

    public SimpleData() {

    }


    public Map<Class<? extends T>, T> members() {
        return members;
    }

    @SuppressWarnings("unchecked")
    public <S extends T> S get(Class<S> clazz) {
        DataInfo info = clazz.getAnnotation(DataInfo.class);
        if (info == null)
            throw new IllegalArgumentException("Class is not annotated with DataInfo : " + clazz.getName());
        if (!info.parent().equals(Data.class)) {
            List<Class<? extends T>> parents = new ArrayList<>();
            parents.add(clazz);
            while (!parents.get(0).getAnnotation(DataInfo.class).parent().equals(Data.class) &&
                    !parents.get(0).getAnnotation(DataInfo.class).parent().equals(getClass())) {
                DataInfo annotation = parents.get(parents.size() - 1)
                        .getAnnotation(DataInfo.class);
                if (parents.contains(annotation.parent())) {
                    throw new IllegalArgumentException("Going through a cyclic heritage : " + annotation.parent() + " " + parents);
                }
                parents.add(0, (Class<? extends T>) annotation.parent());
            }
            if (parents.size() > 1) return (S) get(parents.toArray(new Class[0]));
        }

        return (S) members.computeIfAbsent(clazz, this::createIfAbsent);
    }

    protected <S extends T> S createIfAbsent(Class<S> clazz) {
        return null;
    }

    @SafeVarargs
    public final T get(Class<? extends T>... clazzes) {
        assert clazzes != null && clazzes.length != 0;
        T data = get(clazzes[0]);
        for (int index = 1; index < clazzes.length; index++) {
            Class<? extends T> clazz = clazzes[index];
            data = data.get(clazz);
        }
        return data;
    }

}
