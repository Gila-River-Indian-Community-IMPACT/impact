package us.oh.state.epa.stars2.database.dbObjects.ceta;

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
public class StackTestDocument extends Document {
    private Integer stackTestId;
    private String overloadFileName;

    public StackTestDocument() {
        super();
    }

    /**
     * Copy Constructor
     * 
     * @param stackTestDocument
     *            a <code>StackTestDocument</code> object
     */
    public StackTestDocument(StackTestDocument stackTestDocument) {
        super(stackTestDocument);

        if (stackTestDocument != null) {
            this.stackTestId = stackTestDocument.stackTestId;
        }
    }
    
    /**
     * Create StackTestDocument from a Document.
     * @param doc
     */
    public StackTestDocument(Document doc) {
        super(doc);
    }
    
    public final String getBasePath() {

        String ret = File.separator + "StackTest" + File.separator ;
        if (stackTestId != null) {
            ret += stackTestId + File.separator;
        }
        if (getOverloadFileName() == null) {
            ret += getFileName();
        } else {
            ret += getOverloadFileName();
        }
        return ret;
    }

    public final Integer getStackTestId() {
        return stackTestId;
    }

    public final void setStackTestId(Integer stackTestId) {
        this.stackTestId = stackTestId;
    }

    public final String getOverloadFileName() {
        return overloadFileName;
    }

    public final void setOverloadFileName(String overloadFileName) {
        this.overloadFileName = overloadFileName;
    }
}
