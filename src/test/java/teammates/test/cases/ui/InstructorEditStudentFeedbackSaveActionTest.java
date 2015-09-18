package teammates.test.cases.ui;

import static org.testng.AssertJUnit.assertEquals;
import static org.testng.AssertJUnit.assertFalse;
import static org.testng.AssertJUnit.assertNotNull;
import static org.testng.AssertJUnit.assertNull;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import teammates.common.datatransfer.DataBundle;
import teammates.common.datatransfer.FeedbackQuestionAttributes;
import teammates.common.datatransfer.FeedbackResponseAttributes;
import teammates.common.datatransfer.InstructorAttributes;
import teammates.common.exception.NullPostParameterException;
import teammates.common.exception.UnauthorizedAccessException;
import teammates.common.util.Const;
import teammates.storage.api.FeedbackQuestionsDb;
import teammates.storage.api.FeedbackResponsesDb;
import teammates.ui.controller.InstructorEditStudentFeedbackSaveAction;
import teammates.ui.controller.RedirectResult;

public class InstructorEditStudentFeedbackSaveActionTest extends BaseActionTest {

    private static DataBundle dataBundle;
    
    @BeforeClass
    public static void classSetUp() throws Exception {
        printTestClassHeader();
        dataBundle = loadDataBundle("/InstructorEditStudentFeedbackPageTest.json");
        removeAndRestoreDatastoreFromJson("/InstructorEditStudentFeedbackPageTest.json");
        
        uri = Const.ActionURIs.INSTRUCTOR_EDIT_STUDENT_FEEDBACK_SAVE;
    }
    
    @Test
    public void testExecuteAndPostProcess() throws Exception {
        testModifyResponses();
        
        testIncorrectParameters();

        testDifferentPrivileges();
        
        testSubmitResponseForInvalidQuestion();
        testClosedSession();
    }
    
