package cn.minihand.plantime.util;

import java.sql.*;
import org.apache.commons.dbcp.BasicDataSource;

import org.apache.log4j.Logger;

public class DbManager {
	static Logger logger = Logger.getLogger(DbManager.class);

	private String driverClassName; // 数据库驱动程序
	private String url; // 数据库url
	private String username; // dba帐号
	private String password; // 密码
	private int initialSize; // 初始化连接数量
	private int maxIdle; // 最大idle数
	private int minIdle; // 最小idle数
	private long maxWait; // 超时回收时间
	private int maxActive;

	private BasicDataSource pool; // 数据库连接池

	public DbManager(){
		
	}
	
	public DbManager(String driverClassName, String password, String username,
			String url) {

		initialSize = 10;
		maxIdle = 20;
		minIdle = 5;
		maxWait = 1000 * 60 * 10;
		pool = new BasicDataSource();
		this.maxActive = 50;
		this.pool.setDriverClassName(driverClassName);
		pool.setUrl(url);
		pool.setUsername(username);
		pool.setPassword(password);
		this.pool.setInitialSize(this.initialSize);
		this.pool.setMinIdle(this.minIdle);
		this.pool.setMaxIdle(this.maxIdle);
		this.pool.setMaxWait(maxWait);
		this.pool.setMaxActive(this.maxActive);
		System.out.println("------------------------------初始化连接池-----------------------------------------");
	}

	public String getDriverClassName() {
		return driverClassName;
	}

	public String getUrl() {
		return url;
	}

	public String getUsername() {
		return username;
	}

	public String getPassword() {
		return password;
	}

	public int getMaxIdle() {
		return maxIdle;
	}

	public int getMinIdle() {
		return minIdle;
	}

	public int getInitialSize() {
		return initialSize;
	}

	public long getMaxWait() {
		return maxWait;
	}

	public void setDriverClassName(String driverClassName) {
		if (driverClassName != null && driverClassName.trim().length() > 0)
			this.driverClassName = driverClassName;
		else
			this.driverClassName = null;
		this.pool.setDriverClassName(this.driverClassName);
	}

	public void setUrl(String url) {
		this.url = url;
		this.pool.setUrl(this.url);
	}

	public void setUsername(String username) {
		this.username = username;
		this.pool.setUsername(this.username);
	}

	public void setPassword(String password) {
		this.password = password;
		this.pool.setPassword(this.password);
	}

	public void setMAXIDLE(String maxIdle) {
		try {
			this.maxIdle = Integer.parseInt(maxIdle);
			this.pool.setMaxIdle(this.maxIdle);
		} catch (NumberFormatException nfx) {
		}
	}

	public void setMINIDLE(String minIdle) {
		try {
			this.minIdle = Integer.parseInt(minIdle);
			this.pool.setMinIdle(this.minIdle);
		} catch (NumberFormatException nfx) {
		}
	}

	public void setINITIALSIZE(String initialSize) {
		try {
			this.initialSize = Integer.parseInt(initialSize);
			this.pool.setInitialSize(this.initialSize);
		} catch (NumberFormatException nfx) {
		}
	}

	public void setMAXWAIT(String maxWait) {
		try {
			this.maxWait = Long.parseLong(maxWait);
			this.pool.setMaxWait(this.maxWait);
		} catch (NumberFormatException nfx) {
		}
	}

	public Connection getConn() {

		Connection conn = null;
		try {
			logger.debug(" qzf pool.getNumActive()1=" + pool.getNumActive());
			conn = pool.getConnection();
			logger.debug(" qzf pool.getNumIdle()2=" + pool.getNumIdle());
		} catch (SQLException e1) {
			logger.error(" qzf getConnection come here3.... e1.getMessage()="
					+ e1.getMessage());
		} catch (Exception e2) {
			logger.error(" qzf getConnection come here4....e2.getMessage()"
					+ e2.getMessage());
			System.out.println(e2.getMessage());
		}
		if (conn == null) {
			logger.error(" qzf getConnection come here5 null");
		} else {
			logger.debug(" qzf getConnection come here6 success");
		}
		return conn;

	}

}
