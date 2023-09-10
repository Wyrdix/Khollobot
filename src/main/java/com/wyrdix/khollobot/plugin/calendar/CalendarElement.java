package com.wyrdix.khollobot.plugin.calendar;

import com.wyrdix.khollobot.KUser;
import com.wyrdix.khollobot.plugin.calendar.impl.CalendarElementImpl;
import com.wyrdix.khollobot.plugin.calendar.impl.ComplexCalendarElementImpl;
import com.wyrdix.khollobot.utils.ArgumentParserUtil;

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
        final Map<String, Object> args = ArgumentParserUtil.getMapFromString(line);

        final int priority = Integer.parseInt(Objects.requireNonNullElse(args.get("priority"), "0").toString());
        final int day = Integer.parseInt(Objects.requireNonNull(args.get("day")).toString());
        final int beginning = Integer.parseInt(Objects.requireNonNull(args.get("beginning")).toString());
        final int ending = Integer.parseInt(Objects.requireNonNull(args.getOrDefault("ending", String.valueOf((beginning + 1))).toString()));
        final String template = Objects.requireNonNull(args.get("template")).toString();

        final String filter = args.getOrDefault("filter", "").toString();
        final String cycle = args.getOrDefault("cycle", "").toString();
        final boolean infiniteCycle = args.getOrDefault("infinite_cycle", "false").toString().equals("true");

        if (filter.isEmpty() && cycle.isEmpty() && !infiniteCycle)
            return new CalendarElementImpl(instance, priority, day, beginning, ending, template);
        else
            return new ComplexCalendarElementImpl(instance, priority, day, beginning, ending, template, filter, cycle, infiniteCycle);
    }

    default boolean filter(int week, KUser user) {
        return true;
    }
}
