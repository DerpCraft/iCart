package me.itidez.plugins.icart.events;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import me.itidez.plugins.icart.Icart;
import me.itidez.plugins.icart.util.Db;
import me.itidez.plugins.icart.util.Util;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.SignChangeEvent;

/**
 *
 * @author iTidez
 */
public class EventSignDetector implements Listener {

    public Icart plugin;
    private Db db;
    
    public EventSignDetector(Icart plugin, Db db) {
        this.plugin = plugin;
        this.db = db;
    }
    
    @EventHandler
    public void onSignBreak(BlockBreakEvent event) {
        if(event.isCancelled()) return;
        Block b = event.getBlock();
        if(b.getType() == Material.SIGN || b.getType() == Material.WALL_SIGN) {
            Sign s = (Sign)b.getLocation().getBlock().getState();
            String[] lines = s.getLines();
            ResultSet result = db.query("SELECT `id`,`target`,`location_x`,`location_y`,`location_z`,`type` FROM `signs` WHERE `id` = '"+ lines[1] +"'");
            String id = db.resultString(result, 1);
            if(lines[1].equalsIgnoreCase(id)) {
                db.query("DELETE FROM `signs` WHERE `id`='"+lines[1]+"' ");
                try {
                    result.close();
                    //Bukkit.broadcastMessage(ChatColor.GREEN+"Entery Deleted");
                } catch (SQLException ex) {
                    Logger.getLogger(EventSignDetector.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
    }
    
    @EventHandler
    public void onSignChange(SignChangeEvent event) {
        if(event.isCancelled()) return;
        String line1 = event.getLine(0);
        String line2 = event.getLine(1);
        String line3 = event.getLine(2);
        String line4 = event.getLine(3);
        //Sign sign = (Sign)event.getBlock().getState();
        if(line1.equalsIgnoreCase("[iCart]")) {
            //Bukkit.broadcastMessage("Found sign");
            event.setLine(0, ChatColor.DARK_BLUE+"[iCart]");
            event.getBlock().getState().update();
            //sign.setLine(1, ChatColor.BLUE+"[iCart]");
            //sign.update();
            //String[] lines = sign.getLines();
            if(line2 == null) {
                Util.debug("Error - Line 1 Null");
                return;
            }
            db.query("CREATE TABLE IF NOT EXISTS `signs` (`id` VARCHAR(16) NOT NULL,`target` VARCHAR(16) NOT NULL, `location_x` INT(16) NOT NULL, `location_y` INT(16) NOT NULL, `location_z` INT(16) NOT NULL, `type` VARCHAR(16) NOT NULL, PRIMARY KEY (`id`) ) ENGINE=MyISAM DEFAULT CHARSET=latin1;");
            if(line3.equalsIgnoreCase("Call")) {
                ResultSet result = db.query("SELECT `id`,`target`,`location_x`,`location_y`,`location_z`,`type` FROM `signs` WHERE `id` = '"+ line4 +"'");
                try {
                    if(result.next()) {
                        String id = db.resultString(result, 1);
                        event.setLine(2, ChatColor.GREEN+line3);
                        result.close();
                    } else {
                        event.setLine(2, ChatColor.RED+line3);
                        result.close();
                    }
                } catch (SQLException ex) {
                    String s = ex.getMessage();
                    Util.severe(s);
                }
                
            }
            //Bukkit.broadcastMessage(line1 + " " + line2 + line3 + line4);
            ResultSet result = db.query("SELECT `id`,`target`,`location_x`,`location_y`,`location_z`,`type` FROM `signs` WHERE `id` = '"+ line2 +"'");
            try {
                if(result.next()) {
                   
                } else {
                     if(addToDatabase(line2, line3, line4, event.getBlock().getLocation())) {
                        //Bukkit.broadcastMessage("Placed Sign");
                        Util.debug("Placed sign");
                        result.close();
                    } else {
                        Util.debug("Error saving sign data");
                        result.close();
                    }
                }
            } catch (SQLException ex) {
                Logger.getLogger(EventSignDetector.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
    
    public boolean addToDatabase(String id, String type, String target, Location loc) {
        //if(sign == null || !(sign instanceof Sign)) return false;
        //String[] lines = sign.getLines();
        db.query("INSERT INTO `signs`(`id`,`target`,`location_x`,`location_y`,`location_z`,`type`) VALUES('"+id+"', '"+target+"', "+loc.getBlockX()+", "+loc.getBlockY()+", "+loc.getBlockZ()+", '"+type+"')");
        ResultSet result = db.query("SELECT `id`,`target`,`location_x`,`location_y`,`location_z`,`type` FROM `signs` WHERE `id` = '"+ id +"'");
        try {
            if(result.next()) {
                result.close();
                return true;
            } else {
                result.close();
                return false;
            }
        } catch (SQLException ex) {
            Logger.getLogger(EventSignDetector.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }
    }
}
