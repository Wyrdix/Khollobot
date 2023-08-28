package com.wyrdix.khollobot.plugin.calendar;

import java.io.File;
import java.util.List;

public interface CalendarInstance {
    File CALENDAR_FOLDER = new File("calendars");
    String id();
    void load();
    CalendarElementTemplate getTemplate(String name);

    List<CalendarElement> elements();
}
