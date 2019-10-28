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
	
	public Campground getCampgroundByID(long campgroundID);
	public Park getParkByID(long parkID);
	public Site getSiteByID(long siteID);
	
	public void ensureClassExists(NationalParkObject o, Class<?> c);
}
