package us.oh.state.epa.stars2.database.dbObjects.permit;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;

import org.apache.log4j.Logger;

import us.oh.state.epa.stars2.database.dao.AbstractDAO;
import us.oh.state.epa.stars2.database.dbObjects.BaseDB;
import us.oh.state.epa.stars2.database.dbObjects.ValidationMessage;
import us.oh.state.epa.stars2.framework.util.Utility;

@SuppressWarnings("serial")
public class TimeSheetRow extends BaseDB {
	
	
	private static Logger logger = Logger.getLogger(TimeSheetRow.class);
	
	private Integer hoursEntryId;
	private String appNumber;
	private String employeeName;
	private Timestamp date = new Timestamp((new Date()).getTime());
	private Float hours;
	private String comments;
	
	private Float hourlyRate;
	private Float amount;
	
	private boolean overtime = false;
	private String function;
	private String section;
	private Integer rowId;
	private Integer userId;
	private Integer appId;
	private Integer permitId;
	private String nsrId;
	private String tvId;
	private boolean invoiced = false;
	
	private Integer aqdsEmployeeId;
	private Integer aqdsAppId;
	
	public TimeSheetRow() {
		super();
	}
	
	public TimeSheetRow(TimeSheetRow old) {
		super(old);
		
		if(null != old) {
			setHoursEntryId(old.getHoursEntryId());
			setAppNumber(old.getAppNumber());
			setEmployeeName(old.getEmployeeName());

			setRowId(old.getRowId());
			setUserId(old.getUserId());
			setDate(old.getDate());
			setFunction(old.getFunction());
			setSection(old.getSection());
			setAppId(old.getAppId());
			setPermitId(old.getPermitId());
			setOvertime(old.isOvertime());
			setInvoiced(old.isInvoiced());
			setHours(old.getHours());
			setComments(old.getComments());
			setNsrId(old.getNsrId());
			setTvId(old.getTvId());
			setAqdsEmployeeId(old.getAqdsEmployeeId());
			setAqdsAppId(old.getAqdsAppId());
			setLastModified(old.getLastModified());

			setHourlyRate(old.getHourlyRate());
			setAmount(old.getAmount());
		}
	}
	
	public void populate(ResultSet rs) {
		if(null != rs) {

			try{
				setAqdsEmployeeId(rs.getInt("aqds_employee_id"));
				setAqdsAppId(rs.getInt("aqds_app_id"));
				setHoursEntryId(rs.getInt("aqds_hour_entry_id"));
				setAppNumber(rs.getString("section_nsr_id"));
				setRowId(rs.getInt("entry_id"));
				setUserId(rs.getInt("employee_id"));
				setDate(rs.getTimestamp("date"));
				setFunction(rs.getString("function"));
				setSection(rs.getString("section"));
				setNsrId(rs.getString("section_nsr_id"));
				setTvId(rs.getString("section_tv_id"));
				setAppId(rs.getInt("section_nsr_app"));
				setPermitId(rs.getInt("section_nsr_permit"));
				setOvertime(AbstractDAO.translateIndicatorToBoolean(rs.getString("ot")));
				setInvoiced(AbstractDAO.translateIndicatorToBoolean(rs.getString("invoiced")));
				setHours(rs.getFloat("hours"));
				setComments(rs.getString("comments"));
				setLastModified(rs.getInt("last_modified"));
			}catch(SQLException sqle) {
				logger.error(sqle.getMessage());
			}
			
			try {
				setEmployeeName(rs.getString("engineer"));
			} catch (SQLException e) {
				// engineer is an optional field
			}
			
			setHourlyRate(0.0f);
			setAmount(0.0f);
		}
	}

