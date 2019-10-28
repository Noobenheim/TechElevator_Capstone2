package com.techelevator;

import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
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

public class JDBCParkDAOIntegrationTest extends DAOIntegrationTest {

	private JDBCParkDAO dao;
	
	private Park oilCreekPark;
	private List<Campground> oilCreekCampgrounds;

	@Before
	public void setupPark() {
		dao = new JDBCParkDAO(getDataSource());

		// creating a Park for testing.
		oilCreekPark = helper.createFakePark("Oil Creek", "PA", "1978-05-21", 26000, 2500, "This park is along the banks of Oil Creek");
		helper.createFakePark("Coal Branch Creek", "PA", "1988-04-01", 21000, 3900, "This park is woodlands, reclaimed mining area near Reading PA");
		
		// create campgrounds
		oilCreekCampgrounds = new ArrayList<>(Arrays.asList(
			helper.createFakeCampground(oilCreekPark.getParkID(), "Campground 1", "01", "05", 28.00),
			helper.createFakeCampground(oilCreekPark.getParkID(), "Campground 2", "01", "12", 30.00),
			helper.createFakeCampground(oilCreekPark.getParkID(), "Campground 3", "05", "12", 25.00)
		));
	}

	@Test
	public void testGetAvailableParks() {
		// Test park created above
		List<Park> originalParks = dao.getAvailableParks();
		
		helper.createFakePark("Yellowstone", "Wyoming", "1872-03-01", 2219791, 4115000, "Yellowstone was the first national park in the U.S. and is also widely held to be the first national park in the world.");
		
		List<Park> newParks = dao.getAvailableParks();

		Assert.assertEquals(originalParks.size()+1, newParks.size());
	}

	@Test
	public void testGetCampgroundsForPark() {
		List<Campground> testCampGrounds = dao.getCampgroundsForPark(oilCreekPark.getParkID());
		boolean test = false;
		if (testCampGrounds.size() == oilCreekCampgrounds.size()) {
			test = true;
		}

		Assert.assertTrue(test);

	}

}
