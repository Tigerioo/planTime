package cn.minihand.plantime.test;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

public class Test implements Runnable{
	
	private long millis = 0;
	
	public Test(int hour, int minute){
		Calendar c1 = new GregorianCalendar();
		c1.clear();
		c1.set(Calendar.HOUR_OF_DAY, hour);
		c1.set(Calendar.MINUTE, minute);
		
		millis = c1.getTimeInMillis();
		System.out.println("构造：" + millis);
	}
	
	public void run(){
		while(true){
			try{
				millis -= 1000;
				System.out.println(getLeftTime(millis));
				Thread.sleep(1000);
			}catch(InterruptedException e){
				e.printStackTrace();
			}
		}
	}
	
	public String getLeftTime(long tempMillis){
		
		
		Calendar c = Calendar.getInstance();
		c.clear();
		c.setTimeInMillis(tempMillis);
		SimpleDateFormat format = new SimpleDateFormat("hh:mm:ss");
		String date = format.format(c.getTime());
		return date;
		
	}
	
	
}
