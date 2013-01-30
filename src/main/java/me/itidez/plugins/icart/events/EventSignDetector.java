package me.itidez.plugins.icart.events;

import me.itidez.plugins.icart.Icart;
import org.bukkit.Bukkit;
import org.bukkit.block.Sign;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.SignChangeEvent;

/**
 *
 * @author iTidez
 */
public class EventSignDetector implements Listener {

    @EventHandler
    public void onSignChange(SignChangeEvent event) {
        if(event.isCancelled()) return;
        String line1 = event.getLine(1);
        Sign sign = (Sign)event.getBlock().getState();
        if(line1.equalsIgnoreCase("[iCart]")) {
            Bukkit.broadcastMessage("Found sign");
            if(Icart.signList.containsKey(event.getBlock().getLocation())) {
                Bukkit.broadcastMessage("Sign already registered");
                return;
            } else {
                    Icart.signList.put(event.getBlock().getLocation(), sign);
                    Bukkit.broadcastMessage("Placed Sign");
                    
            }
        }
    }
}
