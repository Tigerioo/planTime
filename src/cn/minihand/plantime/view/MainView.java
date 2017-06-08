package cn.minihand.plantime.view;

import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import com.newland.bg.commontools.file.FileUtil;
import com.newland.bg.commontools.time.TimeTool;

import cn.minihand.plantime.experience.ExperienceManager;
import cn.minihand.plantime.manager.TimeManager;
import cn.minihand.plantime.manager.impl.TimeManagerImpl;
import cn.minihand.plantime.util.SocketUtil;

public class MainView implements ActionListener, KeyListener {
	private static Logger logger = Logger.getLogger(MainView.class);
	private static final long serialVersionUID = 1L;
	private static JFrame frame;
	private JPanel panel, panel2;
	private JMenuItem startItem,expItem;//TomatoTime Menu
	private JMenuItem aboutMe, sync;//Help Menu
	private JPanel outPanel;
	private TimeManager timeManager = new TimeManagerImpl();
	private JLabel today, week, month, all;

	public void lunchFrame() {
		frame = new JFrame();
		
		JMenuBar menuBar = new JMenuBar();
		frame.setJMenuBar(menuBar);
		JMenu editMenu = new JMenu("TomatoTime");
		
		startItem = new JMenuItem("start");
		startItem.addActionListener(this);
		editMenu.add(startItem);
		
		expItem = new JMenuItem("exp");
		expItem.addActionListener(this);
		editMenu.add(expItem);
		
		menuBar.add(editMenu);
		
		JMenu helpMenu = new JMenu("Help");
		
		//add sync menu
		sync = new JMenuItem("Sync");
		sync.addActionListener(this);
		helpMenu.add(sync);
		
		aboutMe = new JMenuItem("aboutMe");
		aboutMe.addActionListener(this);
		helpMenu.add(aboutMe);
		
		menuBar.add(helpMenu);
		
		frame.setMinimumSize(new Dimension(280, 195));
		frame.setSize(280, 195);
		frame.setTitle("PlanTime");
//		frame.setAlwaysOnTop(true);
		Image icon = Toolkit.getDefaultToolkit().getImage("./cup.png");
		frame.setIconImage(icon);
		frame.setDefaultCloseOperation(3);
		
		double width = Toolkit.getDefaultToolkit().getScreenSize().getWidth();
		double height = Toolkit.getDefaultToolkit().getScreenSize().getHeight();
		
		frame.setLocation((int)width/2,(int)height/2); 

		load();
		
//		frame.setFocusableWindowState(false);//不用每次都获取当前窗口的焦点
		frame.setVisible(true);
		
//		while(true){
//			text.setText(getCurrentTime() + "\n");
//			try {
//				Thread.sleep(1000);
//			} catch (InterruptedException e) {
//			}
//		}
		
		
	}
	
	public void load(){
		this.panel = new JPanel();
		JLabel text_tip = new JLabel("总计：", SwingUtilities.LEFT);
		JLabel today_tip = new JLabel("今日：", SwingUtilities.LEFT);
		JLabel week_tip = new JLabel("本周：", SwingUtilities.LEFT);
		JLabel month_tip = new JLabel("本月：", SwingUtilities.LEFT);
		
		panel.setLayout(new GridLayout(5,1));
		panel.add(text_tip);
		panel.add(today_tip);
		panel.add(week_tip);
		panel.add(month_tip);
		
		all = new JLabel(timeManager.truncCountToHour(timeManager.getTaskNum()[3]) + "(" + timeManager.getTaskNum()[3] + ")", SwingUtilities.CENTER);
		today = new JLabel(timeManager.truncCountToHour(timeManager.getTaskNum()[0]) + "(" + timeManager.getTaskNum()[0] + ")", SwingUtilities.CENTER);
		week = new JLabel(timeManager.truncCountToHour(timeManager.getTaskNum()[1]) + "(" + timeManager.getTaskNum()[1] + ")", SwingUtilities.CENTER);
		month = new JLabel(timeManager.truncCountToHour(timeManager.getTaskNum()[2]) + "(" + timeManager.getTaskNum()[2] + ")", SwingUtilities.CENTER);
		this.panel2 = new JPanel();
		panel2.setLayout(new GridLayout(5,1));
		panel2.add(all);
		panel2.add(today);
		panel2.add(week);
		panel2.add(month);
		
		outPanel = new JPanel();
		outPanel.setLayout(new GridLayout(1, 3));
		outPanel.add(panel);
		outPanel.add(panel2);
		frame.add(outPanel);
	}

	public String getCurrentTime(){
		DateFormat format = new SimpleDateFormat("HH点mm分ss秒");
		return format.format(new Date());
	}
	
	public static void main(String[] args) {
		PropertyConfigurator.configure("./conf/log4j.properties");
		MainView view = new MainView();
		view.lunchFrame();
	}

