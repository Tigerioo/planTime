package cn.minihand.plantime.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Map;
import java.util.Properties;

import org.apache.log4j.Logger;

/**
 * 对属性文件的操作
 * 
 * @author Destiny
 * 
 */
public class PropertyUtil {

	private static Logger logger = Logger.getLogger(PropertyUtil.class);
	private String filePath ;

	public PropertyUtil(String filePath){
		this.filePath = filePath;
	}
	
	/**
	 * 读取配置文件
	 * 
	 * @return
	 */
	private Properties getProperty() {
		Properties pro = new Properties();
		try {
			FileInputStream input = new FileInputStream(new File(filePath));
			pro.load(input);
		} catch (FileNotFoundException e) {
			logger.error(e.toString());
		} catch (IOException e) {
			logger.error(e.toString());
		}
		return pro;
	}

	/**
	 * 根据参数来获取值
	 */
	public String getValue(String name) {
		Properties pro = getProperty();
		String value = (String) pro.get(name);
		if (name.equals("driverUrl")) {
			try {
				String ip = InetAddress.getLocalHost().getHostAddress();
				System.out.println("IP:" + ip);
				value = value.replace("myip", ip);
			} catch (UnknownHostException e) {
				logger.error(e.toString());
			}
		}
		return value;
	}
}
