package us.oh.state.epa.stars2.database.dbObjects.ceta;



import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Set;
import java.util.TreeSet;

import us.oh.state.epa.stars2.database.dao.AbstractDAO;
import us.oh.state.epa.stars2.database.dbObjects.BaseDB;
import us.oh.state.epa.stars2.def.PollutantDef;


@SuppressWarnings("serial")
public class FceEmissionsInventoryLineItem extends BaseDB {
    private Integer emissionsRptId;
    private String emissionsInventoryId;
    private Integer reportModified; //previous incentory
    private String emissionsInventoryModifiedId;
    
    private Integer fpId; //Facility History ID
    private String facilityId;
    private Integer versionId; // used for Facility History ID (Current)
    
    private Integer reportYear;
    private String contentTypeCd;
    private Set<String> regulatoryRequirementCds = new TreeSet<String>(); // search result
    
    private String reportingStateCd; // Reporting State Cd
    private Timestamp receivedDate;
//    private Timestamp rptReceivedStatusDate;  // NOT IN USE


    private String regulatoryRequirementCdsString;
    private String reportingStateDesc;
	
    private HashMap<String, Float> totalEmissions = new HashMap<String, Float>();
    private String emissionsPM;
	private String emissionsPM10;
    private String emissionsPM25;
    private String emissionsCO;
    private String emissionsNOx;
    private String emissionsSO2;
    private String emissionsVOC;
    
	@Override
	public void populate(ResultSet rs) throws SQLException {
		// TODO Auto-generated method stub
        try {
            setEmissionsRptId(AbstractDAO.getInteger(rs, "emissions_rpt_id"));
            setEmissionsInventoryId(rs.getString("ei_id"));
            Integer report_modified = AbstractDAO.getInteger(rs, "report_modified");
            setReportModified(report_modified);
            if (report_modified != null) {
            	setEmissionsInventoryModifiedId(AbstractDAO.formatId("EI", "%07d", report_modified.toString()));
            }
            setFpId(AbstractDAO.getInteger(rs, "fp_id"));
            setFacilityId(rs.getString("facility_id"));
            setVersionId(AbstractDAO.getInteger(rs, "version_id"));
            setReportingStateCd(rs.getString("rpt_received_status_cd"));
            setReportYear(AbstractDAO.getInteger(rs, "report_year"));
            setReceivedDate(rs.getTimestamp("received_date"));
        } catch (SQLException sqle) {
        	logger.error(sqle.getMessage(), sqle);
        }
	}


	public Integer getEmissionsRptId() {
		return emissionsRptId;
	}

	public void setEmissionsRptId(Integer emissionsRptId) {
		this.emissionsRptId = emissionsRptId;
	}

	public String getEmissionsInventoryId() {
		return emissionsInventoryId;
	}

	public void setEmissionsInventoryId(String emissionsInventoryId) {
		this.emissionsInventoryId = emissionsInventoryId;
	}

	public Integer getReportModified() {
		return reportModified;
	}

	public void setReportModified(Integer reportModified) {
		this.reportModified = reportModified;
	}

	public String getEmissionsInventoryModifiedId() {
		return emissionsInventoryModifiedId;
	}

	public void setEmissionsInventoryModifiedId(String emissionsInventoryModifiedId) {
		this.emissionsInventoryModifiedId = emissionsInventoryModifiedId;
	}

	public Integer getFpId() {
		return fpId;
	}

	public void setFpId(Integer fpId) {
		this.fpId = fpId;
	}

	public String getFacilityId() {
		return facilityId;
	}

	public void setFacilityId(String facilityId) {
		this.facilityId = facilityId;
	}

	public Integer getVersionId() {
		return versionId;
	}

	public void setVersionId(Integer versionId) {
		this.versionId = versionId;
	}

	public Integer getReportYear() {
		return reportYear;
	}

	public void setReportYear(Integer reportYear) {
		this.reportYear = reportYear;
	}

	public String getContentTypeCd() {
		return contentTypeCd;
	}

	public void setContentTypeCd(String contentTypeCd) {
		this.contentTypeCd = contentTypeCd;
	}

	public Set<String> getRegulatoryRequirementCds() {
		return regulatoryRequirementCds;
	}

	public void setRegulatoryRequirementCds(Set<String> regulatoryRequirementCds) {
		this.regulatoryRequirementCds = regulatoryRequirementCds;
	}

	public String getReportingStateCd() {
		return reportingStateCd;
	}

	public void setReportingStateCd(String reportingStateCd) {
		this.reportingStateCd = reportingStateCd;
	}

	public Timestamp getReceivedDate() {
		return receivedDate;
	}

