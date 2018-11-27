package us.oh.state.epa.stars2.app.reports;

import java.rmi.RemoteException;
import java.sql.Date;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;

import us.oh.state.epa.aport.admin.domain.User;
import us.oh.state.epa.stars2.app.facility.FacilityProfile;
import us.oh.state.epa.stars2.bo.FacilityService;
import us.oh.state.epa.stars2.bo.InfrastructureService;
import us.oh.state.epa.stars2.database.dbObjects.facility.Facility;
import us.oh.state.epa.stars2.database.dbObjects.invoice.InvoiceList;
import us.oh.state.epa.stars2.def.DOLAA;
import us.oh.state.epa.stars2.def.RevenueTypeDef;
import us.oh.state.epa.stars2.webcommon.AppBase;
import us.oh.state.epa.stars2.webcommon.DisplayUtil;
import us.oh.state.epa.stars2.webcommon.FacesUtil;
import us.oh.state.epa.stars2.webcommon.bean.InfrastructureDefs;

@SuppressWarnings("serial")
public class InvoiceReport extends AppBase {

	private Date invoiceDate;
	private boolean hasSearchResults;
	private InvoiceList[] invoiceReports;
	private Map<String, List<InvoiceList>> dolaaCollections;
	private List<Map.Entry<String, List<InvoiceList>>> entrySet;
	private double grandTotal;
	private String revenueType;
	private InvoiceList searchObj;
	private Thread searchThread;
	private boolean searchStarted;
	private boolean searchCompleted;
	private boolean browserCompleted;
    private DisplayUtil displayUtilBean;
    private FacilityProfile fProf;
    private boolean showProgressBar;
    
	private FacilityService facilityService;
	private InfrastructureService infrastructureService;

	public InfrastructureService getInfrastructureService() {
		return infrastructureService;
	}

	public void setInfrastructureService(InfrastructureService infrastructureService) {
		this.infrastructureService = infrastructureService;
	}

	public FacilityService getFacilityService() {
		return facilityService;
	}

	public void setFacilityService(FacilityService facilityService) {
		this.facilityService = facilityService;
	}

	public InvoiceReport() {
		super();
		reset();
		cacheViewIDs.add("/mgmt/invoiceReport.jsp");
	}

	public final String submit() {
		if (invoiceDate == null && revenueType == null) {
			DisplayUtil.displayError("Enter Search criteria");
			return FAIL;
		}

		dolaaCollections = new HashMap<String, List<InvoiceList>>();
		try {
			final User user = InfrastructureDefs.getPortalUser();

			if (user == null) {
				DisplayUtil.displayError("User is null");
				return FAIL;
			}

			searchObj = new InvoiceList();
			searchObj.setBeginDt(invoiceDate);
			searchObj.setEndDt(invoiceDate);
			searchObj.setRevenueTypeCd(revenueType);
			searchObj.setUnlimitedResults(unlimitedResults());

			if (!isSeachStarted() && getSearchThread() == null) {
				FacesContext facesContext = FacesContext.getCurrentInstance();
				displayUtilBean = DisplayUtil.getSessionInstance(facesContext, true);
				hasSearchResults = false;

				String refresh = "<META HTTP-EQUIV=\"refresh\" CONTENT=\"5\" />"
				+ "<META HTTP-EQUIV=\"Pragma\" CONTENT=\"no-cache\" />"
				+ "<META HTTP-EQUIV=\"Expires\" CONTENT=\"-1\" />"
				+ "<META HTTP-EQUIV=\"Cache-Control\" "
				+ "CONTENT=\"no-cache, must-revalidate, pre-check=0, post-check=0, max-age=0\" />";
				fProf = (FacilityProfile) FacesUtil.getManagedBean("facilityProfile");
				fProf.setRefreshStr(refresh);

				setSearchStarted(true);
				
				setSearchThread(new Thread("Invoice Report Search Thread") {

					public void run() {
						try {
							searchInvoices(user);
							fProf.setRefreshStr(" ");
					        setSearchStarted(false);
					        setSearchCompleted(true);
							setBrowserCompleted(true);
						}
						catch (Exception e) {
							String error = "Search failed: System Error. ";
							if (e.getMessage() != null && e.getMessage().length() > 0) {
								error += e.getMessage();
							} else {
								error += e.getClass().getName();
							}
							setErrorMessage(error + " Please contact the System Administrator.");
							setSearchStarted(false);
							setSearchCompleted(false);
						}
					}
				});

				getSearchThread().setDaemon(true);
				setBrowserCompleted(false);

				start(searchThread);

				if (!isSearchCompleted()) {
					DisplayUtil.displayInfo("Searching invoice data. This may take several moments. "
							+ "You may cancel the operation by pressing the \"Reset\" button.");
				}
				else {
					fProf.setRefreshStr(" ");
			        setSearchStarted(false);
					setBrowserCompleted(true);
				}
			}

		} catch (RemoteException re) {
			DisplayUtil.displayError("Search failed : " + re.getMessage());
			handleException(re);
		}
		return SUCCESS;
	}
	
