package teammates.test.cases.ui.pagedata;

import static org.testng.AssertJUnit.assertEquals;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.testng.annotations.Test;

import teammates.common.datatransfer.AccountAttributes;
import teammates.common.datatransfer.SectionDetailsBundle;
import teammates.common.datatransfer.StudentAttributes;
import teammates.common.datatransfer.TeamDetailsBundle;
import teammates.common.util.Const;
import teammates.common.util.Sanitizer;
import teammates.common.util.Url;
import teammates.ui.controller.InstructorStudentListAjaxPageData;
import teammates.ui.template.StudentListSectionData;
import teammates.ui.template.StudentListStudentData;
import teammates.ui.template.StudentListTeamData;

public class InstructorStudentListAjaxPageDataTest {

    private InstructorStudentListAjaxPageData islapd;
    
    private AccountAttributes acct;
    private SectionDetailsBundle sampleSection;
    private TeamDetailsBundle sampleTeam;
    private StudentAttributes sampleStudent;

    private Map<String, Map<String, Boolean>> sectionPrivileges;
    private Map<String, String> emailPhotoUrlMapping;
    
    private String photoUrl;
    
    @Test
    public void allTests() {
        islapd = initializeData();
        for (StudentListSectionData section : islapd.getSections()) {
            testSectionContent(section);
        }
    }

    private void testSectionContent(StudentListSectionData section) {
        assertEquals(Sanitizer.sanitizeForHtml(sampleSection.name), section.getSectionName());
        assertEquals(sectionPrivileges.get(sampleSection.name)
                                      .get(Const.ParamsNames.INSTRUCTOR_PERMISSION_VIEW_STUDENT_IN_SECTIONS)
                                      .booleanValue(),
                     section.isAllowedToViewStudentInSection());
        assertEquals(sectionPrivileges.get(sampleSection.name)
                                      .get(Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_STUDENT)
                                      .booleanValue(),
                     section.isAllowedToModifyStudent());
        assertEquals(sectionPrivileges.get(sampleSection.name)
                                      .get(Const.ParamsNames.INSTRUCTOR_PERMISSION_GIVE_COMMENT_IN_SECTIONS)
                                      .booleanValue(),
                     section.isAllowedToGiveCommentInSection());
        for (StudentListTeamData team : section.getTeams()) {
            testTeamContent(team);
        }
    }

    private void testTeamContent(StudentListTeamData team) {
        assertEquals(Sanitizer.sanitizeForHtml(sampleTeam.name), team.getTeamName());
        for (StudentListStudentData student : team.getStudents()) {
            testStudentContent(student);
        }
    }

    private void testStudentContent(StudentListStudentData student) {
        assertEquals(Sanitizer.sanitizeForHtml(sampleStudent.name), student.getStudentName());
        assertEquals(Sanitizer.sanitizeForHtml(sampleStudent.email), student.getStudentEmail());
        assertEquals("'" + Sanitizer.sanitizeForJs(sampleStudent.course) + "','"
                         + Sanitizer.sanitizeForJs(sampleStudent.name) + "'",
                     student.getToggleDeleteConfirmationParams());
        assertEquals(photoUrl, student.getPhotoUrl());
        assertEquals(getCourseStudentDetailsLink(sampleStudent.course, sampleStudent.email, acct.googleId),
                     student.getCourseStudentDetailsLink());
        assertEquals(getCourseStudentEditLink(sampleStudent.course, sampleStudent.email, acct.googleId),
                     student.getCourseStudentEditLink());
        assertEquals(getCourseStudentDeleteLink(sampleStudent.course, sampleStudent.email, acct.googleId),
                     student.getCourseStudentDeleteLink());
        assertEquals(getCourseStudentRecordsLink(sampleStudent.course, sampleStudent.email, acct.googleId),
                     student.getCourseStudentRecordsLink());
    }

    private InstructorStudentListAjaxPageData initializeData() {
        photoUrl = "validPhotoUrl";
        
        acct = new AccountAttributes();
        acct.googleId = "valid.id"; // only googleId is needed
        
        sampleStudent = new StudentAttributes();
        sampleStudent.name = "<script>alert(\"Valid name\");</script>";
        sampleStudent.email = "1+1@email.com";
        sampleStudent.course = "valid course"; // only three fields needed
        
        sampleTeam = new TeamDetailsBundle();
        sampleTeam.students.add(sampleStudent);
        sampleTeam.name = "valid team name >.<";
        
        sampleSection = new SectionDetailsBundle();
        sampleSection.teams.add(sampleTeam);
        sampleSection.name = "<valid section name>";
        
        List<SectionDetailsBundle> sections = new ArrayList<SectionDetailsBundle>();
        sections.add(sampleSection);

        sectionPrivileges = new HashMap<String, Map<String, Boolean>>();
        Map<String, Boolean> sectionPrivilege = new HashMap<String, Boolean>();
        sectionPrivilege.put(Const.ParamsNames.INSTRUCTOR_PERMISSION_VIEW_STUDENT_IN_SECTIONS, true);
        sectionPrivilege.put(Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_STUDENT, false);
        sectionPrivilege.put(Const.ParamsNames.INSTRUCTOR_PERMISSION_GIVE_COMMENT_IN_SECTIONS, true);
        sectionPrivileges.put(sampleSection.name, sectionPrivilege);

        emailPhotoUrlMapping = new HashMap<String, String>();
        emailPhotoUrlMapping.put(sampleStudent.email, photoUrl);
        
        return new InstructorStudentListAjaxPageData(acct, "valid course id", 1, true, sections,
                                                     sectionPrivileges, emailPhotoUrlMapping);
    }

    private String getCourseStudentDetailsLink(String course, String email, String googleId) {
        return furnishLinkWithCourseEmailAndUserId(Const.ActionURIs.INSTRUCTOR_COURSE_STUDENT_DETAILS_PAGE,
                                                   course, email, googleId);
    }

    private String getCourseStudentEditLink(String course, String email, String googleId) {
        return furnishLinkWithCourseEmailAndUserId(Const.ActionURIs.INSTRUCTOR_COURSE_STUDENT_DETAILS_EDIT,
                                                   course, email, googleId);
    }

    private String getCourseStudentDeleteLink(String course, String email, String googleId) {
        return furnishLinkWithCourseEmailAndUserId(Const.ActionURIs.INSTRUCTOR_COURSE_STUDENT_DELETE,
                                                   course, email, googleId);
    }

    private String getCourseStudentRecordsLink(String course, String email, String googleId) {
        return furnishLinkWithCourseEmailAndUserId(Const.ActionURIs.INSTRUCTOR_STUDENT_RECORDS_PAGE,
                                                   course, email, googleId);
    }

    private String furnishLinkWithCourseEmailAndUserId(String link, String course, String studentEmail,
                                                       String googleId) {
        link = Url.addParamToUrl(link, Const.ParamsNames.COURSE_ID, course);
        link = Url.addParamToUrl(link, Const.ParamsNames.STUDENT_EMAIL, studentEmail);
        link = Url.addParamToUrl(link, Const.ParamsNames.USER_ID, googleId);
        return link;
    }

}
