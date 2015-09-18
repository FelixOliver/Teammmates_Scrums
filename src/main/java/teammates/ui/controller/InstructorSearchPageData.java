package teammates.ui.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import teammates.common.datatransfer.AccountAttributes;
import teammates.common.datatransfer.CommentAttributes;
import teammates.common.datatransfer.CommentSearchResultBundle;
import teammates.common.datatransfer.FeedbackQuestionAttributes;
import teammates.common.datatransfer.FeedbackResponseAttributes;
import teammates.common.datatransfer.FeedbackResponseCommentAttributes;
import teammates.common.datatransfer.FeedbackResponseCommentSearchResultBundle;
import teammates.common.datatransfer.InstructorAttributes;
import teammates.common.datatransfer.SectionDetailsBundle;
import teammates.common.datatransfer.StudentAttributes;
import teammates.common.datatransfer.StudentSearchResultBundle;
import teammates.common.datatransfer.TeamDetailsBundle;
import teammates.common.util.Const;
import teammates.ui.template.Comment;
import teammates.ui.template.FeedbackResponseComment;
import teammates.ui.template.FeedbackSessionRow;
import teammates.ui.template.QuestionTable;
import teammates.ui.template.ResponseRow;
import teammates.ui.template.CommentsForStudentsTable;
import teammates.ui.template.SearchStudentsTable;
import teammates.ui.template.SearchCommentsForResponsesTable;
import teammates.ui.template.StudentListSectionData;

/**
 * PageData: the data to be used in the InstructorSearchPage
 */
public class InstructorSearchPageData extends PageData {
    private String searchKey = "";
    
    /* Whether checkbox is checked for search input */
    private boolean isSearchCommentForStudents;
    private boolean isSearchCommentForResponses;
    private boolean isSearchForStudents;
    
    /* Whether search results are empty */
    private boolean isCommentsForStudentsEmpty;
    private boolean isCommentsForResponsesEmpty;
    private boolean isStudentsEmpty;
    
    /* Tables containing search results */
    private List<CommentsForStudentsTable> searchCommentsForStudentsTables;
    private List<SearchCommentsForResponsesTable> searchCommentsForResponsesTables;
    private List<SearchStudentsTable> searchStudentsTables;
    
    public InstructorSearchPageData(AccountAttributes account) {
        super(account);
    }
    
    public void init(CommentSearchResultBundle commentSearchResultBundle, 
                     FeedbackResponseCommentSearchResultBundle frcSearchResultBundle,
                     StudentSearchResultBundle studentSearchResultBundle, 
                     String searchKey, boolean isSearchCommentForStudents, 
                     boolean isSearchCommentForResponses, boolean isSearchForStudents) {
        
        this.searchKey = searchKey;
        
        this.isSearchCommentForStudents = isSearchCommentForStudents;
        this.isSearchCommentForResponses = isSearchCommentForResponses;
        this.isSearchForStudents = isSearchForStudents;
        
        this.isCommentsForStudentsEmpty = commentSearchResultBundle.getResultSize() == 0;
        this.isCommentsForResponsesEmpty = frcSearchResultBundle.getResultSize() == 0;
        this.isStudentsEmpty = studentSearchResultBundle.getResultSize() == 0;
        
        setSearchCommentsForStudentsTables(commentSearchResultBundle);
        setSearchCommentsForResponsesTables(frcSearchResultBundle);
        setSearchStudentsTables(studentSearchResultBundle);
    }
    
    /*************** Get methods ********************/
    public String getSearchKey() {
        return sanitizeForHtml(searchKey);
    }
    
    public boolean isCommentsForStudentsEmpty() {
        return isCommentsForStudentsEmpty;
    }
    
    public boolean isCommentsForResponsesEmpty() {
        return isCommentsForResponsesEmpty;
    }
    
    public boolean isStudentsEmpty() {
        return isStudentsEmpty;
    }
    
    
    public boolean isSearchCommentForStudents() {
        return isSearchCommentForStudents;
    }
    
    public boolean isSearchCommentForResponses() {
        return isSearchCommentForResponses;
    }
    
    public boolean isSearchForStudents() {
        return isSearchForStudents;
    }
    
    
    public List<CommentsForStudentsTable> getSearchCommentsForStudentsTables() {
        return searchCommentsForStudentsTables;
    }
    
    public List<SearchCommentsForResponsesTable> getSearchCommentsForResponsesTables() {
        return searchCommentsForResponsesTables;
    }
    
    public List<SearchStudentsTable> getSearchStudentsTables() {
        return searchStudentsTables;
    }

    /*************** Set results tables *********************/
    private void setSearchCommentsForStudentsTables(
                                    CommentSearchResultBundle commentSearchResultBundle) {
        
        searchCommentsForStudentsTables = new ArrayList<CommentsForStudentsTable>();      
        
        for (String giverEmailPlusCourseId : commentSearchResultBundle.giverCommentTable.keySet()) {
            String giverDetails = commentSearchResultBundle.giverTable.get(giverEmailPlusCourseId);
            searchCommentsForStudentsTables.add(new CommentsForStudentsTable(
                                                  giverDetails, createCommentRows(giverEmailPlusCourseId, 
                                                                            commentSearchResultBundle)));
        }
    }
    
