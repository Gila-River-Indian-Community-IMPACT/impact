package us.oh.state.epa.stars2.database.dbObjects.application;

import java.io.File;

import us.oh.state.epa.stars2.database.dbObjects.document.Document;

/**
 * This is an extension of the Document class that exists simply to provide
 * a means for defining the path attribute for application documents by allowing
 * an application id to be set and using this id in the getDirName method
 * used by Document.getPath() to determine the path of the document.
 *
 */

@SuppressWarnings("serial")
public class ApplicationDocument extends Document {
    private Integer applicationId;
    private String overloadFileName;

    public ApplicationDocument() {
        super();
    }

    /**
     * Copy Constructor
     * 
     * @param applicationDocument
     *            a <code>ApplicationDocument</code> object
     */
    public ApplicationDocument(ApplicationDocument applicationDocument) {
        super(applicationDocument);

        if (applicationDocument != null) {
            this.applicationId = applicationDocument.applicationId;
        }
    }
    
    /**
     * Create ApplicationDocument from a Document.
     * @param doc
     */
    public ApplicationDocument(Document doc) {
        super(doc);
    }
    
    public final String getBasePath() {

        String ret = File.separator + "Applications" + File.separator ;
        if (applicationId != null) {
            ret += applicationId + File.separator;
        }
        if (getOverloadFileName() == null) {
            ret += getFileName();
        } else {
            ret += getOverloadFileName();
        }
        return ret;
    }

    public final Integer getApplicationId() {
        return applicationId;
    }

    public final void setApplicationId(Integer applicationId) {
        this.applicationId = applicationId;
    }

    public final String getOverloadFileName() {
        return overloadFileName;
    }

    public final void setOverloadFileName(String overloadFileName) {
        this.overloadFileName = overloadFileName;
    }
}
