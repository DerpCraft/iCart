package me.itidez.plugins.icart.events;

import me.itidez.plugins.icart.Icart;
import me.itidez.plugins.icart.util.Db;
import me.itidez.plugins.icart.util.Util;
import org.bukkit.Bukkit;
import org.bukkit.plugin.PluginManager;

/**
 *
 * @author iTidez
 */
public class EventManager {
    public Icart plugin;
    private Db db;
    
    public EventManager(Icart plugin, Db db) {
        this.plugin = plugin;
        this.db = db;
    }
    
    public void registerEvents() {
        PluginManager pm = Bukkit.getPluginManager();
        pm.registerEvents(new EventButtonDetector(plugin, db), plugin);
        pm.registerEvents(new CartSpawner(plugin, db), plugin);
        pm.registerEvents(new EventSignDetector(plugin, db), plugin);
        Util.debug("Registered Button Detector");
    }
}
