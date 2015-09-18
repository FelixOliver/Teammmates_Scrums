package teammates.test.cases.ui;

import static org.testng.AssertJUnit.assertNotNull;
import static org.testng.AssertJUnit.assertEquals;
import static org.testng.AssertJUnit.assertTrue;
import static org.testng.AssertJUnit.assertFalse;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import teammates.common.datatransfer.AccountAttributes;
import teammates.common.datatransfer.DataBundle;
import teammates.common.datatransfer.StudentAttributes;
import teammates.common.exception.UnauthorizedAccessException;
import teammates.common.util.Const;
import teammates.common.util.StringHelper;
import teammates.storage.api.AccountsDb;
import teammates.storage.api.StudentsDb;
import teammates.ui.controller.RedirectResult;
import teammates.ui.controller.StudentCourseJoinAuthenticatedAction;

public class StudentCourseJoinAuthenticatedActionTest extends BaseActionTest {
    private static DataBundle dataBundle;

    @BeforeClass
    public static void classSetUp() throws Exception {
        printTestClassHeader();
        
        dataBundle = loadDataBundle("/StudentCourseJoinAuthenticatedTest.json");
        removeAndRestoreDatastoreFromJson("/StudentCourseJoinAuthenticatedTest.json");
        
        uri = Const.ActionURIs.STUDENT_COURSE_JOIN_AUTHENTICATED;
    }

