package cn.minihand.plantime.util;

import org.apache.log4j.Logger;

import cn.minihand.plantime.manager.PlanManager;
import cn.minihand.plantime.manager.TimeManager;
import cn.minihand.plantime.manager.WeekManager;
import cn.minihand.plantime.manager.impl.PlanManagerImpl;
import cn.minihand.plantime.manager.impl.TimeManagerImpl;
import cn.minihand.plantime.manager.impl.WeekManagerImpl;

public class DbUtil {

	private static Logger logger = Logger.getLogger(DbUtil.class);
	private static String filePath = "./conf/database.properties"; // 数据配置文件
	public static DbManager db ;
	/*public static PlanManager planmManager;
	public static TimeManager timeManager;
	public static WeekManager weekManager;*/
	
	public static void getDb() {
		PropertyUtil pro = new PropertyUtil(filePath);
		String driverClassName = pro.getValue("driverClassName");
		String url = pro.getValue("driverUrl");
		String username = pro.getValue("userName");
		String password = pro.getValue("password");
		
		db = new DbManager(driverClassName, password, username, url);
		
	}
}
