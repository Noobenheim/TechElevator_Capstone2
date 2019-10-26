package com.techelevator.exceptions;

public class ReservationException extends RuntimeException {
	private static final long serialVersionUID = -4609441623691704492L;
	
	private String stringID;
	private Object[] objects;

	public ReservationException(String messageID, Object...objects) {
		super(messageID);
		this.stringID = messageID;
		this.objects = objects;
	}
	
	public String getID() {
		return stringID;
	}
	public Object[] getObjects() {
		return objects;
	}
}
