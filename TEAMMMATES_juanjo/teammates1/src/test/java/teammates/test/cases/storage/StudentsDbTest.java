package teammates.test.cases.storage;

import static org.testng.AssertJUnit.assertEquals;
import static org.testng.AssertJUnit.assertNotNull;
import static org.testng.AssertJUnit.assertNull;
import static org.testng.AssertJUnit.assertTrue;
import static teammates.common.util.FieldValidator.COURSE_ID_ERROR_MESSAGE;
import static teammates.common.util.FieldValidator.REASON_INCORRECT_FORMAT;

import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import teammates.common.datatransfer.StudentAttributes;
import teammates.common.exception.EntityAlreadyExistsException;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.exception.InvalidParametersException;
import teammates.common.util.Const;
import teammates.common.util.StringHelper;
import teammates.storage.api.StudentsDb;
import teammates.test.cases.BaseComponentTestCase;
import teammates.test.driver.AssertHelper;
import teammates.test.util.TestHelper;

public class StudentsDbTest extends BaseComponentTestCase {
    

    private StudentsDb studentsDb = new StudentsDb();
    
    @BeforeClass
    public static void setupClass() throws Exception {
        printTestClassHeader();
        turnLoggingUp(StudentsDb.class);
    }
    
    @Test
    public void testCreateStudent() throws EntityAlreadyExistsException, InvalidParametersException {
        
        StudentAttributes s = new StudentAttributes();
        s.name = "valid student";
        s.lastName = "student";
        s.email = "valid-fresh@email.com";
        s.team = "validTeamName";
        s.section = "validSectionName";
        s.comments = "";
        s.googleId = "validGoogleId";

        ______TS("fail : invalid params"); 
        s.course = "invalid id space";
        try {
            studentsDb.createEntity(s);
            Assert.fail();
        } catch (InvalidParametersException e) {
            AssertHelper.assertContains(
                    String.format(COURSE_ID_ERROR_MESSAGE, s.course,
                            REASON_INCORRECT_FORMAT),
                    e.getMessage());
        }
        TestHelper.verifyAbsentInDatastore(s);

        ______TS("success : valid params");
        s.course = "valid-course";
        studentsDb.createEntity(s);
        TestHelper.verifyPresentInDatastore(s);
        StudentAttributes retrievedStudent = studentsDb.getStudentForGoogleId(s.course, s.googleId);
        assertEquals(true, retrievedStudent.isEnrollInfoSameAs(s));
        assertEquals(null, studentsDb.getStudentForGoogleId(s.course + "not existing", s.googleId));
        assertEquals(null, studentsDb.getStudentForGoogleId(s.course, s.googleId + "not existing"));
        assertEquals(null, studentsDb.getStudentForGoogleId(s.course+ "not existing", s.googleId + "not existing"));
        
        ______TS("fail : duplicate");
        try {
            studentsDb.createEntity(s);
            Assert.fail();
        } catch (EntityAlreadyExistsException e) {
            AssertHelper.assertContains(
                    String.format(
                            StudentsDb.ERROR_CREATE_ENTITY_ALREADY_EXISTS,
                            s.getEntityTypeAsString())
                            + s.getIdentificationString(), e.getMessage());
        }

        ______TS("null params check");
        try {
            studentsDb.createEntity(null);
            Assert.fail();
        } catch (AssertionError a) {
            assertEquals(Const.StatusCodes.DBLEVEL_NULL_INPUT, a.getMessage());
        }
    }

