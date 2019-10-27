package com.techelevator.models.jdbc;

import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;

import com.techelevator.models.Campground;
import com.techelevator.models.Park;
import com.techelevator.models.ParkDAO;


public class JDBCParkDAO implements ParkDAO {
	
	private JdbcTemplate jdbcTemplate;
	private JDBCObjectHelperDAO objectHelper;
	
	public JDBCParkDAO(DataSource dataSource) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		objectHelper = new JDBCObjectHelperDAO(dataSource);
	}

	@Override
	public List<Park> getAvailableParks() {
		ArrayList<Park> parks = new ArrayList<>();
		String sqlReturnAllParks = "SELECT park_id, name, location, establish_date, area, visitors, description " + 
								   "FROM park ORDER BY name ASC";
		SqlRowSet results = jdbcTemplate.queryForRowSet(sqlReturnAllParks);
		
		while (results.next()) {
			Park thePark = objectHelper.mapRowToPark(results);
			parks.add(thePark);
		}

		return parks;
	}

	@Override
	public List<Campground> getCampgroundsForPark(long parkID) {
		ArrayList<Campground> campgrounds = new ArrayList<>();
		String sqlReturnAllCampSitesForPark = "SELECT campground_id,  park_id, name, open_from_mm, open_to_mm, daily_fee "
											+ "FROM campground WHERE park_id = ? ";
		
		SqlRowSet results = jdbcTemplate.queryForRowSet(sqlReturnAllCampSitesForPark, parkID);
		
		while (results.next()) {
			Campground theCamp = objectHelper.mapRowToCampground(results);
			campgrounds.add(theCamp);
		}
		return campgrounds;
	}
}
