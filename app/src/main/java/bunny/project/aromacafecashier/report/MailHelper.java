package bunny.project.aromacafecashier.report;

import android.os.Handler;
import android.os.HandlerThread;

import java.util.Properties;

import javax.mail.Address;
import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import bunny.project.aromacafecashier.MyLog;

/**
 * Created by bunny on 17-3-27.
 */

public class MailHelper {

    private Handler mHandler;

//    public MailHelper() {
//        HandlerThread handlerThread = new HandlerThread("MailHelper");
//        handlerThread.start();
//        mHandler = new Handler(handlerThread.getLooper());
//    }

    public static void sendTodaySheet(String title, String content) {
//        mHandler.post(new Runnable() {
//            @Override
//            public void run() {

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
            message.setRecipient(Message.RecipientType.TO, new InternetAddress("supercarabbit@qq.com"));
            message.setSubject(title);
            message.setText(content);//信息内容
            MimeBodyPart textPart = new MimeBodyPart();
            textPart.setContent("图文加附件邮件测试", "text/html;charset=UTF-8");

            MimeMultipart mmp2 = new MimeMultipart();
            mmp2.addBodyPart(textPart);
//
//            MimeBodyPart imagePart = new MimeBodyPart();
//            imagePart.setDataHandler(new DataHandler(new FileDataSource("imagePath")));//图片路径
//            imagePart.setContentID("myimg");

            message.setContent(content, "text/html;charset=UTF-8");

//            message.setContent(mmp2);


            Transport.send(message);

//            Transport transport = session.getTransport();
//            transport.connect("smtp.163.com", 25, sendUserName, sendPassword);//连接发件人使用发件的服务器
//            transport.sendMessage(message, new Address[]{new InternetAddress("492134880@qq.com")});//接受邮件
//            transport.close();
        } catch (MessagingException e) {
            MyLog.i("", e.toString());
        }

//            }
//    });
    }
}
