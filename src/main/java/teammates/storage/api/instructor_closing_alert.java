package teammates.storage.api;

import java.io.UnsupportedEncodingException;


import java.text.SimpleDateFormat;
import java.text.ParseException;
import java.util.Date;
import java.util.List;
import java.util.ArrayList;
import java.util.Properties;

import teammates.common.datatransfer.CourseAttributes;
import teammates.common.datatransfer.FeedbackSessionAttributes;
import teammates.common.datatransfer.InstructorAttributes;
import teammates.storage.api.CoursesDb;
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
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.sun.jndi.url.corbaname.corbanameURLContextFactory;


@SuppressWarnings("serial")
public class instructor_closing_alert  extends HttpServlet{
    public  void  funcion() throws  UnsupportedEncodingException, ParseException
    {
        
        //Properties props = new Properties();
        //Session session = Session.getDefaultInstance(props, null);
        //String pruebita="2015-12-16 00:00:00";
        //SimpleDateFormat sdfAmerica1 = new SimpleDateFormat("yyy-MM-dd HH:mm:ss");

        
        Calendar cal = Calendar.getInstance();
        //cal.add(cal.HOUR,8);
        //cal.setTime(sdfAmerica1.parse(pruebita));
        if(cal.get(cal.HOUR)==5)
        {
            cal.add(cal.MINUTE,-1);
        }
        
        SimpleDateFormat sdfAmerica = new SimpleDateFormat("yyy-MM-dd HH:mm:ss");
        TimeZone tzInAmerica = TimeZone.getTimeZone("America/Lima");
        sdfAmerica.setTimeZone(tzInAmerica);
        
        String sDateInAmerica = sdfAmerica.format(cal.getTime());
        //String sDateInAmerica = sdfAmerica1.format(cal.getTime());
        sDateInAmerica = sDateInAmerica.substring(0,sDateInAmerica.length()-2)+"00";
        
        String fecha1=sDateInAmerica;
       //*****************************************
        
        //************************
        FeedbackSessionsDb sesion=new FeedbackSessionsDb();
        
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
                  String end_time = formato1.format(i.endTime);        
                  
                  String nombre_sesion=i.getFeedbackSessionName();
                  String nombre_instruc=j.name;
                  String id_curso=i.courseId;
                  String ins_email=j.email;
                  int extraTime = i.gracePeriod;
                  CoursesDb Curso1=new CoursesDb();
                  CourseAttributes a_curso=new CourseAttributes();
                  a_curso=Curso1.getCourse(id_curso);
                  String nombre_curso=a_curso.name;
                  enviaralerta enviar=new enviaralerta();
                  
                  enviar.enviar_Alerta(ins_email,nombre_instruc,nombre_sesion,nombre_curso,start_time,end_time,extraTime);

            }
           
           
        } 
        

    }
    public void doGet(HttpServletRequest req, HttpServletResponse resp) 
                                    throws IOException{
       // resp.getWriter().println("Prueba");
        try {
            funcion();
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
       // resp.getWriter().println("Prueba");
       
    
    //porbando cambios
    }
 
    
}

 