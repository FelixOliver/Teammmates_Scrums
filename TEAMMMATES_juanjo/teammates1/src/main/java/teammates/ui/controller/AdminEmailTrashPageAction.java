package teammates.ui.controller;

import teammates.common.util.Const;
import teammates.logic.api.GateKeeper;

public class AdminEmailTrashPageAction extends Action {

    @Override
    protected ActionResult execute() {
        new GateKeeper().verifyAdminPrivileges(account);
        AdminEmailTrashPageData data = new AdminEmailTrashPageData(account);      
        
        data.adminTrashEmailList = logic.getAdminEmailsInTrashBin(); 
        statusToAdmin = "adminEmailTrashPage Page Load";
        data.init();
        
        return createShowPageResult(Const.ViewURIs.ADMIN_EMAIL, data);     
    }

}
