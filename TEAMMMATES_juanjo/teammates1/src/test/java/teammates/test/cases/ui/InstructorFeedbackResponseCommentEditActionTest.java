package teammates.test.cases.ui;

import static org.testng.AssertJUnit.assertEquals;
import static org.testng.AssertJUnit.assertFalse;
import static org.testng.AssertJUnit.assertNotNull;
import static org.testng.AssertJUnit.assertTrue;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import teammates.common.datatransfer.CommentSendingState;
import teammates.common.datatransfer.DataBundle;
import teammates.common.datatransfer.FeedbackQuestionAttributes;
import teammates.common.datatransfer.FeedbackResponseAttributes;
import teammates.common.datatransfer.FeedbackResponseCommentAttributes;
import teammates.common.datatransfer.InstructorAttributes;
import teammates.common.util.Const;
import teammates.logic.core.FeedbackSessionsLogic;
import teammates.storage.api.FeedbackQuestionsDb;
import teammates.storage.api.FeedbackResponseCommentsDb;
import teammates.storage.api.FeedbackResponsesDb;
import teammates.ui.controller.AjaxResult;
import teammates.ui.controller.InstructorFeedbackResponseCommentAjaxPageData;
import teammates.ui.controller.InstructorFeedbackResponseCommentEditAction;

public class InstructorFeedbackResponseCommentEditActionTest extends BaseActionTest {
    private final DataBundle dataBundle = getTypicalDataBundle();

    @BeforeClass
    public static void classSetUp() throws Exception {
        printTestClassHeader();
		removeAndRestoreTypicalDataInDatastore();
        uri = Const.ActionURIs.INSTRUCTOR_FEEDBACK_RESPONSE_COMMENT_EDIT;
    }
    
