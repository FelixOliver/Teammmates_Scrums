package teammates.test.cases.ui.pagedata;

import static org.testng.AssertJUnit.assertEquals;

import org.testng.annotations.Test;

import teammates.common.datatransfer.AccountAttributes;
import teammates.common.datatransfer.StudentProfileAttributes;
import teammates.common.util.Const;
import teammates.common.util.StringHelper;
import teammates.ui.controller.StudentProfilePageData;
import teammates.ui.template.StudentProfileEditBox;
import teammates.ui.template.StudentProfileUploadPhotoModal;

public class StudentProfilePageDataTest {
    
    private StudentProfileAttributes spa;
    private AccountAttributes acct;
    private String isEditingPhoto;
    private String pictureUrl;

    private StudentProfilePageData sppd;

    @Test
    public void testAll() {
        testWithPictureKeyAndNoNullFields();
        testWithNoPictureKeyAndNullFields();
    }

    private void testWithPictureKeyAndNoNullFields() {
        sppd = initializeDataWithPictureKeyAndNoNullFields();
        testProfileEditBox(sppd.getProfileEditBox());
        testUploadPhotoModal(sppd.getUploadPhotoModal());
    }

    private void testWithNoPictureKeyAndNullFields() {
        sppd = initializeDataWithNoPictureKeyAndNullFields();
        testProfileEditBox(sppd.getProfileEditBox());
        testUploadPhotoModal(sppd.getUploadPhotoModal());
    }

    private StudentProfilePageData initializeDataWithPictureKeyAndNoNullFields() {
        spa = new StudentProfileAttributes("valid.id.2", "short name", "e@mail2.com", "inst", "nationality",
                                           "male", "more info", "pictureKey");
        acct = new AccountAttributes("valid.id", "full name", false, "e@mail1.com", "inst", spa);
        isEditingPhoto = "false";
        pictureUrl = Const.ActionURIs.STUDENT_PROFILE_PICTURE
                   + "?" + Const.ParamsNames.BLOB_KEY + "=" + spa.pictureKey
                   + "&" + Const.ParamsNames.USER_ID + "=" + acct.googleId;
        return new StudentProfilePageData(acct, isEditingPhoto);        
    }
    
    private StudentProfilePageData initializeDataWithNoPictureKeyAndNullFields() {
        spa = new StudentProfileAttributes("valid.id.2", null, null, null, null, "male", null, "");
        acct = new AccountAttributes("valid.id", "full name", false, "e@mail1.com", "inst", spa);
        pictureUrl = Const.SystemParams.DEFAULT_PROFILE_PICTURE_PATH;
        return new StudentProfilePageData(acct, isEditingPhoto);        
    }
    
    private void testProfileEditBox(StudentProfileEditBox profileEditBox) {
        assertEquals(acct.name, profileEditBox.getName());
        assertEquals(isEditingPhoto, profileEditBox.getEditingPhoto());
        assertEquals(StringHelper.convertToEmptyStringIfNull(spa.shortName), profileEditBox.getShortName());
        /*
         * The email field value is the one entered by student (long-term contact email), not the one
         * entered by instructor during enrollment. It comes from SPA, not AA.
         */
        assertEquals(StringHelper.convertToEmptyStringIfNull(spa.email), profileEditBox.getEmail());
        assertEquals(StringHelper.convertToEmptyStringIfNull(spa.institute), profileEditBox.getInstitute());
        assertEquals(StringHelper.convertToEmptyStringIfNull(spa.nationality), profileEditBox.getNationality());
        assertEquals(spa.gender, profileEditBox.getGender());
        assertEquals(StringHelper.convertToEmptyStringIfNull(spa.moreInfo), profileEditBox.getMoreInfo());
        /*
         * Currently across the application googleId is always taken from Account.
         * TODO check if googleId in SPA can ever be different from AA.
         */
        assertEquals(acct.googleId, profileEditBox.getGoogleId());
        assertEquals(pictureUrl, profileEditBox.getPictureUrl());
    }

    private void testUploadPhotoModal(StudentProfileUploadPhotoModal uploadPhotoModal) {
        assertEquals(acct.googleId, uploadPhotoModal.getGoogleId());
        assertEquals(pictureUrl, uploadPhotoModal.getPictureUrl());
        assertEquals(spa.pictureKey, uploadPhotoModal.getPictureKey());
    }
    
}
