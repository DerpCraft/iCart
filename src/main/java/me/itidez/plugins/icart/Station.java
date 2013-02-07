package me.itidez.plugins.icart;

public class Station extends Destination {
	private Location loc;
	private String id;
	private boolean allowed;
	
	public Station(String id, Location loc) {
		super(id, loc);
		this.loc = loc;
		this.id = id;
		if(isStation()) {
			allowed = true;
		} else {
			allowed = false;	
		}
	}
	
	public String[] getActions() {
		if(!allowed) {
			return null;
		}
		ResultSet result = db.query("SELECT `actions` FROM `station` WHERE `id` = '"+this.id+"'");
		String before = result.getString(1);
		result.close();
		String[] actions = new String[];
		if(before.equalsIgnoreCase("default")) {
			actions[0] = "default";
			return 	actions;
		}
		actions = before.split(",");
		return actions;
	}
	
	public Location getLocation() {
		return loc;	
	}
	
	private boolean isStation() {
		ResultSet result = db.query("SELECT `id` FROM `station` WHERE `id` = '"+this.id+"'");
		if(result.next()) {
			result.close();
			return true;	
		} else {
			result.close();
			return false;	
		}
	}
}