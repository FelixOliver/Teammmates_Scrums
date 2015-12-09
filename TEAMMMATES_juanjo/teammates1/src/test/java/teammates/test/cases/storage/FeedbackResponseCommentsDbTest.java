package teammates.test.cases.storage;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;
import static org.testng.Assert.assertNull;
import static org.testng.Assert.assertNotNull;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.google.appengine.api.datastore.Text;

import teammates.common.datatransfer.DataBundle;
import teammates.common.datatransfer.FeedbackResponseCommentAttributes;
import teammates.common.exception.EntityAlreadyExistsException;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.exception.InvalidParametersException;
import teammates.common.util.Const;
import teammates.common.util.StringHelper;
import teammates.storage.api.EntitiesDb;
import teammates.storage.api.FeedbackResponseCommentsDb;
import teammates.storage.entity.FeedbackResponseComment;
import teammates.test.cases.BaseComponentTestCase;
import teammates.test.util.TestHelper;

public class FeedbackResponseCommentsDbTest extends BaseComponentTestCase {

    private static final FeedbackResponseCommentsDb frcDb = new FeedbackResponseCommentsDb();
    private static DataBundle dataBundle = getTypicalDataBundle();

    private static FeedbackResponseCommentAttributes frcaData = dataBundle.feedbackResponseComments
                                                                          .get("comment1FromT1C1ToR1Q1S1C1");
    private static String frId = dataBundle.feedbackResponseComments
                                           .get("comment1FromT1C1ToR1Q1S1C1")
                                           .feedbackResponseId;
    private static FeedbackResponseCommentAttributes anotherFrcaData = dataBundle.feedbackResponseComments
                                                                       .get("comment1FromT1C1ToR1Q2S1C1");
    private static ArrayList<FeedbackResponseCommentAttributes> frcasData = 
            new ArrayList<FeedbackResponseCommentAttributes>();

    @BeforeClass
    public static void classSetUp() throws Exception {
        printTestClassHeader();
        turnLoggingUp(FeedbackResponseCommentsDb.class);
        frcDb.createEntity(frcaData);
        frcDb.createEntity(anotherFrcaData);
        frcaData = frcDb.getFeedbackResponseComment(frcaData.feedbackResponseId, 
                                 frcaData.giverEmail, frcaData.createdAt);
        anotherFrcaData = frcDb.getFeedbackResponseComment(anotherFrcaData.feedbackResponseId, 
                                        anotherFrcaData.giverEmail, anotherFrcaData.createdAt);
        frcasData.add(frcaData);
        frcasData.add(anotherFrcaData);
    }

    @Test
    public void testAll() throws Exception {
        
        testEntityCreationAndDeletion();
        
        testGetFeedbackResponseCommentFromId();
        
        testGetFeedbackResponseCommentFromCommentDetails();
        
        testGetFeedbackResponseCommentForGiver();
        
        testGetFeedbackResponseCommentForResponse();
        
        testUpdateFeedbackResponseComment();
        
        testGetFeedbackResponseCommentsForSession();
        
        testUpdateFeedbackResponseCommentsGiverEmail();
        
        testDeleteFeedbackResponseCommentsForResponse();
        
        testGetFeedbackResponseCommentsForCourse();
        
        testGetAndDeleteFeedbackResponseCommentsForCourses();

    }

    public void testEntityCreationAndDeletion() throws Exception {
        FeedbackResponseCommentAttributes frcaTemp = 
                dataBundle.feedbackResponseComments.get("comment1FromT1C1ToR1Q2S1C1");
        frcaTemp.createdAt = new Date();
        frcaTemp.commentText = new Text("test creation and deletion");
        
        ______TS("Entity creation");
        
        frcDb.createEntity(frcaTemp);
        TestHelper.verifyPresentInDatastore(frcaTemp);
        
        ______TS("Entity deletion");

        frcDb.deleteEntity(frcaTemp);
        TestHelper.verifyAbsentInDatastore(frcaTemp);
    }
    
