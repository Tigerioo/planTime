package cn.minihand.plantime.test;

public class TimeByMinutes implements Runnable{
	
	private int seconds = 0;
	
	public TimeByMinutes(int seconds){
		this.seconds = seconds;
	}
	
	public void run(){
		while(true){
			try{
				seconds --;
				System.out.println(getLeftTime());
				Thread.sleep(1000);
			}catch(InterruptedException e){
				e.printStackTrace();
			}
		}
	}
	
	public String getLeftTime(){
		
		int hour = seconds / 3600;
		int minute = (seconds-hour*3600) / 60;
		int second = seconds - hour*3600 - minute*60;
		
		return hour + ":" + minute + ":" + second;
		
	}
	
	
}
