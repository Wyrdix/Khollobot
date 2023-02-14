package com.wyrdix.khollobot.calendar;

import com.wyrdix.khollobot.data.UserData;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.BiPredicate;

import static com.wyrdix.khollobot.calendar.SchoolDayComponent.Type.*;

public class CalendarFrame {

    private final static int WIDTH_OFFSET = 50;
    private final static int END_COLUMN_OFFSET = 50;
    private final static int HEIGHT_OFFSET = 50;
    private final static int END_HEIGHT_OFFSET = 50;
    private final static int COLUMN_WIDTH = 200;
    private final static int SEGEMENT_HEIGHT = 75;

    private final static int DAYS_PER_WEEK = 5;
    private final static int SEGMENTS_PER_DAY = 11;
    private static final Color KHOLLE_BACK_COLOR = Color.decode("#e58e26");
    private static final List<SchoolDayComponent> weekly_component = new ArrayList<>();
    private static final List<SchoolDayComponent> kholle_component = new ArrayList<>();
    private static final List<SchoolDayComponent> ds_component = new ArrayList<>();

    public static BufferedImage getCalendar(UserData data, int week) {
        BufferedImage image = new BufferedImage(WIDTH_OFFSET + COLUMN_WIDTH * DAYS_PER_WEEK + END_COLUMN_OFFSET, HEIGHT_OFFSET + SEGEMENT_HEIGHT * SEGMENTS_PER_DAY + END_HEIGHT_OFFSET, BufferedImage.TYPE_INT_RGB);

        Graphics2D g2d = (Graphics2D) image.getGraphics();
        g2d.setColor(Color.LIGHT_GRAY);
        g2d.fillRect(0, 0, image.getWidth(), image.getHeight());

        g2d.setColor(Color.BLACK);
        g2d.fillRect(WIDTH_OFFSET, HEIGHT_OFFSET, COLUMN_WIDTH * DAYS_PER_WEEK, SEGEMENT_HEIGHT * SEGMENTS_PER_DAY);


        g2d.setColor(Color.LIGHT_GRAY);
        g2d.fillRect(WIDTH_OFFSET + 1, HEIGHT_OFFSET + 1, COLUMN_WIDTH * DAYS_PER_WEEK - 2, SEGEMENT_HEIGHT * SEGMENTS_PER_DAY - 2);

        g2d.setColor(Color.WHITE);

        for (SchoolDayComponent component : weekly_component) {
            if (component.isElementAbsent(data, week)) continue;
            drawSchoolDayComponent(image, g2d, component);
        }
        for (SchoolDayComponent component : kholle_component) {
            if (component.isElementAbsent(data, week)) continue;
            drawSchoolDayComponent(image, g2d, component);
        }

        g2d.setFont(new Font(g2d.getFont().getName(), Font.BOLD, 30));

        for (SchoolDayComponent component : ds_component) {
            if (component.isElementAbsent(data, week)) continue;
            drawSchoolDayComponent(image, g2d, component);
        }

        g2d.setColor(Color.BLACK);

        g2d.setFont(new Font(g2d.getFont().getName(), Font.BOLD, 30));

        drawCenteredString(g2d, WIDTH_OFFSET, 0, COLUMN_WIDTH, HEIGHT_OFFSET, "Lundi");
        drawCenteredString(g2d, WIDTH_OFFSET + COLUMN_WIDTH, 0, COLUMN_WIDTH, HEIGHT_OFFSET, "Mardi");
        drawCenteredString(g2d, WIDTH_OFFSET + 2 * COLUMN_WIDTH, 0, COLUMN_WIDTH, HEIGHT_OFFSET, "Mercredi");
        drawCenteredString(g2d, WIDTH_OFFSET + 3 * COLUMN_WIDTH, 0, COLUMN_WIDTH, HEIGHT_OFFSET, "Jeudi");
        drawCenteredString(g2d, WIDTH_OFFSET + 4 * COLUMN_WIDTH, 0, COLUMN_WIDTH, HEIGHT_OFFSET, "Vendredi");

        g2d.setFont(new Font(g2d.getFont().getName(), Font.PLAIN, 20));

        for (int i = 0; i <= SEGMENTS_PER_DAY; i++) {
            int v = 8 + i;
            drawCenteredString(g2d, 0, HEIGHT_OFFSET + i * SEGEMENT_HEIGHT - 20, WIDTH_OFFSET - 20, 40, (v < 10 ? " " + v : v + ""));
            drawCenteredString(g2d, 30, HEIGHT_OFFSET + i * SEGEMENT_HEIGHT - 20, 20, 40, "H");
        }

        drawCenteredString(g2d, WIDTH_OFFSET, image.getHeight() - HEIGHT_OFFSET, image.getWidth() - 2 * WIDTH_OFFSET, HEIGHT_OFFSET, "Semaine : "+week);

        g2d.dispose();

        return image;
    }

