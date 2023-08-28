package com.wyrdix.khollobot.plugin.calendar.impl;

import com.wyrdix.khollobot.plugin.calendar.CalendarElementTemplate;
import com.wyrdix.khollobot.plugin.calendar.CalendarInstance;

import java.awt.*;

public class CalendarElementTemplateImpl implements CalendarElementTemplate {
    private final CalendarInstance instance;
    private final String id;
    private final String name;
    private final String teacher;
    private final String room;
    private final Color background;
    private final Color fontColor;

    public CalendarElementTemplateImpl(CalendarInstance instance, String id, String name, String teacher, String room, Color background, Color fontColor) {
        this.instance = instance;
        this.id = id;
        this.name = name;
        this.teacher = teacher;
        this.room = room;
        this.background = background;
        this.fontColor = fontColor;
    }

    @Override
    public CalendarInstance instance() {
        return instance;
    }

    @Override
    public String id() {
        return id;
    }

    @Override
    public String name() {
        return name;
    }

    @Override
    public String teacher() {
        return teacher;
    }

    @Override
    public String room() {
        return room;
    }

    @Override
    public Color background() {
        return background;
    }

    @Override
    public Color fontColor() {
        return fontColor;
    }
}
