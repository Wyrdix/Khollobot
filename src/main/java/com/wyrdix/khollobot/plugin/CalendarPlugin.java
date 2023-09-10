package com.wyrdix.khollobot.plugin;


import com.wyrdix.khollobot.KUser;
import com.wyrdix.khollobot.KholloBot;
import com.wyrdix.khollobot.command.calendar.AddCalendarCommand;
import com.wyrdix.khollobot.command.calendar.CalendarCommand;
import com.wyrdix.khollobot.command.calendar.ListCalendarCommand;
import com.wyrdix.khollobot.command.calendar.RemoveCalendarCommand;
import com.wyrdix.khollobot.plugin.calendar.CalendarElement;
import com.wyrdix.khollobot.plugin.calendar.CalendarElementTemplate;
import com.wyrdix.khollobot.plugin.calendar.CalendarInstance;
import com.wyrdix.khollobot.plugin.calendar.impl.CalendarInstanceImpl;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.callbacks.IReplyCallback;
import net.dv8tion.jda.api.interactions.components.ItemComponent;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import net.dv8tion.jda.api.utils.FileUpload;
import net.fortuna.ical4j.data.CalendarBuilder;
import net.fortuna.ical4j.data.ParserException;
import net.fortuna.ical4j.model.Calendar;
import net.fortuna.ical4j.model.Component;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.*;

@PluginInfo(id = "calendar", name = "Calendrier", version = "1.0-SNAPSHOT", author = "Wyrdix")
public class CalendarPlugin extends ListenerAdapter implements Plugin {

    private final Map<String, CalendarInstance> instanceMap = new HashMap<>();

    private static final ZoneId zoneId = ZoneId.of("Europe/Paris");
    private static final TimeZone timeZone = TimeZone.getTimeZone(zoneId);
    private static final List<Holiday> holidays = new ArrayList<>();
    private static final java.util.Calendar doomsday = java.util.Calendar.getInstance(timeZone);

    private static void drawSchoolDayComponent(Graphics2D g2d, CalendarElement element) {
        int x = ImageGenerator.WIDTH_OFFSET + element.day() * ImageGenerator.COLUMN_WIDTH;
        double y = ImageGenerator.HEIGHT_OFFSET + ((element.beginning() - 8) * ImageGenerator.SEGMENT_HEIGHT);
        float height = ((element.ending() - element.beginning()) * ImageGenerator.SEGMENT_HEIGHT);


        g2d.setColor(Color.BLACK);
        g2d.fillRect(x, (int) y, ImageGenerator.COLUMN_WIDTH, (int) height);


        CalendarElementTemplate template = element.template();
        g2d.setColor(template.background().brighter());
        g2d.fillRect(x + 1, (int) y + 1, ImageGenerator.COLUMN_WIDTH - 2, (int) height - 2);
        g2d.setColor(template.fontColor());

        String extra = "";
        if (!template.room().isEmpty()) extra += template.room();
        if (!template.room().isEmpty() && !template.teacher().isEmpty()) extra += " ";
        if (!template.room().isEmpty()) extra += template.teacher();

        drawCenteredString(g2d, x, (int) y, ImageGenerator.COLUMN_WIDTH, (int) height,
                template.name() + (extra.isEmpty() ? "" : "\n" + extra)
        );
    }

    public Map<String, CalendarInstance> getInstances() {
        return instanceMap;
    }

    private record Holiday(ZonedDateTime begin, ZonedDateTime end, String name){}

    public static int getCurrentWeek(){

        java.util.Calendar current = java.util.Calendar.getInstance(timeZone);
        Instant plus = current.toInstant().plus(3, ChronoUnit.DAYS);
        long between = 1 + ChronoUnit.WEEKS.between(doomsday.toInstant().atZone(zoneId), plus.atZone(zoneId));

        for (Holiday holiday : holidays) {
            if(!holiday.begin.isBefore(current.toInstant().atZone(zoneId))) continue;
            if(holiday.end.isAfter(current.toInstant().atZone(zoneId))){
                between -= ChronoUnit.WEEKS.between(holiday.begin, plus.atZone(zoneId));
            }else between-=2;
        }

        return (int) between;
    }

