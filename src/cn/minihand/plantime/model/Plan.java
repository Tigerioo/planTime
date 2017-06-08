package cn.minihand.plantime.model;

public class Plan {
	
	private int plan_id;
	
	private String planName;
	
	private String planType;
	
	private int planTime;	//正常计划时间
	
	private int overPlanTime;	//加班计划时间
	
	private int weekendPlanTime;	//周末计划时间

	public int getPlan_id() {
		return plan_id;
	}

	public void setPlan_id(int plan_id) {
		this.plan_id = plan_id;
	}

	public String getPlanName() {
		return planName;
	}

	public void setPlanName(String planName) {
		this.planName = planName;
	}

	public String getPlanType() {
		return planType;
	}

	public void setPlanType(String planType) {
		this.planType = planType;
	}

	public int getPlanTime() {
		return planTime;
	}

	public void setPlanTime(int planTime) {
		this.planTime = planTime;
	}

	public int getOverPlanTime() {
		return overPlanTime;
	}

	public void setOverPlanTime(int overPlanTime) {
		this.overPlanTime = overPlanTime;
	}

	public int getWeekendPlanTime() {
		return weekendPlanTime;
	}

	public void setWeekendPlanTime(int weekendPlanTime) {
		this.weekendPlanTime = weekendPlanTime;
	}
}
