package teammates.test.cases.ui;

import static org.testng.AssertJUnit.assertEquals;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import teammates.common.datatransfer.DataBundle;
import teammates.common.datatransfer.InstructorAttributes;
import teammates.common.util.Const;
import teammates.logic.core.CoursesLogic;
import teammates.ui.controller.Action;
import teammates.ui.controller.InstructorCoursesPageData;
import teammates.ui.controller.ShowPageResult;

/**
 * Test case for adding a course for an instructor
 * This test case will fully cover the path in checking archived courses.
 * This also will be tested in UI testing.
 */
public class InstructorCourseAddActionTest extends BaseActionTest {

    private final DataBundle dataBundle = getTypicalDataBundle();
    
    @BeforeClass
    public static void classSetUp() throws Exception {
        printTestClassHeader();
		removeAndRestoreTypicalDataInDatastore();
        uri = Const.ActionURIs.INSTRUCTOR_COURSE_ADD;
    }
    
    @Test
    public void testExecute() throws Exception{
        InstructorAttributes instructor1OfCourse1 = dataBundle.instructors.get("instructor1OfCourse1");
        String instructorId = instructor1OfCourse1.googleId;
        
        String adminUserId = "admin.user";
        
        gaeSimulation.loginAsInstructor(instructorId);

        ______TS("Not enough parameters");
        verifyAssumptionFailure();
        verifyAssumptionFailure(Const.ParamsNames.COURSE_NAME, "ticac tac name");
        
        ______TS("Error: Invalid parameter for Course ID");
        
        Action addAction = getAction(Const.ParamsNames.COURSE_ID, "ticac,tpa1,id",
                                     Const.ParamsNames.COURSE_NAME, "ticac tpa1 name");
        ShowPageResult pageResult = (ShowPageResult) addAction.executeAndPostProcess();
        
        assertEquals(Const.ViewURIs.INSTRUCTOR_COURSES + "?error=true&user=idOfInstructor1OfCourse1", 
                     pageResult.getDestinationWithParams());
        assertEquals(true, pageResult.isError);
        assertEquals(Const.StatusMessages.COURSE_INVALID_ID, pageResult.getStatusMessage());
        
        InstructorCoursesPageData pageData = (InstructorCoursesPageData) pageResult.data;
        assertEquals(1, pageData.getActiveCourses().getRows().size() + pageData.getArchivedCourses().getRows().size());

        String expectedLogMessage = "TEAMMATESLOG|||instructorCourseAdd|||instructorCourseAdd|||true|||Instructor|||"
                                    + "Instructor 1 of Course 1|||idOfInstructor1OfCourse1|||instr1@course1.tmt|||"
                                    + "Please use only alphabets, numbers, dots, hyphens, underscores and dollar "
                                    + "signs in course ID.|||/page/instructorCourseAdd";
        assertEquals(expectedLogMessage, addAction.getLogMessage());

        ______TS("Typical case, 1 existing course");
        
        addAction = getAction(Const.ParamsNames.COURSE_ID, "ticac.tpa1.id",
                              Const.ParamsNames.COURSE_NAME, "ticac tpa1 name");
        pageResult = (ShowPageResult) addAction.executeAndPostProcess();
        
        pageData = (InstructorCoursesPageData) pageResult.data;
        assertEquals(2, pageData.getActiveCourses().getRows().size() + pageData.getArchivedCourses().getRows().size());
        
        expectedLogMessage = "TEAMMATESLOG|||instructorCourseAdd|||instructorCourseAdd|||true|||Instructor|||"
                             + "Instructor 1 of Course 1|||idOfInstructor1OfCourse1|||instr1@course1.tmt|||"
                             + "Course added : ticac.tpa1.id<br>Total courses: 2|||/page/instructorCourseAdd";
        assertEquals(expectedLogMessage, addAction.getLogMessage());
        
        String expected = Const.StatusMessages.COURSE_ADDED
                  .replace("${courseEnrollLink}", 
                           "/page/instructorCourseEnrollPage?courseid=ticac.tpa1.id&user=idOfInstructor1OfCourse1")
                  .replace("${courseEditLink}", 
                           "/page/instructorCourseEditPage?courseid=ticac.tpa1.id&user=idOfInstructor1OfCourse1");
        assertEquals(expected,pageResult.getStatusMessage());
        
        ______TS("Error: Try to add the same course again");
        
        addAction = getAction(Const.ParamsNames.COURSE_ID, "ticac.tpa1.id",
                              Const.ParamsNames.COURSE_NAME, "ticac tpa1 name");
        pageResult = (ShowPageResult) addAction.executeAndPostProcess();
        
        assertEquals(Const.ViewURIs.INSTRUCTOR_COURSES + "?error=true&user=idOfInstructor1OfCourse1", 
                     pageResult.getDestinationWithParams());
        assertEquals(true, pageResult.isError);
        assertEquals(Const.StatusMessages.COURSE_EXISTS, pageResult.getStatusMessage());
        
        pageData = (InstructorCoursesPageData) pageResult.data;
        assertEquals(2, pageData.getActiveCourses().getRows().size() + pageData.getArchivedCourses().getRows().size());
        
        expectedLogMessage = "TEAMMATESLOG|||instructorCourseAdd|||instructorCourseAdd|||true|||Instructor|||"
                             + "Instructor 1 of Course 1|||idOfInstructor1OfCourse1|||instr1@course1.tmt|||"
                             + "A course by the same ID already exists in the system, possibly created by another "
                             + "user. Please choose a different course ID|||/page/instructorCourseAdd";
        assertEquals(expectedLogMessage, addAction.getLogMessage());
        
        ______TS("Masquerade mode, 0 courses");
        
        CoursesLogic.inst().deleteCourseCascade(instructor1OfCourse1.courseId);
        CoursesLogic.inst().deleteCourseCascade("ticac.tpa1.id");
        gaeSimulation.loginAsAdmin(adminUserId);
        addAction = getAction(Const.ParamsNames.USER_ID, instructorId,
                              Const.ParamsNames.COURSE_ID, "ticac.tpa2.id",
                              Const.ParamsNames.COURSE_NAME, "ticac tpa2 name");
        pageResult = (ShowPageResult) addAction.executeAndPostProcess();
        
        String expectedDestination = Const.ViewURIs.INSTRUCTOR_COURSES + "?error=false&user=idOfInstructor1OfCourse1";
        assertEquals(expectedDestination, pageResult.getDestinationWithParams());
        assertEquals(false, pageResult.isError);
        String expectedStatus = "The course has been added. Click <a href=\"/page/instructorCourseEnrollPage?"
                                + "courseid=ticac.tpa2.id&user=idOfInstructor1OfCourse1\">here</a> to add students "
                                + "to the course or click <a href=\"/page/instructorCourseEditPage?"
                                + "courseid=ticac.tpa2.id&user=idOfInstructor1OfCourse1\">here</a> to add other "
                                + "instructors.<br>If you don't see the course in the list below, please refresh "
                                + "the page after a few moments.";
        assertEquals(expectedStatus, pageResult.getStatusMessage());
        
        pageData = (InstructorCoursesPageData) pageResult.data;
        assertEquals(1, pageData.getActiveCourses().getRows().size() + pageData.getArchivedCourses().getRows().size());
        
        expectedLogMessage = "TEAMMATESLOG|||instructorCourseAdd|||instructorCourseAdd|||true|||Instructor(M)|||"
                             + "Instructor 1 of Course 1|||idOfInstructor1OfCourse1|||instr1@course1.tmt|||"
                             + "Course added : ticac.tpa2.id<br>Total courses: 1|||/page/instructorCourseAdd";
        assertEquals(expectedLogMessage, addAction.getLogMessage());
        
        // delete the new course
        CoursesLogic.inst().deleteCourseCascade("ticac.tpa2.id");
        
        ______TS("Test archived Courses");
        InstructorAttributes instructorOfArchivedCourse = dataBundle.instructors.get("instructorOfArchivedCourse");
        instructorId = instructorOfArchivedCourse.googleId;
        
        gaeSimulation.loginAsInstructor(instructorId);
        
        addAction = getAction(Const.ParamsNames.COURSE_ID, "ticac.tpa2.id",
                              Const.ParamsNames.COURSE_NAME, "ticac tpa2 name");
        pageResult = (ShowPageResult) addAction.executeAndPostProcess();
        
        pageData = (InstructorCoursesPageData) pageResult.data;
        assertEquals(2, pageData.getActiveCourses().getRows().size() + pageData.getArchivedCourses().getRows().size());
        
        expectedLogMessage = "TEAMMATESLOG|||instructorCourseAdd|||instructorCourseAdd|||true|||Instructor|||"
                             + "InstructorOfArchiveCourse name|||idOfInstructorOfArchivedCourse|||"
                             + "instructorOfArchiveCourse@archiveCourse.tmt|||Course added : ticac.tpa2.id<br>"
                             + "Total courses: 2|||/page/instructorCourseAdd";
        assertEquals(expectedLogMessage, addAction.getLogMessage());
        
        
        expected = Const.StatusMessages.COURSE_ADDED
                .replace("${courseEnrollLink}", 
                         "/page/instructorCourseEnrollPage?courseid=ticac.tpa2.id&user=idOfInstructorOfArchivedCourse")
                .replace("${courseEditLink}", 
                         "/page/instructorCourseEditPage?courseid=ticac.tpa2.id&user=idOfInstructorOfArchivedCourse");
        assertEquals(expected,pageResult.getStatusMessage());
    }
    
    private Action getAction(String... parameters) throws Exception {
        return (Action) gaeSimulation.getActionObject(uri, parameters);
    }
}
