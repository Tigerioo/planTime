package cn.minihand.plantime.util;

import java.io.File;
import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

/**
 * JDBC数据库模板，进行对象的增删改查
 * 
 * @author Lingq
 * 
 */
public class JDBCTemplate {
	private final Logger logger = Logger.getLogger(JDBCTemplate.class);

	private DbManager dbManager ;
	
	public JDBCTemplate() {
		this.dbManager = DbUtil.db;
	}

	/*
	 * add object
	 */
	public void add(Object obj) {
		if (obj == null) {
			return;
		}
		String className = obj.getClass().getSimpleName();
		Connection conn = null;
		Statement stmt = null;
		StringBuffer insertSql = new StringBuffer(); // insert语句
		insertSql.append("insert into T_" + className + "(" + className.toLowerCase() + "_id");
		StringBuffer valueSql = new StringBuffer(); // values语句
		valueSql.append(" values(seq_" + className.toLowerCase() + ".nextval");

		try {
			Class c1 = obj.getClass(); // 获得obj的class
			Field[] fields = c1.getDeclaredFields(); // 获得所有字段
			AccessibleObject.setAccessible(fields, true); // 可以访问私有变量
			Method[] methods = c1.getDeclaredMethods(); // 获得所有方法
			HashMap values = this.getValues(obj, fields, methods); // 所有属性和所对应的值
			
			// 构造SQL语句
			Iterator<Map.Entry<String, Object>> iter = values.entrySet().iterator();
			while(iter.hasNext()){
				Map.Entry<String, Object> entry = iter.next();
				String key = entry.getKey();
				if(key.toLowerCase().contains(className.toLowerCase()) && key.toLowerCase().contains("id")){
					if(!iter.hasNext()){
						insertSql.append(")");
						valueSql.append(")");
					}
					continue;
				}
				if(iter.hasNext()){
					insertSql.append("," + key);
					valueSql.append("," + entry.getValue());
				}else {
					insertSql.append("," + key + ")");
					valueSql.append("," + entry.getValue() + ")");
				}
			}
			
			String sql = insertSql.toString() + valueSql.toString();
			logger.info(sql);
			conn = dbManager.getConn();
			stmt = conn.createStatement();
			stmt.execute(sql);

		} catch (SecurityException e) {
			logger.error(e.toString());
		} catch (IllegalArgumentException e) {
			logger.error(e.toString());
		} catch (IllegalAccessException e) {
			logger.error(e.toString());
		} catch (InvocationTargetException e) {
			logger.error(e.toString());
		} catch (SQLException e) {
			logger.error(e.toString());
		} finally {
			this.close(stmt);
			this.close(conn);
		}
		logger.info("insert success!");
	}

	/**
	 * 删除该表的所有数据
	 * 
	 * @param obj
	 */
	public void deleteAll(String tableName) {
		Connection conn = null;
		Statement stmt = null;

		try {
			String sql = "delete from " + tableName;
			conn = this.dbManager.getConn();
			stmt = conn.createStatement();

			boolean result = stmt.execute(sql);

			logger.info("删除表" + tableName + "数据成功!");
		} catch (SQLException e) {
			logger.error(e.toString());
		}
	}

	/**
	 * 删除一个对象
	 * 
	 * @param obj
	 */
	public void delete(Object obj) {
		String className = obj.getClass().getSimpleName(); // get class name
		Connection conn = null;
		Statement stmt = null;
		String sql = "";

		try {
			Class c1 = obj.getClass(); // 获得obj的class
			Field[] fields = c1.getDeclaredFields(); // 获得所有字段
			AccessibleObject.setAccessible(fields, true); // 可以访问私有变量
			Method[] methods = c1.getDeclaredMethods(); // 获得所有方法
			HashMap values = this.getValues(obj, fields, methods); // 所有属性和所对应的值

			Iterator iter = values.entrySet().iterator();
			while (iter.hasNext()) {
				Map.Entry entry = (Map.Entry) iter.next();
				Object key = entry.getKey();
				Object value = entry.getValue();
				if ("Integer"
						.equalsIgnoreCase(value.getClass().getSimpleName())) {
					sql = "delete from T_" + className + " where " + key + "="
							+ value;
					logger.info(sql);
					// 链接数据库，并删除
					conn = this.dbManager.getConn();
					stmt = conn.createStatement();
					stmt.execute(sql);
					return;
				}
			}
		} catch (SecurityException e) {
			logger.error(e.toString());
		} catch (IllegalArgumentException e) {
			logger.error(e.toString());
		} catch (IllegalAccessException e) {
			logger.error(e.toString());
		} catch (InvocationTargetException e) {
			logger.error(e.toString());
		} catch (SQLException e) {
			logger.error(e.toString());
		} finally {
			close(stmt);
			close(conn);
		}
	}

