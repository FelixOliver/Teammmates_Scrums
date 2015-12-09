package teammates.ui.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

import teammates.common.datatransfer.FeedbackSessionAttributes;
import teammates.common.datatransfer.InstructorAttributes;
import teammates.common.datatransfer.InstructorPrivileges;
import teammates.common.exception.EntityAlreadyExistsException;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.exception.InvalidParametersException;
import teammates.common.util.Assumption;
import teammates.common.util.Const;
import teammates.common.util.Sanitizer;
import teammates.common.util.StatusMessage;
import teammates.common.util.Const.StatusMessageColor;
import teammates.logic.api.GateKeeper;

/**
 * Action: add another instructor for an existent course of an instructor
 */
public class InstructorCourseInstructorAddAction extends Action {

    @Override
    protected ActionResult execute() throws EntityDoesNotExistException {

        String courseId = getRequestParamValue(Const.ParamsNames.COURSE_ID);
        Assumption.assertNotNull(courseId);
        String instructorName = getRequestParamValue(Const.ParamsNames.INSTRUCTOR_NAME);
        Assumption.assertNotNull(instructorName);
        String instructorEmail = getRequestParamValue(Const.ParamsNames.INSTRUCTOR_EMAIL);
        Assumption.assertNotNull(instructorEmail);
        
        InstructorAttributes instructor = logic.getInstructorForGoogleId(courseId, account.googleId);
        new GateKeeper().verifyAccessible(
                instructor, logic.getCourse(courseId), Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_INSTRUCTOR);
        
        InstructorAttributes instructorToAdd = extractCompleteInstructor(
                courseId, instructorName, instructorEmail);
        
        /* Process adding the instructor and setup status to be shown to user and admin */
        try {
            InstructorAttributes newInstructor = logic.createInstructor(instructorToAdd);
            logic.sendRegistrationInviteToInstructor(courseId, newInstructor);

            statusToUser.add(new StatusMessage(String.format(Const.StatusMessages.COURSE_INSTRUCTOR_ADDED,
                                                               instructorName, instructorEmail), StatusMessageColor.SUCCESS));
            statusToAdmin = "New instructor (<span class=\"bold\"> " + instructorEmail + "</span>)"
                    + " for Course <span class=\"bold\">[" + courseId + "]</span> created.<br>";
        } catch (EntityAlreadyExistsException e) {
            setStatusForException(e, Const.StatusMessages.COURSE_INSTRUCTOR_EXISTS);
        } catch (InvalidParametersException e) {
            setStatusForException(e);
        }
        
        RedirectResult redirectResult = createRedirectResult(Const.ActionURIs.INSTRUCTOR_COURSE_EDIT_PAGE);
        redirectResult.addResponseParam(Const.ParamsNames.COURSE_ID, courseId);
        return redirectResult;
    }
    
    private InstructorAttributes extractCompleteInstructor(String courseId, String instructorName, String instructorEmail) {
        String instructorRole = getRequestParamValue(Const.ParamsNames.INSTRUCTOR_ROLE_NAME);
        Assumption.assertNotNull(instructorRole);
        boolean isDisplayedToStudents = getRequestParamValue(Const.ParamsNames.INSTRUCTOR_IS_DISPLAYED_TO_STUDENT) != null;
        String displayedName = getRequestParamValue(Const.ParamsNames.INSTRUCTOR_DISPLAY_NAME);
        displayedName = (displayedName == null || displayedName.isEmpty()) ?
                InstructorAttributes.DEFAULT_DISPLAY_NAME : displayedName;
        instructorRole = Sanitizer.sanitizeName(instructorRole);
        displayedName = Sanitizer.sanitizeName(displayedName);
        
        InstructorAttributes instructorToAdd = updateBasicInstructorAttributes(courseId, instructorName, instructorEmail,
                instructorRole, isDisplayedToStudents, displayedName);
        
        if (instructorRole.equals(Const.InstructorPermissionRoleNames.INSTRUCTOR_PERMISSION_ROLE_CUSTOM)) {
            updateInstructorCourseLevelPrivileges(instructorToAdd);
        }
        
        updateInstructorWithSectionLevelPrivileges(courseId, instructorToAdd);
        
        instructorToAdd.privileges.validatePrivileges();
        
        instructorToAdd.instructorPrivilegesAsText = instructorToAdd.getTextFromInstructorPrivileges();
        
        return instructorToAdd;
    }
    
