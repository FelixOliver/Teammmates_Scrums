package teammates.ui.controller;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import teammates.common.datatransfer.CourseAttributes;
import teammates.common.datatransfer.FeedbackSessionAttributes;
import teammates.common.datatransfer.InstructorAttributes;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.util.Const;
import teammates.common.util.StatusMessage;
import teammates.common.util.Const.StatusMessageColor;
import teammates.logic.api.GateKeeper;

public class InstructorFeedbacksPageAction extends Action {
    
    @Override
    protected ActionResult execute() throws EntityDoesNotExistException {
        // This can be null. Non-null value indicates the page is being loaded 
        // to add a feedback to the specified course
        String courseIdForNewSession = getRequestParamValue(Const.ParamsNames.COURSE_ID);
        String feedbackSessionToHighlight = getRequestParamValue(Const.ParamsNames.FEEDBACK_SESSION_NAME);
        String isUsingAjax = getRequestParamValue(Const.ParamsNames.IS_USING_AJAX);
        
        new GateKeeper().verifyInstructorPrivileges(account);
                
        if (courseIdForNewSession != null) {
            new GateKeeper().verifyAccessible(
                    logic.getInstructorForGoogleId(courseIdForNewSession, account.googleId), 
                    logic.getCourse(courseIdForNewSession),
                    Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_SESSION);
        }

        InstructorFeedbacksPageData data = new InstructorFeedbacksPageData(account);
        data.setUsingAjax((isUsingAjax != null));
        
        
        boolean omitArchived = true; // TODO: implement as a request parameter
        // HashMap with courseId as key and InstructorAttributes as value
        Map<String, InstructorAttributes> instructors = loadCourseInstructorMap(omitArchived);
        
        List<InstructorAttributes> instructorList =
                new ArrayList<InstructorAttributes>(instructors.values());
        List<CourseAttributes> courses = loadCoursesList(instructorList);
        
        List<FeedbackSessionAttributes> existingFeedbackSessions;
        if (courses.isEmpty() || !data.isUsingAjax()) {
            existingFeedbackSessions = new ArrayList<FeedbackSessionAttributes>();
        } else {
            existingFeedbackSessions = loadFeedbackSessionsList(instructorList);
            if (existingFeedbackSessions.isEmpty()) {
                statusToUser.add(new StatusMessage(Const.StatusMessages.FEEDBACK_SESSION_EMPTY, StatusMessageColor.WARNING));
            }
        }
        
        Map<String, List<String>> courseIdToSectionName = logic.getCourseIdToSectionNamesMap(courses);
        
        if (courses.isEmpty()) {
            statusToUser.add(new StatusMessage(Const.StatusMessages.COURSE_EMPTY_IN_INSTRUCTOR_FEEDBACKS
                                                 .replace("${user}", "?user=" + account.googleId), StatusMessageColor.WARNING));
        }
        
        statusToAdmin = "Number of feedback sessions: " + existingFeedbackSessions.size();
        
        
        data.initWithoutDefaultFormValues(courses, courseIdForNewSession, existingFeedbackSessions,
                                        instructors, feedbackSessionToHighlight, courseIdToSectionName);
        
        return createShowPageResult(Const.ViewURIs.INSTRUCTOR_FEEDBACKS, data);
    }
    
    protected List<FeedbackSessionAttributes> loadFeedbackSessionsList(
            List<InstructorAttributes> instructorList) throws EntityDoesNotExistException {
        
        List<FeedbackSessionAttributes> sessions =
                logic.getFeedbackSessionsListForInstructor(instructorList);
        return sessions;
    }

    protected List<CourseAttributes> loadCoursesList(List<InstructorAttributes> instructorList)
            throws EntityDoesNotExistException {
        
        List<CourseAttributes> courses = logic.getCoursesForInstructor(instructorList);
        
        Collections.sort(courses, new Comparator<CourseAttributes>() {
            @Override
            public int compare(CourseAttributes c1, CourseAttributes c2){
                return c1.id.compareTo(c2.id);
            }
        });
        
        return courses;
    }
    
    /**
     * Gets a Map with courseId as key, and InstructorAttributes as value.
     * @return
     */
    protected HashMap<String, InstructorAttributes> loadCourseInstructorMap(boolean omitArchived) {
        HashMap<String, InstructorAttributes> courseInstructorMap = new HashMap<String, InstructorAttributes>();
        List<InstructorAttributes> instructors = logic.getInstructorsForGoogleId(account.googleId, omitArchived);
        for (InstructorAttributes instructor : instructors) {
            courseInstructorMap.put(instructor.courseId, instructor);
        }
        return courseInstructorMap;
    }
}
