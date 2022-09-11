package com.wyrdix.khollobot.calendar;

import net.fortuna.ical4j.data.CalendarBuilder;
import net.fortuna.ical4j.data.ParserException;
import net.fortuna.ical4j.model.Calendar;
import net.fortuna.ical4j.model.Component;

import java.io.IOException;
import java.io.InputStream;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

public class CalendarManager {

    private static Calendar holidayCalendar;
    private static ZoneId zoneId = ZoneId.of("Europe/Paris");
    private static TimeZone timeZone = TimeZone.getTimeZone(zoneId);
    private static List<Holiday> holidays = new ArrayList<>();
    private static java.util.Calendar doomsday = java.util.Calendar.getInstance(timeZone);

    public static int getCurrentWeek(){

        java.util.Calendar current = java.util.Calendar.getInstance(timeZone);
        Instant plus = current.toInstant().plus(2, ChronoUnit.DAYS);
        long between = 1 + ChronoUnit.WEEKS.between(doomsday.toInstant().atZone(zoneId), plus.atZone(zoneId));

        for (Holiday holiday : holidays) {
            if(!holiday.begin.isBefore(current.toInstant().atZone(zoneId))) continue;
            if(holiday.end.isAfter(current.toInstant().atZone(zoneId))){
                between -= ChronoUnit.WEEKS.between(holiday.begin, plus.atZone(zoneId));
            }else between-=2;
        }

        return (int) between;
    }

    public static void load() {
        InputStream fin = Thread.currentThread().getContextClassLoader().getResourceAsStream("data/Zone-B.ics");
        CalendarBuilder builder = new CalendarBuilder();
        try {
            holidayCalendar = builder.build(fin);

            java.util.Calendar calendar = doomsday;
            calendar.set(2022, java.util.Calendar.SEPTEMBER, 5);


            holidayCalendar.getComponents(Component.VEVENT).forEach(event -> {
                DateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");

                String name = event.getProperty("SUMMARY").get().getValue();
                if (!name.startsWith("Vacances")) return;

                Date begin = null;
                Date end = null;
                try {
                    begin = dateFormat.parse(event.getProperty("DTSTART").get().getValue());
                    end = dateFormat.parse(event.getProperty("DTEND").get().getValue());
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                assert begin != null && end != null;

                ZonedDateTime begin_zdt = begin.toInstant().atZone(zoneId).plusSeconds(1).truncatedTo(ChronoUnit.DAYS);
                ZonedDateTime end_zdt = end.toInstant().atZone(zoneId).minusSeconds(1).truncatedTo(ChronoUnit.DAYS);

                if(end_zdt.toInstant().getEpochSecond() - begin_zdt.toInstant().getEpochSecond() < 86400) return;

                if (begin_zdt.isBefore(calendar.toInstant().atZone(zoneId))) return;
                holidays.add(new Holiday(begin_zdt, end_zdt, name));
            });

        } catch (IOException | ParserException e) {
            e.printStackTrace();
        }

    }

    private record Holiday(ZonedDateTime begin, ZonedDateTime end, String name) {

    }
}
