package us.oh.state.epa.stars2.database.dbObjects.document;

import java.io.File;

@SuppressWarnings("serial")
public class CorrespondenceDocument extends Document {

    public CorrespondenceDocument() {
        super();
    }

    public CorrespondenceDocument(Document doc) {
        super(doc);
    }

    public final String getBasePath() {

        String ret = File.separator + "Correspondence" 
            + File.separator + getFileName();

        return ret;
    }

}
