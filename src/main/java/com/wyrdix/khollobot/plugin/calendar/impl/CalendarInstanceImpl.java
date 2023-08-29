package com.wyrdix.khollobot.plugin.calendar.impl;

import com.wyrdix.khollobot.plugin.calendar.CalendarElement;
import com.wyrdix.khollobot.plugin.calendar.CalendarElementTemplate;
import com.wyrdix.khollobot.plugin.calendar.CalendarInstance;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CalendarInstanceImpl implements CalendarInstance {
    private final String id;
    private final File file;

    private final Map<String, CalendarElementTemplate> templateMap = new HashMap<>();
    private final List<CalendarElement> elements = new ArrayList<>();

    public CalendarInstanceImpl(String id) {
        this.id = id;

        file = new File(CALENDAR_FOLDER, id+".cal");
    }

    @Override
    public String id() {
        return id;
    }

    @Override
    public void load() {
        if(!file.exists()) return;

        try {
            List<String> lines = Files.readAllLines(file.toPath());
            for (String line : lines) {
                if(line.startsWith("DEFINE")) {
                    CalendarElementTemplate template = CalendarElementTemplate.deserialize(this, line);
                    templateMap.put(template.id(), template);
                }else if(line.startsWith("ADD")){
                    CalendarElement element = CalendarElement.deserialize(this, line);
                    elements.add(element);
                }else if(!line.isBlank()) System.err.println("Unrecognized : "+line);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public List<CalendarElement> elements(){
        return elements;
    }

    @Override
    public Map<String, CalendarElementTemplate> getTemplates() {
        return templateMap;
    }
}
