package com.wyrdix.khollobot.plugin;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface PluginInfo {
    String id();

    String name();

    String description() default "";

    String[] author();

    String version();

    Class<? extends Plugin.PluginConfig> config() default Plugin.PluginConfig.class;
}
