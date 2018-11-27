package us.oh.state.epa.stars2.app.ceta;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;

import javax.faces.event.ActionEvent;

import us.oh.state.epa.stars2.bo.GDFService;
import us.oh.state.epa.stars2.database.dbObjects.ValidationMessage;
import us.oh.state.epa.stars2.database.dbObjects.ValidationMessage.Severity;
import us.oh.state.epa.stars2.database.dbObjects.ceta.GDF;
import us.oh.state.epa.stars2.webcommon.FacesUtil;
import us.oh.state.epa.stars2.webcommon.ValidationBase;

@SuppressWarnings("serial")
public class GDFDetail extends ValidationBase {
	private String doLaaCd;
	private Integer gdfId;
	private GDF gdf;
	private List<GDF> gdfsForDoLaa;
    private String year;
    private String month;
    
    public boolean editMode = false;  // true means can make changes.
    
    private GDFService gDFService;

    public GDFService getGDFService() {
		return gDFService;
	}

	public void setGDFService(GDFService gDFService) {
		this.gDFService = gDFService;
	}

	public final void reset() {
		gdf = null;
	}
	
	public final void setGdfId(Integer gdfId) {
		this.gdfId = gdfId;
	}
	
	public final Integer getGdfId() {
		return gdfId;
	}
	
    public final String getDoLaaCd() {
		return doLaaCd;
	}

	public final void setDoLaaCd(String doLaaCd) {
		this.doLaaCd = doLaaCd;
	}
	
	public final GDF getGdf() {
		return this.gdf;
	}	
	
	public final String startNewGDF() {
		gdfId = null;
		gdf = new GDF();
		gdf.setDoLaaCd(doLaaCd);
        gdf.setYear(year);
        gdf.setMonth(month);
        editMode = true;
		return "dialog:gdfDetail";
	}
	
	public final String startEditGDF() {
		String ret = "dialog:gdfDetail";
		try {
			if (doLaaCd != null) {
				this.gdfsForDoLaa = getGDFService().retrieveGDFs(doLaaCd);
				for (GDF match : gdfsForDoLaa) {
					if (match.getGdfId().equals(gdfId)) {
						gdf = match;
					}
				}
			}
			if (gdf == null) {
				throw new RemoteException("No gdf found for doLaa: " + doLaaCd +
						" with gdfId: " + gdfId);
			}
		} catch (RemoteException e) {
			ret = null;
			handleException(e);
		}
		return ret;
	}
	
    public final void enterEditGDF(ActionEvent actionEvent) {
        editMode = true;
    }
	public final void applyEditGDF(ActionEvent actionEvent) {
		try {
			if (gdf != null) {
	            List<ValidationMessage> validationMessages = new ArrayList<ValidationMessage>();
				if (!gdf.isValid()) {
					for (ValidationMessage vm : gdf.getValidationMessages().values()) {
						validationMessages.add(vm);
					}
				}
				if (gdf.getGdfId() == null) {
					GDF match = getGDFService().retrieveGDF(doLaaCd, gdf.getYear(), gdf.getMonth());
					if (match != null) {
						validationMessages.add(new ValidationMessage("Year", 
								"You may not create a new gdf for a year/month for which a gdf already exists",
								Severity.ERROR, "year"));
					}
				}
				if (validationMessages.size() == 0) {
					if (gdf.getGdfId() == null) {
						getGDFService().createGDF(gdf);
                        editMode = false;
                        year = gdf.getYear();
                        month = gdf.getMonth();
                        // up the date by one month
                        int y = Integer.parseInt(year);
                        int m = Integer.parseInt(month);
                        m++;
                        if(m == 13) {
                            m = 1;
                            y++;
                        }
                        year = Integer.toString(y);
                        month = Integer.toString(m);
                        if(month.length() == 1) month = "0" + month;
					} else {
						getGDFService().modifyGDF(gdf);
                        editMode = false;
					}
					GDFSearch cs = (GDFSearch)FacesUtil.getManagedBean("gdfSearch");
					cs.search();
					FacesUtil.returnFromDialogAndRefresh();
				} else {
	                displayValidationMessages("", validationMessages.toArray(new ValidationMessage[0]));
				}
			}
		} catch (RemoteException e) {
			handleException(e);
		}
	}
	
	public final void applyRemoveGDF(ActionEvent actionEvent) {
		try {
			if (gdf != null && gdf.getGdfId() != null) {
				getGDFService().removeGDF(gdf);
				gdf = null;
				gdfId = null;
				GDFSearch cs = (GDFSearch)FacesUtil.getManagedBean("gdfSearch");
				cs.search();
			}
			FacesUtil.returnFromDialogAndRefresh();
		} catch (RemoteException e) {
			handleException(e);
		}
	}	
	
	public final void cancelEditGDF(ActionEvent actionEvent) {
		// clear out gdf data
		gdf = null;
		gdfId = null;
        editMode = false;
		FacesUtil.returnFromDialogAndRefresh();
	}

    public String getMonth() {
        return month;
    }

    public void setMonth(String month) {
        this.month = month;
    }

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public boolean isEditMode() {
        return editMode;
    }
}
