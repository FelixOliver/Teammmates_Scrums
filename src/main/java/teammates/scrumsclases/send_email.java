package teammates.scrumsclases;

import java.io.UnsupportedEncodingException;
import java.util.Properties;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class send_email {
    
    private String destinatario;
    private String fullName;
    private String university;
    private String country;
    private String url;
    private String email;
    private String comments;
    
    public send_email(String destinatario,String fullName,String university,String country,String url,String email,String comments){
        this.destinatario = destinatario;
        this.fullName = fullName;
        this.university = university;
        this.country = country;
        this.url = url;
        this.email = email;
        this.comments = comments;
    }
    
    public void send_email_to_admin(){  
        
        Properties props = new Properties();
        Session session = Session.getDefaultInstance(props, null);
        
        String msgBody = " Hi TEAMMATES' administrators,<br><br>I am " + this.fullName +", I'm writing to tell you that I'm interested in using TEAMMATES. I would like to have an account, it would be great if you approve my request and these are my personal information:<br><br>Full Name: " + this.fullName +"<br>University/School/Institution: " + this.university +"<br>Country: " + this.country +"<br>Email Address: " + this.email + "<br><br>";
        String extraMsgBody = "";
        
        if(!url.isEmpty())
            extraMsgBody = extraMsgBody + "Home Page's URL: " + this.url +"<br>";
 
        if(!comments.isEmpty())
            extraMsgBody = extraMsgBody + "Comments: " + this.comments +"<br>";
        
        if(!extraMsgBody.isEmpty())
            extraMsgBody = "These is my secundary data:<br><br>" + extraMsgBody;
            
        msgBody = msgBody + extraMsgBody + "<br>*This information is personal, please don't use in other cases, be careful with that.<br>*Check more than once and you should be sure that information request iscorrect if you are agree with the request.<br><br>I wait your answer as soon as possible<br><br>Thank you very much for your time and have a nice day!<br>-" + this.fullName;
        
        try {
            Message msg = new MimeMessage(session);
            msg.setFrom(new InternetAddress("teammates-cs@appspot.gserviceaccount.com"));
            msg.addRecipient(Message.RecipientType.TO,
                                            new InternetAddress(destinatario));
            msg.setSubject("Add New Instructor Request");
            
            msg.setContent(msgBody, "text/html");
            Transport.send(msg);

        } catch (AddressException e) {
            System.out.println("Error de dirección de correo");
        } catch (MessagingException e) {
            System.out.println("Error al enviar mensaje");
        }
        
    }
}
