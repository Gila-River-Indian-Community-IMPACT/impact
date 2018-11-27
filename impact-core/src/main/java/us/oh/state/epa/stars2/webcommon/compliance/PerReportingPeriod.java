package us.oh.state.epa.stars2.webcommon.compliance;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

import us.oh.state.epa.stars2.bo.ComplianceReportBO;
import us.oh.state.epa.stars2.bo.PermitBO;
import us.oh.state.epa.stars2.database.dbObjects.complianceReport.CompliancePerDetail;
import us.oh.state.epa.stars2.database.dbObjects.permit.Permit;
import us.oh.state.epa.stars2.database.dbObjects.permit.PermitEU;
import us.oh.state.epa.stars2.database.dbObjects.permit.PermitEUGroup;
import us.oh.state.epa.stars2.def.DefSelectItems;
import us.oh.state.epa.stars2.def.PTIOPERDueDateDef;
import us.oh.state.epa.stars2.webcommon.AppBase;

@SuppressWarnings("serial")
public class PerReportingPeriod extends AppBase {

    private Timestamp  startDate;
    private Timestamp endDate;
    private Timestamp dueDate;
    private String dueDateCd;
    private String facilityId;
    private int reportCt=-1;
    private SimpleDateFormat sdf =  new SimpleDateFormat("MM/dd/yyyy");
    private SimpleDateFormat sdf2 =  new SimpleDateFormat("yyyyMMdd");
    private List<CompliancePerDetail> perDetail = new ArrayList<CompliancePerDetail>();
    
    public PerReportingPeriod(String facilityId,Timestamp startDate, Timestamp endDate, String dueDateCd, Timestamp dueDate) {
        this.startDate = startDate;
        this.endDate = endDate;
        this.dueDateCd = dueDateCd;
        this.facilityId = facilityId;
        this.dueDate = dueDate;
        computeDueDate();
        logger.debug("Creating PER Period object: " + startDate + " to "+endDate);
        logger.debug("Description after creation: " + getDescription());
    }

    public String getDescription() {
        try {
            return sdf.format(getDueDate()) + " (period: " + sdf.format(getStartDate()) + " - " + sdf.format(getEndDate())+ ")";
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            return "ERROR";
        }
    }