    @Override
    public void save() {

    }

    public static void sendCalendar(IReplyCallback event, int week) {
        KUser user = KUser.getKUser(event.getUser().getIdLong());
        BufferedImage image = ImageGenerator.getCalendar(week, user);

        //noinspection ResultOfMethodCallIgnored
        new File("data").mkdirs();

        File file = new File("data/" + System.currentTimeMillis() + ".png");
        try {
            ImageIO.write(image, "PNG", file);
        } catch (IOException e) {
            e.printStackTrace();
        }
        FileUpload upload = FileUpload.fromData(file);
        //noinspection ResultOfMethodCallIgnored
        event.reply("Voici votre emploi du temps de la semaine :").setFiles(upload)
                .addActionRow(generateButton(week)).queue((s) -> file.delete());
    }

    private static ItemComponent[] generateButton(int week) {
        if (week == 1) {
            return new ItemComponent[]{Button.primary("khollobot_week_" + (week + 1), "Semaine Suivante")};
        } else {
            return new ItemComponent[]{Button.primary("khollobot_week_" + (week - 1), "Semaine Précédente"), Button.primary("khollobot_week_" + (week + 1), "Semaine Suivante")};
        }
    }


    @Override
    public void onButtonInteraction(ButtonInteractionEvent event) {
        KUser user = KUser.getKUser(event.getUser().getIdLong());
        Button button = event.getButton();
        if (button.getId() == null || !button.getId().startsWith("khollobot_week_")) return;
        Message.Interaction reference = event.getMessage().getInteraction();
        if (reference == null || reference.getUser().getIdLong() != event.getInteraction().getUser().getIdLong()) {
            event.reply("Vous ne pouvez pas modifier cette demande").complete().setEphemeral(true);
            return;
        }

        try {
            event.reply("Actualisé").complete();
        } catch (Throwable ignored) {
        }

        int week = Integer.parseInt(button.getId().substring("khollobot_week_".length()));

        BufferedImage image = ImageGenerator.getCalendar(week, user);

        //noinspection ResultOfMethodCallIgnored
        new File("data").mkdirs();

        File file = new File("data/" + System.currentTimeMillis() + ".png");
        try {
            ImageIO.write(image, "PNG", file);
        } catch (IOException e) {
            e.printStackTrace();
        }

        FileUpload upload = FileUpload.fromData(file);

        //noinspection ResultOfMethodCallIgnored
        event.getMessage().editMessageAttachments(upload).setActionRow(generateButton(week)).queue((s) -> file.delete());


        for (Message message : event.getChannel().getIterableHistory()) {
            if (message.getContentRaw().equals("Actualisé")) {
                try {
                    message.delete().queue();
                } catch (Exception ignored) {
                }
            }
        }
    }

