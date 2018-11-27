package us.oh.state.epa.stars2.database.dbObjects.ceta;


import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;

import us.oh.state.epa.stars2.database.dbObjects.BaseDB;


@SuppressWarnings("serial")
public class FceApplicationSearchLineItem extends BaseDB {
    private Integer applicationId;
	private String applicationNumber;
    private Integer permitId;
    private String permitNumber;
    
	private String applicationDesc;
    private String applicationTypeCd;
    private Timestamp receivedDate;
    private Timestamp submittedDate;
    private String previousApplicationNumber;
    private Timestamp finalIssuanceDate;
		
    public Integer getApplicationId() {
 		return applicationId;
 	}

 	public void setApplicationId(Integer applicationId) {
 		this.applicationId = applicationId;
 	}

 	public String getApplicationNumber() {
 		return applicationNumber;
 	}

 	public void setApplicationNumber(String applicationNumber) {
 		this.applicationNumber = applicationNumber;
 	}

 	public Integer getPermitId() {
 		return permitId;
 	}

 	public void setPermitId(Integer permitId) {
 		this.permitId = permitId;
 	}

 	public String getPermitNumber() {
 		return permitNumber;
 	}

 	public void setPermitNumber(String permitNumber) {
 		this.permitNumber = permitNumber;
 	}

 	public String getApplicationDesc() {
 		return applicationDesc;
 	}

 	public void setApplicationDesc(String applicationDesc) {
 		this.applicationDesc = applicationDesc;
 	}

 	public String getApplicationTypeCd() {
 		return applicationTypeCd;
 	}

 	public void setApplicationTypeCd(String applicationTypeCd) {
 		this.applicationTypeCd = applicationTypeCd;
 	}

 	public Timestamp getReceivedDate() {
 		return receivedDate;
 	}

 	public void setReceivedDate(Timestamp receivedDate) {
 		this.receivedDate = receivedDate;
 	}

 	public Timestamp getSubmittedDate() {
 		return submittedDate;
 	}

 	public void setSubmittedDate(Timestamp submittedDate) {
 		this.submittedDate = submittedDate;
 	}

 	public String getPreviousApplicationNumber() {
 		return previousApplicationNumber;
 	}

 	public void setPreviousApplicationNumber(String previousApplicationNumber) {
 		this.previousApplicationNumber = previousApplicationNumber;
 	}

 	public Timestamp getFinalIssuanceDate() {
 		return finalIssuanceDate;
 	}

 	public void setFinalIssuanceDate(Timestamp finalIssuanceDate) {
 		this.finalIssuanceDate = finalIssuanceDate;
 	}
   
	@Override
	public void populate(ResultSet rs) throws SQLException {
		if(null != rs) {
	        setApplicationId(rs.getInt("application_id"));
	        setApplicationTypeCd(rs.getString("application_type_cd"));
	        setApplicationNumber(rs.getString("application_nbr"));
	        setApplicationDesc(rs.getString("application_desc"));
	        setPreviousApplicationNumber(rs.getString("previous_application_nbr"));
	        setReceivedDate(rs.getTimestamp("received_date"));
	        setSubmittedDate(rs.getTimestamp("submitted_date"));
	        setPermitId(rs.getInt("permit_id"));
	        setPermitNumber(rs.getString("permit_nbr"));
	        setFinalIssuanceDate(rs.getTimestamp("final_issuance_date"));
		}
	}
}

