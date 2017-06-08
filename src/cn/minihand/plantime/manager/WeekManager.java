package cn.minihand.plantime.manager;

import cn.minihand.plantime.model.Week;

public interface WeekManager {

	public void validateWeek(int plan_id);

	public String getWeekTime(String currentTime);	//根据当前日期来取得本周日期范围
	
	public void addWeek(Week week);
	
	public void updateWeek(int plan_id);
	
	public Week findWeek(int plan_id);
	
	public void deleteAllPlans(); //删除所有数据
}
