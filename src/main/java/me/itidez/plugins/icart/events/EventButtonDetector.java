package me.itidez.plugins.icart.events;

import java.util.ArrayList;
import java.util.List;
import me.itidez.plugins.icart.Icart;
import me.itidez.plugins.icart.util.Db;
import me.itidez.plugins.icart.util.Util;
import net.milkycraft.Scheduler.GeneralTimer;
import net.milkycraft.Scheduler.PlayerTimer;
import net.milkycraft.Scheduler.Schedule;
import net.milkycraft.Scheduler.Scheduler;
import net.milkycraft.Scheduler.Time;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;

/**
 *
 * @author iTidez
 */
public class EventButtonDetector implements Listener {
    public Icart plugin;
    public Db db;
    
    public EventButtonDetector(Icart plugin, Db db) {
        this.plugin = plugin;
        this.db = db;
    }
    
    @EventHandler
    public void onButtonPush(PlayerInteractEvent event) {
        if(event.isCancelled()) return;
        Player player = event.getPlayer();
        Block block = event.getClickedBlock();
        if(block.getType() == Material.STONE_BUTTON || block.getType() == Material.WOOD_BUTTON) {
            Location startLoc = block.getLocation();
            List<Location> locs = getNearLoc(startLoc);
            for(Location loc : locs) {
                Block b = loc.getBlock();
                //Bukkit.broadcastMessage(loc.getBlockX() + " " + loc.getBlockY() + " " + loc.getBlockZ());
                if(b.getType() == Material.SIGN || b.getType() == Material.WALL_SIGN) {
                    Sign sign = (Sign)b.getState();
                    /*if(Icart.signList.containsKey(loc))
                        sign = Icart.signList.get(loc);
                    if(sign == null)
                        return;*/
                    String[] lines = sign.getLines();
                    //Util.debug("Sign detected: Lines: "+lines[0]+" "+lines[1]+" "+lines[2]+" "+lines[3]);
                    if(lines[0].equalsIgnoreCase(ChatColor.DARK_BLUE+"[iCart]") && lines[2].equalsIgnoreCase(ChatColor.GREEN+"Call")) {
                        if(PlayerTimer.isCoolingDown("iTidez", Time.CARTSPAWNDELAY)) {
                            return;
                        }
                        Schedule s = Scheduler.schedule(plugin, "iTidez", Time.CARTSPAWNDELAY);
                        Scheduler.schedulePlayerCooldown(s);
                        IcartSpawnEvent se = new IcartSpawnEvent(b, lines[1], lines[3], player, db);
                        Bukkit.getServer().getPluginManager().callEvent(se);
                        //player.sendMessage(ChatColor.GREEN+"Call button pushed!");
                    }
                }
            }
            //Bukkit.broadcastMessage(ChatColor.GREEN+"Button Pushed!");
        }
    }
    
    public List<Location> getNearLoc(Location loc) {
        List<Location> locList = new ArrayList<Location>();
        if(loc == null) {
            return null;
        }
        locList.add(loc.getBlock().getRelative(BlockFace.EAST).getLocation());
        locList.add(loc.getBlock().getRelative(BlockFace.WEST).getLocation());
        locList.add(loc.getBlock().getRelative(BlockFace.NORTH).getLocation());
        locList.add(loc.getBlock().getRelative(BlockFace.SOUTH).getLocation());
        List<Location> locList2 = new ArrayList<Location>();
        for(Location locs : locList) {
            locList2.add(locs.getBlock().getRelative(BlockFace.EAST).getLocation());
            locList2.add(locs.getBlock().getRelative(BlockFace.WEST).getLocation());
            locList2.add(locs.getBlock().getRelative(BlockFace.NORTH).getLocation());
            locList2.add(locs.getBlock().getRelative(BlockFace.SOUTH).getLocation());
        }
        /*Location locs = loc.;
        locList.add(locs);
        locs = loc.add(-1, 0, 0);
        locList.add(locs);
        locs = loc.add(0, 0, 1);
        locList.add(locs);
        locs = loc.add(0, 0, -1);
        locList.add(locs);
        locs = loc.add(0, 1, 0);
        locList.add(locs);
        locs = loc.add(0, -1, 0);
        locList.add(locs);
        locs = loc.add(2, 0, 0);
        locList.add(locs);
        locs = loc.add(-2, 0, 0);
        locList.add(locs);
        locs = loc.add(0, 0, 2);
        locList.add(locs);
        locs = loc.add(0, 0, -2);
        locList.add(locs);*/
        return locList2;
    }
}
