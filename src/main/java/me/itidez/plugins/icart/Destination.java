package me.itidez.plugins.icart;

public class Destination {
	private String name;
	private Location loc;
	private int x;
	private int y;
	private int z;
	
	public Destination(String name, Location loc) {
		this.name = name;
		this.loc = loc;
		this.x = loc.getX();
		this.y = loc.getY();
		this.z = loc.getZ();
	}
	
	public Destination(String name, Integer x, Integer y, Integer z) {
		this.name = name;
		this.loc = new Location("world", x, y, z);
		this.x = x;
		this.y = y;
		this.z = z;
	}
	
	public void setName(String name) {
		this.name = name;	
	}
	
	public String getName() {
		return name;	
	}
	
	public void setLocation(Location loc) {
		this.loc = loc;	
	}
	
	public Location getLocation() {
		return loc;	
	}
	
	public void setX(Integer x) {
		this.x = x;	
	}
	
	public Integer getX() {
		return x;
	}
	
	public void setY(Integer y) {
		this.y = y;
	}
	
	public Integer getY() {
		return y;
	}
	
	public void setZ(Interger z) {
		this.z = z;
	}
	
	public Integer getZ() {
		return z;	
	}
}