    public static void init() throws IOException {
        initWeekly();
        initKholle();
        initDS();
    }

    private static void initDS() {

        ds_component.add(new SchoolDayComponent(13, 17, 2, "Devoir \n de Mathématique", "", "", Color.decode("#b71540"), Color.BLACK, (data, integer) -> integer == 2));
        ds_component.add(new SchoolDayComponent(13, 17, 2, "Devoir \n de Physique", "", "", Color.decode("#b71540"), Color.BLACK, (data, integer) -> integer == 3));
        ds_component.add(new SchoolDayComponent(15, 17, 4, "Devoir \n de Français", "", "", Color.decode("#b71540"), Color.BLACK, (data, integer) -> integer == 3));
        ds_component.add(new SchoolDayComponent(13, 17, 2, "Devoir \n d'Informatique", "", "", Color.decode("#b71540"), Color.BLACK, (data, integer) -> integer == 4));
        ds_component.add(new SchoolDayComponent(13, 17, 4, "LV1", "", "", Color.decode("#b71540"), Color.BLACK, (data, integer) -> integer == 4));
        ds_component.add(new SchoolDayComponent(13, 17, 2, "Devoir \n d Mathématique", "", "", Color.decode("#b71540"), Color.BLACK, (data, integer) -> integer == 5));
        ds_component.add(new SchoolDayComponent(13, 17, 2, "Devoir \n de Physique", "", "", Color.decode("#b71540"), Color.BLACK, (data, integer) -> integer == 6));
        ds_component.add(new SchoolDayComponent(13, 17, 2, "Devoir \n d Mathématique", "", "", Color.decode("#b71540"), Color.BLACK, (data, integer) -> integer == 7));
        ds_component.add(new SchoolDayComponent(13, 17, 2, "Devoir \n d'Informatique", "", "", Color.decode("#b71540"), Color.BLACK, (data, integer) -> integer == 8));
        ds_component.add(new SchoolDayComponent(13, 17, 2, "Devoir \n d Mathématique", "", "", Color.decode("#b71540"), Color.BLACK, (data, integer) -> integer == 9));

        ds_component.add(new SchoolDayComponent(13, 16, 2, "Mathématique", "M.Valleys", "G 05", MATH.getBack(), MATH.getWritten(), (data, integer) -> integer == 10));

        ds_component.add(new SchoolDayComponent(13, 17, 2, "Devoir \n de Physique", "", "", Color.decode("#b71540"), Color.BLACK, (data, integer) -> integer == 11));
        ds_component.add(new SchoolDayComponent(13, 17, 2, "Devoir \n d'Informatique", "", "", Color.decode("#b71540"), Color.BLACK, (data, integer) -> integer == 12));
        ds_component.add(new SchoolDayComponent(13, 17, 2, "Devoir \n d Mathématique", "", "", Color.decode("#b71540"), Color.BLACK, (data, integer) -> integer == 13));
        ds_component.add(new SchoolDayComponent(15, 17, 2, "Devoir \n de Français", "", "", Color.decode("#b71540"), Color.BLACK, (data, integer) -> integer == 14));
        ds_component.add(new SchoolDayComponent(13, 17, 2, "Devoir \n de Physique", "", "", Color.decode("#b71540"), Color.BLACK, (data, integer) -> integer == 15));
        ds_component.add(new SchoolDayComponent(13, 17, 4, "LV1", "", "", Color.decode("#b71540"), Color.BLACK, (data, integer) -> integer == 15));
        ds_component.add(new SchoolDayComponent(13, 17, 2, "Devoir \n d Mathématique", "", "", Color.decode("#b71540"), Color.BLACK, (data, integer) -> integer == 16));
        ds_component.add(new SchoolDayComponent(13, 17, 2, "Devoir \n d'Infomatique", "", "", Color.decode("#b71540"), Color.BLACK, (data, integer) -> integer == 17));
        ds_component.add(new SchoolDayComponent(13, 17, 2, "Devoir \n de Physique", "", "", Color.decode("#b71540"), Color.BLACK, (data, integer) -> integer == 18));
        ds_component.add(new SchoolDayComponent(13, 17, 2, "Devoir \n d Mathématique", "", "", Color.decode("#b71540"), Color.BLACK, (data, integer) -> integer == 19));
        //ds_component.add(new SchoolDayComponent(13, 17, 2, "Concours Blanc", "Le bot dit certainement de la merde", "", Color.decode("#b71540"), Color.BLACK, (data, integer) -> integer == 20));

        ds_component.add(new SchoolDayComponent(13, 17, 2, "Devoir \n d Mathématique", "", "", Color.decode("#b71540"), Color.BLACK, (data, integer) -> integer == 21));
        ds_component.add(new SchoolDayComponent(13, 17, 2, "Devoir \n d'Infomatique", "", "", Color.decode("#b71540"), Color.BLACK, (data, integer) -> integer == 22));

        ds_component.add(new SchoolDayComponent(8, 12, 0, "Concours \n Blanc \n Math 1", "", "", MATH.getBack(), MATH.getWritten(), (data, integer) -> integer == 20));
        ds_component.add(new SchoolDayComponent(13, 17, 0, "Concours \n Blanc \n LV1", "", "", LV1.getBack(), LV1.getWritten(), (data, integer) -> integer == 20));
        ds_component.add(new SchoolDayComponent(8, 12, 1, "Concours \n Blanc \n Physique", "", "", PHYSIQUE.getBack(), PHYSIQUE.getWritten(), (data, integer) -> integer == 20));
        ds_component.add(new SchoolDayComponent(13, 17, 1, "Concours \n Blanc \n Info", "", "", INFO.getBack(), INFO.getWritten(), (data, integer) -> integer == 20));
        ds_component.add(new SchoolDayComponent(8, 12, 2, "Concours \n Blanc \n Français", "", "", FRANCAIS.getBack(), FRANCAIS.getWritten(), (data, integer) -> integer == 20));
        ds_component.add(new SchoolDayComponent(13, 17, 2, "Concours \n Blanc \n Math 2", "", "", MATH.getBack(), MATH.getWritten(), (data, integer) -> integer == 20));

    }

