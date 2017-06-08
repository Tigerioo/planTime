package cn.minihand.plantime.view;

import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Calendar;
import java.util.GregorianCalendar;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import org.apache.log4j.Logger;

import cn.minihand.plantime.manager.TimeManager;
import cn.minihand.plantime.manager.WeekManager;
import cn.minihand.plantime.manager.impl.TimeManagerImpl;
import cn.minihand.plantime.manager.impl.WeekManagerImpl;
import cn.minihand.plantime.model.Plan;
import cn.minihand.plantime.model.Time;
import cn.minihand.plantime.util.DbUtil;
import cn.minihand.plantime.util.JDBCTemplate;
import cn.minihand.plantime.util.SendMail;

public class PlanView extends WindowAdapter implements Runnable, ActionListener {
	
	private Logger logger = Logger.getLogger(PlanView.class);
	public int leftTime = 0; // 需要执行的时间
	public boolean isRun = false; // 判断是否执行
	public String leftTimeValue = null; // 显示计划剩余时间的string
	public String planName = null;	//计划名称
	private String buttonName = "开始";	//计时按钮名称，默认为开始
	private JButton control;
	private JLabel leftTimeL ;	//显示剩余时间
	private Thread time;	//倒计时线程
	private JPanel panel;
	private Plan plan;
	private Time myTime;
	private TimeManager timeManager;
	private WeekManager weekManager;
	private int tempTime,tempPlanTime; 
	
	public JPanel draw(Plan plan, JFrame frame, boolean isOverTime) {
		timeManager = new TimeManagerImpl();
		weekManager = new WeekManagerImpl();
		this.plan = plan;
		this.planName = plan.getPlanName();	
		timeManager.validatePlanTime(plan.getPlan_id()); //检验是否是当天的任务，昨天的任务时间则重置时间
		this.myTime = timeManager.findTime(plan.getPlan_id()); //根据计划ID获取Time类
		
		int dayTime = myTime.getDayCompleteTime(); //获取当天已执行时间
		if(dayTime < 0) dayTime = 0;	//如果本日执行时间为负数，则调为0
		logger.info("本日执行时间为" + dayTime/60 + "分钟");
		//判断是否周末或加班
		Calendar current = new GregorianCalendar();
		int weekend = current.get(Calendar.DAY_OF_WEEK);
		if(weekend!=1 && weekend!=7){//不是周末
			if(isOverTime == true){ //加班
				tempPlanTime = plan.getOverPlanTime();
				logger.info("加班，'" + this.planName +"'计划时间=" + tempPlanTime);
			}else{ //不加班
				tempPlanTime = plan.getPlanTime();
				logger.info("不加班，'" + this.planName +"'计划时间=" + tempPlanTime);
			}
		}else{//周末
			tempPlanTime = plan.getWeekendPlanTime();
			logger.info("周末，执行时间为：" + tempPlanTime);
		}
		if(dayTime != 0){	//不是第一次执行
			this.tempPlanTime = this.leftTime = tempPlanTime - dayTime;
			logger.info("不是第一次执行，剩余时间为:" + leftTime/60 + "分钟");
		}else{	//第一次执行
			this.leftTime = tempPlanTime;
			logger.info("本日第一次执行，剩余时间为：" + leftTime/60 + "分钟");
		}
		
		this.leftTimeValue = this.getLeftTime(leftTime);
		panel = drawPanel();
		panel.setName(String.valueOf(plan.getPlan_id()));
		frame.add(panel);
		frame.addWindowListener(this);	//添加关闭监听，用来更新时间
		
		return panel;
	}

	public JPanel drawPanel() {
		panel = new JPanel();
		// 获得 plan信息
		panel.setLayout(new FlowLayout()); // 加上布局

		// 计划名称
		JLabel planLabel = new JLabel();
		planLabel.setText(planName);
		planLabel.setForeground(Color.red);
		panel.add(planLabel);
		
		leftTimeL = new JLabel();
		panel.add(leftTimeL);

		control = new JButton();
		control.setText(buttonName);
		control.addActionListener(this);
		panel.add(control);
		
		if(leftTime <= 0){
			leftTimeL.setText("剩余时间：任务已经完成！");
			panel.remove(control);
		}else{
			leftTimeL.setText("剩余时间：" + formatLeftTime());
		}
		
		return panel;
	}
	
