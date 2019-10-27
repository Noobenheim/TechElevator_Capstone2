package com.techelevator.models;

public class Campground extends NationalParkPOJO {
	private Long campgroundID;
	private Long parkID;
	private String name;
	private Integer openFromMonth;
	private Integer openToMonth;
	private Integer fee;
	
	private Park park;
	
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
	public Park getPark() {
		return park;
	}
	public void setPark(Park park) {
		this.park = park;
	}
	public NationalParkPOJO getParent() {
		return getPark();
	}
}
