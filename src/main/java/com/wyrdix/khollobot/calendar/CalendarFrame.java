package com.wyrdix.khollobot.calendar;

import com.wyrdix.khollobot.data.DataFile;
import com.wyrdix.khollobot.data.UserData;
import org.apache.commons.lang3.StringUtils;

import javax.swing.*;
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

    private static List<SchoolDayComponent> weekly_component = new ArrayList<>();
    private static List<SchoolDayComponent> kholle_component = new ArrayList<>();
    private static List<SchoolDayComponent> ds_component = new ArrayList<>();

    private final static int COLUMN_WIDTH = 200;
    private final static int ELEMENT_HEIGHT = 75;


    public static BufferedImage getCalendar(UserData data, int week){
        BufferedImage image = new BufferedImage(COLUMN_WIDTH * 5, ELEMENT_HEIGHT * 12, BufferedImage.TYPE_INT_RGB);

        Graphics2D g2d = (Graphics2D) image.getGraphics();

        g2d.setColor(Color.WHITE);

        for (SchoolDayComponent component : weekly_component) {
            if (!component.isElementPresent(data, week)) continue;
            drawSchoolDayComponent(image, g2d, component);
        }
        for (SchoolDayComponent component : kholle_component) {
            if (!component.isElementPresent(data, week)) continue;
            drawSchoolDayComponent(image, g2d, component);
        }

        for (SchoolDayComponent component : ds_component) {
            if (!component.isElementPresent(data, week)) continue;
            drawSchoolDayComponent(image, g2d, component);
        }

        g2d.dispose();

        JPanel comp = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);

                g.drawImage(image, 0, 0, null);
            }
        };

        return image;
    }

    public static void init() throws IOException {
        initWeekly();
        initKholle();
        initDS();
    }

    private static void initDS() {

        ds_component.add(new SchoolDayComponent(13, 17, 2, "Devoir de Mathématique","","", Color.RED, Color.GREEN, (data, integer) -> integer == 2));
        ds_component.add(new SchoolDayComponent(13, 17, 2, "Devoir de Physique","","", Color.RED, Color.GREEN, (data, integer) -> integer == 3));
        ds_component.add(new SchoolDayComponent(15, 17, 4, "Devoir de Français","","", Color.RED, Color.GREEN, (data, integer) -> integer == 3));
        ds_component.add(new SchoolDayComponent(13, 17, 2, "Devoir d'Informatique","","", Color.RED, Color.GREEN, (data, integer) -> integer == 4));
        ds_component.add(new SchoolDayComponent(15, 17, 2, "LV1","","", Color.RED, Color.GREEN, (data, integer) -> integer == 4));
        ds_component.add(new SchoolDayComponent(13, 17, 2, "Devoir d Mathématique","","", Color.RED, Color.GREEN, (data, integer) -> integer == 5));
        ds_component.add(new SchoolDayComponent(13, 17, 2, "Devoir de Physique","","", Color.RED, Color.GREEN, (data, integer) -> integer == 6));
        ds_component.add(new SchoolDayComponent(13, 17, 2, "Devoir d Mathématique","","", Color.RED, Color.GREEN, (data, integer) -> integer == 7));
        ds_component.add(new SchoolDayComponent(13, 17, 2, "Devoir d'Informatique","","", Color.RED, Color.GREEN, (data, integer) -> integer == 8));
        ds_component.add(new SchoolDayComponent(13, 17, 2, "Devoir d Mathématique","","", Color.RED, Color.GREEN, (data, integer) -> integer == 9));
        ds_component.add(new SchoolDayComponent(13, 17, 2, "Devoir de Physique","","", Color.RED, Color.GREEN, (data, integer) -> integer == 10));

        ds_component.add(new SchoolDayComponent(13, 17, 2, "Devoir d'Informatique","","", Color.RED, Color.GREEN, (data, integer) -> integer == 12));
        ds_component.add(new SchoolDayComponent(13, 17, 2, "Devoir d Mathématique","","", Color.RED, Color.GREEN, (data, integer) -> integer == 13));
        ds_component.add(new SchoolDayComponent(15, 17, 2, "Devoir de Français","","", Color.RED, Color.GREEN, (data, integer) -> integer == 14));
        ds_component.add(new SchoolDayComponent(13, 17, 2, "Devoir de Physique","","", Color.RED, Color.GREEN, (data, integer) -> integer == 15));
        ds_component.add(new SchoolDayComponent(15, 17, 2, "LV1","","", Color.RED, Color.GREEN, (data, integer) -> integer == 16));
        ds_component.add(new SchoolDayComponent(13, 17, 2, "Devoir d Mathématique","","", Color.RED, Color.GREEN, (data, integer) -> integer == 17));
        ds_component.add(new SchoolDayComponent(13, 17, 2, "Devoir d'Infomatique","","", Color.RED, Color.GREEN, (data, integer) -> integer == 18));
        ds_component.add(new SchoolDayComponent(13, 17, 2, "Devoir de Physique","","", Color.RED, Color.GREEN, (data, integer) -> integer == 19));
        ds_component.add(new SchoolDayComponent(13, 17, 2, "Devoir d Mathématique","","", Color.RED, Color.GREEN, (data, integer) -> integer == 20));
        ds_component.add(new SchoolDayComponent(13, 17, 2, "Concours Blanc","Le bot dit certainement de la merde","", Color.RED, Color.GREEN, (data, integer) -> integer == 21));

        ds_component.add(new SchoolDayComponent(13, 17, 2, "Devoir d Mathématique","","", Color.RED, Color.GREEN, (data, integer) -> integer == 24));
        ds_component.add(new SchoolDayComponent(13, 17, 2, "Devoir d'Infomatique","","", Color.RED, Color.GREEN, (data, integer) -> integer == 25));


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
                int day = -1;
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

                split = line.split("&");
                split[split.length - 1] = split[split.length - 1].replace("\\\\", "");

                split = Arrays.copyOfRange(split, 1, split.length - 1);

                var ref = new Object() {
                    int[] groups = new int[0];
                };

                ref.groups = new int[split.length];
                for (int i = 0; i < split.length; i++) {
                    split[i] = StringUtils.deleteWhitespace(split[i]);
                    if (split[i].matches("\\Q{\\!\\!\\\\Esmall../..}")) {
                        split[i] = split[i].replace("{\\!\\!\\small", "");
                        split[i] = split[i].replace("}", "");
                    }
                    if (split[i].isEmpty()) split[i] = null;
                    ref.groups[i] = split[i] == null ? -1 : ((split[i].contains("/") ? -2 : Integer.parseInt(split[i])));
                }

                BiPredicate<UserData, Integer> khollePredicate = (userData, integer) -> {
                    return integer >= 3 && ref.groups[integer - 3] == userData.getGroup();
                };

                kholle_component.add(new SchoolDayComponent(hour, hour + 1, day, "Kholle "+matiere, teacher, "?", Color.YELLOW, Color.BLACK, khollePredicate));
            }else{
                matiere = line.replace("\\\\", "");
            }
            line = reader.readLine();


        }

    }

    private static void initWeekly() {

        BiPredicate<UserData, Integer> isForGroupOne = (userData, integer) -> {
            return userData.getLast_name().compareTo("KUHN") <= 0 == (integer % 2 == 0);
        };

        BiPredicate<UserData, Integer> isForGroupTwo = (userData, integer) -> {
            return !isForGroupOne.test(userData, integer);
        };


        //MONDAY

        weekly_component.add(TP_INFO.toComponent(8, 11, 0, isForGroupTwo));
        weekly_component.add(TP_PHYSIQUE.toComponent(8, 11, 0, isForGroupOne));
        weekly_component.add(PHYSIQUE.toComponent(11, 12, 0));

        weekly_component.add(INFO.toComponent(14, 15, 0));
        weekly_component.add(MATH.toComponent(15, 18, 0));

        //TUESDAY
        weekly_component.add(MATH.toComponent(8, 10, 1));
        weekly_component.add(PHYSIQUE.toComponent(10, 12, 1));
        weekly_component.add(LV2.toComponent(12, 13, 1));

        weekly_component.add(MATH.toComponent(14, 16, 1));
        weekly_component.add(TIPE.toComponent(16, 18, 1));

        //WEDNESDAY
        weekly_component.add(MATH.toComponent(8, 11, 2));
        weekly_component.add(INFO.toComponent(11, 12, 2));

        //THURSDAY
        weekly_component.add(MATH.toComponent(8, 10, 3));
        weekly_component.add(INFO.toComponent(10, 12, 3));

        weekly_component.add(MATH.toComponent(14, 15, 3));
        weekly_component.add(INFO.toComponent(15, 16, 3, isForGroupTwo));
        weekly_component.add(INFO.toComponent(16, 17, 3, isForGroupOne));
        weekly_component.add(PHYSIQUE.toComponent(15, 16, 3, isForGroupOne));
        weekly_component.add(PHYSIQUE.toComponent(16, 17, 3, isForGroupTwo));
        weekly_component.add(LV2.toComponent(17, 18, 3));

        //FRIDAY
        weekly_component.add(PHYSIQUE.toComponent(8, 10, 4));
        weekly_component.add(FRANCAIS.toComponent(10, 12, 4));

        weekly_component.add(LV1.toComponent(13, 15, 4));
    }


    private static void drawSchoolDayComponent(BufferedImage image, Graphics2D g2d, SchoolDayComponent component) {
        int x = component.getDay() * COLUMN_WIDTH;
        double y = ((component.getBegin() - 8) / 12 * image.getHeight());
        float height = ((component.getEnd() - component.getBegin()) * ELEMENT_HEIGHT);

        g2d.setColor(component.getBack());
        g2d.fillRect(x, (int) y, COLUMN_WIDTH, (int) height);
        g2d.setColor(component.getWritten());
        g2d.drawString(component.getName(), x + 10, (int) (y + height / 3));
        g2d.drawString(component.getRoom() + " " + component.getTeacher(), x + 10, (int) (y + 2 * height / 3));
    }
}
