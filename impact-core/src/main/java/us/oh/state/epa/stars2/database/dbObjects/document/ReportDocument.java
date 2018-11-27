package us.oh.state.epa.stars2.database.dbObjects.document;

import java.io.File;

@SuppressWarnings("serial")
public class ReportDocument extends Document {

    public ReportDocument() {
        super();
    }

    public ReportDocument(Document doc) {
        super(doc);
    }

    public final String getBasePath() {

        String ret = File.separator + "Report" 
            + File.separator + getFileName();

        return ret;
    }

}
