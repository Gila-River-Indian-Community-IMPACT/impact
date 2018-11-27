package us.oh.state.epa.stars2.webcommon.reports;

import us.oh.state.epa.stars2.database.dbObjects.emissionReport.FacilityYearPair;
import us.oh.state.epa.stars2.def.OperatingStatusDef;

public class AutoGenRow {
    private String facilityId;
    private int fpId;
    private String facilityName;
    private String facilityOpState;
    private Integer prevRptId;
    private String prevRptCat;
    private String prevRptTons;
    private String genYears;
    private int evenYear = 0;
    private int oddYear = 0;
    private String genRanges;
    private Integer evenFeeId;
    private Integer oddFeeId;
    private boolean canGenerate;  // set checkbox to true
    private boolean hasBeenGenerated; // set after generation
    private boolean isDuplicate;
    
    public AutoGenRow(FacilityYearPair fyp, boolean evenSkip, int evenYear, boolean oddSkip, int oddYear) {
        facilityId = fyp.getFacilityId();
        facilityOpState = fyp.getFacilityOpState();

        
        String yearStr = null;
        if(!evenSkip) {
            this.evenYear = evenYear;
            yearStr = Integer.toString(evenYear);
        }
        if(!oddSkip) {
            this.oddYear = oddYear;
            if(yearStr == null) {
                yearStr = Integer.toString(oddYear);
            } else {
                yearStr = yearStr + "-" + Integer.toString(oddYear);
            }
        }
        genYears = yearStr;
    }
    
    public void addRptInfo(Integer prevRptId, String rptCat, String facilityName, int fpId, String emissions,
            String newEmissions, Integer evenFeeId, Integer oddFeeId, boolean autoGen) {
        this.prevRptId = prevRptId;
        prevRptCat = rptCat;
        this.facilityName = facilityName;
        this.fpId = fpId;
        prevRptTons = emissions;
        this.evenFeeId = evenFeeId;
        this.oddFeeId = oddFeeId;
        genRanges = newEmissions;
        canGenerate = autoGen;
        if(facilityOpState.equals(OperatingStatusDef.SD)) {
            canGenerate = false;
        }
    }

    public boolean isCanGenerate() {
        return canGenerate;
    }

    public String getFacilityId() {
        return facilityId;
    }

    public String getFacilityName() {
        return facilityName;
    }

    public String getPrevRptCat() {
        return prevRptCat;
    }

    public Integer getPrevRptId() {
        return prevRptId;
    }

    public String getPrevRptTons() {
        return prevRptTons;
    }

    public String getGenRanges() {
        return genRanges;
    }

    public String getGenYears() {
        return genYears;
    }

    public int getEvenYear() {
        return evenYear;
    }

    public int getOddYear() {
        return oddYear;
    }

    public Integer getEvenFeeId() {
        return evenFeeId;
    }

    public Integer getOddFeeId() {
        return oddFeeId;
    }

    public void setCanGenerate(boolean canGenerate) {
        this.canGenerate = canGenerate;
    }

    public int getFpId() {
        return fpId;
    }

    public void setFpId(int fpId) {
        this.fpId = fpId;
    }

    public String getFacilityOpState() {
        return facilityOpState;
    }

    public void setFacilityOpState(String facilityOpState) {
        this.facilityOpState = facilityOpState;
    }

    public boolean isHasBeenGenerated() {
        return hasBeenGenerated;
    }

    public void setHasBeenGenerated(boolean hasBeenGenerated) {
        if(hasBeenGenerated) {
            canGenerate = false;
        }
        this.hasBeenGenerated = hasBeenGenerated;
    }

    public boolean isDuplicate() {
        return isDuplicate;
    }

    public void setDuplicate(boolean isDuplicate) {
        this.isDuplicate = isDuplicate;
    }
}