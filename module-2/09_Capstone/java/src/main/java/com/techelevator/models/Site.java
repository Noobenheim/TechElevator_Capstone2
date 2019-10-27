package com.techelevator.models;

public class Site extends NationalParkObject {
	private Long siteID;
	private Long campgroundID;
	private Long siteNumber;
	private Long maxOccupancy;
	private Boolean accessible;
	private Integer maxRVLength;
	private Boolean utilities;
	
	private Campground campground;
	
	public Long getSiteID() {
		return siteID;
	}
	public void setSiteID(Long siteID) {
		this.siteID = siteID;
	}
	public Long getCampgroundID() {
		return campgroundID;
	}
	public void setCampgroundID(Long campgroundID) {
		this.campgroundID = campgroundID;
	}
	public Long getSiteNumber() {
		return siteNumber;
	}
	public void setSiteNumber(Long siteNumber) {
		this.siteNumber = siteNumber;
	}
	public Long getMaxOccupancy() {
		return maxOccupancy;
	}
	public void setMaxOccupancy(Long maxOccupancy) {
		this.maxOccupancy = maxOccupancy;
	}
	public Boolean getAccessible() {
		return accessible;
	}
	public void setAccessible(Boolean accessible) {
		this.accessible = accessible;
	}
	public Integer getMaxRVLength() {
		return maxRVLength;
	}
	public void setMaxRVLength(Integer maxRVLength) {
		this.maxRVLength = maxRVLength;
	}
	public Boolean getUtilities() {
		return utilities;
	}
	public void setUtilities(Boolean utilities) {
		this.utilities = utilities;
	}
	public Campground getCampground() {
		return campground;
	}
	public void setCampground(Campground campground) {
		this.campground = campground;
	}
	public NationalParkObject getParent() {
		return getCampground();
	}
}
