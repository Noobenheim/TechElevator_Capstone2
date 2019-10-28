package com.techelevator.models;

import java.time.LocalDate;

public class Reservation extends NationalParkObject {
	private Long reservationID;
	private Long siteID;
	private String name;
	private LocalDate fromDate;
	private LocalDate toDate;
	private LocalDate createDate;
	
	private Site site;
	
	public Long getReservationID() {
		return reservationID;
	}
	public void setReservationID(Long reservationID) {
		this.reservationID = reservationID;
	}
	public Long getSiteID() {
		return siteID;
	}
	public void setSiteID(Long siteID) {
		this.siteID = siteID;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public LocalDate getFromDate() {
		return fromDate;
	}
	public void setFromDate(LocalDate fromDate) {
		this.fromDate = fromDate;
	}
	public LocalDate getToDate() {
		return toDate;
	}
	public void setToDate(LocalDate toDate) {
		this.toDate = toDate;
	}
	public LocalDate getCreateDate() {
		return createDate;
	}
	public void setCreateDate(LocalDate createDate) {
		this.createDate = createDate;
	}
	public Site getSite() {
		return this.site;
	}
	public void setSite(Site site) {
		this.site = site;
	}
	public NationalParkObject getParent() {
		return getSite();
	}
}
