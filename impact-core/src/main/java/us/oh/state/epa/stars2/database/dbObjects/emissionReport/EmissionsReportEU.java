package us.oh.state.epa.stars2.database.dbObjects.emissionReport;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import us.oh.state.epa.stars2.database.dao.AbstractDAO;
import us.oh.state.epa.stars2.database.dbObjects.ValidationMessage;
import us.oh.state.epa.stars2.database.dbObjects.facility.EmissionUnit;
import us.oh.state.epa.stars2.def.EmissionCalcMethodDef;
import us.oh.state.epa.stars2.webcommon.reports.ReportProfileBase;

public class EmissionsReportEU extends EmissionUnit {
	
    private Integer emissionsRptId;
    private Boolean exemptEG71;
    private boolean excludedDueToAttributes;  // Exemption Status or TV Classification say excluded
    private boolean zeroEmissions;
    private boolean forceDetailedReporting;
    private transient boolean isPhaseOneBoiler;
    //private transient boolean noPeriods;  // No periods under the report EU (could be some under groups)
    private transient boolean noProcesses; // No processes under the profile EU.
    private List<EmissionsReportPeriod> periods = new ArrayList<EmissionsReportPeriod>(0);
    private HashMap<String, EmissionsReportEUGroup> euGroups = new HashMap<String, EmissionsReportEUGroup>(0);
    
    // NOT IN DATABASE
    // Used to indicate if the EU is not in Facility.
    private boolean notInFacility;
    // Used to tie together a reportEU (orig) and the reportEU (comp) it is
    // being compared to.
    // These are for temporary in-memory use and not on disk.
    private EmissionsReportEU orig; // points to the original reportEU object or
                                    // null
    private EmissionsReportEU comp; // points to the comparison reportEU object
                                    // or null.
    private boolean caution; // indicate if caution should be displayed on
                                // tree.

    public EmissionsReportEU() {
        super();
        orig = null;
        comp = null;
        caution = false;
        exemptEG71 = new Boolean(false);
        zeroEmissions = false;
        forceDetailedReporting = false;
        notInFacility = false; // default for normal display
    }

    public EmissionsReportEU(EmissionsReportEU old) {
        super(old);

        if (old != null) {
            setEmissionsRptId(old.getEmissionsRptId());
            setExemptEG71(old.getExemptEG71());
            setZeroEmissions(old.isZeroEmissions());
            setPeriods(old.getPeriods());
            setEuGroups(old.getEuGroups());
            setForceDetailedReporting(old.isForceDetailedReporting());
            setEmissionChoice(old.getEmissionChoice());
        }
    }
    
    public final List<ValidationMessage> submitVerify() {
        if(notInFacility || caution) {
            validationMessages.put("EU " + this.getEpaEmuId(),
                    new ValidationMessage("edit",
                            "Report Emission Unit: " +
                            this.getEpaEmuId() +
                            " is currently not valid",
                            ValidationMessage.Severity.ERROR, "unit:"
                            + ReportProfileBase.treeNodeId(this),
                            this.getEpaEmuId()));
        }
        return new ArrayList<ValidationMessage>(validationMessages.values());
    }
    
    public final boolean zeroEmissions() {
        boolean zero = true;
        for(EmissionsReportPeriod p : periods) {
            if(!p.zeroHours()) {
                zero = false;
                break;
            }
        }
        return zero;
    }
    
    public final HashMap<String, EmissionsReportEUGroup> getEuGroups() {
        return euGroups;
    }

    public final void addEuGroup(EmissionsReportEUGroup euGroup) {
        if (euGroup != null) {
            euGroups.put(euGroup.getReportEuGroupName(), euGroup);
        }
    }

    public void setEuGroups(
            HashMap<String, EmissionsReportEUGroup> euGroups) {
        if (euGroups == null) {
            this.euGroups = new HashMap<String, EmissionsReportEUGroup>(0);
        } else {
            this.euGroups = euGroups;
        }
    }

