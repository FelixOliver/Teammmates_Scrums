package teammates.test.cases.common;

import static org.testng.AssertJUnit.assertEquals;
import static org.testng.AssertJUnit.assertTrue;
import static org.testng.AssertJUnit.assertFalse;

import java.util.HashMap;

import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import teammates.common.datatransfer.InstructorPrivileges;
import teammates.common.util.Const;
import teammates.test.cases.BaseTestCase;

public class InstructorPrivilegesTest extends BaseTestCase {
    @BeforeClass
    public static void setupClass() throws Exception {
        printTestClassHeader();
    }

    @Test
    public void testSetDefault() {
        InstructorPrivileges privileges = new InstructorPrivileges();
        HashMap<String, Boolean> courseLevelMap;
        
        // co-owner: all true
        privileges.setDefaultPrivilegesForCoowner();
        courseLevelMap = privileges.getCourseLevelPrivileges();
        assertEquals(Boolean.valueOf(true), courseLevelMap.get(Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_COURSE));
        assertEquals(Boolean.valueOf(true), courseLevelMap.get(Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_INSTRUCTOR));
        assertEquals(Boolean.valueOf(true), courseLevelMap.get(Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_SESSION));
        assertEquals(Boolean.valueOf(true), courseLevelMap.get(Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_STUDENT));
        assertEquals(Boolean.valueOf(true), courseLevelMap.get(Const.ParamsNames.INSTRUCTOR_PERMISSION_VIEW_STUDENT_IN_SECTIONS));
        assertEquals(Boolean.valueOf(true), courseLevelMap.get(Const.ParamsNames.INSTRUCTOR_PERMISSION_VIEW_COMMENT_IN_SECTIONS));
        assertEquals(Boolean.valueOf(true), courseLevelMap.get(Const.ParamsNames.INSTRUCTOR_PERMISSION_GIVE_COMMENT_IN_SECTIONS));
        assertEquals(Boolean.valueOf(true), courseLevelMap.get(Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_COMMENT_IN_SECTIONS));
        assertEquals(Boolean.valueOf(true), courseLevelMap.get(Const.ParamsNames.INSTRUCTOR_PERMISSION_VIEW_SESSION_IN_SECTIONS));
        assertEquals(Boolean.valueOf(true), courseLevelMap.get(Const.ParamsNames.INSTRUCTOR_PERMISSION_SUBMIT_SESSION_IN_SECTIONS));
        assertEquals(Boolean.valueOf(true), courseLevelMap.get(Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_SESSION_COMMENT_IN_SECTIONS));
        String invalidKey = "invalid key";
        assertEquals(null, courseLevelMap.get(invalidKey));
        assertEquals(true, privileges.getSessionLevelPrivileges().isEmpty());
        
        // manager: only one false
        privileges.setDefaultPrivilegesForManager();
        courseLevelMap = privileges.getCourseLevelPrivileges();
        assertEquals(Boolean.valueOf(false), courseLevelMap.get(Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_COURSE));
        assertEquals(Boolean.valueOf(true), courseLevelMap.get(Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_INSTRUCTOR));
        assertEquals(Boolean.valueOf(true), courseLevelMap.get(Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_SESSION));
        assertEquals(Boolean.valueOf(true), courseLevelMap.get(Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_STUDENT));
        assertEquals(Boolean.valueOf(true), courseLevelMap.get(Const.ParamsNames.INSTRUCTOR_PERMISSION_VIEW_STUDENT_IN_SECTIONS));
        assertEquals(Boolean.valueOf(true), courseLevelMap.get(Const.ParamsNames.INSTRUCTOR_PERMISSION_VIEW_COMMENT_IN_SECTIONS));
        assertEquals(Boolean.valueOf(true), courseLevelMap.get(Const.ParamsNames.INSTRUCTOR_PERMISSION_GIVE_COMMENT_IN_SECTIONS));
        assertEquals(Boolean.valueOf(true), courseLevelMap.get(Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_COMMENT_IN_SECTIONS));
        assertEquals(Boolean.valueOf(true), courseLevelMap.get(Const.ParamsNames.INSTRUCTOR_PERMISSION_VIEW_SESSION_IN_SECTIONS));
        assertEquals(Boolean.valueOf(true), courseLevelMap.get(Const.ParamsNames.INSTRUCTOR_PERMISSION_SUBMIT_SESSION_IN_SECTIONS));
        assertEquals(Boolean.valueOf(true), courseLevelMap.get(Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_SESSION_COMMENT_IN_SECTIONS));
        
        // observer: view only
        privileges.setDefaultPrivilegesForObserver();
        courseLevelMap = privileges.getCourseLevelPrivileges();
        assertEquals(Boolean.valueOf(false), courseLevelMap.get(Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_COURSE));
        assertEquals(Boolean.valueOf(false), courseLevelMap.get(Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_INSTRUCTOR));
        assertEquals(Boolean.valueOf(false), courseLevelMap.get(Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_SESSION));
        assertEquals(Boolean.valueOf(false), courseLevelMap.get(Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_STUDENT));
        assertEquals(Boolean.valueOf(true), courseLevelMap.get(Const.ParamsNames.INSTRUCTOR_PERMISSION_VIEW_STUDENT_IN_SECTIONS));
        assertEquals(Boolean.valueOf(true), courseLevelMap.get(Const.ParamsNames.INSTRUCTOR_PERMISSION_VIEW_COMMENT_IN_SECTIONS));
        assertEquals(Boolean.valueOf(false), courseLevelMap.get(Const.ParamsNames.INSTRUCTOR_PERMISSION_GIVE_COMMENT_IN_SECTIONS));
        assertEquals(Boolean.valueOf(false), courseLevelMap.get(Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_COMMENT_IN_SECTIONS));
        assertEquals(Boolean.valueOf(true), courseLevelMap.get(Const.ParamsNames.INSTRUCTOR_PERMISSION_VIEW_SESSION_IN_SECTIONS));
        assertEquals(Boolean.valueOf(false), courseLevelMap.get(Const.ParamsNames.INSTRUCTOR_PERMISSION_SUBMIT_SESSION_IN_SECTIONS));
        assertEquals(Boolean.valueOf(false), courseLevelMap.get(Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_SESSION_COMMENT_IN_SECTIONS));
        
        // tutor
        privileges.setDefaultPrivilegesForTutor();
        courseLevelMap = privileges.getCourseLevelPrivileges();
        assertEquals(Boolean.valueOf(false), courseLevelMap.get(Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_COURSE));
        assertEquals(Boolean.valueOf(false), courseLevelMap.get(Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_INSTRUCTOR));
        assertEquals(Boolean.valueOf(false), courseLevelMap.get(Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_SESSION));
        assertEquals(Boolean.valueOf(false), courseLevelMap.get(Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_STUDENT));
        assertEquals(Boolean.valueOf(true), courseLevelMap.get(Const.ParamsNames.INSTRUCTOR_PERMISSION_VIEW_STUDENT_IN_SECTIONS));
        assertEquals(Boolean.valueOf(false), courseLevelMap.get(Const.ParamsNames.INSTRUCTOR_PERMISSION_VIEW_COMMENT_IN_SECTIONS));
        assertEquals(Boolean.valueOf(true), courseLevelMap.get(Const.ParamsNames.INSTRUCTOR_PERMISSION_GIVE_COMMENT_IN_SECTIONS));
        assertEquals(Boolean.valueOf(false), courseLevelMap.get(Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_COMMENT_IN_SECTIONS));
        assertEquals(Boolean.valueOf(true), courseLevelMap.get(Const.ParamsNames.INSTRUCTOR_PERMISSION_VIEW_SESSION_IN_SECTIONS));
        assertEquals(Boolean.valueOf(true), courseLevelMap.get(Const.ParamsNames.INSTRUCTOR_PERMISSION_SUBMIT_SESSION_IN_SECTIONS));
        assertEquals(Boolean.valueOf(false), courseLevelMap.get(Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_SESSION_COMMENT_IN_SECTIONS));
        
        // helper
        privileges.setDefaultPrivilegesForCustom();
        courseLevelMap = privileges.getCourseLevelPrivileges();
        assertEquals(Boolean.valueOf(false), courseLevelMap.get(Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_COURSE));
        assertEquals(Boolean.valueOf(false), courseLevelMap.get(Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_INSTRUCTOR));
        assertEquals(Boolean.valueOf(false), courseLevelMap.get(Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_SESSION));
        assertEquals(Boolean.valueOf(false), courseLevelMap.get(Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_STUDENT));
        assertEquals(Boolean.valueOf(false), courseLevelMap.get(Const.ParamsNames.INSTRUCTOR_PERMISSION_VIEW_STUDENT_IN_SECTIONS));
        assertEquals(Boolean.valueOf(false), courseLevelMap.get(Const.ParamsNames.INSTRUCTOR_PERMISSION_VIEW_COMMENT_IN_SECTIONS));
        assertEquals(Boolean.valueOf(false), courseLevelMap.get(Const.ParamsNames.INSTRUCTOR_PERMISSION_GIVE_COMMENT_IN_SECTIONS));
        assertEquals(Boolean.valueOf(false), courseLevelMap.get(Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_COMMENT_IN_SECTIONS));
        assertEquals(Boolean.valueOf(false), courseLevelMap.get(Const.ParamsNames.INSTRUCTOR_PERMISSION_VIEW_SESSION_IN_SECTIONS));
        assertEquals(Boolean.valueOf(false), courseLevelMap.get(Const.ParamsNames.INSTRUCTOR_PERMISSION_SUBMIT_SESSION_IN_SECTIONS));
        assertEquals(Boolean.valueOf(false), courseLevelMap.get(Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_SESSION_COMMENT_IN_SECTIONS));
    }
    