    public void testGetFeedbackResponseCommentFromId() throws Exception {
        
        ______TS("null parameter");

        try {
            frcDb.getFeedbackResponseComment(null);
            signalFailureToDetectException();
        } catch (AssertionError ae) {
            assertEquals(Const.StatusCodes.DBLEVEL_NULL_INPUT, ae.getMessage());
        }

        ______TS("typical success case");

        FeedbackResponseCommentAttributes frcaExpected = 
                frcDb.getFeedbackResponseComment(frcaData.courseId, frcaData.createdAt, frcaData.giverEmail);

        FeedbackResponseCommentAttributes frcaActual = 
                frcDb.getFeedbackResponseComment(frcaExpected.getId());

        assertEquals(frcaExpected.toString(), frcaActual.toString());

        ______TS("non-existent comment");

        assertNull(frcDb.getFeedbackResponseComment(-1L));
    }

    public void testGetFeedbackResponseCommentFromCommentDetails() throws Exception {

        ______TS("null parameter");

        try {
            frcDb.getFeedbackResponseComment(null, "", new Date());
            signalFailureToDetectException();
        } catch (AssertionError ae) {
            assertEquals(Const.StatusCodes.DBLEVEL_NULL_INPUT, ae.getMessage());
        }

        try {
            frcDb.getFeedbackResponseComment("", null, new Date());
            signalFailureToDetectException();
        } catch (AssertionError ae) {
            assertEquals(Const.StatusCodes.DBLEVEL_NULL_INPUT, ae.getMessage());
        }

        try {
            frcDb.getFeedbackResponseComment("", "", null);
            signalFailureToDetectException();
        } catch (AssertionError ae) {
            assertEquals(Const.StatusCodes.DBLEVEL_NULL_INPUT, ae.getMessage());
        }

        ______TS("typical success case");

        FeedbackResponseCommentAttributes frcaExpected = frcaData;
        FeedbackResponseCommentAttributes frca = 
                frcDb.getFeedbackResponseComment(frId, frcaExpected.giverEmail, frcaExpected.createdAt);

        // fill back the Ids
        frcaExpected.feedbackResponseId = frId;
        frcaExpected.setId(frca.getId());
        frcaExpected.feedbackQuestionId = frca.feedbackQuestionId;

        assertEquals(frcaExpected.toString(), frca.toString());

        ______TS("non-existent comment");

        assertNull(frcDb.getFeedbackResponseComment("123", frca.giverEmail, frca.createdAt));
        
        ______TS("non-existent giver");
        
        assertNull(frcDb.getFeedbackResponseComment(frca.getId().toString(), "nonExistentGiverEmail", frca.createdAt));
        assertNull(frcDb.getFeedbackResponseComment(frcaData.courseId, frcaData.createdAt, "nonExistentGiverEmail"));
    }
    
    public void testGetFeedbackResponseCommentForGiver() 
            throws InvalidParametersException, EntityAlreadyExistsException {
        List<FeedbackResponseCommentAttributes> frcasExpected = frcasData;
        
        ______TS("null parameter");

        try {
            frcDb.getFeedbackResponseCommentForGiver(null, frcaData.giverEmail);
            signalFailureToDetectException();
        } catch (AssertionError ae) {
            assertEquals(Const.StatusCodes.DBLEVEL_NULL_INPUT, ae.getMessage());
        }
        
        try {
            frcDb.getFeedbackResponseCommentForGiver(frcaData.courseId, null);
            signalFailureToDetectException();
        } catch (AssertionError ae) {
            assertEquals(Const.StatusCodes.DBLEVEL_NULL_INPUT, ae.getMessage());
        }
        
        ______TS("typical success case");
        
        List<FeedbackResponseCommentAttributes> frcas = 
                frcDb.getFeedbackResponseCommentForGiver(frcaData.courseId, frcaData.giverEmail);
        verifyListsContainSameResponseCommentAttributes(
                new ArrayList<FeedbackResponseCommentAttributes>(frcasExpected), frcas);
        
        ______TS("non-existent course id");
        
        frcas = frcDb.getFeedbackResponseCommentForGiver("idOfNonExistentCourse", frcaData.giverEmail);
        assertTrue(frcas.isEmpty());
        
        ______TS("non-existent giver");
        
        frcas = frcDb.getFeedbackResponseCommentForGiver(frcaData.courseId, "nonExistentGiverEmail");
        assertTrue(frcas.isEmpty());
    }
    
