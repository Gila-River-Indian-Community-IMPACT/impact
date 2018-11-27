package us.oh.state.epa.stars2.database.dbObjects.ceta;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import us.oh.state.epa.stars2.database.dbObjects.BaseDB;
import us.oh.state.epa.stars2.database.dbObjects.complianceReport.ComplianceReport;
import us.oh.state.epa.stars2.database.dbObjects.complianceReport.ComplianceReportList;
import us.oh.state.epa.stars2.database.dbObjects.document.Correspondence;
import us.oh.state.epa.stars2.database.dbObjects.permit.Permit;
import us.wy.state.deq.impact.def.FCEPreSnapshotTypeDef;

@SuppressWarnings("serial")
public class FcePreData extends BaseDB{

	//Application
	private SearchDateRange dateRangePA;
	private List<Integer> applicationList;

	//Permit
	private SearchDateRange dateRangePT;
	private List<Integer> permitList;
	
	//Stack Test
	private SearchDateRange dateRangeST;
	private List<Integer> stackTestList;
	
	//Compliance Report
	private SearchDateRange dateRangeCR;
	private List<Integer> complianceReportList;
	
	//Correspondence
	private SearchDateRange dateRangeDC;
	private List<Integer> correspondenceList;	

	//Emissions Inventory
	private SearchDateRange dateRangeEI;
	private List<Integer> emissionsInventoryList;	
	
	//Ambient Monitor
	private SearchDateRange dateRangeAmbientMonitors;
	private List<Integer> ambientMonitorList;

	//Site Visits
	private SearchDateRange dateRangeSiteVisits;
	private List<Integer> siteVisitList;
	
	//CEM/COM/CMS Monitor
	private SearchDateRange dateRangeContinuousMonitors;
	private List<Integer> continuousMonitorList;
	//Add other snapshot

	public FcePreData(){
		super();
		dateRangePA = new SearchDateRange();
		applicationList = new ArrayList<Integer>();
		dateRangePT = new SearchDateRange();
		permitList = new ArrayList<Integer>();
		dateRangeST = new SearchDateRange();
		stackTestList = new ArrayList<Integer>();
		dateRangeSiteVisits = new SearchDateRange();
		siteVisitList = new ArrayList<Integer>();
		dateRangeCR = new SearchDateRange();
		complianceReportList = new ArrayList<Integer>();
		dateRangeDC = new SearchDateRange();
		correspondenceList = new ArrayList<Integer>();
		dateRangeEI = new SearchDateRange();
		emissionsInventoryList = new ArrayList<Integer>();
		dateRangeAmbientMonitors = new SearchDateRange();
		ambientMonitorList = new ArrayList<Integer>();
		dateRangeContinuousMonitors = new SearchDateRange();
		continuousMonitorList = new ArrayList<Integer>();
	}
	
	@Override
	public void populate(ResultSet rs) throws SQLException {
		// TODO Auto-generated method stub
		return;
		
	}
	
	public SearchDateRange getDateRangePA() {
		return dateRangePA;
	}

	public void setDateRangePA(SearchDateRange dateRangePA) {
		this.dateRangePA = dateRangePA;
	}
	
	public List<Integer> getApplicationList() {
		return applicationList;
	}

	public void setApplicationList(List<Integer> applicationList) {
		this.applicationList = applicationList;
	}

	public SearchDateRange getDateRangePT() {
		return dateRangePT;
	}

	public void setDateRangePT(SearchDateRange dateRangePT) {
		this.dateRangePT = dateRangePT;
	}

	public List<Integer> getPermitList() {
		return permitList;
	}

	public void setPermitList(List<Integer> permitList) {
		this.permitList = permitList;
	}


	public SearchDateRange getDateRangeST() {
		return dateRangeST;
	}

	public void setDateRangeST(SearchDateRange dateRangeST) {
		this.dateRangeST = dateRangeST;
	}

	public List<Integer> getStackTestList() {
		return stackTestList;
	}

	public void setStackTestList(List<Integer> stackTestList) {
		this.stackTestList = stackTestList;
	}

	public SearchDateRange getDateRangeSiteVisits() {
		return dateRangeSiteVisits;
	}

	public void setDateRangeSiteVisits(SearchDateRange dateRangeSiteVisits) {
		this.dateRangeSiteVisits = dateRangeSiteVisits;
	}

	public List<Integer> getSiteVisitList() {
		return siteVisitList;
	}

	public void setSiteVisitList(List<Integer> siteVisitList) {
		this.siteVisitList = siteVisitList;
	}

	public SearchDateRange getDateRangeCR() {
		return dateRangeCR;
	}

	public void setDateRangeCR(SearchDateRange dateRangeCR) {
		this.dateRangeCR = dateRangeCR;
	}

	public List<Integer> getComplianceReportList() {
		return complianceReportList;
	}

	public void setComplianceReportList(List<Integer> complianceReportList) {
		this.complianceReportList = complianceReportList;
	}

	public SearchDateRange getDateRangeAmbientMonitors() {
		return dateRangeAmbientMonitors;
	}

	public void setDateRangeAmbientMonitors(SearchDateRange dateRangeAmbientMonitors) {
		this.dateRangeAmbientMonitors = dateRangeAmbientMonitors;
	}

	public List<Integer> getAmbientMonitorList() {
		return ambientMonitorList;
	}

	public void setAmbientMonitorList(List<Integer> ambientMonitorList) {
		this.ambientMonitorList = ambientMonitorList;
	}


	public SearchDateRange getDateRangeDC() {
		return dateRangeDC;
	}

	public void setDateRangeDC(SearchDateRange dateRangeDC) {
		this.dateRangeDC = dateRangeDC;
	}