    @Test
    public void testConstructor() {
        InstructorPrivileges privileges = new InstructorPrivileges();
        InstructorPrivileges privileges1 = new InstructorPrivileges(Const.InstructorPermissionRoleNames.INSTRUCTOR_PERMISSION_ROLE_COOWNER);
        
        privileges.setDefaultPrivilegesForCoowner();
        assertEquals(privileges, privileges1);
        
        privileges.setDefaultPrivilegesForManager();
        privileges1 = new InstructorPrivileges(Const.InstructorPermissionRoleNames.INSTRUCTOR_PERMISSION_ROLE_MANAGER);
        assertEquals(privileges, privileges1);
        
        privileges.setDefaultPrivilegesForObserver();
        privileges1 = new InstructorPrivileges(Const.InstructorPermissionRoleNames.INSTRUCTOR_PERMISSION_ROLE_OBSERVER);
        assertEquals(privileges, privileges1);
        
        privileges.setDefaultPrivilegesForTutor();
        privileges1 = new InstructorPrivileges(Const.InstructorPermissionRoleNames.INSTRUCTOR_PERMISSION_ROLE_TUTOR);
        assertEquals(privileges, privileges1);
        
        privileges.setDefaultPrivilegesForCustom();
        privileges1 = new InstructorPrivileges(Const.InstructorPermissionRoleNames.INSTRUCTOR_PERMISSION_ROLE_CUSTOM);
        assertEquals(privileges, privileges1);
        
        privileges1 = new InstructorPrivileges("random string");
        assertEquals(privileges, privileges1);
    }
    
