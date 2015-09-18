package teammates.test.cases.ui;

import static org.testng.AssertJUnit.assertEquals;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import teammates.common.datatransfer.DataBundle;
import teammates.common.datatransfer.InstructorAttributes;
import teammates.common.util.Const;
import teammates.test.driver.AssertHelper;
import teammates.ui.controller.InstructorCourseArchiveAction;
import teammates.ui.controller.RedirectResult;

public class InstructorCourseArchiveActionTest extends BaseActionTest {

    private final DataBundle dataBundle = getTypicalDataBundle();
    
    @BeforeClass
    public static void classSetUp() throws Exception {
        printTestClassHeader();
		removeAndRestoreTypicalDataInDatastore();
        uri = Const.ActionURIs.INSTRUCTOR_COURSE_ARCHIVE;
    }
    
    @Test
    public void testExecuteAndPostProcess() throws Exception {
        String[] submissionParams = new String[]{};
        
        InstructorAttributes instructor1OfCourse1 = dataBundle.instructors.get("instructor1OfCourse1");
        String instructorId = instructor1OfCourse1.googleId;
        String courseId = instructor1OfCourse1.courseId;
        gaeSimulation.loginAsInstructor(instructorId);
        
        ______TS("Not enough parameters");
        
        verifyAssumptionFailure();
        verifyAssumptionFailure(Const.ParamsNames.COURSE_ID, courseId);
        verifyAssumptionFailure(Const.ParamsNames.COURSE_ARCHIVE_STATUS, "true");
        
        ______TS("Typical case: archive a course, redirect to homepage");
        
        submissionParams = new String[]{
                Const.ParamsNames.COURSE_ID, instructor1OfCourse1.courseId,
                Const.ParamsNames.COURSE_ARCHIVE_STATUS, "true",
                Const.ParamsNames.NEXT_URL, Const.ActionURIs.INSTRUCTOR_HOME_PAGE
        };
        
        InstructorCourseArchiveAction archiveAction = getAction(submissionParams);
        RedirectResult redirectResult = getRedirectResult(archiveAction);
        
        assertEquals(Const.ActionURIs.INSTRUCTOR_HOME_PAGE + "?error=false&user=idOfInstructor1OfCourse1", 
                     redirectResult.getDestinationWithParams());
        assertEquals(false, redirectResult.isError);
        assertEquals(String.format(Const.StatusMessages.COURSE_ARCHIVED_FROM_HOMEPAGE, courseId), 
                     redirectResult.getStatusMessage());
        
        String expectedLogSegment = "Course archived: " + courseId;
        AssertHelper.assertContains(expectedLogSegment, archiveAction.getLogMessage());

        ______TS("Rare case: archive an already archived course, redirect to homepage");
        
        submissionParams = new String[]{
                Const.ParamsNames.COURSE_ID, instructor1OfCourse1.courseId,
                Const.ParamsNames.COURSE_ARCHIVE_STATUS, "true",
                Const.ParamsNames.NEXT_URL, Const.ActionURIs.INSTRUCTOR_HOME_PAGE
        };
        
        archiveAction = getAction(submissionParams);
        redirectResult = getRedirectResult(archiveAction);
        
        assertEquals(Const.ActionURIs.INSTRUCTOR_HOME_PAGE + "?error=false&user=idOfInstructor1OfCourse1", 
                     redirectResult.getDestinationWithParams());
        assertEquals(false, redirectResult.isError);
        assertEquals(String.format(Const.StatusMessages.COURSE_ARCHIVED_FROM_HOMEPAGE, courseId), 
                     redirectResult.getStatusMessage());
        
        expectedLogSegment = "Course archived: " + courseId;
        AssertHelper.assertContains(expectedLogSegment, archiveAction.getLogMessage());
        
        ______TS("Typical case: unarchive a course, redirect to Courses page");
        
        submissionParams = new String[]{
                Const.ParamsNames.COURSE_ID, instructor1OfCourse1.courseId,
                Const.ParamsNames.COURSE_ARCHIVE_STATUS, "false",
                Const.ParamsNames.NEXT_URL, Const.ActionURIs.INSTRUCTOR_COURSES_PAGE
        };
        
        InstructorCourseArchiveAction unarchiveAction = getAction(submissionParams);
        redirectResult = getRedirectResult(unarchiveAction);
        
        assertEquals(Const.ActionURIs.INSTRUCTOR_COURSES_PAGE + "?error=false&user=idOfInstructor1OfCourse1", 
                     redirectResult.getDestinationWithParams());
        assertEquals(false, redirectResult.isError);
        assertEquals(String.format(Const.StatusMessages.COURSE_UNARCHIVED, courseId), 
                     redirectResult.getStatusMessage());
        
        expectedLogSegment = "Course unarchived: " + courseId;
        AssertHelper.assertContains(expectedLogSegment, unarchiveAction.getLogMessage());
        
        ______TS("Rare case: unarchive an active course, redirect to Courses page");
        
        submissionParams = new String[]{
                Const.ParamsNames.COURSE_ID, instructor1OfCourse1.courseId,
                Const.ParamsNames.COURSE_ARCHIVE_STATUS, "false",
                Const.ParamsNames.NEXT_URL, Const.ActionURIs.INSTRUCTOR_COURSES_PAGE
        };
        
        unarchiveAction = getAction(submissionParams);
        redirectResult = getRedirectResult(unarchiveAction);
        
        assertEquals(Const.ActionURIs.INSTRUCTOR_COURSES_PAGE + "?error=false&user=idOfInstructor1OfCourse1", 
                     redirectResult.getDestinationWithParams());
        assertEquals(false, redirectResult.isError);
        assertEquals(String.format(Const.StatusMessages.COURSE_UNARCHIVED, courseId), 
                     redirectResult.getStatusMessage());
        
        expectedLogSegment = "Course unarchived: " + courseId;
        AssertHelper.assertContains(expectedLogSegment, unarchiveAction.getLogMessage());
        
        ______TS("Rare case: unarchive an active course, no next URL, redirect to Courses page");
        
        submissionParams = new String[]{
                Const.ParamsNames.COURSE_ID, instructor1OfCourse1.courseId,
                Const.ParamsNames.COURSE_ARCHIVE_STATUS, "false",
        };
        
        unarchiveAction = getAction(submissionParams);
        redirectResult = getRedirectResult(unarchiveAction);
        
        assertEquals(Const.ActionURIs.INSTRUCTOR_COURSES_PAGE + "?error=false&user=idOfInstructor1OfCourse1", 
                     redirectResult.getDestinationWithParams());
        assertEquals(false, redirectResult.isError);
        assertEquals(String.format(Const.StatusMessages.COURSE_UNARCHIVED, courseId), 
                     redirectResult.getStatusMessage());
        
        expectedLogSegment = "Course unarchived: " + courseId;
        AssertHelper.assertContains(expectedLogSegment, unarchiveAction.getLogMessage());
        
        ______TS("Masquerade mode: archive course, redirect to Courses page");
        
        gaeSimulation.loginAsAdmin("admin.user");
        submissionParams = new String[]{
                Const.ParamsNames.COURSE_ID, instructor1OfCourse1.courseId,
                Const.ParamsNames.COURSE_ARCHIVE_STATUS, "true",
                Const.ParamsNames.NEXT_URL, Const.ActionURIs.INSTRUCTOR_COURSES_PAGE
        };
        archiveAction = getAction(addUserIdToParams(instructorId, submissionParams));
        redirectResult = getRedirectResult(archiveAction);
        
        assertEquals(Const.ActionURIs.INSTRUCTOR_COURSES_PAGE + "?error=false&user=idOfInstructor1OfCourse1", 
                     redirectResult.getDestinationWithParams());
        assertEquals(false, redirectResult.isError);
        assertEquals(String.format(Const.StatusMessages.COURSE_ARCHIVED, courseId), 
                     redirectResult.getStatusMessage());
        
        expectedLogSegment = "Course archived: " + courseId;
        AssertHelper.assertContains(expectedLogSegment, archiveAction.getLogMessage());
        
        ______TS("Rare case: empty course ID");
        
        gaeSimulation.loginAsAdmin("admin.user");
        submissionParams = new String[]{
                Const.ParamsNames.COURSE_ID, "",
                Const.ParamsNames.COURSE_ARCHIVE_STATUS, "true",
                Const.ParamsNames.NEXT_URL, Const.ActionURIs.INSTRUCTOR_COURSES_PAGE
        };
        archiveAction = getAction(addUserIdToParams(instructorId, submissionParams));
       
        try {
            redirectResult = getRedirectResult(archiveAction);
            signalFailureToDetectException(" - IllegalArgumentException");
        } catch (Exception e){
            AssertHelper.assertContains("name cannot be null or empty", e.getMessage());
        }
        
        assertEquals(Const.ActionURIs.INSTRUCTOR_COURSES_PAGE + "?error=false&user=idOfInstructor1OfCourse1", 
                     redirectResult.getDestinationWithParams());
        assertEquals(false, redirectResult.isError);
        assertEquals(String.format(Const.StatusMessages.COURSE_ARCHIVED, courseId), redirectResult.getStatusMessage());
        
        expectedLogSegment = "TEAMMATESLOG|||instructorCourseArchive|||instructorCourseArchive|||true|||"
                             + "Instructor(M)|||Instructor 1 of Course 1|||idOfInstructor1OfCourse1|||"
                             + "instr1@course1.tmt|||null|||/page/instructorCourseArchive";
        AssertHelper.assertContains(expectedLogSegment, archiveAction.getLogMessage());
        
    }
    
    private InstructorCourseArchiveAction getAction(String... params) throws Exception {
        return (InstructorCourseArchiveAction) (gaeSimulation.getActionObject(uri, params));
    }
    

}