    public void testGetFeedbackResponseCommentForResponse() 
            throws InvalidParametersException, EntityAlreadyExistsException {
        String responseId = "1%student1InCourse1@gmail.tmt%student1InCourse1@gmail.tmt";
        ArrayList<FeedbackResponseCommentAttributes> frcasExpected =
                new ArrayList<FeedbackResponseCommentAttributes>();
        frcasExpected.add(frcaData);

        ______TS("typical success case");
        
        ArrayList<FeedbackResponseCommentAttributes> frcas =
            (ArrayList<FeedbackResponseCommentAttributes>) frcDb
                    .getFeedbackResponseCommentsForResponse(responseId);
        verifyListsContainSameResponseCommentAttributes(
                new ArrayList<FeedbackResponseCommentAttributes>(frcasExpected), frcas);
    }

    public void testUpdateFeedbackResponseComment() throws Exception {
        
        ______TS("null parameter");
        
        try {
            frcDb.updateFeedbackResponseComment(null);
            signalFailureToDetectException();
        } catch (AssertionError ae) {
            assertEquals(Const.StatusCodes.DBLEVEL_NULL_INPUT, ae.getMessage());
        }
        
        ______TS("typical success case");
        
        FeedbackResponseCommentAttributes frcaTemp = 
                dataBundle.feedbackResponseComments.get("comment1FromT1C1ToR1Q2S1C1");
        frcaTemp.createdAt = new Date();
        frcaTemp.commentText = new Text("Update feedback response comment");
        frcDb.createEntity(frcaTemp);
        frcaTemp = frcDb.getFeedbackResponseComment(frcaTemp.feedbackResponseId, 
                                 frcaTemp.giverEmail, frcaTemp.createdAt);
        
        FeedbackResponseCommentAttributes frcaExpected =
                frcDb.getFeedbackResponseComment(frcaTemp.courseId, frcaTemp.createdAt, frcaTemp.giverEmail);
        frcaExpected.commentText = new Text("This is new Text");
        frcDb.updateFeedbackResponseComment(frcaExpected);
        
        FeedbackResponseCommentAttributes frcaActual =
                frcDb.getFeedbackResponseComment(
                              frcaExpected.courseId, frcaExpected.createdAt,frcaExpected.giverEmail);
        
        frcaExpected.setId(frcaActual.getId());
        frcaExpected.feedbackQuestionId = frcaActual.feedbackQuestionId;
        assertEquals(frcaExpected.courseId, frcaActual.courseId);
        assertEquals(frcaExpected.commentText, frcaActual.commentText);
        
        frcDb.deleteEntity(frcaTemp);
        
        ______TS("non-existent comment");
        
        frcaExpected.setId(-1L);
        
        try {
            frcDb.updateFeedbackResponseComment(frcaExpected);
            signalFailureToDetectException();
        } catch (EntityDoesNotExistException edne) {
            assertEquals(EntitiesDb.ERROR_UPDATE_NON_EXISTENT + frcaExpected.toString(), edne.getMessage());
        }
        
        // set responseId back
        frcaExpected.feedbackResponseId = frId;
        
        ______TS("invalid parameters");
        
        frcaExpected.courseId = "";
        frcaExpected.feedbackSessionName = "%asdt";
        frcaExpected.giverEmail = "test-no-at-funny.com";
        
        try {
            frcDb.updateFeedbackResponseComment(frcaExpected);
            signalFailureToDetectException();
        } catch (InvalidParametersException ipe) {
            assertEquals(StringHelper.toString(frcaExpected.getInvalidityInfo()), ipe.getMessage());
        }
    }