	public List<Integer> getCorrespondenceList() {
		return correspondenceList;
	}

	public void setCorrespondenceList(List<Integer> correspondenceList) {
		this.correspondenceList = correspondenceList;
	}

	public SearchDateRange getDateRangeContinuousMonitors() {
		return dateRangeContinuousMonitors;
	}

	public void setDateRangeContinuousMonitors(SearchDateRange dateRangeContinuousMonitors) {
		this.dateRangeContinuousMonitors = dateRangeContinuousMonitors;
	}

	public List<Integer> getContinuousMonitorList() {
		return continuousMonitorList;
	}

	public void setContinuousMonitorList(List<Integer> continuousMonitorList) {
		this.continuousMonitorList = continuousMonitorList;
	}

	
	public SearchDateRange getDateRangeEI() {
		return dateRangeEI;
	}

	public void setDateRangeEI(SearchDateRange dateRangeEI) {
		this.dateRangeEI = dateRangeEI;
	}

	public List<Integer> getEmissionsInventoryList() {
		return emissionsInventoryList;
	}

	public void setEmissionsInventoryList(List<Integer> emissionsInventoryList) {
		this.emissionsInventoryList = emissionsInventoryList;
	}

	public <T extends BaseDB> List<Integer> getSnapshotIdList(List<T> searchResultList, Class<T> classType){
        HashSet<Integer> Ids = new HashSet<Integer>();
        if (FceApplicationSearchLineItem.class.equals(classType)){
        	for (T item: searchResultList){
    			Ids.add(((FceApplicationSearchLineItem) item).getApplicationId());
    		}
        } else if (Permit.class.equals(classType)){
        	for (T item: searchResultList){
    			Ids.add(((Permit) item).getPermitID());
    		}
        } else if (FceStackTestSearchLineItem.class.equals(classType)){
        	for (T item: searchResultList){
    			Ids.add(((FceStackTestSearchLineItem) item).getStackTestId());
    		}
        } else if (ComplianceReportList.class.equals(classType)){
        	for (T item: searchResultList){
    			Ids.add(((ComplianceReportList) item).getReportId());
    		}
        } else if (Correspondence.class.equals(classType)){
        	for (T item: searchResultList){
    			Ids.add(((Correspondence) item).getCorrespondenceID());
    		}
        } else if (FceEmissionsInventoryLineItem.class.equals(classType)){
        	for (T item: searchResultList){
    			Ids.add(((FceEmissionsInventoryLineItem) item).getEmissionsRptId());
    		}
        } else if (FceContinuousMonitorLineItem.class.equals(classType)){
        	for (T item: searchResultList){
    			Ids.add(((FceContinuousMonitorLineItem) item).getLimitId());
    		}
        } else if (FceAmbientMonitorLineItem.class.equals(classType)){
        	for (T item: searchResultList){
    			Ids.add(((FceAmbientMonitorLineItem) item).getMonitorId());
    		}
        } else if (SiteVisit.class.equals(classType)){
        	for (T item: searchResultList){
    			Ids.add(((SiteVisit) item).getId());
    		}
        }
        return new ArrayList<Integer>(Ids);
	}	
	
	public <T extends BaseDB> void setSnapshotIdList(List<T> searchResultList, Class<T> classType){
        HashSet<Integer> Ids = new HashSet<Integer>();
        if (FceApplicationSearchLineItem.class.equals(classType)){
        	for (T item: searchResultList){
    			Ids.add(((FceApplicationSearchLineItem) item).getApplicationId());
    		}
        	setApplicationList(new ArrayList<Integer>(Ids));
        } else if (Permit.class.equals(classType)){
        	for (T item: searchResultList){
    			Ids.add(((Permit) item).getPermitID());
    		}
        	setPermitList(new ArrayList<Integer>(Ids));
        } else if (FceStackTestSearchLineItem.class.equals(classType)){
        	for (T item: searchResultList){
    			Ids.add(((FceStackTestSearchLineItem) item).getStackTestId());
    		}
        	setStackTestList(new ArrayList<Integer>(Ids));
        } else if (ComplianceReportList.class.equals(classType)){
        	for (T item: searchResultList){
    			Ids.add(((ComplianceReportList) item).getReportId());
    		}
        	setComplianceReportList(new ArrayList<Integer>(Ids));
        } else if (Correspondence.class.equals(classType)){
        	for (T item: searchResultList){
    			Ids.add(((Correspondence) item).getCorrespondenceID());
    		}
        	setCorrespondenceList(new ArrayList<Integer>(Ids));
        } else if (FceEmissionsInventoryLineItem.class.equals(classType)){
        	for (T item: searchResultList){
    			Ids.add(((FceEmissionsInventoryLineItem) item).getEmissionsRptId());
    		}
        	setEmissionsInventoryList(new ArrayList<Integer>(Ids));
        } else if (FceContinuousMonitorLineItem.class.equals(classType)){
        	for (T item: searchResultList){
    			Ids.add(((FceContinuousMonitorLineItem) item).getLimitId());
    		}
        	setContinuousMonitorList(new ArrayList<Integer>(Ids));
        } else if (FceAmbientMonitorLineItem.class.equals(classType)){
        	for (T item: searchResultList){
    			Ids.add(((FceAmbientMonitorLineItem) item).getSiteId());
    		}
        	setAmbientMonitorList(new ArrayList<Integer>(Ids));
        } else if (SiteVisit.class.equals(classType)){
        	for (T item: searchResultList){
    			Ids.add(((SiteVisit) item).getId());
    		}
        	setSiteVisitList(new ArrayList<Integer>(Ids));
        }

	}	
	

	

}