    @Test
    public void testExcecuteAndPostProcess() throws Exception {
        FeedbackQuestionsDb feedbackQuestionsDb = new FeedbackQuestionsDb();
        FeedbackResponsesDb feedbackResponsesDb = new FeedbackResponsesDb();
        FeedbackResponseCommentsDb feedbackResponseCommentsDb = new FeedbackResponseCommentsDb();

        int questionNumber = 1;
        FeedbackQuestionAttributes feedbackQuestion = feedbackQuestionsDb.getFeedbackQuestion(
                "First feedback session", "idOfTypicalCourse1", questionNumber);
        
        String giverEmail = "student1InCourse1@gmail.tmt";
        String receiverEmail = "student1InCourse1@gmail.tmt";
        FeedbackResponseAttributes feedbackResponse = 
                feedbackResponsesDb.getFeedbackResponse(feedbackQuestion.getId(), giverEmail, receiverEmail);
        
        FeedbackResponseCommentAttributes feedbackResponseComment =
                dataBundle.feedbackResponseComments.get("comment1FromT1C1ToR1Q1S1C1");
        
        feedbackResponseComment = feedbackResponseCommentsDb.getFeedbackResponseComment(feedbackResponse.getId(),
                feedbackResponseComment.giverEmail, feedbackResponseComment.createdAt);
        assertNotNull("response comment not found", feedbackResponseComment);
        
        InstructorAttributes instructor = dataBundle.instructors.get("instructor1OfCourse1");
        gaeSimulation.loginAsInstructor(instructor.googleId);
        
        ______TS("Unsuccessful csae: not enough parameters");
        
        verifyAssumptionFailure();
        
        String[] submissionParams = new String[] {
                Const.ParamsNames.COURSE_ID, feedbackResponseComment.courseId,
                Const.ParamsNames.FEEDBACK_SESSION_NAME, feedbackResponseComment.feedbackSessionName,
                Const.ParamsNames.FEEDBACK_RESPONSE_COMMENT_TEXT, "Comment to first response",
                Const.ParamsNames.USER_ID, instructor.googleId
        };
        
        verifyAssumptionFailure(submissionParams);
        
        ______TS("Typical successful case for unpublished session");
        
        submissionParams = new String[] {
                Const.ParamsNames.COURSE_ID, feedbackResponseComment.courseId,
                Const.ParamsNames.FEEDBACK_SESSION_NAME, feedbackResponseComment.feedbackSessionName,
                Const.ParamsNames.FEEDBACK_RESPONSE_ID, feedbackResponseComment.feedbackResponseId,
                Const.ParamsNames.FEEDBACK_RESPONSE_COMMENT_ID, feedbackResponseComment.getId().toString(),
                Const.ParamsNames.FEEDBACK_RESPONSE_COMMENT_TEXT, feedbackResponseComment.commentText + " (Edited)",
                Const.ParamsNames.FEEDBACK_RESULTS_SORTTYPE, "recipient",
                Const.ParamsNames.RESPONSE_COMMENTS_SHOWCOMMENTSTO, "GIVER,INSTRUCTORS",
                Const.ParamsNames.RESPONSE_COMMENTS_SHOWGIVERTO, "GIVER,INSTRUCTORS"
        };
        
        InstructorFeedbackResponseCommentEditAction action = getAction(submissionParams);
        AjaxResult result = (AjaxResult) action.executeAndPostProcess();
        InstructorFeedbackResponseCommentAjaxPageData data =
                (InstructorFeedbackResponseCommentAjaxPageData) result.data;
        
        assertFalse(data.isError);
        assertEquals("", result.getStatusMessage());
        assertEquals(CommentSendingState.SENT, data.comment.sendingState);
        
        ______TS("Null show comments and show giver permissions");
        
        submissionParams = new String[] {
                Const.ParamsNames.COURSE_ID, feedbackResponseComment.courseId,
                Const.ParamsNames.FEEDBACK_SESSION_NAME, feedbackResponseComment.feedbackSessionName,
                Const.ParamsNames.FEEDBACK_RESPONSE_ID, feedbackResponseComment.feedbackResponseId,
                Const.ParamsNames.FEEDBACK_RESPONSE_COMMENT_ID, feedbackResponseComment.getId().toString(),
                Const.ParamsNames.FEEDBACK_RESPONSE_COMMENT_TEXT, feedbackResponseComment.commentText + " (Edited)",
                Const.ParamsNames.FEEDBACK_RESULTS_SORTTYPE, "recipient"
        };
        
        action = getAction(submissionParams);
        result = (AjaxResult) action.executeAndPostProcess();
        data = (InstructorFeedbackResponseCommentAjaxPageData) result.data;
        
        assertFalse(data.isError);
        assertEquals("", result.getStatusMessage());
        assertEquals(CommentSendingState.SENT, data.comment.sendingState);
        
        ______TS("Empty show comments and show giver permissions");
        
        submissionParams = new String[] {
                Const.ParamsNames.COURSE_ID, feedbackResponseComment.courseId,
                Const.ParamsNames.FEEDBACK_SESSION_NAME, feedbackResponseComment.feedbackSessionName,
                Const.ParamsNames.FEEDBACK_RESPONSE_ID, feedbackResponseComment.feedbackResponseId,
                Const.ParamsNames.FEEDBACK_RESPONSE_COMMENT_ID, feedbackResponseComment.getId().toString(),
                Const.ParamsNames.FEEDBACK_RESPONSE_COMMENT_TEXT, feedbackResponseComment.commentText + " (Edited)",
                Const.ParamsNames.FEEDBACK_RESULTS_SORTTYPE, "recipient",
                Const.ParamsNames.RESPONSE_COMMENTS_SHOWCOMMENTSTO, "",
                Const.ParamsNames.RESPONSE_COMMENTS_SHOWGIVERTO, ""
        };
        
        action = getAction(submissionParams);
        result = (AjaxResult) action.executeAndPostProcess();
        data = (InstructorFeedbackResponseCommentAjaxPageData) result.data;
        
        assertFalse(data.isError);
        assertEquals("", result.getStatusMessage());
        assertEquals(CommentSendingState.SENT, data.comment.sendingState);
        
        ______TS("Typical successful case for unpublished session public to various recipients");
        
        submissionParams = new String[] {
                Const.ParamsNames.COURSE_ID, feedbackResponseComment.courseId,
                Const.ParamsNames.FEEDBACK_SESSION_NAME, feedbackResponseComment.feedbackSessionName,
                Const.ParamsNames.FEEDBACK_RESPONSE_ID, feedbackResponseComment.feedbackResponseId,
                Const.ParamsNames.FEEDBACK_RESPONSE_COMMENT_ID, feedbackResponseComment.getId().toString(),
                Const.ParamsNames.FEEDBACK_RESPONSE_COMMENT_TEXT, feedbackResponseComment.commentText + " (Edited)",
                Const.ParamsNames.FEEDBACK_RESULTS_SORTTYPE, "recipient",
                Const.ParamsNames.RESPONSE_COMMENTS_SHOWCOMMENTSTO, "",
        };
        
        action = getAction(submissionParams);
        result = (AjaxResult) action.executeAndPostProcess();
        data = (InstructorFeedbackResponseCommentAjaxPageData) result.data;
        
        assertFalse(data.isError);
        assertEquals("", result.getStatusMessage());
        assertEquals(CommentSendingState.SENT, data.comment.sendingState);
        
        submissionParams = new String[] {
                Const.ParamsNames.COURSE_ID, feedbackResponseComment.courseId,
                Const.ParamsNames.FEEDBACK_SESSION_NAME, feedbackResponseComment.feedbackSessionName,
                Const.ParamsNames.FEEDBACK_RESPONSE_ID, feedbackResponseComment.feedbackResponseId,
                Const.ParamsNames.FEEDBACK_RESPONSE_COMMENT_ID, feedbackResponseComment.getId().toString(),
                Const.ParamsNames.FEEDBACK_RESPONSE_COMMENT_TEXT, feedbackResponseComment.commentText + " (Edited)",
                Const.ParamsNames.FEEDBACK_RESULTS_SORTTYPE, "recipient",
                Const.ParamsNames.RESPONSE_COMMENTS_SHOWCOMMENTSTO, "GIVER",
        };
        
        action = getAction(submissionParams);
        result = (AjaxResult) action.executeAndPostProcess();
        data = (InstructorFeedbackResponseCommentAjaxPageData) result.data;
        
        assertFalse(data.isError);
        assertEquals("", result.getStatusMessage());
        assertEquals(CommentSendingState.SENT, data.comment.sendingState);
        
        submissionParams = new String[] {
                Const.ParamsNames.COURSE_ID, feedbackResponseComment.courseId,
                Const.ParamsNames.FEEDBACK_SESSION_NAME, feedbackResponseComment.feedbackSessionName,
                Const.ParamsNames.FEEDBACK_RESPONSE_ID, feedbackResponseComment.feedbackResponseId,
                Const.ParamsNames.FEEDBACK_RESPONSE_COMMENT_ID, feedbackResponseComment.getId().toString(),
                Const.ParamsNames.FEEDBACK_RESPONSE_COMMENT_TEXT, feedbackResponseComment.commentText + " (Edited)",
                Const.ParamsNames.FEEDBACK_RESULTS_SORTTYPE, "recipient",
                Const.ParamsNames.RESPONSE_COMMENTS_SHOWCOMMENTSTO, "RECEIVER",
        };
        
        action = getAction(submissionParams);
        result = (AjaxResult) action.executeAndPostProcess();
        data = (InstructorFeedbackResponseCommentAjaxPageData) result.data;
        
        assertFalse(data.isError);
        assertEquals("", result.getStatusMessage());
        assertEquals(CommentSendingState.SENT, data.comment.sendingState);
        
        submissionParams = new String[] {
                Const.ParamsNames.COURSE_ID, feedbackResponseComment.courseId,
                Const.ParamsNames.FEEDBACK_SESSION_NAME, feedbackResponseComment.feedbackSessionName,
                Const.ParamsNames.FEEDBACK_RESPONSE_ID, feedbackResponseComment.feedbackResponseId,
                Const.ParamsNames.FEEDBACK_RESPONSE_COMMENT_ID, feedbackResponseComment.getId().toString(),
                Const.ParamsNames.FEEDBACK_RESPONSE_COMMENT_TEXT, feedbackResponseComment.commentText + " (Edited)",
                Const.ParamsNames.FEEDBACK_RESULTS_SORTTYPE, "recipient",
                Const.ParamsNames.RESPONSE_COMMENTS_SHOWCOMMENTSTO, "OWN_TEAM_MEMBERS",
        };
        
        action = getAction(submissionParams);
        result = (AjaxResult) action.executeAndPostProcess();
        data = (InstructorFeedbackResponseCommentAjaxPageData) result.data;
        
        assertFalse(data.isError);
        assertEquals("", result.getStatusMessage());
        assertEquals(CommentSendingState.SENT, data.comment.sendingState);
        
        submissionParams = new String[] {
                Const.ParamsNames.COURSE_ID, feedbackResponseComment.courseId,
                Const.ParamsNames.FEEDBACK_SESSION_NAME, feedbackResponseComment.feedbackSessionName,
                Const.ParamsNames.FEEDBACK_RESPONSE_ID, feedbackResponseComment.feedbackResponseId,
                Const.ParamsNames.FEEDBACK_RESPONSE_COMMENT_ID, feedbackResponseComment.getId().toString(),
                Const.ParamsNames.FEEDBACK_RESPONSE_COMMENT_TEXT, feedbackResponseComment.commentText + " (Edited)",
                Const.ParamsNames.FEEDBACK_RESULTS_SORTTYPE, "recipient",
                Const.ParamsNames.RESPONSE_COMMENTS_SHOWCOMMENTSTO, "RECEIVER_TEAM_MEMBERS",
        };
        
        action = getAction(submissionParams);
        result = (AjaxResult) action.executeAndPostProcess();
        data = (InstructorFeedbackResponseCommentAjaxPageData) result.data;
        
        assertFalse(data.isError);
        assertEquals("", result.getStatusMessage());
        assertEquals(CommentSendingState.SENT, data.comment.sendingState);
        
        submissionParams = new String[] {
                Const.ParamsNames.COURSE_ID, feedbackResponseComment.courseId,
                Const.ParamsNames.FEEDBACK_SESSION_NAME, feedbackResponseComment.feedbackSessionName,
                Const.ParamsNames.FEEDBACK_RESPONSE_ID, feedbackResponseComment.feedbackResponseId,
                Const.ParamsNames.FEEDBACK_RESPONSE_COMMENT_ID, feedbackResponseComment.getId().toString(),
                Const.ParamsNames.FEEDBACK_RESPONSE_COMMENT_TEXT, feedbackResponseComment.commentText + " (Edited)",
                Const.ParamsNames.FEEDBACK_RESULTS_SORTTYPE, "recipient",
                Const.ParamsNames.RESPONSE_COMMENTS_SHOWCOMMENTSTO, "STUDENTS",
        };
        
        action = getAction(submissionParams);
        result = (AjaxResult) action.executeAndPostProcess();
        data = (InstructorFeedbackResponseCommentAjaxPageData) result.data;
        
        assertFalse(data.isError);
        assertEquals("", result.getStatusMessage());
        assertEquals(CommentSendingState.SENT, data.comment.sendingState);
        
        ______TS("Non-existent feedback response comment id");
        
        submissionParams = new String[] {
                Const.ParamsNames.COURSE_ID, feedbackResponseComment.courseId,
                Const.ParamsNames.FEEDBACK_SESSION_NAME, feedbackResponseComment.feedbackSessionName,
                Const.ParamsNames.FEEDBACK_RESPONSE_ID, feedbackResponseComment.feedbackResponseId,
                Const.ParamsNames.FEEDBACK_RESPONSE_COMMENT_ID, "123123123123123",
                Const.ParamsNames.FEEDBACK_RESPONSE_COMMENT_TEXT, feedbackResponseComment.commentText + " (Edited)",
                Const.ParamsNames.FEEDBACK_RESULTS_SORTTYPE, "recipient",
                Const.ParamsNames.RESPONSE_COMMENTS_SHOWCOMMENTSTO, "GIVER,INSTRUCTORS",
                Const.ParamsNames.RESPONSE_COMMENTS_SHOWGIVERTO, "GIVER,INSTRUCTORS"
        };
        
        try {
            action = getAction(submissionParams);
            result = (AjaxResult) action.executeAndPostProcess();
        } catch (AssertionError e) {
            assertEquals("FeedbackResponseComment should not be null", e.getMessage());
        }
        
        
        ______TS("Instructor is not feedback response comment giver");
        
        gaeSimulation.loginAsInstructor("idOfInstructor2OfCourse1");
        
        submissionParams = new String[] {
                Const.ParamsNames.COURSE_ID, feedbackResponseComment.courseId,
                Const.ParamsNames.FEEDBACK_SESSION_NAME, feedbackResponseComment.feedbackSessionName,
                Const.ParamsNames.FEEDBACK_RESPONSE_ID, feedbackResponseComment.feedbackResponseId,
                Const.ParamsNames.FEEDBACK_RESPONSE_COMMENT_ID, feedbackResponseComment.getId().toString(),
                Const.ParamsNames.FEEDBACK_RESPONSE_COMMENT_TEXT, feedbackResponseComment.commentText + " (Edited)",
                Const.ParamsNames.FEEDBACK_RESULTS_SORTTYPE, "recipient",
                Const.ParamsNames.RESPONSE_COMMENTS_SHOWCOMMENTSTO, "GIVER,INSTRUCTORS",
                Const.ParamsNames.RESPONSE_COMMENTS_SHOWGIVERTO, "GIVER,INSTRUCTORS"
        };
        action = getAction(submissionParams);
        result = (AjaxResult) action.executeAndPostProcess();
        data = (InstructorFeedbackResponseCommentAjaxPageData) result.data;
        
        assertFalse(data.isError);
        assertEquals("", result.getStatusMessage());
        assertEquals(CommentSendingState.SENT, data.comment.sendingState);
        
        ______TS("Typical successful case for published session");
        
        gaeSimulation.loginAsInstructor(instructor.googleId);
        
        FeedbackSessionsLogic.inst().publishFeedbackSession(feedbackResponseComment.feedbackSessionName,
                                                            feedbackResponseComment.courseId);
        submissionParams = new String[] {
                Const.ParamsNames.COURSE_ID, feedbackResponseComment.courseId,
                Const.ParamsNames.FEEDBACK_SESSION_NAME, feedbackResponseComment.feedbackSessionName,
                Const.ParamsNames.FEEDBACK_RESPONSE_ID, feedbackResponseComment.feedbackResponseId,
                Const.ParamsNames.FEEDBACK_RESPONSE_COMMENT_ID, feedbackResponseComment.getId().toString(),
                Const.ParamsNames.FEEDBACK_RESPONSE_COMMENT_TEXT, feedbackResponseComment.commentText 
                                                                + " (Edited for published session)",
                Const.ParamsNames.FEEDBACK_RESULTS_SORTTYPE, "recipient",
                Const.ParamsNames.RESPONSE_COMMENTS_SHOWCOMMENTSTO, "GIVER,INSTRUCTORS"
        };
        
        action = getAction(submissionParams);
        result = (AjaxResult) action.executeAndPostProcess();
        data = (InstructorFeedbackResponseCommentAjaxPageData) result.data;
        
        assertFalse(data.isError);
        assertEquals("", result.getStatusMessage());
        assertEquals(CommentSendingState.PENDING, data.comment.sendingState);
        
        ______TS("Unsuccessful case: empty comment text");
        
        submissionParams = new String[] {
                Const.ParamsNames.COURSE_ID, feedbackResponseComment.courseId,
                Const.ParamsNames.FEEDBACK_SESSION_NAME, feedbackResponseComment.feedbackSessionName,
                Const.ParamsNames.FEEDBACK_RESPONSE_ID, feedbackResponseComment.feedbackResponseId,
                Const.ParamsNames.FEEDBACK_RESPONSE_COMMENT_ID, feedbackResponseComment.getId().toString(),
                Const.ParamsNames.FEEDBACK_RESPONSE_COMMENT_TEXT, "",
                Const.ParamsNames.FEEDBACK_RESULTS_SORTTYPE, "recipient"
        };

        action = getAction(submissionParams);
        result = (AjaxResult) action.executeAndPostProcess();
        assertEquals("", result.getStatusMessage());
        data = (InstructorFeedbackResponseCommentAjaxPageData) result.data;

        assertTrue(data.isError);
        assertEquals(Const.StatusMessages.FEEDBACK_RESPONSE_COMMENT_EMPTY, data.errorMessage);
    }
    
    private InstructorFeedbackResponseCommentEditAction getAction(String... params) throws Exception {
        return (InstructorFeedbackResponseCommentEditAction) (gaeSimulation.getActionObject(uri, params));
    }
}
