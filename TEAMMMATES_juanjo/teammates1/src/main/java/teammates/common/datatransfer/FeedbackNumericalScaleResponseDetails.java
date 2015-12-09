package teammates.common.datatransfer;

import teammates.common.util.StringHelper;
import teammates.common.util.Utils;

public class FeedbackNumericalScaleResponseDetails extends
        FeedbackResponseDetails {
    private double answer;
    
    public FeedbackNumericalScaleResponseDetails() {
        super(FeedbackQuestionType.NUMSCALE);
    }
    
    @Override
    public void extractResponseDetails(FeedbackQuestionType questionType,
            FeedbackQuestionDetails questionDetails, String[] answer) {
        try {
            double numscaleAnswer = Double.parseDouble(answer[0]);
            setAnswer(numscaleAnswer);
        } catch (NumberFormatException e) {
            Utils.getLogger().severe("Failed to parse numscale answer to double - " + answer[0]);
            throw e;
        }
    }

    /**
     * @return answer in double form
     */
    public double getAnswer() {
        return answer;
    }

    @Override
    public String getAnswerString() {
        return StringHelper.toDecimalFormatString(answer);
    }

    @Override
    public String getAnswerHtml(FeedbackQuestionDetails questionDetails) {
        return getAnswerString();
    }

    @Override
    public String getAnswerCsv(FeedbackQuestionDetails questionDetails) {
        return getAnswerString();
    }

    private void setAnswer(double answer) {
        this.answer = answer;
    }

}
