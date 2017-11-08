package bunny.project.aromacafecashier.report;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.util.Properties;

import javax.activation.CommandMap;
import javax.activation.DataHandler;
import javax.activation.FileDataSource;
import javax.activation.MailcapCommandMap;
import javax.mail.Address;
import javax.mail.Authenticator;
import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.mail.internet.MimeUtility;

import bunny.project.aromacafecashier.MyLog;

/**
 * Created by bunny on 17-3-27.
 */

public class MailHelper {

    private static Address[] TO_ADDRESSES;

    static {
        try {
            TO_ADDRESSES = new Address[]{
                    new InternetAddress("1172434168@qq.com"),
                    new InternetAddress("supercarabbit@qq.com")
            };
        } catch (AddressException e) {
            MyLog.i("", e.toString());
        }
    }


    public static boolean sendTodaySheet(String title, String content, File file) {
        final String sendUserName = "aroma_cafe@163.com";
        final String sendPassword = "orange12345";

        final String SSL_FACTORY = "javax.net.ssl.SSLSocketFactory";

        Properties properties = new Properties();
        properties.setProperty("mail.smtp.host", "smtp.163.com");
        properties.setProperty("mail.transport.protocol", "smtp");//声明发送邮件使用的端口
        properties.setProperty("mail.smtp.socketFactory.class", SSL_FACTORY);
        properties.setProperty("mail.smtp.socketFactory.fallback", "false");
        properties.setProperty("mail.smtp.port", "465");
        properties.setProperty("mail.smtp.socketFactory.port", "465");
        properties.setProperty("mail.smtp.auth", "true");//服务器需要认证

        Session session = Session.getInstance(properties, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(sendUserName, sendPassword);
            }
        });
        session.setDebug(true);//同意在当前线程的控制台打印与服务器对话信息

        try {
            Message message = new MimeMessage(session);//构建发送的信息
            message.setFrom(new InternetAddress("aroma_cafe@163.com"));//发件人
            message.setRecipients(Message.RecipientType.TO, TO_ADDRESSES);
            message.setSubject(title);

            Multipart multipart = new MimeMultipart();


            addContent(multipart, content);
            addFile(multipart, file);

            message.setContent(multipart);


            MailcapCommandMap mc = (MailcapCommandMap) CommandMap.getDefaultCommandMap();
            mc.addMailcap("text/html;; x-Java-content-handler=com.sun.mail.handlers.text_html");
            mc.addMailcap("text/xml;; x-java-content-handler=com.sun.mail.handlers.text_xml");
            mc.addMailcap("text/plain;; x-java-content-handler=com.sun.mail.handlers.text_plain");
            mc.addMailcap("multipart/*;; x-java-content-handler=com.sun.mail.handlers.multipart_mixed");
            mc.addMailcap("message/rfc822;; x-java-content-handler=com.sun.mail.handlers.message_rfc822");
            CommandMap.setDefaultCommandMap(mc);


            Transport.send(message);
        } catch (Exception e) {
            MyLog.i("sendTodaySheet", e.toString());
            return false;
        }

        return true;
    }

    private static void addFile(Multipart multipart, File file) {
        if (file == null || file.isDirectory() || !file.exists()) {
            return;
        }
        try {
            BodyPart bodyPart = new MimeBodyPart();
            FileDataSource fileDataSource = new FileDataSource(file);
            bodyPart.setDataHandler(new DataHandler(fileDataSource));
            bodyPart.setFileName(MimeUtility.encodeText(fileDataSource.getName(), "UTF-8", null));

            multipart.addBodyPart(bodyPart);
        } catch (MessagingException e) {
        } catch (UnsupportedEncodingException e) {
        }
    }

    private static void addContent(Multipart multipart, String content) {
        try {
            BodyPart bodyPart = new MimeBodyPart();
            bodyPart.setContent(content, "text/html;charset=UTF-8");
            multipart.addBodyPart(bodyPart);
        } catch (MessagingException e) {
            MyLog.i("", e.getMessage());
        }
    }

    public static void receiveMail() {

    }
}