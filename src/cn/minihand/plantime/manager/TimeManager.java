package cn.minihand.plantime.manager;

import cn.minihand.plantime.model.Time;

public interface TimeManager {

	public Time findTime(int plan_id); //根据idName,idValue来查找Time对象
	
	public void updateTime(Time time, int updateTime);
	
	public void updateTime(Time time);
	
	public void validatePlanTime(int plan_id);
	
	public void addTime(int plan_id);
	
	public void deleteAllPlans(); //删除所有数据
	
	public int[] getTaskNum();//获取任务记录数 add 2015-02-11 10:53:42
	
	public String truncCountToHour(int count);
}
