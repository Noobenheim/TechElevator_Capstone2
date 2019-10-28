package com.techelevator;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.SingleConnectionDataSource;
import org.springframework.jdbc.support.rowset.SqlRowSet;

import com.techelevator.models.Campground;
import com.techelevator.models.Park;
import com.techelevator.models.Site;
import com.techelevator.models.jdbc.JDBCObjectHelperDAO;

public class IntegrationTestHelper {
	private JdbcTemplate jdbcTemplate;
	private JDBCObjectHelperDAO objectHelper;
	
	public IntegrationTestHelper(SingleConnectionDataSource dataSource) {
		this.jdbcTemplate = new JdbcTemplate(dataSource);
		this.objectHelper = new JDBCObjectHelperDAO(dataSource);
	}
	
	public Park createFakePark(String name, String location, String date, long area, long visitors, String description) {
		String sqlInsertPark = "INSERT INTO park (name, location, establish_date, area, visitors, description) "
				+ "VALUES (?, ?, ?, ?, ?, ?) RETURNING park_id";

		DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd");
		LocalDate dt = LocalDate.parse("1978-05-21", dtf);

		SqlRowSet results = jdbcTemplate.queryForRowSet(sqlInsertPark, name, location, dt, area, visitors, description);
		
		long id = Long.MIN_VALUE;
		Park park = null;
		
		if( results.next() ) {
			id = results.getLong("park_id");
		}
		
		if( id > 0 ) {
			park = objectHelper.getParkByID(id);
		}
		
		return park;
	}
	
	public Campground createFakeCampground(long parkID, String name, String openFrom, String openTo, double cost) {
		String sqlInsertCampground = "INSERT INTO campground (park_id, name, open_from_mm, open_to_mm, daily_fee) " +
									" VALUES(?, ?, ?, ?, CAST(? AS NUMERIC)) RETURNING campground_id";
		
		SqlRowSet results = jdbcTemplate.queryForRowSet(sqlInsertCampground, parkID, name, openFrom, openTo, cost);
		
		long id = Long.MIN_VALUE;
		Campground campground = null;
		
		if( results.next() ) {
			id = results.getLong("campground_id");
		}
		
		if( id > 0 ) {
			campground = objectHelper.getCampgroundByID(id);
		}
		
		return campground;
	}
	
	public Site createFakeSite(long campgroundID, long maxOccupancy, boolean accessible, int maxRVLength, boolean utilities) {
		String sqlInsertSite = "INSERT INTO site (campground_id, site_number, max_occupancy, accessible, max_rv_length, utilities) " +
						 	   "VALUES(?,(SELECT COALESCE(MAX(site_number), 0)+1 FROM site WHERE campground_id = ?),?,?,?,?) RETURNING site_id";
		
		SqlRowSet results = jdbcTemplate.queryForRowSet(sqlInsertSite, campgroundID, campgroundID, maxOccupancy, accessible, maxRVLength, utilities);
		
		long id = Long.MIN_VALUE;
		Site site = null;
		
		if( results.next() ) {
			id = results.getLong("site_id");
		}
		
		if( id > 0 ) {
			site = objectHelper.getSiteByID(id);
		}
		
		return site;
	}
}
