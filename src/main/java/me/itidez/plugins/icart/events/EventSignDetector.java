package me.itidez.plugins.icart.events;

import java.sql.ResultSet;
import me.itidez.plugins.icart.Icart;
import me.itidez.plugins.icart.util.Db;
import me.itidez.plugins.icart.util.Util;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.block.Sign;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.SignChangeEvent;

/**
 *
 * @author iTidez
 */
public class EventSignDetector implements Listener {

    public Icart plugin;
    private Db db;
    
    public EventSignDetector(Icart plugin) {
        this.plugin = plugin;
        this.db = new Db(plugin, "localhost", "iTidez_iCart", "iTidez_iCart", "plurlife1337");
    }
    
    @EventHandler
    public void onSignChange(SignChangeEvent event) {
        if(event.isCancelled()) return;
        String line1 = event.getLine(0);
        Sign sign = (Sign)event.getBlock().getState();
        if(line1.equalsIgnoreCase("[iCart]")) {
            Bukkit.broadcastMessage("Found sign");
            sign.setLine(0, ChatColor.BLUE+"[iCart]");
            if(sign.getLine(1) == null) {
                Util.debug("Error - Line 1 Null");
            }
            db.query("CREATE TABLE IF NOT EXISTS `signs` (`id` VARCHAR(16) NOT NULL,`target` VARCHAR(16) NOT NULL, `location_x` INT(4) unsigned NOT NULL, `location_y` INT(3) unsigned NOT NULL, `location_z` INT(4) unsigned NOT NULL, `type` VARCHAR(16) NOT NULL, PRIMARY KEY (`name`) ) ENGINE=MyISAM DEFAULT CHARSET=latin1;");
            ResultSet result = db.query("SELECT `id`,`target`,`location_x`,`location_y`,`location_z`,`type` FROM `signs` WHERE `id` = '"+sign.getLine(2) +"'");
            String id = db.resultString(result, 1);
            if(sign.getLine(1).equalsIgnoreCase(id)) {
                Bukkit.broadcastMessage("Sign already registered");
            } else {
                plugin.signList.put(sign.getLine(1), sign);
                if(addToDatabase(sign)) {
                    Bukkit.broadcastMessage("Placed Sign");
                } else
                    Bukkit.broadcastMessage("Error placing sign");
            }
        }
    }
    
    public boolean addToDatabase(Sign sign) {
        if(sign == null || !(sign instanceof Sign)) return false;
        db.query("INSERT INTO `signs`(`id`,`target`,`location_x`,`location_y`,`location_z`,`type`) VALUES("+sign.getLine(2) +", "+sign.getLine(3) +", "+sign.getBlock().getLocation().getBlockX()+", "+sign.getBlock().getLocation().getBlockY()+", "+sign.getBlock().getLocation().getBlockZ()+", "+sign.getLine(1)+")");
        
        ResultSet result = db.query("SELECT `id`,`target`,`location_x`,`location_y`,`location_z`,`type` FROM `signs` WHERE `id` = '"+sign.getLine(2) +"'");
        if(result == null)
            return false;
        else
            return true;
    }
}