	public Object findById(Object obj, int id) {
		Connection conn = null;
		Statement stmt = null;
		ResultSet rs = null;
		HashMap results = new HashMap();
		Class cl = obj.getClass(); // 获得obj的class

		try {
			String className = cl.getSimpleName();
			Field[] fields = cl.getDeclaredFields(); // 获得所有字段
			AccessibleObject.setAccessible(fields, true); // 可以访问私有变量
			Method[] methods = cl.getDeclaredMethods(); // 获得所有方法

			String sql = "select * from T_" + className + " where " + className
					+ "_id=" + id;
			logger.info(sql);
			conn = this.dbManager.getConn();
			stmt = conn.createStatement();
			rs = stmt.executeQuery(sql);

			if (rs.next()) {
				for (int i = 0; i < methods.length; i++) {
					Method method = methods[i];
					String methodName = method.getName();
					if (methodName.startsWith("set")) {
						String m1 = methodName.replaceFirst("set", "");
						String temp1 = m1.substring(0, 1);
						String temp2 = m1.replaceFirst(temp1, temp1
								.toLowerCase()); // 获得set的属性名
						// 执行set方法,并赋值
						for (int j = 0; j < fields.length; j++) {
							if (temp2.equalsIgnoreCase(fields[j].getName())) {
								if ("String".equalsIgnoreCase(fields[j]
										.getType().getSimpleName())) {
									String val = rs.getString(temp2);
									if (val != null && !"".equals(val)) {
										method.invoke(obj, val);
									}
								}
								if ("int".equalsIgnoreCase(fields[j].getType()
										.getSimpleName())) {
									int val = rs.getInt(temp2);
									if (!"".equals(val)) {
										method.invoke(obj, val);
									}
								}
								if ("Date".equalsIgnoreCase(fields[j].getType()
										.getSimpleName())) {
									Date val = rs.getDate(temp2);
									if (!"".equals(val)) {
										method.invoke(obj, val);
									}
								}
							}
						}
					}
				}
			}
		} catch (SQLException e) {
			logger.error(e.toString());
		} catch (IllegalArgumentException e) {
			logger.error(e.toString());
		} catch (IllegalAccessException e) {
			logger.error(e.toString());
		} catch (InvocationTargetException e) {
			logger.error(e.toString());
		} finally {
			close(rs);
			close(stmt);
			close(conn);
		}
		return obj;
	}

	/**
	 * 
	 * @param obj
	 *            要查找的对象
	 * @param fieldName
	 *            where语句的字段
	 * @param fieldValue
	 *            该字段的值
	 * @return
	 */
	public Object findByField(Object obj, Object fieldName, Object fieldValue) {
		Connection conn = null;
		Statement stmt = null;
		ResultSet rs = null;
		HashMap results = new HashMap();
		Class cl = obj.getClass(); // 获得obj的class

		try {
			String className = cl.getSimpleName();
			Field[] fields = cl.getDeclaredFields(); // 获得所有字段
			AccessibleObject.setAccessible(fields, true); // 可以访问私有变量
			Method[] methods = cl.getDeclaredMethods(); // 获得所有方法
			String sql = "";
			// 根据传入的参数类型来构造sql查询语句
			if ("Integer".equals(fieldValue.getClass().getSimpleName())) {
				sql = "select * from T_" + className + " where " + fieldName
						+ "=" + fieldValue;
			} else {
				sql = "select * from T_" + className + " where " + fieldName
						+ "='" + fieldValue + "'";
			}

			logger.info(sql);
			conn = this.dbManager.getConn();
			stmt = conn.createStatement();
			rs = stmt.executeQuery(sql);

			if (rs.next()) {
				for (int i = 0; i < methods.length; i++) {
					Method method = methods[i];
					String methodName = method.getName();
					if (methodName.startsWith("set")) {
						String m1 = methodName.replaceFirst("set", "");
						String temp1 = m1.substring(0, 1);
						String temp2 = m1.replaceFirst(temp1, temp1
								.toLowerCase()); // 获得set的属性名
						// 执行set方法,并赋值
						for (int j = 0; j < fields.length; j++) {
							if (temp2.equalsIgnoreCase(fields[j].getName())) {
								if ("String".equalsIgnoreCase(fields[j]
										.getType().getSimpleName())) {
									String val = rs.getString(temp2);
									if (val != null && !"".equals(val)) {
										method.invoke(obj, val);
									}
								}
								if ("int".equalsIgnoreCase(fields[j].getType()
										.getSimpleName())) {
									int val = rs.getInt(temp2);
									if (!"".equals(val)) {
										method.invoke(obj, val);
									}
								}
								if ("Date".equalsIgnoreCase(fields[j].getType()
										.getSimpleName())) {
									Timestamp ts = rs.getTimestamp(temp2);
									SimpleDateFormat format = new SimpleDateFormat(
											"yyyy-MM-dd HH:mm:ss");
									if (!"".equals(ts)) {
										method.invoke(obj, ts);
									}
								}
							}
						}
					}
				}
			}
		} catch (SQLException e) {
			logger.error(e.toString());
		} catch (IllegalArgumentException e) {
			logger.error(e.toString());
		} catch (IllegalAccessException e) {
			logger.error(e.toString());
		} catch (InvocationTargetException e) {
			logger.error(e.toString());
		} finally {
			close(rs);
			close(stmt);
			close(conn);
		}
		return obj;
	}

