package teammates.storage.api;

import java.io.UnsupportedEncodingException;
import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.servlet.http.HttpServlet;
@SuppressWarnings("serial")
public class enviaralerta extends HttpServlet{
    public void enviar_Alerta(String destinatario,String instructorName,String evaluationName,String curseName,String timeStart,String timeEnd) throws UnsupportedEncodingException
    {
        Properties props = new Properties();
        Session session = Session.getDefaultInstance(props, null);

        String msgBody = " Hi "+ instructorName +",<br><br>Teammates' team here!<br>We remind you some evaluation where you are, just finished, this evaluation has the following data:<br><br>Evaluation's name: "+ evaluationName +"<br>Course: "+ curseName +"<br>Time start: " + timeStart + "<br>Time end: " + timeEnd + "<br><br>*This information is only for you and the instructors who are in the course, this message is only an alert.<br><br>Thank you very much for your time and have a nice day!<br>";
        System.out.println(msgBody);
        try {
            Message msg = new MimeMessage(session);
            msg.setFrom(new InternetAddress("scrum-juanjo@appspot.gserviceaccount.com")); 
            msg.addRecipient(Message.RecipientType.TO,
                                            new InternetAddress(destinatario));
            msg.setSubject("Evaluation Clousing Alert");
            msg.setContent(msgBody, "text/html");
            Transport.send(msg);

        } catch (AddressException e) {
            System.out.println("Error de dirección de correo");
        } catch (MessagingException e) {
            System.out.println("Error al enviar mensaje");
        }
    }
}

 