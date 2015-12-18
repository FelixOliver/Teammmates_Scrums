package teammates.storage.api;

import java.io.UnsupportedEncodingException;
import java.util.Properties;

import javax.servlet.http.*;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
@SuppressWarnings("serial")
public class enviaralerta_start extends HttpServlet{
    public void enviar_Alerta_start(String destinatario,String instructorName,String evaluationName,String curseName,String timeStart,String timeEnd, int gracePeriod) throws UnsupportedEncodingException
    {
        String extraTime = Integer.toString(gracePeriod);
        
        Properties props = new Properties();
        Session session = Session.getDefaultInstance(props, null);

        String msgBody = " Hi " + instructorName +",<br><br>Teammates' team here!<br>We remind you some evaluation where you are, this evaluation is going to open into 24 hours, this evaluation has the following data:<br><br>Evaluation's name: " + evaluationName + "<br>Course: " + curseName + "<br>Time start: " + timeStart + "<br>Time end: " + timeEnd + "<br>Extra time: " + extraTime + " mins<br><br>*This information is only for you and the instructors who are in the course, this message is only an alert." + "<br>**This information helps to prevent accidental opening of evaluations, maybe you set the wrong opening time so you can edit the information of this evaluation, you have 24 hours.<br><br>Thank you very much for your time and have a nice day!<br>";

        System.out.println(msgBody);
        try {
            Message msg = new MimeMessage(session);
            msg.setFrom(new InternetAddress("pruebais2-1147@appspot.gserviceaccount.com")); 
            msg.addRecipient(Message.RecipientType.TO,
                                            new InternetAddress(destinatario));
            msg.setSubject("Evaluation Openning Alert");
            
            msg.setContent(msgBody, "text/html");
            Transport.send(msg);

        } catch (AddressException e) {
            System.out.println("Error de dirección de correo");
        } catch (MessagingException e) {
            System.out.println("Error al enviar mensaje");
        }
    }
    
}