    private void setSearchCommentsForResponsesTables(
                                    FeedbackResponseCommentSearchResultBundle frcSearchResultBundle) {
        
        searchCommentsForResponsesTables = new ArrayList<SearchCommentsForResponsesTable>();
        searchCommentsForResponsesTables.add(new SearchCommentsForResponsesTable(
                                               createFeedbackSessionRows(frcSearchResultBundle)));
    }
    
    private void setSearchStudentsTables(StudentSearchResultBundle studentSearchResultBundle) {
        
        searchStudentsTables = new ArrayList<SearchStudentsTable>(); // 1 table for each course      
        List<String> courseIdList = getCourseIdsFromStudentSearchResultBundle(
                                        studentSearchResultBundle.studentList, studentSearchResultBundle);
        
        for (String courseId : courseIdList) {
            searchStudentsTables.add(new SearchStudentsTable(
                                       courseId, createStudentRows(courseId, studentSearchResultBundle)));
        }
    }  
    
    /*************** Create data structures for feedback response comments results ********************/
    private List<FeedbackSessionRow> createFeedbackSessionRows(
                                    FeedbackResponseCommentSearchResultBundle frcSearchResultBundle) {
        
        List<FeedbackSessionRow> rows = new ArrayList<FeedbackSessionRow>();
        
        for (String fsName : frcSearchResultBundle.questions.keySet()) {
            String courseId = frcSearchResultBundle.sessions.get(fsName).courseId;
            
            rows.add(new FeedbackSessionRow(fsName, courseId, createQuestionTables(
                                                                fsName, frcSearchResultBundle)));
        }
        return rows;
    }
    
    private List<QuestionTable> createQuestionTables(
                                    String fsName, 
                                    FeedbackResponseCommentSearchResultBundle frcSearchResultBundle) {
        
        List<QuestionTable> questionTables = new ArrayList<QuestionTable>();
        List<FeedbackQuestionAttributes> questionList = frcSearchResultBundle.questions.get(fsName);
        
        for (FeedbackQuestionAttributes question : questionList) {
            int questionNumber = question.questionNumber;
            String questionText = question.getQuestionDetails().questionText;
            String additionalInfo = question.getQuestionDetails()
                                            .getQuestionAdditionalInfoHtml(questionNumber, "");
            
            questionTables.add(new QuestionTable(questionNumber, questionText, additionalInfo, 
                                            createResponseRows(question, frcSearchResultBundle)));
        }
        return questionTables;
    }
    
    private List<ResponseRow> createResponseRows(
                                    FeedbackQuestionAttributes question, 
                                    FeedbackResponseCommentSearchResultBundle frcSearchResultBundle) {
        
        List<ResponseRow> rows = new ArrayList<ResponseRow>();
        List<FeedbackResponseAttributes> responseList = frcSearchResultBundle.responses.get(question.getId());
        
        for (FeedbackResponseAttributes responseEntry : responseList) {
            String giverName = frcSearchResultBundle.responseGiverTable.get(responseEntry.getId());            
            String recipientName = frcSearchResultBundle.responseRecipientTable.get(responseEntry.getId());           
            String response = responseEntry.getResponseDetails().getAnswerHtml(question.getQuestionDetails());
            
            rows.add(new ResponseRow(giverName, recipientName, response, 
                                       createFeedbackResponseCommentRows(responseEntry, frcSearchResultBundle)));
        }
        return rows;
    }
    
    private List<Comment> createCommentRows(
                                    String giverEmailPlusCourseId, 
                                    CommentSearchResultBundle commentSearchResultBundle) {
        
        List<Comment> rows = new ArrayList<Comment>();
        String giverDetails = commentSearchResultBundle.giverTable.get(giverEmailPlusCourseId);
        String instructorCommentsLink = getInstructorCommentsLink();
        
        for (CommentAttributes comment : commentSearchResultBundle.giverCommentTable.get(giverEmailPlusCourseId)) {            
            String recipientDetails = commentSearchResultBundle.recipientTable
                                                                   .get(comment.getCommentId().toString());
            String link = instructorCommentsLink + "&" + Const.ParamsNames.COURSE_ID 
                                            + "=" + comment.courseId + "#" + comment.getCommentId();           
            Comment commentRow = new Comment(comment, giverDetails, recipientDetails);
            commentRow.withLinkToCommentsPage(link);
            
            rows.add(commentRow);
        }       
        return rows;
    }
    
