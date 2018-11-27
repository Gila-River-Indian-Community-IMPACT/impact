package us.oh.state.epa.stars2.database.dbObjects.ceta;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import us.oh.state.epa.stars2.database.dao.AbstractDAO;
import us.oh.state.epa.stars2.database.dbObjects.BaseDB;
import us.oh.state.epa.stars2.database.dbObjects.ValidationMessage;
import us.oh.state.epa.stars2.database.dbObjects.facility.PollutantCompCode;
import us.oh.state.epa.stars2.def.ComplianceStatusDef;

@SuppressWarnings("serial")
public class FacilityHistory extends BaseDB {
	private Integer facilityHistId;
    private String airProgramCd;
	private String airProgramCompCd;
	private String sipCompCd;
	private boolean mact;
	private String mactCompCd;
	private boolean neshaps;
	private ArrayList<FacilityComplianceStatus> neshapsCompStatusList = new ArrayList<FacilityComplianceStatus>();
	private boolean nsps;
	private ArrayList<FacilityComplianceStatus> nspsCompStatusList = new ArrayList<FacilityComplianceStatus>();
	private boolean psd;
	private ArrayList<FacilityComplianceStatus> psdCompStatusList = new ArrayList<FacilityComplianceStatus>();
	private boolean nsrNonAttainment;
	private ArrayList<FacilityComplianceStatus> nsrNonAttainmentCompStatusList = new ArrayList<FacilityComplianceStatus>();
	private Timestamp startDate;
	private Timestamp endDate;
	
	    
    public ValidationMessage[] validate() {
        validationMessages = new HashMap<String, ValidationMessage>(1); // clear messages out
        checkFacilityTable(neshaps, "NESHAPS", "NESHAPS Subparts", neshapsCompStatusList);
        //checkFacilityTable(nsps, "NSPS", "NSPS Subparts", nspsCompStatusList);
        //checkFacilityTable(nsps, "NSPS", "NSPS Pollutants", nspsCompStatusList);
        //checkFacilityTable(psd, "PSD", "PSD Pollutants", psdCompStatusList);
        //checkFacilityTable(nsrNonAttainment, "Non-attainment NSR", "Non-attainment NSR Pollutants", nsrNonAttainmentCompStatusList);
        return new ArrayList<ValidationMessage>(validationMessages.values())
        .toArray(new ValidationMessage[0]);
    }
    
    private void checkFacilityTable(boolean subject, String fieldName,
            String tableName, List<FacilityComplianceStatus> tableVals) {
        ValidationMessage validationMessage;

        if (subject) {
            if (tableVals.size() == 0) {
                validationMessage = new ValidationMessage(fieldName,
                        "Subject to : " + fieldName
                                + " is checked; Table : " + tableName
                                + " must have at least one entry.",
                        ValidationMessage.Severity.ERROR, "facility");
                validationMessages.put(tableName, validationMessage);
                return;
            }

            validationMessages.remove(tableName);
            checkFacilityTable(fieldName, tableName, tableVals);
        }

    }

    private void checkFacilityTable(String fieldName, String tableName,
            List<FacilityComplianceStatus> tableVals) {
        HashSet<String> values = new HashSet<String>(tableVals.size());
        ValidationMessage validationMessage;

        for (FacilityComplianceStatus temp : tableVals) {
            if (temp == null || temp.getPollutantCd() == null || temp.getPollutantCd().equals("")
                    || temp.getComplianceCd() == null || temp.getComplianceCd().equals("")) {
                validationMessage = new ValidationMessage(fieldName,
                        "empty value in a row of the " + tableName + " table",
                        ValidationMessage.Severity.ERROR, "facilityHistory");
                validationMessages.put(fieldName, validationMessage);
                return;
            } else {
                if (values.contains(temp.getPollutantCd())) {
                    validationMessage = new ValidationMessage(fieldName,
                            "Duplicate value in " + tableName + " table",
                            ValidationMessage.Severity.ERROR, "facility");
                    validationMessages.put(fieldName, validationMessage);
                    return;
                } else {
                    values.add(temp.getPollutantCd());
                }
            }
            validationMessages.remove(fieldName);
        }
    }

	
	public final Integer getFacilityHistId() {
		return facilityHistId;
	}

