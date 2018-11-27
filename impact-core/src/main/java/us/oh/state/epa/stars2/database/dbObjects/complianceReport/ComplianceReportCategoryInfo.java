package us.oh.state.epa.stars2.database.dbObjects.complianceReport;

import java.sql.ResultSet;
import java.sql.SQLException;

import us.oh.state.epa.stars2.database.dbObjects.BaseDB;

@SuppressWarnings("serial")
public class ComplianceReportCategoryInfo  extends BaseDB {
	
	String reportTypeDesc;
	String categoryTypeDesc;
	String explanation;
	
	public String getReportTypeDesc() {
		return reportTypeDesc;
	}
	
	public void setReportTypeDesc(String reportTypeDesc) {
		this.reportTypeDesc = reportTypeDesc;
	}
	
	public String getCategoryTypeDesc() {
		return categoryTypeDesc;
	}
	
	public void setCategoryTypeDesc(String categoryTypeDesc) {
		this.categoryTypeDesc = categoryTypeDesc;
	}
	
	public String getExplanation() {
		return explanation;
	}
	
	public void setExplanation(String explanation) {
		this.explanation = explanation;
	}
	
	@Override
	public void populate(ResultSet rs) throws SQLException {
		if(null != rs) {
			setReportTypeDesc(rs.getString("report_type_desc"));
			setCategoryTypeDesc(rs.getString("category_type_desc"));
			setExplanation(rs.getString("explanation"));
		}
	}
}
