package com.wyrdix.data.utils;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.function.Supplier;

public final class ReflectionUtils {
    private ReflectionUtils() {
    }

    public static <T> Optional<Supplier<T>> getGenerator(Class<T> clazz, List<Object> params) {
        params = new ArrayList<>(params);
        params.removeIf(Objects::isNull);
        Constructor<?> cons = null;
        int count = -1;
        List<Class<?>> classes = new ArrayList<>(params.size());
        for (Object param : params) {
            classes.add(param.getClass());
        }
        for (Constructor<?> constructor : clazz.getDeclaredConstructors()) {
            List<Class<?>> list = new ArrayList<>(Arrays.asList(constructor.getParameterTypes()));
            for (Class<?> clazz2 : classes) {
                for (int i = 0; i < list.size(); i++) {
                    if (list.get(i).isAssignableFrom(clazz2)) {
                        list.remove(i);
                        break;
                    }
                }
                if (list.isEmpty()) break;
            }
            if (list.isEmpty()) {
                if (constructor.getParameterCount() > count) {
                    cons = constructor;
                    count = constructor.getParameterCount();
                }
            }
        }
        try {
            if (cons == null) return Optional.of(() -> {
                try {
                    return clazz.newInstance();
                } catch (InstantiationException | IllegalAccessException e) {
                    e.printStackTrace();
                }
                return null;
            });

            List<Object> finalParams = new ArrayList<>();
            List<Class<?>> finalList = Arrays.asList(cons.getParameterTypes());
            List<Class<?>> list = new ArrayList<>(finalList);
            for (int j = 0; j < classes.size(); j++) {
                Class<?> clazz2 = classes.get(j);
                for (int i = 0; i < list.size(); i++) {
                    Class<?> clazz3 = list.get(i);
                    if (clazz3.isAssignableFrom(clazz2)) {
                        finalParams.add(clazz3.cast(params.get(j)));
                        list.remove(i);
                        break;
                    }
                }
                if (list.isEmpty()) break;
            }

            finalParams.sort(Comparator.comparingInt(s -> finalList.indexOf(s.getClass())));

            Constructor<?> finalCons = cons;
            return Optional.of(() -> {
                try {
                    finalCons.setAccessible(true);
                    return clazz.cast(finalCons.newInstance(finalParams.toArray()));
                } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
                    e.printStackTrace();
                }
                return null;
            });
        } catch (Exception e) {
            e.printStackTrace();
            e.printStackTrace();
        }
        return Optional.empty();
    }

    public static <T> Optional<Supplier<T>> getGenerator(Class<T> clazz, Object... params) {
        return getGenerator(clazz, Arrays.asList(params));
    }
}
