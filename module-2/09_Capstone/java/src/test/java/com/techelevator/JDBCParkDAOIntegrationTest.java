package com.techelevator;

import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

import org.junit.After;
import org.junit.Before;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.SingleConnectionDataSource;

import com.techelevator.models.Campground;
import com.techelevator.models.Park;
import com.techelevator.models.jdbc.JDBCParkDAO; 

public class JDBCParkDAOIntegrationTest extends DAOIntegrationTest{
	
	private JDBCParkDAO dao;
	
	private long test_Park_Id;
	
	@Before
	public void setupPark() {	
		dao = new JDBCParkDAO(dataSource);
		JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
		// creating at Park for testing.
		String sqlInsertPark = "INSERT INTO park (name, location, establish_date, area, visitors, description) "
							   + "VALUES (?, ?, ?, ?, ?, ?) RETURNING park_id";
		
		DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd");
		LocalDate dt = LocalDate.parse("1978-05-21", dtf);
		
		test_Park_Id = jdbcTemplate.queryForObject(sqlInsertPark, Long.TYPE, "Oil Creek", "PA", dt, 26000, 3500, "This park is along the banks of Oil Creek");

		dt = LocalDate.parse("1988-04-01", dtf);
		test_Park_Id = jdbcTemplate.queryForObject(sqlInsertPark, Long.TYPE, "Coal Branch Creek", "PA", dt, 21000, 3900, "This park wooden reclaimed mining area near Reading PA");
	}
	
	@Test
	public void testGetAvailableParks() {
		// Test park created above
		List<Park> testParks = dao.getAvailableParks();
		boolean test = false;
		if (testParks.size() >= 2) {
			test = true;
		}
		
		Assert.assertTrue(test);

	}	
	
	@Test
	public void testGetCampgroundsForPark() {
		// returns camp sites for Acadia
		List<Campground> testCampGrounds = dao.getCampgroundsForPark(1);
		// Acadia has the following sites:
		// Blackwoods
		// Seawall
		// Schoodic Woods
		boolean test = false;
		if (testCampGrounds.size() == 3 ) {
			test = true;
		}
		
		Assert.assertTrue(test);
		
	}
	
	
	
}
