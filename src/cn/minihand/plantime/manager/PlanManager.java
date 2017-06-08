package cn.minihand.plantime.manager;

import java.util.List;

import cn.minihand.plantime.model.Plan;

public interface PlanManager {
	
	public void addPlan(Plan plan);	//add plan
	
	public List<Plan> findAll();	// find all plan
	
	public void deletePlan(Plan plan); //delete plan
	
	public void updatePlan(Plan plan);
	
	public void deleteAllPlans(); //删除所有数据
	
	public List<Plan> findByDate();	//按日期来查找是否加载
	
	public Plan findByPlanName(String planName); //根据计划名称来获取计划
}
