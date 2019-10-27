package com.techelevator.models;

import org.springframework.jdbc.support.rowset.SqlRowSet;

public interface ObjectHelperDAO {
	public Park mapRowToPark(SqlRowSet results);
	public Campground mapRowToCampground(SqlRowSet results);
	public Site mapRowToSite(SqlRowSet results);
	public Reservation mapRowToReservation(SqlRowSet results);
	
	public void mapSiteToReservation(Reservation reservation);
	public void mapCampgroundToSite(Site site);
	public void mapParkToCampground(Campground campground);
	
	public void ensureClassExists(NationalParkPOJO o, Class<?> c);
}
