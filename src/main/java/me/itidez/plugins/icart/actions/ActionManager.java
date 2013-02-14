package me.itidez.plugins.icart.actions;

public class ActionManager {
	private Class[] actions;
	private boolean initialized;
	public Icart plugin;
	
	public ActionManager(Icart plugin) {
		this.plugin = plugin;
		initialize();
	}
	
	public void initialize() {
		int i = 0;
		for(Class action : Action.getValues()) {
			actions[i] = action;
			i++;
		}
		if(actions == null) {
			initialized = false;	
		} else {
			initalized = true;	
		}
	}
	
	public Class[] getActions() {
		return actions;	
	}
	
	public Class getAction(Class action) {
		for(Class a : actions) {
				if(a == action) {
					return a;	
				}
		}
	}
	
	public void callAction(Action a, List<Object> arguments) {
		a.getAction().execute(arguments);	
	}
}