    public final void setPeriods(List<EmissionsReportPeriod> periods) {
        if (periods == null) {
            this.periods = new ArrayList<EmissionsReportPeriod>(0);
        } else {
            this.periods = periods;
        }
    }
    
    public final boolean nonEmptyPeriods() {
        // Determine if emissions are to be deleted
        boolean emissionsNeedClearing = false;
        // Are there emissions to remove.
        b: for(EmissionsReportPeriod p : this.getPeriods()) {
            if(p.anyValuesSet()) {
                emissionsNeedClearing = true;
                break b;
            }
            // If any emisions lines are filled out then don't automatically delete.
            // Note that if a pollutant is required by one of the reports and has its
            // method set to SCCEmissionsFactor with zero adjust -- will not look
            // like the user has entered values; but this is very minor.
            // Also when method is SCCEmissionsFactor, it will not be recognize
            // if the user has selected a single fire row from several choices.
            // Also, new pollutants are not recognized as added by user.
            for(Emissions e : p.getEmissions().values()) {
                if(e.getEmissionCalcMethodCd() != null
                        && !e.getEmissionCalcMethodCd().equals(EmissionCalcMethodDef.SCCEmissionsFactor)) {
                    emissionsNeedClearing = true;
                    break b;
                } else if(e.getEmissionCalcMethodCd() != null && e.getEmissionCalcMethodCd().equals(EmissionCalcMethodDef.SCCEmissionsFactor)
                        && !"0".equals(e.getAnnualAdjust())) {
                    emissionsNeedClearing = true;
                    break b;
                }
            }
        }
        return emissionsNeedClearing;
    }

    public final void clearPeriods() {
        this.periods = new ArrayList<EmissionsReportPeriod>(0);
    }

    public final void addPeriod(EmissionsReportPeriod period) {
        if (period != null) {
            periods.add(period);
        }
    }

    public final List<EmissionsReportPeriod> getPeriods() {
        return periods;
    }

    // Return the period with matching SCC code.
    public final EmissionsReportPeriod getPeriod(String sccId) {
        for (EmissionsReportPeriod p : periods) {
            if (p.getSccId() != null
                    && p.getSccId().equals(sccId))
                return p;
        }
        return null;
    }

    public final Integer getEmissionsRptId() {
        return emissionsRptId;
    }

    public final void setEmissionsRptId(Integer emissionsRptId) {
        this.emissionsRptId = emissionsRptId;
    }

    @Override
    public final void populate(ResultSet rs) {
        boolean foundAll = false;
        try {
            this.setCorrEpaEmuId(AbstractDAO.getInteger(rs, "corr_id"));
            setExemptEG71(AbstractDAO.translateIndicatorToBoolean(rs
                    .getString("exempt_by_EG71")));
            setEmissionsRptId(AbstractDAO.getInteger(rs, "emissions_rpt_id"));
            setLastModified(AbstractDAO.getInteger(rs, "rre_lm"));
            setZeroEmissions(AbstractDAO.translateIndicatorToBoolean(rs
                    .getString("zero_emissions")));
            setForceDetailedReporting(AbstractDAO.translateIndicatorToBoolean(rs
                    .getString("force_detailed_reporting")));
            foundAll = true;
            if (AbstractDAO.getInteger(rs, "emission_period_id") != null) {
                do {
                    EmissionsReportPeriod period = new EmissionsReportPeriod();

                    period.populate(rs);

                    periods.add(period);
                } while (rs.next());
            }
        } catch (SQLException sqle) {
            if(!foundAll) {
                logger.warn(sqle.getMessage());
            }
        }
    }

    @Override
    public int hashCode() {
        final int PRIME = 31;
        int result = super.hashCode();
        result = PRIME * result + ((emissionsRptId == null) ? 0 : emissionsRptId.hashCode());
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
        final EmissionsReportEU other = (EmissionsReportEU) obj;
        if (emissionsRptId == null) {
            if (other.emissionsRptId != null)
                return false;
        } else if (!emissionsRptId.equals(other.emissionsRptId))
            return false;
        return true;
    }