    private void testModifyResponses() throws Exception {
        ______TS("edit existing answer");
        
        FeedbackQuestionsDb fqDb = new FeedbackQuestionsDb();
        FeedbackQuestionAttributes fq = fqDb.getFeedbackQuestion("First feedback session", "IESFPTCourse", 1);
        assertNotNull("Feedback question not found in database", fq);
        
        FeedbackResponsesDb frDb = new FeedbackResponsesDb();
        FeedbackResponseAttributes fr = dataBundle.feedbackResponses.get("response1ForQ1");
        fr = frDb.getFeedbackResponse(fq.getId(), fr.giverEmail, fr.recipientEmail); // necessary to get the correct responseId
        assertNotNull("Feedback response not found in database", fr);
        
        InstructorAttributes instructor = dataBundle.instructors.get("IESFPTCourseinstr");
        
        gaeSimulation.loginAsInstructor(instructor.googleId);
        
        String moderatedStudentEmail = "student1InIESFPTCourse@gmail.tmt";
        
        String[] submissionParams = new String[]{
                Const.ParamsNames.FEEDBACK_QUESTION_RESPONSETOTAL + "-1", "1",
                Const.ParamsNames.FEEDBACK_RESPONSE_ID + "-1-0", fr.getId(),
                Const.ParamsNames.FEEDBACK_SESSION_NAME, fr.feedbackSessionName,
                Const.ParamsNames.COURSE_ID, fr.courseId,
                Const.ParamsNames.FEEDBACK_QUESTION_ID + "-1", fr.feedbackQuestionId,
                Const.ParamsNames.FEEDBACK_RESPONSE_RECIPIENT + "-1-0", fr.recipientEmail,
                Const.ParamsNames.FEEDBACK_QUESTION_TYPE + "-1", fr.feedbackQuestionType.toString(),
                Const.ParamsNames.FEEDBACK_RESPONSE_TEXT + "-1-0", "Edited" + fr.getResponseDetails().getAnswerString(),
                Const.ParamsNames.FEEDBACK_SESSION_MODERATED_STUDENT, moderatedStudentEmail
        };
        
        InstructorEditStudentFeedbackSaveAction a = getAction(submissionParams);
        RedirectResult r = (RedirectResult) a.executeAndPostProcess();
        
        assertFalse(r.isError);
        assertEquals("All responses submitted succesfully!", r.getStatusMessage());
        assertEquals("/page/instructorEditStudentFeedbackPage" + 
                     "?error=false&moderatedstudent=student1InIESFPTCourse%40gmail.tmt" + 
                     "&user=IESFPTCourseinstr&courseid=IESFPTCourse" + 
                     "&fsname=First+feedback+session",
                     r.getDestinationWithParams());
        assertNotNull(frDb.getFeedbackResponse(fq.getId(), fr.giverEmail, fr.recipientEmail));
        
        ______TS("deleted response");
        
        submissionParams = new String[]{
                Const.ParamsNames.FEEDBACK_QUESTION_RESPONSETOTAL + "-1", "1",
                Const.ParamsNames.FEEDBACK_RESPONSE_ID + "-1-0", fr.getId(),
                Const.ParamsNames.FEEDBACK_SESSION_NAME, fr.feedbackSessionName,
                Const.ParamsNames.COURSE_ID, fr.courseId,
                Const.ParamsNames.FEEDBACK_QUESTION_ID + "-1", fr.feedbackQuestionId,
                Const.ParamsNames.FEEDBACK_RESPONSE_RECIPIENT + "-1-0", fr.recipientEmail,
                Const.ParamsNames.FEEDBACK_QUESTION_TYPE + "-1", fr.feedbackQuestionType.toString(),
                Const.ParamsNames.FEEDBACK_RESPONSE_TEXT + "-1-0", "",
                Const.ParamsNames.FEEDBACK_SESSION_MODERATED_STUDENT, moderatedStudentEmail
        };
        
        a = getAction(submissionParams);
        r = (RedirectResult) a.executeAndPostProcess();
        
        assertFalse(r.isError);
        assertEquals("All responses submitted succesfully!", r.getStatusMessage());
        assertEquals("/page/instructorEditStudentFeedbackPage" + 
                     "?error=false&moderatedstudent=student1InIESFPTCourse%40gmail.tmt" + 
                     "&user=IESFPTCourseinstr&courseid=IESFPTCourse" + 
                     "&fsname=First+feedback+session",
                     r.getDestinationWithParams());
        assertNull(frDb.getFeedbackResponse(fq.getId(), fr.giverEmail, fr.recipientEmail));
           
        ______TS("skipped question");
        
        submissionParams = new String[]{
                Const.ParamsNames.FEEDBACK_QUESTION_RESPONSETOTAL + "-1", "1",
                Const.ParamsNames.FEEDBACK_SESSION_NAME, fr.feedbackSessionName,
                Const.ParamsNames.COURSE_ID, fr.courseId,
                Const.ParamsNames.FEEDBACK_QUESTION_ID + "-1", fr.feedbackQuestionId,
                Const.ParamsNames.FEEDBACK_RESPONSE_RECIPIENT + "-1-0", fr.recipientEmail,
                Const.ParamsNames.FEEDBACK_QUESTION_TYPE + "-1", fr.feedbackQuestionType.toString(),
                Const.ParamsNames.FEEDBACK_RESPONSE_TEXT + "-1-0", "" ,
                Const.ParamsNames.FEEDBACK_SESSION_MODERATED_STUDENT, moderatedStudentEmail
        };
        
        a = getAction(submissionParams);
        r = (RedirectResult) a.executeAndPostProcess();
        
        assertFalse(r.isError);
        assertEquals("All responses submitted succesfully!", r.getStatusMessage());
        assertEquals("/page/instructorEditStudentFeedbackPage" + 
                     "?error=false&moderatedstudent=student1InIESFPTCourse%40gmail.tmt" + 
                     "&user=IESFPTCourseinstr&courseid=IESFPTCourse" + 
                     "&fsname=First+feedback+session",
                     r.getDestinationWithParams());
        assertNull(frDb.getFeedbackResponse(fq.getId(), fr.giverEmail, fr.recipientEmail));
        
        ______TS("new response");
        
        submissionParams = new String[]{
                Const.ParamsNames.FEEDBACK_QUESTION_RESPONSETOTAL + "-1", "1",
                Const.ParamsNames.FEEDBACK_SESSION_NAME, fr.feedbackSessionName,
                Const.ParamsNames.COURSE_ID, fr.courseId,
                Const.ParamsNames.FEEDBACK_QUESTION_ID + "-1", fr.feedbackQuestionId,
                Const.ParamsNames.FEEDBACK_RESPONSE_RECIPIENT + "-1-0", fr.recipientEmail,
                Const.ParamsNames.FEEDBACK_QUESTION_TYPE + "-1", fr.feedbackQuestionType.toString(),
                Const.ParamsNames.FEEDBACK_RESPONSE_TEXT + "-1-0", "New " + fr.getResponseDetails().getAnswerString(), 
                Const.ParamsNames.FEEDBACK_SESSION_MODERATED_STUDENT, moderatedStudentEmail
        };
        
        a = getAction(submissionParams);
        r = (RedirectResult) a.executeAndPostProcess();
        
        assertFalse(r.isError);
        assertEquals("All responses submitted succesfully!", r.getStatusMessage());
        assertEquals("/page/instructorEditStudentFeedbackPage" + 
                     "?error=false&moderatedstudent=student1InIESFPTCourse%40gmail.tmt" + 
                     "&user=IESFPTCourseinstr&courseid=IESFPTCourse" + 
                     "&fsname=First+feedback+session",
                     r.getDestinationWithParams());
        assertNotNull(frDb.getFeedbackResponse(fq.getId(), fr.giverEmail, fr.recipientEmail));
    }
    
