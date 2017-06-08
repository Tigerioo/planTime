/**
 * 
 */
package cn.minihand.plantime.util;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;

import org.apache.log4j.Logger;

/**
 * <p>Title: cn.minihand.plantime.util.SocketUtil.java</p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) 2001-2013 Newland SoftWare Company</p>
 *
 * <p>Company: Newland SoftWare Company</p>
 *
 * @author Lewis.Lynn
 *
 * @version 1.0 CreateTime：2015年2月3日 下午4:37:07
 */

public class SocketUtil {
	
	private static Logger logger = Logger.getLogger(SocketUtil.class);
	
	public static boolean isConnected(){
		PropertyUtil pro = new PropertyUtil("./server.properties");
		String socketaddress = pro.getValue("server_ip");
		int port = Integer.parseInt(pro.getValue("server_port"));
		logger.info("socketaddress=" + socketaddress + ",port=" + port);
		
		Socket socket = new Socket();
		SocketAddress address = new InetSocketAddress(socketaddress, port);
		try {
			socket.connect(address, 3000);
		} catch (IOException e) {
			return false;
		}
		return socket.isConnected();
		
	}
	
	public static boolean sendToServer(Object obj) {
		PropertyUtil pro = new PropertyUtil("./server.properties");
		String socketaddress = pro.getValue("server_ip");
		int port = Integer.parseInt(pro.getValue("server_port"));
		logger.info("socketaddress=" + socketaddress + ",port=" + port);
		
		Socket socket = null;
		OutputStream socketOs = null;
		ObjectOutputStream streamToServer = null;
		try {
			if (obj == null || socketaddress == null || port < 0) {
				throw new IOException("object isn't right!");
			}
			
			socket = new Socket();
			SocketAddress address = new InetSocketAddress(socketaddress, port);
			socket.connect(address, 3000);
			
			socketOs = socket.getOutputStream();
			streamToServer = new ObjectOutputStream(socketOs);
			streamToServer.writeObject(obj);
			logger.info("send to server success ...... ");
		} catch (IOException e2) {
			logger.error(e2.toString(), e2);
			return false;
		} catch (Exception e) {
			logger.error(e.toString(), e);
			return false;
		} finally {
			try {
				if(socketOs != null){
					socketOs.close();
				}
				if(streamToServer != null){
					streamToServer.close();
				}
			} catch (Exception e1) {
				logger.error(e1.toString(), e1);
				socketOs = null;
				streamToServer = null;
			}
			
			try{
				if(null!=socket){
					socket.close();
					socket=null;
				}
			}catch(Exception e){
				socket=null;
			}
		}
		
		return true;
	}
	
	public static void main(String[] args) {
		SocketUtil.sendToServer(new String[]{"201502", "20150204", "111_222_333"});
	}
}
