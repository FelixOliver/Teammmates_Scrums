package teammates.client.scripts;

import java.io.IOException;
import java.util.List;

import teammates.client.remoteapi.RemoteApiClient;
import teammates.common.datatransfer.FeedbackParticipantType;
import teammates.common.datatransfer.FeedbackQuestionAttributes;
import teammates.common.datatransfer.FeedbackResponseAttributes;
import teammates.common.datatransfer.StudentAttributes;
import teammates.common.exception.EntityAlreadyExistsException;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.exception.InvalidParametersException;
import teammates.logic.api.Logic;
import teammates.storage.datastore.Datastore;

public class DataRepairForCorruptedResponses extends RemoteApiClient {
    
    private Logic logic = new Logic();
    
    public static void main(String[] args) throws IOException{
        DataRepairForCorruptedResponses dataRepair = new DataRepairForCorruptedResponses();
        dataRepair.doOperationRemotely();
    }
    
    @Override
    protected void doOperation() {
        Datastore.initialize();
        try {
            repairDataForSession("Course-ID", "Session name");
        } catch (EntityDoesNotExistException | InvalidParametersException | EntityAlreadyExistsException e) {
            e.printStackTrace();
        }
    }
    
    private void repairDataForSession(String courseId, String sessionName) throws EntityDoesNotExistException, InvalidParametersException, EntityAlreadyExistsException{
        List<FeedbackQuestionAttributes> questions = logic.getFeedbackQuestionsForSession(sessionName, courseId);
        for(FeedbackQuestionAttributes question : questions){
            boolean needRepairGiverSection = isGiverContainingSection(question.giverType);
            boolean needRepairRecipientSection = isRecipientContaningSection(question.giverType, question.recipientType);
            if(needRepairGiverSection || needRepairRecipientSection){
                repairResponsesForQuestion(question, needRepairGiverSection, needRepairRecipientSection);
            }
        }
    }
    
    private void repairResponsesForQuestion(FeedbackQuestionAttributes question, boolean needRepairGiverSection, boolean needRepairRecipientSection) throws InvalidParametersException, EntityAlreadyExistsException, EntityDoesNotExistException{
        List<FeedbackResponseAttributes> responses = logic.getFeedbackResponsesForQuestion(question.getId());
        for(FeedbackResponseAttributes response : responses){
            boolean needUpdateResponse = false;
            String originalGiverSection = "";
            String originalRecipientSection = "";
            if(needRepairGiverSection){
                StudentAttributes student = logic.getStudentForEmail(question.courseId, response.giverEmail);
                if(!response.giverSection.equals(student.section)){
                    originalGiverSection = response.giverSection;
                    response.giverSection = student.section;
                    needUpdateResponse = true;
                }
            }
            
            if(needRepairRecipientSection){
                if(isTeamRecipient(question.recipientType)){
                    String recipientSection = logic.getStudentsForTeam(response.recipientEmail, question.courseId).get(0).section;
                    if(!recipientSection.equals(response.recipientSection)){
                        originalRecipientSection = response.recipientSection;
                        response.recipientSection = recipientSection;
                        needUpdateResponse = true;
                    }
                } else {
                    StudentAttributes student = logic.getStudentForEmail(question.courseId, response.recipientEmail);
                    if(!response.recipientSection.equals(student.section)){
                        originalRecipientSection = response.recipientSection;
                        response.recipientSection = student.section;
                        needUpdateResponse = true;
                    }
                }
            }
            
            if(needUpdateResponse){
                System.out.println("Repairing giver section:" +
                        originalGiverSection + "-->" + response.giverSection
                        + " receiver section:" +
                        originalRecipientSection + "-->" + response.recipientSection);
                logic.updateFeedbackResponse(response);
            }
        }
    }
    
    private boolean isGiverContainingSection(FeedbackParticipantType giverType){
        return giverType == FeedbackParticipantType.STUDENTS || giverType == FeedbackParticipantType.TEAMS;
    }
    
    private boolean isRecipientContaningSection(FeedbackParticipantType giverType, FeedbackParticipantType recipientType) {
        return (recipientType == FeedbackParticipantType.SELF && isGiverContainingSection(giverType))
                || recipientType == FeedbackParticipantType.STUDENTS
                || recipientType == FeedbackParticipantType.TEAMS
                || recipientType == FeedbackParticipantType.OWN_TEAM
                || recipientType == FeedbackParticipantType.OWN_TEAM_MEMBERS
                || recipientType == FeedbackParticipantType.OWN_TEAM_MEMBERS_INCLUDING_SELF;
    }
    
    private boolean isTeamRecipient(FeedbackParticipantType recipientType){
        return recipientType == FeedbackParticipantType.TEAMS || recipientType == FeedbackParticipantType.OWN_TEAM;
    }
}
