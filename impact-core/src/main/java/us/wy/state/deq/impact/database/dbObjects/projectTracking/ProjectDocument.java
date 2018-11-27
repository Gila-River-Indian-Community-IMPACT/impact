package us.wy.state.deq.impact.database.dbObjects.projectTracking;

import java.io.File;

import us.oh.state.epa.stars2.database.dbObjects.document.Document;

/**
 * This is an extension of the Document class that exists simply to provide
 * a means for defining the path attribute for stack test documents by allowing
 * an stack test id to be set and using this id in the getDirName method
 * used by Document.getPath() to determine the path of the document.
 *
 */

@SuppressWarnings("serial")
public class ProjectDocument extends Document {
    private Integer projectId;
    private String overloadFileName; //TODO ??

    public ProjectDocument() {
        super();
    }

    /**
     * Copy Constructor
     * 
     * @param stackTestDocument
     *            a <code>StackTestDocument</code> object
     */
    public ProjectDocument(ProjectDocument stackTestDocument) {
        super(stackTestDocument);

        if (stackTestDocument != null) {
            this.projectId = stackTestDocument.projectId;
        }
    }
    
    /**
     * Create StackTestDocument from a Document.
     * @param doc
     */
    public ProjectDocument(Document doc) {
        super(doc);
    }
    
    public final String getBasePath() {

        String ret = File.separator + "StackTest" + File.separator ;
        if (projectId != null) {
            ret += projectId + File.separator;
        }
        if (getOverloadFileName() == null) {
            ret += getFileName();
        } else {
            ret += getOverloadFileName();
        }
        return ret;
    }

    public final Integer getStackTestId() {
        return projectId;
    }

    public final void setStackTestId(Integer stackTestId) {
        this.projectId = stackTestId;
    }

    public final String getOverloadFileName() {
        return overloadFileName;
    }

    public final void setOverloadFileName(String overloadFileName) {
        this.overloadFileName = overloadFileName;
    }
}
