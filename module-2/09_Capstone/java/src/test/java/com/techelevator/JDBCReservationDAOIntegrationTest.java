package com.techelevator;

import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;

import com.techelevator.models.Campground;
import com.techelevator.models.Site;
import com.techelevator.models.jdbc.JDBCObjectHelperDAO;
import com.techelevator.models.jdbc.JDBCReservationDAO;

public class JDBCReservationDAOIntegrationTest extends DAOIntegrationTest {

	private JDBCReservationDAO dao;
	JdbcTemplate jdbcTemplate = new JdbcTemplate(getDataSource());
	
	private String beginDate = "2019-11-02"; 
	private String endDate = "2019-11-05";
	
	private JDBCObjectHelperDAO objectHelper = new JDBCObjectHelperDAO(getDataSource());
	
	@Before
	public void setup() {
		dao = new JDBCReservationDAO(getDataSource());
	}
	
	@Test
	public void testGetAvailableReservations() {
		Campground campground = objectHelper.getCampgroundByID(1);
		List<Site> testRes = dao.getAvailableReservations(campground, beginDate, endDate);
		
		boolean test = false;
		
		if (testRes.size() == 5) { // if true we've returned the top 5 camp sites
			test = true;
		}
		
		Assert.assertTrue("Is true if top 5 camp sites returned.", test);
		
	}
	
	@Test
	public void testMakeReservation() {
		
		String sqlPreviousReservations = "SELECT count(*) AS resCount FROM reservation";
		SqlRowSet results = jdbcTemplate.queryForRowSet(sqlPreviousReservations);
		results.next();
		int previousReservationCount = results.getInt("resCount"); // reservation count before adding a new reservation.
		
		Long newReservationID = dao.makeReservation(1, "Bill Johnson", beginDate, endDate); // adds new reservation.
		results = jdbcTemplate.queryForRowSet(sqlPreviousReservations);
		results.next();
		
		int newReservationCount = results.getInt("resCount"); // reservation count after adding a new reservation.
		
		Assert.assertNotEquals(previousReservationCount, newReservationCount); // Is true if the reservation has been added.
		
		Assert.assertEquals(previousReservationCount + 1, newReservationCount); // Is true if previousReservationCount + 1 equals newReservationCount. 
		
	}
	
}