    public final EmissionsReportPeriod findPeriod(String scc) {
        EmissionsReportPeriod ret = null;
        for (EmissionsReportPeriod p : this.getPeriods()) {
            boolean isNull = false;
            if(null == p.getSccId()) {
                isNull = true;
            }
            if(isNull) {
                if(scc == null) {
                    ret = p;
                    break;
                }
            } else {
                if (scc != null && scc.equals(p.getSccId())) {
                    ret = p;
                    break;
                }
            }
        }
        return ret;
    }

    public final Boolean getExemptEG71() {
        return exemptEG71;
    }
    
    public final boolean isBelowRequirements() {
        return exemptEG71 && !forceDetailedReporting;
    }
    
    public final boolean isEg71OrZero() {
        return isBelowRequirements() || zeroEmissions;
    }

    public final void setExemptEG71(Boolean exemptEG71) {
        this.exemptEG71 = exemptEG71;
        if(exemptEG71) {
            // remove all period information if exempt.
            euGroups = new HashMap<String, EmissionsReportEUGroup>(0);
        }
    }

    public final EmissionsReportEU getComp() {
        return comp;
    }

    public final void setComp(EmissionsReportEU comp) {
        this.comp = comp;
    }

    public final EmissionsReportEU getOrig() {
        return orig;
    }

    public final void setOrig(EmissionsReportEU orig) {
        this.orig = orig;
    }

    public final boolean isCaution() {
        return caution;
    }

    public final void setCaution(boolean caution) {
        this.caution = caution;
    }

    public final boolean isNotInFacility() {
        return notInFacility;
    }

    public final void setNotInFacility(boolean notInFacility) {
        this.notInFacility = notInFacility;
    }
    
    private static final String[] phaseOne = {
        "0165000006", "B004", "0204010000", "B008", "0243160009", "B001",
        "0243160009", "B002", "0243160009", "B003", "0243160009", "B004",
        "0243160009", "B005", "0247030013", "B012", "0247080049", "B001",
        "0247080049", "B002", "0247080049", "B003", "0278060023", "B001",
        "0278060023", "B002", "0448010086", "B005", "0448010086", "B006",
        "0448010086", "B007", "0448010086", "B009", "0448010086", "B010",
        "0616000000", "B001", "0616000000", "B002", "0616000000", "B003",
        "0616000000", "B004", "0627000003", "B001", "0627000003", "B002",
        "0627000003", "B003", "0627000003", "B004", "0627000003", "B005",
        "0627010056", "B003", "0627010056", "B004", "0684000000", "B002",
        "0684000000", "B003", "0684000000", "B004", "0684000000", "B005",
        "0684000000", "B006", "0701000007", "B001", "0701000007", "B002",
        "0701000007", "B003", "0701000007", "B004", "1413100008", "B005",
        "1413100008", "B006", "1431350093", "B007", "1431350093", "B015",
        "1677010022", "B001", "1677010022", "B002", "0607130015", "B005",
        "0607130015", "B006", "0607130015", "B009", "0607130015", "B010",
        "0607130015", "B011", "0607130015", "B012", "0641050002", "B001",
        "0641050002", "B002", "0641160017", "B011", "0641160017", "B012",
        "0641160017", "B013", "0641800018", "B001", "0641800018", "B002",
        "0641800018", "B003", "0448020006", "X001", "0448020006", "X001",
        "0448020006", "X001", "0448020006", "B004", "0204000211", "B001",
        "0204000211", "B002", "0204000211", "B003", "0204000211", "B004",
        "0247030013", "B010", "0247030013", "B011", "0247030013", "B014",
        "0641050003", "B001", "0448010086", "B008", "1318000245", "B004",
        "0641050002", "B003", "0616000000", "B005", "0616000000", "B006",
        "1431350093", "B005", "1431350093", "B006", "0607130015", "B008",
        "0607130015", "B007", "0641160017", "B007", "0641160017", "B008",
        "1741160017", "B009", "0123000005", "B003", "1318000245", "B002",
        "1318000245", "B006", "0204010000", "B101", "0204010000", "B103",
        "0204010000", "B104"};
    