	public void update(Object obj) {
		if (obj == null) {
			return;
		}
		String className = obj.getClass().getSimpleName();
		Connection conn = null;
		Statement stmt = null;
		StringBuffer updateSql = new StringBuffer("update T_" + className
				+ " set "); // insert语句
		int id = 0;

		try {
			Class c1 = obj.getClass(); // 获得obj的class
			Field[] fields = c1.getDeclaredFields(); // 获得所有字段
			AccessibleObject.setAccessible(fields, true); // 可以访问私有变量
			Method[] methods = c1.getDeclaredMethods(); // 获得所有方法
			HashMap values = this.getValues(obj, fields, methods); // 所有属性和所对应的值

			// 构造SQL语句
			Iterator<Map.Entry<String, Object>> iter = values.entrySet().iterator();
			while(iter.hasNext()){
				Map.Entry<String, Object> entry = iter.next();
				String key = entry.getKey();
				if(key.contains("id") && key.contains(className.toLowerCase())){
					id = (Integer) entry.getValue();
					continue;
				}
				
				if(iter.hasNext()){
					updateSql.append(entry.getKey() + "="
							+ entry.getValue() + ", ");
				}else {
					updateSql.append(key + "="
							+ entry.getValue() + " where " + className
							+ "_id=" + id);
				}
			}
			String sql = updateSql.toString();
			logger.info(sql);
			conn = this.dbManager.getConn();
			stmt = conn.createStatement();
			stmt.executeUpdate(sql);

		} catch (SecurityException e) {
			logger.error(e.toString());
		} catch (IllegalArgumentException e) {
			logger.error(e.toString());
		} catch (IllegalAccessException e) {
			logger.error(e.toString());
		} catch (InvocationTargetException e) {
			logger.error(e.toString());
		} catch (SQLException e) {
			logger.error(e.toString());
		} finally {
			this.close(stmt);
			this.close(conn);
		}

	}

	public List findAll(Object obj) {
		List all = new ArrayList();
		Connection conn = null;
		Statement stmt = null;
		ResultSet rs = null;
		HashMap results = new HashMap();
		Class cl = obj.getClass(); // 获得obj的class

		try {
			String className = cl.getSimpleName();
			String sql = "select * from T_" + className;
			logger.info(sql);
			conn = this.dbManager.getConn();
			stmt = conn.createStatement();
			rs = stmt.executeQuery(sql);

			while (rs.next()) {
				Object newObj = Class.forName(obj.getClass().getName())
						.newInstance();
				Field[] fields = newObj.getClass().getDeclaredFields(); // 获得所有字段
				AccessibleObject.setAccessible(fields, true); // 可以访问私有变量
				Method[] methods = newObj.getClass().getDeclaredMethods(); // 获得所有方法
				for (int i = 0; i < methods.length; i++) {
					Method method = methods[i];
					String methodName = method.getName();
					if (methodName.startsWith("set")) {
						String m1 = methodName.replaceFirst("set", "");
						String temp1 = m1.substring(0, 1);
						String temp2 = m1.replaceFirst(temp1, temp1
								.toLowerCase()); // 获得set的属性名
						// 执行set方法,并赋值
						for (int j = 0; j < fields.length; j++) {
							if (temp2.equalsIgnoreCase(fields[j].getName())) {
								if ("String".equalsIgnoreCase(fields[j]
										.getType().getSimpleName())) {
									String val = rs.getString(temp2);
									if (val != null && !"".equals(val)) {
										method.invoke(newObj, val);
										continue;
									}
								}
								if ("int".equalsIgnoreCase(fields[j].getType()
										.getSimpleName())) {
									int val = rs.getInt(temp2);
									if (!"".equals(val)) {
										method.invoke(newObj, val);
									}
								}
								if ("Date".equalsIgnoreCase(fields[j].getType()
										.getSimpleName())) {
									Date val = rs.getDate(temp2);
									if (!"".equals(val)) {
										method.invoke(obj, val);
									}
								}
							}
						}
					}
				}
				all.add(newObj);
			}
		} catch (SQLException e) {
			logger.error(e.toString());
		} catch (IllegalArgumentException e) {
			logger.error(e.toString());
		} catch (IllegalAccessException e) {
			logger.error(e.toString());
		} catch (InvocationTargetException e) {
			logger.error(e.toString());
		} catch (ClassNotFoundException e) {
			logger.error(e.toString());
		} catch (InstantiationException e) {
			logger.error(e.toString());
		} finally {
			close(rs);
			close(stmt);
			close(conn);
		}
		return all;
	}

