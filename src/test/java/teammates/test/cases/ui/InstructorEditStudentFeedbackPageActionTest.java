package teammates.test.cases.ui;

import static org.junit.Assert.assertEquals;
import static org.testng.AssertJUnit.assertEquals;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import teammates.common.datatransfer.DataBundle;
import teammates.common.datatransfer.InstructorAttributes;
import teammates.common.datatransfer.StudentAttributes;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.exception.UnauthorizedAccessException;
import teammates.common.util.Const;
import teammates.ui.controller.InstructorEditStudentFeedbackPageAction;
import teammates.ui.controller.ShowPageResult;

public class InstructorEditStudentFeedbackPageActionTest extends BaseActionTest {
    private static DataBundle dataBundle;
    
    @BeforeClass
    public static void classSetUp() throws Exception {
        printTestClassHeader();
        dataBundle = loadDataBundle("/InstructorEditStudentFeedbackPageTest.json");
        removeAndRestoreDatastoreFromJson("/InstructorEditStudentFeedbackPageTest.json");
        
        uri = Const.ActionURIs.INSTRUCTOR_EDIT_STUDENT_FEEDBACK_PAGE;
    }
    
    @Test
    public void testExecuteAndPostProcess() throws Exception {
        InstructorAttributes instructor = dataBundle.instructors.get("IESFPTCourseinstr");
        InstructorAttributes instructorHelper = dataBundle.instructors.get("IESFPTCoursehelper1");
        String idOfInstructor = instructor.googleId;
        String idOfInstructorHelper = instructorHelper.googleId;
        StudentAttributes student = dataBundle.students.get("student1InCourse1");

        gaeSimulation.loginAsInstructor(idOfInstructor);

        ______TS("typical success case");

        String feedbackSessionName = "First feedback session";
        String courseId = student.course;
        String moderatedStudentEmail = student.email;

        String[] submissionParams = new String[]{
                Const.ParamsNames.COURSE_ID, courseId,
                Const.ParamsNames.FEEDBACK_SESSION_NAME, feedbackSessionName,
                Const.ParamsNames.FEEDBACK_SESSION_MODERATED_STUDENT, moderatedStudentEmail
        };

        InstructorEditStudentFeedbackPageAction editPageAction = getAction(submissionParams);
        ShowPageResult showPageResult = (ShowPageResult) editPageAction.executeAndPostProcess();

        assertEquals(Const.ViewURIs.STUDENT_FEEDBACK_SUBMISSION_EDIT +
                "?error=false" +
                "&user=" + idOfInstructor,
                showPageResult.getDestinationWithParams());
        assertEquals("", showPageResult.getStatusMessage());

        assertEquals("TEAMMATESLOG|||instructorEditStudentFeedbackPage|||instructorEditStudentFeedbackPage" +
                "|||true|||Instructor|||IESFPTCourseinstr|||IESFPTCourseinstr|||IESFPTCourseintr@course1.tmt|||" +
                "Moderating feedback session for student (" + student.email + ")<br>" +
                "Session Name: First feedback session<br>Course ID: IESFPTCourse|||" +
                "/page/instructorEditStudentFeedbackPage",
                editPageAction.getLogMessage());
        
        ______TS("success: another feedback");
        
        feedbackSessionName = "Another feedback session";
        courseId = student.course;
        moderatedStudentEmail = student.email;

        submissionParams = new String[]{
                Const.ParamsNames.COURSE_ID, courseId,
                Const.ParamsNames.FEEDBACK_SESSION_NAME, feedbackSessionName,
                Const.ParamsNames.FEEDBACK_SESSION_MODERATED_STUDENT, moderatedStudentEmail
        };

        editPageAction = getAction(submissionParams);
        showPageResult = (ShowPageResult) editPageAction.executeAndPostProcess();
        
        ______TS("success case: closed session");

        feedbackSessionName = "Closed feedback session";
        courseId = student.course;
        moderatedStudentEmail = student.email;

        submissionParams = new String[]{
                Const.ParamsNames.COURSE_ID, courseId,
                Const.ParamsNames.FEEDBACK_SESSION_NAME, feedbackSessionName,
                Const.ParamsNames.FEEDBACK_SESSION_MODERATED_STUDENT, moderatedStudentEmail
        };

        editPageAction = getAction(submissionParams);
        showPageResult = (ShowPageResult) editPageAction.executeAndPostProcess();

        assertEquals(Const.ViewURIs.STUDENT_FEEDBACK_SUBMISSION_EDIT +
                "?error=false" +
                "&user="+ idOfInstructor,
                showPageResult.getDestinationWithParams());
        assertEquals("", showPageResult.getStatusMessage());
        

        gaeSimulation.loginAsInstructor(idOfInstructor);
        ______TS("success case: moderate team");

        feedbackSessionName = "Closed feedback session";
        courseId = student.course;
        String moderatedStudentTeam = student.team;

        submissionParams = new String[] {
                Const.ParamsNames.COURSE_ID, courseId,
                Const.ParamsNames.FEEDBACK_SESSION_NAME, feedbackSessionName,
                Const.ParamsNames.FEEDBACK_SESSION_MODERATED_STUDENT, moderatedStudentTeam
        };

        editPageAction = getAction(submissionParams);
        showPageResult = (ShowPageResult) editPageAction.executeAndPostProcess();

        assertEquals(Const.ViewURIs.STUDENT_FEEDBACK_SUBMISSION_EDIT 
                + "?error=false"
                + "&user="+ idOfInstructor
                , showPageResult.getDestinationWithParams());
        assertEquals("", showPageResult.getStatusMessage());

        assertEquals("TEAMMATESLOG|||instructorEditStudentFeedbackPage|||instructorEditStudentFeedbackPage"
                + "|||true|||Instructor|||IESFPTCourseinstr|||IESFPTCourseinstr|||IESFPTCourseintr@course1.tmt|||"
                + "Moderating feedback session for student (" + student.email + ")<br>"
                + "Session Name: Closed feedback session<br>Course ID: IESFPTCourse|||"
                + "/page/instructorEditStudentFeedbackPage"
                , editPageAction.getLogMessage());
        
        gaeSimulation.loginAsInstructor(idOfInstructorHelper);
        
        ______TS("failure: does not have privilege");
        
        feedbackSessionName = "First feedback session";
        courseId = "IESFPTCourse";
        moderatedStudentEmail = student.email;
        
        submissionParams = new String[]{
                Const.ParamsNames.COURSE_ID, courseId,
                Const.ParamsNames.FEEDBACK_SESSION_NAME, feedbackSessionName,
                Const.ParamsNames.FEEDBACK_SESSION_MODERATED_STUDENT, moderatedStudentEmail
        };
        
        try {
            editPageAction = getAction(submissionParams);
            showPageResult = (ShowPageResult) editPageAction.executeAndPostProcess();
        } catch (UnauthorizedAccessException e) {
            assertEquals("Feedback session [First feedback session] is not accessible to instructor [" + 
                    instructorHelper.email + "] for privilege [canmodifysessioncommentinsection] on section [Section 1]", e.getMessage());
        }
        
        gaeSimulation.loginAsInstructor(idOfInstructor);

        ______TS("failure: non-existent moderatedstudent email");
        
        moderatedStudentEmail = "non-exIstentEmail@gsail.tmt";

        submissionParams = new String[]{
                Const.ParamsNames.COURSE_ID, courseId,
                Const.ParamsNames.FEEDBACK_SESSION_NAME, feedbackSessionName,
                Const.ParamsNames.FEEDBACK_SESSION_MODERATED_STUDENT, moderatedStudentEmail
        };

        try {
            editPageAction = getAction(submissionParams);
            showPageResult = (ShowPageResult) editPageAction.executeAndPostProcess();
            signalFailureToDetectException();
        } catch (EntityDoesNotExistException edne) {
            assertEquals("An entity with the identifier "
                            + moderatedStudentEmail + " does not exist in " + courseId
                            + ".", 
                         edne.getMessage());

        }
    }
            
    private InstructorEditStudentFeedbackPageAction getAction(String... params) throws Exception {
        return (InstructorEditStudentFeedbackPageAction) gaeSimulation.getActionObject(uri, params);
    }
}
