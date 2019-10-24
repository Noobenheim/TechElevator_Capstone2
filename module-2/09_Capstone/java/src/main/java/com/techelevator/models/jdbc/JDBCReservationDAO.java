package com.techelevator.models.jdbc;

import java.util.List;

import javax.sql.DataSource;

import org.springframework.jdbc.core.JdbcTemplate;

import com.techelevator.models.ReservationDAO;
import com.techelevator.models.Site;

public class JDBCReservationDAO implements ReservationDAO {

	private JdbcTemplate jdbcTemplate;
	
	public JDBCReservationDAO(DataSource dataSource) {
		jdbcTemplate = new JdbcTemplate(dataSource);
	}
	
	@Override
	public List<Site> getAvailableReservations(long campgroundID, String startDate, String endDate) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public long makeReservation(long siteID, String name, String startDate, String endDate) {
		// TODO Auto-generated method stub
		return 0;
	}

}
