package cn.iocoder.yudao.framework.common.util.easy;

import cn.iocoder.yudao.framework.common.util.exception.ExceptionUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.mail.Address;
import javax.mail.Message;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.util.Properties;
import java.util.concurrent.CompletableFuture;

/**
 * 邮件发送工具类
 * @author liurui


    <dependency>
        <groupId>javax.mail</groupId>
        <artifactId>javax.mail-api</artifactId>
        <version>1.6.2</version>
    </dependency>
    <dependency>
        <groupId>com.sun.mail</groupId>
        <artifactId>javax.mail</artifactId>
        <version>1.6.2</version>
    </dependency>

 */
@Slf4j
@Component
public class EmailUtil {
	public static final String TO = "liurui416@qq.com";
	public static final String SMTP = "smtp";
	public static final String HOST = "smtp.qq.com";
	public static final String SEND_NAME = "liurui416@qq.com";
	public static final String SEND_PORT = "80";
	public static final String USER_NAME = "liurui416@qq.com";
	public static final String USER_PASSWORD = "ssdhrshggnnldjgj";
	public static final String SUBJECT = "科德服务";

    public static String env = "";

	public static boolean send_SC(String subjectParam, String content, String env, String toStrs) {
        // return true;
        // 邮件暂时报错，先不发送，回头修复在处理
		 try {
		 		String[] tos = StringUtil.isEmpty(toStrs)?TO.split(","):toStrs.split(",");
		 		String subject = StringUtil.customMerge("===",subjectParam,StringUtil.isEmpty(env)?SUBJECT:env);
		 		for (String to : tos) {
		 			send(to,subject,content,SMTP,HOST,SEND_NAME,SEND_PORT,USER_NAME,USER_PASSWORD);
		 		}
		 		log.warn("【邮件发送成功】middle,tos:{}", toStrs);
		 	CompletableFuture.runAsync(() -> {
		 	});
		 	return true;
		 } catch (Exception e) {
             e.printStackTrace();
		 	log.error("【邮件发送失败】middle,异常:{}", ExceptionUtil.getStackTrace(e));
		 	return false;
		 }
	}

    public static boolean send(String subjectParam, String content, String env, String toStrs) {
        if(EnvInfo.isDev())
            return true;
        try {
            String[] tos = StringUtil.isEmpty(toStrs)?TO.split(","):toStrs.split(",");
            String subject = StringUtil.customMerge("===",subjectParam,StringUtil.isEmpty(env)?SUBJECT:env);
            for (String to : tos) {
                send(to,subject,content,SMTP,HOST,SEND_NAME,SEND_PORT,USER_NAME,USER_PASSWORD);
            }
            log.warn("【邮件发送成功】middle,tos:{}", toStrs);
            return true;
        } catch (Exception e) {
            log.error("【邮件发送失败】middle,异常:{}", ExceptionUtil.getStackTrace(e));
            return false;
        }
    }

	// 发送邮件。参数：内容
	public static boolean send(String subject, String content) {
		return send_SC(subject, content, env, TO);
	}

	/**
     * 邮件发送的方法
     *
     * @param to 收件人
     * @param subject 主题
     * @param content 内容
     * @param smtp 协议
     * @param host 发送服务器服务器
     * @param sendName 邮件发送人
     * @param sendPort 邮件发送人端口
     * @param userName 邮件发送人名
     * @param userPwd 邮件发送人密码
     * @return 成功或失败
     */
    public static boolean send(String to, String subject, String content, String smtp, String host,
            String sendName, String sendPort, String userName, String userPwd) {
         if(EnvInfo.isDev())
             return true;
        // 第一步：创建Session
        Properties props = new Properties();
        // 指定邮件的传输协议，smtp(Simple Mail Transfer Protocol 简单的邮件传输协议)
        props.put("mail.transport.protocol", smtp);
        // 指定邮件发送服务器服务器 "smtp.qq.com"
        props.put("mail.host", host);
        // 指定邮件的发送人(您用来发送邮件的服务器，比如您的163\sina等邮箱)
        props.put("mail.from", sendName);
        if (true) {
            props.put("mail.smtp.starttls.enable", "true");
            // props.put("mail.smtp.socketFactory.fallback", "false");
            // props.put("mail.smtp.socketFactory.port", sendPort);
            
            props.setProperty("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
            props.setProperty("mail.smtp.socketFactory.port", "465");
            props.setProperty("mail.smtp.port", "465");
        }
        Session session = Session.getDefaultInstance(props);
 
        // 开启调试模式
        session.setDebug(false);
        try {
            // 第二步：获取邮件发送对象
            Transport transport = session.getTransport();
            // 连接邮件服务器，链接您的163、sina邮箱，用户名（不带@163.com，登录邮箱的邮箱账号，不是邮箱地址）、密码
            transport.connect(userName, userPwd);
            Address toAddress = new  InternetAddress(to);
 
            // 第三步：创建邮件消息体
            MimeMessage message = new MimeMessage(session);
            //设置自定义发件人昵称
            String nick="";
            try {
                nick=javax.mail.internet.MimeUtility.encodeText("我的昵称");
            } catch (UnsupportedEncodingException e) {
                log.error(e.getMessage());
            }
            message.setFrom(new InternetAddress(nick+" <"+sendName+">"));
          //设置发信人
           // message.setFrom(new InternetAddress(sendName));
 
            // 邮件的主题
            message.setSubject(subject);
            //收件人
            message.addRecipient(Message.RecipientType.TO, toAddress);
            /*//抄送人
            Address ccAddress = new InternetAddress("first.lady@whitehouse.gov");
            message.addRecipient(Message.RecipientType.CC, ccAddress);*/
            // 邮件的内容
            message.setContent(content, "text/html;charset=utf-8");
            // 邮件发送时间
            message.setSentDate(new Date());
 
            // 第四步：发送邮件
            // 第一个参数：邮件的消息体
            // 第二个参数：邮件的接收人，多个接收人用逗号隔开（test1@163.com,test2@sina.com）
            transport.sendMessage(message, InternetAddress.parse(to));
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            log.error(e.getMessage());
        }
        return false;
    }

    @Value("${spring.profiles.active:}")
    public void setEnv(String env) {
        EmailUtil.env = env;
    }

    public static void main(String[] args) {
        // 您要发送给谁，标题，内容
    	String url = "liurui416@qq.com";
    	boolean send = send("邮件测试=========================",url);
    	System.out.println("test===="+send);
    	// send_SC("测试","测试内容");
    }
}