    private InstructorAttributes updateBasicInstructorAttributes(String courseId, String instructorName, String instructorEmail,
            String instructorRole, boolean isDisplayedToStudents, String displayedName) {
        String instrName = Sanitizer.sanitizeName(instructorName);
        String instrEmail = Sanitizer.sanitizeEmail(instructorEmail);
        String instrRole = Sanitizer.sanitizeName(instructorRole);
        String instrDisplayedName = Sanitizer.sanitizeName(displayedName);
        InstructorPrivileges privileges = new InstructorPrivileges(instructorRole);
        
        InstructorAttributes instructorToAdd = new InstructorAttributes(null, courseId, instrName, instrEmail,
                instrRole, isDisplayedToStudents, instrDisplayedName, privileges);
        
        instructorToAdd.instructorPrivilegesAsText = instructorToAdd.getTextFromInstructorPrivileges();
        
        return instructorToAdd;
    }

    private void updateInstructorCourseLevelPrivileges(
            InstructorAttributes instructorToAdd) {
        boolean isModifyCourseChecked = getRequestParamValue(Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_COURSE) != null;
        boolean isModifyInstructorChecked = getRequestParamValue(Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_INSTRUCTOR) != null;
        boolean isModifySessionChecked = getRequestParamValue(Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_SESSION) != null;
        boolean isModifyStudentChecked = getRequestParamValue(Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_STUDENT) != null;
        
        boolean isViewStudentInSectionsChecked = getRequestParamValue(Const.ParamsNames.INSTRUCTOR_PERMISSION_VIEW_STUDENT_IN_SECTIONS) != null;
        boolean isViewCommentInSectionsChecked = getRequestParamValue(Const.ParamsNames.INSTRUCTOR_PERMISSION_VIEW_COMMENT_IN_SECTIONS) != null;
        boolean isGiveCommentInSectionsChecked = getRequestParamValue(Const.ParamsNames.INSTRUCTOR_PERMISSION_GIVE_COMMENT_IN_SECTIONS) != null;
        boolean isModifyCommentInSectionsChecked = getRequestParamValue(Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_COMMENT_IN_SECTIONS) != null;
        
        boolean isViewSessionInSectionsChecked = getRequestParamValue(Const.ParamsNames.INSTRUCTOR_PERMISSION_VIEW_SESSION_IN_SECTIONS) != null;
        boolean isSubmitSessionInSectionsChecked = getRequestParamValue(Const.ParamsNames.INSTRUCTOR_PERMISSION_SUBMIT_SESSION_IN_SECTIONS) != null;
        boolean isModifySessionInSectionsChecked = getRequestParamValue(Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_SESSION_COMMENT_IN_SECTIONS) != null;
        
        instructorToAdd.privileges.updatePrivilege(Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_COURSE, isModifyCourseChecked);
        instructorToAdd.privileges.updatePrivilege(Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_INSTRUCTOR, isModifyInstructorChecked);
        instructorToAdd.privileges.updatePrivilege(Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_SESSION, isModifySessionChecked);
        instructorToAdd.privileges.updatePrivilege(Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_STUDENT, isModifyStudentChecked);
        
        instructorToAdd.privileges.updatePrivilege(Const.ParamsNames.INSTRUCTOR_PERMISSION_VIEW_STUDENT_IN_SECTIONS, isViewStudentInSectionsChecked);
        instructorToAdd.privileges.updatePrivilege(Const.ParamsNames.INSTRUCTOR_PERMISSION_VIEW_COMMENT_IN_SECTIONS, isViewCommentInSectionsChecked);
        instructorToAdd.privileges.updatePrivilege(Const.ParamsNames.INSTRUCTOR_PERMISSION_GIVE_COMMENT_IN_SECTIONS, isGiveCommentInSectionsChecked);
        instructorToAdd.privileges.updatePrivilege(Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_COMMENT_IN_SECTIONS, isModifyCommentInSectionsChecked);
        
        instructorToAdd.privileges.updatePrivilege(Const.ParamsNames.INSTRUCTOR_PERMISSION_VIEW_SESSION_IN_SECTIONS, isViewSessionInSectionsChecked);
        instructorToAdd.privileges.updatePrivilege(Const.ParamsNames.INSTRUCTOR_PERMISSION_SUBMIT_SESSION_IN_SECTIONS, isSubmitSessionInSectionsChecked);
        instructorToAdd.privileges.updatePrivilege(Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_SESSION_COMMENT_IN_SECTIONS, isModifySessionInSectionsChecked);
    }
    