    private List<FeedbackResponseComment> createFeedbackResponseCommentRows(
                                    FeedbackResponseAttributes responseEntry,
                                    FeedbackResponseCommentSearchResultBundle frcSearchResultBundle) {
        
        List<FeedbackResponseComment> rows = new ArrayList<FeedbackResponseComment>();
        List<FeedbackResponseCommentAttributes> frcList = frcSearchResultBundle
                                                              .comments.get(responseEntry.getId());
        
        for (FeedbackResponseCommentAttributes frc : frcList) {
            String frCommentGiver = frcSearchResultBundle
                                            .commentGiverTable.get(frc.getId().toString());
            if (!frCommentGiver.equals("Anonymous")) {
                frCommentGiver = frc.giverEmail;
            }
            String link = getInstructorCommentsLink() + "&" + Const.ParamsNames.COURSE_ID + "=" 
                              + frc.courseId + "#" + frc.getId();         
            
            FeedbackResponseComment frcDiv = new FeedbackResponseComment(frc, frCommentGiver);
            frcDiv.setLinkToCommentsPage(link);
            
            rows.add(frcDiv);
        } 
        return rows;
    }
    
    /*************** Create data structures for student search results ********************/
    private List<StudentListSectionData> createStudentRows(String courseId, 
                                                           StudentSearchResultBundle studentSearchResultBundle) {
        List<StudentListSectionData> rows = new ArrayList<StudentListSectionData>();      
        List<StudentAttributes> studentsInCourse = filterStudentsByCourse(
                                                       courseId, studentSearchResultBundle);
        Map<String, List<String>> sectionNameToTeamNameMap = new HashMap<String, List<String>>();
        Map<String, List<StudentAttributes>> teamNameToStudentsMap = new HashMap<String, List<StudentAttributes>>();
        Map<String, String> emailToPhotoUrlMap = new HashMap<String, String>();
        for (StudentAttributes student : studentsInCourse) {
            String teamName = student.team;
            String sectionName = student.section;
            String viewPhotoLink = addUserIdToUrl(student.getPublicProfilePictureUrl());
            emailToPhotoUrlMap.put(student.email, viewPhotoLink);
            if (!teamNameToStudentsMap.containsKey(teamName)) {
                teamNameToStudentsMap.put(teamName, new ArrayList<StudentAttributes>());
            }
            teamNameToStudentsMap.get(teamName).add(student);
            if (!sectionNameToTeamNameMap.containsKey(sectionName)) {
                sectionNameToTeamNameMap.put(sectionName, new ArrayList<String>());
            }
            if (!sectionNameToTeamNameMap.get(sectionName).contains(teamName)) {
                sectionNameToTeamNameMap.get(sectionName).add(teamName);
            }
        }
        List<SectionDetailsBundle> sections = new ArrayList<SectionDetailsBundle>();
        for (String sectionName : sectionNameToTeamNameMap.keySet()) {
            SectionDetailsBundle sdb = new SectionDetailsBundle();
            sdb.name = sectionName;
            ArrayList<TeamDetailsBundle> teams = new ArrayList<TeamDetailsBundle>();
            for (String teamName : sectionNameToTeamNameMap.get(sectionName)) {
                TeamDetailsBundle tdb = new TeamDetailsBundle();
                tdb.name = teamName;
                tdb.students = teamNameToStudentsMap.get(teamName);
                teams.add(tdb);
            }
            sdb.teams = teams;
            sections.add(sdb);
        }
        for (SectionDetailsBundle section : sections) {
            InstructorAttributes instructor = studentSearchResultBundle.instructors.get(courseId);
            boolean isAllowedToViewStudentInSection =
                                            instructor.isAllowedForPrivilege(section.name, Const.ParamsNames.INSTRUCTOR_PERMISSION_VIEW_STUDENT_IN_SECTIONS);
            boolean isAllowedToModifyStudent =
                                            instructor.isAllowedForPrivilege(section.name, Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_STUDENT);
            boolean isAllowedToGiveCommentInSection =
                                            instructor.isAllowedForPrivilege(section.name, Const.ParamsNames.INSTRUCTOR_PERMISSION_GIVE_COMMENT_IN_SECTIONS);
            rows.add(new StudentListSectionData(section, isAllowedToViewStudentInSection,
                                                isAllowedToModifyStudent, isAllowedToGiveCommentInSection,
                                                emailToPhotoUrlMap, account.googleId));
        }
        return rows;
    }
    
    
    private List<String> getCourseIdsFromStudentSearchResultBundle(
                                    List<StudentAttributes> studentList, 
                                    StudentSearchResultBundle studentSearchResultBundle) {
        List<String> courses = new ArrayList<String>();
        
        for (StudentAttributes student : studentSearchResultBundle.studentList) {
            String course = student.course;
            if (!courses.contains(course)) {
                courses.add(course);
            }
        }
        return courses;
    }
    
    /**
     * Filters students from studentSearchResultBundle by course ID
     * @param courseId 
     * @return students whose course ID is equal to the courseId given in the parameter
     */
    private List<StudentAttributes> filterStudentsByCourse(
                                    String courseId, 
                                    StudentSearchResultBundle studentSearchResultBundle) {
        
        List<StudentAttributes> students = new ArrayList<StudentAttributes>();
        
        for (StudentAttributes student : studentSearchResultBundle.studentList) {
            if (courseId.equals(student.course)) {
                students.add(student);
            }
        }
        return students;
    }

}
