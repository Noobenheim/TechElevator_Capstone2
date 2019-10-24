package com.techelevator.models;

public class Campground {
	private Long campgroundID;
	private Long parkID;
	private String name;
	private Integer openFromMonth;
	private Integer openToMonth;
	private Integer fee;
	
	public Long getCampgroundID() {
		return campgroundID;
	}
	public void setCampgroundID(Long campgroundID) {
		this.campgroundID = campgroundID;
	}
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
	public Integer getOpenFromMonth() {
		return openFromMonth;
	}
	public void setOpenFromMonth(Integer openFromMonth) {
		this.openFromMonth = openFromMonth;
	}
	public Integer getOpenToMonth() {
		return openToMonth;
	}
	public void setOpenToMonth(Integer openToMonth) {
		this.openToMonth = openToMonth;
	}
	public Integer getFee() {
		return fee;
	}
	public void setFee(Integer fee) {
		this.fee = fee;
	}
}