    private void updateInstructorWithSectionLevelPrivileges(String courseId, InstructorAttributes instructorToAdd){
        List<String> sectionNames = null;
        try {
            sectionNames = logic.getSectionNamesForCourse(courseId);
        } catch(EntityDoesNotExistException e) {
            return ;
        }
        HashMap<String, Boolean> sectionNamesTable = new HashMap<String, Boolean>();
        for (String sectionName : sectionNames) {
            sectionNamesTable.put(sectionName, false);
        }

        List<String> feedbackNames = new ArrayList<String>();

        List<FeedbackSessionAttributes> feedbacks = logic.getFeedbackSessionsForCourse(courseId);
        for (FeedbackSessionAttributes feedback : feedbacks) {
            feedbackNames.add(feedback.feedbackSessionName);
        }
        HashMap<String, List<String>> sectionNamesMap = extractSectionNames(instructorToAdd, sectionNames, sectionNamesTable);
        for (Entry<String, List<String>> entry : sectionNamesMap.entrySet()) {
            updateInstructorPrivilegesForSectionInSectionLevel(entry.getKey(), entry.getValue(), instructorToAdd);
            String setSessionsStr = getRequestParamValue("is" + entry.getKey() + "sessionsset");
            boolean isSessionsSpecial = setSessionsStr != null && setSessionsStr.equals("true");
            if (isSessionsSpecial) {
                updateInstructorPrivilegesForSectionInSessionLevel(entry.getKey(), entry.getValue(), feedbackNames, instructorToAdd);
            } else {
                removeSessionLevelPrivileges(instructorToAdd, entry.getValue());
            }
        }
        for (Entry<String, Boolean> entry : sectionNamesTable.entrySet()) {
            if (!entry.getValue().booleanValue()) {
                instructorToAdd.privileges.removeSectionLevelPrivileges(entry.getKey());
            }
        }
    }

    private void removeSessionLevelPrivileges(InstructorAttributes instructorToAdd, List<String> sectionNames) {
        for (String sectionName : sectionNames) {
            instructorToAdd.privileges.removeSessionsPrivilegesForSection(sectionName);
        }
    }

    private HashMap<String, List<String>> extractSectionNames(
            InstructorAttributes instructorToAdd, List<String> sectionNames, HashMap<String, Boolean> sectionNamesTable) {
        HashMap<String, List<String>> sectionNamesMap = new HashMap<String, List<String>>();
        if (instructorToAdd.role.equals(Const.InstructorPermissionRoleNames.INSTRUCTOR_PERMISSION_ROLE_CUSTOM)) {
            for (int i=0; i<sectionNames.size(); i++) {
                String setSectionGroupStr = getRequestParamValue("is" + Const.ParamsNames.INSTRUCTOR_SECTION_GROUP + i + "set");
                boolean isSectionGroupSpecial = setSectionGroupStr != null && setSectionGroupStr.equals("true");
                for (int j=0; j<sectionNames.size(); j++) {
                    String valueForSectionName = getRequestParamValue(Const.ParamsNames.INSTRUCTOR_SECTION_GROUP + i + Const.ParamsNames.INSTRUCTOR_SECTION + j);
                    if (isSectionGroupSpecial && valueForSectionName != null && sectionNamesTable.containsKey(valueForSectionName)) {
                        if (sectionNamesMap.get(Const.ParamsNames.INSTRUCTOR_SECTION_GROUP + i) == null) {
                            sectionNamesMap.put(Const.ParamsNames.INSTRUCTOR_SECTION_GROUP + i, new ArrayList<String>());
                        }
                        sectionNamesMap.get(Const.ParamsNames.INSTRUCTOR_SECTION_GROUP + i).add(valueForSectionName);
                        sectionNamesTable.put(valueForSectionName, true);
                    }
                }
            }
        }
        return sectionNamesMap;
    }

