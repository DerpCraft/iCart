package me.itidez.plugins.icart;

import java.util.logging.Logger;
import me.itidez.plugins.icart.util.Util;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;

public class Icart extends JavaPlugin {
    public static Icart instance;
    public String version;
    public PluginDescriptionFile description;
    @Override
    public void onDisable() {
        instance = null;
    }

    @Override
    public void onEnable() {
        instance = this;
        description = getDescription();
        version = description.getVersion();
        Util.info("Loaded");
        Util.debug("Debug mode enabled");
    }
}