    public void setPhase1Boiler(String facility, Integer year) {
        /*
         * Per law, OAC 3745.11(C)(3), "the fees assessed do not apply
         *  to emissions from any electric generating unit designated as 
         *  a Phase I unit under Title IV prior to calendar year 2000."
         */
        isPhaseOneBoiler = false;
        if(year == null || year.intValue() > 1999) {
            return;
        }
        int i = 0;
        while (i < phaseOne.length) {
            if(facility.equals(phaseOne[i]) && this.getEpaEmuId() != null && this.getEpaEmuId().equals(phaseOne[i+1])) {
                isPhaseOneBoiler = true;
                return;
            }
            i = i + 2;
        }
        return;
    }
    
    public boolean isEg71WithEmissions() {
       //return !this.notInFacility && !this.noPeriods && this.isBelowRequirements();
        return !this.notInFacility && !this.isNoPeriods() && this.isBelowRequirements();
    }
    
    public boolean isNotOperWithEmissions() {
       // return !this.notInFacility && !this.noPeriods && this.zeroEmissions;
        return !this.notInFacility && !this.isNoPeriods() && this.zeroEmissions;
    }
    
    public boolean isShouldHaveProcesses() {
        return this.noProcesses && !this.isEg71OrZero();
    }
    
    public boolean isReportingNotNeeded() {
       //return this.notInFacility || (this.noProcesses && !this.isEg71OrZero()) || (!this.noPeriods && this.isEg71OrZero());
        return this.notInFacility || (this.noProcesses && !this.isEg71OrZero()) || (!this.isNoPeriods() && this.isEg71OrZero());
    }
    
    public boolean isInvoicingNeeded() {
         return !this.notInFacility && !this.noProcesses && !this.isEg71OrZero() && !this.isNoPeriods();
     }

    public boolean isPhaseOneBoiler() {
        return isPhaseOneBoiler;
    }

    public boolean isNoPeriods() {
        //return noPeriods;
        return periods == null || periods.size() == 0;
    }

//    public void setNoPeriods(boolean noPeriods) {
//        this.noPeriods = noPeriods;
//    }

    public boolean isNoProcesses() {
        return noProcesses;
    }

    public void setNoProcesses(boolean noProcesses) {
        this.noProcesses = noProcesses;
    }

    public boolean isZeroEmissions() {
        return zeroEmissions;
    }

    public void setZeroEmissions(boolean zeroEmissions) {
        this.zeroEmissions = zeroEmissions;
    }

//  emissionChoice; // 0 - zero, 1 - eg#71, 2 - emissions
    public Integer getEmissionChoice() {
        if(zeroEmissions) return new Integer(0);
        if(forceDetailedReporting) return new Integer(2);
        if(exemptEG71) return new Integer(1);
        return(new Integer(2));
    }

    public void setEmissionChoice(Integer emissionChoice) {
        if(emissionChoice == null) return;
        zeroEmissions = false;
        exemptEG71 = false;
        if(emissionChoice.intValue() == 0) {
            zeroEmissions = true;
        } else if(emissionChoice.intValue() == 1) {
            exemptEG71 = true;
        }
    }

    public boolean isForceDetailedReporting() {
        return forceDetailedReporting;
    }

    public void setForceDetailedReporting(boolean forceDetailedReporting) {
        this.forceDetailedReporting = forceDetailedReporting;
    }

    public boolean isExcludedDueToAttributes() {
        return excludedDueToAttributes;
    }

    public void setExcludedDueToAttributes(boolean excludedDueToAttributes) {
        this.excludedDueToAttributes = excludedDueToAttributes;
    }
}
