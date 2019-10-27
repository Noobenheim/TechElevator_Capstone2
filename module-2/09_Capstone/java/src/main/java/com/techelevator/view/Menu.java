package com.techelevator.view;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.Scanner;

public class Menu {

	private PrintWriter out;
	private Scanner in;

	private String invalidOptionMessage = "%s";
	private String choiceOptionMessage = "";
	
	private String header = null;
	
	public Menu(InputStream input, OutputStream output) {
		this.out = new PrintWriter(output);
		this.in = new Scanner(input);
	}

	public Object getChoiceFromOptions(Object...options) {
		Object choice = null;
		while(choice == null) {
			displayMenuOptions(options);
			choice = getChoiceFromUserInput(options);
		}
		return choice;
	}

	private Object getChoiceFromUserInput(Object[] options) {
		Object choice = null;
		String userInput = in.nextLine();
		cls();
		try {
			int selectedOption = Integer.valueOf(userInput);
			if(selectedOption <= options.length) {
				choice = options[selectedOption - 1];
			}
		} catch(NumberFormatException e) {
			// eat the exception, an error message will be displayed below since choice will be null
		}
		if(choice == null) {
			String invalid = String.format(invalidOptionMessage, userInput);
			out.format("\n*** %s  ***\n\n", invalid);
			out.flush();
		}
		return choice;
	}
	
	public String getAnyChoiceFromOptionsWithQuit(String quitMessage, Object...options) {
		displayMenuOptions(quitMessage, options);
		out.flush();
		String userInput = in.nextLine();
		cls();
		return userInput;
	}
	
	public void setChoiceOptionMessage(String message) {
		this.choiceOptionMessage = message;
	}
	public void setInvalidOptionMessage(String message) {
		this.invalidOptionMessage = message;
	}

	private void displayMenuOptions(Object[] options) {
		displayMenuOptions(null, options);
	}
	private void displayMenuOptions(String quitMessage, Object[] options) {
		for(int i = 0; i < options.length; i++) {
			int optionNum = i+1;
			out.format("   %d) %s\n", optionNum, options[i]);
		}
		if( quitMessage != null && quitMessage.length() > 0 ) {
			out.format("   %s) %s\n", quitMessage.substring(0,1).toUpperCase(), quitMessage);
		}
		out.format("\n%s >>> \n", choiceOptionMessage);
		out.flush();
	}
	
	public void setHeader(String header) {
		this.header = header;
	}
	public void clearHeader() {
		this.header = null;
	}
	public String getHeader() {
		return this.header;
	}
	
	public void cls() {
		for( int i=0; i<100; i++ ) {
			out.println();
		}
		if( this.header != null ) {
			out.println(this.header);
		}
		out.flush();
	}
}
