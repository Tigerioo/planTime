package cn.minihand.plantime.util;

import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import cn.minihand.plantime.manager.PlanManager;
import cn.minihand.plantime.manager.TimeManager;
import cn.minihand.plantime.manager.WeekManager;
import cn.minihand.plantime.manager.impl.PlanManagerImpl;
import cn.minihand.plantime.manager.impl.TimeManagerImpl;
import cn.minihand.plantime.manager.impl.WeekManagerImpl;
import cn.minihand.plantime.model.Plan;

public class InitializePlan {

	private Logger logger = Logger.getLogger(InitializePlan.class);
	private PlanManager planManager = new PlanManagerImpl();
	private TimeManager timeManager = new TimeManagerImpl();
	private WeekManager weekManager = new WeekManagerImpl();
	
	// 初始化数据
	public void init() {
		XmlUtil util = new XmlUtil();
		List<Plan> plans = util.readXml("./conf/myplan.xml");
		if (plans.size() != 0) {
			logger.info("成功读取plan,总共" + plans.size() + "个");
			dropAllPlans(plans);
			for (Iterator iterator = plans.iterator(); iterator.hasNext();) {
				Plan plan = (Plan) iterator.next();
				planManager.addPlan(plan);
			}
		}
	}

	public void dropAllPlans(List<Plan> plans) {
		timeManager.deleteAllPlans();
		weekManager.deleteAllPlans();
		planManager.deleteAllPlans();
	}

	public static void main(String[] args) {
		PropertyConfigurator.configure("./conf/log4j.properties");
		DbUtil.getDb();
		new InitializePlan().init();
	}
}
