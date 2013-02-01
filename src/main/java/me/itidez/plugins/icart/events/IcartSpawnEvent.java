package me.itidez.plugins.icart.events;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import me.itidez.plugins.icart.util.Db;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 *
 * @author iTidez
 */
public class IcartSpawnEvent extends Event{
    private static final HandlerList handlers = new HandlerList();
    private Block sign;
    private String callerId;
    private String target;
    private Player player;
    private Db db;
    
    public IcartSpawnEvent(Block sign, String callerId, String target, Player playerId, Db db) {
        this.target = target;
        this.sign = sign;
        this.callerId = callerId;
        this.player = playerId;
        this.db = db;
    }
    
    public Block getCallerBlock() {
        return sign;
    }
    
    public String getCallerID() {
        return callerId;
    }
    
    public Player getCaller() {
        return player;
    }
    
    public Location getLocation() {
        ResultSet result = db.query("SELECT `location_x` FROM `signs` WHERE `id` = '"+ target +"'");
        try {
            if(!result.next()) {
                player.sendMessage("Error");
                return null;
            }
        } catch (SQLException ex) {
            Logger.getLogger(IcartSpawnEvent.class.getName()).log(Level.SEVERE, null, ex);
        }
        int x = db.resultInt(result, 1);
        try {
            result.close();
        } catch (SQLException ex) {
            Logger.getLogger(IcartSpawnEvent.class.getName()).log(Level.SEVERE, null, ex);
        }
        result = db.query("SELECT `location_y` FROM `signs` WHERE `id` = '"+ target +"'");
        int y = db.resultInt(result, 1);
        try {
            result.close();
        } catch (SQLException ex) {
            Logger.getLogger(IcartSpawnEvent.class.getName()).log(Level.SEVERE, null, ex);
        }
        result = db.query("SELECT `location_z`FROM `signs` WHERE `id` = '"+ target +"'");
        int z = db.resultInt(result, 1);
        try {
            result.close();
        } catch (SQLException ex) {
            Logger.getLogger(IcartSpawnEvent.class.getName()).log(Level.SEVERE, null, ex);
        }
        Location loc = new Location(Bukkit.getWorld("world"), x, y, z);
        return loc;
    }
    
    public Block getBlock() {
        ResultSet result = db.query("SELECT `location_x` FROM `signs` WHERE `id` = '"+ target +"'");
        try {
            if(!result.next()) {
                player.sendMessage("Error");
                return null;
            }
        } catch (SQLException ex) {
            Logger.getLogger(IcartSpawnEvent.class.getName()).log(Level.SEVERE, null, ex);
        }
        int x = db.resultInt(result, 1);
        try {
            result.close();
        } catch (SQLException ex) {
            Logger.getLogger(IcartSpawnEvent.class.getName()).log(Level.SEVERE, null, ex);
        }
        result = db.query("SELECT `location_y` FROM `signs` WHERE `id` = '"+ target +"'");
        int y = db.resultInt(result, 1);
        try {
            result.close();
        } catch (SQLException ex) {
            Logger.getLogger(IcartSpawnEvent.class.getName()).log(Level.SEVERE, null, ex);
        }
        result = db.query("SELECT `location_z`FROM `signs` WHERE `id` = '"+ target +"'");
        int z = db.resultInt(result, 1);
        try {
            result.close();
        } catch (SQLException ex) {
            Logger.getLogger(IcartSpawnEvent.class.getName()).log(Level.SEVERE, null, ex);
        }
        Location loc = new Location(Bukkit.getWorld("world"), x, y, z);
        if(loc.getBlock().getType() == Material.SIGN || loc.getBlock().getType() == Material.WALL_SIGN) {
            return loc.getBlock();
        } else {
            player.sendMessage(ChatColor.RED+"Error grabbing location: "+x+", "+y+", "+z);
            return null;
        }
    }
    
    public Location getTargetLocation() {
        ResultSet result = db.query("SELECT `location_x`,`location_y`,`location_z`FROM `signs` WHERE `id` = '"+ target +"'");
        int x = db.resultInt(result, 1);
        try {
            result.close();
        } catch (SQLException ex) {
            Logger.getLogger(IcartSpawnEvent.class.getName()).log(Level.SEVERE, null, ex);
        }
        result = db.query("SELECT `location_y` FROM `signs` WHERE `id` = '"+ target +"'");
        int y = db.resultInt(result, 1);
        try {
            result.close();
        } catch (SQLException ex) {
            Logger.getLogger(IcartSpawnEvent.class.getName()).log(Level.SEVERE, null, ex);
        }
        result = db.query("SELECT `location_z`FROM `signs` WHERE `id` = '"+ target +"'");
        int z = db.resultInt(result, 1);
        try {
            result.close();
        } catch (SQLException ex) {
            Logger.getLogger(IcartSpawnEvent.class.getName()).log(Level.SEVERE, null, ex);
        }
        Location loc = new Location(Bukkit.getWorld("world"), x, y + 2, z);
        return loc;
    }
    
    public String getTargetName() {
        ResultSet result = db.query("SELECT `target` FROM `signs` WHERE `id` = '"+ target +"'");
        return db.resultString(result, 1);
    }
    
    public Block getTargetedBlock() {
        ResultSet result = db.query("SELECT `location_x`,`location_y`,`location_z`FROM `signs` WHERE `id` = '"+ target +"'");
        int x = db.resultInt(result, 1);
        try {
            result.close();
        } catch (SQLException ex) {
            Logger.getLogger(IcartSpawnEvent.class.getName()).log(Level.SEVERE, null, ex);
        }
        result = db.query("SELECT `location_y` FROM `signs` WHERE `id` = '"+ target +"'");
        int y = db.resultInt(result, 1);
        try {
            result.close();
        } catch (SQLException ex) {
            Logger.getLogger(IcartSpawnEvent.class.getName()).log(Level.SEVERE, null, ex);
        }
        result = db.query("SELECT `location_z`FROM `signs` WHERE `id` = '"+ target +"'");
        int z = db.resultInt(result, 1);
        try {
            result.close();
        } catch (SQLException ex) {
            Logger.getLogger(IcartSpawnEvent.class.getName()).log(Level.SEVERE, null, ex);
        }
        Location loc = new Location(Bukkit.getWorld("world"), x, y, z);
        return loc.getBlock();
    }
    
    public HandlerList getHandlers() {
        return handlers;
    }
    
    public static HandlerList getHandlerList() {
        return handlers;
    }
}
