package com.wyrdix.khollobot.calendar;

import com.wyrdix.khollobot.data.UserData;

import java.awt.*;
import java.util.function.BiPredicate;

public final class SchoolDayComponent {

    private final float begin;
    private final float end;
    private final int day;
    private final BiPredicate<UserData, Integer> isElementPresent;
    public String name;
    public String teacher;
    public String room;
    public Color back;
    public Color written;

    public SchoolDayComponent(Type type, float begin, float end, int day, BiPredicate<UserData, Integer> isElementPresent) {

        this.name = type.name;
        this.teacher = type.teacher;
        this.room = type.room;
        this.back = type.back;
        this.written = type.written;
        this.begin = begin;
        this.end = end;
        this.day = day;
        this.isElementPresent = isElementPresent;
    }

    public SchoolDayComponent(float begin, float end, int day, String name, String teacher, String room, Color back, Color written, BiPredicate<UserData, Integer> isElementPresent) {
        this.begin = begin;
        this.end = end;
        this.day = day;
        this.isElementPresent = isElementPresent;
        this.name = name;
        this.teacher = teacher;
        this.room = room;
        this.back = back;
        this.written = written;
    }

    public String getName() {
        return name;
    }

    public String getTeacher() {
        return teacher;
    }

    public String getRoom() {
        return room;
    }

    public Color getBack() {
        return back;
    }

    public Color getWritten() {
        return written;
    }

    @Override
    public String toString() {
        return "SchoolDayComponent{" +
               "begin=" + begin +
               ", end=" + end +
               ", day=" + day +
               ", isElementPresent=" + isElementPresent +
               ", name='" + name + '\'' +
               ", teacher='" + teacher + '\'' +
               ", room='" + room + '\'' +
               ", back=" + back +
               ", written=" + written +
               '}';
    }

    public float getBegin() {
        return begin;
    }

    public float getEnd() {
        return end;
    }

    public int getDay() {
        return day;
    }

    public boolean isElementAbsent(UserData data, int week) {
        return !isElementPresent.test(data, week);
    }

    public enum Type {
        FRANCAIS("Français", "M. Caffier", "G05", Color.decode("#f6b93b"), Color.BLACK),
        INFO("Informatique", "Mme.Montfleur", "G05", Color.decode("#b8e994"), Color.BLACK),
        LV1("LV1", "Prof de LV1", "Anglais G05"),
        LV2("LV2", "Prof de LV2", ""),
        MATH("Mathematique", "M.Valleys", "G05", Color.decode("#e55039"), Color.BLACK),
        PHYSIQUE("Physique", "M.Cousin", "G05", Color.decode("#82ccdd"), Color.BLACK),
        TIPE("Tipe", "", "G05 + S1012"),
        TP_INFO("TP Info", "Mme.Montfleur", "S1012", Color.decode("#78e08f"), Color.BLACK),
        TP_PHYSIQUE("TP Physique", "M.Cousin", "", Color.decode("#3c6382"), Color.BLACK);

        private final String name;
        private final String teacher;
        private final String room;
        private final Color back;
        private final Color written;

        Type(String name, String teacher, String room) {
            this(name, teacher, room, Color.GRAY, Color.BLACK);
        }

        Type(String name, String teacher, String room, Color back, Color written) {

            this.name = name;
            this.teacher = teacher;
            this.room = room;
            this.back = back;
            this.written = written;
        }

        public SchoolDayComponent toComponent(float begin, float end, int day, BiPredicate<UserData, Integer> isElementPresent) {
            return new SchoolDayComponent(this, begin, end, day, isElementPresent);
        }

        public SchoolDayComponent toComponent(float begin, float end, int day) {
            return toComponent(begin, end, day, (userData, integer) -> true);
        }

        public Color getBack() {
            return back;
        }

        public Color getWritten() {
            return written;
        }

        public String getName() {
            return name;
        }

        public String getTeacher() {
            return teacher;
        }

        public String getRoom() {
            return room;
        }
    }


}
