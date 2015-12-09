package teammates.test.cases.ui;

import static org.testng.AssertJUnit.assertEquals;

import java.util.List;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import teammates.common.datatransfer.DataBundle;
import teammates.common.datatransfer.InstructorAttributes;
import teammates.common.exception.UnauthorizedAccessException;
import teammates.common.util.Const;
import teammates.logic.core.CoursesLogic;
import teammates.logic.core.InstructorsLogic;
import teammates.test.driver.AssertHelper;
import teammates.ui.controller.InstructorCourseEditPageAction;
import teammates.ui.controller.InstructorCourseEditPageData;
import teammates.ui.controller.ShowPageResult;
import teammates.ui.template.CourseEditInstructorPanel;

public class InstructorCourseEditPageActionTest extends BaseActionTest {

    private final DataBundle dataBundle = getTypicalDataBundle();
    
    @BeforeClass
    public static void classSetUp() throws Exception {
        printTestClassHeader();
		removeAndRestoreTypicalDataInDatastore();
        uri = Const.ActionURIs.INSTRUCTOR_COURSE_EDIT_PAGE;
    }
    
    @Test
    public void testExecuteAndPostProcess() throws Exception {
        InstructorAttributes instructor1OfCourse1 = dataBundle.instructors.get("instructor1OfCourse1");
        String instructorId = instructor1OfCourse1.googleId;
        String courseId = instructor1OfCourse1.courseId;
        
        gaeSimulation.loginAsInstructor(instructorId);
        
        ______TS("Not enough parameters");
        verifyAssumptionFailure();

        ______TS("Typical case: open the course edit page");
        String[] submissionParams = new String[]{
            Const.ParamsNames.COURSE_ID, courseId
        };
        
        InstructorCourseEditPageAction editAction = getAction(submissionParams);
        ShowPageResult pageResult = getShowPageResult(editAction);
        assertEquals(Const.ViewURIs.INSTRUCTOR_COURSE_EDIT + "?error=false&user=idOfInstructor1OfCourse1", 
                     pageResult.getDestinationWithParams());
        assertEquals(false, pageResult.isError);
        assertEquals("", pageResult.getStatusMessage());
        
        InstructorCourseEditPageData data = (InstructorCourseEditPageData) pageResult.data;
        assertEquals(CoursesLogic.inst().getCourse(courseId).toString(), data.getCourse().toString());
        verifySameInstructorList(InstructorsLogic.inst().getInstructorsForCourse(courseId), data.getInstructorPanelList());
        
        String expectedLogSegment = "instructorCourseEdit Page Load<br>"
                                    + "Editing information for Course <span class=\"bold\">[" + courseId + "]</span>";
        AssertHelper.assertContains(expectedLogSegment, editAction.getLogMessage());
        
        ______TS("Typical case: open the course edit page with instructor's email");
        submissionParams = new String[]{
            Const.ParamsNames.COURSE_ID, courseId,
            Const.ParamsNames.INSTRUCTOR_EMAIL, "instr1@course1.tmt",
            Const.ParamsNames.COURSE_EDIT_MAIN_INDEX, "1"
        };
        
        editAction = getAction(submissionParams);
        pageResult = getShowPageResult(editAction);
        assertEquals(Const.ViewURIs.INSTRUCTOR_COURSE_EDIT + "?error=false&user=idOfInstructor1OfCourse1", 
                     pageResult.getDestinationWithParams());
        assertEquals(false, pageResult.isError);
        assertEquals("", pageResult.getStatusMessage());
        
        data = (InstructorCourseEditPageData) pageResult.data;
        assertEquals(CoursesLogic.inst().getCourse(courseId).toString(), data.getCourse().toString());
        assertEquals(1, data.getInstructorPanelList().size());
        
        expectedLogSegment = "instructorCourseEdit Page Load<br>"
                             + "Editing information for Course <span class=\"bold\">[" + courseId + "]</span>";
        AssertHelper.assertContains(expectedLogSegment, editAction.getLogMessage());
        
        ______TS("Masquerade mode");
        
        InstructorAttributes instructor = dataBundle.instructors.get("instructor4");
        instructorId = instructor.googleId;
        courseId = instructor.courseId;

        gaeSimulation.loginAsAdmin("admin.user");
        
        submissionParams = new String[]{
            Const.ParamsNames.USER_ID, instructorId,
            Const.ParamsNames.COURSE_ID, courseId
        };
        
        editAction = getAction(submissionParams);
        pageResult = getShowPageResult(editAction);
        assertEquals(Const.ViewURIs.INSTRUCTOR_COURSE_EDIT + "?error=false&user=idOfInstructor4", 
                     pageResult.getDestinationWithParams());
        assertEquals(false, pageResult.isError);
        assertEquals("", pageResult.getStatusMessage());
        
        data = (InstructorCourseEditPageData) pageResult.data;
        assertEquals(CoursesLogic.inst().getCourse(courseId).toString(), data.getCourse().toString());
        verifySameInstructorList(InstructorsLogic.inst().getInstructorsForCourse(courseId), data.getInstructorPanelList());
        
        expectedLogSegment = "instructorCourseEdit Page Load<br>"
                             + "Editing information for Course <span class=\"bold\">[" + courseId + "]</span>";
        AssertHelper.assertContains(expectedLogSegment, editAction.getLogMessage());
        
        ______TS("Failure case: edit a non-existing course");
        
        CoursesLogic.inst().deleteCourseCascade(courseId);
        
        submissionParams = new String[]{
            Const.ParamsNames.USER_ID, instructorId,
            Const.ParamsNames.COURSE_ID, courseId
        };
            
        try {
            editAction = getAction(submissionParams);
            pageResult = getShowPageResult(editAction);
            signalFailureToDetectException();
        } catch (UnauthorizedAccessException e) {
            assertEquals("Trying to access system using a non-existent instructor entity", e.getMessage());
        }
    }
    
    private InstructorCourseEditPageAction getAction(String... params) throws Exception {
        return (InstructorCourseEditPageAction) (gaeSimulation.getActionObject(uri, params));
    }
    
    private void verifySameInstructorList(List<InstructorAttributes> list1, List<CourseEditInstructorPanel> list2) {
        assertEquals(list1.size(), list2.size());
        
        for (int i = 0; i < list1.size(); i++) {
            assertEquals(list1.get(i).toString(), list2.get(i).getInstructor().toString());
        }
    }
}
