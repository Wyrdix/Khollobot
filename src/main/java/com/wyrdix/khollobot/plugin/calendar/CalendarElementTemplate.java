package com.wyrdix.khollobot.plugin.calendar;

import com.wyrdix.khollobot.plugin.calendar.impl.CalendarElementTemplateImpl;

import java.awt.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public interface CalendarElementTemplate {

    CalendarInstance instance();

    String id();
    String name();

    String teacher();

    String room();

    Color background();

    Color fontColor();

    static CalendarElementTemplate deserialize(CalendarInstance instance, String line){
        assert line.startsWith("DEFINE ");
        line = line.substring("DEFINE ".length());

        assert line.matches("^[A-Z]+\\(");
        final String id = line.substring(0, line.indexOf('('));
        line = line.substring(line.indexOf('('));

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

        final String name = Objects.requireNonNull(args.get("name")).toString();
        final String teacher = Objects.requireNonNull(args.get("teacher")).toString();
        final String room = Objects.requireNonNull(args.get("room")).toString();
        final Color background = new Color(Integer.decode(Objects.requireNonNull(args.get("background")).toString()), false);
        final Color fontColor = new Color(Integer.decode(Objects.requireNonNullElse(args.get("fontColor"), "#000000").toString()), false);

        return new CalendarElementTemplateImpl(instance, id, name, teacher, room, background, fontColor);
    }
}
