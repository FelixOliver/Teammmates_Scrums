package teammates.ui.controller;

import java.util.ArrayList;
import java.util.List;

import teammates.common.datatransfer.AccountAttributes;
import teammates.common.datatransfer.FeedbackSessionResultsBundle;
import teammates.common.datatransfer.StudentAttributes;
import teammates.ui.template.FeedbackResultsTable;

public class InstructorStudentRecordsAjaxPageData extends PageData {

    private List<FeedbackResultsTable> resultsTables;

    public InstructorStudentRecordsAjaxPageData(AccountAttributes account, StudentAttributes student,
                                                List<FeedbackSessionResultsBundle> results) {
        super(account, student);
        this.resultsTables = new ArrayList<FeedbackResultsTable>();
        for (int i = 0; i < results.size(); i++) {
            FeedbackSessionResultsBundle result = results.get(i);
            String studentName = result.appendTeamNameToName(student.name, student.team);
            this.resultsTables.add(new FeedbackResultsTable(i, studentName, result));
        }
    }

    public List<FeedbackResultsTable> getResultsTables() {
        return resultsTables;
    }

}
