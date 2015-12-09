package teammates.common.datatransfer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import teammates.common.util.Const;
import teammates.common.util.HttpRequestHelper;
import teammates.common.util.Sanitizer;
import teammates.common.util.StringHelper;

public class FeedbackMsqResponseDetails extends FeedbackResponseDetails {
    public List<String> answers; // answers contain the "other" answer, if any
    private boolean isOther;
    private String otherFieldContent; //content of other field if "other" is selected as the answer
    
    public FeedbackMsqResponseDetails() {
        super(FeedbackQuestionType.MSQ);
        this.answers = new ArrayList<String>();
        isOther = false;
        otherFieldContent = "";
    }
    
    @Override
    public void extractResponseDetails(FeedbackQuestionType questionType,
            FeedbackQuestionDetails questionDetails, String[] answer) {
        this.answers = Arrays.asList(answer);      
    }
    

    public void extractResponseDetails(FeedbackQuestionType questionType,
                                    FeedbackQuestionDetails questionDetails, String[] answer,
                                    Map<String, String[]> requestParameters, int questionIndx,
                                    int responseIndx) {
        
        // "1" if other is selected, "0" if other is not selected, null if other is disabled by the instructor
        String isOtherOptionAnswer = HttpRequestHelper.getValueFromParamMap(
                                        requestParameters, Const.ParamsNames.FEEDBACK_QUESTION_MSQ_ISOTHEROPTIONANSWER 
                                        + "-" + questionIndx + "-" + responseIndx);
        
        if (isOtherOptionAnswer != null && isOtherOptionAnswer.equals("1")) {
            isOther = true;
            try {
                otherFieldContent = answer[answer.length - 1];
            } catch (IndexOutOfBoundsException e) {
                otherFieldContent = "";
            }
        }
        
        extractResponseDetails(questionType, questionDetails, answer); 
    }

    public boolean contains(String candidateAnswer) {
        return answers.contains(candidateAnswer);
    }

    @Override
    public String getAnswerString() {
        return StringHelper.toString(answers, ", ");
    }
    
    public List<String> getAnswerStrings() {
        return answers;
    }

    @Override
    public String getAnswerHtml(FeedbackQuestionDetails questionDetails) {
        StringBuilder htmlBuilder = new StringBuilder();
        
        if (isAnswerBlank()) {
            // display an empty string if "None of the above" was selected
            htmlBuilder.append("");
        } else {
            htmlBuilder.append("<ul class=\"selectedOptionsList\">");
            for (String answer : answers) {
                htmlBuilder.append("<li>");
                htmlBuilder.append(Sanitizer.sanitizeForHtml(answer));
                htmlBuilder.append("</li>");
            }
            htmlBuilder.append("</ul>");
        }
        
        return htmlBuilder.toString();
    }

    @Override
    public String getAnswerCsv(FeedbackQuestionDetails questionDetails) {
        FeedbackMsqQuestionDetails msqDetails = (FeedbackMsqQuestionDetails) questionDetails;
        StringBuilder csvBuilder = new StringBuilder();
        
        if (isAnswerBlank()) {
            csvBuilder.append("");
        } else {
            for(String choice : msqDetails.msqChoices) {
                csvBuilder.append(",");
                if (this.contains(choice)) {
                    csvBuilder.append(Sanitizer.sanitizeForCsv(choice));
                }
            }
        }

        return csvBuilder.toString();
    }
    
    protected boolean isAnswerBlank() {
        return answers.size() == 1 && 
               answers.get(0).equals("");
    }
    
    public Boolean isOtherOptionAnswer() {
        return isOther;
    }
    
    public String getOtherFieldContent() {
        return otherFieldContent;
    }

}
