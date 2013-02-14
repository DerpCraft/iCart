package me.itidez.plugins.icart.actions;

public enum Action {
	BroadcastAction(BroadcastAction.class),
	DebugAction(DebugAction.class);
	
	public Class value;
	
	public Action(Class value) {
		this.value = value;	
	}
	
	public Class getAction() {
		return value;	
	}
}