    @Test
    public void testIsPrivilegeNameValid() {
        assertTrue(InstructorPrivileges.isPrivilegeNameValid(Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_COURSE));
        assertTrue(InstructorPrivileges.isPrivilegeNameValid(Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_INSTRUCTOR));
        assertTrue(InstructorPrivileges.isPrivilegeNameValid(Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_SESSION));
        assertTrue(InstructorPrivileges.isPrivilegeNameValid(Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_STUDENT));
        assertTrue(InstructorPrivileges.isPrivilegeNameValid(Const.ParamsNames.INSTRUCTOR_PERMISSION_VIEW_STUDENT_IN_SECTIONS));
        assertTrue(InstructorPrivileges.isPrivilegeNameValid(Const.ParamsNames.INSTRUCTOR_PERMISSION_VIEW_COMMENT_IN_SECTIONS));
        assertTrue(InstructorPrivileges.isPrivilegeNameValid(Const.ParamsNames.INSTRUCTOR_PERMISSION_GIVE_COMMENT_IN_SECTIONS));
        assertTrue(InstructorPrivileges.isPrivilegeNameValid(Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_COMMENT_IN_SECTIONS));
        assertTrue(InstructorPrivileges.isPrivilegeNameValid(Const.ParamsNames.INSTRUCTOR_PERMISSION_VIEW_SESSION_IN_SECTIONS));
        assertTrue(InstructorPrivileges.isPrivilegeNameValid(Const.ParamsNames.INSTRUCTOR_PERMISSION_SUBMIT_SESSION_IN_SECTIONS));
        assertTrue(InstructorPrivileges.isPrivilegeNameValid(Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_SESSION_COMMENT_IN_SECTIONS));
        String invalidPrivileName = "invalidPrivilegeName";
        assertFalse(InstructorPrivileges.isPrivilegeNameValid(invalidPrivileName));
        
        assertFalse(InstructorPrivileges.isPrivilegeNameValidForSectionLevel(Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_COURSE));
        assertFalse(InstructorPrivileges.isPrivilegeNameValidForSectionLevel(Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_INSTRUCTOR));
        assertFalse(InstructorPrivileges.isPrivilegeNameValidForSectionLevel(Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_SESSION));
        assertFalse(InstructorPrivileges.isPrivilegeNameValidForSectionLevel(Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_STUDENT));
        assertTrue(InstructorPrivileges.isPrivilegeNameValidForSectionLevel(Const.ParamsNames.INSTRUCTOR_PERMISSION_VIEW_STUDENT_IN_SECTIONS));
        assertTrue(InstructorPrivileges.isPrivilegeNameValidForSectionLevel(Const.ParamsNames.INSTRUCTOR_PERMISSION_VIEW_COMMENT_IN_SECTIONS));
        assertTrue(InstructorPrivileges.isPrivilegeNameValidForSectionLevel(Const.ParamsNames.INSTRUCTOR_PERMISSION_GIVE_COMMENT_IN_SECTIONS));
        assertTrue(InstructorPrivileges.isPrivilegeNameValidForSectionLevel(Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_COMMENT_IN_SECTIONS));
        assertTrue(InstructorPrivileges.isPrivilegeNameValidForSectionLevel(Const.ParamsNames.INSTRUCTOR_PERMISSION_VIEW_SESSION_IN_SECTIONS));
        assertTrue(InstructorPrivileges.isPrivilegeNameValidForSectionLevel(Const.ParamsNames.INSTRUCTOR_PERMISSION_SUBMIT_SESSION_IN_SECTIONS));
        assertTrue(InstructorPrivileges.isPrivilegeNameValidForSectionLevel(Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_SESSION_COMMENT_IN_SECTIONS));
        assertFalse(InstructorPrivileges.isPrivilegeNameValidForSectionLevel(invalidPrivileName));
        
        assertFalse(InstructorPrivileges.isPrivilegeNameValidForSessionLevel(Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_COURSE));
        assertFalse(InstructorPrivileges.isPrivilegeNameValidForSessionLevel(Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_INSTRUCTOR));
        assertFalse(InstructorPrivileges.isPrivilegeNameValidForSessionLevel(Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_SESSION));
        assertFalse(InstructorPrivileges.isPrivilegeNameValidForSessionLevel(Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_STUDENT));
        assertFalse(InstructorPrivileges.isPrivilegeNameValidForSessionLevel(Const.ParamsNames.INSTRUCTOR_PERMISSION_VIEW_STUDENT_IN_SECTIONS));
        assertFalse(InstructorPrivileges.isPrivilegeNameValidForSessionLevel(Const.ParamsNames.INSTRUCTOR_PERMISSION_VIEW_COMMENT_IN_SECTIONS));
        assertFalse(InstructorPrivileges.isPrivilegeNameValidForSessionLevel(Const.ParamsNames.INSTRUCTOR_PERMISSION_GIVE_COMMENT_IN_SECTIONS));
        assertFalse(InstructorPrivileges.isPrivilegeNameValidForSessionLevel(Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_COMMENT_IN_SECTIONS));
        assertTrue(InstructorPrivileges.isPrivilegeNameValidForSessionLevel(Const.ParamsNames.INSTRUCTOR_PERMISSION_VIEW_SESSION_IN_SECTIONS));
        assertTrue(InstructorPrivileges.isPrivilegeNameValidForSessionLevel(Const.ParamsNames.INSTRUCTOR_PERMISSION_SUBMIT_SESSION_IN_SECTIONS));
        assertTrue(InstructorPrivileges.isPrivilegeNameValidForSessionLevel(Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_SESSION_COMMENT_IN_SECTIONS));
        assertFalse(InstructorPrivileges.isPrivilegeNameValidForSessionLevel(invalidPrivileName));
    }
    
