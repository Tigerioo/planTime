package cn.minihand.plantime.test;

import java.awt.FlowLayout;
import java.awt.event.*;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JButton;
import javax.swing.JTextField;
import javax.swing.JOptionPane;

public class Test2  {

	public static void main(String[] args) {
		String str = "1:0:2";
		System.out.println(formatLeftTime(str));
	}
	
	public static String formatLeftTime(String leftTimeValue){
		String[] times = leftTimeValue.split(":");
		String strHour = times[0];
		String strMin = times[1];
		String strSec = times[2];
		
		if(strHour.length() == 1){
			strHour = "0" + strHour;
		}
		if(strMin.length() == 1){
			strMin = "0" + strMin;
		}
		if(strSec.length() == 1) {
			strSec = "0" + strSec;
			System.out.println(strSec);
		}
		
		return strHour + ":" + strMin + ":" + strSec + "   ";
	}
}
