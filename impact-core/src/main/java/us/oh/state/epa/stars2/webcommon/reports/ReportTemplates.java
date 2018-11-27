package us.oh.state.epa.stars2.webcommon.reports;

import java.util.ArrayList;

import us.oh.state.epa.stars2.database.dbObjects.serviceCatalog.SCEmissionsReport;
import us.oh.state.epa.stars2.webcommon.DisplayUtil;

public class ReportTemplates implements java.io.Serializable {
    private boolean failed;
    private ArrayList<String> displayMsgs;
    //private SCEmissionsReport scFER;
    //private SCEmissionsReport scEIS;
    //private SCEmissionsReport scES; 
    
   private SCEmissionsReport sc;
    
    public ReportTemplates() {
    }
    
    public void setNull() {
        failed = false;
        displayMsgs = new ArrayList<String>();
        //scFER = null;
        //scEIS = null;
        //scES = null;
        sc = null;
    }
    
    public void addDisplayMsg(String s) {
        displayMsgs.add(s);
    }
    
    public void displayMsgs() {
        for(String s: displayMsgs) {
            DisplayUtil.displayError(s);
        }
    }
    
    /*
    public void setScEIS(ReportTemplates r) {
        this.scEIS = r.getScEIS();
    }
    
    public void setScES(ReportTemplates r) {
        this.scES = r.getScES();
    }
    
    public void setScFER(ReportTemplates r) {
        this.scFER = r.getScFER();
    }

    public SCEmissionsReport getScEIS() {
        return scEIS;
    }

    public void setScEIS(SCEmissionsReport scEIS) {
        this.scEIS = scEIS;
    }

    public SCEmissionsReport getScES() {
        return scES;
    }

    public void setScES(SCEmissionsReport scES) {
        this.scES = scES;
    }

    public SCEmissionsReport getScFER() {
        return scFER;
    }

    public void setScFER(SCEmissionsReport scFER) {
        this.scFER = scFER;
    }
    */
    
    public void setSc(ReportTemplates r) {
        this.sc = r.getSc();
    }
    
    
    public SCEmissionsReport getSc() {
        return sc;
    }

    public void setSc(SCEmissionsReport sc) {
        this.sc = sc;
    }

    public boolean isFailed() {
        return failed;
    }

    public void setFailed(boolean failed) {
        this.failed = failed;
    }

    public final ArrayList<String> getDisplayMsgs() {
        return displayMsgs;
    }

    public final void setDisplayMsgs(ArrayList<String> displayMsgs) {
        this.displayMsgs = displayMsgs;
    }
}