    @Test
    public void testUpdatePrivilegeInCourseLevel() {
        InstructorPrivileges privileges = new InstructorPrivileges(Const.InstructorPermissionRoleNames.INSTRUCTOR_PERMISSION_ROLE_COOWNER);
        
        privileges.updatePrivilege(Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_COURSE, false);
        HashMap<String, Boolean> courseLevelPrivileges = privileges.getCourseLevelPrivileges();
        assertEquals(Boolean.valueOf(false), courseLevelPrivileges.get(Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_COURSE));
        assertEquals(Boolean.valueOf(true), courseLevelPrivileges.get(Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_INSTRUCTOR));
        assertEquals(Boolean.valueOf(true), courseLevelPrivileges.get(Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_SESSION));
        assertEquals(Boolean.valueOf(true), courseLevelPrivileges.get(Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_STUDENT));
        assertEquals(Boolean.valueOf(true), courseLevelPrivileges.get(Const.ParamsNames.INSTRUCTOR_PERMISSION_VIEW_STUDENT_IN_SECTIONS));
        assertEquals(Boolean.valueOf(true), courseLevelPrivileges.get(Const.ParamsNames.INSTRUCTOR_PERMISSION_VIEW_COMMENT_IN_SECTIONS));
        assertEquals(Boolean.valueOf(true), courseLevelPrivileges.get(Const.ParamsNames.INSTRUCTOR_PERMISSION_GIVE_COMMENT_IN_SECTIONS));
        assertEquals(Boolean.valueOf(true), courseLevelPrivileges.get(Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_COMMENT_IN_SECTIONS));
        assertEquals(Boolean.valueOf(true), courseLevelPrivileges.get(Const.ParamsNames.INSTRUCTOR_PERMISSION_VIEW_SESSION_IN_SECTIONS));
        assertEquals(Boolean.valueOf(true), courseLevelPrivileges.get(Const.ParamsNames.INSTRUCTOR_PERMISSION_SUBMIT_SESSION_IN_SECTIONS));
        assertEquals(Boolean.valueOf(true), courseLevelPrivileges.get(Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_SESSION_COMMENT_IN_SECTIONS));
        
        String invalidPrivilegeName = "invalidPrivilegeName";
        privileges.updatePrivilege(invalidPrivilegeName, false);
        courseLevelPrivileges = privileges.getCourseLevelPrivileges();
        assertFalse(courseLevelPrivileges.containsKey(invalidPrivilegeName));
        assertEquals(Boolean.valueOf(false), courseLevelPrivileges.get(Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_COURSE));
        assertEquals(Boolean.valueOf(true), courseLevelPrivileges.get(Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_INSTRUCTOR));
        assertEquals(Boolean.valueOf(true), courseLevelPrivileges.get(Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_SESSION));
        assertEquals(Boolean.valueOf(true), courseLevelPrivileges.get(Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_STUDENT));
        assertEquals(Boolean.valueOf(true), courseLevelPrivileges.get(Const.ParamsNames.INSTRUCTOR_PERMISSION_VIEW_STUDENT_IN_SECTIONS));
        assertEquals(Boolean.valueOf(true), courseLevelPrivileges.get(Const.ParamsNames.INSTRUCTOR_PERMISSION_VIEW_COMMENT_IN_SECTIONS));
        assertEquals(Boolean.valueOf(true), courseLevelPrivileges.get(Const.ParamsNames.INSTRUCTOR_PERMISSION_GIVE_COMMENT_IN_SECTIONS));
        assertEquals(Boolean.valueOf(true), courseLevelPrivileges.get(Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_COMMENT_IN_SECTIONS));
        assertEquals(Boolean.valueOf(true), courseLevelPrivileges.get(Const.ParamsNames.INSTRUCTOR_PERMISSION_VIEW_SESSION_IN_SECTIONS));
        assertEquals(Boolean.valueOf(true), courseLevelPrivileges.get(Const.ParamsNames.INSTRUCTOR_PERMISSION_SUBMIT_SESSION_IN_SECTIONS));
        assertEquals(Boolean.valueOf(true), courseLevelPrivileges.get(Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_SESSION_COMMENT_IN_SECTIONS));
    }
    