	public final void setFacilityHistId(Integer facilityHistId) {
		this.facilityHistId = facilityHistId;
	}

	public final String getAirProgramCompCd() {
		return airProgramCompCd;
	}

	public final void setAirProgramCompCd(String airProgramCompCd) {
		this.airProgramCompCd = airProgramCompCd;
	}

	public final String getSipCompCd() {
		return sipCompCd;
	}

	public final void setSipCompCd(String sipCompCd) {
		this.sipCompCd = sipCompCd;
	}

	public final boolean isMact() {
		return mact;
	}

	public final void setMact(boolean mact) {
		this.mact = mact;
	}

	public final boolean isNeshaps() {
		return neshaps;
	}

	public final void setNeshaps(boolean neshaps) {
		this.neshaps = neshaps;
        if(!neshaps) neshapsCompStatusList = new ArrayList<FacilityComplianceStatus>();
	}

	public final boolean isNsps() {
		return nsps;
	}

	public final void setNsps(boolean nsps) {
		this.nsps = nsps;
        if(!nsps) nspsCompStatusList = new ArrayList<FacilityComplianceStatus>();
	}

	public final boolean isPsd() {
		return psd;
	}

	public final void setPsd(boolean psd) {
		this.psd = psd;
        if(!psd) psdCompStatusList = new ArrayList<FacilityComplianceStatus>();
	}

	public final String getMactCompCd() {
		return mactCompCd;
	}

	public final void setMactCompCd(String mactCompCd) {
		this.mactCompCd = mactCompCd;
	}

	public final List<FacilityComplianceStatus> getNeshapsCompStatusList() {
		if (neshapsCompStatusList == null) {
			neshapsCompStatusList = new ArrayList<FacilityComplianceStatus>();
		}
		return neshapsCompStatusList;
	}

	public final void setNeshapsCompStatusList(
			List<FacilityComplianceStatus> neshapsCompStatusList) {
		if (this.neshapsCompStatusList == null) {
			this.neshapsCompStatusList = new ArrayList<FacilityComplianceStatus>();
		}
		if (neshapsCompStatusList != null) {
			this.neshapsCompStatusList.addAll(neshapsCompStatusList);
		}
	}

	public final List<FacilityComplianceStatus> getNspsCompStatusList() {
		if (nspsCompStatusList == null) {
			nspsCompStatusList = new ArrayList<FacilityComplianceStatus>();
		}
		return nspsCompStatusList;
	}

	public final void setNspsCompStatusList(
			List<FacilityComplianceStatus> nspsCompStatusList) {
		if (this.nspsCompStatusList == null) {
			this.nspsCompStatusList = new ArrayList<FacilityComplianceStatus>();
		}
		if (nspsCompStatusList != null) {
			this.nspsCompStatusList.addAll(nspsCompStatusList);
		}
	}

	public final List<FacilityComplianceStatus> getPsdCompStatusList() {
		if (psdCompStatusList == null) {
			psdCompStatusList = new ArrayList<FacilityComplianceStatus>();
		}
		return psdCompStatusList;
	}

	public final void setPsdCompStatusList(
			List<FacilityComplianceStatus> psdCompStatusList) {
		if (this.psdCompStatusList == null) {
			this.psdCompStatusList = new ArrayList<FacilityComplianceStatus>();
		}
		if (psdCompStatusList != null) {
			this.psdCompStatusList.addAll(psdCompStatusList);
		}
	}

	public final boolean isNsrNonAttainment() {
		return nsrNonAttainment;
	}

	public final void setNsrNonAttainment(boolean nsrNonAttainment) {
		this.nsrNonAttainment = nsrNonAttainment;
        if(!nsrNonAttainment) nsrNonAttainmentCompStatusList = new ArrayList<FacilityComplianceStatus>();
	}

