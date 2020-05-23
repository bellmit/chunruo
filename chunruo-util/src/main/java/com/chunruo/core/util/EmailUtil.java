package com.chunruo.core.util;

import java.util.Properties;

import javax.mail.Message;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import com.chunruo.core.util.DateUtil;
import com.chunruo.core.util.StringUtil;

public class EmailUtil {
	public static final int SUCCESS = 1;
	public static final int FAILURE = 0;
	
	
	public static int sendMail(String mailFrom, String mailTo,
			String mailTitle, String mailContent, String mailServer, String mailPort,
			String mailUser, String mailPassword) {
		try {
//			String emailPort = "587";// gmail's smtp port  
			//建立邮件会话 
			Properties props = new Properties();
			//存储发送邮件服务器的信息
			props.put("mail.smtp.host", mailServer);
			
			if (!StringUtil.isNull(mailPort)) {
				props.put("mail.smtp.port", mailPort);
			}
			//同时通过验证 
			props.put("mail.smtp.auth","true"); 
			props.setProperty("mail.smtp.starttls.enable", "true");
			//根据属性新建一个邮件会话 
			Session s=Session.getInstance(props);
			//由邮件会话新建一个消息对象 
			MimeMessage message=new MimeMessage(s); 
			//设置邮件 
			InternetAddress from= new InternetAddress(mailFrom);
			message.setFrom(from); //设置发件人的地址 
			//设置收件人,并设置其接收类型为TO 
			InternetAddress to=new InternetAddress(mailTo);
			message.setRecipient(Message.RecipientType.TO, to); 
			//设置标题 
			message.setSubject(mailTitle);
			//设置信件内容 
			message.setContent(mailContent, "text/html;charset=utf8");//发送HTML邮件
			//message.setText(mailContent); //发送文本邮件 
			//设置发信时间 
			message.setSentDate(DateUtil.getCurrentDate()); 
			//存储邮件信息 
			message.saveChanges(); 
			//发送邮件 
			Transport transport=s.getTransport("smtp"); 
			//以smtp方式登录邮箱,第一个参数是发送邮件用的邮件服务器SMTP地址,第二个参数为用户名,第三个参数为密码 
			transport.connect(mailServer, mailUser, mailPassword); 
			//发送邮件,其中第二个参数是所有已设好的收件人地址 
			transport.sendMessage(message,message.getAllRecipients()); 
			transport.close(); 
			
			return SUCCESS;
		} catch (Exception e) {
			e.printStackTrace();
			return FAILURE;
		}
	}

	public static int sendMail(String mailFrom, String mailTo,
			String mailTitle, String mailContent, String mailServer,
			String mailCount, String mailPassword) {
		return sendMail(mailFrom, mailTo, mailTitle, mailContent, mailServer, null, mailCount, mailPassword);
	}
	
}
