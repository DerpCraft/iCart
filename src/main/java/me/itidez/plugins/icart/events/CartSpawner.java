package me.itidez.plugins.icart.events;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import me.itidez.plugins.icart.Icart;
import me.itidez.plugins.icart.util.Db;
import me.itidez.plugins.icart.util.FaceUtil;
import net.minecraft.server.v1_4_6.EntityMinecart;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.entity.Minecart;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.vehicle.VehicleEnterEvent;
import org.bukkit.event.vehicle.VehicleMoveEvent;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.util.Vector;

/**
 *
 * @author iTidez
 */
public class CartSpawner implements Listener {
    public Icart plugin;
    private Db db;
    
    public CartSpawner(Icart plugin, Db db) {
        this.plugin = plugin;
        this.db = db;
    }
    
    @EventHandler
    public void onCartSpawned(IcartSpawnEvent event) {
        Player player = event.getCaller();
        Block target = event.getTargetLocation().getBlock();
        Minecart minecart = target.getWorld().spawn(target.getLocation().add(0.5,0,0.5), Minecart.class);
        minecart.setMetadata("iCart", new FixedMetadataValue(plugin, "stostation"));
        player.sendMessage(event.getTargetName());
        Location loc = null;
        try {
            loc = getTargetLocation(event.getTargetName());
        } catch (SQLException ex) {
            Logger.getLogger(CartSpawner.class.getName()).log(Level.SEVERE, null, ex);
        }
        Vector velocity = new Vector(loc.getBlockX(), loc.getBlockY(), loc.getBlockZ());
        minecart.setVelocity(velocity);
        FaceUtil.yawToFace(minecart.getLocation().getYaw(), false);
        event.getCaller().sendMessage(ChatColor.GREEN+"Event passed");
    }
    
    @EventHandler
    public void onCartMove(VehicleMoveEvent event) {
        if(event.getVehicle() instanceof Minecart) {
            Location loc = event.getTo();
            boolean allowed = false;
            Block block = loc.getBlock();
            db.query("CREATE TABLE IF NOT EXISTS `icarts` (`id` INT(10) NOT NULL, `location` VARCHAR(50) NOT NULL, `move_allowed` VARCHAR(10), PRIMARY KEY (`id`) ) ENGINE=MyISAM DEFAULT CHARSET=latin1;");
            ResultSet result = db.query("SELECT `id` WHERE id = '"+event.getVehicle().getEntityId()+"'");
            try {
                if(result.next()) {
                    allowed = true;
                }
            } catch (SQLException ex) {
                Logger.getLogger(CartSpawner.class.getName()).log(Level.SEVERE, null, ex);
            }
            if(block.getType() == Material.DETECTOR_RAIL && allowed == false) {
                event.getVehicle().setVelocity(new Vector(0, 0, 0));
                //Bukkit.broadcastMessage(ChatColor.YELLOW+"MC UUID"+event.getVehicle().getEntityId());
                result = db.query("SELECT `id`, `location` WHERE id='"+event.getVehicle().getEntityId()+"'");
                try {
                    if(result.next()) {
                        
                    } else {
                        db.query("INSERT INTO `icarts`(`id`,`location`,`move_allowed`) VALUES('"+event.getVehicle().getEntityId()+"', '"+loc.getX()+","+loc.getY()+","+loc.getZ()+"', 'false')");
                    }
                } catch (SQLException ex) {
                    Logger.getLogger(CartSpawner.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
    }
    
    @EventHandler
    public void onPlayerEnter(VehicleEnterEvent event) {
        if(event.getEntered() instanceof Player) {
            Player p = (Player)event.getEntered();
            if(event.getVehicle() instanceof Minecart) {
                String allowed = null;
                Location loc = null;
                ResultSet result = db.query("SELECT `id`, `location`, `move_allowed` WHERE id='"+event.getVehicle().getEntityId()+"'");
                try {
                    if(result.next()) {
                        allowed = db.resultString(result, 3);
                        String[] locs = db.resultString(result, 2).split(",");
                        loc = new Location(Bukkit.getWorld("world"),Double.parseDouble(locs[0]), Double.parseDouble(locs[1]) - 2, Double.parseDouble(locs[2]));
                    }
                } catch (SQLException ex) {
                    Logger.getLogger(CartSpawner.class.getName()).log(Level.SEVERE, null, ex);
                }
                    if(allowed.equalsIgnoreCase("false")) {
                        db.query("UPDATE icarts SET move_allowed='true' WHERE id='"+event.getVehicle().getEntityId()+"'");
                        if(loc.getBlock().getType() == Material.SIGN || loc.getBlock().getType() == Material.WALL_SIGN || loc.getBlock().getType() == Material.SIGN_POST) {
                            p.sendMessage(ChatColor.GREEN+"Sign Detected");
                            Sign s = (Sign)loc.getBlock().getState();
                            String target = s.getLine(3);
                            result = db.query("SELECT `id`,`location_x`,`location_y`,`location_z` WHERE id=`"+target+"'");
                            try {
                                if(result.next()) {
                                    int x = db.resultInt(result, 2);
                                    int y = db.resultInt(result, 3);
                                    int z = db.resultInt(result, 4);
                                    event.getVehicle().setVelocity(new Vector(x, y, z));
                                }
                            } catch (SQLException ex) {
                                Logger.getLogger(CartSpawner.class.getName()).log(Level.SEVERE, null, ex);
                            }
                        }
                    } else {
                }
            }
        }
    }
    
    public Location getTargetLocation(String orig) throws SQLException {
        int x = 0;
        int y = 0;
        int z = 0;
        ResultSet result = db.query("SELECT `target` FROM `signs` WHERE `id` = '"+ orig +"'");
                String target = db.resultString(result, 1);
                result.close();
                result = db.query("SELECT `location_x` FROM `signs` WHERE `id` = '"+ orig +"'");
                x = db.resultInt(result, 1);
                result.close();
                result = db.query("SELECT `location_y` FROM `signs` WHERE `id` = '"+ orig +"'");
                y = db.resultInt(result, 1)+2;
                result.close();
                result = db.query("SELECT `location_z` FROM `signs` WHERE `id` = '"+ orig +"'");
                z = db.resultInt(result, 1);
                result.close();
        return new Location(Bukkit.getWorld("world"), x, y, z);
    }
}