    @Test
    public void testExecuteAndPostProcess() throws Exception {
        StudentsDb studentsDb = new StudentsDb();
        AccountsDb accountsDb = new AccountsDb();
        
        StudentAttributes student1InCourse1 = dataBundle.students
                .get("student1InCourse1");
        student1InCourse1 = studentsDb.getStudentForGoogleId(
                student1InCourse1.course, student1InCourse1.googleId);

        gaeSimulation.loginAsStudent(student1InCourse1.googleId);

        ______TS("not enough parameters");

        verifyAssumptionFailure();

        ______TS("invalid key");

        String invalidKey = "invalid key";
        String[] submissionParams = new String[] {
                Const.ParamsNames.REGKEY, invalidKey,
                Const.ParamsNames.NEXT_URL, Const.ActionURIs.STUDENT_HOME_PAGE
        };

        try {
            StudentCourseJoinAuthenticatedAction authenticatedAction = getAction(submissionParams);
            getRedirectResult(authenticatedAction);
        } catch (UnauthorizedAccessException uae) {
            assertEquals("No student with given registration key:" + invalidKey, uae.getMessage());
        }

        ______TS("already registered student");

        submissionParams = new String[] {
                Const.ParamsNames.REGKEY,
                StringHelper.encrypt(student1InCourse1.key),
                Const.ParamsNames.NEXT_URL, Const.ActionURIs.STUDENT_PROFILE_PAGE
        };

        StudentCourseJoinAuthenticatedAction authenticatedAction = getAction(submissionParams);
        RedirectResult redirectResult = getRedirectResult(authenticatedAction);

        assertEquals(Const.ActionURIs.STUDENT_HOME_PAGE
                + "?error=true&user=" + student1InCourse1.googleId,
                redirectResult.getDestinationWithParams());
        assertTrue(redirectResult.isError);
        assertEquals("You (student1InCourse1) have already joined this course", 
                redirectResult.getStatusMessage());

        /*______TS("student object belongs to another account");

        StudentAttributes student2InCourse1 = dataBundle.students
                .get("student2InCourse1");
        student2InCourse1 = studentsDb.getStudentForGoogleId(
                student2InCourse1.course, student2InCourse1.googleId);

        submissionParams = new String[] {
                Const.ParamsNames.REGKEY,
                StringHelper.encrypt(student2InCourse1.key),
                Const.ParamsNames.NEXT_URL, Const.ActionURIs.STUDENT_HOME_PAGE
        };

        authenticatedAction = getAction(submissionParams);
        redirectResult = getRedirectResult(authenticatedAction);

        assertEquals(
                Const.ActionURIs.STUDENT_HOME_PAGE
                        + "?persistencecourse=" + student1InCourse1.course
                        + "&error=true&user=" + student1InCourse1.googleId,
                redirectResult.getDestinationWithParams());
        assertTrue(redirectResult.isError);
        assertEquals(
                "The join link used belongs to a different user"
                        + " whose Google ID is stude..ourse1 "
                        + "(only part of the Google ID is shown to protect privacy)."
                        + " If that Google ID is owned by you, "
                        + "please logout and re-login using that Google account."
                        + " If it doesn’t belong to you, please "
                        + "<a href=\"mailto:teammates@comp.nus.edu.sg?body=Your name:%0AYour course:%0AYour university:\">"
                        + "contact us</a> so that we can investigate.",
                redirectResult.getStatusMessage());
*/
        ______TS("join course with no feedback sessions, profile is empty");
        AccountAttributes studentWithEmptyProfile = dataBundle.accounts.get("noFSStudent");
        studentWithEmptyProfile = accountsDb.getAccount(studentWithEmptyProfile.googleId, true);
        assertNotNull(studentWithEmptyProfile.studentProfile);
        assertEquals("", studentWithEmptyProfile.studentProfile.pictureKey);
        assertEquals("", studentWithEmptyProfile.studentProfile.shortName);
        assertEquals("", studentWithEmptyProfile.studentProfile.nationality);
        assertEquals("", studentWithEmptyProfile.studentProfile.moreInfo);
        assertEquals("", studentWithEmptyProfile.studentProfile.email);

        StudentAttributes studentWithEmptyProfileAttributes = dataBundle.students.get("noFSStudentWithNoProfile");
        studentWithEmptyProfileAttributes = studentsDb.getStudentForEmail(
                studentWithEmptyProfileAttributes.course, studentWithEmptyProfileAttributes.email);

        gaeSimulation.loginUser("idOfNoFSStudent");

        submissionParams = new String[] {
                Const.ParamsNames.REGKEY,
                StringHelper.encrypt(studentWithEmptyProfileAttributes.key),
                Const.ParamsNames.NEXT_URL, Const.ActionURIs.STUDENT_HOME_PAGE
        };

        authenticatedAction = getAction(submissionParams);
        redirectResult = getRedirectResult(authenticatedAction);

        assertEquals(Const.ActionURIs.STUDENT_HOME_PAGE
                + "?persistencecourse=idOfCourseNoEvals"
                + "&error=false&user=idOfNoFSStudent",
                redirectResult.getDestinationWithParams());
        assertFalse(redirectResult.isError);
        assertEquals(
                String.format(Const.StatusMessages.STUDENT_COURSE_JOIN_SUCCESSFUL, "[idOfCourseNoEvals] Typical Course 3 with 0 Evals")
                + "<br />"
                + String.format(Const.StatusMessages.HINT_FOR_NO_SESSIONS_STUDENT, "[idOfCourseNoEvals] Typical Course 3 with 0 Evals")
                + "<br />" 
                + Const.StatusMessages.STUDENT_UPDATE_PROFILE,  
                redirectResult.getStatusMessage());

        ______TS("join course with no feedback sessions, profile has only one missing field");
        AccountAttributes studentWithoutProfilePicture = dataBundle.accounts.get("noFSStudent2");
        studentWithoutProfilePicture = accountsDb.getAccount(studentWithoutProfilePicture.googleId, true);
        assertNotNull(studentWithoutProfilePicture.studentProfile);
        assertEquals("", studentWithoutProfilePicture.studentProfile.pictureKey);
        assertFalse(studentWithoutProfilePicture.studentProfile.nationality.equals(""));
        assertFalse(studentWithoutProfilePicture.studentProfile.shortName.equals(""));
        assertFalse(studentWithoutProfilePicture.studentProfile.moreInfo.equals(""));
        assertFalse(studentWithoutProfilePicture.studentProfile.email.equals(""));

        
        StudentAttributes studentWithoutProfilePictureAttributes = dataBundle.students.get("noFSStudentWithPartialProfile");
        
        studentWithoutProfilePictureAttributes = studentsDb.getStudentForEmail(
                studentWithoutProfilePictureAttributes.course, studentWithoutProfilePictureAttributes.email);

        gaeSimulation.loginUser("idOfNoFSStudent2");

        submissionParams = new String[] {
                Const.ParamsNames.REGKEY,
                StringHelper.encrypt(studentWithoutProfilePictureAttributes.key),
                Const.ParamsNames.NEXT_URL, Const.ActionURIs.STUDENT_HOME_PAGE
        };

        authenticatedAction = getAction(submissionParams);
        redirectResult = getRedirectResult(authenticatedAction);

        assertEquals(Const.ActionURIs.STUDENT_HOME_PAGE
                + "?persistencecourse=idOfCourseNoEvals"
                + "&error=false&user=idOfNoFSStudent2",
                redirectResult.getDestinationWithParams());
        assertFalse(redirectResult.isError);
        assertEquals(
                String.format(Const.StatusMessages.STUDENT_COURSE_JOIN_SUCCESSFUL, "[idOfCourseNoEvals] Typical Course 3 with 0 Evals")
                + "<br />"
                + String.format(Const.StatusMessages.HINT_FOR_NO_SESSIONS_STUDENT, "[idOfCourseNoEvals] Typical Course 3 with 0 Evals") 
                + "<br />"
                + Const.StatusMessages.STUDENT_UPDATE_PROFILE_PICTURE,
                redirectResult.getStatusMessage());

        ______TS("join course with no feedback sessions, profile has no missing field");        
        AccountAttributes studentWithFullProfile = dataBundle.accounts.get("noFSStudent3");
        
        studentWithFullProfile = accountsDb.getAccount(studentWithFullProfile.googleId, true);
        assertNotNull(studentWithFullProfile.studentProfile);
        assertFalse(studentWithFullProfile.studentProfile.pictureKey.equals(""));
        assertFalse(studentWithoutProfilePicture.studentProfile.nationality.equals(""));
        assertFalse(studentWithoutProfilePicture.studentProfile.shortName.equals(""));
        assertFalse(studentWithoutProfilePicture.studentProfile.moreInfo.equals(""));
        assertFalse(studentWithoutProfilePicture.studentProfile.email.equals(""));

        
        StudentAttributes studentWithFullProfileAttributes = dataBundle.students.get("noFSStudentWithFullProfile");
        studentWithFullProfileAttributes = studentsDb.getStudentForEmail(
                studentWithFullProfileAttributes.course, studentWithFullProfileAttributes.email);

        gaeSimulation.loginUser("idOfNoFSStudent3");

        submissionParams = new String[] {
                Const.ParamsNames.REGKEY,
                StringHelper.encrypt(studentWithFullProfileAttributes.key),
                Const.ParamsNames.NEXT_URL, Const.ActionURIs.STUDENT_HOME_PAGE
        };

        authenticatedAction = getAction(submissionParams);
        redirectResult = getRedirectResult(authenticatedAction);

        assertEquals(Const.ActionURIs.STUDENT_HOME_PAGE
                + "?persistencecourse=idOfCourseNoEvals"
                + "&error=false&user=idOfNoFSStudent3",
                redirectResult.getDestinationWithParams());
        assertFalse(redirectResult.isError);
        assertEquals(
                String.format(Const.StatusMessages.STUDENT_COURSE_JOIN_SUCCESSFUL, "[idOfCourseNoEvals] Typical Course 3 with 0 Evals") 
                + "<br />"
                + String.format(Const.StatusMessages.HINT_FOR_NO_SESSIONS_STUDENT, "[idOfCourseNoEvals] Typical Course 3 with 0 Evals"),
                redirectResult.getStatusMessage());

        
        ______TS("typical case");

        AccountAttributes newStudentAccount = new AccountAttributes(
                "idOfNewStudent", "nameOfNewStudent", false,
                "newStudent@gmail.com", "TEAMMATES Test Institute 5");
        accountsDb.createAccount(newStudentAccount);

        StudentAttributes newStudentAttributes = new StudentAttributes(
                student1InCourse1.section,
                student1InCourse1.team,
                "nameOfNewStudent", "newStudent@course1.com",
                "This is a new student", student1InCourse1.course);

        studentsDb.createEntity(newStudentAttributes);
        newStudentAttributes = studentsDb.getStudentForEmail(
                newStudentAttributes.course, newStudentAttributes.email);

        gaeSimulation.loginUser("idOfNewStudent");

        submissionParams = new String[] {
                Const.ParamsNames.REGKEY,
                StringHelper.encrypt(newStudentAttributes.key),
                Const.ParamsNames.NEXT_URL, Const.ActionURIs.STUDENT_PROFILE_PAGE
        };

        authenticatedAction = getAction(submissionParams);
        redirectResult = getRedirectResult(authenticatedAction);

        assertEquals(Const.ActionURIs.STUDENT_PROFILE_PAGE
                + "?persistencecourse=idOfTypicalCourse1"
                + "&error=false&user=idOfNewStudent",
                redirectResult.getDestinationWithParams());
        assertFalse(redirectResult.isError);
        assertEquals(
                String.format(Const.StatusMessages.STUDENT_COURSE_JOIN_SUCCESSFUL, "[idOfTypicalCourse1] Typical Course 1 with 2 Evals"), 
                redirectResult.getStatusMessage());

    }

    private StudentCourseJoinAuthenticatedAction getAction(String... params)
            throws Exception {

        return (StudentCourseJoinAuthenticatedAction) (gaeSimulation
                .getActionObject(uri, params));

    }
}
