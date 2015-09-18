package teammates.ui.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import teammates.common.datatransfer.CommentSendingState;
import teammates.common.datatransfer.CourseSummaryBundle;
import teammates.common.datatransfer.FeedbackSessionAttributes;
import teammates.common.datatransfer.InstructorAttributes;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.util.Const;
import teammates.common.util.StatusMessage;
import teammates.common.util.Const.StatusMessageColor;
import teammates.common.util.Const.StatusMessages;
import teammates.logic.api.GateKeeper;

public class InstructorHomePageAction extends Action {
    @Override
    public ActionResult execute() throws EntityDoesNotExistException {
        if (!account.isInstructor && isPersistenceIssue()) {
            ShowPageResult response = createShowPageResult(Const.ViewURIs.INSTRUCTOR_HOME,
                                                           new InstructorHomePageData(account));
            statusToUser.add(new StatusMessage(Const.StatusMessages.INSTRUCTOR_PERSISTENCE_ISSUE,
                                               StatusMessageColor.WARNING));
            return response;
        }
        
        new GateKeeper().verifyInstructorPrivileges(account);
        
        String courseToLoad = getRequestParamValue(Const.ParamsNames.COURSE_TO_LOAD);
        return courseToLoad == null ? loadPage() : loadCourse(courseToLoad);
    }

    private ActionResult loadCourse(String courseToLoad) throws EntityDoesNotExistException {
        int index = Integer.parseInt(getRequestParamValue("index"));
        
        InstructorAttributes instructor = logic.getInstructorForGoogleId(courseToLoad, account.googleId);
        
        CourseSummaryBundle course = logic.getCourseSummaryWithFeedbackSessions(instructor);
        FeedbackSessionAttributes.sortFeedbackSessionsByCreationTimeDescending(course.feedbackSessions);
        
        int commentsForSendingStateCount =
                logic.getCommentsForSendingState(courseToLoad, CommentSendingState.PENDING).size();
        int feedbackResponseCommentsForSendingStateCount =
                logic.getFeedbackResponseCommentsForSendingState(courseToLoad, CommentSendingState.PENDING)
                     .size();
        int pendingCommentsCount = commentsForSendingStateCount + feedbackResponseCommentsForSendingStateCount;
        List<String> sectionNames = logic.getSectionNamesForCourse(course.course.id);
        
        InstructorHomeCourseAjaxPageData data = new InstructorHomeCourseAjaxPageData(account);
        data.init(index, course, instructor, pendingCommentsCount, sectionNames);
        
        return createShowPageResult(Const.ViewURIs.INSTRUCTOR_HOME_AJAX_COURSE_TABLE, data);
    }

    private ActionResult loadPage() throws EntityDoesNotExistException {
        boolean omitArchived = true;
        HashMap<String, CourseSummaryBundle> courses = logic.getCourseSummariesWithoutStatsForInstructor(
                                                                 account.googleId, omitArchived);
        
        ArrayList<CourseSummaryBundle> courseList = new ArrayList<CourseSummaryBundle>(courses.values());
        
        String sortCriteria = getSortCriteria();
        sortCourse(courseList, sortCriteria);
        
        InstructorHomePageData data = new InstructorHomePageData(account);
        data.init(courseList, sortCriteria);
        
        if (logic.isNewInstructor(account.googleId)) {
            statusToUser.add(new StatusMessage(StatusMessages.HINT_FOR_NEW_INSTRUCTOR, StatusMessageColor.INFO));
        }
        statusToAdmin = "instructorHome Page Load<br>" + "Total Courses: " + courseList.size();
        
        ShowPageResult response = createShowPageResult(Const.ViewURIs.INSTRUCTOR_HOME, data);
        return response;
    }

    private String getSortCriteria() {
        String sortCriteria = getRequestParamValue(Const.ParamsNames.COURSE_SORTING_CRITERIA);
        if (sortCriteria == null) {
            sortCriteria = Const.DEFAULT_SORT_CRITERIA;
        }
        
        return sortCriteria;
    }
    
    private void sortCourse(ArrayList<CourseSummaryBundle> courseList, String sortCriteria) {
        switch (sortCriteria) {
            case Const.SORT_BY_COURSE_ID:
                CourseSummaryBundle.sortSummarizedCoursesByCourseId(courseList);
                break;
            case Const.SORT_BY_COURSE_NAME:
                CourseSummaryBundle.sortSummarizedCoursesByCourseName(courseList);
                break;
            case Const.SORT_BY_COURSE_CREATION_DATE:
                CourseSummaryBundle.sortSummarizedCoursesByCreationDate(courseList);
                break;
            default:
                throw new RuntimeException("Invalid course sorting criteria.");
        }
    }    
}