    private void testIncorrectParameters() throws Exception {
        ______TS("Unsuccessful case: test empty feedback session name parameter");
        
        FeedbackQuestionsDb fqDb = new FeedbackQuestionsDb();
        FeedbackQuestionAttributes fq = fqDb.getFeedbackQuestion("First feedback session", "IESFPTCourse", 1);
        assertNotNull("Feedback question not found in database", fq);
        
        FeedbackResponsesDb frDb = new FeedbackResponsesDb();
        FeedbackResponseAttributes fr = dataBundle.feedbackResponses.get("response1ForQ1");
        fr = frDb.getFeedbackResponse(fq.getId(), fr.giverEmail, fr.recipientEmail); // necessary to get the correct responseId
        assertNotNull("Feedback response not found in database", fr);
        
        String moderatedStudentEmail = "student1InIESFPTCourse@gmail.tmt";
        String[] submissionParams = new String[]{
                Const.ParamsNames.COURSE_ID, dataBundle.feedbackResponses.get("response1ForQ1").courseId,
                Const.ParamsNames.FEEDBACK_SESSION_MODERATED_STUDENT, moderatedStudentEmail
        };
        
        InstructorEditStudentFeedbackSaveAction a;
        @SuppressWarnings("unused")
        // unused but still needed to allow detection of exception
        RedirectResult r;
        
        try {
            a = getAction(submissionParams);
            r = (RedirectResult) a.executeAndPostProcess();
            signalFailureToDetectException("Did not detect that parameters are null.");
        } catch (NullPostParameterException e) {
            assertEquals(String.format(Const.StatusCodes.NULL_POST_PARAMETER, 
                    Const.ParamsNames.FEEDBACK_SESSION_NAME), e.getMessage());
        }
                
        ______TS("Unsuccessful case: test empty course id parameter");
        
        submissionParams = new String[]{
                Const.ParamsNames.FEEDBACK_SESSION_NAME, dataBundle.feedbackResponses.get("response1ForQ1").feedbackSessionName, 
                Const.ParamsNames.FEEDBACK_SESSION_MODERATED_STUDENT, moderatedStudentEmail
        };
        
        try {
            a = getAction(submissionParams);
            r = (RedirectResult) a.executeAndPostProcess();
            signalFailureToDetectException("Did not detect that parameters are null.");
        } catch (NullPostParameterException e) {
            assertEquals(String.format(Const.StatusCodes.NULL_POST_PARAMETER, 
                    Const.ParamsNames.COURSE_ID), e.getMessage());
        }
        
        ______TS("Unsuccessful case: test no moderated student parameter");
        
        submissionParams = new String[]{
                Const.ParamsNames.FEEDBACK_QUESTION_RESPONSETOTAL + "-1", "1",
                Const.ParamsNames.FEEDBACK_RESPONSE_ID + "-1-0", fr.getId(),
                Const.ParamsNames.FEEDBACK_SESSION_NAME, fr.feedbackSessionName,
                Const.ParamsNames.COURSE_ID, fr.courseId,
                Const.ParamsNames.FEEDBACK_QUESTION_ID + "-1", fr.feedbackQuestionId,
                Const.ParamsNames.FEEDBACK_RESPONSE_RECIPIENT + "-1-0", "",
                Const.ParamsNames.FEEDBACK_QUESTION_TYPE + "-1", fr.feedbackQuestionType.toString(),
                Const.ParamsNames.FEEDBACK_RESPONSE_TEXT + "-1-0", "Edited" + fr.getResponseDetails().getAnswerString(),
        };
        
        try {
            a = getAction(submissionParams);
            r = (RedirectResult) a.executeAndPostProcess();
            signalFailureToDetectException("Did not detect that parameters are null.");
        } catch (NullPostParameterException e) {
            assertEquals(String.format(Const.StatusCodes.NULL_POST_PARAMETER, 
                    Const.ParamsNames.FEEDBACK_SESSION_MODERATED_STUDENT), e.getMessage());
        }
        
    }
    
