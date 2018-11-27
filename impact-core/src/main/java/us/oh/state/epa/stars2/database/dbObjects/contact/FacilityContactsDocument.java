package us.oh.state.epa.stars2.database.dbObjects.contact;

import java.io.File;

import us.oh.state.epa.stars2.database.dbObjects.document.Document;

/**
 * This is an extension of the Document class that exists simply to provide
 * a means for defining the path attribute for facility inventory documents by allowing
 * an application id to be set and using this id in the getDirName method
 * used by Document.getPath() to determine the path of the document.
 *
 */

public class FacilityContactsDocument extends Document {
    private String overloadFileName = null;
    private String userName;

    public FacilityContactsDocument() {
        super();
    }
    
    public final String getBasePath() {

        String ret = File.separator + "tmp" + File.separator;
        if (getOverloadFileName() == null) {
            ret += "FacProfileDoc_" + userName + ".pdf";
        } else {
            ret += getOverloadFileName();
        }
        return ret;
    }

    public final String getOverloadFileName() {
        return overloadFileName;
    }

    public final void setOverloadFileName(String overloadFileName) {
        this.overloadFileName = overloadFileName;
    }

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}
}