    @Test
    public void testUpdatePrivilegeInSectionLevel() {
        InstructorPrivileges privileges = new InstructorPrivileges(Const.InstructorPermissionRoleNames.INSTRUCTOR_PERMISSION_ROLE_COOWNER);
        String sectionId = "sectionId";
        
        privileges.updatePrivilege(sectionId, Const.ParamsNames.INSTRUCTOR_PERMISSION_VIEW_STUDENT_IN_SECTIONS, false);
        assertEquals(1, privileges.getSectionLevelPrivileges().size());
        assertTrue(privileges.getSectionLevelPrivileges().containsKey(sectionId));
        
        privileges.updatePrivilege(sectionId, Const.ParamsNames.INSTRUCTOR_PERMISSION_VIEW_COMMENT_IN_SECTIONS, false);
        assertEquals(1, privileges.getSectionLevelPrivileges().size());
        assertTrue(privileges.getSectionLevelPrivileges().containsKey(sectionId));
        HashMap<String, Boolean> sectionPrivilges = privileges.getSectionLevelPrivileges().get(sectionId);
        assertEquals(null, sectionPrivilges.get(Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_COURSE));
        assertEquals(Boolean.valueOf(false), sectionPrivilges.get(Const.ParamsNames.INSTRUCTOR_PERMISSION_VIEW_STUDENT_IN_SECTIONS));
        assertEquals(Boolean.valueOf(false), sectionPrivilges.get(Const.ParamsNames.INSTRUCTOR_PERMISSION_VIEW_COMMENT_IN_SECTIONS));
        assertEquals(null, sectionPrivilges.get(Const.ParamsNames.INSTRUCTOR_PERMISSION_GIVE_COMMENT_IN_SECTIONS));
        assertEquals(null, sectionPrivilges.get(Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_COMMENT_IN_SECTIONS));
        assertEquals(null, sectionPrivilges.get(Const.ParamsNames.INSTRUCTOR_PERMISSION_VIEW_SESSION_IN_SECTIONS));
        assertEquals(null, sectionPrivilges.get(Const.ParamsNames.INSTRUCTOR_PERMISSION_SUBMIT_SESSION_IN_SECTIONS));
        assertEquals(null, sectionPrivilges.get(Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_SESSION_COMMENT_IN_SECTIONS));
        
        privileges.updatePrivilege(sectionId, Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_COURSE, false);
        sectionPrivilges = privileges.getSectionLevelPrivileges().get(sectionId);
        assertEquals(1, privileges.getSectionLevelPrivileges().size());
        assertTrue(privileges.getSectionLevelPrivileges().containsKey(sectionId));
        assertEquals(null, sectionPrivilges.get(Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_COURSE));
        assertEquals(Boolean.valueOf(false), sectionPrivilges.get(Const.ParamsNames.INSTRUCTOR_PERMISSION_VIEW_STUDENT_IN_SECTIONS));
        assertEquals(Boolean.valueOf(false), sectionPrivilges.get(Const.ParamsNames.INSTRUCTOR_PERMISSION_VIEW_COMMENT_IN_SECTIONS));
        assertEquals(null, sectionPrivilges.get(Const.ParamsNames.INSTRUCTOR_PERMISSION_GIVE_COMMENT_IN_SECTIONS));
        assertEquals(null, sectionPrivilges.get(Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_COMMENT_IN_SECTIONS));
        assertEquals(null, sectionPrivilges.get(Const.ParamsNames.INSTRUCTOR_PERMISSION_VIEW_SESSION_IN_SECTIONS));
        assertEquals(null, sectionPrivilges.get(Const.ParamsNames.INSTRUCTOR_PERMISSION_SUBMIT_SESSION_IN_SECTIONS));
        assertEquals(null, sectionPrivilges.get(Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_SESSION_COMMENT_IN_SECTIONS));
        
        String invalidPrivilegeName = "invalidPrivilegeName";
        privileges.updatePrivilege(sectionId, invalidPrivilegeName, false);
        sectionPrivilges = privileges.getSectionLevelPrivileges().get(sectionId);
        assertEquals(1, privileges.getSectionLevelPrivileges().size());
        assertTrue(privileges.getSectionLevelPrivileges().containsKey(sectionId));
        assertEquals(2, privileges.getSectionLevelPrivileges().get(sectionId).size());
        
    }
    
    @Test
    public void testUpdatePrivilegesInSectionLevel() {
        InstructorPrivileges privileges = new InstructorPrivileges(Const.InstructorPermissionRoleNames.INSTRUCTOR_PERMISSION_ROLE_COOWNER);
        String sectionId = "sectionId";
        HashMap<String, Boolean> privilegeMap = new HashMap<String, Boolean>();
        
        privilegeMap.put(Const.ParamsNames.INSTRUCTOR_PERMISSION_VIEW_STUDENT_IN_SECTIONS, false);
        privilegeMap.put(Const.ParamsNames.INSTRUCTOR_PERMISSION_VIEW_COMMENT_IN_SECTIONS, false);
        
        privileges.updatePrivileges(sectionId, privilegeMap);
        HashMap<String, Boolean> sectionPrivileges = privileges.getSectionLevelPrivileges().get(sectionId);
        assertEquals(1, privileges.getSectionLevelPrivileges().size());
        assertTrue(privileges.getSectionLevelPrivileges().containsKey(sectionId));
        assertEquals(2, sectionPrivileges.size());
        assertEquals(Boolean.valueOf(false), sectionPrivileges.get(Const.ParamsNames.INSTRUCTOR_PERMISSION_VIEW_STUDENT_IN_SECTIONS));
        assertEquals(Boolean.valueOf(false), sectionPrivileges.get(Const.ParamsNames.INSTRUCTOR_PERMISSION_VIEW_COMMENT_IN_SECTIONS));
        
        String invalidPrivilegeName = "invalidPrivilegeName";
        privilegeMap.put(invalidPrivilegeName, false);
        privileges.updatePrivileges(sectionId, privilegeMap);
        sectionPrivileges = privileges.getSectionLevelPrivileges().get(sectionId);
        assertEquals(1, privileges.getSectionLevelPrivileges().size());
        assertTrue(privileges.getSectionLevelPrivileges().containsKey(sectionId));
        assertEquals(2, sectionPrivileges.size());
        assertEquals(Boolean.valueOf(false), sectionPrivileges.get(Const.ParamsNames.INSTRUCTOR_PERMISSION_VIEW_STUDENT_IN_SECTIONS));
        assertEquals(Boolean.valueOf(false), sectionPrivileges.get(Const.ParamsNames.INSTRUCTOR_PERMISSION_VIEW_COMMENT_IN_SECTIONS));
    }
    
