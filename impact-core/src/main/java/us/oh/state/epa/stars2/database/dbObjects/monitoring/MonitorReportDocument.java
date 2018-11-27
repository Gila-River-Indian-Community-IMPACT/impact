package us.oh.state.epa.stars2.database.dbObjects.monitoring;

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
public class MonitorReportDocument extends Document {
    private Integer monitorReportId;
    private String overloadFileName;

    public MonitorReportDocument() {
        super();
    }

    /**
     * Copy Constructor
     * 
     * @param stackTestDocument
     *            a <code>StackTestDocument</code> object
     */
    public MonitorReportDocument(MonitorReportDocument monitorReportDocument) {
        super(monitorReportDocument);

        if (monitorReportDocument != null) {
            this.monitorReportId = monitorReportDocument.monitorReportId;
        }
    }
    
    /**
     * Create StackTestDocument from a Document.
     * @param doc
     */
    public MonitorReportDocument(Document doc) {
        super(doc);
    }
    
    public final String getBasePath() {

        String ret = File.separator + "MonitorReport" + File.separator ;
        if (monitorReportId != null) {
            ret += monitorReportId + File.separator;
        }
        if (getOverloadFileName() == null) {
            ret += getFileName();
        } else {
            ret += getOverloadFileName();
        }
        return ret;
    }

    public final Integer getMonitorReportId() {
        return monitorReportId;
    }

    public final void setMonitorReportId(Integer monitorReportId) {
        this.monitorReportId = monitorReportId;
    }

    public final String getOverloadFileName() {
        return overloadFileName;
    }

    public final void setOverloadFileName(String overloadFileName) {
        this.overloadFileName = overloadFileName;
    }
}