	public void actionPerformed(ActionEvent e) {
		if(e.getSource() == startItem){
//			TestView timer = new TestView();
			new Thread(new TimerView()).start();
//			TimerView timer = new TimerView();
//			timer.lunchFrame();
//			JPanel p2 = new JPanel();
			
//			for (int i = 0; i < 10; i++) {
//				JOptionPane.showMessageDialog(frame, i);
//			}
		}
		if(e.getSource() == expItem){
			JOptionPane.showMessageDialog(frame, "当前等级" + ExperienceManager.getCurrentLevel() + ", 经验值：" + ExperienceManager.getCurrentExpInfo());
			
		}
		
		if(e.getSource() == sync){
			logger.info("send upfile to server......");
			String result = ExperienceManager.sendUpFileToServer();
			JOptionPane.showMessageDialog(frame, result);
		}
		
		if(e.getSource() == aboutMe){
			StringBuffer buff = new StringBuffer();
			buff.append(" Author Lewis Lynn \n");
			buff.append("   Version 0.1\n  ");
			buff.append("   2013-08-20\n");
			System.out.println(buff.toString());
			JOptionPane.showMessageDialog(frame, buff.toString());
		}
	}

	public void keyPressed(KeyEvent e) {
	}

	public void keyReleased(KeyEvent e) {
	}

	public void keyTyped(KeyEvent e) {
	}
	
	class TimerView implements ActionListener, KeyListener, Runnable {
		private JFrame timeFrame;
		private JPanel timePanel;
		private JLabel minutes;
		private JLabel timeField;
		private int count;//用于计数，2个25分钟要起来活动活动
		private String preTime;//前一个记录的时间
		private JMenuItem startItem,expItem;//TomatoTime Menu
//		private JMenuItem aboutMe, sync;//Help Menu

