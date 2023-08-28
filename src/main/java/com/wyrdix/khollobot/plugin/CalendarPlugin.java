package com.wyrdix.khollobot.plugin;


import com.wyrdix.khollobot.command.calendar.CalendarCommand;
import net.dv8tion.jda.api.interactions.callbacks.IReplyCallback;
import net.dv8tion.jda.api.interactions.components.ItemComponent;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import net.dv8tion.jda.api.utils.FileUpload;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import static com.wyrdix.khollobot.plugin.CalendarPlugin.ImageGenerator.getCalendar;

@PluginInfo(id = "calendar", name = "Calendrier", version = "1.0-SNAPSHOT", author = "Wyrdix")
public class CalendarPlugin implements Plugin{
    @Override
    public void onEnable() {
        addCommand(CalendarCommand.getInstance());
    }

    @Override
    public void save() {

    }

    public static void sendCalendar(IReplyCallback event, int week) {
        BufferedImage image = getCalendar(week);

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

        int size = week < 3 ? 1 : 2;

        if (size == 1) {
            return new ItemComponent[]{Button.primary("khollobot_week_" + (week - 1), "Semaine Précédente")};
        } else {
            return new ItemComponent[]{Button.primary("khollobot_week_" + (week - 1), "Semaine Précédente"), Button.primary("khollobot_week_" + (week + 1), "Semaine Suivante")};
        }
    }

    static final class ImageGenerator {

        private final static int WIDTH_OFFSET = 50;
        private final static int END_COLUMN_OFFSET = 50;
        private final static int HEIGHT_OFFSET = 50;
        private final static int END_HEIGHT_OFFSET = 50;
        private final static int COLUMN_WIDTH = 200;
        private final static int SEGEMENT_HEIGHT = 75;

        private final static int DAYS_PER_WEEK = 5;
        private final static int SEGMENTS_PER_DAY = 11;
        private static final Color KHOLLE_BACK_COLOR = Color.decode("#e58e26");
        public static BufferedImage getCalendar(int week) {
            BufferedImage image = new BufferedImage(WIDTH_OFFSET + COLUMN_WIDTH * DAYS_PER_WEEK + END_COLUMN_OFFSET, HEIGHT_OFFSET + SEGEMENT_HEIGHT * SEGMENTS_PER_DAY + END_HEIGHT_OFFSET, BufferedImage.TYPE_INT_RGB);

            Graphics2D g2d = (Graphics2D) image.getGraphics();
            g2d.setColor(Color.LIGHT_GRAY);
            g2d.fillRect(0, 0, image.getWidth(), image.getHeight());

            g2d.setColor(Color.BLACK);
            g2d.fillRect(WIDTH_OFFSET, HEIGHT_OFFSET, COLUMN_WIDTH * DAYS_PER_WEEK, SEGEMENT_HEIGHT * SEGMENTS_PER_DAY);


            g2d.setColor(Color.LIGHT_GRAY);
            g2d.fillRect(WIDTH_OFFSET + 1, HEIGHT_OFFSET + 1, COLUMN_WIDTH * DAYS_PER_WEEK - 2, SEGEMENT_HEIGHT * SEGMENTS_PER_DAY - 2);

            g2d.setColor(Color.WHITE);

            //TODO Add elements to the calendar

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
