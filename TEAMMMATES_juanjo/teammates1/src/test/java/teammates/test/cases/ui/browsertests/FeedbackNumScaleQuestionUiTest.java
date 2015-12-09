package teammates.test.cases.ui.browsertests;

import static org.testng.AssertJUnit.assertEquals;
import static org.testng.AssertJUnit.assertNotNull;
import static org.testng.AssertJUnit.assertNull;
import static org.testng.AssertJUnit.assertTrue;

import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import teammates.common.datatransfer.DataBundle;
import teammates.common.util.Const;
import teammates.test.driver.BackDoor;
import teammates.test.pageobjects.Browser;
import teammates.test.pageobjects.BrowserPool;
import teammates.test.pageobjects.InstructorFeedbackEditPage;

public class FeedbackNumScaleQuestionUiTest extends FeedbackQuestionUiTest {
    private static Browser browser;
    private static InstructorFeedbackEditPage feedbackEditPage;
    private static DataBundle testData;

    private static String courseId;
    private static String feedbackSessionName;
    private static String instructorId;
    
    @BeforeClass
    public void classSetup() throws Exception {
        printTestClassHeader();
        testData = loadDataBundle("/FeedbackNumScaleQuestionUiTest.json");
        removeAndRestoreTestDataOnServer(testData);
        browser = BrowserPool.getBrowser();
        
        instructorId = testData.accounts.get("instructor1").googleId;
        courseId = testData.courses.get("course").id;
        feedbackSessionName = testData.feedbackSessions.get("openSession").feedbackSessionName;
        feedbackEditPage = getFeedbackEditPage(instructorId, courseId, feedbackSessionName, browser);

    }
    
    @Test
    public void allTests() throws Exception{
        testEditPage();
        
        //TODO: move/create other NumScale question related UI tests here.
        //i.e. results page, submit page.
    }
    
    private void testEditPage(){
        testNewQuestionFrame();
        testInputValidation();
        testCustomizeOptions();
        testAddQuestionAction();
        testEditQuestionAction();
        testDeleteQuestionAction();
    }

    public void testNewQuestionFrame() {
        ______TS("NUMSCALE: new question (frame) link");

        feedbackEditPage.selectNewQuestionType("Numerical-scale question");
        feedbackEditPage.clickNewQuestionButton();
        assertTrue(feedbackEditPage.verifyNewNumScaleQuestionFormIsDisplayed());
    }
    
    public void testInputValidation() {
        
        ______TS("empty options");
        
        feedbackEditPage.fillQuestionBox("NumScale qn");
        feedbackEditPage.fillMinNumScaleBox("", -1);
        feedbackEditPage.fillStepNumScaleBox("", -1);
        feedbackEditPage.fillMaxNumScaleBox("", -1);
        
        assertEquals("[Please enter valid numbers for all the options.]",
                feedbackEditPage.getNumScalePossibleValuesString(-1));
        
        feedbackEditPage.clickAddQuestionButton();
        
        assertEquals("Please enter valid options. The min/max/step cannot be empty.", feedbackEditPage.getStatus());
        
        
        ______TS("invalid options");
        
        feedbackEditPage.fillQuestionBox("NumScale qn");
        feedbackEditPage.fillMinNumScaleBox("1", -1);
        feedbackEditPage.fillStepNumScaleBox("0.3", -1);
        feedbackEditPage.fillMaxNumScaleBox("5", -1);
        
        assertEquals("[The interval 1 - 5 is not divisible by the specified increment.]",
                feedbackEditPage.getNumScalePossibleValuesString(-1));
        
        feedbackEditPage.clickAddQuestionButton();
        
        assertEquals("Please enter valid options. The interval is not divisible by the specified increment.", feedbackEditPage.getStatus());
        
        ______TS("possible floating point error");
        
        feedbackEditPage.fillQuestionBox("NumScale qn");
        feedbackEditPage.fillMinNumScaleBox("1", -1);
        feedbackEditPage.fillStepNumScaleBox("0.001", -1);
        feedbackEditPage.fillMaxNumScaleBox("5555", -1);
        
        assertEquals("[Based on the above settings, acceptable responses are: 1, 1.001, 1.002, ..., 5554.998, 5554.999, 5555]",
                feedbackEditPage.getNumScalePossibleValuesString(-1));
        
        ______TS("more than three dp step rounding test");

        feedbackEditPage.fillMaxNumScaleBox("1002", -1);
        feedbackEditPage.fillStepNumScaleBox("1.00123456789", -1);

        assertEquals("[Based on the above settings, acceptable responses are: 1, 2.001, 3.002, ..., 999.998, 1000.999, 1002]",
                feedbackEditPage.getNumScalePossibleValuesString(-1));
        
        ______TS("NUMSCALE: min >= max test");
        //Tests javascript that automatically makes max = min+1 when max is <= min.
        feedbackEditPage.fillMinNumScaleBox(1, -1);
        feedbackEditPage.fillStepNumScaleBox(1, -1);
        feedbackEditPage.fillMaxNumScaleBox(5, -1);
        assertEquals("[Based on the above settings, acceptable responses are: 1, 2, 3, 4, 5]",
                feedbackEditPage.getNumScalePossibleValuesString(-1));
        
        fillNumScaleBoxWithRecheck(true, 6, -1, "7");
        fillNumScaleBoxWithRecheck(false, 6, -1, "7");
            
        //Reset values
        feedbackEditPage.fillMinNumScaleBox(1, -1);
        feedbackEditPage.fillMaxNumScaleBox(5, -1);
    }

