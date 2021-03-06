package me.itidez.plugins.icart.actions;

public class DebugAction implements BaseAction extends Action {
	private String name;
	private Double version;
	private static boolean canceled;
	
	public static void execute(List<Object> arguments) {
		if(isCanceled()) {
			return;	
		}
		Bukkit.getServer().broadcastMessage(ChatColor.DARK_AQUA+"Debug Action successfully passed");
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
