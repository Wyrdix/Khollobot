package com.wyrdix.data;

public interface Data<T extends Data<T>> {
    default String id() {
        DataInfo annotation = getClass().getAnnotation(DataInfo.class);
        if (annotation == null)
            throw new IllegalStateException(getClass().getName() + " is a data type but isn't annotated with @DataInfo");
        return annotation.id();
    }

    <S extends T> S get(Class<S> clazz);

    T get(Class<? extends T>... clazzes);
}
