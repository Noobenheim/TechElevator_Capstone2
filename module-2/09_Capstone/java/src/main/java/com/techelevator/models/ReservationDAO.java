package com.techelevator.models;

import java.util.List;

public interface ReservationDAO {
	
	public List<Reservation> showFutureReservations(Park park, int days);

	public List<Site> getAvailableReservations(Campground campground, String startDate, String endDate, DatesAndRequirements requirements);
	public List<Site> getAvailableReservations(Campground campground, String startDate, String endDate);

	public List<Site> getAvailableReservations(Park park, String startDate, String endDate, DatesAndRequirements requirements);
	public List<Site> getAvailableReservations(Park park, String startDate, String endDate);
	
	public long makeReservation(long siteID, String name, String startDate, String endDate);
}
