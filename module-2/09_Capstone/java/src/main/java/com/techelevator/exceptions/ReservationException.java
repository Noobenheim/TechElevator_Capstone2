package com.techelevator.exceptions;

public class ReservationException extends RuntimeException {
	private static final long serialVersionUID = -4609441623691704492L;

	public ReservationException(String message) {
		super(message);
	}
}
