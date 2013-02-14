package me.itidez.plugins.icart.actions;

public class BroadcastAction implements BaseAction {
	public String name;
	public Action action;
	public Double version;
	public String message;
	
	public static void execute(List<Object> arguments) {
		if(isCanceled()) {
			return;	
		}
		Bukkit.getServer().broadcastMessage(ChatColor.translateAlternateColorCodes("&", message);
	}
	
	public String getName() {
		return this.name;
	}
	
	public Action getAction() {
		return (Action)this;	
	}
	
	public boolean isCanceled() {
		return canceled;
	}
	
	public void setCanceled(boolean result) {
		this.canceled = result;	
	}
}