	@Override
	public ValidationMessage[] validate() {
		clearValidationMessages();
		
		requiredField(date, "date", "Date");
		requiredField(function, "function", "Function");
		requiredField(hours, "hours", "Hours");
		requiredField(overtime, "overtime", "OT");
		requiredField(invoiced, "invoiced", "Invoiced");
		
		if(!Utility.isNullOrEmpty(this.getComments())) {
			setComments(getComments().trim());
			if (getComments().length() > 80) {
				ValidationMessage valMsg = new ValidationMessage("description", 
						"Description must not exceed 80 characters.", 
						ValidationMessage.Severity.ERROR);
				this.validationMessages.put("description", valMsg);
			}
		}
		
		if (null != getHours()) {
			if (getHours() > 0) {
				if (getHours() % .5 != 0) {
					ValidationMessage valMsg = new ValidationMessage("hours", 
							"Hours must be divisible by 0.5.", 
							ValidationMessage.Severity.ERROR);
					this.validationMessages.put("hours", valMsg);
				}
			} else {
				ValidationMessage valMsg = new ValidationMessage("hours", 
						"Hours must be at least 0.5.", 
						ValidationMessage.Severity.ERROR);
				this.validationMessages.put("hours", valMsg);
			}
		}
		
		return new ArrayList<ValidationMessage>(validationMessages.values())
				.toArray(new ValidationMessage[0]);
	}
 	
	public String getTvId() {
		return tvId;
	}

	public void setTvId(String tvId) {
		this.tvId = tvId;
	}

	public Integer getAqdsEmployeeId() {
		return aqdsEmployeeId;
	}

	public void setAqdsEmployeeId(Integer aqdsEmployeeId) {
		this.aqdsEmployeeId = aqdsEmployeeId;
	}

	public Integer getAqdsAppId() {
		return aqdsAppId;
	}

	public void setAqdsAppId(Integer aqdsAppId) {
		this.aqdsAppId = aqdsAppId;
	}

	public boolean isInvoiced() {
		return invoiced;
	}

	public void setInvoiced(boolean invoiced) {
		this.invoiced = invoiced;
	}

	public String getNsrId() {
		return nsrId;
	}

	public void setNsrId(String nsrId) {
		this.nsrId = nsrId;
	}

	public Integer getAppId() {
		return appId;
	}

	public void setAppId(Integer appId) {
		this.appId = appId;
	}

	public Integer getPermitId() {
		return permitId;
	}

	public void setPermitId(Integer permitId) {
		this.permitId = permitId;
	}

	public Integer getUserId() {
		return userId;
	}

	public void setUserId(Integer userId) {
		this.userId = userId;
	}

	public Integer getRowId() {
		return rowId;
	}

	public void setRowId(Integer rowId) {
		this.rowId = rowId;
	}

	public boolean isOvertime() {
		return overtime;
	}
	
	public void setOvertime(boolean overtime) {
		this.overtime = overtime;
	}

	public String getOvertimeYesNo() {
		return isOvertime()? "Yes" : "No";
	}

	public String getInvoicedYesNo() {
		return isInvoiced()? "Yes" : "No";
	}

	public Integer getHoursEntryId() {
		return hoursEntryId;
	}

	public void setHoursEntryId(Integer hoursEntryId) {
		this.hoursEntryId = hoursEntryId;
	}

	public String getAppNumber() {
		return appNumber;
	}
	
	public void setAppNumber(String appNumber) {
		this.appNumber = appNumber;
	}
	
	public String getEmployeeName() {
		return employeeName;
	}
	
	public void setEmployeeName(String employeeName) {
		this.employeeName = employeeName;
	}
	
	public Timestamp getDate() {
		return date;
	}
	
	public void setDate(Timestamp date) {
		this.date = date;
	}
	
	public Float getHours() {
		return hours;
	}
	
	public void setHours(Float hours) {
		this.hours = hours;
	}
	
	public String getComments() {
		return comments;
	}
	
	public void setComments(String comments) {
		this.comments = comments;
	}

	public Float getHourlyRate() {
		return hourlyRate;
	}

	public void setHourlyRate(Float hourlyRate) {
		this.hourlyRate = hourlyRate;
	}

	public Float getAmount() {
		return amount;
	}

	public void setAmount(Float amount) {
		this.amount = amount;
	}

	public String getFunction() {
		return function;
	}

	public void setFunction(String function) {
		this.function = function;
	}

	public String getSection() {
		return section;
	}

	public void setSection(String section) {
		this.section = section;
	}

	
}
