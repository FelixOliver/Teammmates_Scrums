package teammates.ui.template;

import java.util.ArrayList;
import java.util.List;

import teammates.common.datatransfer.FeedbackResponseAttributes;
import teammates.common.datatransfer.FeedbackSessionResultsBundle;

public class FeedbackResponsePersonRow {

    private String personName;
    private List<FeedbackResponseRow> responses;

    public FeedbackResponsePersonRow(int fbIndex, int personIndex, String personName, String personType,
                                     List<FeedbackResponseAttributes> responses,
                                     FeedbackSessionResultsBundle results) {
        this.personName = personName;
        this.responses = new ArrayList<FeedbackResponseRow>();
        for (FeedbackResponseAttributes response : responses) {
            this.responses.add(new FeedbackResponseRow(fbIndex, personIndex, personType, response, results));
        }
    }

    public String getPersonName() {
        return personName;
    }

    public List<FeedbackResponseRow> getResponses() {
        return responses;
    }

}
