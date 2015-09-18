package teammates.test.cases.storage;

import static org.testng.AssertJUnit.assertEquals;
import static org.testng.AssertJUnit.assertNotNull;
import static org.testng.AssertJUnit.assertNull;
import static org.testng.Assert.fail;
import static teammates.common.util.FieldValidator.EMAIL_ERROR_MESSAGE;
import static teammates.common.util.FieldValidator.GOOGLE_ID_ERROR_MESSAGE;
import static teammates.common.util.FieldValidator.PERSON_NAME_ERROR_MESSAGE;
import static teammates.common.util.FieldValidator.REASON_EMPTY;
import static teammates.common.util.FieldValidator.REASON_INCORRECT_FORMAT;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import teammates.common.datatransfer.DataBundle;
import teammates.common.datatransfer.InstructorAttributes;
import teammates.common.datatransfer.InstructorPrivileges;
import teammates.common.exception.EntityAlreadyExistsException;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.exception.InvalidParametersException;
import teammates.common.util.Const;
import teammates.common.util.StringHelper;
import teammates.logic.core.InstructorsLogic;
import teammates.storage.api.EntitiesDb;
import teammates.storage.api.InstructorsDb;
import teammates.test.cases.BaseComponentTestCase;
import teammates.test.driver.AssertHelper;
import teammates.test.util.TestHelper;

public class InstructorsDbTest extends BaseComponentTestCase {
    
    private static final InstructorsDb instructorsDb = new InstructorsDb();
    private static DataBundle dataBundle = getTypicalDataBundle();
    
    @BeforeClass
    public static void setupClass() throws Exception {
        printTestClassHeader();
        turnLoggingUp(InstructorsDb.class);
        addInstructorsToDb();
    }
    
    private static void addInstructorsToDb() throws Exception {
        Set<String> keys = dataBundle.instructors.keySet();
        for (String i : keys) {
            try {
                instructorsDb.createEntity(dataBundle.instructors.get(i));
            } catch (EntityAlreadyExistsException e) {
                instructorsDb.updateInstructorByGoogleId(
                        dataBundle.instructors.get(i));
            }
        }
    }

    @Test
    public void testCreateInstructor() 
            throws EntityAlreadyExistsException, InvalidParametersException {
        
        ______TS("Success: create an instructor");
        
        String googleId = "valid.fresh.id";
        String courseId = "valid.course.Id";
        String name = "valid.name";
        String email = "valid@email.tmt";
        String role = Const.InstructorPermissionRoleNames.INSTRUCTOR_PERMISSION_ROLE_COOWNER;
        String displayedName = InstructorAttributes.DEFAULT_DISPLAY_NAME;
        InstructorPrivileges privileges = new InstructorPrivileges(Const.InstructorPermissionRoleNames.INSTRUCTOR_PERMISSION_ROLE_COOWNER);
        InstructorAttributes i = new InstructorAttributes(googleId, courseId, name, email, role, displayedName, privileges);
        
        instructorsDb.deleteEntity(i);
        instructorsDb.createEntity(i);
        
        TestHelper.verifyPresentInDatastore(i);
        
        ______TS("Failure: create a duplicate instructor");

        try {
            instructorsDb.createEntity(i);
            signalFailureToDetectException();
        } catch (EntityAlreadyExistsException e) {
            AssertHelper.assertContains(String.format(InstructorsDb.ERROR_CREATE_ENTITY_ALREADY_EXISTS, "Instructor"),
                                        e.getMessage());
        }
        
        ______TS("Failure: create an instructor with invalid parameters");

        i.googleId = "invalid id with spaces";
        try {
            instructorsDb.createEntity(i);
            signalFailureToDetectException();
        } catch (InvalidParametersException e) {
            AssertHelper.assertContains(
                    String.format(GOOGLE_ID_ERROR_MESSAGE, i.googleId, REASON_INCORRECT_FORMAT),
                    e.getMessage());
        }
        
        i.googleId = "valid.fresh.id";
        i.email = "invalid.email.tmt";
        try {
            instructorsDb.createEntity(i);
            signalFailureToDetectException();
        } catch (InvalidParametersException e) {
            AssertHelper.assertContains(
                    String.format(EMAIL_ERROR_MESSAGE, i.email, REASON_INCORRECT_FORMAT),
                    e.getMessage());
        }

        ______TS("Failure: null parameters");
        
        try {
            instructorsDb.createEntity(null);
            signalFailureToDetectException();
        } catch (AssertionError e) {
            assertEquals(Const.StatusCodes.DBLEVEL_NULL_INPUT, e.getMessage());
        }
    }
    
