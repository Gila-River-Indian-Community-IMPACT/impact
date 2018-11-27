package us.oh.state.epa.stars2.webcommon.document;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;

import us.oh.state.epa.stars2.bo.DocumentService;
import us.oh.state.epa.stars2.database.dbObjects.document.Document;
import us.oh.state.epa.stars2.webcommon.AppBase;
import us.oh.state.epa.stars2.webcommon.DisplayUtil;

/**
 * <DL>
 * <DT><B>Copyright:</B></DT>
 * <DD>Copyright 2006 Mentorgen, LLC</DD>
 * <DT><B>Company:</B></DT>
 * <DD>Mentorgen, LLC</DD>
 * </DL>
 *
 * @author cmeier
 *
 */
public class DocumentSearch extends AppBase {
    private static int maxNumberOfHits = 10; // TODO maxNumberOfHits
    private String fullTextQuery;
    private List<Document> documents;
    private boolean hasSearchResults;
    
    private DocumentService documentService;
    
    public DocumentService getDocumentService() {
		return documentService;
	}

	public void setDocumentService(DocumentService documentService) {
		this.documentService = documentService;
	}


    public DocumentSearch() {
        super();
    }
    
    public final List<Document> getDocuments() {
        return documents;
    }

    public final String getFullTextQuery() {
        return fullTextQuery;
    }

    public final void setFullTextQuery(String fullTextQuery) {
        this.fullTextQuery = fullTextQuery;
    }

    public final boolean getHasSearchResults() {
        return hasSearchResults;
    }

    /*
     * Actions
     */
    public final String search() {
        try {
            Document[] tempDocuments = getDocumentService().searchDocumentsFullText(
                    fullTextQuery, maxNumberOfHits);

            documents = new ArrayList<Document>();

            for (Document temp : tempDocuments) {
                documents.add(temp);
            }

            hasSearchResults = true;
        } catch (RemoteException re) {
            DisplayUtil.displayError("cannot search documents");
            logger.error(re.getMessage(), re);
        }
        return "documentSearch";
    }

    public final void reset() {
        fullTextQuery = null;
        hasSearchResults = false;
        documents = null;
    }

}