    public void testCustomizeOptions() {
        feedbackEditPage.fillQuestionBox("NumScale qn");
        assertEquals("[Based on the above settings, acceptable responses are: 1, 2, 3, 4, 5]",
                feedbackEditPage.getNumScalePossibleValuesString(-1));
        feedbackEditPage.fillStepNumScaleBox(0.3, -1);
        assertEquals("[The interval 1 - 5 is not divisible by the specified increment.]",
                feedbackEditPage.getNumScalePossibleValuesString(-1));
        feedbackEditPage.fillMinNumScaleBox(5, -1);
        feedbackEditPage.fillMaxNumScaleBox(6, -1);
        feedbackEditPage.fillStepNumScaleBox(0.001, -1);
        assertEquals("[Based on the above settings, acceptable responses are: 5, 5.001, 5.002, ..., 5.998, 5.999, 6]",
                feedbackEditPage.getNumScalePossibleValuesString(-1));
        feedbackEditPage.fillMinNumScaleBox(0, -1);
        feedbackEditPage.fillMaxNumScaleBox(1, -1);
        feedbackEditPage.fillStepNumScaleBox(0.1, -1);
        assertEquals("[Based on the above settings, acceptable responses are: 0, 0.1, 0.2, ..., 0.8, 0.9, 1]",
                feedbackEditPage.getNumScalePossibleValuesString(-1));
    }

    public void testAddQuestionAction() {
        ______TS("NUMSCALE: add question action success");

        assertNull(BackDoor.getFeedbackQuestion(courseId, feedbackSessionName, 1));
        feedbackEditPage.selectRecipientsToBeStudents();
        feedbackEditPage.clickAddQuestionButton();
        assertEquals(Const.StatusMessages.FEEDBACK_QUESTION_ADDED, feedbackEditPage.getStatus());
        assertNotNull(BackDoor.getFeedbackQuestion(courseId, feedbackSessionName, 1));
        feedbackEditPage.verifyHtmlMainContent("/instructorFeedbackNumScaleQuestionAddSuccess.html");
    }

    public void testEditQuestionAction() {
        ______TS("NUMSCALE: edit question success");

        assertEquals(true, feedbackEditPage.clickEditQuestionButton(1));
        feedbackEditPage.fillEditQuestionBox("edited numscale qn text", 1);
        feedbackEditPage.fillMinNumScaleBox(3, 1);
        feedbackEditPage.fillMaxNumScaleBox(4, 1);
        feedbackEditPage.fillStepNumScaleBox(0.002, 1);
        assertEquals("[Based on the above settings, acceptable responses are: 3, 3.002, 3.004, ..., 3.996, 3.998, 4]",
                feedbackEditPage.getNumScalePossibleValuesString(1));
        feedbackEditPage.clickSaveExistingQuestionButton(1);
        assertEquals(Const.StatusMessages.FEEDBACK_QUESTION_EDITED, feedbackEditPage.getStatus());

        feedbackEditPage.verifyHtmlMainContent("/instructorFeedbackNumScaleQuestionEditSuccess.html");
    }
    
    public void testDeleteQuestionAction(){
        ______TS("NUMSCALE: qn delete then cancel");

        feedbackEditPage.clickAndCancel(feedbackEditPage.getDeleteQuestionLink(1));
        assertNotNull(BackDoor.getFeedbackQuestion(courseId, feedbackSessionName, 1));

        ______TS("NUMSCALE: qn delete then accept");

        feedbackEditPage.clickAndConfirm(feedbackEditPage.getDeleteQuestionLink(1));
        assertEquals(Const.StatusMessages.FEEDBACK_QUESTION_DELETED, feedbackEditPage.getStatus());
        assertNull(BackDoor.getFeedbackQuestion(courseId, feedbackSessionName, 1));
    }
    
    private void fillNumScaleBoxWithRecheck(boolean isMinScaleBox, int scale, int qnNumber, String expected){
        int counter = 0;
        while(counter != 100) {
            if(isMinScaleBox){
                feedbackEditPage.fillMinNumScaleBox(scale, qnNumber);
            } else {
                feedbackEditPage.fillMaxNumScaleBox(scale, qnNumber);
            }
            if(expected.equals(feedbackEditPage.getMaxNumScaleBox(qnNumber))){
                return;
            }
            counter++;
            browser.driver.switchTo().window("");
        }
        assertEquals(expected, feedbackEditPage.getMaxNumScaleBox(qnNumber));
    }
    
    @AfterClass
    public static void classTearDown() throws Exception {
        BrowserPool.release(browser);
    }
}
