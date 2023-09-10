package com.wyrdix.khollobot.plugin;

import com.wyrdix.khollobot.command.cafeteria.WeekMenuCommand;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Base64;

@PluginInfo(id = "cafeteria", name = "Cantine", version = "1.0-SNAPSHOT", author = "Wyrdix")
public class CafeteriaPlugin implements Plugin {

    public static BufferedImage getMenu(){



        try {
            URL url = new URL("https://www.faidherbe.org/le-menu-de-la-semaine");
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            BufferedReader br = new BufferedReader(
                    new InputStreamReader(con.getInputStream()));

            StringBuilder raw = new StringBuilder();
            while (br.ready()) raw.append(br.readLine());

            String s = raw.toString();
            int start = s.indexOf("data:image/jpeg;base64,") + "data:image/jpeg;base64,".length();
            int end = s.substring(start).indexOf("\"") + start;

            s = s.substring(start, end);
            byte[] imageBytes = Base64.getDecoder().decode(s);

            return ImageIO.read(new ByteArrayInputStream(imageBytes));
        } catch (Throwable e) {
            e.printStackTrace();
        }


        return null;
    }

    @Override
    public void onEnable() {
        addCommand(WeekMenuCommand.getInstance());
    }

    @Override
    public void save() {

    }
}
