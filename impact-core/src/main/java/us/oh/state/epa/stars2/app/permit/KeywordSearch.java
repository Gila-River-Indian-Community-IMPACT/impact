package us.oh.state.epa.stars2.app.permit;

import java.rmi.RemoteException;

import us.oh.state.epa.stars2.bo.InfrastructureService;
import us.oh.state.epa.stars2.database.dbObjects.document.Document;
import us.oh.state.epa.stars2.def.SystemPropertyDef;
import us.oh.state.epa.stars2.framework.config.ConfigManager;
import us.oh.state.epa.stars2.framework.config.ConfigManagerFactory;
import us.oh.state.epa.stars2.framework.config.Node;
import us.oh.state.epa.stars2.webcommon.AppBase;
import us.oh.state.epa.stars2.webcommon.DisplayUtil;

/**
 * @author Pyeh
 * 
 */
@SuppressWarnings("serial")
public class KeywordSearch extends AppBase {
    private String searchString;
    private Integer hitLimit;
    private Document[] docs;
    
    private boolean hasSearchResults;
	private InfrastructureService infrastructureService;

	public InfrastructureService getInfrastructureService() {
		return infrastructureService;
	}

	public void setInfrastructureService(InfrastructureService infrastructureService) {
		this.infrastructureService = infrastructureService;
	}

    public KeywordSearch() {
        super();
        reset();
        cacheViewIDs.add("/permits/keywordSearch.jsp");
    }

    /**
     * @return
     */
    public final boolean getHasSearchResults() {
        return hasSearchResults;
    }

    /*
     * Actions -------------------------------------------------------
     */
    /**
     * @return
     */
    public final String search() {
        String ret = "failure";
        if(searchString == null || searchString.trim().length() == 0) {
            DisplayUtil.displayError("No Search String provided");
            return ret;
        }
        if(hitLimit == null || hitLimit == 0) {
            DisplayUtil.displayError("No Maximum Number of Documents Returned provided");
            return ret;
        }

        try {
            docs = null;
            hasSearchResults = false;
            docs = getInfrastructureService().keyWordSearch(searchString, hitLimit);
            if (docs.length == 0) {
                DisplayUtil.displayNoRecords();
            }
            if (docs.length == hitLimit) {
                DisplayUtil.displayInfo("Search return limited.");
            }
            hasSearchResults = true;
            ret = "keywordSearch";
        } catch (RemoteException ex) {
            handleException(ex);
            DisplayUtil.displayError("search failed");
        }
        return ret;
    }

    /**
     * @return
     */
    public final String reset() {
        searchString = null;
        docs = null;
        hitLimit = SystemPropertyDef.getSystemPropertyValueAsInteger("KeywordSearchHitLimit", 100);
        hasSearchResults = false;
        return SUCCESS;
    }

    public void restoreCache() {
        // submitSearch();
    }

    public void clearCache() {
        if (docs != null) {
            docs = null;
        }

        hasSearchResults = false;
    }

    /**
     * @return the docs
     */
    public final Document[] getDocs() {
        return docs;
    }

    /**
     * @param docs the docs to set
     */
    public final void setDocs(Document[] docs) {
        this.docs = docs;
    }

    /**
     * @return the searchString
     */
    public final String getSearchString() {
        return searchString;
    }

    /**
     * @param searchString the searchString to set
     */
    public final void setSearchString(String searchString) {
        this.searchString = searchString;
    }

    /**
     * @param hasSearchResults the hasSearchResults to set
     */
    public final void setHasSearchResults(boolean hasSearchResults) {
        this.hasSearchResults = hasSearchResults;
    }

    public Integer getHitLimit() {
        return hitLimit;
    }

    public void setHitLimit(Integer hitLimit) {
        this.hitLimit = hitLimit;
    }

}
