package cn.minihand.plantime.util;

import java.util.Date;
import java.util.Properties;
import javax.mail.Address;
import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.apache.log4j.Logger;

/**
 * 发送普通邮件，接受普通邮件 发送带有附件的邮件，接收带有附件的邮件 发送html形式的邮件，接受html形式的邮件 发送带有图片的邮件等做了一个总结。
 */
public class SendMail {
	private Logger logger = Logger.getLogger(SendMail.class);
	// 邮箱服务器
	private String host = "smtp.qq.com";
	// 这个是你的邮箱用户名
	private String username = "274221276@qq.com";
	// 你的邮箱密码
	private String password = "linguoqiang27422";

	private String mail_head_name = "this is head of this mail";

	private String mail_head_value = "任务计划完成提醒";

	private String mail_to = "15960163650@qq.com";

	private String mail_from = "274221276@qq.com";

	private String mail_subject = "任务完成提醒邮件";

	private String personalName = "我的邮件";

	public SendMail() {
	}

	/**
	 * 此段代码用来发送普通电子邮件
	 */
	public int send(String mail_body){
		int res = -1;
		try {
			Properties props = new Properties(); // 获取系统环境
			Authenticator auth = new Email_Autherticator(); // 进行邮件服务器用户认证
			props.put("mail.smtp.host", host);
			props.put("mail.smtp.auth", "true");
			Session session = Session.getDefaultInstance(props, auth);
			// 设置session,和邮件服务器进行通讯。
			MimeMessage message = new MimeMessage(session);
			// message.setContent("foobar, "application/x-foobar"); // 设置邮件格式
			message.setSubject(mail_subject); // 设置邮件主题
			message.setText(mail_body); // 设置邮件正文
			message.setHeader(mail_head_name, mail_head_value); // 设置邮件标题
			message.setSentDate(new Date()); // 设置邮件发送日期
			Address address = new InternetAddress(mail_from, personalName);
			message.setFrom(address); // 设置邮件发送者的地址
			Address toAddress = new InternetAddress(mail_to); // 设置邮件接收方的地址
			message.addRecipient(Message.RecipientType.TO, toAddress);
			Transport.send(message); // 发送邮件
			logger.info("邮件发送成功！");
			res = 1;
			return res;
		} catch (Exception ex) {
			ex.printStackTrace();
			return res;
		}
	}

	/**
	 * 用来进行服务器对用户的认证
	 */
	public class Email_Autherticator extends Authenticator {
		public Email_Autherticator() {
			super();
		}

		public Email_Autherticator(String user, String pwd) {
			super();
			username = user;
			password = pwd;
		}

		public PasswordAuthentication getPasswordAuthentication() {
			return new PasswordAuthentication(username, password);
		}
	}

	public static void main(String[] args) {
		//发送邮件
		SendMail send = new SendMail();
		int res = send.send("已经完成任务！");
		while(res != 1){
			res = send.send("已经完成任务！");
		}
	}
}

