package com.wyrdix.khollobot.plugin.calendar;

import com.wyrdix.khollobot.KUser;

public interface ComplexCalendarElement extends CalendarElement {
    String cycleWith();

    int getCycleOnWeek(int week);

    boolean infiniteCycle();

    boolean filter(int week, KUser user);
}
