package com.wyrdix.khollobot.command.cafeteria;

import com.wyrdix.khollobot.command.KCommandImpl;
import com.wyrdix.khollobot.plugin.CafeteriaPlugin;
import com.wyrdix.khollobot.plugin.DefaultPlugin;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.utils.FileUpload;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class WeekMenuCommand extends KCommandImpl {

    private static final WeekMenuCommand INSTANCE = new WeekMenuCommand();
    private static final String ID = "menu";
    private static final String DESCRIPTION = "Reçoit le menu de la cantine";

    public WeekMenuCommand() {
        super(DefaultPlugin.class, ID, DESCRIPTION);
    }

    public static WeekMenuCommand getInstance() {
        return INSTANCE;
    }

    @Override
    public void execute(SlashCommandInteractionEvent event){
        BufferedImage menu = CafeteriaPlugin.getMenu();
        File dataFolder = new File("data/" + System.currentTimeMillis() + "/");
        //noinspection ResultOfMethodCallIgnored
        dataFolder.mkdirs();
        File file = new File(dataFolder, "menu.jpeg");

        assert menu != null;
        try {
            ImageIO.write(menu, "JPEG", file);
        } catch (IOException e) {
            e.printStackTrace();
        }

        FileUpload upload = FileUpload.fromData(file);
        event.replyFiles(upload).queue();
    }
}