    @Test
    public void testUpdatePrivilegeInSessionLevel() {
        InstructorPrivileges privileges = new InstructorPrivileges(Const.InstructorPermissionRoleNames.INSTRUCTOR_PERMISSION_ROLE_COOWNER);
        String sectionId = "sectionId";
        String sessionId = "sessionId";
        
        privileges.updatePrivilege(sectionId, sessionId, Const.ParamsNames.INSTRUCTOR_PERMISSION_VIEW_SESSION_IN_SECTIONS, false);
        privileges.updatePrivilege(sectionId, sessionId, Const.ParamsNames.INSTRUCTOR_PERMISSION_SUBMIT_SESSION_IN_SECTIONS, false);
        assertEquals(1, privileges.getSessionLevelPrivileges().size());
        assertTrue(privileges.getSessionLevelPrivileges().containsKey(sectionId));
        assertEquals(1, privileges.getSessionLevelPrivileges().get(sectionId).size());
        assertTrue(privileges.getSessionLevelPrivileges().get(sectionId).containsKey(sessionId));
        HashMap<String, Boolean> sessionPrivileges = privileges.getSessionLevelPrivileges().get(sectionId).get(sessionId);
        assertEquals(2, sessionPrivileges.size());
        assertEquals(Boolean.valueOf(false), sessionPrivileges.get(Const.ParamsNames.INSTRUCTOR_PERMISSION_VIEW_SESSION_IN_SECTIONS));
        assertEquals(Boolean.valueOf(false), sessionPrivileges.get(Const.ParamsNames.INSTRUCTOR_PERMISSION_SUBMIT_SESSION_IN_SECTIONS));
        
        String invalidPrivilegeName = "invalidPrivilegeName";
        privileges.updatePrivilege(sectionId, sessionId, invalidPrivilegeName, false);
        sessionPrivileges = privileges.getSessionLevelPrivileges().get(sectionId).get(sessionId);
        assertEquals(2, sessionPrivileges.size());
        assertEquals(Boolean.valueOf(false), sessionPrivileges.get(Const.ParamsNames.INSTRUCTOR_PERMISSION_VIEW_SESSION_IN_SECTIONS));
        assertEquals(Boolean.valueOf(false), sessionPrivileges.get(Const.ParamsNames.INSTRUCTOR_PERMISSION_SUBMIT_SESSION_IN_SECTIONS));
    }
    
    @Test
    public void testUpdatePrivilegesInSessionLevel() {
        InstructorPrivileges privileges = new InstructorPrivileges(Const.InstructorPermissionRoleNames.INSTRUCTOR_PERMISSION_ROLE_COOWNER);
        String sectionId = "sectionId";
        String sessionId = "sessionId";
        HashMap<String, Boolean> privilegeMap = new HashMap<String, Boolean>();
        
        privilegeMap.put(Const.ParamsNames.INSTRUCTOR_PERMISSION_VIEW_SESSION_IN_SECTIONS, false);
        privilegeMap.put(Const.ParamsNames.INSTRUCTOR_PERMISSION_SUBMIT_SESSION_IN_SECTIONS, false);
        privileges.updatePrivileges(sectionId, sessionId, privilegeMap);
        assertEquals(1, privileges.getSessionLevelPrivileges().size());
        assertTrue(privileges.getSessionLevelPrivileges().containsKey(sectionId));
        assertEquals(1, privileges.getSessionLevelPrivileges().get(sectionId).size());
        assertTrue(privileges.getSessionLevelPrivileges().get(sectionId).containsKey(sessionId));
        HashMap<String, Boolean> sessionPrivileges = privileges.getSessionLevelPrivileges().get(sectionId).get(sessionId);
        assertEquals(2, sessionPrivileges.size());
        assertEquals(Boolean.valueOf(false), sessionPrivileges.get(Const.ParamsNames.INSTRUCTOR_PERMISSION_VIEW_SESSION_IN_SECTIONS));
        assertEquals(Boolean.valueOf(false), sessionPrivileges.get(Const.ParamsNames.INSTRUCTOR_PERMISSION_SUBMIT_SESSION_IN_SECTIONS));
        
        String invalidPrivilegeName = "invalidPrivilegeName";
        privilegeMap.put(invalidPrivilegeName, false);
        privileges.updatePrivileges(sectionId, sessionId, privilegeMap);
        sessionPrivileges = privileges.getSessionLevelPrivileges().get(sectionId).get(sessionId);
        assertEquals(2, sessionPrivileges.size());
        assertEquals(Boolean.valueOf(false), sessionPrivileges.get(Const.ParamsNames.INSTRUCTOR_PERMISSION_VIEW_SESSION_IN_SECTIONS));
        assertEquals(Boolean.valueOf(false), sessionPrivileges.get(Const.ParamsNames.INSTRUCTOR_PERMISSION_SUBMIT_SESSION_IN_SECTIONS));
    }
    