    public void testGetFeedbackResponseCommentsForSession() throws Exception {
        
        ______TS("null parameter");

        try {
            frcDb.getFeedbackResponseCommentsForSession(null, "");
            signalFailureToDetectException();
        } catch (AssertionError ae) {
            assertEquals(Const.StatusCodes.DBLEVEL_NULL_INPUT, ae.getMessage());
        }

        try {
            frcDb.getFeedbackResponseCommentsForSession("", null);
            signalFailureToDetectException();
        } catch (AssertionError ae) {
            assertEquals(Const.StatusCodes.DBLEVEL_NULL_INPUT, ae.getMessage());
        }

        ______TS("typical success case");
        
        List<FeedbackResponseCommentAttributes> actualFrcas =
                frcDb.getFeedbackResponseCommentsForSession(frcaData.courseId, frcaData.feedbackSessionName);
        List<FeedbackResponseCommentAttributes> expectedFrcas = 
                new ArrayList<FeedbackResponseCommentAttributes>();
        expectedFrcas.add(frcaData);
        expectedFrcas.add(anotherFrcaData);
        verifyListsContainSameResponseCommentAttributes(expectedFrcas, actualFrcas);
    }
    
    public void testUpdateFeedbackResponseCommentsGiverEmail() 
            throws InvalidParametersException, EntityAlreadyExistsException {
        FeedbackResponseCommentAttributes frcaDataOfNewGiver =
                dataBundle.feedbackResponseComments.get("comment1FromT1C1ToR1Q3S1C1");
        String giverEmail = "frcdb.newGiver@email.com";
        String courseId = "frcdb.giver.courseId";
        Date createdAt = new Date();
        frcaDataOfNewGiver.createdAt = createdAt;
        frcaDataOfNewGiver.commentText = new Text("another comment for this response");
        frcaDataOfNewGiver.setId(null);
        frcaDataOfNewGiver.giverEmail = giverEmail;
        frcaDataOfNewGiver.courseId = courseId;
        frcDb.createEntity(frcaDataOfNewGiver);
        assertNotNull(frcDb.getFeedbackResponseComment(courseId, createdAt, giverEmail));
        
        ______TS("typical success case");
        
        String updatedEmail = "frcdb.updatedGiver@email.com";;
        frcDb.updateGiverEmailOfFeedbackResponseComments(courseId, giverEmail, updatedEmail);
        assertNull(frcDb.getFeedbackResponseComment(courseId, createdAt, giverEmail));
        assertNotNull(frcDb.getFeedbackResponseComment(courseId, createdAt, updatedEmail));
        
        ______TS("Same email");
        
        FeedbackResponseCommentAttributes expectedFrca =
                frcDb.getFeedbackResponseComment(courseId, createdAt, updatedEmail);
        frcDb.updateGiverEmailOfFeedbackResponseComments(courseId, updatedEmail, updatedEmail);
        FeedbackResponseCommentAttributes actualFrca =
                frcDb.getFeedbackResponseComment(courseId, createdAt, updatedEmail);          
        assertEquals(actualFrca.courseId, expectedFrca.courseId);
        assertEquals(actualFrca.createdAt, expectedFrca.createdAt);
        assertEquals(actualFrca.giverEmail, expectedFrca.giverEmail);
        
        ______TS("null parameter");

        try {
            frcDb.updateGiverEmailOfFeedbackResponseComments(null, giverEmail, updatedEmail);
            signalFailureToDetectException();
        } catch (AssertionError ae) {
            assertEquals(Const.StatusCodes.DBLEVEL_NULL_INPUT, ae.getMessage());
        }
        
        try {
            frcDb.updateGiverEmailOfFeedbackResponseComments(courseId, null, updatedEmail);
            signalFailureToDetectException();
        } catch (AssertionError ae) {
            assertEquals(Const.StatusCodes.DBLEVEL_NULL_INPUT, ae.getMessage());
        }
        
        try {
            frcDb.updateGiverEmailOfFeedbackResponseComments(courseId, giverEmail, null);
            signalFailureToDetectException();
        } catch (AssertionError ae) {
            assertEquals(Const.StatusCodes.DBLEVEL_NULL_INPUT, ae.getMessage());
        }
    }

