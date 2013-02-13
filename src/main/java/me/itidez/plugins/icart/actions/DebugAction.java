package me.itidez.plugins.icart.actions;

public class DebugAction implements BaseAction extends Action {
	private String name;
	private Double version;
	private boolean canceled;
	
	public DebugAction(String name) {
		this.name = name;
		this.version = 1.0;
		this.canceled = false;
	}
	
	public void execute() {
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
