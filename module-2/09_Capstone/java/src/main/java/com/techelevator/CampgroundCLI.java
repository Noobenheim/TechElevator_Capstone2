package com.techelevator;

import java.io.IOException;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.dbcp2.BasicDataSource;
import org.xml.sax.SAXException;

import com.techelevator.exceptions.ReservationException;
import com.techelevator.lang.Strings;
import com.techelevator.models.Campground;
import com.techelevator.models.Park;
import com.techelevator.models.ParkDAO;
import com.techelevator.models.ReservationDAO;
import com.techelevator.models.Site;
import com.techelevator.models.jdbc.JDBCParkDAO;
import com.techelevator.models.jdbc.JDBCReservationDAO;
import com.techelevator.view.Menu;
import com.techelevator.view.UserInput;

public class CampgroundCLI {
	private final ParkDAO parkDAO;
	private final ReservationDAO reservationDAO;
	private Map<Integer,String> monthNames = new HashMap<>();
	
	private final static int SPACE_BETWEEN_COLUMNS = 3;
	private final static int WRAP_WIDTH = 90;

	private Menu menu;
	private UserInput input;
	
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
		
		input = new UserInput(System.in, System.out);

		monthNames.put(1,strings.get("JANUARY"));
		monthNames.put(2,strings.get("FEBRUARY"));
		monthNames.put(3,strings.get("MARCH"));
		monthNames.put(4,strings.get("APRIL"));
		monthNames.put(5,strings.get("MAY"));
		monthNames.put(6,strings.get("JUNE"));
		monthNames.put(7,strings.get("JULY"));
		monthNames.put(8,strings.get("AUGUST"));
		monthNames.put(9,strings.get("SEPTEMBER"));
		monthNames.put(10,strings.get("OCTOBER"));
		monthNames.put(11,strings.get("NOVEMBER"));
		monthNames.put(12,strings.get("DECEMBER"));
	}

	public void run() {
		showMainMenu();
	}
	
	private void showMainMenu() {
		menu.cls();
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
		
		String parkName = park.getName();
		
		if( !parkName.toLowerCase().endsWith("park") ) {
			parkName = String.format("%s %s", parkName, strings.get("NATIONAL_PARK"));
		}
		
		String output = String.format("%%s\n%s%%s\n%s%%s\n%s%%s %%s\n%s%%s\n\n%%s\n", columnFormat, columnFormat, columnFormat, columnFormat);
		output = String.format(output, parkName, 
									   strings.get("LOCATION"), park.getLocation(),
									   strings.get("ESTABLISHED"), park.getEstablishedDate().format(DateTimeFormatter.ofPattern("MM/dd/yyyy")),
									   strings.get("AREA"), NumberFormat.getIntegerInstance().format(park.getArea()), strings.get("SQKM"),
									   strings.get("ANNUAL_VISITORS"), NumberFormat.getIntegerInstance().format(park.getVisitors()),
									   wrap(park.getDescription(), WRAP_WIDTH)
		);

		System.out.println(output);
		do {
			System.out.println(strings.get("COMMAND_MESSAGE"));
			String choice = (String)menu.getChoiceFromOptions(strings.get("VIEW_CAMPGROUNDS"), strings.get("SEARCH_FOR_RESERVATION"), strings.get("RETURN_PREVIOUS_SCREEN") );
			
			if( choice.equals(strings.get("VIEW_CAMPGROUNDS")) ) {
				// title
				parkName = park.getName(); // reuse parkName variable
				if( !parkName.toLowerCase().endsWith("park") ) {
					parkName = String.format("%s %s %s", parkName, strings.get("NATIONAL_PARK"), strings.get("CAMPGROUNDS"));
				}
				System.out.println(parkName);
				System.out.println();
				
				showCampgrounds(park);
				System.out.println();
			} else if( choice.equals(strings.get("SEARCH_FOR_RESERVATION")) ) {
				showCampgroundMenu(park);
				menu.cls();
			} else {
				break;
			}
		} while(true);
	}
	
	private void showCampgrounds(Park park) {
		showCampgrounds(parkDAO.getCampgroundsForPark(park.getParkID()));
	}
	private void showCampgrounds(List<Campground> campgrounds) {
		int numberColumnWidth = (int)Math.ceil(Math.log10(campgrounds.size())) + SPACE_BETWEEN_COLUMNS + 1; // 1 for #
		int campgroundColumnWidth = strings.get("NAME").length();
		int monthWidth = 0;
		
		for( Campground c : campgrounds ) {
			if( c.getName().length() > campgroundColumnWidth ) {
				campgroundColumnWidth = c.getName().length();
			}
		}
		campgroundColumnWidth += SPACE_BETWEEN_COLUMNS;
		
		for( String s : monthNames.values() ) {
			if( s.length() > monthWidth ) {
				monthWidth = s.length();
			}
		}
		
		int openColumnWidth = max(monthWidth, strings.get("OPEN").length()) + SPACE_BETWEEN_COLUMNS;
		int closeColumnWidth = max(monthWidth, strings.get("CLOSE").length()) + SPACE_BETWEEN_COLUMNS;
		
		String format = String.format("%%-%ds%%-%ds%%-%ds%%-%ds%%s\n", numberColumnWidth, campgroundColumnWidth, openColumnWidth, closeColumnWidth);
		
		// header
		System.out.format(format, "", strings.get("NAME"), strings.get("OPEN"), strings.get("CLOSE"), strings.get("DAILY_FEE"));
		
		for( Campground c : campgrounds ) {
			String number = String.format("#%d", c.getCampgroundID());
			String name = c.getName();
			String open = monthNames.get(c.getOpenFromMonth());
			String close = monthNames.get(c.getOpenToMonth());
			
			System.out.format(format, number, name, open, close, String.format("$%.2f", c.getFee()/100.0));
		}
	}
	
	private void showCampgroundMenu(Park park) {
		List<Campground> campgrounds = parkDAO.getCampgroundsForPark(park.getParkID());
		Map<Long,Campground> campgroundIDs = new HashMap<>();
		
		showCampgrounds(campgrounds);
		
		// build a list for checking later
		for( Campground c : campgrounds ) {
			campgroundIDs.put(c.getCampgroundID(), c);
		}
		
		Long campgroundID;
		do {
			System.out.println();
			campgroundID = input.getLong(strings.get("WHICH_CAMPGROUND"));
			// verify user input
			if( campgroundID == null || !campgroundIDs.containsKey(campgroundID) && campgroundID != 0 ) {
				System.err.println(strings.get("INVALID_CAMPGROUND"));
				campgroundID = -1L;
			}
		} while(campgroundID < 0 );
		if( campgroundID == 0 ) {
			return;
		}
		boolean repeatDate;
		do {
			SimpleDateFormat arrivalDate = new SimpleDateFormat("MM/dd/yyyy");
			do {
				String date = input.getString(strings.get("WHAT_IS_ARRIVAL_DATE"));
				// validate valid date
				try {
					arrivalDate.parse(date);
					break;
				} catch( ParseException e ) {
					System.err.println(strings.get("INVALID_DATE"));
				}
			} while(true);
			SimpleDateFormat departureDate = new SimpleDateFormat("MM/dd/yyyy");
			do {
				String date = input.getString(strings.get("WHAT_IS_DEPARTURE_DATE"));
				// validate valid date
				try {
					departureDate.parse(date);
					break;
				} catch( ParseException e ) {
					System.err.println(strings.get("INVALID_DATE"));
				}
			} while(true);
			
			Boolean additionalRestrictions = input.getBoolean(strings.get("SPECIAL_REQUIREMENTS"));
			Map<String,Object> requirements = new HashMap<>();
			if( additionalRestrictions != null && additionalRestrictions ) {
				Long people = input.getLong("%s %s", strings.get("REQUIREMENT_PEOPLE"), strings.get("REQUIREMENT_SKIPPED"));
				if( people != null && people > 0 ) {
					requirements.put("people", people);
				}
				Boolean wheelchair = input.getBoolean("%s %s", strings.get("REQUIREMENT_WHEELCHAIR"), strings.get("REQUIREMENT_SKIPPED"));
				if( wheelchair != null && wheelchair) {
					requirements.put("wheelchair", wheelchair);
				}
				Integer rv = input.getInt("%s %s", strings.get("REQUIREMENT_RV_LENGTH"), strings.get("REQUIREMENT_SKIPPED"));
				if( rv != null && rv > 0 ) {
					requirements.put("rv", rv);
				}
				Boolean utility = input.getBoolean("%s %s", strings.get("REQUIREMENT_UTILITY"), strings.get("REQUIREMENT_SKIPPED"));
				if( utility != null && utility ) {
					requirements.put("utility", utility);
				}
			}
			
			repeatDate = showReservationMenu(campgroundIDs.get(campgroundID), arrivalDate.getCalendar(), departureDate.getCalendar(), requirements);
		} while( repeatDate );
	}
	
	private boolean showReservationMenu(Campground campground, Calendar arrivalDateCalendar, Calendar departureDateCalendar, Map<String,Object> requirements) {
		menu.cls();
		
		// swap the dates if departure is before arrival
		if( arrivalDateCalendar.getTimeInMillis() > departureDateCalendar.getTimeInMillis() ) {
			Calendar buffer = arrivalDateCalendar;
			arrivalDateCalendar = departureDateCalendar;
			departureDateCalendar = buffer;
		}
		
		SimpleDateFormat dateFormat = new SimpleDateFormat("YYYY-MM-dd");
		String arrivalDate = dateFormat.format(arrivalDateCalendar.getTime());
		String departureDate = dateFormat.format(departureDateCalendar.getTime());
		
		if ((arrivalDateCalendar.get(Calendar.MONTH) + 1 < campground.getOpenFromMonth()) || (departureDateCalendar.get(Calendar.MONTH) + 1 > campground.getOpenToMonth())) {
			System.out.format(strings.get("THE_PARK_IS_CLOSED_FROM"), 
					monthNames.get(campground.getOpenToMonth()),
					monthNames.get(campground.getOpenFromMonth())
					);
			System.out.println();
			return input.getBoolean(strings.get("ASK_ALTERNATIVE_DATE"));
		}
		
		// parse requirements
		Integer personCapacity = (Integer)requirements.get("people");
		Boolean needsWheelchairAccess = (Boolean)requirements.get("wheelchair");
		Integer rvLengthRequired = (Integer)requirements.get("rv");
		Boolean needsUtility = (Boolean)requirements.get("utility");
		
		List<Site> sites = reservationDAO.getAvailableReservations(campground.getCampgroundID(), arrivalDate, departureDate, personCapacity, needsWheelchairAccess, rvLengthRequired, needsUtility);
		
		if( sites.size() == 0 ) {
			dateFormat = new SimpleDateFormat("EEEE LLLL d, yyyy");
			System.out.format(strings.get("NO_RESERVATIONS_AVAILABLE"),
							  dateFormat.format(arrivalDateCalendar.getTime()),
							  dateFormat.format(departureDateCalendar.getTime())
						     );
			System.out.println();
			return input.getBoolean(strings.get("ASK_ALTERNATIVE_DATE"));
		}
		
		// cache sites
		Map<Long,Site> siteMap = new HashMap<>();
		for( Site s : sites ) {
			siteMap.put(s.getSiteNumber(), s);
		}
		
		String format = "%%-%ds%%-%ds%%-%ds%%-%ds%%-%ds%%s\n";
		
		int siteNoColumnWidth = strings.get("SITE_NO").length() + SPACE_BETWEEN_COLUMNS;
		int maxOccupColumnWidth = strings.get("MAX_OCCUP").length() + SPACE_BETWEEN_COLUMNS;
		int accessibleColumnWidth = strings.get("ACCESSIBLE").length() + SPACE_BETWEEN_COLUMNS;
		int maxRVLengthColumnWidth = strings.get("MAX_RV_LENGTH").length() + SPACE_BETWEEN_COLUMNS;
		int utilityColumnWidth = strings.get("UTILITY").length() + SPACE_BETWEEN_COLUMNS;
		
		format = String.format(format, siteNoColumnWidth, maxOccupColumnWidth, accessibleColumnWidth, maxRVLengthColumnWidth, utilityColumnWidth);
		
		long totalDays = ChronoUnit.DAYS.between(arrivalDateCalendar.toInstant(), departureDateCalendar.toInstant());
		
		// header
		System.out.format(format, strings.get("SITE_NO"), strings.get("MAX_OCCUP"), strings.get("ACCESSIBLE"), strings.get("MAX_RV_LENGTH"), strings.get("UTILITY"), strings.get("COST"));
		
		for( Site site : sites ) {
			String siteNumber = Long.toString(site.getSiteNumber());
			String maxOccup = Long.toString(site.getMaxOccupancy());
			String accessible = site.getAccessible()?strings.get("YES"):strings.get("NO");
			String maxRVLength = site.getMaxRVLength()==0?strings.get("NA"):Integer.toString(site.getMaxRVLength());
			String utility = site.getUtilities()?strings.get("YES"):strings.get("NA");
			String cost = String.format("$%.2f", campground.getFee()/100.0*totalDays);
			
			System.out.format(format, siteNumber, maxOccup, accessible, maxRVLength, utility, cost);
		}
		
		long siteNumber;
		System.out.println();
		do {
			siteNumber = input.getLong(strings.get("WHICH_SITE_RESERVED"));
			if( !siteMap.containsKey(siteNumber) && siteNumber != 0 ) {
				System.err.println(strings.get("INVALID_SITE"));
				siteNumber = -1;
			}
		} while( siteNumber < 0 );
		if( siteNumber == 0 ) {
			return false;
		}
		String reservationName;
		do {
			reservationName = input.getString(strings.get("RESERVATION_NAME"));
			if( reservationName.trim().isEmpty() ) {
				System.err.println(strings.get("INVALID_RESERVATION_NAME"));
			}
		} while( reservationName.trim().isEmpty() );
		
		menu.cls();
		long confirmationID;
		try {
			confirmationID = reservationDAO.makeReservation(siteMap.get(siteNumber).getSiteID(), reservationName, arrivalDate, departureDate);
			
			System.out.format(strings.get("CONFIRMATION_NUMBER"), confirmationID);
			System.out.println("\n\n");
		} catch( ReservationException e ) {
			System.out.format(strings.get(e.getID()), e.getObjects());
			System.out.println();
		}
		
		return false;
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
				lineString = splitString[i] + " ";
			}
		}
		
		resultString.append(lineString);

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