    @SuppressWarnings("deprecation")
    @Test
    public void testGetStudent() throws InvalidParametersException, EntityDoesNotExistException {
        int currentNumberOfStudent = studentsDb.getAllStudents().size();
        StudentAttributes s = createNewStudent();
        s.googleId = "validGoogleId";
        s.googleId = "validTeam";
        studentsDb.updateStudentWithoutSearchability(s.course, s.email, s.name, s.team, s.section, s.email, s.googleId, s.comments);
        
        ______TS("typical success case: existent");
        StudentAttributes retrieved = studentsDb.getStudentForEmail(s.course, s.email);
        assertNotNull(retrieved);
        assertNotNull(studentsDb.getStudentForRegistrationKey(retrieved.key));
        assertNotNull(studentsDb.getStudentForRegistrationKey(StringHelper.encrypt(retrieved.key)));
        assertNull(studentsDb.getStudentForRegistrationKey("notExistingKey"));
        ______TS("non existant student case");
        retrieved = studentsDb.getStudentForEmail("any-course-id", "non-existent@email.com");
        assertNull(retrieved);
        
        StudentAttributes s2 = createNewStudent("one.new@gmail.com");
        s2.googleId = "validGoogleId2";
        studentsDb.updateStudentWithoutSearchability(s2.course, s2.email, s2.name, s2.team, s2.section, s2.email, s2.googleId, s2.comments);
        studentsDb.deleteStudentsForGoogleIdWithoutDocument(s2.googleId);
        assertNull(studentsDb.getStudentForGoogleId(s2.course, s2.googleId));
        
        s2 = createNewStudent("one.new@gmail.com");
        assertEquals(true, studentsDb.getUnregisteredStudentsForCourse(s2.course).get(0).isEnrollInfoSameAs(s2));
        
        s2.googleId = null;
        studentsDb.updateStudentWithoutSearchability(s2.course, s2.email, s2.name, s2.team, s2.section, s2.email, s2.googleId, s2.comments);
        assertEquals(true, studentsDb.getUnregisteredStudentsForCourse(s2.course).get(0).isEnrollInfoSameAs(s2));
        
        assertTrue(s.isEnrollInfoSameAs(studentsDb.getStudentsForGoogleId(s.googleId).get(0)));
        assertEquals(true, studentsDb.getStudentsForCourse(s.course).get(0).isEnrollInfoSameAs(s));
        assertEquals(true, studentsDb.getStudentsForTeam(s.team, s.course).get(0).isEnrollInfoSameAs(s));
        assertEquals(2 + currentNumberOfStudent, studentsDb.getAllStudents().size()); 
        
        
        ______TS("null params case");
        try {
            studentsDb.getStudentForEmail(null, "valid@email.com");
            Assert.fail();
        } catch (AssertionError a) {
            assertEquals(Const.StatusCodes.DBLEVEL_NULL_INPUT, a.getMessage());
        }        
        try {
            studentsDb.getStudentForEmail("any-course-id", null);
            Assert.fail();
        } catch (AssertionError a) {
            assertEquals(Const.StatusCodes.DBLEVEL_NULL_INPUT, a.getMessage());
        }
    }
    
    @Test
    public void testupdateStudentWithoutDocument() throws InvalidParametersException, EntityDoesNotExistException {
        
        // Create a new student with valid attributes
        StudentAttributes s = createNewStudent();
        studentsDb.updateStudentWithoutSearchability(s.course, s.email, "new-name", "new-team", "new-section", "new@email.com", "new.google.id", "lorem ipsum dolor si amet");
        
        ______TS("non-existent case");
        try {
            studentsDb.updateStudentWithoutSearchability("non-existent-course", "non@existent.email", "no-name", "non-existent-team", "non-existent-section", "non.existent.ID", "blah", "blah");
            signalFailureToDetectException();
        } catch (EntityDoesNotExistException e) {
            assertEquals(StudentsDb.ERROR_UPDATE_NON_EXISTENT_STUDENT + "non-existent-course/non@existent.email", e.getMessage());
        }
        
        // Only check first 2 params (course & email) which are used to identify the student entry. The rest are actually allowed to be null.
        ______TS("null course case");
        try {
            studentsDb.updateStudentWithoutSearchability(null, s.email, "new-name", "new-team", "new-section", "new@email.com", "new.google.id", "lorem ipsum dolor si amet");
            signalFailureToDetectException();
        } catch (AssertionError a) {
            assertEquals(Const.StatusCodes.DBLEVEL_NULL_INPUT, a.getMessage());
        }
        
        ______TS("null email case");
        try {
            studentsDb.updateStudentWithoutSearchability(s.course, null, "new-name", "new-team", "new-section", "new@email.com", "new.google.id", "lorem ipsum dolor si amet");
            signalFailureToDetectException();
        } catch (AssertionError a) {
            assertEquals(Const.StatusCodes.DBLEVEL_NULL_INPUT, a.getMessage());
        }
        
        ______TS("duplicate email case");
        s = createNewStudent();
        // Create a second student with different email address
        StudentAttributes s2 = createNewStudent("valid2@email.com");
        try {
            studentsDb.updateStudentWithoutSearchability(s.course, s.email, "new-name", "new-team", "new-section", s2.email, "new.google.id", "lorem ipsum dolor si amet");
            signalFailureToDetectException();
        } catch (InvalidParametersException e) {
            assertEquals(StudentsDb.ERROR_UPDATE_EMAIL_ALREADY_USED + s2.name + "/" + 
                    s2.email,e.getMessage());
        }

        ______TS("typical success case");
        String originalEmail = s.email;
        s.name = "new-name-2";
        s.team = "new-team-2";
        s.email = "new-email-2";
        s.googleId = "new-id-2";
        s.comments = "this are new comments";
        studentsDb.updateStudentWithoutSearchability(s.course, originalEmail, s.name, s.team, s.section, s.email, s.googleId, s.comments);
        
        StudentAttributes updatedStudent = studentsDb.getStudentForEmail(s.course, s.email);
        assertTrue(updatedStudent.isEnrollInfoSameAs(s));
    }