	/**
	 * 执行特定SQL语句，检查是否有返回值
	 * 
	 * @param sql
	 * @return
	 */
	public int executeSql(String sql) {
		int ret = 0;
		Connection conn = null;
		Statement stmt = null;
		ResultSet rs = null;
		try {
			conn = this.dbManager.getConn();
			stmt = conn.createStatement();
			rs = stmt.executeQuery(sql);
			if (rs.next()) {
				ret = 1;
				return ret;
			}

		} catch (SQLException e) {
			logger.error(e.toString());
		}

		return ret;
	}

	/**
	 * 字段所对应的值，存放在HashMap中
	 * 
	 * @param obj
	 * @param fields
	 * @param methods
	 * @return
	 * @throws IllegalArgumentException
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 */
	private HashMap getValues(Object obj, Field[] fields, Method[] methods)
			throws IllegalArgumentException, IllegalAccessException,
			InvocationTargetException {
		HashMap maps = new HashMap();

		for (int i = 0; i < methods.length; i++) {
			Method method = methods[i];
			String name = method.getName();
			if (name.startsWith("get")) {
				name = name.replace("get", "");
				for (int j = 0; j < fields.length; j++) {
					String fieldName = fields[j].getName();
					Class type = fields[j].getType();
					if (name.toLowerCase().equals(fieldName.toLowerCase())) {
						if ("int".equals(type.getSimpleName())) {
							if (method.invoke(obj, null) == null
									|| method.invoke(obj, null).equals("")) {
								break;
							} else {
								maps.put(fieldName, method.invoke(obj, null));
								break;
							}
						} else if ("Date".equals(type.getSimpleName())) {// date类型的处理，采用to_date函数转换
							if (method.invoke(obj, null) == null
									|| method.invoke(obj, null).equals("")) {
								break;
							} else {
								SimpleDateFormat format = new SimpleDateFormat(
										"yyyy-MM-dd HH:mm:ss");
								String date = format.format(method.invoke(obj,
										null));
								maps.put(fieldName, "to_date('" + date
										+ "','yyyy-mm-dd hh24:mi:ss')");
								break;
							}
						} else {
							if (method.invoke(obj, null) == null
									|| method.invoke(obj, null).equals("")) {
								break;
							} else {
								maps.put(fieldName, "'"
										+ method.invoke(obj, null) + "'");
								break;
							}
						}
					}
				}
			}
		}
		return maps;
	}

	/**
	 * 判断当前是那种数据库连接
	 * 
	 * @param xmlFileName
	 *            根据spring的配置文件中的dataSource来识别
	 * @return 返回的是数据库名称，如：mysql、oracle
	 */
	public String getDataSourceInfo(String xmlpath) {
		String info = null;
		SAXReader reader = new SAXReader();
		try {
			Document document = reader.read(new File(xmlpath));
			Element root = document.getRootElement();

			// 循环根节点
			for (Iterator iter = root.elementIterator(); iter.hasNext();) {
				Element bean = (Element) iter.next(); // 获得下一个节点
				String value = bean.attribute("id").getValue(); // id的指
				// 如果是dataSource就继续
				if ("dataSource".equals(value)) {
					for (Iterator iterator = bean.elementIterator(); iterator
							.hasNext();) {
						// get property element
						Element property = (Element) iterator.next();
						String value1 = property.attribute("name").getValue();
						// get value from driverClassName
						if ("driverClassName".equals(value1)) {
							String value2 = property.element("value").getText();
							if (value2.toLowerCase().contains("mysql")) {
								info = "mysql";
								logger.info("the database is mysql");
								return info;
							} else if (value2.toLowerCase().contains("oracle")) {
								info = "oracle";
								logger.info("the database is oracle");
								return info;
							}
						}
					}
				}
			}
		} catch (DocumentException e) {
			logger.error(e.toString());
		}
		return info;
	}

	private void close(Connection conn) {
		try {
			if (conn != null) {
				conn.close();
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	private void close(PreparedStatement pstmt) {
		try {
			if (pstmt != null) {
				pstmt.close();
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	private void close(Statement stmt) {
		try {
			if (stmt != null) {
				stmt.close();
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	private void close(ResultSet rs) {
		try {
			if (rs != null) {
				rs.close();
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public void testConn(){
		this.dbManager.getConn();
		System.out.println("success");
	}
	
}