		public void run() {
			try {
				lunchCurrentContent();
			} catch (IOException e1) {
				logger.error(e1.toString(), e1);
			}
			
			timeFrame = new JFrame();
			
			/*JMenuBar menuBar = new JMenuBar();
			timeFrame.setJMenuBar(menuBar);
			JMenu editMenu = new JMenu("TomatoTime");
			
			startItem = new JMenuItem("start");
			startItem.addActionListener(this);
			editMenu.add(startItem);
			
			expItem = new JMenuItem("exp");
			expItem.addActionListener(this);
			editMenu.add(expItem);
			
			menuBar.add(editMenu);
			
			JMenu helpMenu = new JMenu("Help");
			aboutMe = new JMenuItem("aboutMe");
			aboutMe.addActionListener(this);
			helpMenu.add(aboutMe);
			menuBar.add(helpMenu);*/
			
			timeFrame.setSize(50, 65);
//			timeFrame.setTitle("PlanTime");
			timeFrame.setAlwaysOnTop(true);
			Image icon = Toolkit.getDefaultToolkit().getImage("./cup.png");
			timeFrame.setIconImage(icon);
			timeFrame.setDefaultCloseOperation(3);
			
			double width = Toolkit.getDefaultToolkit().getScreenSize().getWidth();
			double height = Toolkit.getDefaultToolkit().getScreenSize().getHeight();
			
			timeFrame.setLocation(-30,(int)height-85);

			this.timePanel = new JPanel();

			int seconds = 25 * 60;
//			int seconds = 3;
			this.timeField = new JLabel("开始计时");
			this.timePanel.add(this.timeField);
			timeFrame.add(this.timePanel);
//			timeFrame.setFocusableWindowState(false);//不用每次都获取当前窗口的焦点
			timeFrame.setVisible(true);
			
			String task_content = JOptionPane.showInputDialog("任务内容:", ExperienceManager.getLastContent());
			timeFrame.setTitle(task_content);
			String beginTime = ExperienceManager.getCurrentTime();//add since v1.4.1
			while (seconds != 0) {
				int min = seconds / 60;
				int sec = seconds - min * 60;
				String secStr = "" + sec;
				if(sec < 10){
					secStr = "0" + sec;
				}
				if(min == 0){
					timeField.setText(secStr + "秒(" + ExperienceManager.getTodayCount() + ")");
				}else {
					timeField.setText(min + "分" + secStr + "秒(" + ExperienceManager.getTodayCount() + ")");
				}
				seconds--;
				try {
					Thread.sleep(1000L);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			String endTime = ExperienceManager.getCurrentTime();//add since v1.4.1
			
			DateFormat format = new SimpleDateFormat("yyyyMM");
			String monthDir = format.format(new Date());
			format = new SimpleDateFormat("yyyyMMdd");
			String dayDir = format.format(new Date());
			String fileName = beginTime + "_" + endTime + "_" + task_content;
			ExperienceManager.saveLastContent(task_content);//记录本次的任务内容
			ExperienceManager.saveTimeToFile(monthDir, dayDir, fileName);
			
			/*
			 * add 2013-08-27 15:16:21
			 * 升级经验开始
			 */
			ExperienceManager.upgrade();

			//update mainframe panel text
			all.setText(timeManager.truncCountToHour(timeManager.getTaskNum()[3]) + "(" + timeManager.getTaskNum()[3] + ")");
			today.setText(timeManager.truncCountToHour(timeManager.getTaskNum()[0]) + "(" + timeManager.getTaskNum()[0] + ")");
			week.setText(timeManager.truncCountToHour(timeManager.getTaskNum()[1]) + "(" + timeManager.getTaskNum()[1] + ")");
			month.setText(timeManager.truncCountToHour(timeManager.getTaskNum()[2]) + "(" + timeManager.getTaskNum()[2] + ")");
			
			/*
			 * add since v1.4.1
			 */
			boolean isSend = SocketUtil.sendToServer(new String[]{monthDir, dayDir, fileName});
			if(!isSend){//网络不通畅，放到等待上传文件夹upfile下
				logger.error(fileName + " send to server fail, backup to upfile...");
				ExperienceManager.saveToUpFile(monthDir, dayDir, fileName);
				
			}else {//网络通畅，将历史的数据传到服务端
				logger.info("send upfile to server......");
				ExperienceManager.sendUpFileToServer();
			}
			
			if(count == 2){
				JOptionPane.showMessageDialog(timeFrame, "第"+count+"个番茄时间到了……今日已完成" + ExperienceManager.getTodayCount() + "个,亲，该喝水了！当前等级" + ExperienceManager.getCurrentLevel() + ", 经验值：" + ExperienceManager.getCurrentExpInfo());
			}else if(count == 4){
				JOptionPane.showMessageDialog(timeFrame, "第"+count+"个番茄时间到了……今日已完成" + ExperienceManager.getTodayCount() + "个,亲，该起来活动活动了！当前等级" + ExperienceManager.getCurrentLevel() + ", 经验值：" + ExperienceManager.getCurrentExpInfo());
				count = 1;//重置count
			}else {
				JOptionPane.showMessageDialog(timeFrame, "第"+count+"个番茄时间到了……今日已完成" + ExperienceManager.getTodayCount() + "个,当前等级" + ExperienceManager.getCurrentLevel() + ", 经验值：" + ExperienceManager.getCurrentExpInfo());
			}
			count ++;
			updateCount();
			
			timeFrame.dispose();
//			System.exit(0);
		}

		public void lunchCurrentContent() throws IOException{
			File file = new File("./count");
			String currentTime = TimeTool.getCurrentDate();
			if(!file.exists()){
				file.createNewFile();
				FileUtil.writeContentToFile(file, "1###" + currentTime);
				count=1;
				return;
			}
			
			String content = FileUtil.readFile(file);
			String[] strArray = content.split("###");
			
			if(content.length() > 0){
				count=Integer.parseInt(strArray[0]);
				preTime = strArray[1];
				DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				Date current = null;
				Date last = null;
				try {
					current = format.parse(currentTime);
					last = format.parse(preTime);
				} catch (ParseException e) {
					logger.error(e.toString(), e);
				}
				
				long currentMillis = current.getTime();
				long lastMillis = last.getTime();
				
				if(currentMillis > (lastMillis + 2*25*60*1000)){//超过2个番茄时间,重新计数并重置时间
					count = 1;
					FileUtil.writeContentToFile(file, "1###" + currentTime);
				}
			}
		}
		
		private void updateCount(){
			FileUtil.writeContentToFile(new File("./count"), String.valueOf(count) + "###" + TimeTool.getCurrentDate());
		}
		
		public void actionPerformed(ActionEvent e) {
			if(e.getSource() == expItem){
				JOptionPane.showMessageDialog(timeFrame, "当前等级" + ExperienceManager.getCurrentLevel() + ", 经验值：" + ExperienceManager.getCurrentExpInfo());
			}
			
			if(e.getSource() == sync){
				System.out.println("sync");
			}
			
			if(e.getSource() == aboutMe){
				StringBuffer buff = new StringBuffer();
				buff.append(" Author Lewis Lynn \n");
				buff.append("   Version 0.1\n  ");
				buff.append("   2013-08-20\n");
				System.out.println(buff.toString());
				JOptionPane.showMessageDialog(timeFrame, buff.toString());
			}
		}

		public void keyPressed(KeyEvent e) {
		}

		public void keyReleased(KeyEvent e) {
		}

		public void keyTyped(KeyEvent e) {
			if (e.getSource() == this.minutes)
				System.out.println("1111");
		}
	}
}