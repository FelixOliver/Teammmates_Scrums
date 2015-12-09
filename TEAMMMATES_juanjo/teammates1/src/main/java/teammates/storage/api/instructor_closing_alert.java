package teammates.storage.api;

import java.io.UnsupportedEncodingException;


import java.text.SimpleDateFormat;
import java.text.ParseException;
import java.util.Date;
import java.util.List;
import java.util.ArrayList;
import java.util.Properties;

import teammates.common.datatransfer.FeedbackSessionAttributes;
import teammates.common.datatransfer.InstructorAttributes;
import teammates.storage.api.enviaralerta;
import teammates.storage.api.InstructorsDb;

import java.util.Calendar;
import java.util.TimeZone;
import java.io.IOException;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;



public class instructor_closing_alert{
    public static void  main(String [] args) throws ParseException, UnsupportedEncodingException
    {
        
        //Properties props = new Properties();
        //Session session = Session.getDefaultInstance(props, null);
        Calendar cal = Calendar.getInstance();
        
        //cal.set(cal.SECOND,0);
        SimpleDateFormat sdfAmerica = new SimpleDateFormat("yyy-MM-dd HH:mm:ss");
        TimeZone tzInAmerica = TimeZone.getTimeZone("America/Lima");
        sdfAmerica.setTimeZone(tzInAmerica);
        
        String sDateInAmerica = sdfAmerica.format(cal.getTime());
        sDateInAmerica = sDateInAmerica.substring(0,sDateInAmerica.length()-2)+"00";
        
        String fecha1=sDateInAmerica;
       //*****************************************
        String destinatario="juanjolopez28@gmail.com";
        Properties props = new Properties();
        Session session = Session.getDefaultInstance(props, null);

        String msgBody = " Hi "+sDateInAmerica;
        System.out.println(msgBody);
        try {
            Message msg = new MimeMessage(session);
            msg.setFrom(new InternetAddress("scrum-juanjo@appspot.gserviceaccount.com")); 
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
        
        
        
        
        
        //************************
        /*FeedbackSessionsDb sesion=new FeedbackSessionsDb();
        
        //String fecha="Wed, Dec 09 23:59:00 UTC 2015";
        //String fecha1="2015-12-05 23:59:00";
        
        SimpleDateFormat formato1 = new SimpleDateFormat("yyy-MM-dd HH:mm:ss");
        
        Date fechaDate1=formato1.parse(fecha1); 
        List<FeedbackSessionAttributes> sesiones_temp=new ArrayList<FeedbackSessionAttributes>();
        sesiones_temp=sesion.get_open(fechaDate1);
        for(FeedbackSessionAttributes i:sesiones_temp)
        {
          

          InstructorsDb prueba = new InstructorsDb();
           String curso = i.getCourseId();
           List<InstructorAttributes> respuesta = new ArrayList<InstructorAttributes>();
           respuesta = prueba.getInstructorsForCourse(curso);

           for(InstructorAttributes j:respuesta)
            {
                  
                  String start_time=formato1.format(i.startTime);
                  
                  String nombre_sesion=i.getFeedbackSessionName();
                  String nombre_instruc=j.name;
                  String nombre_curso=i.courseId;
                  String ins_email=j.email;
                  enviaralerta enviar=new enviaralerta();
                  enviar.enviar_Alerta(ins_email,nombre_instruc,nombre_sesion,nombre_curso,start_time,fecha1);
                  
                  //out.print(j.email);
                  //out.print("____");
            }
           
           
        } */
        

    }
}

 