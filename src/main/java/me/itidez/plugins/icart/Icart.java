package me.itidez.plugins.icart;

import java.io.File;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import me.itidez.plugins.icart.events.EventManager;
import me.itidez.plugins.icart.util.Config;
import me.itidez.plugins.icart.util.Util;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;

public class Icart extends JavaPlugin {
    public static Icart instance;
    public String version;
    public PluginDescriptionFile description;
    public static Config config;
    public HashMap<String, Sign> signList;
    private File slapiFile = new File(getDataFolder(), "signList.bin");
    
    @Override
    public void onDisable() {
        instance = null;
        try {
            SLAPI.save(signList,slapiFile);
        } catch (Exception ex) {
            Logger.getLogger(Icart.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public void onEnable() {
        instance = this;
        description = getDescription();
        version = description.getVersion();
        config = new Config(this);
        EventManager em = new EventManager(this);
        em.registerEvents();
        try {
            signList = (HashMap<String, Sign>)SLAPI.load(slapiFile);
        } catch (Exception ex) {
            Logger.getLogger(Icart.class.getName()).log(Level.SEVERE, null, ex);
        }
        if(signList == null) {
            signList = new HashMap<String, Sign>();
        }
        Util.info("Loaded");
        Util.debug("Debug mode enabled");
    }
}