	public void searchInvoices(User user) throws RemoteException {
		setValue(0);
		setMaximum(1);
		if (getSearchObj().getBeginDt() != null) {
			if (user == null) {
				setErrorMessage("User Name is null.");
			}
			InvoiceList[] reports = getInfrastructureService().searchInvoices(user, getSearchObj(), true);
			if (reports == null) {
				setErrorMessage("Search results are null.");
			} else {
				setInvoiceReports(reports);
			}
		} else {// if only revenueType is set, search stars2 first and then
			// revenues to avoid MaxMessageSizeExceededException.
			setInvoiceReports(getInfrastructureService().searchInvoices(user, getSearchObj(), false));
		}

		if (getInvoiceReports() != null) {
			if (getInvoiceReports().length == 0) {
				setInfoMessage("Search returned no records.");
				setHasSearchResults(false);
			}
		} else {
			return;
		}

		setMaximum(getInvoiceReports().length);
		int processedCount = 0;
		setGrandTotal(0.d);

		if (getInvoiceReports().length > 0) {
			HashMap<String, List<InvoiceList>> doLaaInvoiceListMap = new HashMap<String, List<InvoiceList>>();
			HashMap<String, InvoiceList> doLaaTotalMap = new HashMap<String, InvoiceList>();

			for (InvoiceList invoiceList : getInvoiceReports()) {
				Facility facility = getFacilityService().retrieveFacilityData(invoiceList.getFacilityId(), -1);
				List<InvoiceList> subList = doLaaInvoiceListMap.get(facility.getDoLaaCd());
				if (subList == null) {
					subList = new ArrayList<InvoiceList>();
					doLaaInvoiceListMap.put(facility.getDoLaaCd(), subList);
					InvoiceList total = new InvoiceList();
					total.setOrigAmount(0.d);
					total.setFacilityName("dummy");
					total.setRevenueTypeCd("Total Invoiced this Field Office ");
					doLaaTotalMap.put(facility.getDoLaaCd(), total);
				}
				InvoiceList total = doLaaTotalMap.get(facility.getDoLaaCd());
				invoiceList.setIntraStateVoucherFlag(facility.isIntraStateVoucherFlag());
				subList.add(invoiceList);
				total.setOrigAmount(total.getOrigAmount() + invoiceList.getOrigAmount());
				setGrandTotal(getGrandTotal() + invoiceList.getOrigAmount());
				invoiceList.setInvoiceDifference(total.getOrigAmount());
				String doLaaLabel = DOLAA.getData().getItems().getItemDesc(facility.getDoLaaCd());
				if (getDolaaCollections().get(doLaaLabel) == null) {
					getDolaaCollections().put(doLaaLabel, subList);
				}
				setValue(++processedCount);
			}
			// add totals to lists
			for (String doLaaCd : doLaaInvoiceListMap.keySet()) {
				InvoiceList total = doLaaTotalMap.get(doLaaCd);
				if (total != null) {
					List<InvoiceList> subList = doLaaInvoiceListMap.get(doLaaCd);
					subList.add(total);
				}
			}
			setHasSearchResults(true);
		}
		
		setEntrySet(new ArrayList<Map.Entry<String, List<InvoiceList>>>(getDolaaCollections().entrySet()));
	}

	public final List<SelectItem> getRevenueTypes() {
        return RevenueTypeDef.getData().getItems().getItems(
                revenueType, true);
    }
	
