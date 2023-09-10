package com.wyrdix.khollobot.plugin.calendar.impl;

import com.wyrdix.khollobot.KUser;
import com.wyrdix.khollobot.plugin.GroupPlugin;
import com.wyrdix.khollobot.plugin.calendar.CalendarElementTemplate;
import com.wyrdix.khollobot.plugin.calendar.CalendarInstance;
import com.wyrdix.khollobot.plugin.calendar.ComplexCalendarElement;

import java.util.*;

public class ComplexCalendarElementImpl implements ComplexCalendarElement {
    private final CalendarInstance instance;
    private final int priority;
    private final int day;
    private final int beginning;
    private final int ending;
    private final String template;
    private final List<String> filter;
    private final String cycleWith;
    private final int[] cycle;
    private final boolean infiniteCycle;

    public ComplexCalendarElementImpl(CalendarInstance instance, int priority, int day, int beginning, int ending, String template, String filter, String cycle, boolean infinite_cycle) {
        this.instance = instance;
        this.priority = priority;
        this.day = day;
        this.beginning = beginning;
        this.ending = ending;
        this.template = template;
        if (filter.contains(";")) this.filter = Collections.singletonList(filter);
        else this.filter = Arrays.stream(filter.split(";")).toList();
        if (cycle != null && !cycle.isEmpty()) {
            String[] split = cycle.split("\\|");
            this.cycleWith = split[0];
            this.cycle = Arrays.stream(split[1].split(";")).mapToInt(Integer::parseInt).toArray();
            this.infiniteCycle = infinite_cycle;
        } else {
            this.cycleWith = null;
            this.cycle = new int[0];
            this.infiniteCycle = false;
        }
    }

    @Override
    public CalendarInstance instance() {
        return instance;
    }

    @Override
    public int priority() {
        return priority;
    }

    @Override
    public int day() {
        return day;
    }

    @Override
    public int beginning() {
        return beginning;
    }

    @Override
    public int ending() {
        return ending;
    }

    @Override
    public CalendarElementTemplate template() {
        return instance().getTemplate(template);
    }

    @Override
    public String cycleWith() {
        return cycleWith;
    }

    @Override
    public int getCycleOnWeek(int week) {
        if (cycle.length == 0) return (-1);
        week--;
        if (week < 0 || (week > cycle.length && !infiniteCycle)) return (-1);
        return cycle[week % cycle.length];
    }

    @Override
    public boolean infiniteCycle() {
        return infiniteCycle;
    }

    @Override
    public boolean filter(int week, KUser user) {
        Map<String, GroupPlugin.GroupConfig> map = new HashMap<>(user.get(GroupPlugin.USER_GROUPS));
        map.values().removeIf(s -> s.subgroup == 0);

        if (cycle.length != 0) {

            if (!map.containsKey(cycleWith)) return false;

            int cycleOnWeek = getCycleOnWeek(week);
            if (cycleOnWeek != map.get(cycleWith).subgroup) return false;

        }

        for (String s : filter) {
            if (s == null) continue;
            if (s.isEmpty()) continue;
            if (s.startsWith("^") == map.containsKey(s)) return false;
        }
        return true;
    }

    @Override
    public List<String> filter() {
        return filter;
    }

    @Override
    public int[] cycle() {
        return cycle;
    }
}
