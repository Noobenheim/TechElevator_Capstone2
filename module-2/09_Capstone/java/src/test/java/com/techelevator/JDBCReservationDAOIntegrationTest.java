package com.techelevator;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;

import com.techelevator.models.Campground;
import com.techelevator.models.Park;
import com.techelevator.models.Site;
import com.techelevator.models.jdbc.JDBCObjectHelperDAO;
import com.techelevator.models.jdbc.JDBCReservationDAO;

public class JDBCReservationDAOIntegrationTest extends DAOIntegrationTest {

	private JDBCReservationDAO dao;
	JdbcTemplate jdbcTemplate = new JdbcTemplate(getDataSource());
	
	private String beginDate = "2019-11-02"; 
	private String endDate = "2019-11-05";
	
	private JDBCObjectHelperDAO objectHelper = new JDBCObjectHelperDAO(getDataSource());
	
	private Park oilCreekPark;
	private Campground oilCreekCampground;
	List<Site> oilCreekSites;
	
	@Before
	public void setup() {
		dao = new JDBCReservationDAO(getDataSource());
	}
	
	@Before
	public void setupCampgrounds() {
		// creating a Park for testing.
		oilCreekPark = helper.createFakePark("Oil Creek", "PA", "1978-05-21", 26000, 2500, "This park is along the banks of Oil Creek");
		helper.createFakePark("Coal Branch Creek", "PA", "1988-04-01", 21000, 3900, "This park is woodlands, reclaimed mining area near Reading PA");
		
		// create campgrounds
		oilCreekCampground = helper.createFakeCampground(oilCreekPark.getParkID(), "Campground 1", "01", "12", 28.00);
		
		oilCreekSites = new ArrayList<>(Arrays.asList(
			helper.createFakeSite(oilCreekCampground.getCampgroundID(), 6, false, 0, false),
			helper.createFakeSite(oilCreekCampground.getCampgroundID(), 6, false, 0, false),
			helper.createFakeSite(oilCreekCampground.getCampgroundID(), 6, false, 0, false)
		));
	}
	
	@Test
	public void testGetAvailableReservations() {
		Campground campground = oilCreekCampground;
		List<Site> testRes = dao.getAvailableReservations(campground, beginDate, endDate);
		
		Assert.assertEquals(oilCreekSites.size(), testRes.size());
		
		helper.createFakeSite(oilCreekCampground.getCampgroundID(), 6, false, 0, false);
		helper.createFakeSite(oilCreekCampground.getCampgroundID(), 6, false, 0, false);
		helper.createFakeSite(oilCreekCampground.getCampgroundID(), 6, false, 0, false);
		helper.createFakeSite(oilCreekCampground.getCampgroundID(), 6, false, 0, false);
		helper.createFakeSite(oilCreekCampground.getCampgroundID(), 6, false, 0, false);
		
		testRes = dao.getAvailableReservations(campground, beginDate, endDate);
		
		boolean test = false;
		
		if( testRes.size() == 5 ) {
			test = true;
		}
		
		Assert.assertTrue("Is true if top 5 campsites returned.", test);
	}
	
	@Test
	public void testMakeReservation() {
		
		String sqlPreviousReservations = "SELECT count(*) AS resCount FROM reservation";
		SqlRowSet results = jdbcTemplate.queryForRowSet(sqlPreviousReservations);
		results.next();
		int previousReservationCount = results.getInt("resCount"); // reservation count before adding a new reservation.
		
		dao.makeReservation(1, "Bill Johnson", beginDate, endDate); // adds new reservation.
		results = jdbcTemplate.queryForRowSet(sqlPreviousReservations);
		results.next();
		
		int newReservationCount = results.getInt("resCount"); // reservation count after adding a new reservation.
		
		Assert.assertNotEquals(previousReservationCount, newReservationCount); // Is true if the reservation has been added.
		
		Assert.assertEquals(previousReservationCount + 1, newReservationCount); // Is true if previousReservationCount + 1 equals newReservationCount. 
		
	}
	
}
