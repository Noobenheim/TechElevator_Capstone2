package com.techelevator.models;

import java.util.List;

public interface ReservationDAO {
	public List<Site> getAvailableReservations(long campgroundID, String startDate, String endDate);
	
	public long makeReservation(long siteID, String name, String startDate, String endDate);
}