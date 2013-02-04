package me.itidez.plugins.icart.events;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import me.itidez.plugins.icart.Icart;
import me.itidez.plugins.icart.util.Db;
import me.itidez.plugins.icart.util.FaceUtil;
import net.milkycraft.Scheduler.PlayerTimer;
import net.milkycraft.Scheduler.Schedule;
import net.milkycraft.Scheduler.Scheduler;
import net.milkycraft.Scheduler.Time;
import net.minecraft.server.v1_4_6.EntityMinecart;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Minecart;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.vehicle.VehicleEnterEvent;
import org.bukkit.event.vehicle.VehicleMoveEvent;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.util.Vector;

/**
 *
 * @author iTidez
 */
public class CartSpawner implements Listener {
    public Icart plugin;
    private Db db;
    private HashMap<Integer, Boolean> l;
    
    public CartSpawner(Icart plugin, Db db) {
        this.plugin = plugin;
        this.db = db;
        this.l = new HashMap<Integer, Boolean>();
    }
    
    @EventHandler
    public void onCartSpawned(IcartSpawnEvent event) {
        if(PlayerTimer.isCoolingDown(event.getCallerID(), Time.CARTSPAWNDELAY)) {
            event.getCaller().sendMessage(ChatColor.RED+"Error: You must wait "+PlayerTimer.getRemainingTime(event.getCallerID(), Time.CARTSPAWNDELAY)+" before spawning another cart");
            return;
        }
        Player player = event.getCaller();
        Block target = event.getTargetLocation().getBlock();
        Minecart minecart = target.getWorld().spawn(target.getLocation().add(0.5,0,0.5), Minecart.class);
        Schedule s = Scheduler.schedule(plugin, event.getCallerID(), Time.CARTSTATIONDELAY);
        Scheduler.scheduleGeneralCooldown(s);
        minecart.setMetadata("iCart", new FixedMetadataValue(plugin, "stostation,false"));
        String[] array = new String[]{"stostation", "false"};
        plugin.cartList.put(minecart.getEntityId(), array);
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
    public void onCartMove(final VehicleMoveEvent event) {
        if(event.getVehicle() instanceof Minecart) {
            Location loc = event.getTo().subtract(0, 2, 0);
            Block block = loc.getBlock();
            int entId = event.getVehicle().getEntityId();
            boolean allowed = false;
            if(l.containsKey(entId)) {
                allowed = l.get(entId);
            }
            if(/*block.getType() == Material.DETECTOR_RAIL*/(block.getType() == Material.SIGN || block.getType() == Material.WALL_SIGN || block.getType() == Material.SIGN_POST) && allowed == false) {
                Sign s = (Sign)loc.getBlock().getState();
                String type = s.getLine(2);
                /*Player p = (Player)event.getVehicle().getPassenger();
                p.sendMessage(type);*/
                if(type.equalsIgnoreCase("station")) {
                    event.getVehicle().setVelocity(new Vector(0, 0, 0));
                    event.getVehicle().getLocation().getWorld().playEffect(event.getVehicle().getLocation(), Effect.EXTINGUISH, 1004);
                    event.getVehicle().getLocation().getWorld().playEffect(event.getVehicle().getLocation(), Effect.MOBSPAWNER_FLAMES, 2000);
                    event.getVehicle().getLocation().getWorld().playEffect(event.getVehicle().getLocation(), Effect.ENDER_SIGNAL, 2003);
                    //Bukkit.broadcastMessage(ChatColor.YELLOW+"MC UUID"+event.getVehicle().getEntityId());
                    if(s.getLine(3).equalsIgnoreCase("delete")) {
                        event.getVehicle().leaveVehicle();
                        event.getVehicle().remove();
                    }
                    ResultSet result;
                    result = db.query("SELECT `id`, `location` FROM `icarts` WHERE `id` = '"+entId+"'");
                    try {
                        if(result.next()) {
                        
                        } else {
                            db.query("INSERT INTO `icarts`(`id`,`location`,`move_allowed`) VALUES('"+event.getVehicle().getEntityId()+"', '"+loc.getX()+","+loc.getY()+","+loc.getZ()+"', 'false')");
                        }
                    } catch (SQLException ex) {
                        Logger.getLogger(CartSpawner.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            } else if(block.getType() == Material.SIGN || block.getType() == Material.WALL_SIGN || block.getType() == Material.SIGN_POST) {
                Sign s = (Sign)loc.getBlock().getState();
                /*Player p = (Player)event.getVehicle().getPassenger();
                p.sendMessage(s.getLine(2));*/
                if(s.getLine(2).equalsIgnoreCase("exit")) {
                    //Bukkit.broadcastMessage(s.getLine(2));
                     //event.getVehicle().remove();
                    Location targetLoc = null;
   
                    ResultSet result = db.query("SELECT `id`, `location_x` FROM `signs` WHERE `id` = '"+s.getLine(3)+"'");
                    try {
                        if(result.next()) {
                            result.close();
                            result = db.query("SELECT `id`, `location_x` FROM `signs` WHERE `id` = '"+s.getLine(3)+"'");
                            int x = db.resultInt(result, 2);
                            result.close();
                            result = db.query("SELECT `id`, `location_Y` FROM `signs` WHERE `id` = '"+s.getLine(3)+"'");
                            int y = db.resultInt(result, 2);
                            result.close();
                            result = db.query("SELECT `id`, `location_Z` FROM `signs` WHERE `id` = '"+s.getLine(3)+"'");
                            int z = db.resultInt(result, 2);
                            result.close();
                            targetLoc = new Location(event.getVehicle().getWorld(), x, y, z).add(new Location(event.getVehicle().getWorld(), 0, 2, 0));
                            targetLoc = targetLoc.add(new Location(event.getVehicle().getWorld(), (double)0, (double)2.5, (double)0, 150F, 150F));
                        }
                    } catch (SQLException ex) {
                        Logger.getLogger(CartSpawner.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    final Entity ev = event.getVehicle().getPassenger();
                    event.getVehicle().eject();
                    event.getVehicle().teleport(targetLoc);
                    event.getVehicle().teleport(targetLoc);
                    Bukkit.getScheduler().runTaskLaterAsynchronously(plugin, new Runnable() {
                        public void run() {
                            event.getVehicle().setPassenger(ev);
                        }
                    }, 1
                    );
                }
            }
        }
    }
    
    @EventHandler
    public void onPlayerEnter(VehicleEnterEvent event) {
        if(event.getEntered() instanceof Player) {
            Player p = (Player)event.getEntered();
            if(event.getVehicle() instanceof Minecart) {
                //String allowed = null;
                Location loc = null;
                ResultSet result = db.query("SELECT `id`, `location`, `move_allowed` FROM `icarts` WHERE `id` = '"+event.getVehicle().getEntityId()+"'");
                try {
                    if(result.next()) {
                        //allowed = db.resultString(result, 3);
                        //String[] locs = db.resultString(result, 2).split(",");
                        plugin.cartList.remove(event.getVehicle().getEntityId());
                        String[] array = new String[] {"stoexit", "true"};
                        plugin.cartList.put(event.getVehicle().getEntityId(), array);
                        loc = event.getVehicle().getLocation();
                        loc = loc.subtract(0, 2, 0);
                        if(l.containsKey(event.getVehicle().getEntityId())) {
                            l.remove(event.getVehicle().getEntityId());
                        }
                        l.put(event.getVehicle().getEntityId(), false);
                    }
                } catch (SQLException ex) {
                    Logger.getLogger(CartSpawner.class.getName()).log(Level.SEVERE, null, ex);
                }
                    if(l.get(event.getVehicle().getEntityId()) == false) {
                        //db.query("UPDATE icarts SET move_allowed='true' WHERE id='"+event.getVehicle().getEntityId()+"'");
                        if(loc.getBlock().getType() == Material.SIGN || loc.getBlock().getType() == Material.WALL_SIGN || loc.getBlock().getType() == Material.SIGN_POST) {
                           //p.sendMessage(ChatColor.GREEN+"Sign Detected");
                            Sign s = (Sign)loc.getBlock().getState();
                            String target = s.getLine(3);
                            result = db.query("SELECT `id`,`location_x`,`location_y`,`location_z` FROM `signs` WHERE `id` = '"+target+"'");
                            try {
                                if(result.next()) {
                                    result = db.query("SELECT `location_x` FROM `signs` WHERE `id` = '"+target+"'");
                                    int x = db.resultInt(result, 1);
                                    result.close();
                                    result = db.query("SELECT `location_y`FROM `signs` WHERE `id` = '"+target+"'");
                                    int y = db.resultInt(result, 1);
                                    result.close();
                                    result = db.query("SELECT `location_z` FROM `signs` WHERE `id` = '"+target+"'");
                                    int z = db.resultInt(result, 1);
                                    result.close();
                                    l.remove(event.getVehicle().getEntityId());
                                    l.put(event.getVehicle().getEntityId(), true);
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
