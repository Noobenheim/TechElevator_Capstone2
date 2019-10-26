package com.techelevator.view;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.Scanner;

public class UserInput {
	private Scanner in;
	private PrintWriter out;
	
	public UserInput(InputStream in, OutputStream out) {
		this.in = new Scanner(in);
		this.out = new PrintWriter(out);
	}
	
	private String displayAndGet(String message, Object...options) {
		out.format(message, options);
		out.print(" ");
		out.flush();
		return in.nextLine();
	}
	
	public Integer getInt(String message, Object...options) {
		String line = displayAndGet(message, options);
		int number;
		
		try {
			number = Integer.parseInt(line);
		} catch( NumberFormatException e ) {
			return null;
		}
		
		return number;
	}
	public Long getLong(String message, Object...options) {
		String line = displayAndGet(message, options);
		long number;
		
		try {
			number = Long.parseLong(line);
		} catch( NumberFormatException e ) {
			return null;
		}
		
		return number;
	}
	public String getString(String message, Object...options) {
		return displayAndGet(message, options);
	}
	public Boolean getBoolean(String message, Object...options) {
		String line = displayAndGet(message, options).toLowerCase();
		
		if( line.trim().isEmpty() ) {
			return null;
		}
		
		return line.equals("1") || line.equals("true") || line.equals("yes") || line.equals("y");
	}
}
