<%@page import="teammates.storage.api.InstructorsDb"%>
<%@page import="teammates.storage.api.AccountsDb"%>
<%@page import="com.google.appengine.api.users.User" %>
<%@page import="com.google.appengine.api.users.UserService" %>
<%@page import="com.google.appengine.api.users.UserServiceFactory" %>
<%@page import="java.util.*" %>
<%@ page import="java.text.SimpleDateFormat"%>

<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib tagdir="/WEB-INF/tags" prefix="t" %>
<%@ taglib tagdir="/WEB-INF/tags/instructor" prefix="ti" %>
 <%
   
        UserService userService = UserServiceFactory.getUserService();
        User user = userService.getCurrentUser();
        
        String emailUser="";
        emailUser = user.getEmail(); 
        boolean comprobador_instructor,comprobador_accounts;
        
        InstructorsDb inst = new InstructorsDb();
        AccountsDb accounts = new AccountsDb();
        
        //System.out.print(emailUser);
        
        comprobador_accounts = accounts.VerifyFalseOnGetInstructorAccountsForEmail(emailUser);
        comprobador_instructor = inst.comprobarInstructorExiste(emailUser);
       
        
        
        //int i=0;
        
        //out.println(emailUser);
        
        
        //CONDICIONALES
        /*
        
        if(comprobador_instructor){
         
                       
                
        	//out.println("<ti:instructorPage pageTitle=\"TEAMMATES - Instructor\" bodyTitle=\"\">");
        	//out.println("<br />");
        	//out.println("<t:statusMessage />");
        	//out.println("<br />");
         
                out.println("<div class=\"panel panel-primary panel-narrow\">");
                out.println("<div class=\"panel-heading\">");
                out.println("<h3>Confirm your Google account</h3>");
                out.println("</div>");
            
                out.println("<div class=\"panel-body\">");
                out.println("<p class=\"lead\">");
                out.println("You are currently logged in as <span><strong>${data.account.googleId}</strong></span>.");
                out.println("<br>If this is not you please <a");
                out.println("href=\"/logout.jsp\">log out</a> and re-login using your own Google account."); 
                out.println("<br>If this is you, please confirm below to complete your registration. <br>");
                
                out.println("<div class=\"align-center\">");
                    
                out.println("<a href=\"${data.confirmationLink}\" id=\"button_confirm\"");
                out.println("class=\"btn btn-success\">Yes, this is my account</a>"); 
                    
                out.println("<a href=\"/logout.jsp\" id=\"button_cancel\"");
                out.println("class=\"btn btn-danger\">No, this is not my account</a>");
                out.println("</div>");
                out.println("</p>");
                out.println("</div>");
                
               // out.println("</ti:instructorPage>");
              
        }
        else{
          
         
           if(comprobador_accounts){
               
               
               
               //out.println("<ti:instructorPage pageTitle=\"TEAMMATES - Instructor\" bodyTitle=\"\">");
               //out.println("<br />");
               //out.println("<t:statusMessage />");
               //out.println("<br />");
            
                   out.println("<div class=\"panel panel-primary panel-narrow\">");
                   out.println("<div class=\"panel-heading\">");
                   out.println("<h3>Confirm your Google account</h3>");
                   out.println("</div>");
               
                   out.println("<div class=\"panel-body\">");
                   out.println("<p class=\"lead\">");
                   out.println("You are currently logged in as <span><strong>${data.account.googleId}</strong></span>.");
                   out.println("<br>If this is not you please <a");
                   out.println("href=\"/logout.jsp\">log out</a> and re-login using your own Google account."); 
                   out.println("<br>If this is you, please confirm below to complete your registration. <br>");
                   
                   out.println("<div class=\"align-center\">");
                       
                   out.println("<a href=\"${data.confirmationLink}\" id=\"button_confirm\"");
                   out.println("class=\"btn btn-success\">Yes, this is my account</a>"); 
                       
                   out.println("<a href=\"/logout.jsp\" id=\"button_cancel\"");
                   out.println("class=\"btn btn-danger\">No, this is not my account</a>");
                   out.println("</div>");
                   out.println("</p>");
                   out.println("</div>");
                   
                  // out.println("</ti:instructorPage>");
                 
           }
           else{
        	//out.println("<ti:instructorPage pageTitle=\"TEAMMATES - Instructor\" bodyTitle=\"\">");
            //out.println("<br />");
            //out.println("<t:statusMessage />");
            //out.println("<br />");
         
            out.println("<div class=\"panel panel-primary panel-narrow\">");
            out.println("<div class=\"panel-heading\">");
            out.println("<h3>Invitation</h3>");
            out.println("</div>");
        
            out.println("<div class=\"panel-body\">");
            out.println("<p class=\"lead\">");
            out.println("You have been added to one course like a instructor.<br>We invite you to send your data information.");
            out.println("<br>");
            out.println("<div class=\"align-center\">");
                
            out.println("<a href=\"/request.html\" id=\"button_confirm\"");
            out.println("class=\"btn btn-success\"> Acept Invitation</a>"); 
            System.out.println("ingreso a 3 if");    
            
            out.println("</div>");
            out.println("</p>");
            out.println("</div>");
         
           }
        }
   */
    %>
    
       
<ti:instructorPage pageTitle="TEAMMATES - Instructor" bodyTitle="">
    <br />
    <t:statusMessage />
    <br />
    
   
    
    <!--
    
    <div class="panel panel-primary panel-narrow">
        <div class="panel-heading">
            <h3>Confirm your Google account</h3>
        </div>
        <div class="panel-body">
            <p class="lead">
               You are currently logged in as <span><strong>${data.account.googleId}</strong></span>.
                <br>If this is not you please <a
                    href="/logout.jsp">log out</a> and re-login using your own Google account. 
                    <br>If this is you, please confirm below to complete your registration. <br>
            <div class="align-center">
                <a href="${data.confirmationLink}" id="button_confirm"
                    class="btn btn-success">Yes, this is my account</a> <a href="/logout.jsp" id="button_cancel"
                    class="btn btn-danger">No, this is not my account</a>
            </div>
            </p>
        </div>
    </div>
    
   -->
    
</ti:instructorPage>

 