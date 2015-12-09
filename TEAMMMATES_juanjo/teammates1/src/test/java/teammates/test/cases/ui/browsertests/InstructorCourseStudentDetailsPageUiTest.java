package teammates.test.cases.ui.browsertests;

import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import teammates.common.datatransfer.DataBundle;
import teammates.common.util.Const;
import teammates.common.util.Url;
import teammates.test.pageobjects.Browser;
import teammates.test.pageobjects.BrowserPool;
import teammates.test.pageobjects.InstructorCourseStudentDetailsViewPage;

/**
 * Covers the 'student details' view for instructors.
 * SUT: {@link InstructorCourseStudentDetailsViewPage}.
 */
public class InstructorCourseStudentDetailsPageUiTest extends BaseUiTestCase {
    private static Browser browser;
    private static InstructorCourseStudentDetailsViewPage viewPage;
    private static DataBundle testData;
    
    private static String instructorId;
    private static String courseId;
    

    @BeforeClass
    public static void classSetup() throws Exception {
        printTestClassHeader();
        testData = loadDataBundle("/InstructorCourseStudentDetailsPageUiTest.json");
        removeAndRestoreTestDataOnServer(testData);
        browser = BrowserPool.getBrowser();
        instructorId = testData.instructors.get("CCSDetailsUiT.instr").googleId;
        courseId = testData.courses.get("CCSDetailsUiT.CS2104").id;
    }
    
    
    @Test
    public void testAll() throws Exception{

        testContent();
    }

    private void testContent() {
        
        ______TS("content: registered student");
        
        viewPage = getCourseStudentDetailsPage("registeredStudent");

        // This is the full HTML verification for Instructor Student Details Page, the rest can all be verifyMainHtml
        viewPage.verifyHtml("/InstructorCourseStudentDetailsRegistered.html");

        ______TS("content: unregistered student");
            
        viewPage = getCourseStudentDetailsPage("unregisteredStudent");
        viewPage.verifyHtmlMainContent("/InstructorCourseStudentDetailsUnregistered.html");
        
        ______TS("content: registered student with helper view");
        
        // the helper here is configured to be able to view studentDetailsPage
        instructorId = testData.instructors.get("CCSDetailsUiT.Helper").googleId;
        
        viewPage = getCourseStudentDetailsPage("registeredStudent");
        viewPage.verifyHtmlMainContent("/InstructorCourseStudentDetailsRegisteredWithHelperView.html");
        
        // TODO: add test for the comment box in this page
    }


    private InstructorCourseStudentDetailsViewPage getCourseStudentDetailsPage(String studentStr) {
        Url viewPageUrl = createUrl(Const.ActionURIs.INSTRUCTOR_COURSE_STUDENT_DETAILS_PAGE)
            .withUserId(instructorId)
            .withCourseId(courseId)
            .withStudentEmail(testData.students.get(studentStr).email);
        
        return loginAdminToPage(browser, viewPageUrl, InstructorCourseStudentDetailsViewPage.class);
    }
    

    @AfterClass
    public static void classTearDown() throws Exception {
        BrowserPool.release(browser);
    }
}