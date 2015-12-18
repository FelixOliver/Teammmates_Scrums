package teammates.scrums;

import teammates.scrumsclases.*;
import java.io.IOException;

import javax.servlet.http.*;

@SuppressWarnings("serial")
public class send_servlet extends HttpServlet {
    public void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws IOException { 
        
        String destinatario_s="";
        String fullName_s="";
        String university_s="";
        String country_s="";
        String url_s="";
        String email_s="";
        String comments_s="";
      
        /*
        destinatario_s = "fexolisumari@gmail.com";
        fullName_s = "juanjo";
        university_s = "rex";
        country_s = "rexcity";
        url_s = "nono";
        comments_s = "soy un apestoso";
         */
        destinatario_s = "fexolisumari@gmail.com";
        fullName_s = req.getParameter("fullname").toString();
        university_s = req.getParameter("university").toString();
        country_s = req.getParameter("country").toString();
        url_s = req.getParameter("url").toString();
        //email_s = "juanjolopez28@gmail.com";
        email_s = req.getParameter("email");
        comments_s = req.getParameter("comentario").toString();
        
        send_email sendmsg = new send_email(destinatario_s,fullName_s,university_s,country_s,url_s,email_s,comments_s);  
        sendmsg.send_email_to_admin();
        
        resp.sendRedirect("/request.html");
        //resp.setContentType("text/plain");
        //resp.getWriter().println("Hello, world");
    }
}