    private static void initKholle() throws IOException {
        InputStream khollestream = Thread.currentThread().getContextClassLoader().getResourceAsStream("data/kholloscope.tex");
        assert khollestream != null;
        BufferedReader reader = new BufferedReader(new InputStreamReader(khollestream));

        String matiere = "";
        String line = reader.readLine();

        while (line != null) {
            String[] split = line.split(" ");
            if (split.length > 1) {

                String teacher = split[0];
                String raw_day = split[1].substring(1);
                int day;
                switch (raw_day) {
                    case "lun" -> day = 0;
                    case "mar" -> day = 1;
                    case "mer" -> day = 2;
                    case "jeu" -> day = 3;
                    case "ven" -> day = 4;
                    default -> day = -1;
                }

                if (day == -1) {
                    System.err.println(raw_day + " couldn't be parsed");
                    continue;
                }

                int hour = Integer.parseInt(split[2].substring(0, 2));

                String room = split[3].substring(0, split[3].length()-1).replace('_', ' ');

                split = line.split("&");
                split[split.length - 1] = split[split.length - 1].replace("\\\\", "");

                split = Arrays.copyOfRange(split, 1, split.length - 1);

                var ref = new Object() {
                    int[] groups = new int[0];
                    int[] groupslv2 = new int[0];
                };

                ref.groups = new int[split.length];
                ref.groupslv2 = new int[split.length];
                for (int i = 0; i < split.length; i++) {
                    split[i] = deleteWhitespace(split[i]);
                    if (split[i].matches("\\Q{\\!\\!\\\\Esmall../..}")) {
                        split[i] = split[i].replace("{\\!\\!\\small", "");
                        split[i] = split[i].replace("}", "");
                    }
                    if (split[i].isEmpty()) split[i] = null;
                    ref.groups[i] = split[i] == null ? -1 : ((split[i].contains("/") ? Integer.parseInt(split[i].split("/")[0]) : Integer.parseInt(split[i])));
                    if(split[i] != null && split[i].contains("/")) ref.groupslv2[i] = Integer.parseInt(split[i].split("/")[1]);
                    else ref.groupslv2[i] = -1;
                }

                BiPredicate<UserData, Integer> khollePredicate = (userData, integer) -> integer >= 3 && ref.groups[integer - 3] == userData.getGroup() || (ref.groupslv2[integer - 3] == userData.getGroup() && userData.getLv2());

                kholle_component.add(new SchoolDayComponent(hour, hour + 1, day, "Kholle " + matiere, teacher, room, KHOLLE_BACK_COLOR, Color.BLACK, khollePredicate));
            } else {
                matiere = line.replace("\\\\", "");
            }
            line = reader.readLine();


        }

    }

