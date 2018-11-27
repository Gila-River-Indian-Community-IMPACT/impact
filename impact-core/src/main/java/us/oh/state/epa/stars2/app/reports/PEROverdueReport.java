package us.oh.state.epa.stars2.app.reports;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.faces.context.FacesContext;

import us.oh.state.epa.stars2.app.facility.FacilityProfile;
import us.oh.state.epa.stars2.bo.ReportService;
import us.oh.state.epa.stars2.database.dbObjects.report.PEROverdueDetails;
import us.oh.state.epa.stars2.webcommon.AppBase;
import us.oh.state.epa.stars2.webcommon.DisplayUtil;
import us.oh.state.epa.stars2.webcommon.FacesUtil;
import us.oh.state.epa.stars2.webcommon.ServiceFactory;

@SuppressWarnings("serial")
public class PEROverdueReport extends AppBase {
	private String countyCd;
	private String doLaaCd;
	private boolean hasResults;
	private List<PEROverdueDetails> details;
	
    private Thread searchThread;
    DisplayUtil displayUtilBean;
    private FacilityProfile fProf;
    private boolean searchStarted;
	private boolean showProgressBar;
	private boolean renderSubmit = true;
	
	public final String submit() {
		if (!searchStarted && getSearchThread() == null) {
            String refresh = "<META HTTP-EQUIV=\"refresh\" CONTENT=\"5\" />"
                + "<META HTTP-EQUIV=\"Pragma\" CONTENT=\"no-cache\" />"
                + "<META HTTP-EQUIV=\"Expires\" CONTENT=\"-1\" />"
                + "<META HTTP-EQUIV=\"Cache-Control\" "
                + "CONTENT=\"no-cache, must-revalidate, pre-check=0, post-check=0, max-age=0\" />";
            fProf = (FacilityProfile) FacesUtil.getManagedBean("facilityProfile");
            fProf.setRefreshStr(refresh);
			initSearchThread();
			getSearchThread().setDaemon(true);
			setValue(0);
			setMaximum(1000);
			showProgressBar = true;
			renderSubmit = false;
			getSearchThread().start();
            if (!hasResults) {
                DisplayUtil.displayInfo("Processing report. This may take several moments. "
                                        + "You may cancel the operation by pressing the \"Reset\" button.");
            }
            else {
                fProf.setRefreshStr(" ");
            }
		} else {
			DisplayUtil.displayInfo("Still processing. Please wait."
					+ "You may cancel the operation by pressing the \"Reset\" button.");

		}
		FacesContext facesContext = FacesContext.getCurrentInstance();
		displayUtilBean = DisplayUtil.getSessionInstance(facesContext, true);
		return SUCCESS;
	}
	
	private void initSearchThread() {
		searchThread = new Thread("PER Overdue Report Thread") {
            
            public void run() {
                try {
        			searchStarted = true;
        			ReportService reportBO = ServiceFactory.getInstance().getReportService();
                    details = null; 
        			if (details != null) {
        				Collections.sort(details,
        						new Comparator<PEROverdueDetails>() {
        					public int compare(PEROverdueDetails a, PEROverdueDetails b) {
        						int retVal = 0;
        						if (a.getPerDueDate() != null) {
        							if (b.getPerDueDate() != null) {
        								retVal = -a.getPerDueDate().compareTo(b.getPerDueDate());
        								if (retVal == 0) {
        									if (a.getFacilityId() != null) {
        										if (b.getFacilityId() != null) {
        											retVal = a.getFacilityId().compareTo(b.getFacilityId());
        											if (retVal == 0) {
        												if (a.getEpaEmuId() != null) {
        													if (b.getEpaEmuId() != null) {
        														retVal = a.getEpaEmuId().compareTo(b.getEpaEmuId());
        													} else {
        														retVal = -1;
        													}
        												} else if (b.getEpaEmuId() != null) {
        													retVal = 1;
        												}
        											}
        										} else {
        											retVal = -1;
        										} 
        									} else if (b.getFacilityId() != null) {
        										retVal = 1;
        									}
        								}
        							} else {
        								retVal = 1;
        							}
        						} else if (b.getPerDueDate() != null) {
        							retVal = -1;
        						}
        	
        						return retVal;
        					}
        				}
        				);
        	
        				setValue(1000);
        				hasResults = true;
        				showProgressBar = false;
                        fProf.setRefreshStr(" ");
        			}
                    searchThread = null;
                } catch (Exception e) {
                    String error = "A system error has occurred. Please contact System Administrator.";
                    logger.error(error, e);
                    displayUtilBean.addMessageToQueue(error, DisplayUtil.ERROR, null);
                    showProgressBar = false;
                    searchStarted = false;
                    hasResults = false;;
                    fProf.setRefreshStr(" ");
                }
            }
        };  // semicolon is actually needed!
	}
	
	private Thread getSearchThread() {
		return searchThread;
	}
	
	public final List<PEROverdueDetails> getDetails() {
		return details;
	}

	public final String reset() {
		countyCd = null;
		doLaaCd = null;
		details = null;
		hasResults = false;
		searchStarted = false;
		searchThread = null;
		showProgressBar = false;
		fProf.setRefreshStr(" ");
		renderSubmit = true;
		return SUCCESS;
	}
	
	public final boolean isHasResults() {
		return hasResults;
	}
	public final void setHasResults(boolean hasResults) {
		this.hasResults = hasResults;
	}
	public final String getCountyCd() {
		return countyCd;
	}
	public final void setCountyCd(String countyCd) {
		this.countyCd = countyCd;
	}
	public final String getDoLaaCd() {
		return doLaaCd;
	}
	public final void setDoLaaCd(String doLaaCd) {
		this.doLaaCd = doLaaCd;
	}

	public final boolean isShowProgressBar() {
		return showProgressBar;
	}

	public final void setShowProgressBar(boolean showProgressBar) {
		this.showProgressBar = showProgressBar;
	}
	
    public synchronized long getValue() {
        long value = super.getValue() + 5;
        setValue(value);
        return value;
    }

	public boolean isRenderSubmit() {
		return renderSubmit;
	}
}
