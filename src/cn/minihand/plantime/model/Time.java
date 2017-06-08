package cn.minihand.plantime.model;

import java.util.Date;

public class Time {

	private int time_id;
	private int plan_id;
	private int dayCompleteTime; // 当天完成的情况：单位：秒
	private int weekCompleteTime;
	private int monthCompleteTime;
	private int yearCompleteTime;
	private int totalCompleteTime;
	private Date lastUpdateTime;

	public Date getLastUpdateTime() {
		return lastUpdateTime;
	}

	public void setLastUpdateTime(Date lastUpdateTime) {
		this.lastUpdateTime = lastUpdateTime;
	}

	public int getTime_id() {
		return time_id;
	}

	public void setTime_id(int time_id) {
		this.time_id = time_id;
	}

	public int getPlan_id() {
		return plan_id;
	}

	public void setPlan_id(int plan_id) {
		this.plan_id = plan_id;
	}

	public int getDayCompleteTime() {
		return dayCompleteTime;
	}

	public void setDayCompleteTime(int dayCompleteTime) {
		this.dayCompleteTime = dayCompleteTime;
	}

	public int getWeekCompleteTime() {
		return weekCompleteTime;
	}

	public void setWeekCompleteTime(int weekCompleteTime) {
		this.weekCompleteTime = weekCompleteTime;
	}

	public int getMonthCompleteTime() {
		return monthCompleteTime;
	}

	public void setMonthCompleteTime(int monthCompleteTime) {
		this.monthCompleteTime = monthCompleteTime;
	}

	public int getYearCompleteTime() {
		return yearCompleteTime;
	}

	public void setYearCompleteTime(int yearCompleteTime) {
		this.yearCompleteTime = yearCompleteTime;
	}

	public int getTotalCompleteTime() {
		return totalCompleteTime;
	}

	public void setTotalCompleteTime(int totalCompleteTime) {
		this.totalCompleteTime = totalCompleteTime;
	}
}
