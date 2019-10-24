package com.techelevator.models;

import java.util.List;

public interface ParkDAO {
	public List<Park> getAvailablePerks();
	
	public List<Campground> getCampgroundsForPark(long parkID);
}