    @SuppressWarnings({"OptionalGetWithoutIsPresent", "CallToPrintStackTrace"})
    @Override
    public void onEnable() {
        addCommand(CalendarCommand.getInstance());
        addCommand(AddCalendarCommand.getInstance());
        addCommand(RemoveCalendarCommand.getInstance());
        addCommand(ListCalendarCommand.getInstance());

        File calendarFolder = CalendarInstance.CALENDAR_FOLDER;
        if(!calendarFolder.exists()) //noinspection ResultOfMethodCallIgnored
            calendarFolder.mkdirs();

        for (File file : Objects.requireNonNull(calendarFolder.listFiles())) {
            String name = file.getName();
            if(name.endsWith(".cal")) name = name.substring(0, name.lastIndexOf(".cal"));

            System.out.println("Loading calendar : "+name);
            instanceMap.put(name, new CalendarInstanceImpl(name));
        }

        instanceMap.values().forEach(CalendarInstance::load);

        InputStream fin;
        try {
            fin = new URL("https://fr.ftp.opendatasoft.com/openscol/fr-en-calendrier-scolaire/Zone-B.ics").openStream();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        CalendarBuilder builder = new CalendarBuilder();
        try {
            Calendar holidayCalendar = builder.build(fin);

            java.util.Calendar calendar = doomsday;
            calendar.set(2023, java.util.Calendar.SEPTEMBER, 5);


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

        KholloBot.getJDA().addEventListener(this);
    }

    static final class ImageGenerator {

        private final static int WIDTH_OFFSET = 50;
        private final static int END_COLUMN_OFFSET = 50;
        private final static int HEIGHT_OFFSET = 50;
        private final static int END_HEIGHT_OFFSET = 50;
        private final static int COLUMN_WIDTH = 200;
        final static int SEGMENT_HEIGHT = 75;

        private final static int DAYS_PER_WEEK = 5;
        private final static int SEGMENTS_PER_DAY = 11;

        @SuppressWarnings("all")
        public static BufferedImage getCalendar(int week, KUser user) {
            BufferedImage image = new BufferedImage(WIDTH_OFFSET + COLUMN_WIDTH * DAYS_PER_WEEK + END_COLUMN_OFFSET, HEIGHT_OFFSET + SEGMENT_HEIGHT * SEGMENTS_PER_DAY + END_HEIGHT_OFFSET, BufferedImage.TYPE_INT_RGB);

            Graphics2D g2d = (Graphics2D) image.getGraphics();
            g2d.setColor(Color.LIGHT_GRAY);
            g2d.fillRect(0, 0, image.getWidth(), image.getHeight());

            g2d.setColor(Color.BLACK);
            g2d.fillRect(WIDTH_OFFSET, HEIGHT_OFFSET, COLUMN_WIDTH * DAYS_PER_WEEK, SEGMENT_HEIGHT * SEGMENTS_PER_DAY);


            g2d.setColor(Color.LIGHT_GRAY);
            g2d.fillRect(WIDTH_OFFSET + 1, HEIGHT_OFFSET + 1, COLUMN_WIDTH * DAYS_PER_WEEK - 2, SEGMENT_HEIGHT * SEGMENTS_PER_DAY - 2);

            CalendarPlugin plugin = KholloBot.getPlugin(CalendarPlugin.class);

            g2d.setColor(Color.BLACK);
            for (CalendarInstance value : plugin.instanceMap.values()) {
                for (CalendarElement element : value.elements()) {
                    if (element.filter(week, user)) drawSchoolDayComponent(g2d, element);
                }
            }


            g2d.setFont(new Font(g2d.getFont().getName(), Font.BOLD, 34));

            drawCenteredString(g2d, WIDTH_OFFSET + 0 * COLUMN_WIDTH, 0, COLUMN_WIDTH, HEIGHT_OFFSET, "Lundi");
            drawCenteredString(g2d, WIDTH_OFFSET + 1 * COLUMN_WIDTH, 0, COLUMN_WIDTH, HEIGHT_OFFSET, "Mardi");
            drawCenteredString(g2d, WIDTH_OFFSET + 2 * COLUMN_WIDTH, 0, COLUMN_WIDTH, HEIGHT_OFFSET, "Mercredi");
            drawCenteredString(g2d, WIDTH_OFFSET + 3 * COLUMN_WIDTH, 0, COLUMN_WIDTH, HEIGHT_OFFSET, "Jeudi");
            drawCenteredString(g2d, WIDTH_OFFSET + 4 * COLUMN_WIDTH, 0, COLUMN_WIDTH, HEIGHT_OFFSET, "Vendredi");

            g2d.setFont(new Font(g2d.getFont().getName(), Font.PLAIN, 20));

            for (int i = 0; i <= SEGMENTS_PER_DAY; i++) {
                int v = 8 + i;
                drawCenteredString(g2d, 0, HEIGHT_OFFSET + i * SEGMENT_HEIGHT - 20, WIDTH_OFFSET - 20, 40, (v < 10 ? " " + v : v + ""));
                drawCenteredString(g2d, 30, HEIGHT_OFFSET + i * SEGMENT_HEIGHT - 20, 20, 40, "H");
            }

            drawCenteredString(g2d, WIDTH_OFFSET, image.getHeight() - HEIGHT_OFFSET, image.getWidth() - 2 * WIDTH_OFFSET, HEIGHT_OFFSET, "Semaine : " + week);

            g2d.dispose();

            return image;
        }
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
