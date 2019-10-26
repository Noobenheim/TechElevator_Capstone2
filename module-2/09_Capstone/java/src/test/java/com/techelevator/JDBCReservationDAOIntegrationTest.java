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
import org.springframework.jdbc.support.rowset.SqlRowSet;

import com.techelevator.models.Park;
import com.techelevator.models.Reservation;
import com.techelevator.models.Site;
import com.techelevator.models.jdbc.JDBCParkDAO;
import com.techelevator.models.jdbc.JDBCReservationDAO;

public class JDBCReservationDAOIntegrationTest extends DAOIntegrationTest {

	private JDBCReservationDAO dao;
	JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
	
	private String beginDate = "2019-11-02"; 
	private String endDate = "2019-11-05";
	
	@Before
	public void setup() {
		dao = new JDBCReservationDAO(dataSource);
	}
	
	@Test
	public void testGetAvailableReservations() {
		
		List<Site> testRes = dao.getAvailableReservations(1, beginDate, endDate);
		
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
