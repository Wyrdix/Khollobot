package com.wyrdix.khollobot.plugin.calendar;

import com.wyrdix.khollobot.plugin.calendar.impl.CalendarElementImpl;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public interface CalendarElement {
    int priority();

    int day();
    int beginning();

    int ending();

    CalendarElementTemplate template();

    CalendarInstance instance();

    static CalendarElement deserialize(CalendarInstance instance, String line){
        assert line.startsWith("ADD");
        line = line.substring("ADD".length());

        assert line.matches("^\\(.*\\)$");
        line = line.substring(1, line.length()-1);
        final Map<String, Object> args = new HashMap<>();

        String argName;
        String argValue;
        String[] split;
        while (!line.isEmpty()){
            assert line.matches("^[a-z]+=\".*\"");
            argName = line.substring(0, line.indexOf('='));
            split = line.split("\"", 3);
            argValue = split[1];

            args.put(argName, argValue);
            line = split[2];
            if(line.startsWith(",")) line = line.substring(1).trim();
            else assert line.isEmpty();
        }

        final int priority = Integer.parseInt(Objects.requireNonNullElse(args.get("priority"), "0").toString());
        final int day = Integer.parseInt(Objects.requireNonNull(args.get("day")).toString());
        final int beginning = Integer.parseInt(Objects.requireNonNull(args.get("beginning")).toString());
        final int ending = Integer.parseInt(Objects.requireNonNull(args.get("ending")).toString());
        final String template = Objects.requireNonNull(args.get("template")).toString();

        return new CalendarElementImpl(instance, priority, day, beginning, ending, template);
    }
}
