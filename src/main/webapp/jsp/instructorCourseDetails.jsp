<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib tagdir="/WEB-INF/tags" prefix="t" %>
<%@ taglib tagdir="/WEB-INF/tags/instructor" prefix="ti" %>
<%@ taglib tagdir="/WEB-INF/tags/instructor/course" prefix="course" %>
<c:set var="jsIncludes">
    <script type="text/javascript" src="/js/instructor.js"></script>
    <script type="text/javascript" src="/js/instructorCourseDetails.js"></script>
    <script type="text/javascript" src="/js/contextualcomments.js"></script>
</c:set>

<ti:instructorPage pageTitle="TEAMMATES - Instructor" bodyTitle="Course Details" jsIncludes="${jsIncludes}">
    <course:courseInformationContainer courseDetails="${data.courseDetails}" 
                                       instructors="${data.instructors}" 
                                       giveCommentButton="${data.giveCommentButton}" 
                                       courseRemindButton="${data.courseRemindButton}"/>
    <course:commentArea courseId="${data.courseDetails.course.id}"/>
    <br>
    <t:statusMessage />
    <br>
    <ti:studentList courseId="${data.courseDetails.course.id}" courseIndex="${0}" hasSection="${data.hasSection}" sections="${data.sections}"
                    fromCourseDetailsPage="${true}"/>
    <br>
    <br>
    <br>
</ti:instructorPage>
