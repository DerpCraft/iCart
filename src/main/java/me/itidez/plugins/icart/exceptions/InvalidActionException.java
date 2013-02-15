package me.itidez.plugins.icart.exceptions;

public class InvalidActionException extends Exception {
	private static final long serialVersionUID = 1897047051293914036L;
	
	public InvalidActionException() {
		super("The action defined is either un-registered, undefined, or non-existant");	
	}
}