    @Test
    public void testGetInstructorForEmail() throws InvalidParametersException {
        
        InstructorAttributes i = dataBundle.instructors.get("instructor1OfCourse1");
        
        ______TS("Success: get an instructor");
        
        InstructorAttributes retrieved = instructorsDb.getInstructorForEmail(i.courseId, i.email);
        assertNotNull(retrieved);
        
        ______TS("Failure: instructor does not exist");
        
        retrieved = instructorsDb.getInstructorForEmail("non.existent.course", "non.existent");
        assertNull(retrieved);
        
        ______TS("Failure: null parameters");

        try {
            instructorsDb.getInstructorForEmail(null, null);
            signalFailureToDetectException();
        } catch (AssertionError e) {
            assertEquals(Const.StatusCodes.DBLEVEL_NULL_INPUT, e.getMessage());
        }
    }
    
    @Test
    public void testGetInstructorForGoogleId() throws InvalidParametersException {
        
        InstructorAttributes i = dataBundle.instructors.get("instructor1OfCourse1");
        
        ______TS("Success: get an instructor");
        
        InstructorAttributes retrieved = instructorsDb.getInstructorForGoogleId(i.courseId, i.googleId);
        assertNotNull(retrieved);
        
        ______TS("Failure: instructor does not exist");
        
        retrieved = instructorsDb.getInstructorForGoogleId("non.existent.course", "non.existent");
        assertNull(retrieved);
        
        ______TS("Failure: null parameters");
        
        try {
            instructorsDb.getInstructorForGoogleId(null, null);
            signalFailureToDetectException();
        } catch (AssertionError e) {
            assertEquals(Const.StatusCodes.DBLEVEL_NULL_INPUT, e.getMessage());
        }
    }
    
    @Test
    public void testGetInstructorForRegistrationKey() throws InvalidParametersException {
        
        InstructorAttributes i = dataBundle.instructors.get("instructorNotYetJoinCourse");
        
        ______TS("Success: get an instructor");
        
        String key = i.key;
        
        InstructorAttributes retrieved = instructorsDb.getInstructorForRegistrationKey(StringHelper.encrypt(key));
        assertEquals(i.courseId, retrieved.courseId);
        assertEquals(i.name, retrieved.name);
        assertEquals(i.email, retrieved.email);
        
        ______TS("Failure: instructor does not exist");
        
        key = "non.existent.key";
        retrieved = instructorsDb.getInstructorForRegistrationKey(StringHelper.encrypt(key));
        assertNull(retrieved);
        
        ______TS("Failure: null parameters");
        
        try {
            instructorsDb.getInstructorForRegistrationKey(null);
            signalFailureToDetectException();
        } catch (AssertionError e) {
            assertEquals(Const.StatusCodes.DBLEVEL_NULL_INPUT, e.getMessage());
        }
    }

    @Test
    public void testGetInstructorsForEmail() throws Exception {
        
        ______TS("Success: get instructors with specific email");
        
        String email = "instructor1@course1.tmt";
        
        List<InstructorAttributes> retrieved = instructorsDb.getInstructorsForEmail(email);
        assertEquals(1, retrieved.size());
        
        InstructorAttributes instructor = retrieved.get(0);
        
        assertEquals("idOfTypicalCourse1", instructor.courseId);
        
        ______TS("Failure: instructor does not exist");
        
        retrieved = instructorsDb.getInstructorsForEmail("non-exist-email");
        assertEquals(0, retrieved.size());
        
        ______TS("Failure: null parameters");

        try {
            instructorsDb.getInstructorsForEmail(null);
            signalFailureToDetectException();
        } catch (AssertionError e) {
            assertEquals(Const.StatusCodes.DBLEVEL_NULL_INPUT, e.getMessage());
        }
    }

    @Test
    public void testGetInstructorsForGoogleId() throws Exception {
        
        ______TS("Success: get instructors with specific googleId");
        
        String googleId = "idOfInstructor3";
        
        List<InstructorAttributes> retrieved = instructorsDb.getInstructorsForGoogleId(googleId, false);
        assertEquals(2, retrieved.size());
        
        InstructorAttributes instructor1 = retrieved.get(0);
        InstructorAttributes instructor2 = retrieved.get(1);
        
        assertEquals("idOfTypicalCourse1", instructor1.courseId);
        assertEquals("idOfTypicalCourse2", instructor2.courseId);
        

        ______TS("Success: get instructors with specific googleId, with 1 archived course.");
        
        InstructorsLogic.inst().setArchiveStatusOfInstructor(googleId, instructor1.courseId, true);
        retrieved = instructorsDb.getInstructorsForGoogleId(googleId, true);
        assertEquals(1, retrieved.size());
        InstructorsLogic.inst().setArchiveStatusOfInstructor(googleId, instructor1.courseId, false);
        
        ______TS("Failure: instructor does not exist");
        
        retrieved = instructorsDb.getInstructorsForGoogleId("non-exist-id", false);
        assertEquals(0, retrieved.size());
        
        ______TS("Failure: null parameters");

        try {
            instructorsDb.getInstructorsForGoogleId(null, false);
            signalFailureToDetectException();
        } catch (AssertionError e) {
            assertEquals(Const.StatusCodes.DBLEVEL_NULL_INPUT, e.getMessage());
        }
    }
    
