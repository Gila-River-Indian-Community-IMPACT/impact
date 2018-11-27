package us.oh.state.epa.stars2.app.ceta;

import java.rmi.RemoteException;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;

import us.oh.state.epa.stars2.bo.GDFService;
import us.oh.state.epa.stars2.database.dbObjects.ceta.GDF;
import us.oh.state.epa.stars2.webcommon.AppBase;
import us.oh.state.epa.stars2.webcommon.DisplayUtil;
import us.oh.state.epa.stars2.webcommon.FacesUtil;

@SuppressWarnings("serial")
public class GDFSearch extends AppBase {
	private String doLaaCd;
	private List<GDF> gdfs;
	private boolean hasSearchResults;

	private GDFService gDFService;

    public GDFService getGDFService() {
		return gDFService;
	}

	public void setGDFService(GDFService gDFService) {
		this.gDFService = gDFService;
	}

	public String initGDF() {
	    Calendar cal = Calendar.getInstance();
	    int y = cal.get(Calendar.YEAR);
	    int mon = cal.get(Calendar.MONTH) + 1;
	    if(mon == 1) {
	        mon = 12;
	        y = y -1;
	    } else {
	        mon = mon - 1;
	    }
        GDFDetail gdfD = (GDFDetail)FacesUtil.getManagedBean("gdfDetail");
        gdfD.setYear(Integer.toString(y));
	    String m = Integer.toString(mon);
	    if(m.length() == 1) m = "0" + m;
        gdfD.setMonth(m);
        return "gdfs.search";
	}

	public final String reset() {
    	doLaaCd = null;
    	hasSearchResults = false;
    	return SUCCESS;
    }
	
	/**
	 * Invoked from GDF search screen.
	 * @return
	 */
	public final String search() {
		try {
            hasSearchResults = false;
            gdfs = getGDFService().retrieveGDFs(doLaaCd);
            DisplayUtil.displayHitLimit(gdfs.size());
            if (gdfs.size() == 0) {
            	DisplayUtil.displayInfo("There are no GDFs for the selected DO/LAA.");
            } else {
            	// want results sorted in reverse order
            	Collections.sort(gdfs);
            	Collections.reverse(gdfs);
            }
            hasSearchResults = true;
		} catch (RemoteException e) {
			handleException(e);
		}
		return "gdfs.search";
	}

    public final String refresh() {
        String ret = "gdfs.search";
        search();
        return ret;
    }

	public final String getDoLaaCd() {
		return doLaaCd;
	}

	public final List<GDF> getGdfs() {
		return gdfs;
	}

	public final void setDoLaaCd(String doLaaCd) {
		this.doLaaCd = doLaaCd;
	}

	public final boolean isHasSearchResults() {
		return hasSearchResults;
	}
}
