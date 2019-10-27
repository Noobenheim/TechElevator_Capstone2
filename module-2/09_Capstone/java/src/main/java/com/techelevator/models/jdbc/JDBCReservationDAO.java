package com.techelevator.models.jdbc;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.sql.DataSource;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;

import com.techelevator.exceptions.ReservationException;
import com.techelevator.models.Campground;
import com.techelevator.models.DatesAndRequirements;
import com.techelevator.models.Park;
import com.techelevator.models.Reservation;
import com.techelevator.models.ReservationDAO;
import com.techelevator.models.Site;

public class JDBCReservationDAO implements ReservationDAO {
	private final static int AVAILABLE_RESERVATIONS_LIMIT = 5;

	private JdbcTemplate jdbcTemplate;
	private JDBCObjectHelperDAO objectHelper;
	private JDBCParkDAO parkDAO;
	
	public JDBCReservationDAO(DataSource dataSource) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		objectHelper = new JDBCObjectHelperDAO(dataSource);
		parkDAO = new JDBCParkDAO(dataSource);
	}
	
	@Override
	public List<Site> getAvailableReservations(Campground campground, String startDate, String endDate) {
		return getAvailableReservations(campground, startDate, endDate, AVAILABLE_RESERVATIONS_LIMIT, null);
	}
	@Override
	public List<Site> getAvailableReservations(Campground campground, String startDate, String endDate, DatesAndRequirements requirements) {
		return getAvailableReservations(campground, startDate, endDate, AVAILABLE_RESERVATIONS_LIMIT, requirements);
	}
	public List<Site> getAvailableReservations(Campground campground, String startDate, String endDate, int limit) {
		return getAvailableReservations(campground, startDate, endDate, limit, null);
	}
	
	@Override
	public List<Site> getAvailableReservations(Park park, String startDate, String endDate) {
		return getAvailableReservations(park, startDate, endDate, AVAILABLE_RESERVATIONS_LIMIT, null);
	}
	@Override
	public List<Site> getAvailableReservations(Park park, String startDate, String endDate, DatesAndRequirements requirements) {
		return getAvailableReservations(park, startDate, endDate, AVAILABLE_RESERVATIONS_LIMIT, requirements);
	}
	public List<Site> getAvailableReservations(Park park, String startDate, String endDate, int limit) {
		return getAvailableReservations(park, startDate, endDate, limit, null);
	}
	
	private List<Site> getAvailableReservations(Campground campground, String startDate, String endDate, int limit, DatesAndRequirements requirements) {
		List<Site> available = new ArrayList<>();
		String sqlReservations = " SELECT site_id, campground_id, site_number, max_occupancy, accessible, max_rv_length, utilities " +
				"FROM site " + 
				"WHERE site_id NOT IN " + 
				"        (SELECT site_id " + 
				"         FROM site " + 
				"         LEFT JOIN reservation USING(site_id) " + 
				"         WHERE site.campground_id = ? AND " + 
				"                ? BETWEEN reservation.from_date AND reservation.to_date OR " + 
				"                ? BETWEEN reservation.from_date AND reservation.to_date" + 
				"        ) " +
				"AND site.campground_id = ?";
		List<Object> replacements = null;
		try {
			replacements = new ArrayList<>(Arrays.asList(campground.getCampgroundID(), LocalDate.parse(startDate), LocalDate.parse(endDate), campground.getCampgroundID()));
		} catch( DateTimeParseException e ) {
			return available;
		}
		
		if( requirements != null ) {
			// parse advanced search
			if( requirements.getPeople() != null && requirements.getPeople() > 0 ) {
				sqlReservations += "AND max_occupancy >= ? ";
				replacements.add(requirements.getPeople());
			}
			if( requirements.getWheelchair() != null && requirements.getWheelchair() ) {
				sqlReservations += "AND accessible = true ";
			}
			if( requirements.getRv() != null && requirements.getRv() > 0 ) {
				sqlReservations += "AND max_rv_length >= ? ";
				replacements.add(requirements.getRv());
			}
			if( requirements.getUtility() != null && requirements.getUtility() ) {
				sqlReservations += "AND utilities = true ";
			}
		}
		
		sqlReservations += "ORDER BY site_number ASC ";
		if( limit > 0 ) {
			sqlReservations += "LIMIT ?";
			replacements.add(limit);
		}
		SqlRowSet results = jdbcTemplate.queryForRowSet(sqlReservations, replacements.toArray(new Object[replacements.size()]));
		
		while( results.next() ) {
			available.add(objectHelper.mapRowToSite(results));
		}
		return available;
	}
	private List<Site> getAvailableReservations(Park park, String startDate, String endDate, int limit, DatesAndRequirements requirements) {
		List<Site> sites = new ArrayList<>();
		// select all campgrounds in park
		List<Campground> campgrounds = parkDAO.getCampgroundsForPark(park.getParkID());
		for( Campground c : campgrounds ) {
			sites.addAll(getAvailableReservations(c, startDate, endDate, limit, requirements));
		}
		
		return sites;
	}
	@Override
	public long makeReservation(long siteID, String name, String startDate, String endDate) throws ReservationException {
		// verify reservation is available
		String sqlCampground = "SELECT campground_id FROM site WHERE site_id = ?";
		SqlRowSet results = jdbcTemplate.queryForRowSet(sqlCampground, siteID);
		
		if( results.next() ) {
			long campgroundID = results.getLong("campground_id");
			Campground campground = objectHelper.getCampgroundByID(campgroundID);
			List<Site> allReservations = getAvailableReservations(campground, startDate, endDate, 0);
			boolean found = false;
			for( Site site : allReservations ) {
				if( site.getSiteID() == siteID ) {
					found = true;
					break;
				}
			}
			if( !found ) {
				throw new ReservationException("INVALID_RESERVATION_DATE", startDate, endDate);
			}
		} else {
			throw new ReservationException("INVALID_RESERVATION_SITE", siteID);
		}
		
		String sqlReservation = "INSERT INTO reservation(site_id, name, from_date, to_date, create_date) VALUES(?,?,?,?,now()) RETURNING reservation_id";
		results = jdbcTemplate.queryForRowSet(sqlReservation, siteID, name, LocalDate.parse(startDate), LocalDate.parse(endDate));
		if( results.next() ) {
			return results.getLong("reservation_id");
		}
		
		throw new ReservationException("UNKNOWN_ERROR");
	}

	@Override
	public List<Reservation> showFutureReservations(Park park, int days) {
		List<Reservation> reservation = new ArrayList<>();
		String sqlReservation = "SELECT reservation_id, site_id, reservation.name, from_date, to_date, create_date FROM reservation " + 
				"JOIN site USING(site_id) " +
				"JOIN campground USING(campground_id) " +
				"JOIN park USING(park_id) " +
				"WHERE (from_date BETWEEN NOW() AND (NOW() + CAST( ? || ' day' AS INTERVAL)) " + 
				"OR to_date BETWEEN NOW() AND (NOW() + CAST( ? || ' day' AS INTERVAL))) " +
				"AND park_id = ? " +
				"ORDER BY park_id, campground_id, site_id, from_date, to_date";
		SqlRowSet results = jdbcTemplate.queryForRowSet(sqlReservation, days, days, park.getParkID());
		
		while (results.next()) {
			reservation.add(objectHelper.mapRowToReservation(results));
		}
			
		return reservation;
	}
}