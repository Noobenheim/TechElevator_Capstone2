package com.techelevator.models.jdbc;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
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
	
	public JDBCParkDAO(DataSource dataSource) {
		jdbcTemplate = new JdbcTemplate(dataSource);
	}

	@Override
	public List<Park> getAvailableParks() {
		ArrayList<Park> parks = new ArrayList<>();
		String sqlReturnAllParks = "SELECT park_id, name, location, establish_date, area, visitors, description " + "FROM parks ";
		SqlRowSet results = jdbcTemplate.queryForRowSet(sqlReturnAllParks);
		
		while (results.next()) {
			Park thePark = mapRowToPark(results);
			parks.add(thePark);
		}

		return parks;
	}

	@Override
	public List<Campground> getCampgroundsForPark(long parkID) {
		ArrayList<Campground> campgrounds = new ArrayList<>();
		String sqlReturnAllCampSitesForPark = "SELECT campground_id,  park_id, name, open_from_mm, open_to_mm, daily_fee "
											+ "FROM campground WHERE park_id = ? ";
		
		SqlRowSet results = jdbcTemplate.queryForRowSet(sqlReturnAllCampSitesForPark);
		
		while (results.next()) {
			Campground theCamp = mapRowToCamp(results);
			campgrounds.add(theCamp);
		}
		return campgrounds;
	}
	
	private Park mapRowToPark(SqlRowSet results) {
		Park thePark;
		thePark = new Park();
		thePark.setParkID(results.getLong("park_id"));
		thePark.setName(results.getString("name"));
		thePark.setLocation("location");

		String tempDateString = results.getString("to_date");
		if (tempDateString != null) {
			LocalDate tempDate = LocalDate.parse(tempDateString, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
			thePark.setEstablishedDate(tempDate);
		}
		
		thePark.setArea(results.getLong("area"));
		thePark.setVisitors(results.getLong("visitors"));
		thePark.setDescription(results.getString("description"));
		
		return thePark;
	
	}
	
	private Campground mapRowToCamp(SqlRowSet results) {
		Campground theCamp;
		theCamp = new Campground();
		theCamp.setCampgroundID(results.getLong("campground_id"));
		theCamp.setParkID(results.getLong("park_id"));
		theCamp.setName(results.getString("name"));
		theCamp.setOpenFromMonth(results.getInt("open_from_mm"));
		theCamp.setOpenToMonth(results.getInt("open_to_mm"));
		theCamp.setFee(results.getInt("daily_fee"));
		
		return theCamp;
	}

}
