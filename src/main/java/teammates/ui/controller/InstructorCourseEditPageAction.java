package teammates.ui.controller;

import java.util.ArrayList;
import java.util.List;

import teammates.common.datatransfer.CourseAttributes;
import teammates.common.datatransfer.FeedbackSessionAttributes;
import teammates.common.datatransfer.InstructorAttributes;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.util.Assumption;
import teammates.common.util.Const;
import teammates.logic.api.GateKeeper;

/**
 * Action: showing the 'Edit' page for a course of an instructor
 */
public class InstructorCourseEditPageAction extends Action {
    
    //TODO: display privileges in the database properly
    @Override
    public ActionResult execute() throws EntityDoesNotExistException { 
                
        String courseId = getRequestParamValue(Const.ParamsNames.COURSE_ID);
        Assumption.assertNotNull(courseId);
        String instructorEmail = getRequestParamValue(Const.ParamsNames.INSTRUCTOR_EMAIL);
        String index = getRequestParamValue(Const.ParamsNames.COURSE_EDIT_MAIN_INDEX);

        InstructorAttributes instructor = logic.getInstructorForGoogleId(courseId, account.googleId);
        CourseAttributes courseToEdit = logic.getCourse(courseId);
         
        new GateKeeper().verifyAccessible(instructor, courseToEdit);
        
        /* Setup page data for 'Edit' page of a course for an instructor */
        List<InstructorAttributes> instructorList = new ArrayList<InstructorAttributes>();
        
        int instructorToShowIndex  = -1;    // -1 means showing all instructors
        
        if (instructorEmail == null) {
            instructorList = logic.getInstructorsForCourse(courseId);
        } else {
            instructorList.add(logic.getInstructorForEmail(courseId, instructorEmail));
            instructorToShowIndex = Integer.parseInt(index);        
        }
        
        List<String> sectionNames = logic.getSectionNamesForCourse(courseId);
        List<String> feedbackNames = new ArrayList<String>();
        
        List<FeedbackSessionAttributes> feedbacks = logic.getFeedbackSessionsForCourse(courseId);
        for (FeedbackSessionAttributes feedback : feedbacks) {
            feedbackNames.add(feedback.feedbackSessionName);
        }
        
        InstructorCourseEditPageData data = new InstructorCourseEditPageData(account, courseToEdit, 
                                                                             instructorList, instructor, 
                                                                             instructorToShowIndex, 
                                                                             sectionNames, feedbackNames);
        
        statusToAdmin = "instructorCourseEdit Page Load<br>"
                        + "Editing information for Course <span class=\"bold\">[" + courseId + "]</span>";
        
        ShowPageResult response = createShowPageResult(Const.ViewURIs.INSTRUCTOR_COURSE_EDIT, data);
        return response;
    }
}
