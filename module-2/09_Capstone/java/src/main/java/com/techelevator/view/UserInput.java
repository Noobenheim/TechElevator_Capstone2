package com.techelevator.view;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.Scanner;

public class UserInput {
	Scanner in;
	PrintWriter out;
	
	public UserInput(InputStream in, OutputStream out) {
		this.in = new Scanner(in);
		this.out = new PrintWriter(out);
	}
	
	private String displayAndGet(String message) {
		out.print(message);
		out.print(" ");
		out.flush();
		return in.nextLine();
	}
	
	public int getInt(String message) {
		String line = displayAndGet(message);
		int number = Integer.MIN_VALUE;
		
		try {
			number = Integer.parseInt(line);
		} catch( NumberFormatException e ) {
		}
		
		return number;
	}
	public long getLong(String message) {
		String line = displayAndGet(message);
		long number = Long.MIN_VALUE;
		
		try {
			number = Long.parseLong(line);
		} catch( NumberFormatException e ) {
		}
		
		return number;
	}
	public String getString(String message) {
		return displayAndGet(message);
	}
	public boolean getBoolean(String message) {
		String line = displayAndGet(message).toLowerCase();
		
		return line.equals("1") || line.equals("true") || line.equals("yes") || line.equals("y");
	}
}
