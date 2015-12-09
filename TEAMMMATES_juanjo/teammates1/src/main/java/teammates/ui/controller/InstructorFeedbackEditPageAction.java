package teammates.ui.controller;

import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import teammates.common.datatransfer.FeedbackQuestionAttributes;
import teammates.common.datatransfer.FeedbackSessionAttributes;
import teammates.common.datatransfer.InstructorAttributes;
import teammates.common.datatransfer.StudentAttributes;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.util.Assumption;
import teammates.common.util.Const;
import teammates.logic.api.GateKeeper;

public class InstructorFeedbackEditPageAction extends Action {

    @Override
    protected ActionResult execute() throws EntityDoesNotExistException {
        
        String courseId = getRequestParamValue(Const.ParamsNames.COURSE_ID);
        Assumption.assertNotNull(courseId);
        String feedbackSessionName = getRequestParamValue(Const.ParamsNames.FEEDBACK_SESSION_NAME);
        Assumption.assertNotNull(feedbackSessionName);
        
        FeedbackSessionAttributes feedbackSession = logic.getFeedbackSession(feedbackSessionName, courseId);
        new GateKeeper().verifyAccessible(
                logic.getInstructorForGoogleId(courseId, account.googleId), 
                feedbackSession,
                false,
                Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_SESSION);
        
        
        List<FeedbackQuestionAttributes> questions = logic.getFeedbackQuestionsForSession(feedbackSessionName, courseId);
        List<FeedbackQuestionAttributes> copiableQuestions = logic.getCopiableFeedbackQuestionsForInstructor(account.googleId);
        
        Map<String, Boolean> questionHasResponses = new HashMap<String, Boolean>();
        
        for (FeedbackQuestionAttributes question : questions) {
            boolean hasResponse = logic.isQuestionHasResponses(question.getId());
            questionHasResponses.put(question.getId(), hasResponse); 
        }
        
        List<StudentAttributes> studentList = logic.getStudentsForCourse(courseId);
        Collections.sort(studentList, new StudentComparator());
        
        List<InstructorAttributes> instructorList = logic.getInstructorsForCourse(courseId);
        Collections.sort(instructorList, new InstructorComparator());
        
        InstructorAttributes instructor = logic.getInstructorForGoogleId(courseId, account.googleId);
        
        statusToAdmin = "instructorFeedbackEdit Page Load<br>" 
                        + "Editing information for Feedback Session "
                        + "<span class=\"bold\">[" + feedbackSessionName + "]</span>"
                        + "in Course: <span class=\"bold\">[" + courseId + "]</span>";
        
        InstructorFeedbackEditPageData data = new InstructorFeedbackEditPageData(account);
        data.init(feedbackSession, questions, copiableQuestions, questionHasResponses, studentList, instructorList, instructor);
           
        return createShowPageResult(Const.ViewURIs.INSTRUCTOR_FEEDBACK_EDIT, data);
    }
    
    private class StudentComparator implements Comparator<StudentAttributes> {
        @Override
        public int compare(StudentAttributes s1, StudentAttributes s2) {
            if (s1.team.equals(s2.team)) {
                return s1.name.compareToIgnoreCase(s2.name);
            }
            return s1.team.compareToIgnoreCase(s2.team);
        }
    }
    
    private class InstructorComparator implements Comparator<InstructorAttributes> {
        @Override
        public int compare(InstructorAttributes i1, InstructorAttributes i2) {
            return i1.name.compareToIgnoreCase(i2.name);
        }
    }
}
 