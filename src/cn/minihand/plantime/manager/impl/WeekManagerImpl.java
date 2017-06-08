package cn.minihand.plantime.manager.impl;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.TimeZone;

import org.apache.log4j.Logger;

import cn.minihand.plantime.manager.WeekManager;
import cn.minihand.plantime.model.Week;
import cn.minihand.plantime.util.JDBCTemplate;

public class WeekManagerImpl implements WeekManager{

	private Logger logger = Logger.getLogger(WeekManagerImpl.class);
	private JDBCTemplate template ;
	
	public WeekManagerImpl(){
		template = new JDBCTemplate();
	}
	
	public void validateWeek(int plan_id) {
		SimpleDateFormat format = new SimpleDateFormat("yyyy.MM.dd");
		String weektime = this.getWeekTime(format.format(new Date()));
		String sql = "select week_id from t_week where plan_id=" + plan_id + " and weekTime='" + weektime + "'" ;
		int result = template.executeSql(sql);
		if(result == 1){
			logger.info("当前week已经存在,不需要创建");
		}else {
			logger.debug("当前week不存在，需要创建");
			//创建一个week
			Week week = new Week();
			week.setPlan_id(plan_id);
			week.setWeekTime(weektime);
			this.addWeek(week);
			logger.info("创建plan_id为" + plan_id + "的week!");
		}
	}

	public String getWeekTime(String currentTime) {
		Calendar current = new GregorianCalendar(TimeZone.getTimeZone("GMT+8"));
		current.clear();
		SimpleDateFormat format = new SimpleDateFormat("yyyy.MM.dd");
		
		try {
			current.setTime(format.parse(currentTime));
		} catch (ParseException e) {
			e.printStackTrace();
		}
		current.set(Calendar.DAY_OF_WEEK, 2);//设为周一
		String day1 = format.format(current.getTime());
		current.set(Calendar.DAY_OF_WEEK, 7);	//周六
		current.set(Calendar.DAY_OF_MONTH, current.get(Calendar.DAY_OF_MONTH)+1); // 周日
		String day2 = format.format(current.getTime());
		logger.info("所属日期:" + day1 + "-" + day2);
		return day1 + "-" + day2;
	}

	public void addWeek(Week week) {
		this.template.add(week);
	}

	public void updateWeek(int plan_id) {
		Week week = this.findWeek(plan_id);
		Calendar c = new GregorianCalendar();
		c.setTime(new Date());
		int dayOfWeek = c.get(Calendar.DAY_OF_WEEK);
		
		if(dayOfWeek == 1){
			week.setSunday("1");
		}else if(dayOfWeek == 2){
			week.setMonday("1");
		}else if(dayOfWeek == 3){
			week.setTuesday("1");
		}else if(dayOfWeek == 4){
			week.setWednesday("1");
		}else if(dayOfWeek == 5){
			week.setThursday("1");
		}else if(dayOfWeek == 6){
			week.setFriday("1");
		}else{
			week.setSaturday("1");
		}
		
		this.template.update(week);
	}

	public Week findWeek(int plan_id) {
		Week week = (Week)this.template.findByField(new Week(), "plan_id", plan_id);
		return week;
	}

	public void deleteAllPlans() {
		this.template.deleteAll("t_week");
	}
	
}