	public void setReceivedDate(Timestamp receivedDate) {
		this.receivedDate = receivedDate;
	}

	public String getRegulatoryRequirementCdsString() {
		return regulatoryRequirementCdsString;
	}

	public void setRegulatoryRequirementCdsString(String regulatoryRequirementCdsString) {
		this.regulatoryRequirementCdsString = regulatoryRequirementCdsString;
	}

	public String getReportingStateDesc() {
		return reportingStateDesc;
	}

	public void setReportingStateDesc(String reportingStateDesc) {
		this.reportingStateDesc = reportingStateDesc;
	}

	public HashMap<String, Float> getTotalEmissions() {
		return totalEmissions;
	}

	public void setTotalEmissions(HashMap<String, Float> totalEmissions) {
		this.totalEmissions = totalEmissions;
	}
	
	public void setPollutantEmissionsValue(HashMap<String, Float> totalEmissions){
		setTotalEmissions(totalEmissions);//Calculated Total Emissions
		//convert to 2 decimal number
		DecimalFormat f = new DecimalFormat("#0.00");
		if (totalEmissions.get(PollutantDef.PE_CD) != null){
			setEmissionsPM(f.format(totalEmissions.get(PollutantDef.PE_CD)));
		}
		if (totalEmissions.get(PollutantDef.PM10_CD) !=null){
			setEmissionsPM10(f.format(totalEmissions.get(PollutantDef.PM10_CD)));
		}
		if (totalEmissions.get(PollutantDef.PM25_CD) !=null){
			setEmissionsPM25(f.format(totalEmissions.get(PollutantDef.PM25_CD)));
		}
		if (totalEmissions.get(PollutantDef.CO_CD) != null){
			setEmissionsCO(f.format(totalEmissions.get(PollutantDef.CO_CD)));
		}
		if (totalEmissions.get(PollutantDef.NOX_CD) != null){
			setEmissionsNOx(f.format(totalEmissions.get(PollutantDef.NOX_CD)));
		}
		if (totalEmissions.get(PollutantDef.SO2_CD) != null){
			setEmissionsSO2(f.format(totalEmissions.get(PollutantDef.SO2_CD)));
		}
		if (totalEmissions.get(PollutantDef.VOC_CD) != null){
			setEmissionsVOC(f.format(totalEmissions.get(PollutantDef.VOC_CD)));
		}
	}
	
    public String getEmissionsPM() {
 		return emissionsPM;
 	}


 	public void setEmissionsPM(String emissionsPM) {
 		this.emissionsPM = emissionsPM;
 	}


 	public String getEmissionsPM10() {
 		return emissionsPM10;
 	}


 	public void setEmissionsPM10(String emissionsPM10) {
 		this.emissionsPM10 = emissionsPM10;
 	}


 	public String getEmissionsPM25() {
 		return emissionsPM25;
 	}


 	public void setEmissionsPM25(String emissionsPM25) {
 		this.emissionsPM25 = emissionsPM25;
 	}


 	public String getEmissionsCO() {
 		return emissionsCO;
 	}


 	public void setEmissionsCO(String emissionsCO) {
 		this.emissionsCO = emissionsCO;
 	}


 	public String getEmissionsNOx() {
 		return emissionsNOx;
 	}


 	public void setEmissionsNOx(String emissionsNOx) {
 		this.emissionsNOx = emissionsNOx;
 	}


 	public String getEmissionsSO2() {
 		return emissionsSO2;
 	}


 	public void setEmissionsSO2(String emissionsSO2) {
 		this.emissionsSO2 = emissionsSO2;
 	}


 	public String getEmissionsVOC() {
 		return emissionsVOC;
 	}


 	public void setEmissionsVOC(String emissionsVOC) {
 		this.emissionsVOC = emissionsVOC;
 	}


    //For sorting in jsp table
 	public Float getFloatEmissionsPM(){
 		return totalEmissions.get(PollutantDef.PE_CD);
 	}
 	
 	public Float getFloatEmissionsPM10(){
 		return totalEmissions.get(PollutantDef.PM10_CD);
 	}
	
 	public Float getFloatEmissionsPM25(){
 		return totalEmissions.get(PollutantDef.PM25_CD);
 	}
 	
    public Float getFloatEmissionsCO() {
    	return totalEmissions.get(PollutantDef.CO_CD);
	}

    public Float getFloatEmissionsNOx() {
    	return totalEmissions.get(PollutantDef.NOX_CD);
	}

	public Float getFloatEmissionsSO2() {
		return totalEmissions.get(PollutantDef.SO2_CD);
	}

	public Float getFloatEmissionsVOC() {
		return totalEmissions.get(PollutantDef.VOC_CD);
	}
}



