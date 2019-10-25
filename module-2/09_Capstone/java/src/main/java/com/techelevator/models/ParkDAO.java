package com.techelevator.models;

import java.util.List;

public interface ParkDAO {
	public List<Park> getAvailableParks();
	
	public List<Campground> getCampgroundsForPark(long parkID);
}
