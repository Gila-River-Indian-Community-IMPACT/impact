package us.oh.state.epa.stars2.app.ceta;

import java.rmi.RemoteException;
import java.sql.Date;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.ListIterator;

import us.oh.state.epa.stars2.bo.FullComplianceEvalService;
import us.oh.state.epa.stars2.database.dbObjects.ValidationMessage;
import us.oh.state.epa.stars2.database.dbObjects.ceta.CetaBaseDB;
import us.oh.state.epa.stars2.database.dbObjects.ceta.FceScheduleRow;
import us.oh.state.epa.stars2.def.FacilityTypeDef;
import us.oh.state.epa.stars2.def.PermitClassDef;
import us.oh.state.epa.stars2.def.SystemPropertyDef;
import us.oh.state.epa.stars2.def.YearsForFCEs;
import us.oh.state.epa.stars2.webcommon.DisplayUtil;
import us.oh.state.epa.stars2.webcommon.FacesUtil;
import us.oh.state.epa.stars2.webcommon.ValidationBase;

/*
 * Backing bean for Inspection/Site Visit work.
 */

public class FceSchedules extends ValidationBase {

	private static final long serialVersionUID = -7109207174357418762L;

	private String facilityId;
    private String facilityName;
    private String doLaaCd;
    private String countyCd;
    private boolean schedOrNot = false;  // Also show (or not) already scheduled Inspections
    private boolean nonCommitted = true; // THIS IS NO LONGER USED
	private boolean hasSearchResults;
    protected String popupRedirectOutcome;
    private List<FceScheduleRow> scheduleList;
    private FceScheduleRow lastAddedBeforeSave = null;

    private boolean disclosed = true;
    private HashMap<String, LinkedHashMap<String, Timestamp>> maps;
    private HashMap<Integer, LinkedHashMap<String, Timestamp>> oldMaps;
    private LinkedHashMap<String, Integer> choiceOfFfy;
    private int nextFfy;  // default for what FFY to view
    
    private String facilityTypeCd;
    private String permitClassCd;
       
    private boolean editable  = false;
    
    private int displayRows = getPageLimit();
    private boolean showDisplayAll = true;
    private boolean showDisplaySome = false;
    private int numRows = 0;
    private boolean firstTime = true;
    private int firstTimeFfy;  // for default number of years.
    private Integer showThruFfy;
    private int inspectionFrequency;
    private int maxYearsOut = YearsForFCEs.maxYearsOut;
    
    private int rowStart = 0;
    private int addAnotherLikeRowNum;  // set from jsp page to indicate what row to duplicate
    private String howToHelp = "";
    
	private FullComplianceEvalService fullComplianceEvalService;
	
	private String cmpId;
	
	public FullComplianceEvalService getFullComplianceEvalService() {
		return fullComplianceEvalService;
	}

	public void setFullComplianceEvalService(
			FullComplianceEvalService fullComplianceEvalService) {
		this.fullComplianceEvalService = fullComplianceEvalService;
	}
    
    public FceSchedules() {
        super();
    }

    public String start() {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.YEAR, cal.get(Calendar.YEAR) + 1);
        nextFfy = getFfy(new Timestamp(cal.getTimeInMillis()));
        choiceOfFfy = new LinkedHashMap<String, Integer>();
        for(int i = nextFfy; i < nextFfy + maxYearsOut; i++) {
            choiceOfFfy.put(Integer.toString(i), i);
        }
        if(firstTime) {
            scheduleList = new ArrayList<FceScheduleRow>();
            hasSearchResults = false;
            
            reset();
            firstTime = false;
        }
        
        howToHelp = SystemPropertyDef.getSystemPropertyValue("FCE_SCHEDULING_TOOL_HELP", null);
        