    @SuppressWarnings("deprecation")
    @Test
    public void testDeleteStudent() throws InvalidParametersException, EntityDoesNotExistException {
        StudentAttributes s = createNewStudent();
        s.googleId = "validGoogleId";
        studentsDb.updateStudentWithoutSearchability(s.course, s.email, s.name, s.team, s.section, s.email, s.googleId, s.comments);
        // Delete
        studentsDb.deleteStudentWithoutDocument(s.course, s.email);
        
        StudentAttributes deleted = studentsDb.getStudentForEmail(s.course, s.email);
        
        assertNull(deleted);
        studentsDb.deleteStudentsForGoogleIdWithoutDocument(s.googleId);
        assertEquals(null, studentsDb.getStudentForGoogleId(s.course , s.googleId));
        int currentStudentNum = studentsDb.getAllStudents().size();
        s = createNewStudent();
        createNewStudent("secondStudent@mail.com");
        assertEquals(2 + currentStudentNum, studentsDb.getAllStudents().size());
        studentsDb.deleteStudentsForCourseWithoutDocument(s.course);
        assertEquals(currentStudentNum, studentsDb.getAllStudents().size());
        // delete again - should fail silently
        studentsDb.deleteStudentWithoutDocument(s.course, s.email);
        
        // Null params check:
        try {
            studentsDb.deleteStudentWithoutDocument(null, s.email);
            Assert.fail();
        } catch (AssertionError a) {
            assertEquals(Const.StatusCodes.DBLEVEL_NULL_INPUT, a.getMessage());
        }
        
        try {
            studentsDb.deleteStudentWithoutDocument(s.course, null);
            Assert.fail();
        } catch (AssertionError a) {
            assertEquals(Const.StatusCodes.DBLEVEL_NULL_INPUT, a.getMessage());
        }
        
      //Untested case: The deletion is not persisted immediately (i.e. persistence delay) 
      //       Reason: Difficult to reproduce a persistence delay during testing
    }
    
    @AfterClass
    public static void tearDownClass() throws Exception {
        turnLoggingDown(StudentsDb.class);
    }
    
    private StudentAttributes createNewStudent() throws InvalidParametersException {
        StudentAttributes s = new StudentAttributes();
        s.name = "valid student";
        s.course = "valid-course";
        s.email = "valid@email.com";
        s.team = "validTeamName";
        s.section = "validSectionName";
        s.comments = "";
        s.googleId="";
        try {
            studentsDb.createEntity(s);
        } catch (EntityAlreadyExistsException e) {
            // Okay if it's already inside
        }
        
        return s;
    }
    
    private StudentAttributes createNewStudent(String email) throws InvalidParametersException {
        StudentAttributes s = new StudentAttributes();
        s.name = "valid student 2";
        s.course = "valid-course";
        s.email = email;
        s.team = "valid team name";
        s.section = "valid section name";
        s.comments = "";
        s.googleId="";
        try {
            studentsDb.createEntity(s);
        } catch (EntityAlreadyExistsException e) {
            // Okay if it's already inside
        }
        
        return s;
    }
}