    private void testDifferentPrivileges() throws Exception {
        ______TS("Unsuccessful case: insufficient privileges");
        
        FeedbackQuestionsDb fqDb = new FeedbackQuestionsDb();
        FeedbackQuestionAttributes fq = fqDb.getFeedbackQuestion("First feedback session", "IESFPTCourse", 1);
        assertNotNull("Feedback question not found in database", fq);
        
        FeedbackResponsesDb frDb = new FeedbackResponsesDb();
        FeedbackResponseAttributes fr = dataBundle.feedbackResponses.get("response1ForQ1");
        fr = frDb.getFeedbackResponse(fq.getId(), fr.giverEmail, fr.recipientEmail); // necessary to get the correct responseId
        assertNotNull("Feedback response not found in database", fr);
        
        String moderatedStudentEmail = "student1InIESFPTCourse@gmail.tmt";
        
        InstructorAttributes instructorHelper = dataBundle.instructors.get("IESFPTCoursehelper1");
        gaeSimulation.loginAsInstructor(instructorHelper.googleId);
        
        String[] submissionParams = new String[]{
                Const.ParamsNames.FEEDBACK_QUESTION_RESPONSETOTAL + "-1", "1",
                Const.ParamsNames.FEEDBACK_RESPONSE_ID + "-1-0", fr.getId(),
                Const.ParamsNames.FEEDBACK_SESSION_NAME, fr.feedbackSessionName,
                Const.ParamsNames.COURSE_ID, fr.courseId,
                Const.ParamsNames.FEEDBACK_QUESTION_ID + "-1", fr.feedbackQuestionId,
                Const.ParamsNames.FEEDBACK_RESPONSE_RECIPIENT + "-1-0", fr.recipientEmail,
                Const.ParamsNames.FEEDBACK_QUESTION_TYPE + "-1", fr.feedbackQuestionType.toString(),
                Const.ParamsNames.FEEDBACK_RESPONSE_TEXT + "-1-0", "Edited" + fr.getResponseDetails().getAnswerString(),
                Const.ParamsNames.FEEDBACK_SESSION_MODERATED_STUDENT, moderatedStudentEmail
        };
        
        InstructorEditStudentFeedbackSaveAction a;
        RedirectResult r;
        
        try {
            a = getAction(submissionParams);
            r = (RedirectResult) a.executeAndPostProcess();
        } catch (UnauthorizedAccessException e) {
            assertEquals("Feedback session [First feedback session] is not accessible to instructor [" + 
                    instructorHelper.email + "] for privilege [canmodifysessioncommentinsection] on section [Section 1]", e.getMessage());
        }
        
        ______TS("Unsuccessful case: sufficient privileges only for a section, but attempted to modify another section");
        
        instructorHelper = dataBundle.instructors.get("IESFPTCoursehelper1");
        gaeSimulation.loginAsInstructor(instructorHelper.googleId);
        
        submissionParams = new String[]{
                Const.ParamsNames.FEEDBACK_QUESTION_RESPONSETOTAL + "-1", "1",
                Const.ParamsNames.FEEDBACK_RESPONSE_ID + "-1-0", fr.getId(),
                Const.ParamsNames.FEEDBACK_SESSION_NAME, fr.feedbackSessionName,
                Const.ParamsNames.COURSE_ID, fr.courseId,
                Const.ParamsNames.FEEDBACK_QUESTION_ID + "-1", fr.feedbackQuestionId,
                Const.ParamsNames.FEEDBACK_RESPONSE_RECIPIENT + "-1-0", fr.recipientEmail,
                Const.ParamsNames.FEEDBACK_QUESTION_TYPE + "-1", fr.feedbackQuestionType.toString(),
                Const.ParamsNames.FEEDBACK_RESPONSE_TEXT + "-1-0", "Edited" + fr.getResponseDetails().getAnswerString(),
                Const.ParamsNames.FEEDBACK_SESSION_MODERATED_STUDENT, moderatedStudentEmail
        };
        
        try {
            a = getAction(submissionParams);
            r = (RedirectResult) a.executeAndPostProcess();
        } catch (UnauthorizedAccessException e) {
            assertEquals("Feedback session [First feedback session] is not accessible to instructor [" + 
                    instructorHelper.email + "] for privilege [canmodifysessioncommentinsection] on section [Section 1]", e.getMessage());
        }
       
        ______TS("Successful case: sufficient privileges only for a section");
        
        moderatedStudentEmail = "student2InIESFPTCourse@gmail.tmt";
        instructorHelper = dataBundle.instructors.get("IESFPTCoursehelper1");
        gaeSimulation.loginAsInstructor(instructorHelper.googleId);
        
        frDb = new FeedbackResponsesDb();
        fr = dataBundle.feedbackResponses.get("response2ForQ1");
        fr = frDb.getFeedbackResponse(fq.getId(), fr.giverEmail, fr.recipientEmail); // necessary to get the correct responseId
        assertNotNull("Feedback response not found in database", fr);
        
        submissionParams = new String[]{
                Const.ParamsNames.FEEDBACK_QUESTION_RESPONSETOTAL + "-1", "1",
                Const.ParamsNames.FEEDBACK_RESPONSE_ID + "-1-0", fr.getId(),
                Const.ParamsNames.FEEDBACK_SESSION_NAME, fr.feedbackSessionName,
                Const.ParamsNames.COURSE_ID, fr.courseId,
                Const.ParamsNames.FEEDBACK_QUESTION_ID + "-1", fr.feedbackQuestionId,
                Const.ParamsNames.FEEDBACK_RESPONSE_RECIPIENT + "-1-0", fr.recipientEmail,
                Const.ParamsNames.FEEDBACK_QUESTION_TYPE + "-1", fr.feedbackQuestionType.toString(),
                Const.ParamsNames.FEEDBACK_RESPONSE_TEXT + "-1-0", "Edited" + fr.getResponseDetails().getAnswerString(),
                Const.ParamsNames.FEEDBACK_SESSION_MODERATED_STUDENT, moderatedStudentEmail
        };
        
        a = getAction(submissionParams);
        r = (RedirectResult) a.executeAndPostProcess();
        
        assertFalse(r.isError);
        assertEquals("All responses submitted succesfully!", r.getStatusMessage());
        assertEquals("/page/instructorEditStudentFeedbackPage" + 
                     "?error=false&moderatedstudent=student2InIESFPTCourse%40gmail.tmt" + 
                     "&user=IESFPTCoursehelper1&courseid=IESFPTCourse" + 
                     "&fsname=First+feedback+session",
                     r.getDestinationWithParams());
        assertNotNull(frDb.getFeedbackResponse(fq.getId(), fr.giverEmail, fr.recipientEmail));
        
        
        ______TS("failure case: privileges sufficient for section BUT insufficient for a session");
        moderatedStudentEmail = "student2InIESFPTCourse@gmail.tmt";
        InstructorAttributes instructorHelper2 = dataBundle.instructors.get("IESFPTCoursehelper2");
        gaeSimulation.loginAsInstructor(instructorHelper2.googleId);
        
        frDb = new FeedbackResponsesDb();
        fr = dataBundle.feedbackResponses.get("response2ForQ1");
        fr = frDb.getFeedbackResponse(fq.getId(), fr.giverEmail, fr.recipientEmail); //necessary to get the correct responseId
        assertNotNull("Feedback response not found in database", fr);
        
        submissionParams = new String[]{
                Const.ParamsNames.FEEDBACK_QUESTION_RESPONSETOTAL + "-1", "1",
                Const.ParamsNames.FEEDBACK_RESPONSE_ID + "-1-0", fr.getId(),
                Const.ParamsNames.FEEDBACK_SESSION_NAME, fr.feedbackSessionName,
                Const.ParamsNames.COURSE_ID, fr.courseId,
                Const.ParamsNames.FEEDBACK_QUESTION_ID + "-1", fr.feedbackQuestionId,
                Const.ParamsNames.FEEDBACK_RESPONSE_RECIPIENT + "-1-0", fr.recipientEmail,
                Const.ParamsNames.FEEDBACK_QUESTION_TYPE + "-1", fr.feedbackQuestionType.toString(),
                Const.ParamsNames.FEEDBACK_RESPONSE_TEXT + "-1-0", "Edited" + fr.getResponseDetails().getAnswerString(),
                Const.ParamsNames.FEEDBACK_SESSION_MODERATED_STUDENT, moderatedStudentEmail
        };
        
        try {
            a = getAction(submissionParams);
            r = (RedirectResult) a.executeAndPostProcess();
        } catch (UnauthorizedAccessException e) {
            assertEquals("Feedback session [First feedback session] is not accessible to instructor [" + 
                    instructorHelper2.email + "] for privilege [canmodifysessioncommentinsection] on section [Section 2]", e.getMessage());
        }
        
        ______TS("Successful case: sufficient for section, although insufficient for another session");
        
        frDb = new FeedbackResponsesDb();
        fr = dataBundle.feedbackResponses.get("response2ForS2Q1");
        fq = fqDb.getFeedbackQuestion("Another feedback session", "IESFPTCourse", 1);
        
        fr = frDb.getFeedbackResponse(fq.getId(), fr.giverEmail, fr.recipientEmail); // necessary to get the correct responseId
        assertNotNull("Feedback response not found in database", fr);
        
        submissionParams = new String[]{
                Const.ParamsNames.FEEDBACK_QUESTION_RESPONSETOTAL + "-1", "1",
                Const.ParamsNames.FEEDBACK_RESPONSE_ID + "-1-0", fr.getId(),
                Const.ParamsNames.FEEDBACK_SESSION_NAME, fr.feedbackSessionName,
                Const.ParamsNames.COURSE_ID, fr.courseId,
                Const.ParamsNames.FEEDBACK_QUESTION_ID + "-1", fr.feedbackQuestionId,
                Const.ParamsNames.FEEDBACK_RESPONSE_RECIPIENT + "-1-0", fr.recipientEmail,
                Const.ParamsNames.FEEDBACK_QUESTION_TYPE + "-1", fr.feedbackQuestionType.toString(),
                Const.ParamsNames.FEEDBACK_RESPONSE_TEXT + "-1-0", "Edited" + fr.getResponseDetails().getAnswerString(),
                Const.ParamsNames.FEEDBACK_SESSION_MODERATED_STUDENT, moderatedStudentEmail
        };
        
        a = getAction(submissionParams);
        r = (RedirectResult) a.executeAndPostProcess();
        
        assertFalse(r.isError);
        assertEquals("All responses submitted succesfully!", r.getStatusMessage());
        assertEquals("/page/instructorEditStudentFeedbackPage" + 
                     "?error=false&moderatedstudent=student2InIESFPTCourse%40gmail.tmt" + 
                     "&user=IESFPTCoursehelper2&courseid=IESFPTCourse" + 
                     "&fsname=Another+feedback+session",
                     r.getDestinationWithParams());
        assertNotNull(frDb.getFeedbackResponse(fq.getId(), fr.giverEmail, fr.recipientEmail));
        
        ______TS("Success case: insufficient for section, BUT sufficient for a session");
        
        moderatedStudentEmail = "student2InIESFPTCourse@gmail.tmt";
        InstructorAttributes instructorHelper3 = dataBundle.instructors.get("IESFPTCoursehelper3");
        gaeSimulation.loginAsInstructor(instructorHelper3.googleId);
        
        frDb = new FeedbackResponsesDb();
        fr = dataBundle.feedbackResponses.get("response2ForQ1");
        fq = fqDb.getFeedbackQuestion("First feedback session", "IESFPTCourse", 1);
        
        fr = frDb.getFeedbackResponse(fq.getId(), fr.giverEmail, fr.recipientEmail); // necessary to get the correct responseId
        assertNotNull("Feedback response not found in database", fr);
        
        submissionParams = new String[]{
                Const.ParamsNames.FEEDBACK_QUESTION_RESPONSETOTAL + "-1", "1",
                Const.ParamsNames.FEEDBACK_RESPONSE_ID + "-1-0", fr.getId(),
                Const.ParamsNames.FEEDBACK_SESSION_NAME, fr.feedbackSessionName,
                Const.ParamsNames.COURSE_ID, fr.courseId,
                Const.ParamsNames.FEEDBACK_QUESTION_ID + "-1", fr.feedbackQuestionId,
                Const.ParamsNames.FEEDBACK_RESPONSE_RECIPIENT + "-1-0", fr.recipientEmail,
                Const.ParamsNames.FEEDBACK_QUESTION_TYPE + "-1", fr.feedbackQuestionType.toString(),
                Const.ParamsNames.FEEDBACK_RESPONSE_TEXT + "-1-0", "Edited" + fr.getResponseDetails().getAnswerString(),
                Const.ParamsNames.FEEDBACK_SESSION_MODERATED_STUDENT, moderatedStudentEmail
        };
        
        a = getAction(submissionParams);
        r = (RedirectResult) a.executeAndPostProcess();
        
        assertFalse(r.isError);
        assertEquals("All responses submitted succesfully!", r.getStatusMessage());
        assertEquals("/page/instructorEditStudentFeedbackPage" + 
                     "?error=false&moderatedstudent=student2InIESFPTCourse%40gmail.tmt" + 
                     "&user=IESFPTCoursehelper3&courseid=IESFPTCourse" + 
                     "&fsname=First+feedback+session",
                     r.getDestinationWithParams());
        assertNotNull(frDb.getFeedbackResponse(fq.getId(), fr.giverEmail, fr.recipientEmail));
        
        ______TS("Failure case: insufficient for section, although sufficient for another session");
        
        moderatedStudentEmail = "student2InIESFPTCourse@gmail.tmt";
        
        frDb = new FeedbackResponsesDb();
        fr = dataBundle.feedbackResponses.get("response2ForS2Q1");
        fq = fqDb.getFeedbackQuestion("Another feedback session", "IESFPTCourse", 1);
        
        fr = frDb.getFeedbackResponse(fq.getId(), fr.giverEmail, fr.recipientEmail); // necessary to get the correct responseId
        assertNotNull("Feedback response not found in database", fr);
        
        submissionParams = new String[]{
                Const.ParamsNames.FEEDBACK_QUESTION_RESPONSETOTAL + "-1", "1",
                Const.ParamsNames.FEEDBACK_RESPONSE_ID + "-1-0", fr.getId(),
                Const.ParamsNames.FEEDBACK_SESSION_NAME, fr.feedbackSessionName,
                Const.ParamsNames.COURSE_ID, fr.courseId,
                Const.ParamsNames.FEEDBACK_QUESTION_ID + "-1", fr.feedbackQuestionId,
                Const.ParamsNames.FEEDBACK_RESPONSE_RECIPIENT + "-1-0", fr.recipientEmail,
                Const.ParamsNames.FEEDBACK_QUESTION_TYPE + "-1", fr.feedbackQuestionType.toString(),
                Const.ParamsNames.FEEDBACK_RESPONSE_TEXT + "-1-0", "Edited" + fr.getResponseDetails().getAnswerString(),
                Const.ParamsNames.FEEDBACK_SESSION_MODERATED_STUDENT, moderatedStudentEmail
        };
        
        try{
            a = getAction(submissionParams);
            r = (RedirectResult) a.executeAndPostProcess();
        } catch (UnauthorizedAccessException e) {
            assertEquals("Feedback session [Another feedback session] is not accessible to instructor [" + instructorHelper3.email + "] for privilege [canmodifysessioncommentinsection] on section [Section 2]", e.getMessage());
        }
    }
    