    private void computeDueDate() {
        DefSelectItems dDueDateMonthDays= null;
        try {
            dDueDateMonthDays = PTIOPERDueDateDef.getDueDateMonthDay().getItems();
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
        int iDueDateMonth = Integer.parseInt(dDueDateMonthDays.getItemDesc(dueDateCd).substring(0, 2))-1;
        int iDueDateDay = Integer.parseInt(dDueDateMonthDays.getItemDesc(dueDateCd).substring(2));
        
        Calendar x =  Calendar.getInstance();
        x.setTimeInMillis(endDate.getTime());
        Calendar cDueDt = Calendar.getInstance();
        
        Calendar cal =  Calendar.getInstance();
        cal.setTimeInMillis(dueDate.getTime());
        int year = cal.get(Calendar.YEAR); //x.get(Calendar.YEAR);
        
        if (x.get(Calendar.MONTH)==12) {
            //the due date year is the next year if our reporting end date month
            //is December.
            year++;
        }
        cDueDt.set(year, iDueDateMonth,iDueDateDay,23,59,59);
        dueDate= new Timestamp(cDueDt.getTimeInMillis());
    }
    
    public CompliancePerDetail[] retrieveEusForPeriod() {
        if (perDetail.size()==0) {
            HashMap<Integer,CompliancePerDetail> uniqueEU = new HashMap<Integer,CompliancePerDetail>();
            PermitBO permitBo = new PermitBO();
            try {
                List<Permit> permits = permitBo.searchPERs(facilityId, getStartDate(),getEndDate());
                for (int i=0;i<permits.size();i++) {
                    Permit p = permits.get(i);
                    String effectiveDate = "";
                    if (p.getEffectiveDate() != null) {
                    	effectiveDate = sdf.format(p.getEffectiveDate());
                    }
                    List<PermitEUGroup> eug = p.getEuGroups();
                    for (int j=0;j<eug.size();j++) {
                        PermitEUGroup euGroup = eug.get(j);
                        List<PermitEU> eus= euGroup.getPermitEUs();
               
                        for (int k=0;k<eus.size();k++) {
                            PermitEU eu = eus.get(k);
                            CompliancePerDetail euDetail = uniqueEU.get(eu.getCorrEpaEMUID());
                            if (euDetail == null) {
                                logger.debug("Building EU's for period: Adding EU #"+eu.getCorrEpaEMUID());
                                CompliancePerDetail dTemp = new CompliancePerDetail();
                                dTemp.setEUId(eu.getCorrEpaEMUID());
                                dTemp.setPermitId(p.getPermitID());
                                dTemp.setPermitInfo(p.getPermitNumber() + ": " + effectiveDate);
                                perDetail.add(dTemp);
                                uniqueEU.put(new Integer(eu.getCorrEpaEMUID()),dTemp);
                            } else {
                            	// more than one permit for the EU
                            	euDetail.setPermitInfo(euDetail.getPermitInfo() + "; " + p.getPermitNumber() + 
                            			" " + effectiveDate);
                            }
                        }
                      
                    }
                }
            
            } catch (Exception e) {
                logger.error(e.getMessage(), e);
            }
        }

        return perDetail.toArray(new CompliancePerDetail[0]);
    }
    

    
    public void updateEusForPeriod(CompliancePerDetail[] details) {
    	PermitBO permitBo = new PermitBO();
    	try {
    		List<Permit> permits = permitBo.searchPERs(facilityId, getStartDate(),getEndDate());
    		for (int i=0;i<permits.size();i++) {
    			Permit p = permits.get(i);
    			String effectiveDate = "";
    			if (p.getEffectiveDate() != null) {
    				effectiveDate = sdf.format(p.getEffectiveDate());
    			}
    			List<PermitEUGroup> eug = p.getEuGroups();
    			for (int j=0;j<eug.size();j++) {
    				PermitEUGroup euGroup = eug.get(j);
    				List<PermitEU> eus= euGroup.getPermitEUs();

    				for (int k=0;k<eus.size();k++) {
    					PermitEU eu = eus.get(k);
    					CompliancePerDetail euDetail = null;
    					for (CompliancePerDetail dtl : details) {
    						if (eu.getCorrEpaEMUID().equals(dtl.getEUId())) {
    							euDetail = dtl;
    							break;
    						}
    					}
    					if (euDetail == null) {
    						logger.error("Missing EU in PER Detail list: "+eu.getCorrEpaEMUID());
    					} else {
    						if (euDetail.getPermitInfo() == null) {
    							euDetail.setPermitId(p.getPermitID());
    							euDetail.setPermitInfo(p.getPermitNumber() + ": " + effectiveDate);
    						} else {
    							euDetail.setPermitInfo(euDetail.getPermitInfo() + "; " + p.getPermitNumber() + 
    									": " + effectiveDate);
    						}
    					}
    				}
    			}
    		}

    	} catch (Exception e) {
    		logger.error(e.getMessage(), e);
    	}
    }
    
    public int getEuCount() {
        /* 
         * Convenience attribute to show user how many EU's are represented in 
         * this period.
        */
        return perDetail.size();
    }
    
    public Integer getPerReportCount() {
        /* 
         * Convenience attribute to show user how many reports are represented in 
         * this period.
        */
      return reportCt;
    }
    
    public void retrieveReportCount() {
        logger.debug("report count start: " + getStartDate());
        ComplianceReportBO crBo = new ComplianceReportBO();
        try {
            int pendCt= crBo.retrievePerReportCount(facilityId, "pend", new java.sql.Timestamp(getStartDate().getTime()), new java.sql.Timestamp(getEndDate().getTime()));
            int revCt= crBo.retrievePerReportCount(facilityId, "rvwd", new java.sql.Timestamp(getStartDate().getTime()), new java.sql.Timestamp(getEndDate().getTime()));
            int sbmtCt= crBo.retrievePerReportCount(facilityId, "sbmt", new java.sql.Timestamp(getStartDate().getTime()), new java.sql.Timestamp(getEndDate().getTime()));
            reportCt = pendCt+revCt + sbmtCt;
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            reportCt=-1;
        }
        logger.debug("report count finish: " + getStartDate());
    }
    public Timestamp getDueDate() {
        return dueDate;
    }
    
    public String getFullDueDateCd() {
        return sdf2.format(dueDate);
    }
    
    public String getDueDateCd() {
        return dueDateCd;
    }
    public void setDueDateCd(String dueDateCd) {
        this.dueDateCd = dueDateCd;
    }
    public Timestamp getEndDate() {
        return endDate;
    }
    public void setEndDate(Timestamp endDate) {
        this.endDate = endDate;
    }
    public Timestamp getStartDate() {
        return startDate;
    }
    public void setStartDate(Timestamp startDate) {
        this.startDate = startDate;
    }

}