	public final boolean isHasSearchResults() {
		return hasSearchResults;
	}

	public final void setHasSearchResults(boolean hasSearchResults) {
		this.hasSearchResults = hasSearchResults;
	}

	public final Date getInvoiceDate() {
		return invoiceDate;
	}

	public final void setInvoiceDate(Date invoiceDate) {
		this.invoiceDate = invoiceDate;
	}

	public final String getRevenueType() {
		return revenueType;
	}

	public final void setRevenueType(String revenueType) {
		this.revenueType = revenueType;
	}

	public InvoiceList[] getInvoiceReports() {
		return invoiceReports;
	}

	public void setInvoiceReports(InvoiceList[] invoiceReports) {
		this.invoiceReports = invoiceReports;
	}

	public final Map<String, List<InvoiceList>> getDolaaCollections() {
		return dolaaCollections;
	}

	public final void setDolaaCollections(Map<String, List<InvoiceList>> dolaaCollections) {
		this.dolaaCollections = dolaaCollections;
	}

	public final List<Map.Entry<String, List<InvoiceList>>> getEntrySet() {
		return entrySet;
	}

	public final void setEntrySet(List<Map.Entry<String, List<InvoiceList>>> entrySet) {
		this.entrySet = entrySet;
	}

	public final double getGrandTotal() {
		return grandTotal;
	}

	public final void setGrandTotal(double grandTotal) {
		this.grandTotal = grandTotal;
	}

	public final String reset() {
        if (fProf != null) {
            fProf.setRefreshStr(" ");
        }

        if (getSearchThread() != null && getSearchThread().isAlive()) {
            try {
                getSearchThread().interrupt();
                getSearchThread().join();
            }
            catch (Exception e) {
                // Ignore.
            }
            finally {
                setSearchThread(null);
            }
        }
        else {
            setSearchThread(null);
        }

        setSearchStarted(false);
        setSearchCompleted(false);
        setBrowserCompleted(false);
        
		invoiceDate = null;
		hasSearchResults = false;
		revenueType = null;

		return SUCCESS;
	}
	
	 public void restoreCache(){
	    	
	 }
	    
	public void clearCache(){	 
		invoiceReports = null;
	  	hasSearchResults = false;
	}

	public final Thread getSearchThread() {
		return searchThread;
	}

	public final void setSearchThread(Thread searchThread) {
		this.searchThread = searchThread;
	}

	public final boolean isSeachStarted() {
		return searchStarted;
	}

	public final void setSearchStarted(boolean searchStarted) {
		this.searchStarted = searchStarted;
		if (searchStarted) {
			setShowProgressBar(true);
		}
	}
	
    public final synchronized void setErrorMessage(String error) {
        displayUtilBean.addMessageToQueue(error, DisplayUtil.ERROR, null);
    }

    public final synchronized void setInfoMessage(String info) {
        displayUtilBean.addMessageToQueue(info, DisplayUtil.INFO, null);
    }

	public final boolean isSearchCompleted() {
		return searchCompleted;
	}

	public final void setSearchCompleted(boolean searchCompleted) {
		this.searchCompleted = searchCompleted;
		if (searchCompleted) {
			setShowProgressBar(false);
		}
	}
	
    /** Start the thread that will do the search work. */
    public final synchronized void start(Thread thread) {
        setSearchCompleted(false);
        thread.start();
    }
    
    public final synchronized void setBrowserCompleted(boolean completed) {

        browserCompleted = completed;

        if (browserCompleted) {

            if (getSearchThread() != null && getSearchThread().isAlive()) {
                try {
                    getSearchThread().interrupt();
                    getSearchThread().join();
                }
                catch (Exception e) {
                    // Ignore.
                }
                finally {
                    setSearchThread(null);
                }
            }
            else {
                setSearchThread(null);
            }
            setSearchStarted(false);
        }

    }

	public final InvoiceList getSearchObj() {
		return searchObj;
	}

	public final void setSearchObj(InvoiceList searchObj) {
		this.searchObj = searchObj;
	}

	public final boolean isShowProgressBar() {
		return showProgressBar;
	}

	public final void setShowProgressBar(boolean showProgressBar) {
		this.showProgressBar = showProgressBar;
	}
}