        // set values to see if in past.
        cal = Calendar.getInstance();
        Timestamp cd = new Timestamp(cal.getTimeInMillis());
        FceScheduleRow.setCurrentDate(cd);
        FceScheduleRow.setCurrentFFY(Integer.parseInt(CetaBaseDB.getScheduledFFY(cd)));
        return "ceta.fceSchedule";
    }
    
    public final String submitSearch() {
        lastAddedBeforeSave = null;
        scheduleList = new ArrayList<FceScheduleRow>();
        hasSearchResults = false;
        boolean hideDeistrict = SystemPropertyDef.getSystemPropertyValueAsBoolean("hideDistrict", false);
        if (hideDeistrict){
        	doLaaCd = SystemPropertyDef.getSystemPropertyValue("districtCd", null);
        }
        if(doLaaCd == null) {
        	if (getFacilityId() == null && getFacilityName() == null && getCountyCd() == null && facilityTypeCd == null && permitClassCd == null)
        	{
        		ValidationMessage[] validationMessages = new ValidationMessage[1];
        		validationMessages[0] = new ValidationMessage(
        				"dolaa", "A District must be selected",
        				ValidationMessage.Severity.ERROR, null);
        		displayValidationMessages("", validationMessages);
        		return "ceta.fceSchedule";
        	}
        }
        
        try {
            scheduleList = getFullComplianceEvalService().needToSchedFce(facilityId, facilityName,
                    doLaaCd, countyCd, permitClassCd, facilityTypeCd, schedOrNot, showThruFfy, nonCommitted, cmpId);
            if (scheduleList != null) {
                hasSearchResults = true;
                numRows = scheduleList.size();
                Calendar cal = Calendar.getInstance();
                firstTimeFfy = cal.get(Calendar.YEAR) + maxYearsOut;
                maps = new HashMap<String, LinkedHashMap<String, Timestamp>>();
                oldMaps = new HashMap<Integer, LinkedHashMap<String, Timestamp>>();
                for(FceScheduleRow r : scheduleList) {
                    Integer y = r.getNeededBy();
                    // Use maxYearsOut years if there is no first Inspection and if we do not know when needed by
                    if(r.getScheduledFceId() == null && y == null) {
                        y = firstTimeFfy;
                    }
                    LinkedHashMap<String, Timestamp> map = null;
                    if (y <= cal.get(Calendar.YEAR))
                    	map = getScheduleChoices();
                    else
                    	map = getScheduleChoices(null, y);
                    r.setPickListSchedule(map);
             /*       if(r.getNextScheduled() != null) {
                        map = getOldScheduleChoices(getFfy(r.getNextScheduled()));
                        r.setPickListSchedule(map);
                    }*/
                }

                // Number the rows.
                rowStart = 0;
                FceScheduleRow r = null;
                ListIterator<FceScheduleRow> it = scheduleList.listIterator();
                while(it.hasNext()) {
                    r = it.next();
                    r.setRowNum(rowStart++);
                }
            }
        } catch (RemoteException e) {
            handleException(e);
        }
        return "ceta.fceSchedule";
    }
    
    //  Return Federal Fiscal Year.  It is the year that includes September
    static int getFfy(Timestamp ts) {
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(ts.getTime());
        if(cal.get(Calendar.MONTH) < Calendar.OCTOBER) {
            return cal.get(Calendar.YEAR);
        } else {
            return cal.get(Calendar.YEAR) + 1;
        }
    }
    
    //   Keep within the FFY already set
    public LinkedHashMap<String, Timestamp> getOldScheduleChoices(Integer ffy) {
        if(oldMaps.containsKey(ffy)) {
            return oldMaps.get(ffy);
        }
        LinkedHashMap<String, Timestamp> scheds = new LinkedHashMap<String, Timestamp>();
        if(ffy != null) {
            Calendar calFfy = Calendar.getInstance();
            calFfy.set(Calendar.YEAR, ffy);
            calFfy.set(Calendar.MONTH, Calendar.OCTOBER);
            calFfy.set(Calendar.DAY_OF_MONTH, 1);
            calFfy.set(Calendar.HOUR_OF_DAY, 0);
            calFfy.set(Calendar.MINUTE, 0);
            calFfy.set(Calendar.SECOND, 0);
            calFfy.set(Calendar.MILLISECOND, 0);
            Timestamp refFfy = new Timestamp(calFfy.getTimeInMillis());
            refFfy.setNanos(0);
 
            calFfy.set(Calendar.YEAR, ffy - 1);
            calFfy.set(Calendar.MONTH, Calendar.OCTOBER);
            calFfy.set(Calendar.DAY_OF_MONTH, 1);
            calFfy.set(Calendar.HOUR_OF_DAY, 0);
            calFfy.set(Calendar.MINUTE, 0);
            calFfy.set(Calendar.SECOND, 0);
            calFfy.set(Calendar.MILLISECOND, 0);
            // ref date is first second of the period
            Timestamp ref = new Timestamp(calFfy.getTimeInMillis());
            ref.setNanos(0);
            while(ref.before(refFfy)) {
                scheds.put(CetaBaseDB.getScheduled(ref), ref);
                calFfy.add(Calendar.MONTH, 3);
                ref = new Timestamp(calFfy.getTimeInMillis());
            }
        }
        oldMaps.put(ffy, scheds);
        return scheds;
    }
    
    // All quarters from now until last quarter in FFY specified.
    public LinkedHashMap<String, Timestamp> getScheduleChoices(Timestamp beginDate, Integer ffy) {
        Calendar cal = Calendar.getInstance();
        if(beginDate != null) {
            cal.setTimeInMillis(beginDate.getTime());
        }
        int month = cal.get(Calendar.MONTH)/3 * 3;
        cal.set(Calendar.MONTH, month);
        cal.set(Calendar.DAY_OF_MONTH, 1);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        // ref date is first second of the period
        Timestamp ref = new Timestamp(cal.getTimeInMillis());
        ref.setNanos(0);
        String key = "K" + cal.get(Calendar.MONTH) + "/"+ cal.get(Calendar.YEAR) + ":" + ffy;
        if(maps.containsKey(key)) {
            return maps.get(key);
        }
        LinkedHashMap<String, Timestamp> scheds = new LinkedHashMap<String, Timestamp>();
        if(ffy != null) {
            Calendar calFfy = Calendar.getInstance();
            calFfy.set(Calendar.YEAR, ffy);
            calFfy.set(Calendar.MONTH, Calendar.OCTOBER);
            calFfy.set(Calendar.DAY_OF_MONTH, 1);
            calFfy.set(Calendar.HOUR_OF_DAY, 0);
            calFfy.set(Calendar.MINUTE, 0);
            calFfy.set(Calendar.SECOND, 0);
            calFfy.set(Calendar.MILLISECOND, 0);
            Timestamp refFfy = new Timestamp(calFfy.getTimeInMillis());
            refFfy.setNanos(0);
 
            cal.setTime(new Date(ref.getTime()));
            if(!ref.before(refFfy)) {
                // Required in the past.
                // Go from ffy to one year beyond current.
                ref = refFfy;
                cal = calFfy;
                calFfy = Calendar.getInstance();
                calFfy.add(Calendar.YEAR, 1);
                month = calFfy.get(Calendar.MONTH)/3 * 3;
                calFfy.set(Calendar.MONTH, month);
                calFfy.set(Calendar.DAY_OF_MONTH, 1);
                calFfy.set(Calendar.HOUR_OF_DAY, 0);
                calFfy.set(Calendar.MINUTE, 0);
                calFfy.set(Calendar.SECOND, 0);
                calFfy.set(Calendar.MILLISECOND, 0);
                refFfy = new Timestamp(calFfy.getTimeInMillis());
                refFfy.setNanos(0);
            } 
            while(ref.before(refFfy)) {
                scheds.put(CetaBaseDB.getScheduled(ref), ref);
                cal.add(Calendar.MONTH, 3);
                ref = new Timestamp(cal.getTimeInMillis());
            }
        }
        maps.put(key, scheds);
        return scheds;
    }
    
    public LinkedHashMap<String, Timestamp> getScheduleChoices() {
        LinkedHashMap<String, Timestamp> scheds = new LinkedHashMap<String, Timestamp>();
        Calendar cal = Calendar.getInstance();
        int month = cal.get(Calendar.MONTH)/3 * 3;
        cal.set(Calendar.MONTH, month);
        cal.set(Calendar.DAY_OF_MONTH, 1);
        cal.set(Calendar.HOUR_OF_DAY, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MILLISECOND, 0);
		Timestamp ref = new Timestamp(cal.getTimeInMillis());
		ref.setNanos(0);
		cal.setTime(new Date(ref.getTime()));
		for (int i = 0; i < 40; i++) {
			Timestamp use = ref;
			scheds.put(CetaBaseDB.getScheduled(ref), use);
			cal.add(Calendar.MONTH, 3);
			ref = new Timestamp(cal.getTimeInMillis());
		}
		return scheds;
    }

    public final String reset() {
    	facilityId = null;
        facilityName = null;
        doLaaCd = null;
        countyCd = null;
        facilityTypeCd = null;
        permitClassCd = null;
        schedOrNot = false;
        showThruFfy = nextFfy + maxYearsOut - 1;
        nonCommitted = false;
    	hasSearchResults = false;
    	cmpId = null;
    	return "ceta.fceSchedule";
    }
    
    public final String edit() {
        editable = true;
        return "ceta.fceSchedule";
    }
    
    public final String cancel() {
        editable = false;
        submitSearch();
        return "ceta.fceSchedule";
    }
    
    public final String addAnother() {
        // add another row to result;  called from jsp.
        ListIterator<FceScheduleRow> iter = scheduleList.listIterator();
        while(iter.hasNext()) {
            FceScheduleRow fsr = iter.next();
            if(fsr.getRowNum() == addAnotherLikeRowNum) {
            	
                if(fsr.getCompletedFceId() == null && (fsr.getAssignedStaff() == null || fsr.getNextScheduled() == null)) {
                    // cannot create another one until current one is completed.
                    DisplayUtil.displayError("Both Next Scheduled and Assigned Staff must be specified before another can be added.");
                    break;
                }
                // before creating next row, adjust pick list of last created one
                if(lastAddedBeforeSave != null && lastAddedBeforeSave.getNextScheduled() !=null) {
                    lastAddedBeforeSave.setPickListSchedule(getOldScheduleChoices(getFfy(lastAddedBeforeSave.getNextScheduled())));
                }
                FceScheduleRow r = new FceScheduleRow();
                r.setRowNum(rowStart++);
                r.setFacilityId(fsr.getFacilityId());
                r.setFacilityName(fsr.getFacilityName());
                r.setCountyCd(fsr.getCountyCd());
                r.setOperatingStatusCd(fsr.getOperatingStatusCd());
                r.setFacilityTypeCd(fsr.getFacilityTypeCd());
                r.setPermitClassCd(fsr.getPermitClassCd());
                r.setNextScheduled(null);
                r.setAssignedStaff(null);
                fsr.setAllowAddAnother(false);
                
                Integer nextNeededFfy = 0;
                int currentFfy;
                Timestamp currentTimestamp = null;
                if(fsr.getNextScheduled() != null) {
                    currentFfy = getFfy(fsr.getNextScheduled());
                    currentTimestamp = fsr.getNextScheduled();
                } else {
                    currentFfy = fsr.getNeededBy();
                    currentTimestamp = fsr.getCompletedScheduledTimestamp();
                }
                if(PermitClassDef.TV.equals(r.getPermitClassCd())) {
                	inspectionFrequency = Integer.parseInt(FacilityTypeDef.getTvInspectionFrequency(r.getFacilityTypeCd()));
                    nextNeededFfy = currentFfy + inspectionFrequency;
                    r.setNeededBy(nextNeededFfy);
                } else if(PermitClassDef.SMTV.equals(r.getPermitClassCd())) {
                	inspectionFrequency = Integer.parseInt(FacilityTypeDef.getSmtvInspectionFrequency(r.getFacilityTypeCd()));
                    nextNeededFfy = currentFfy + inspectionFrequency;
                    r.setNeededBy(nextNeededFfy);
                } else if(PermitClassDef.NTV.equals(r.getPermitClassCd())) {
                	inspectionFrequency = Integer.parseInt(FacilityTypeDef.getSmtvInspectionFrequency(r.getFacilityTypeCd()));
                    nextNeededFfy = currentFfy + inspectionFrequency;
                    r.setNeededBy(nextNeededFfy);
                }
                LinkedHashMap<String, Timestamp> map = getScheduleChoices(currentTimestamp, nextNeededFfy); // need beginning date
                r.setPickListSchedule(map);
                r.setAllowAddAnother(true);  // Allow creating multiple Inspections within the same Edit
                if(nextNeededFfy > nextFfy + maxYearsOut) {
                    DisplayUtil.displayError("Not allowed to schedule beyond " + (nextFfy + maxYearsOut) + ".");
                    rowStart--;
                    break;
                }
                iter.add(r);
                lastAddedBeforeSave = r;  // keep track of last added to adjust its picklist
                break;
            }
        }
        return null;  // re-render page
    }
    
    public final String save() {
    	
        // check for errors
        boolean errorsExisting = false;
        for (FceScheduleRow r : scheduleList) {
        	
            if (r.isLocked()) continue;  // this row is blank.
            
            if (r.getScheduledFceId() == null) {  // new ones

            	int cnt = 0;
                r.setFlagErrorExisting(false);
                if (r.getAssignedStaff() != null) cnt++;
                if (r.getNextScheduled() != null) cnt++;
                if (cnt == 1) {
                    r.setFlagErrorExisting(true);
                    errorsExisting = true;
                }

            } else { // old ones

            	if (r.getAssignedStaff() == null) {
                    if (r.getUnchangedAssignedStaff() != null) {
                        r.setChanged(true);
                    }

            	} else if (!r.getAssignedStaff().equals(r.getUnchangedAssignedStaff())) {
                    r.setChanged(true);
                }

                if (r.getNextScheduled() == null) {
                    if (r.getUnchangedNextScheduled() != null) {
                        r.setChanged(true);
                    }
                } else if (!r.getNextScheduled().equals(r.getUnchangedNextScheduled())) {
                    r.setChanged(true);
                }
                
                if (r.isChanged()) { // is change legal
                    if (r.getAssignedStaff() == null || r.getNextScheduled() == null) {
                        r.setFlagErrorExisting(true);
                        errorsExisting = true;
                    }
                }
                
                if (r.isUnchangedScheduledUsEpaCommitted() != r.isScheduledUsEpaCommitted()) {
                    r.setChanged(true);
                }

            }
        }

        if (errorsExisting) {
            DisplayUtil.displayError("Select both Next Scheduled and Assigned Staff.");
            return "ceta.fceSchedule";
        }
        try {
            int cnt = getFullComplianceEvalService().updateFceSched(scheduleList);
            DisplayUtil.displayInfo(cnt + " Inspections created/modified successfully");
        } catch (RemoteException e) {
            handleException(e);
        }
        editable = false;
        submitSearch();
        return "ceta.fceSchedule";

    }

    public final String getFacilityId() {
        return facilityId;
    }
    
    public final void  setFacilityId(String facilityId) {
    	this.facilityId = facilityId;
    }

	public final boolean isHasSearchResults() {
		return hasSearchResults;
	}

	public final void setHasSearchResults(boolean hasSearchResults) {
		this.hasSearchResults = hasSearchResults;
	}

	public final String getPopupRedirectOutcome() {
		return popupRedirectOutcome;
	}

	public final void setPopupRedirectOutcome(String popupRedirectOutcome) {
		this.popupRedirectOutcome = popupRedirectOutcome;
	}
	
    public final String getPopupRedirect() {
        if (popupRedirectOutcome != null) {
            FacesUtil.setOutcome(null, popupRedirectOutcome);
            popupRedirectOutcome = null;
        }
        return null;
    }
    
    public void displaySomeRows() {
        displayRows = getPageLimit();
        showDisplayAll = true;
        showDisplaySome = false;
    }
    
    public void displayAllRows() {
        displayRows = 600;
        showDisplayAll = false;
        showDisplaySome = true;
    }
    
    public String getHowToUse() {
        return howToHelp;
    }
    
    public boolean isShowDisplayAll() {
        return showDisplayAll && numRows > getPageLimit();
    }

    public boolean isShowDisplaySome() {
        return showDisplaySome && numRows > getPageLimit();
    }

    public String getCountyCd() {
        return countyCd;
    }

    public void setCountyCd(String countyCd) {
        this.countyCd = countyCd;
    }

    public String getFacilityName() {
        return facilityName;
    }

    public void setFacilityName(String facilityName) {
        this.facilityName = facilityName;
    }

    public List<FceScheduleRow> getScheduleList() {
        return scheduleList;
    }

    public String getDoLaaCd() {
        return doLaaCd;
    }

    public void setDoLaaCd(String doLaaCd) {
        this.doLaaCd = doLaaCd;
    }

    public boolean isEditable() {
        return editable;
    }

    public void setEditable(boolean editable) {
        this.editable = editable;
    }

    public boolean isSchedOrNot() {
        return schedOrNot;
    }

    public void setSchedOrNot(boolean schedOrNot) {
        this.schedOrNot = schedOrNot;
    }

    public boolean isDisclosed() {
        return disclosed;
    }

    public void setDisclosed(boolean disclosed) {
        this.disclosed = disclosed;
    }

    public int getDisplayRows() {
        return displayRows;
    }

    public boolean isNonCommitted() {
        return nonCommitted;
    }

    public void setNonCommitted(boolean nonCommitted) {
        this.nonCommitted = nonCommitted;
    }

    public LinkedHashMap<String, Integer> getChoiceOfFfy() {
        return choiceOfFfy;
    }

    public Integer getShowThruFfy() {
        return showThruFfy;
    }

    public void setShowThruFfy(Integer showThruFfy) {
        this.showThruFfy = showThruFfy;
    }

    public int getAddAnotherLikeRowNum() {
        return addAnotherLikeRowNum;
    }

    public void setAddAnotherLikeRowNum(int addAnotherLikeRowNum) {
        this.addAnotherLikeRowNum = addAnotherLikeRowNum;
    }
    
    public final void dialogDone() {
		return;
	}
    
    public final String displayInspectionScheduleHelpInfo() {
		return "dialog:inspectionScheduleHelpInfo";
	}

	public String getFacilityTypeCd() {
		return facilityTypeCd;
	}

	public void setFacilityTypeCd(String facilityTypeCd) {
		this.facilityTypeCd = facilityTypeCd;
	}

	public String getPermitClassCd() {
		return permitClassCd;
	}

	public void setPermitClassCd(String permitClassCd) {
		this.permitClassCd = permitClassCd;
	}

	public String getCmpId() {
		return cmpId;
	}

	public void setCmpId(String cmpId) {
		this.cmpId = cmpId;
	}
}
