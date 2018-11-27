package us.oh.state.epa.stars2.database.dbObjects.application;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import us.oh.state.epa.stars2.database.dao.AbstractDAO;
import us.oh.state.epa.stars2.database.dbObjects.BaseDB;
import us.oh.state.epa.stars2.database.dbObjects.ValidationMessage;
import us.oh.state.epa.stars2.database.dbObjects.facility.EmissionUnit;
import us.oh.state.epa.stars2.database.dbObjects.facility.EuEmission;

@SuppressWarnings("serial")
public class NSRApplicationBACTEmission extends BaseDB {
	private Integer applicationEuId;
	private String pollutantCd;
	private String bact;

	public NSRApplicationBACTEmission() {
		super();
	}

	public Integer getApplicationEuId() {
		return applicationEuId;
	}

	public void setApplicationEuId(Integer applicationEuId) {
		this.applicationEuId = applicationEuId;
	}

	public String getPollutantCd() {
		return pollutantCd;
	}

	public void setPollutantCd(String pollutantCd) {
		this.pollutantCd = pollutantCd;
	}

	public String getBact() {
		return bact;
	}

	public void setBact(String bact) {
		this.bact = bact;
	}

	@Override
	public void populate(ResultSet rs) throws SQLException {
		try {
			setApplicationEuId(AbstractDAO.getInteger(rs, "application_eu_id"));
			setPollutantCd(rs.getString("pollutant_cd"));
			setBact(rs.getString("bact"));
			setLastModified(AbstractDAO.getInteger(rs, "last_modified"));
		} catch (SQLException sqle) {
			logger.warn(sqle.getMessage());
		}
	}

	@Override
	public int hashCode() {
		final int PRIME = 31;
		int result = super.hashCode();
		result = PRIME * result
				+ ((pollutantCd == null) ? 0 : pollutantCd.hashCode());
		result = PRIME * result + ((bact == null) ? 0 : bact.hashCode());
		result = PRIME * result
				+ ((applicationEuId == null) ? 0 : applicationEuId.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (getClass() != obj.getClass())
			return false;
		final NSRApplicationBACTEmission other = (NSRApplicationBACTEmission) obj;
		if (applicationEuId == null) {
			if (other.applicationEuId != null)
				return false;
		} else if (!applicationEuId.equals(other.applicationEuId))
			return false;
		if (pollutantCd == null) {
			if (other.pollutantCd != null)
				return false;
		} else if (!pollutantCd.equals(other.pollutantCd))
			return false;
		if (bact == null) {
			if (other.bact != null)
				return false;
		} else if (!bact.equals(other.bact))
			return false;
		return true;
	}
	
	public ValidationMessage[] validate() {

		String pageViewId = "";
		requiredField(pollutantCd, pageViewId + "pollutant", "Pollutant");
		requiredField(bact, pageViewId + "proposedBACT", "Proposed BACT");

		return new ArrayList<ValidationMessage>(validationMessages.values())
				.toArray(new ValidationMessage[0]);
	}

}