    private void updateInstructorPrivilegesForSectionInSectionLevel(String sectionParam, List<String> sectionNames, InstructorAttributes instructorToAdd) {
        boolean isViewStudentInSectionsChecked = getRequestParamValue(Const.ParamsNames.INSTRUCTOR_PERMISSION_VIEW_STUDENT_IN_SECTIONS + sectionParam) != null;
        boolean isViewCommentInSectionsChecked = getRequestParamValue(Const.ParamsNames.INSTRUCTOR_PERMISSION_VIEW_COMMENT_IN_SECTIONS + sectionParam) != null;
        boolean isGiveCommentInSectionsChecked = getRequestParamValue(Const.ParamsNames.INSTRUCTOR_PERMISSION_GIVE_COMMENT_IN_SECTIONS + sectionParam) != null;
        boolean isModifyCommentInSectionsChecked = getRequestParamValue(Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_COMMENT_IN_SECTIONS + sectionParam) != null;
        
        boolean isViewSessionInSectionsChecked = getRequestParamValue(Const.ParamsNames.INSTRUCTOR_PERMISSION_VIEW_SESSION_IN_SECTIONS + sectionParam) != null;
        boolean isSubmitSessionInSectionsChecked = getRequestParamValue(Const.ParamsNames.INSTRUCTOR_PERMISSION_SUBMIT_SESSION_IN_SECTIONS + sectionParam) != null;
        boolean isModifySessionInSectionsChecked = getRequestParamValue(Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_SESSION_COMMENT_IN_SECTIONS + sectionParam) != null;
        
        for (String sectionName : sectionNames) {
            instructorToAdd.privileges.updatePrivilege(sectionName, Const.ParamsNames.INSTRUCTOR_PERMISSION_VIEW_STUDENT_IN_SECTIONS, isViewStudentInSectionsChecked);
            instructorToAdd.privileges.updatePrivilege(sectionName, Const.ParamsNames.INSTRUCTOR_PERMISSION_VIEW_COMMENT_IN_SECTIONS, isViewCommentInSectionsChecked);
            instructorToAdd.privileges.updatePrivilege(sectionName, Const.ParamsNames.INSTRUCTOR_PERMISSION_GIVE_COMMENT_IN_SECTIONS, isGiveCommentInSectionsChecked);
            instructorToAdd.privileges.updatePrivilege(sectionName, Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_COMMENT_IN_SECTIONS, isModifyCommentInSectionsChecked);
            instructorToAdd.privileges.updatePrivilege(sectionName, Const.ParamsNames.INSTRUCTOR_PERMISSION_VIEW_SESSION_IN_SECTIONS, isViewSessionInSectionsChecked);
            instructorToAdd.privileges.updatePrivilege(sectionName, Const.ParamsNames.INSTRUCTOR_PERMISSION_SUBMIT_SESSION_IN_SECTIONS, isSubmitSessionInSectionsChecked);
            instructorToAdd.privileges.updatePrivilege(sectionName, Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_SESSION_COMMENT_IN_SECTIONS, isModifySessionInSectionsChecked);
        }
    }

    private void updateInstructorPrivilegesForSectionInSessionLevel(String sectionParam,
            List<String> sectionNames, List<String> feedbackNames, InstructorAttributes instructorToAdd) {
        for (String feedbackName : feedbackNames) {
            boolean isViewSessionInSectionsChecked = getRequestParamValue(Const.ParamsNames.INSTRUCTOR_PERMISSION_VIEW_SESSION_IN_SECTIONS 
                    + sectionParam + "feedback" + feedbackName) != null;
            boolean isSubmitSessionInSectionsChecked = getRequestParamValue(Const.ParamsNames.INSTRUCTOR_PERMISSION_SUBMIT_SESSION_IN_SECTIONS
                    + sectionParam + "feedback" + feedbackName) != null;
            boolean isModifySessionInSectionsChecked = getRequestParamValue(Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_SESSION_COMMENT_IN_SECTIONS
                    + sectionParam + "feedback" + feedbackName) != null;
            
            for (String sectionName : sectionNames) {
                instructorToAdd.privileges.updatePrivilege(sectionName, feedbackName,
                        Const.ParamsNames.INSTRUCTOR_PERMISSION_VIEW_SESSION_IN_SECTIONS, isViewSessionInSectionsChecked);
                instructorToAdd.privileges.updatePrivilege(sectionName, feedbackName,
                        Const.ParamsNames.INSTRUCTOR_PERMISSION_SUBMIT_SESSION_IN_SECTIONS, isSubmitSessionInSectionsChecked);
                instructorToAdd.privileges.updatePrivilege(sectionName, feedbackName,
                        Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_SESSION_COMMENT_IN_SECTIONS, isModifySessionInSectionsChecked);
            }
        }
    }
}