    private void testSubmitResponseForInvalidQuestion() throws Exception {
        ______TS("Failure case: submit response for question in session, but should not be editable by instructor");
        
        InstructorAttributes instructor = dataBundle.instructors.get("IESFPTCourseinstr");
        gaeSimulation.loginAsInstructor(instructor.googleId);
        
        FeedbackResponsesDb frDb = new FeedbackResponsesDb();
        FeedbackQuestionsDb fqDb = new FeedbackQuestionsDb();
        
        FeedbackResponseAttributes fr = dataBundle.feedbackResponses.get("response1ForQ3");
        FeedbackQuestionAttributes fq = fqDb.getFeedbackQuestion("First feedback session", "IESFPTCourse", 3);
        
        fr = frDb.getFeedbackResponse(fq.getId(), fr.giverEmail, fr.recipientEmail); // necessary to get the correct responseId
        assertNotNull("Feedback response not found in database", fr);
        String moderatedStudentEmail = "student1InIESFPTCourse@gmail.tmt";
        
        String[] submissionParams = new String[]{
                Const.ParamsNames.FEEDBACK_QUESTION_RESPONSETOTAL + "-1", "1",
                Const.ParamsNames.FEEDBACK_RESPONSE_ID + "-1-0", fr.getId(),
                Const.ParamsNames.FEEDBACK_SESSION_NAME, fr.feedbackSessionName,
                Const.ParamsNames.COURSE_ID, fr.courseId,
                Const.ParamsNames.FEEDBACK_QUESTION_ID + "-1", fr.feedbackQuestionId,
                Const.ParamsNames.FEEDBACK_RESPONSE_RECIPIENT + "-1-0", fr.recipientEmail,
                Const.ParamsNames.FEEDBACK_QUESTION_TYPE + "-1", fr.feedbackQuestionType.toString(),
                Const.ParamsNames.FEEDBACK_RESPONSE_TEXT + "-1-0", "Edited" + fr.getResponseDetails().getAnswerString(),
                Const.ParamsNames.FEEDBACK_SESSION_MODERATED_STUDENT, moderatedStudentEmail
        };
        
        InstructorEditStudentFeedbackSaveAction a;
        @SuppressWarnings("unused")
        // unused but still needed to allow detection of exception
        RedirectResult r;
        
        try {
            a = getAction(submissionParams);
            r = (RedirectResult) a.executeAndPostProcess();
            signalFailureToDetectException("Did not detect that this instructor cannot access this particular question.");
        } catch (UnauthorizedAccessException e) {
            assertEquals("Feedback session [First feedback session] question [" + fr.feedbackQuestionId + "] is not accessible to instructor [" + 
                    instructor.email + "]", e.getMessage());
        }

        fq = fqDb.getFeedbackQuestion("First feedback session", "IESFPTCourse", 4);
        assertNotNull("Feedback question not found in database", fq);

        fr = dataBundle.feedbackResponses.get("response1ForQ4");
        fr = frDb.getFeedbackResponse(fq.getId(), fr.giverEmail, fr.recipientEmail); // necessary to get the correct responseId
        assertNotNull("Feedback response not found in database", fr);
        
        submissionParams = new String[]{
                Const.ParamsNames.FEEDBACK_QUESTION_RESPONSETOTAL + "-1", "1",
                Const.ParamsNames.FEEDBACK_RESPONSE_ID + "-1-0", fr.getId(),
                Const.ParamsNames.FEEDBACK_SESSION_NAME, fr.feedbackSessionName,
                Const.ParamsNames.COURSE_ID, fr.courseId,
                Const.ParamsNames.FEEDBACK_QUESTION_ID + "-1", fr.feedbackQuestionId,
                Const.ParamsNames.FEEDBACK_RESPONSE_RECIPIENT + "-1-0", fr.recipientEmail,
                Const.ParamsNames.FEEDBACK_QUESTION_TYPE + "-1", fr.feedbackQuestionType.toString(),
                Const.ParamsNames.FEEDBACK_RESPONSE_TEXT + "-1-0", "Edited" + fr.getResponseDetails().getAnswerString(),
                Const.ParamsNames.FEEDBACK_SESSION_MODERATED_STUDENT, moderatedStudentEmail
        };
        
        try {
            a = getAction(submissionParams);
            r = (RedirectResult) a.executeAndPostProcess();
            signalFailureToDetectException("Did not detect that this instructor cannot access this particular question.");
        } catch (UnauthorizedAccessException e) {
            assertEquals("Feedback session [First feedback session] question [" + fr.feedbackQuestionId + "] is not accessible to instructor [" + 
                    instructor.email + "]", e.getMessage());
        }
        
        fq = fqDb.getFeedbackQuestion("First feedback session", "IESFPTCourse", 5);
        assertNotNull("Feedback question not found in database", fq);

        fr = dataBundle.feedbackResponses.get("response1ForQ5");
        fr = frDb.getFeedbackResponse(fq.getId(), fr.giverEmail, fr.recipientEmail); // necessary to get the correct responseId
        assertNotNull("Feedback response not found in database", fr);
        
        submissionParams = new String[]{
                Const.ParamsNames.FEEDBACK_QUESTION_RESPONSETOTAL + "-1", "1",
                Const.ParamsNames.FEEDBACK_RESPONSE_ID + "-1-0", fr.getId(),
                Const.ParamsNames.FEEDBACK_SESSION_NAME, fr.feedbackSessionName,
                Const.ParamsNames.COURSE_ID, fr.courseId,
                Const.ParamsNames.FEEDBACK_QUESTION_ID + "-1", fr.feedbackQuestionId,
                Const.ParamsNames.FEEDBACK_RESPONSE_RECIPIENT + "-1-0", fr.recipientEmail,
                Const.ParamsNames.FEEDBACK_QUESTION_TYPE + "-1", fr.feedbackQuestionType.toString(),
                Const.ParamsNames.FEEDBACK_RESPONSE_TEXT + "-1-0", "Edited" + fr.getResponseDetails().getAnswerString(),
                Const.ParamsNames.FEEDBACK_SESSION_MODERATED_STUDENT, moderatedStudentEmail
        };
        
        try {
            a = getAction(submissionParams);
            r = (RedirectResult) a.executeAndPostProcess();
            signalFailureToDetectException("Did not detect that this instructor cannot access this particular question.");
        } catch (UnauthorizedAccessException e) {
            assertEquals("Feedback session [First feedback session] question [" + fr.feedbackQuestionId + "] is not accessible to instructor [" + 
                    instructor.email + "]", e.getMessage());
        }
    }
    