	/*
	 * 根据任务计划的总秒数
	 */
	public String getLeftTime(int seconds) {
		
		int hour = seconds / 3600;
		int minute = (seconds - hour * 3600) / 60;
		int second = seconds - hour * 3600 - minute * 60;
		return hour + ":" + minute + ":" + second + "   ";

	}

	/*
	 * 如果时间是个位的，则加0
	 */
	public String formatLeftTime(){
		String[] times = leftTimeValue.split(":");
		String strHour = times[0];
		String strMin = times[1];
		String strSec = times[2];
		
		if(strHour.trim().length() == 1){
			strHour = "0" + strHour;
		}
		if(strMin.trim().length() == 1){
			strMin = "0" + strMin;
		}
		if(strSec.trim().length() == 1) {
			strSec = "0" + strSec;
		}
		
		return strHour + ":" + strMin + ":" + strSec + "   ";
	}
	
	public void run() {

		while (Thread.currentThread().getName().equals(planName) && isRun) {
			
			leftTimeValue = getLeftTime(leftTime);
			leftTimeL.setText("剩余时间：" + formatLeftTime());
			leftTime--;
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			if (leftTime <= 0) {
				//更新时间
				int updateTime = this.tempPlanTime-leftTime-tempTime;
				logger.info("updateTime = tempPlanTime-leftTime-tempTime --->" + updateTime + "=" + tempPlanTime + "-" + leftTime + "-" + tempTime);
				if(updateTime != 0){
					logger.info(this.planName + ",本次通过完成任务新增" + updateTime/60 + "分钟" + (updateTime-(updateTime/60)) + "秒");
					timeManager.updateTime(myTime, updateTime);
					tempTime = tempPlanTime-leftTime;
				}
				
				//发送邮件
				SendMail send = new SendMail();
				int res = send.send(this.planName + "已经完成任务！");
				if(res != 1){
					logger.info(plan.getPlanName() + "邮件发送失败，继续发送");
					res = send.send(this.planName + "已经完成任务！");
				}
				
				leftTimeL.setText("剩余时间：任务已经完成！");
				panel.remove(control);
				panel.repaint();
				isRun = false;
				
				weekManager.validateWeek(plan.getPlan_id());	//验证week是否创建
				
				weekManager.updateWeek(plan.getPlan_id());	//更新week表，该天的此任务为已经完成
				
				JOptionPane pane = new JOptionPane();
				pane.setLocation(500,200);
				pane.showMessageDialog(panel, plan.getPlanName() + "任务已经完成！");
				
				Thread.currentThread().interrupt();
			}
		}
		
	}

	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == control) {
			time = new Thread(this, planName);
			if ("开始".equals(buttonName)) {
				isRun = true;
				buttonName = "暂停";
				control.setText(buttonName);
				time.start();
			} else {
				buttonName = "开始";
				control.setText(buttonName);
				isRun = false;
				//更新时间
				int updateTime = tempPlanTime-leftTime-tempTime;
				logger.info("updateTime = tempPlanTime-leftTime-tempTime --->" + updateTime + "=" + tempPlanTime + "-" + leftTime + "-" + tempTime);
				if(updateTime != 0){
					logger.info(this.planName + ",本次通过暂停新增" + updateTime/60 + "分钟" + (updateTime-(updateTime/60)) + "秒");
					timeManager.updateTime(myTime, updateTime);
					tempTime = tempPlanTime-leftTime;
					System.out.println(tempTime);
				}
			}
		}
	}

	public void windowClosing(WindowEvent e) {
		//更新时间
		int updateTime = tempPlanTime-leftTime-tempTime;
		logger.info("updateTime = tempPlanTime-leftTime-tempTime --->" + updateTime + "=" + tempPlanTime + "-" + leftTime + "-" + tempTime);
		if(updateTime != 0){
			logger.info(this.planName + ",本次通过关闭窗口新增" + updateTime/60 + "分钟" + (updateTime-(updateTime/60)) + "秒");
			timeManager.updateTime(myTime, updateTime);
		}
	}

	public JPanel getPanel() {
		return panel;
	}

}