    public static String deleteWhitespace(final String str) {
        if (str == null || str.isEmpty()) {
            return str;
        }
        final int sz = str.length();
        final char[] chs = new char[sz];
        int count = 0;
        for (int i = 0; i < sz; i++) {
            if (!Character.isWhitespace(str.charAt(i))) {
                chs[count++] = str.charAt(i);
            }
        }
        if (count == sz) {
            return str;
        }
        if (count == 0) {
            return "";
        }
        return new String(chs, 0, count);
    }

    private static void initWeekly() {

        BiPredicate<UserData, Integer> isForGroupOne = (userData, integer) -> (userData.getLast_name().compareTo("KUHN") <= 0 == (integer % 2 == 0));

        BiPredicate<UserData, Integer> isForGroupTwo = (userData, integer) -> !isForGroupOne.test(userData, integer);

        BiPredicate<UserData, Integer> isLv2 = (data, integer) -> data.getLv2();


        //MONDAY

        weekly_component.add(TP_INFO.toComponent(8, 11, 0, isForGroupTwo.negate()));
        weekly_component.add(TP_PHYSIQUE.toComponent(8, 11, 0, isForGroupOne.negate()));
        weekly_component.add(PHYSIQUE.toComponent(11, 12, 0));

        weekly_component.add(INFO.toComponent(14, 15, 0));
        weekly_component.add(MATH.toComponent(15, 18, 0));

        //TUESDAY
        weekly_component.add(MATH.toComponent(8, 10, 1));
        weekly_component.add(PHYSIQUE.toComponent(10, 12, 1));
        weekly_component.add(LV2.toComponent(12, 13, 1, isLv2));

        weekly_component.add(MATH.toComponent(14, 16, 1));
        weekly_component.add(TIPE.toComponent(16, 18, 1));

        //WEDNESDAY
        weekly_component.add(MATH.toComponent(8, 11, 2));
        weekly_component.add(INFO.toComponent(11, 12, 2));

        //THURSDAY
        weekly_component.add(MATH.toComponent(8, 10, 3));
        weekly_component.add(INFO.toComponent(10, 12, 3));

        weekly_component.add(MATH.toComponent(13, 15, 3));
        weekly_component.add(INFO.toComponent(15, 16, 3, isForGroupTwo));
        weekly_component.add(INFO.toComponent(16, 17, 3, isForGroupOne));
        weekly_component.add(PHYSIQUE.toComponent(15, 16, 3, isForGroupOne));
        weekly_component.add(PHYSIQUE.toComponent(16, 17, 3, isForGroupTwo));
        weekly_component.add(LV2.toComponent(17, 18, 3, isLv2));

        //FRIDAY
        weekly_component.add(PHYSIQUE.toComponent(8, 10, 4));
        weekly_component.add(FRANCAIS.toComponent(10, 12, 4));

        weekly_component.add(LV1.toComponent(13, 15, 4));
    }


    private static void drawSchoolDayComponent(BufferedImage image, Graphics2D g2d, SchoolDayComponent component) {
        int x = WIDTH_OFFSET + component.getDay() * COLUMN_WIDTH;
        double y = HEIGHT_OFFSET + ((component.getBegin() - 8) * SEGEMENT_HEIGHT);
        float height = ((component.getEnd() - component.getBegin()) * SEGEMENT_HEIGHT);


        g2d.setColor(Color.BLACK);
        g2d.fillRect(x, (int) y, COLUMN_WIDTH, (int) height);


        g2d.setColor(component.getBack().brighter());
        g2d.fillRect(x + 1, (int) y + 1, COLUMN_WIDTH - 2, (int) height - 2);
        g2d.setColor(component.getWritten());

        String extra = "";
        if (!component.getRoom().isEmpty()) extra += component.getRoom();
        if (!component.getRoom().isEmpty() && !component.getTeacher().isEmpty()) extra += " ";
        if (!component.getTeacher().isEmpty()) extra += component.getTeacher();

        drawCenteredString(g2d, x, (int) y, COLUMN_WIDTH, (int) height,
                component.getName() + (extra.isEmpty() ? "" : "\n" + extra)
        );
    }


    private static void drawCenteredString(Graphics g, int x, int y, int width, int height, String text) {
        // Get the FontMetrics
        FontMetrics metrics = g.getFontMetrics(g.getFont());
        // Determine the X coordinate for the text
        String[] split = text.split("\n");
        for (int i = 0; i < split.length; i++) {

            String line = split[i];

            int newx = x + (width - metrics.stringWidth(line)) / 2;
            int newy = y + (i * (metrics.getHeight())) + ((height - (split.length) * metrics.getHeight()) / 2) + metrics.getAscent();

            g.drawString(line, newx, newy);

        }

    }
}
