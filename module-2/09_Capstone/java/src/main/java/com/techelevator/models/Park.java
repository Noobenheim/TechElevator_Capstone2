package com.techelevator.models;

import java.time.LocalDate;

public class Park extends NationalParkPOJO {
	private Long parkID;
	private String name;
	private String location;
	private LocalDate establishedDate;
	private Long area;
	private Long visitors;
	private String description;
	
	public Long getParkID() {
		return parkID;
	}
	public void setParkID(Long parkID) {
		this.parkID = parkID;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getLocation() {
		return location;
	}
	public void setLocation(String location) {
		this.location = location;
	}
	public LocalDate getEstablishedDate() {
		return establishedDate;
	}
	public void setEstablishedDate(LocalDate establishedDate) {
		this.establishedDate = establishedDate;
	}
	public Long getArea() {
		return area;
	}
	public void setArea(Long area) {
		this.area = area;
	}
	public Long getVisitors() {
		return visitors;
	}
	public void setVisitors(Long visitors) {
		this.visitors = visitors;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public NationalParkPOJO getParent() {
		return null;
	}
}
