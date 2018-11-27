package us.oh.state.epa.stars2.database.dbObjects.document;

import java.io.File;

public class TmpDocument extends Document {
    private String tmpFileName;
    
    public TmpDocument() {
        super();
    }

    public TmpDocument(TmpDocument old) {
        super(old);
    }
    

    public String getDirName() {

        String baseDir = File.separator + "tmp";

        if (getFacilityID() != null && getFacilityID().length() > 0) {
            baseDir += File.separator + "Facilities" 
                + File.separator + getFacilityID();
        } 

        return baseDir;
    }
    
    public String getBasePath() {
        String ret = "";
        if (tmpFileName != null && tmpFileName.trim().length() > 0) {
            ret = File.separator + tmpFileName.trim();
        }
        return ret;
    }

    public final String getTmpFileName() {
        return tmpFileName;
    }

    public final void setTmpFileName(String tmpFileName) {
        this.tmpFileName = tmpFileName;
    }
}
