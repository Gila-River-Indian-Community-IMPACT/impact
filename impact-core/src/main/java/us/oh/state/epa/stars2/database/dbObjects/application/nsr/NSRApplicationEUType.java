package us.oh.state.epa.stars2.database.dbObjects.application.nsr;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import us.oh.state.epa.stars2.database.dao.AbstractDAO;
import us.oh.state.epa.stars2.database.dbObjects.BaseDB;
import us.oh.state.epa.stars2.database.dbObjects.ValidationMessage;


public class NSRApplicationEUType extends BaseDB {

	private static final long serialVersionUID = -4961696705085810673L;

	private Integer applicationEuId;
	private String emissionUnitTypeCd;

	public NSRApplicationEUType() {
		super();
	}

	public NSRApplicationEUType(NSRApplicationEUType old) {
		super(old);
		if (old != null) {
			setApplicationEuId(old.getApplicationEuId());
			setEmissionUnitTypeCd(old.getEmissionUnitTypeCd());
		}
	}

	public Integer getApplicationEuId() {
		return applicationEuId;
	}

	public void setApplicationEuId(Integer applicationEuId) {
		this.applicationEuId = applicationEuId;
	}

	public String getEmissionUnitTypeCd() {
		return emissionUnitTypeCd;
	}

	public void setEmissionUnitTypeCd(String emissionUnitTypeCd) {
		this.emissionUnitTypeCd = emissionUnitTypeCd;
	}

	@Override
	public void populate(ResultSet rs) throws SQLException {
		setApplicationEuId(AbstractDAO.getInteger(rs, "application_eu_id"));
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result
				+ ((applicationEuId == null) ? 0 : applicationEuId.hashCode());
		result = prime * result
				+ ((emissionUnitTypeCd == null) ? 0 : emissionUnitTypeCd.hashCode());
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
		final NSRApplicationEUType other = (NSRApplicationEUType) obj;
		if (applicationEuId == null) {
			if (other.applicationEuId != null)
				return false;
		} else if (!applicationEuId.equals(other.applicationEuId))
			return false;
		if (emissionUnitTypeCd == null) {
			if (other.emissionUnitTypeCd != null)
				return false;
		} else if (!emissionUnitTypeCd.equals(other.emissionUnitTypeCd))
			return false;
		return true;
	}
	
	@Override
	public ValidationMessage[] validate() {
		return new ArrayList<ValidationMessage>(validationMessages.values())
				.toArray(new ValidationMessage[0]);
	}

}
