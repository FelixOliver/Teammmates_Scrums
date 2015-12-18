<%@page import="teammates.storage.api.CoursesDb"%>
<%@page import="teammates.storage.api.StudentsDb"%>
<%@page import="teammates.storage.api.InstructorsDb"%>
<%@page import="teammates.storage.api.CommentsDb"%>



  <link rel="stylesheet" href="http://maxcdn.bootstrapcdn.com/bootstrap/3.3.5/css/bootstrap.min.css">
  <link rel="stylesheet" type="text/css" href="http://jqplot.com/src/jquery.jqplot.css" />

	<script type="text/javascript" src="http://www.jqplot.com/src/jquery.jqplot.min.js"></script>
  
  <script src="https://ajax.googleapis.com/ajax/libs/jquery/1.9.1/jquery.min.js"></script>
  <script src="http://maxcdn.bootstrapcdn.com/bootstrap/3.3.5/js/bootstrap.min.js"></script>
  	<script src="http://jqplot.com/src/plugins/jqplot.pieRenderer.js"></script>
  

<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib tagdir="/WEB-INF/tags" prefix="t" %>
<%@ taglib tagdir="/WEB-INF/tags/instructor" prefix="ti" %>
<%@ taglib tagdir="/WEB-INF/tags/instructor/home" prefix="home" %>

<c:set var="jsIncludes">
    <script type="text/javascript" src="/js/instructor.js"></script>
    <script type="text/javascript" src="/js/instructorHome.js"></script>
    <script type="text/javascript" src="/js/ajaxResponseRate.js"></script>
    <script type="text/javascript" src="/js/instructorFeedbackAjaxRemindModal.js"></script>
    
</c:set>
<% CoursesDb cursos=new CoursesDb();
    int temp=cursos.gettotalcourses();   
   StudentsDb estudiantes=new StudentsDb();
   int temp1=estudiantes.gettotalstudent();
   InstructorsDb instructores=new InstructorsDb();
   int temp2=instructores.gettotalinstructores();
   CommentsDb comentarios=new CommentsDb();
   int temp3=comentarios.gettodocomentario();
%>

<div class="container" >
  <h2>Estadisticas</h2>
  <p>Estadisticas de Instructores,Cursos, Alumnos etx...</p>            
  <table class="table" style="width:40%">
    <thead>
      <tr>
        <th>Entidades</th>
        <th>Cantidad</th>
      </tr>
    </thead>
    <tbody>
      <tr class="success">
        <td>Instructores</td>
        <td><%out.print(temp2); %></td>
      </tr>
      <tr class="danger">
        <td>Cursos</td>
        <td><%out.print(temp); %></td>
      </tr>
      <tr class="info">
        <td>Alumnos</td>
        <td id="alu"> <%out.print(temp1); %></td>
      </tr>
            <tr class="lol">
        <td>Comentarios</td>
        <td id="alu"> <%out.print(temp3); %></td>
      </tr>
    </tbody>
  </table>
</div>

   


<ti:instructorPage pageTitle="TEAMMATES - Instructor" bodyTitle="Instructor Home" jsIncludes="${jsIncludes}">
    <home:search />
    <br />
    <t:statusMessage />
    <ti:remindParticularStudentsModal />
    <c:if test="${data.account.instructor}">
        <home:sort isSortButtonsDisabled="${data.sortingDisabled}"/>
        <br />
        <c:forEach items="${data.courseTables}" var="courseTable" varStatus="i">
            <home:coursePanel courseTable="${courseTable}" index="${i.index}" />
        </c:forEach>
        <ti:copyModal />
    </c:if>
</ti:instructorPage>