    @Test
    public void testGetInstructorsForCourse() throws Exception {
        
        ______TS("Success: get instructors of a specific course");
        
        String courseId = "idOfTypicalCourse1";
        
        List<InstructorAttributes> retrieved = instructorsDb.getInstructorsForCourse(courseId);
        assertEquals(5, retrieved.size());
        
        List<String> idList = new ArrayList<String>();
        idList.add("idOfInstructor1OfCourse1");
        idList.add("idOfInstructor2OfCourse1");
        idList.add("idOfInstructor3");
        idList.add("idOfHelperOfCourse1");
        idList.add(null);
        for (InstructorAttributes instructor : retrieved) {
            if (idList.contains(instructor.googleId)) {
            } else {
                fail();
            }
        }
        
        ______TS("Failure: no instructors for a course");
        
        retrieved = instructorsDb.getInstructorsForCourse("non-exist-course");
        assertEquals(0, retrieved.size());
        
        ______TS("Failure: null parameters");

        try {
            instructorsDb.getInstructorsForCourse(null);
            signalFailureToDetectException();
        } catch (AssertionError e) {
            assertEquals(Const.StatusCodes.DBLEVEL_NULL_INPUT, e.getMessage());
        }
    }
    
    @Test
    public void testUpdateInstructorByGoogleId() throws Exception {
        
        InstructorAttributes instructorToEdit = dataBundle.instructors.get("instructor2OfCourse1");
        
        ______TS("Success: update an instructor");

        instructorToEdit.name = "New Name";
        instructorToEdit.email = "InstrDbT.new-email@email.tmt";
        instructorsDb.updateInstructorByGoogleId(instructorToEdit);
        
        InstructorAttributes instructorUpdated = instructorsDb.getInstructorForGoogleId(instructorToEdit.courseId, instructorToEdit.googleId);
        assertEquals(instructorToEdit.name, instructorUpdated.name);
        assertEquals(instructorToEdit.email, instructorUpdated.email);
        
        ______TS("Failure: invalid parameters");
        
        instructorToEdit.name = "";
        instructorToEdit.email = "aaa";
        try {
            instructorsDb.updateInstructorByGoogleId(instructorToEdit);
            signalFailureToDetectException();
        } catch (InvalidParametersException e) {
            AssertHelper.assertContains(
                        String.format(PERSON_NAME_ERROR_MESSAGE, instructorToEdit.name, REASON_EMPTY) + Const.EOL 
                        + String.format(EMAIL_ERROR_MESSAGE, instructorToEdit.email, REASON_INCORRECT_FORMAT),
                        e.getMessage());
        }

        ______TS("Failure: non-existent entity");

        instructorToEdit.googleId = "idOfInstructor4";
        instructorToEdit.name = "New Name 2";
        instructorToEdit.email = "InstrDbT.new-email2@email.tmt";
        try {
            instructorsDb.updateInstructorByGoogleId(instructorToEdit);
            signalFailureToDetectException();
        } catch (EntityDoesNotExistException e) {
            AssertHelper.assertContains(
                        EntitiesDb.ERROR_UPDATE_NON_EXISTENT_ACCOUNT,
                        e.getMessage());
        }
        
        ______TS("Failure: null parameters");

        try {
            instructorsDb.updateInstructorByGoogleId(null);
            signalFailureToDetectException();
        } catch (AssertionError e) {
            assertEquals(Const.StatusCodes.DBLEVEL_NULL_INPUT, e.getMessage());
        }
    }

