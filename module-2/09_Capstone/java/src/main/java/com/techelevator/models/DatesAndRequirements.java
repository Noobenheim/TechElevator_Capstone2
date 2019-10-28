package com.techelevator.models;

import java.util.Calendar;

public class DatesAndRequirements {
	private Calendar fromDate;
	private Calendar toDate;
	private Long people;
	private Boolean wheelchair;
	private Integer rv;
	private Boolean utility;
	public Calendar getFromDate() {
		return fromDate;
	}
	public void setFromDate(Calendar fromDate) {
		this.fromDate = fromDate;
	}
	public Calendar getToDate() {
		return toDate;
	}
	public void setToDate(Calendar toDate) {
		this.toDate = toDate;
	}
	public Long getPeople() {
		return people;
	}
	public void setPeople(Long people) {
		this.people = people;
	}
	public Boolean getWheelchair() {
		return wheelchair;
	}
	public void setWheelchair(Boolean wheelchair) {
		this.wheelchair = wheelchair;
	}
	public Integer getRv() {
		return rv;
	}
	public void setRv(Integer rv) {
		this.rv = rv;
	}
	public Boolean getUtility() {
		return utility;
	}
	public void setUtility(Boolean utility) {
		this.utility = utility;
	}
}
