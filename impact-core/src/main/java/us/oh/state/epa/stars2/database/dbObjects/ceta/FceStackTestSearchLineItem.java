package us.oh.state.epa.stars2.database.dbObjects.ceta;


import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;

import us.oh.state.epa.stars2.database.dao.AbstractDAO;


@SuppressWarnings("serial")
public class FceStackTestSearchLineItem extends CetaBaseDB {
	private Integer stackTestId;
	private Integer fpId;

	private String stckId;
	private String stackTestMethodCd;
	private Timestamp dateReceived;
	private Timestamp dateReviewed;
	private String conformedToTestMethod;
	
	
	private String pollutantsTested;
	private String eus;
	private String pollutantsFailed;
	private String testDatesString; 
	private Timestamp firstVisitDate;

	@Override
	public void populate(ResultSet rs) throws SQLException {
		// TODO Auto-generated method stub
		if(null != rs) {
			setStackTestId(rs.getInt("stack_test_id"));
			setFpId(rs.getInt("fp_id"));
	        setStckId(rs.getString("stck_id"));
	        setStackTestMethodCd(rs.getString("stack_test_method_cd"));        
	        setDateReceived(rs.getTimestamp("date_received"));
	        setDateReviewed(rs.getTimestamp("date_evaluated"));
	        conformedToTestMethod = rs.getString("conformed");
		}
	}
	
	
	public Integer getStackTestId() {
		return stackTestId;
	}
	public void setStackTestId(Integer stackTestId) {
		this.stackTestId = stackTestId;
	}
	public Integer getFpId() {
		return fpId;
	}
	public void setFpId(Integer fpId) {
		this.fpId = fpId;
	}
	
	public String getStckId() {
		return stckId;
	}
	public void setStckId(String stckId) {
		this.stckId = stckId;
	}
	public String getStackTestMethodCd() {
		return stackTestMethodCd;
	}
	public void setStackTestMethodCd(String stackTestMethodCd) {
		this.stackTestMethodCd = stackTestMethodCd;
	}
	public Timestamp getDateReceived() {
		return dateReceived;
	}
	public void setDateReceived(Timestamp dateReceived) {
		this.dateReceived = dateReceived;
	}
	public Timestamp getDateReviewed() {
		return dateReviewed;
	}
	public void setDateReviewed(Timestamp dateReviewed) {
		this.dateReviewed = dateReviewed;
	}
	public String getConformedToTestMethod() {
		return conformedToTestMethod;
	}
	public void setConformedToTestMethod(String conformedToTestMethod) {
		this.conformedToTestMethod = conformedToTestMethod;
	}
	public String getPollutantsTested() {
		return pollutantsTested;
	}
	public void setPollutantsTested(String pollutantsTested) {
		this.pollutantsTested = pollutantsTested;
	}
	public String getEus() {
		return eus;
	}
	public void setEus(String eus) {
		this.eus = eus;
	}
	public String getPollutantsFailed() {
		return pollutantsFailed;
	}
	public void setPollutantsFailed(String pollutantsFailed) {
		this.pollutantsFailed = pollutantsFailed;
	}
	public String getTestDatesString() {
		return testDatesString;
	}
	public void setTestDatesString(String testDatesString) {
		this.testDatesString = testDatesString;
	}

	public Timestamp getFirstVisitDate() {
		return firstVisitDate;
	}


	public void setFirstVisitDate(Timestamp firstVisitDate) {
		this.firstVisitDate = firstVisitDate;
	}

}