    @Test
    public void testAddSectionWithDefaultPrivilegesToSectionLevel() {
        InstructorPrivileges privileges = new InstructorPrivileges(Const.InstructorPermissionRoleNames.INSTRUCTOR_PERMISSION_ROLE_COOWNER);
        String sectionId = "sectionId";
        String sectionId2 = "sectionId2";
        
        privileges.addSectionWithDefaultPrivileges(sectionId);
        assertEquals(1, privileges.getSectionLevelPrivileges().size());
        assertTrue(privileges.getSectionLevelPrivileges().containsKey(sectionId));
        privileges.addSectionWithDefaultPrivileges(sectionId);
        assertEquals(1, privileges.getSectionLevelPrivileges().size());
        assertTrue(privileges.getSectionLevelPrivileges().containsKey(sectionId));
        privileges.addSectionWithDefaultPrivileges(sectionId2);
        assertEquals(2, privileges.getSectionLevelPrivileges().size());
        assertTrue(privileges.getSectionLevelPrivileges().containsKey(sectionId));
        assertTrue(privileges.getSectionLevelPrivileges().containsKey(sectionId2));
        
        //TODO: more checking for this and the method follows
    }
    
    @Test
    public void testAddSessionWithDefaultPrivilegesToSessionLevel() {
        InstructorPrivileges privileges = new InstructorPrivileges(Const.InstructorPermissionRoleNames.INSTRUCTOR_PERMISSION_ROLE_COOWNER);
        String sectionId = "sectionId";
        String sectionId2 = "sectionId2";
        String sessionId = "sessionId";
        String sessionId2 = "sessionId2";
        
        privileges.addSessionWithDefaultPrivileges(sectionId, sessionId);
        privileges.addSessionWithDefaultPrivileges(sectionId, sessionId);
        privileges.addSessionWithDefaultPrivileges(sectionId, sessionId2);
        privileges.addSessionWithDefaultPrivileges(sectionId2, sessionId);
        assertEquals(2, privileges.getSessionLevelPrivileges().size());
        assertTrue(privileges.getSessionLevelPrivileges().containsKey(sectionId));
        assertTrue(privileges.getSessionLevelPrivileges().containsKey(sectionId2));
        assertEquals(2, privileges.getSessionLevelPrivileges().get(sectionId).size());
        assertEquals(1, privileges.getSessionLevelPrivileges().get(sectionId2).size());
    }
    
    @Test
    public void testIsAllowedForPrivilege() {
        InstructorPrivileges privileges = new InstructorPrivileges(Const.InstructorPermissionRoleNames.INSTRUCTOR_PERMISSION_ROLE_COOWNER);
        
        assertTrue(privileges.isAllowedForPrivilege(Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_COURSE));
        String invalidPrivilegeName = "invalidPrivilegeName";
        assertFalse(privileges.isAllowedForPrivilege(invalidPrivilegeName));
        
        String sectionId = "sectionId";
        assertTrue(privileges.isAllowedForPrivilege(sectionId, Const.ParamsNames.INSTRUCTOR_PERMISSION_VIEW_SESSION_IN_SECTIONS));
        privileges.updatePrivilege(sectionId, Const.ParamsNames.INSTRUCTOR_PERMISSION_VIEW_SESSION_IN_SECTIONS, false);
        assertFalse(privileges.isAllowedForPrivilege(sectionId, Const.ParamsNames.INSTRUCTOR_PERMISSION_VIEW_SESSION_IN_SECTIONS));
        assertFalse(privileges.isAllowedForPrivilege(sectionId, Const.ParamsNames.INSTRUCTOR_PERMISSION_SUBMIT_SESSION_IN_SECTIONS));
        
        String sessionId = "sessionId";
        String sessionId2 = "sessionId2";
        assertFalse(privileges.isAllowedForPrivilege(sectionId, sessionId, Const.ParamsNames.INSTRUCTOR_PERMISSION_VIEW_SESSION_IN_SECTIONS));
        privileges.addSessionWithDefaultPrivileges(sectionId, sessionId2);
        assertFalse(privileges.isAllowedForPrivilege(sectionId, sessionId, Const.ParamsNames.INSTRUCTOR_PERMISSION_VIEW_SESSION_IN_SECTIONS));
        privileges.updatePrivilege(sectionId, sessionId, Const.ParamsNames.INSTRUCTOR_PERMISSION_VIEW_SESSION_IN_SECTIONS, true);
        assertTrue(privileges.isAllowedForPrivilege(sectionId, sessionId, Const.ParamsNames.INSTRUCTOR_PERMISSION_VIEW_SESSION_IN_SECTIONS));
        assertFalse(privileges.isAllowedForPrivilege(sectionId, sessionId, Const.ParamsNames.INSTRUCTOR_PERMISSION_SUBMIT_SESSION_IN_SECTIONS));
    }
    
