package us.oh.state.epa.stars2.webcommon.reports;

import java.sql.ResultSet;
import java.sql.SQLException;

import us.oh.state.epa.stars2.database.dbObjects.BaseDB;
import us.oh.state.epa.stars2.database.dbObjects.facility.EmissionProcess;
import us.oh.state.epa.stars2.database.dbObjects.facility.EmissionUnit;
import us.oh.state.epa.stars2.database.dbObjects.facility.Facility;
import us.oh.state.epa.stars2.database.dbObjects.serviceCatalog.SCNonChargePollutant;

public class MissingFIREFactor extends BaseDB {
	private EmissionProcess ep;
	private EmissionUnit eu;
	private Facility facility;

	public EmissionProcess getEp() {
		return ep;
	}

	public void setEp(EmissionProcess ep) {
		this.ep = ep;
	}

	public EmissionUnit getEu() {
		return eu;
	}

	public void setEu(EmissionUnit eu) {
		this.eu = eu;
	}

	public Facility getFacility() {
		return facility;
	}

	public void setFacility(Facility facility) {
		this.facility = facility;
	}

	@Override
	public void populate(ResultSet rs) throws SQLException {
		// TODO Auto-generated method stub

	}

	@Override
	public final boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (getClass() != obj.getClass())
			return false;
		
		String facId = facility.getFacilityId();
		Integer corrEpaEmuId = eu.getCorrEpaEmuId();
		String sccId = ep.getSccId();
		
		final MissingFIREFactor other = (MissingFIREFactor) obj;
		String otherFacId = other.getFacility().getFacilityId();
		Integer otherCorrEpaEmuId = other.getEu().getCorrEpaEmuId();
		String otherSccId = other.getEp().getSccId();
		
		boolean equal = facId.equals(otherFacId) && corrEpaEmuId
				.equals(otherCorrEpaEmuId)
				&& sccId.equals(otherSccId);
		if (!equal){
			return false;
		}
		return true;
	}

}
