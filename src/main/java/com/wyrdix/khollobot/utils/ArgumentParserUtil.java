package com.wyrdix.khollobot.utils;

import java.util.HashMap;
import java.util.Map;

public final class ArgumentParserUtil {

    public static Map<String, Object> getMapFromString(String line) {
        final Map<String, Object> args = new HashMap<>();

        String argName;
        String argValue;
        String[] split;
        while (!line.isEmpty()) {
            assert line.matches("^[a-z]+=\".*\"");
            argName = line.substring(0, line.indexOf('='));
            split = line.split("\"", 3);
            argValue = split[1];

            args.put(argName, argValue);
            line = split[2];
            if (line.startsWith(",")) line = line.substring(1).trim();
            else assert line.isEmpty();
        }

        return args;
    }
}
