package com.wyrdix.khollobot.plugin.calendar;

import com.wyrdix.khollobot.KUser;

import java.util.List;

public interface ComplexCalendarElement extends CalendarElement {
    String cycleWith();

    int getCycleOnWeek(int week);

    boolean infiniteCycle();

    boolean filter(int week, KUser user);

    List<String> filter();

    int[] cycle();
}
