package me.itidez.plugins.icart.events;

import me.itidez.plugins.icart.Icart;
import me.itidez.plugins.icart.util.Util;
import org.bukkit.Bukkit;
import org.bukkit.plugin.PluginManager;

/**
 *
 * @author iTidez
 */
public class EventManager {
    public Icart plugin;
    
    public EventManager(Icart plugin) {
        this.plugin = plugin;
    }
    
    public void registerEvents() {
        PluginManager pm = Bukkit.getPluginManager();
        pm.registerEvents(new EventButtonDetector(), plugin);
        pm.registerEvents(new EventSignDetector(plugin), plugin);
        Util.debug("Registered Button Detector");
    }
}
