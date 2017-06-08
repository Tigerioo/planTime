package cn.minihand.plantime.view;

import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.text.ParseException;
import java.util.Iterator;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import cn.minihand.plantime.manager.PlanManager;
import cn.minihand.plantime.manager.impl.PlanManagerImpl;
import cn.minihand.plantime.model.Plan;
import cn.minihand.plantime.util.DbManager;
import cn.minihand.plantime.util.DbUtil;
import cn.minihand.plantime.util.JDBCTemplate;

public class PlanTimeView implements ActionListener, KeyListener{
	
	private static Logger logger = Logger.getLogger(PlanTimeView.class);
	private static final long serialVersionUID = 1L;
	private PlanManager planManager ;
	private static JFrame frame ;
	private List<Plan> plans;
	private JPanel panel, tempPanel ;
	private JButton yesOverTime, noOverTime, valdPwd;
	private JTextField field;
	private JLabel psw ;
	private JComboBox comboBox; //下拉框
	private boolean isOverTime;
	
	public void lunchFrame(){
		DbUtil.getDb(); //加载数据
		planManager = new PlanManagerImpl();
		frame = new JFrame();
		
		panel = new JPanel();
		
		psw = new JLabel("请输入密码：");
		psw.addKeyListener(this);
		panel.add(psw);
		field = new JTextField(10);
		panel.add(field);
		
		valdPwd = new JButton("登录");
		valdPwd.addActionListener(this);
		panel.add(valdPwd);
		
		frame.add(panel);
		
		frame.setSize(100, 150);
		frame.setTitle("计划计时器");
		
		frame.setDefaultCloseOperation(frame.EXIT_ON_CLOSE); // 按下关闭按钮时结束进程
		frame.setLocation(600, 200);
		frame.setVisible(true);
	}
	
	public void isOverTime(){
		
		yesOverTime = new JButton("加班");
		yesOverTime.addActionListener(this);
		
		noOverTime = new JButton("不加班");
		noOverTime.addActionListener(this);
		
		//reload = new JButton("重新加载数据");
		//reload.addActionListener(this);
		
		panel.add(yesOverTime);
		panel.add(noOverTime);
		//panel.add(reload);
	}
	
	private void drawComboBox(boolean isOverTime){
		this.isOverTime = isOverTime;
		plans = planManager.findByDate();
		comboBox = new JComboBox();
		comboBox.addItem("请选择...");
		int size = plans.size();
		logger.info("总共加载" + size + "个计划");
		
		for (Iterator<Plan> iterator = plans.iterator(); iterator.hasNext();) {
			Plan plan = (Plan) iterator.next();
			comboBox.addItem(plan.getPlanName());
			
		}
		frame.add(comboBox);
		comboBox.addActionListener(this);
		frame.setLayout(new GridLayout(size + 1,3));
	
		frame.setSize(300, size * 80);
		frame.validate();
	}
	
	public static void main(String[] args) {
		PropertyConfigurator.configure("./conf/log4j.properties");
		PlanTimeView view = new PlanTimeView();
		view.lunchFrame();
	}
	
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == yesOverTime) {
			frame.remove(panel);
			frame.validate();
			drawComboBox(true);
		}
		if (e.getSource() == noOverTime) {
			frame.remove(panel);
			frame.validate();
			drawComboBox(false);
		}
		if (e.getSource() == valdPwd) {
			if("317".equals(field.getText())){
				panel.remove(psw);
				panel.remove(field);
				panel.remove(valdPwd);
				frame.repaint();
				isOverTime();
				frame.validate();
			}else{
				JOptionPane pane = new JOptionPane();
				pane.showMessageDialog(panel, "password is 317!");
				System.exit(0);
			}
		}
		if(e.getSource() == comboBox){
			PlanView planView = new PlanView();
			if(tempPanel != null){
				frame.remove(tempPanel);
			}
			String itemName = (String)comboBox.getSelectedItem();
			Plan plan = null;
			if(itemName != null && !"null".equals(itemName)){
				plan = planManager.findByPlanName(itemName);
				System.out.println(plan.getPlanName());
				tempPanel = planView.draw(plan, frame, isOverTime);
			}
			frame.validate();
		}
		/*if (e.getSource() == reload) {
			InitializePlan init = new InitializePlan();
			init.init();
			frame.validate();
		}*/
	}

	public void keyPressed(KeyEvent e) {
		
	}

	public void keyReleased(KeyEvent e) {
		
	}

	public void keyTyped(KeyEvent e) {
		if(e.getSource() == this.psw){
			System.out.println("1111");
		}
	}
}