	public final List<FacilityComplianceStatus> getNsrNonAttainmentCompStatusList() {
		if (nsrNonAttainmentCompStatusList == null) {
			nsrNonAttainmentCompStatusList = new ArrayList<FacilityComplianceStatus>();
		}
		return nsrNonAttainmentCompStatusList;
	}

	public final void setNsrNonAttainmentCompStatusList(
			List<FacilityComplianceStatus> nsrNonAttainmentCompStatusList) {
		if (this.nsrNonAttainmentCompStatusList == null) {
			this.nsrNonAttainmentCompStatusList = new ArrayList<FacilityComplianceStatus>();
		}
		if (nsrNonAttainmentCompStatusList != null) {
			this.nsrNonAttainmentCompStatusList.addAll(nsrNonAttainmentCompStatusList);
		}
	}

	public final Timestamp getStartDate() {
		return startDate;
	}

	public final void setStartDate(Timestamp startDate) {
		this.startDate = startDate;
	}

	public final Timestamp getEndDate() {
		return endDate;
	}

	public final void setEndDate(Timestamp endDate) {
		this.endDate = endDate;
	}

	
	public void populate(ResultSet rs) throws SQLException {
		setFacilityHistId(AbstractDAO.getInteger(rs, "facility_hist_id"));
		setAirProgramCompCd(rs.getString("air_program_comp_cd"));
        setAirProgramCd(rs.getString("air_program_cd"));
		setSipCompCd(rs.getString("sip_comp_cd"));
		setMact(AbstractDAO.translateIndicatorToBoolean(rs.getString("mact_ind")));
		setMactCompCd(rs.getString("mact_comp_cd"));
		setNeshaps(AbstractDAO.translateIndicatorToBoolean(rs.getString("neshaps_ind")));
		setNsps(AbstractDAO.translateIndicatorToBoolean(rs.getString("nsps_ind")));
		setNsrNonAttainment(AbstractDAO.translateIndicatorToBoolean(rs.getString("nsr_nonattainment_ind")));
		setPsd(AbstractDAO.translateIndicatorToBoolean(rs.getString("psd_ind")));
		setStartDate(rs.getTimestamp("start_date"));
		setEndDate(rs.getTimestamp("end_date"));
		setLastModified(AbstractDAO.getInteger(rs, "fac_hist_lm"));
	}

	public final String getAirProgramCd() {
		return airProgramCd;
	}

	public final void setAirProgramCd(String airProgramCd) {
		this.airProgramCd = airProgramCd;
	}
    
    public boolean allInCompliance() {
        if(airProgramCd != null) { // must be TV or SMTV
            if (airProgramCompCd == null || ComplianceStatusDef.NO.equals(airProgramCompCd)) {
                return false;
            }
        }
        if (sipCompCd == null || ComplianceStatusDef.NO.equals(sipCompCd)) {
            return false;
        }
        if (mact && (mactCompCd == null || ComplianceStatusDef.NO.equals(mactCompCd))) {
            return false;
        }
        if (neshaps) {
            for (FacilityComplianceStatus neshapsSubpart : this.getNeshapsCompStatusList()) {
                if (neshapsSubpart.getComplianceCd() == null || ComplianceStatusDef.NO.equals(neshapsSubpart.getComplianceCd())) {
                    return false;
                }
            }
        }
        if (nsps) {
            for (FacilityComplianceStatus nspsPollutant : this.getNspsCompStatusList()) {
                if (nspsPollutant.getComplianceCd() == null || ComplianceStatusDef.NO.equals(nspsPollutant.getComplianceCd())) {
                    return false;
                }
            }
        }
        if (psd) {
            for (FacilityComplianceStatus psdPollutant : this.getPsdCompStatusList()) {
                if (psdPollutant.getComplianceCd() == null || ComplianceStatusDef.NO.equals(psdPollutant.getComplianceCd())) {
                    return false;
                }
            }
        }
        if (nsrNonAttainment) {
            for (FacilityComplianceStatus nsrPollutant : this.getNspsCompStatusList()) {
                if (nsrPollutant.getComplianceCd() == null || ComplianceStatusDef.NO.equals(nsrPollutant.getComplianceCd())) {
                    return false;
                }
            }
        }
        return true;
    }
}