    @Test
    public void testUpdateInstructorByEmail() throws Exception {
        
        InstructorAttributes instructorToEdit = instructorsDb.getInstructorForEmail("idOfTypicalCourse1", "instructor1@course1.tmt");
        
        ______TS("Success: update an instructor");
        
        instructorToEdit.googleId = "new-id";
        instructorToEdit.name = "New Name";
        instructorsDb.updateInstructorByEmail(instructorToEdit);
        
        InstructorAttributes instructorUpdated = instructorsDb.getInstructorForEmail(instructorToEdit.courseId, instructorToEdit.email);
        assertEquals("new-id", instructorUpdated.googleId);
        assertEquals("New Name", instructorUpdated.name);

        ______TS("Failure: invalid parameters");

        instructorToEdit.googleId = "invalid id";
        instructorToEdit.name = "";
        try {
            instructorsDb.updateInstructorByEmail(instructorToEdit);
            signalFailureToDetectException();
        } catch (InvalidParametersException e) {
            AssertHelper.assertContains(
                    String.format(GOOGLE_ID_ERROR_MESSAGE, instructorToEdit.googleId, REASON_INCORRECT_FORMAT)
                            + Const.EOL
                            + String.format(PERSON_NAME_ERROR_MESSAGE, instructorToEdit.name, REASON_EMPTY),
                    e.getMessage());
        }

        ______TS("Failure: non-existent entity");

        instructorToEdit.googleId = "idOfInstructor4";
        instructorToEdit.name = "New Name 2";
        instructorToEdit.email = "newEmail@email.tmt";
        try {
            instructorsDb.updateInstructorByEmail(instructorToEdit);
            signalFailureToDetectException();
        } catch (EntityDoesNotExistException e) {
            AssertHelper.assertContains(
                        EntitiesDb.ERROR_UPDATE_NON_EXISTENT_ACCOUNT,
                        e.getMessage());
        }

        ______TS("Failure: null parameters");

        try {
            instructorsDb.updateInstructorByEmail(null);
            signalFailureToDetectException();
        } catch (AssertionError e) {
            assertEquals(Const.StatusCodes.DBLEVEL_NULL_INPUT, e.getMessage());
        }
    }
    
    @Test
    public void testDeleteInstructor() throws InvalidParametersException {
        InstructorAttributes i = dataBundle.instructors.get("instructorWithOnlyOneSampleCourse");
        
        ______TS("Success: delete an instructor");
        
        instructorsDb.deleteInstructor(i.courseId, i.email);
        
        InstructorAttributes deleted = instructorsDb.getInstructorForEmail(i.courseId, i.email);
        assertNull(deleted);
        
        ______TS("Failure: delete a non-exist instructor, should fail silently");

        instructorsDb.deleteInstructor(i.courseId, i.email);
        
        ______TS("Failure: null parameters");
        
        try {
            instructorsDb.deleteInstructor(null, null);
            signalFailureToDetectException();
        } catch (AssertionError e) {
            assertEquals(Const.StatusCodes.DBLEVEL_NULL_INPUT, e.getMessage());
        }
    }
    
    @Test
    public void testDeleteInstructorsForGoogleId() throws Exception {
        
        ______TS("Success: delete instructors with specific googleId");
        
        String googleId = "instructorWithOnlyOneSampleCourse";
        instructorsDb.deleteInstructorsForGoogleId(googleId);
        
        List<InstructorAttributes> retrieved = instructorsDb.getInstructorsForGoogleId(googleId, false);
        assertEquals(0, retrieved.size());
        
        ______TS("Failure: try to delete where there's no instructors associated with the googleId, should fail silently");
        
        instructorsDb.deleteInstructorsForGoogleId(googleId);
        
        ______TS("Failure: null parameters");
        
        try {
            instructorsDb.deleteInstructorsForGoogleId(null);
            signalFailureToDetectException();
        } catch (AssertionError e) {
            assertEquals(Const.StatusCodes.DBLEVEL_NULL_INPUT, e.getMessage());
        }
    }
    
    @Test
    public void testDeleteInstructorsForCourse() throws Exception {
        
        ______TS("Success: delete instructors of a specific course");
        
        String courseId = "idOfArchivedCourse";
        instructorsDb.deleteInstructorsForCourse(courseId);
        
        List<InstructorAttributes> retrieved = instructorsDb.getInstructorsForCourse(courseId);
        assertEquals(0, retrieved.size());
        
        ______TS("Failure: no instructor exists for the course, should fail silently");
        
        instructorsDb.deleteInstructorsForCourse(courseId);
        
        ______TS("Failure: null parameters");
        
        try {
            instructorsDb.deleteInstructorsForCourse(null);
            signalFailureToDetectException();
        } catch (AssertionError e) {
            assertEquals(Const.StatusCodes.DBLEVEL_NULL_INPUT, e.getMessage());
        }
    }
    
    @AfterClass
    public void classTearDown() throws Exception {
        deleteInstructorsFromDb();
        printTestClassFooter();
    }
    
    private static void deleteInstructorsFromDb() throws Exception {
        Set<String> keys = dataBundle.instructors.keySet();
        for (String i : keys) {
            instructorsDb.deleteEntity(dataBundle.instructors.get(i));
        }
    }
}
