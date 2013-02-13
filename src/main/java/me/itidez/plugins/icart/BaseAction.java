package me.itidez.plugins.icart;

public interface BaseAction {
	public void execute() {};
	public String getName() {};
	public Action getAction() {};
	public boolean isCanceled() {};
	public void setCanceled() {};
}
