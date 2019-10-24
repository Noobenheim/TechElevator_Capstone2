package com.techelevator.models.jdbc;

import java.util.List;

import javax.sql.DataSource;

import org.springframework.jdbc.core.JdbcTemplate;

import com.techelevator.models.Campground;
import com.techelevator.models.Park;
import com.techelevator.models.ParkDAO;

public class JDBCParkDAO implements ParkDAO {
	
	private JdbcTemplate jdbcTemplate;
	
	public JDBCParkDAO(DataSource dataSource) {
		jdbcTemplate = new JdbcTemplate(dataSource);
	}

	@Override
	public List<Park> getAvailablePerks() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Campground> getCampgroundsForPark(long parkID) {
		// TODO Auto-generated method stub
		return null;
	}

}
