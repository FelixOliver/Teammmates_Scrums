package teammates.storage.api;

import java.io.UnsupportedEncodingException;


import java.text.SimpleDateFormat;
import java.text.ParseException;
import java.util.Date;
import java.util.List;
import java.util.ArrayList;

import teammates.common.datatransfer.FeedbackSessionAttributes;
import teammates.common.datatransfer.InstructorAttributes;
import teammates.storage.api.enviaralerta_start;
import teammates.storage.api.InstructorsDb;

import java.util.Calendar;
import java.util.TimeZone;
import java.io.IOException;



public class instructor_start_alert{
    public static void  main(String [] args) throws ParseException, UnsupportedEncodingException
    {
        
        //Properties props = new Properties();
        
        //Session session = Session.getDefaultInstance(props, null);
        Calendar cal = Calendar.getInstance();
       // cal.add(cal.DAY_OF_MONTH, 1);
        //cal.set(cal.SECOND,0);

        SimpleDateFormat sdfAmerica = new SimpleDateFormat("yyy-MM-dd HH:mm:ss");
        TimeZone tzInAmerica = TimeZone.getTimeZone("America/Lima");
        sdfAmerica.setTimeZone(tzInAmerica);
        
        String sDateInAmerica = sdfAmerica.format(cal.getTime());
        sDateInAmerica = sDateInAmerica.substring(0,sDateInAmerica.length()-2)+"00";

        String fecha1=sDateInAmerica;

        
        
        
        
        
        
        //************************
        FeedbackSessionsDb sesion=new FeedbackSessionsDb();
        
        //String fecha="Wed, Dec 09 23:59:00 UTC 2015";
        //String fecha1="2015-12-05 23:59:00";
        
        SimpleDateFormat formato1 = new SimpleDateFormat("yyy-MM-dd HH:mm:ss");
        
        Date fechaDate1=formato1.parse(fecha1); 
        List<FeedbackSessionAttributes> sesiones_temp=new ArrayList<FeedbackSessionAttributes>();
        sesiones_temp=sesion.get_start(fechaDate1);
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
                  enviaralerta_start enviar=new enviaralerta_start();
                  enviar.enviar_Alerta_start(ins_email,nombre_instruc,nombre_sesion,nombre_curso,start_time,fecha1);
                  
                  //out.print(j.email);
                  //out.print("____");
            }
           
           
        } 
        

    }
}