    @Test
    public void testValidatePrivileges() {
        InstructorPrivileges privileges = new InstructorPrivileges(Const.InstructorPermissionRoleNames.INSTRUCTOR_PERMISSION_ROLE_COOWNER);
        privileges.updatePrivilege(Const.ParamsNames.INSTRUCTOR_PERMISSION_VIEW_COMMENT_IN_SECTIONS, false);
        privileges.updatePrivilege(Const.ParamsNames.INSTRUCTOR_PERMISSION_VIEW_SESSION_IN_SECTIONS, false);
        privileges.updatePrivilege(Const.ParamsNames.INSTRUCTOR_PERMISSION_VIEW_STUDENT_IN_SECTIONS, false);
        privileges.validatePrivileges();
        
        assertTrue(privileges.isAllowedForPrivilege(Const.ParamsNames.INSTRUCTOR_PERMISSION_VIEW_COMMENT_IN_SECTIONS));
        assertTrue(privileges.isAllowedForPrivilege(Const.ParamsNames.INSTRUCTOR_PERMISSION_VIEW_SESSION_IN_SECTIONS));
        assertTrue(privileges.isAllowedForPrivilege(Const.ParamsNames.INSTRUCTOR_PERMISSION_GIVE_COMMENT_IN_SECTIONS));
        
        // restore courseLevel to pre-validate
        privileges.updatePrivilege(Const.ParamsNames.INSTRUCTOR_PERMISSION_VIEW_COMMENT_IN_SECTIONS, false);
        privileges.updatePrivilege(Const.ParamsNames.INSTRUCTOR_PERMISSION_VIEW_SESSION_IN_SECTIONS, false);
        privileges.updatePrivilege(Const.ParamsNames.INSTRUCTOR_PERMISSION_VIEW_STUDENT_IN_SECTIONS, false);
        String sectionName = "section";
        privileges.addSectionWithDefaultPrivileges(sectionName);
        privileges.validatePrivileges();
        assertTrue(privileges.isAllowedForPrivilege(Const.ParamsNames.INSTRUCTOR_PERMISSION_VIEW_COMMENT_IN_SECTIONS));
        assertTrue(privileges.isAllowedForPrivilege(Const.ParamsNames.INSTRUCTOR_PERMISSION_VIEW_SESSION_IN_SECTIONS));
        assertTrue(privileges.isAllowedForPrivilege(Const.ParamsNames.INSTRUCTOR_PERMISSION_GIVE_COMMENT_IN_SECTIONS));
        assertTrue(privileges.isAllowedForPrivilege(sectionName, Const.ParamsNames.INSTRUCTOR_PERMISSION_VIEW_COMMENT_IN_SECTIONS));
        assertTrue(privileges.isAllowedForPrivilege(sectionName, Const.ParamsNames.INSTRUCTOR_PERMISSION_VIEW_SESSION_IN_SECTIONS));
        assertTrue(privileges.isAllowedForPrivilege(sectionName, Const.ParamsNames.INSTRUCTOR_PERMISSION_GIVE_COMMENT_IN_SECTIONS));
        
        privileges.updatePrivilege(Const.ParamsNames.INSTRUCTOR_PERMISSION_VIEW_COMMENT_IN_SECTIONS, false);
        privileges.updatePrivilege(Const.ParamsNames.INSTRUCTOR_PERMISSION_VIEW_SESSION_IN_SECTIONS, false);
        privileges.updatePrivilege(Const.ParamsNames.INSTRUCTOR_PERMISSION_VIEW_STUDENT_IN_SECTIONS, false);
        privileges.updatePrivilege(sectionName, Const.ParamsNames.INSTRUCTOR_PERMISSION_VIEW_COMMENT_IN_SECTIONS, false);
        privileges.updatePrivilege(sectionName, Const.ParamsNames.INSTRUCTOR_PERMISSION_VIEW_SESSION_IN_SECTIONS, false);
        privileges.updatePrivilege(sectionName, Const.ParamsNames.INSTRUCTOR_PERMISSION_VIEW_STUDENT_IN_SECTIONS, false);
        String sessionName = "session";
        privileges.addSessionWithDefaultPrivileges(sectionName, sessionName);
        privileges.validatePrivileges();
        assertTrue(privileges.isAllowedForPrivilege(Const.ParamsNames.INSTRUCTOR_PERMISSION_VIEW_COMMENT_IN_SECTIONS));
        assertTrue(privileges.isAllowedForPrivilege(Const.ParamsNames.INSTRUCTOR_PERMISSION_VIEW_SESSION_IN_SECTIONS));
        assertTrue(privileges.isAllowedForPrivilege(Const.ParamsNames.INSTRUCTOR_PERMISSION_GIVE_COMMENT_IN_SECTIONS));
        assertTrue(privileges.isAllowedForPrivilege(sectionName, Const.ParamsNames.INSTRUCTOR_PERMISSION_VIEW_COMMENT_IN_SECTIONS));
        assertTrue(privileges.isAllowedForPrivilege(sectionName, Const.ParamsNames.INSTRUCTOR_PERMISSION_VIEW_SESSION_IN_SECTIONS));
        assertTrue(privileges.isAllowedForPrivilege(sectionName, Const.ParamsNames.INSTRUCTOR_PERMISSION_GIVE_COMMENT_IN_SECTIONS));
        assertTrue(privileges.isAllowedForPrivilege(sectionName, sessionName, Const.ParamsNames.INSTRUCTOR_PERMISSION_VIEW_SESSION_IN_SECTIONS));
    }
    
    @AfterClass
    public static void tearDown() {
        printTestClassFooter();
    }
}