    private void testClosedSession() throws Exception {
        ______TS("Success case: modifying responses in closed session");
        
        InstructorAttributes instructor = dataBundle.instructors.get("IESFPTCourseinstr");
        gaeSimulation.loginAsInstructor(instructor.googleId);
        
        String moderatedStudentEmail = "student1InIESFPTCourse@gmail.tmt";
        FeedbackResponsesDb frDb = new FeedbackResponsesDb();
        FeedbackQuestionsDb fqDb = new FeedbackQuestionsDb();
        FeedbackQuestionAttributes fq = fqDb.getFeedbackQuestion("Closed feedback session", "IESFPTCourse", 1);
        
        FeedbackResponseAttributes fr = dataBundle.feedbackResponses.get("response1ForQ1InClosedSession");
        fr = frDb.getFeedbackResponse(fq.getId(), fr.giverEmail, fr.recipientEmail); // necessary to get the correct responseId
        assertNotNull("Feedback response not found in database", fr);
        
        String[] submissionParams = new String[]{
                Const.ParamsNames.FEEDBACK_QUESTION_RESPONSETOTAL + "-1", "1",
                Const.ParamsNames.FEEDBACK_RESPONSE_ID + "-1-0", fr.getId(),
                Const.ParamsNames.FEEDBACK_SESSION_NAME, fr.feedbackSessionName,
                Const.ParamsNames.COURSE_ID, fr.courseId,
                Const.ParamsNames.FEEDBACK_QUESTION_ID + "-1", fr.feedbackQuestionId,
                Const.ParamsNames.FEEDBACK_RESPONSE_RECIPIENT + "-1-0", fr.recipientEmail,
                Const.ParamsNames.FEEDBACK_QUESTION_TYPE + "-1", fr.feedbackQuestionType.toString(),
                Const.ParamsNames.FEEDBACK_RESPONSE_TEXT + "-1-0", "Edited" + fr.getResponseDetails().getAnswerString(),
                Const.ParamsNames.FEEDBACK_SESSION_MODERATED_STUDENT, moderatedStudentEmail
        };
        
        InstructorEditStudentFeedbackSaveAction a = getAction(submissionParams);
        RedirectResult r = (RedirectResult) a.executeAndPostProcess();
        
        assertFalse(r.isError);
        assertEquals("All responses submitted succesfully!", r.getStatusMessage());
        assertEquals("/page/instructorEditStudentFeedbackPage" + 
                     "?error=false&moderatedstudent=student1InIESFPTCourse%40gmail.tmt" + 
                     "&user=IESFPTCourseinstr&courseid=IESFPTCourse" + 
                     "&fsname=Closed+feedback+session",
                     r.getDestinationWithParams());
        assertNotNull(frDb.getFeedbackResponse(fq.getId(), fr.giverEmail, fr.recipientEmail));
    }
    
    private InstructorEditStudentFeedbackSaveAction getAction(String... params) throws Exception {
        return (InstructorEditStudentFeedbackSaveAction) (gaeSimulation.getActionObject(uri, params));
    }
}
