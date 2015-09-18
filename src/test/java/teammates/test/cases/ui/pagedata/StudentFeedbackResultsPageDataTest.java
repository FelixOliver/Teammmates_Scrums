package teammates.test.cases.ui.pagedata;

import static org.testng.AssertJUnit.assertNotNull;
import static org.testng.AssertJUnit.assertTrue;
import static org.testng.AssertJUnit.assertEquals;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import teammates.common.datatransfer.AccountAttributes;
import teammates.common.datatransfer.DataBundle;
import teammates.common.datatransfer.FeedbackQuestionAttributes;
import teammates.common.datatransfer.FeedbackResponseAttributes;
import teammates.common.datatransfer.StudentAttributes;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.exception.UnauthorizedAccessException;
import teammates.logic.api.Logic;
import teammates.test.cases.BaseComponentTestCase;
import teammates.ui.controller.StudentFeedbackResultsPageData;
import teammates.ui.template.StudentFeedbackResultsQuestionWithResponses;

public class StudentFeedbackResultsPageDataTest extends BaseComponentTestCase {
    private static DataBundle dataBundle = getTypicalDataBundle();
    
    @BeforeClass
    public static void classSetUp() throws Exception {
        printTestClassHeader();
        removeAndRestoreTypicalDataInDatastore();
    }
    
    @Test
    public void testAll() throws UnauthorizedAccessException, EntityDoesNotExistException {
        ______TS("typical success case");
        
        AccountAttributes account = dataBundle.accounts.get("student1InCourse1");
        StudentAttributes student = dataBundle.students.get("student1InCourse1");
        assertNotNull(student);
        Logic logic = new Logic();
        
        StudentFeedbackResultsPageData pageData = new StudentFeedbackResultsPageData(account, student);
        
        Map<FeedbackQuestionAttributes, List<FeedbackResponseAttributes>> questionsWithResponses = 
                                        new HashMap<FeedbackQuestionAttributes, List<FeedbackResponseAttributes>>();
        
        FeedbackQuestionAttributes question1 = dataBundle.feedbackQuestions.get("qn1InSession1InCourse1");
        assertNotNull(question1);
        FeedbackQuestionAttributes question2 = dataBundle.feedbackQuestions.get("qn2InSession1InCourse1");
        assertNotNull(question2);
        
        List<FeedbackResponseAttributes> responsesForQ1 = new ArrayList<FeedbackResponseAttributes>();
        List<FeedbackResponseAttributes> responsesForQ2 = new ArrayList<FeedbackResponseAttributes>();
        
        /* Question 1 with responses */
        responsesForQ1.add(dataBundle.feedbackResponses.get("response1ForQ1S1C1"));
        responsesForQ1.add(dataBundle.feedbackResponses.get("response2ForQ1S1C1"));
        questionsWithResponses.put(question1, responsesForQ1);
        
        /* Question 2 with responses */
        responsesForQ2.add(dataBundle.feedbackResponses.get("response1ForQ2S1C1"));
        responsesForQ2.add(dataBundle.feedbackResponses.get("response2ForQ2S1C1"));
        questionsWithResponses.put(question2, responsesForQ1);
            
        pageData.setBundle(logic.getFeedbackSessionResultsForStudent(question1.feedbackSessionName, question1.courseId, student.email));
        pageData.init(questionsWithResponses);
        
        StudentFeedbackResultsQuestionWithResponses questionBundle1 = pageData.getFeedbackResultsQuestionsWithResponses().get(0);
        StudentFeedbackResultsQuestionWithResponses questionBundle2 = pageData.getFeedbackResultsQuestionsWithResponses().get(1);
        
        assertNotNull(pageData.getFeedbackResultsQuestionsWithResponses());
        assertEquals(2, pageData.getFeedbackResultsQuestionsWithResponses().size());
        assertEquals("You are viewing feedback results as <span class='text-danger text-bold text-large'>"
                      + "student1 In Course1</span>. You may submit feedback and view results without logging in. "
                      + "To access other features you need <a href='/page/studentCourseJoinAuthentication?studentemail="
                      + "student1InCourse1%40gmail.tmt&courseid=idOfTypicalCourse1' class='link'>to login using "
                      + "a google account</a> (recommended).", 
                      pageData.getRegisterMessage()); 
        
        assertNotNull(questionBundle1.getQuestionDetails());
        assertNotNull(questionBundle2.getQuestionDetails()); 
        
        assertEquals("1", questionBundle1.getQuestionDetails().getQuestionIndex());
        assertEquals("2", questionBundle2.getQuestionDetails().getQuestionIndex()); 
        
        assertEquals("", questionBundle1.getQuestionDetails().getAdditionalInfo());
        assertEquals("", questionBundle2.getQuestionDetails().getAdditionalInfo());
        
        assertNotNull(questionBundle1.getResponseTables());
        assertNotNull(questionBundle2.getResponseTables());      
        
        assertEquals("You", questionBundle1.getResponseTables().get(0).getRecipientName());
        assertEquals("student2 In Course1", questionBundle1.getResponseTables().get(1).getRecipientName());
        
        assertNotNull(questionBundle1.getResponseTables().get(0).getResponses());
        assertNotNull(questionBundle2.getResponseTables().get(1).getResponses());
        
        assertEquals("You", questionBundle1.getResponseTables().get(0).getResponses()
                                        .get(0).getGiverName());
        assertEquals("student2 In Course1", questionBundle1.getResponseTables().get(1).getResponses()
                                        .get(0).getGiverName());
        
        assertEquals("Student 1 self feedback.", questionBundle1.getResponseTables().get(0).getResponses()
                                        .get(0).getAnswer());
        assertEquals("I&#39;m cool&#39;", questionBundle1.getResponseTables().get(1).getResponses()
                                        .get(0).getAnswer());
        
        ______TS("student in unregistered course");
        
        student = dataBundle.students.get("student1InUnregisteredCourse");
        
        pageData = new StudentFeedbackResultsPageData(account, student);
        Map<FeedbackQuestionAttributes, List<FeedbackResponseAttributes>> questionsWithResponsesUnregistered = 
                                        new HashMap<FeedbackQuestionAttributes, List<FeedbackResponseAttributes>>();
        
        pageData.init(questionsWithResponsesUnregistered);
        
        assertTrue(pageData.getFeedbackResultsQuestionsWithResponses().isEmpty());
        
        assertEquals("regKeyForStuNotYetJoinCourse", student.key);
        assertEquals("idOfUnregisteredCourse", student.course);
        assertEquals("student1InUnregisteredCourse@gmail.tmt", student.email);
        
        assertEquals("You are viewing feedback results as "
                      + "<span class='text-danger text-bold text-large'>student1 In "
                      + "unregisteredCourse</span>. You may submit feedback and view "
                      + "results without logging in. To access other features you need "
                      + "<a href='/page/studentCourseJoinAuthentication?key="
                      + "regKeyForStuNotYetJoinCourse&studentemail="
                      + "student1InUnregisteredCourse%40gmail.tmt&courseid=idOfUnregisteredCourse' "
                      + "class='link'>to login using a google account</a> (recommended).", 
                      pageData.getRegisterMessage());       
    }
}