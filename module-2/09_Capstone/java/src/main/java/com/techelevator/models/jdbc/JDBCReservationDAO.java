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
import com.techelevator.models.ReservationDAO;
import com.techelevator.models.Site;

public class JDBCReservationDAO implements ReservationDAO {
	private final static int AVAILABLE_RESERVATIONS_LIMIT = 5;

	private JdbcTemplate jdbcTemplate;
	
	public JDBCReservationDAO(DataSource dataSource) {
		jdbcTemplate = new JdbcTemplate(dataSource);
	}
	
	@Override
	public List<Site> getAvailableReservations(long campgroundID, String startDate, String endDate) {
		return getAvailableReservations(campgroundID, startDate, endDate, AVAILABLE_RESERVATIONS_LIMIT);
	}
	public List<Site> getAvailableReservations(long campgroundID, String startDate, String endDate, int limit) {
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
				"AND site.campground_id = ? " +
				"ORDER BY site_number ASC ";
		List<Object> replacements = null;
		try {
			replacements = Arrays.asList(campgroundID, LocalDate.parse(startDate), LocalDate.parse(endDate), campgroundID);
		} catch( DateTimeParseException e ) {
			return available;
		}
		if( limit > 0 ) {
			sqlReservations += "LIMIT ?";
			replacements.add(limit);
		}
		SqlRowSet results = jdbcTemplate.queryForRowSet(sqlReservations, replacements.toArray(new Object[replacements.size()]));
		
		while( results.next() ) {
			available.add(mapRowToSite(results));
		}
		return available;
	}

	@Override
	public long makeReservation(long siteID, String name, String startDate, String endDate) throws ReservationException {
		// verify reservation is available
		String sqlCampground = "SELECT campground_id FROM site WHERE site_id = ?";
		SqlRowSet results = jdbcTemplate.queryForRowSet(sqlCampground, siteID);
		
		if( results.next() ) {
			long campgroundID = results.getLong("campground_id");
			List<Site> allReservations = getAvailableReservations(campgroundID, startDate, endDate, 0);
			boolean found = false;
			for( Site site : allReservations ) {
				if( site.getSiteID() == siteID ) {
					found = true;
					break;
				}
			}
			if( !found ) {
				throw new ReservationException(String.format("Date range (%s - %s) is not available for reservation", startDate, endDate));
			}
		} else {
			throw new ReservationException(String.format("Unknown site ID : %d", siteID));
		}
		
		String sqlReservation = "INSERT INTO reservation(site_id, name, from_date, to_date, create_date) VALUES(?,?,?,?,now()) RETURNING reservation_id";
		results = jdbcTemplate.queryForRowSet(sqlReservation, siteID, name, LocalDate.parse(startDate), LocalDate.parse(endDate));
		if( results.next() ) {
			return results.getLong("reservation_id");
		}
		
		throw new ReservationException(String.format("An unknown error has occurred."));
	}

	private Site mapRowToSite(SqlRowSet results) {
		Site site = new Site();
		
		site.setSiteID(results.getLong("site_id"));
		site.setCampgroundID(results.getLong("campground_id"));
		site.setSiteNumber(results.getLong("site_number"));
		site.setMaxOccupancy(results.getLong("max_occupancy"));
		site.setAccessible(results.getBoolean("accessible"));
		site.setMaxRVLength(results.getInt("max_rv_length"));
		site.setUtilities(results.getBoolean("utilities"));
		
		return site;
	}
}