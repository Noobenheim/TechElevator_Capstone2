package com.techelevator;

import java.io.IOException;
import java.text.NumberFormat;
import java.util.List;

import javax.sql.DataSource;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.dbcp2.BasicDataSource;
import org.xml.sax.SAXException;

import com.techelevator.lang.Strings;
import com.techelevator.models.Park;
import com.techelevator.models.ParkDAO;
import com.techelevator.models.ReservationDAO;
import com.techelevator.models.jdbc.JDBCParkDAO;
import com.techelevator.models.jdbc.JDBCReservationDAO;
import com.techelevator.view.Menu;

public class CampgroundCLI {
	private final ParkDAO parkDAO;
	private final ReservationDAO reservationDAO;

	private Menu menu;
	
	private Strings strings;

	public static void main(String[] args) {
		BasicDataSource dataSource = new BasicDataSource();
		dataSource.setUrl("jdbc:postgresql://localhost:5432/campground");
		dataSource.setUsername("postgres");
		dataSource.setPassword("postgres1");

		CampgroundCLI application = new CampgroundCLI(dataSource, "English");
		application.run();
	}

	public CampgroundCLI(DataSource datasource, String language) {
		parkDAO = new JDBCParkDAO(datasource);
		reservationDAO = new JDBCReservationDAO(datasource);
		
		try {
			strings = new Strings(language);
		} catch (ParserConfigurationException | SAXException | IOException e) {
			e.printStackTrace();
			System.exit(0);
		}

		menu = new Menu(System.in, System.out);
		menu.setChoiceOptionMessage(strings.get("SELECT_OPTION"));
		menu.setInvalidOptionMessage(strings.get("INVALID_OPTION"));
	}

	public void run() {
		showMainMenu();
	}
	
	private void showMainMenu() {
		System.out.println(strings.get("MAIN_MENU_MESSAGE"));
		List<Park> parks = parkDAO.getAvailableParks();
		String choice;
		do {
			String[] parkNames = new String[parks.size()];
			for( int i=0; i<parks.size(); i++ ) {
				parkNames[i] = parks.get(i).getName();
			}
			choice = menu.getAnyChoiceFromOptionsWithQuit(strings.get("QUIT"), (Object[])parkNames);
			
			int parkNumber = -1;
			try {
				parkNumber = Integer.parseInt(choice);
				parkNumber--;
			} catch(NumberFormatException e) {
				// do nothing to loop back
			}
			if( parkNumber >= 0 && parkNumber < parks.size() ) {
				showParkMenu(parks.get(parkNumber));
			} else {
				System.out.println("");
			}
		} while( !choice.equalsIgnoreCase("q") );
	}
	
	private void showParkMenu(Park park) {
		int padding = max(strings.get("LOCATION").length(),
				          strings.get("ESTABLISHED").length(), 
				          strings.get("AREA").length(), 
				          strings.get("ANNUAL_VISITORS").length()) + 2; // 2 for ': '
		String columnFormat = String.format("%%-%ds: ", padding);
		
		String output = String.format("%%s\n%s%%s\n%s%%s\n%s%%s %%s\n%s%%s\n\n%%s", columnFormat, columnFormat, columnFormat, columnFormat);
		output = String.format(output, park.getName(), 
									   strings.get("LOCATION"), park.getLocation(),
									   strings.get("ESTABLISHED"), park.getEstablishedDate().toString(),
									   strings.get("AREA"), NumberFormat.getIntegerInstance().format(park.getArea()), strings.get("SQKM"),
									   strings.get("ANNUAL_VISITORS"), NumberFormat.getIntegerInstance().format(park.getVisitors()),
									   wrap(park.getDescription(), 80)
		);
		
		do {
			System.out.print(output);
			String choice = (String)menu.getChoiceFromOptions(strings.get("VIEW_CAMPGROUNDS"), strings.get("SEARCH_FOR_RESERVATION"), strings.get("RETURN_PREVIOUS_SCREEN") );
			
			if( choice.equals(strings.get("VIEW_CAMPGROUNDS")) ) {
				showCampgroundMenu(park);
			} else if( choice.equals(strings.get("SEARCH_FOR_RESERVATION")) ) {
				showReservationMenu(park);
			} else {
				break;
			}
		} while(true);
	}
	
	private void showCampgroundMenu(Park park) {
		
	}
	
	private void showReservationMenu(Park park) {
		
	}
	
	private String wrap(String longString, int width) {
		String[] splitString = longString.split(" ");
		StringBuilder resultString = new StringBuilder();
		String lineString = "";
		
		for( int i=0; i<splitString.length; i++ ) {
			if( lineString.isEmpty() ) {
				lineString += splitString[i]+" ";
			} else if( lineString.length() + splitString[i].length() < width ) {
				lineString += splitString[i]+" ";
			} else {
				resultString.append(lineString.trim());
				resultString.append("\n");
				lineString = "";
			}
		}

	    return resultString.toString();
	}
	
	private int max(int...numbers) {
		int ret = Integer.MIN_VALUE;
		for( int i=0; i<numbers.length; i++ ) {
			if( numbers[i] > ret )
				ret = numbers[i];
		}
		return ret;
	}
}