    public void testDeleteFeedbackResponseCommentsForResponse()
            throws InvalidParametersException, EntityAlreadyExistsException {
        
        ______TS("typical success case");
        
        // get another frc from data bundle and use it to create another feedback response
        FeedbackResponseCommentAttributes tempFrcaData =
                dataBundle.feedbackResponseComments.get("comment1FromT1C1ToR1Q2S1C1");
        tempFrcaData.createdAt = new Date();
        tempFrcaData.commentText = new Text("another comment for this response");
        // for some reason, the id is 0 instead of null. so we explicitly set it to be null
        tempFrcaData.setId(null);
        // set this comment to have the same responseId as frcaData
        String responseId = "1%student1InCourse1@gmail.com%student1InCourse1@gmail.com";
        tempFrcaData.feedbackResponseId = responseId;
        frcDb.createEntity(tempFrcaData);
        
        frcDb.deleteFeedbackResponseCommentsForResponse(responseId);
        assertEquals(frcDb.getFeedbackResponseCommentsForResponse(responseId).size(), 0);
        
        ______TS("null parameter");

        try {
            frcDb.deleteFeedbackResponseCommentsForResponse(null);
            signalFailureToDetectException();
        } catch (AssertionError ae) {
            assertEquals(Const.StatusCodes.DBLEVEL_NULL_INPUT, ae.getMessage());
        }
    }
    
    public void testGetFeedbackResponseCommentsForCourse() {
        String courseId = "idOfTypicalCourse1";
        List<FeedbackResponseCommentAttributes> expectedFrcs =
                new ArrayList<FeedbackResponseCommentAttributes>();
        expectedFrcs.add(frcaData);
        expectedFrcs.add(anotherFrcaData);
        
        ______TS("successful get feedback response comment for course");
        
        List<FeedbackResponseCommentAttributes> actualFrcs =
                frcDb.getFeedbackResponseCommentsForCourse(courseId);
        verifyListsContainSameResponseCommentAttributes(expectedFrcs, actualFrcs);
    }

    public void testGetAndDeleteFeedbackResponseCommentsForCourses()
            throws InvalidParametersException, EntityAlreadyExistsException {
        List<String> courseIds = new ArrayList<String>();
        courseIds.add("idOfTypicalCourse1");
        List<FeedbackResponseComment> expectedFrcs =
                new ArrayList<FeedbackResponseComment>();
        expectedFrcs.add(frcaData.toEntity());
        expectedFrcs.add(anotherFrcaData.toEntity());
        
        ______TS("successful get feedback response comment for courses");
        
        List<FeedbackResponseComment> actualFrcs =
                frcDb.getFeedbackResponseCommentEntitiesForCourses(courseIds);
        TestHelper.isSameContentIgnoreOrder(expectedFrcs, actualFrcs);
        
        ______TS("successful delete feedback response comment for courses");
        
        frcDb.deleteFeedbackResponseCommentsForCourses(courseIds);
        actualFrcs = frcDb.getFeedbackResponseCommentEntitiesForCourses(courseIds);
        assertTrue(actualFrcs.isEmpty());
    }

    
    private void verifyListsContainSameResponseCommentAttributes(
            List<FeedbackResponseCommentAttributes> expectedFrcas,
            List<FeedbackResponseCommentAttributes> actualFrcas) {
        
        for (FeedbackResponseCommentAttributes frca : expectedFrcas) {
            frca.feedbackQuestionId = "";
            frca.feedbackResponseId = "";
            frca.setId(0L);
        }
        
        for (FeedbackResponseCommentAttributes frca : actualFrcas) {
            frca.feedbackQuestionId = "";
            frca.feedbackResponseId = "";
            frca.setId(0L);
        }
        
        assertTrue(TestHelper.isSameContentIgnoreOrder(expectedFrcas, actualFrcas));
        
    }

    @AfterMethod
    public void caseTearDown() throws Exception {
        turnLoggingDown(FeedbackResponseCommentsDb.class);
    }

    @AfterClass
    public static void classTearDown() throws Exception {
        printTestClassFooter();
    }

}
