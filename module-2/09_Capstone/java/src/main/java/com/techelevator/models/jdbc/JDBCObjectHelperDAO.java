package com.techelevator.models.jdbc;

import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;

import com.techelevator.models.Campground;
import com.techelevator.models.NationalParkPOJO;
import com.techelevator.models.ObjectHelperDAO;
import com.techelevator.models.Park;
import com.techelevator.models.Reservation;
import com.techelevator.models.Site;

public class JDBCObjectHelperDAO implements ObjectHelperDAO {

	private JdbcTemplate jdbcTemplate;
	private List<Class<?>> validClasses = new ArrayList<Class<?>>();
	
	public JDBCObjectHelperDAO(DataSource dataSource) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		
		validClasses.add(Park.class);
		validClasses.add(Campground.class);
		validClasses.add(Site.class);
		validClasses.add(Reservation.class);
	}
	
	@Override
	public Park mapRowToPark(SqlRowSet results) {
		Park park;
		park = new Park();
		park.setParkID(results.getLong("park_id"));
		park.setName(results.getString("name"));
		park.setLocation(results.getString("location"));
		park.setEstablishedDate(results.getDate("establish_date").toLocalDate());		
		park.setArea(results.getLong("area"));
		park.setVisitors(results.getLong("visitors"));
		park.setDescription(results.getString("description"));
		
		return park;
	}

	@Override
	public Campground mapRowToCampground(SqlRowSet results) {
		Campground campground;
		
		campground = new Campground();
		campground.setCampgroundID(results.getLong("campground_id"));
		campground.setParkID(results.getLong("park_id"));
		campground.setName(results.getString("name"));
		campground.setOpenFromMonth(results.getInt("open_from_mm"));
		campground.setOpenToMonth(results.getInt("open_to_mm"));
		campground.setFee((int)(results.getDouble("daily_fee")*100.0));
	
		return campground;
	}

	@Override
	public Site mapRowToSite(SqlRowSet results) {
		Site site = new Site();
		
		site.setSiteID(results.getLong("site_id"));
		site.setCampgroundID(results.getLong("campground_id"));
		site.setSiteNumber(results.getLong("site_number"));
		site.setMaxOccupancy(results.getLong("max_occupancy"));
		site.setAccessible(results.getBoolean("accessible"));
		site.setMaxRVLength(results.getInt("max_rv_length"));
		site.setUtilities(results.getBoolean("utilities"));
		
		return site;
	}

	@Override
	public Reservation mapRowToReservation(SqlRowSet results) {
		Reservation reservation = new Reservation();
		
		reservation.setReservationID(results.getLong("reservation_id"));
		reservation.setSiteID(results.getLong("site_id"));
		reservation.setName(results.getString("name"));
		reservation.setFromDate(results.getDate("from_date").toLocalDate());
		reservation.setToDate(results.getDate("to_date").toLocalDate());
		reservation.setCreateDate(results.getDate("create_date").toLocalDate());
		
		return reservation;
	}

	@Override
	public void mapSiteToReservation(Reservation reservation) {
		String sqlSite = "SELECT site_id, campground_id, site_number, max_occupancy, accessible, max_rv_length, utilities " +
						 "FROM site WHERE site_id = ?";
		SqlRowSet results = jdbcTemplate.queryForRowSet(sqlSite, reservation.getSiteID());
		
		if( results.next() ) {
			reservation.setSite(mapRowToSite(results));
		}
	}

	@Override
	public void mapCampgroundToSite(Site site) {
		String sqlCampground = "SELECT campground_id, park_id, name, open_from_mm, open_to_mm, daily_fee " +
							   "FROM campground WHERE campground_id = ?";
		SqlRowSet results = jdbcTemplate.queryForRowSet(sqlCampground, site.getCampgroundID());
		
		if( results.next() ) {
			site.setCampground(mapRowToCampground(results));
		}
	}

	@Override
	public void mapParkToCampground(Campground campground) {
		String sqlPark = "SELECT park_id, name, location, establish_date, area, visitors, description " +
						 "FROM park WHERE park_id = ?";
		SqlRowSet results = jdbcTemplate.queryForRowSet(sqlPark, campground.getParkID());
		
		if( results.next() ) {
			campground.setPark(mapRowToPark(results));
		}
	}

	@Override
	public void ensureClassExists(NationalParkPOJO npo, Class<?> c) {
		if( !validClasses.contains(c) ) {
			return;
		}
		for( int i=validClasses.size()-1; i>0; i-- ) {
			if( validClasses.get(i) == c ) {
				return; // c is lower than npo
			}
			if( validClasses.get(i).isInstance(npo) ) {
				do {
					Class<?> oneHigher = validClasses.get(--i);
					fillMap(npo, oneHigher);
					npo = npo.getParent();
					if( oneHigher == c ) {
						break;
					}
				} while( i > 0 );
				return;
			}
		}
	}
	
	// one level check
	private void fillMap(NationalParkPOJO npo, Class<?> c) {
		if( c == Site.class && npo instanceof Reservation ) {
			Reservation r = (Reservation)npo;
			if( r.getSite() == null ) {
				mapSiteToReservation(r);
			}
		} else if( c == Campground.class && npo instanceof Site ) {
			Site s = (Site)npo;
			if( s.getCampground() == null ) {
				mapCampgroundToSite(s);
			}
		} else if( c == Park.class && npo instanceof Campground ) {
			Campground camp = (Campground)npo;
			if( camp.getPark() == null ) {
				mapParkToCampground(camp);
			}
		}
	}
}
