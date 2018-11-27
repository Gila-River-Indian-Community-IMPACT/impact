package us.oh.state.epa.stars2.app.admin;

import java.rmi.RemoteException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import javax.faces.event.AbortProcessingException;
import javax.faces.event.ActionEvent;

import oracle.adf.view.faces.context.AdfFacesContext;
import oracle.adf.view.faces.event.AttributeChangeEvent;
import oracle.adf.view.faces.event.AttributeChangeListener;

import org.apache.myfaces.custom.tree2.TreeModelBase;
import org.apache.myfaces.custom.tree2.TreeNode;
import org.apache.myfaces.custom.tree2.TreeNodeBase;
import org.apache.myfaces.custom.tree2.TreeStateBase;
import org.quartz.JobDataMap;
import org.quartz.Trigger;

import us.oh.state.epa.stars2.bo.InfrastructureService;
import us.oh.state.epa.stars2.database.dbObjects.infrastructure.SchedulerJob;
import us.oh.state.epa.stars2.webcommon.DisplayUtil;
import us.oh.state.epa.stars2.webcommon.FacesUtil;
import us.oh.state.epa.stars2.webcommon.TreeBase;
import us.oh.state.epa.stars2.webcommon.bean.NameValue;

@SuppressWarnings("serial")
public class Scheduler extends TreeBase implements AttributeChangeListener {
    private SchedulerJob job;
    private Trigger trigger;
    private HashMap<String, String> dataMapValues = new HashMap<String, String>();
    private HashMap<String, SchedulerJob> jobs = new HashMap<String, SchedulerJob>();
    private boolean addingJob;
    private boolean addingSchedule;
    private boolean editingJob;
    private boolean editingSchedule;
    private boolean removeJob;
    private boolean removeSchedule;
    private boolean once;
    private boolean repeating;
    private boolean addDataMapValue;
    private Boolean schedulerRunning;
    private String scheduleType;
    private String repeatType;
    private String jobName;
    private String jobClassName;
    private String triggerName;
    private String timeOfDay;
    private Integer dayOfWeek;
    private Integer dayOfMonth;
    private Integer monthOfYear;
    private String dataMapName;
    private String dataMapValue;
    private Date onceDate;
    private DecimalFormat timeFormat = new DecimalFormat("00");
    private Integer hour;
    private Integer minute;
    
	private InfrastructureService infrastructureService;

    public Scheduler() {
        super();
    }

    public InfrastructureService getInfrastructureService() {
		return infrastructureService;
	}

	public void setInfrastructureService(InfrastructureService infrastructureService) {
		this.infrastructureService = infrastructureService;
	}

	public final String getDataMapName() {
        return dataMapName;
    }

    public final void setDataMapName(String dataMapName) {
        this.dataMapName = dataMapName;
    }

    public final String getDataMapValue() {
        return dataMapValue;
    }

    public final void setDataMapValue(String dataMapValue) {
        this.dataMapValue = dataMapValue;
    }

    public final String getMonthOfYear() {
        String ret = null;

        if (monthOfYear != null) {
            if (monthOfYear == Calendar.JANUARY) {
                ret = "January";
            } else if (monthOfYear == Calendar.FEBRUARY) {
                ret = "February";
            } else if (monthOfYear == Calendar.MARCH) {
                ret = "March";
            } else if (monthOfYear == Calendar.APRIL) {
                ret = "April";
            } else if (monthOfYear == Calendar.MAY) {
                ret = "May";
            } else if (monthOfYear == Calendar.JUNE) {
                ret = "June";
            } else if (monthOfYear == Calendar.JULY) {
                ret = "July";
            } else if (monthOfYear == Calendar.AUGUST) {
                ret = "August";
            } else if (monthOfYear == Calendar.SEPTEMBER) {
                ret = "September";
            } else if (monthOfYear == Calendar.OCTOBER) {
                ret = "October";
            } else if (monthOfYear == Calendar.NOVEMBER) {
                ret = "November";
            } else if (monthOfYear == Calendar.DECEMBER) {
                ret = "December";
            }
        }

        return ret;
    }

    public final void setMonthOfYear(String monthOfYear) {
        if (monthOfYear != null) {
            if (monthOfYear.compareToIgnoreCase("January") == 0) {
                this.monthOfYear = new Integer(Calendar.JANUARY);
            } else if (monthOfYear.compareToIgnoreCase("Febrary") == 0) {
                this.monthOfYear = new Integer(Calendar.FEBRUARY);
            } else if (monthOfYear.compareToIgnoreCase("March") == 0) {
                this.monthOfYear = new Integer(Calendar.MARCH);
            } else if (monthOfYear.compareToIgnoreCase("April") == 0) {
                this.monthOfYear = new Integer(Calendar.APRIL);
            } else if (monthOfYear.compareToIgnoreCase("May") == 0) {
                this.monthOfYear = new Integer(Calendar.MAY);
            } else if (monthOfYear.compareToIgnoreCase("June") == 0) {
                this.monthOfYear = new Integer(Calendar.JUNE);
            } else if (monthOfYear.compareToIgnoreCase("July") == 0) {
                this.monthOfYear = new Integer(Calendar.JULY);
            } else if (monthOfYear.compareToIgnoreCase("August") == 0) {
                this.monthOfYear = new Integer(Calendar.AUGUST);
            } else if (monthOfYear.compareToIgnoreCase("September") == 0) {
                this.monthOfYear = new Integer(Calendar.SEPTEMBER);
            } else if (monthOfYear.compareToIgnoreCase("October") == 0) {
                this.monthOfYear = new Integer(Calendar.OCTOBER);
            } else if (monthOfYear.compareToIgnoreCase("November") == 0) {
                this.monthOfYear = new Integer(Calendar.NOVEMBER);
            } else if (monthOfYear.compareToIgnoreCase("December") == 0) {
                this.monthOfYear = new Integer(Calendar.DECEMBER);
            } else {
                DisplayUtil.displayError("Invalid month of year");
            }
        }
    }

    public final String getDayOfWeek() {
        String ret = null;

        if (dayOfWeek != null) {
            if (dayOfWeek == Calendar.SUNDAY) {
                ret = "Sunday";
            } else if (dayOfWeek == Calendar.MONDAY) {
                ret = "Monday";
            } else if (dayOfWeek == Calendar.TUESDAY) {
                ret = "Tuesday";
            } else if (dayOfWeek == Calendar.WEDNESDAY) {
                ret = "Wednesday";
            } else if (dayOfWeek == Calendar.THURSDAY) {
                ret = "Thursday";
            } else if (dayOfWeek == Calendar.FRIDAY) {
                ret = "Friday";
            } else if (dayOfWeek == Calendar.SATURDAY) {
                ret = "Saturday";
            }
        }

        return ret;
    }

    public final void setDayOfWeek(String dayOfWeek) {
        if (dayOfWeek != null) {
            if (dayOfWeek.compareToIgnoreCase("Sunday") == 0) {
                this.dayOfWeek = new Integer(Calendar.SUNDAY);
            } else if (dayOfWeek.compareToIgnoreCase("Monday") == 0) {
                this.dayOfWeek = new Integer(Calendar.MONDAY);
            } else if (dayOfWeek.compareToIgnoreCase("Tuesday") == 0) {
                this.dayOfWeek = new Integer(Calendar.TUESDAY);
            } else if (dayOfWeek.compareToIgnoreCase("Wednesday") == 0) {
                this.dayOfWeek = new Integer(Calendar.WEDNESDAY);
            } else if (dayOfWeek.compareToIgnoreCase("Thursday") == 0) {
                this.dayOfWeek = new Integer(Calendar.THURSDAY);
            } else if (dayOfWeek.compareToIgnoreCase("Friday") == 0) {
                this.dayOfWeek = new Integer(Calendar.FRIDAY);
            } else if (dayOfWeek.compareToIgnoreCase("Saturday") == 0) {
                this.dayOfWeek = new Integer(Calendar.SATURDAY);
            } else {
                DisplayUtil.displayError("Invalid day of week");
            }
        }
    }

    public String getTriggerStatus() {
    	String ret = null;
    	Trigger trigger = getTrigger();
        try {
        	if (null != trigger) {
        		ret = 
        			getInfrastructureService().getTriggerStatus(trigger.getKey());
        	}
        } catch (RemoteException re) {
            handleException(re);
        }
        return ret;
    }
    
    public final String getTimeOfDay() {
        return timeOfDay;
    }

    public final void setTimeOfDay(String timeOfDay) {
        StringBuffer errorMessage = null;

        if (timeOfDay.indexOf(':') != -1) {
            String temp = timeOfDay.substring(0, timeOfDay.indexOf(':'));

            Integer tempInt = new Integer(temp);

            if (tempInt > 23 && tempInt < 0) {
                errorMessage = new StringBuffer(
                        "Not a valid hour, must be between 0 and 23.\n");
            }

            temp = timeOfDay.substring(timeOfDay.indexOf(':') + 1, timeOfDay
                    .length());

            tempInt = new Integer(temp);

            if (tempInt > 59 && tempInt < 0) {
                if (errorMessage == null) {
                    errorMessage = new StringBuffer();
                }
                errorMessage
                        .append("Not a valid minute, must be between 0 and 59.");
            }
        } else {
            errorMessage = new StringBuffer(
                    "Time must be in following format: HH:MM");
        }

        if (errorMessage != null) {
            DisplayUtil.displayError(errorMessage.toString());
        } else {
            this.timeOfDay = timeOfDay;
        }
        return;
    }

    public final String getRepeatType() {
        return repeatType;
    }

    public final void setRepeatType(String repeatType) {
        this.repeatType = repeatType;
    }

    public final Date getOnceDate() {
        return onceDate;
    }

    public final void setOnceDate(Date onceDate) {
        this.onceDate = onceDate;
    }

    public final String getScheduleType() {
        return scheduleType;
    }

    public final void setScheduleType(String scheduleType) {
        this.scheduleType = scheduleType;

        onceDate = null;
        repeatType = null;
        timeOfDay = null;
        dayOfWeek = null;
        monthOfYear = null;

        if (scheduleType.compareTo("Once") == 0) {
            setOnce(true);
            setRepeating(false);
        } else {
            setOnce(false);
            setRepeating(true);
        }
        return;
    }

    public final boolean isRemoveSchedule() {
        return removeSchedule;
    }

    public final void setRemoveSchedule(boolean removeSchedule) {
        this.removeSchedule = removeSchedule;
    }

    public final boolean isAddingSchedule() {
        return addingSchedule;
    }

    public final void setAddingSchedule(boolean addingSchedule) {
        this.addingSchedule = addingSchedule;
    }

    public final boolean isEditingSchedule() {
        return editingSchedule;
    }

    public final void setEditingSchedule(boolean editingSchedule) {
        this.editingSchedule = editingSchedule;
    }

    public final boolean isOnce() {
        return once;
    }

    public final void setOnce(boolean once) {
        this.once = once;
    }

    public final boolean isRepeating() {
        return repeating;
    }

    public final void setRepeating(boolean repeating) {
        this.repeating = repeating;
    }

    public final boolean isRemoveJob() {
        return removeJob;
    }

    public final void setRemoveJob(boolean removeJob) {
        this.removeJob = removeJob;
    }

    @Override
    public final void setSelectedTreeNode(TreeNode selectedTreeNode) {
        reset();
        this.selectedTreeNode = selectedTreeNode;
        
        if ("trigger".equals(selectedTreeNode.getType())) {
            getJob();
            getTriggerData();
        }

    }
    
    private void getTriggerData() {
        once = trigger.getFinalFireTime() != null;
        if (once) {
            scheduleType = "Once";
            onceDate = trigger.getFinalFireTime();
            once = true;
            repeating = false;
            Calendar lastFireCal = Calendar.getInstance();
            lastFireCal.setTime(onceDate);
            int hour = lastFireCal.get(Calendar.HOUR_OF_DAY);
            int minute = lastFireCal.get(Calendar.MINUTE);
            timeOfDay = timeFormat.format(hour) + ":" + timeFormat.format(minute);
        } else {
            scheduleType = "Repeating";
            onceDate = null;
            once = false;
            repeating = true;
            Date lastFireTime = trigger.getPreviousFireTime();
            Date nextFireTime = trigger.getNextFireTime();
            if (lastFireTime == null) {
                lastFireTime = nextFireTime;
                nextFireTime = trigger.getFireTimeAfter(lastFireTime);
            }
            Calendar lastFireCal = Calendar.getInstance();
            lastFireCal.setTime(lastFireTime);
            long fireInterval = nextFireTime.getTime() - lastFireTime.getTime();
            if (fireInterval > 31l*24l*60l*60l*1000l) {
                repeatType = "Yearly";
            } else if (fireInterval > 7l*24l*60l*60l*1000l) {
                repeatType = "Monthly";
            } else if (fireInterval > 24l*60l*60l*1000l) {
                repeatType = "Weekly";
            } else {
                repeatType = "Daily";
            }
            if ("Weekly".equals(repeatType)) {
                dayOfWeek = lastFireCal.get(Calendar.DAY_OF_WEEK);
            }
            if ("Monthly".equals(repeatType)) {
                dayOfMonth = lastFireCal.get(Calendar.DAY_OF_MONTH);
            }
            if ("Yearly".equals(repeatType)) {
                monthOfYear = lastFireCal.get(Calendar.MONTH);
            }
            int hour = lastFireCal.get(Calendar.HOUR_OF_DAY);
            int minute = lastFireCal.get(Calendar.MINUTE);
            timeOfDay = timeFormat.format(hour) + ":" + timeFormat.format(minute);
        }
        
        JobDataMap jobDataMap = trigger.getJobDataMap();
        dataMapValues = new HashMap<String, String>();
        if (jobDataMap != null) {
            for (String key : jobDataMap.getKeys()) {
                dataMapValues.put(key, jobDataMap.getString(key));
            }
        }
    }

    public final SchedulerJob getJob() {
        if (selectedTreeNode.getType().equals("job") && !addingJob) {
            if ((job == null)
                    || (selectedTreeNode.getIdentifier().compareTo(
                            job.getName()) != 0)) {
                if (jobs == null) {
                    getJobs();
                }
                jobName = selectedTreeNode.getIdentifier();
            }
        } else if (selectedTreeNode.getType().equals("trigger")) {
            jobName = selectedTreeNode.getIdentifier();

            jobName = jobName.substring(jobName.indexOf('|') + 1, jobName
                    .length());

            job = jobs.get(jobName);

            triggerName = selectedTreeNode.getIdentifier();

            triggerName = triggerName.substring(0, triggerName.length()
                    - jobName.length() - 1);
            trigger = job.getTrigger(triggerName);
        }

        job = jobs.get(jobName);

        jobClassName = job.getClassName();

        return job;
    }

    public final Trigger getTrigger() {
        if (selectedTreeNode.getType().equals("trigger") && !addingSchedule) {
            if ((trigger == null)
                    || (selectedTreeNode.getIdentifier().compareTo(
                            trigger.getKey().getName()) != 0)) {
                if (jobs == null) {
                    getJobs();
                }

//                jobName = selectedTreeNode.getIdentifier();
//
//                jobName = jobName.substring(jobName.indexOf('|') + 1, jobName
//                        .length());
//
                String[] nodeId = selectedTreeNode.getIdentifier().split("\\|");
                jobName = nodeId[1];
                
                job = jobs.get(jobName);

                jobClassName = job.getClassName();

//                triggerName = selectedTreeNode.getIdentifier();
//
//                triggerName = triggerName.substring(0, triggerName.length()
//                        - jobName.length() + 1);
                triggerName = nodeId[0];
                
                trigger = job.getTrigger(triggerName);

                trigger.getJobDataMap();
            }
        }

        return trigger;
    }

    @SuppressWarnings("unchecked")
    @Override
    public final TreeModelBase getTreeData() {
        if (treeData == null) {
            TreeNodeBase root = new TreeNodeBase("root", "Jobs", "root", false);
            treeData = new TreeModelBase(root);
            ArrayList<String> treePath = new ArrayList<String>();
            treePath.add("0");

            if (isSchedulerRunning()) {
                int level = 0;

                getJobs();

                for (SchedulerJob lJob : jobs.values()) {
                    TreeNodeBase jobNode = new TreeNodeBase("job", lJob
                            .getName(), lJob.getName(), false);

                    treePath.add("0:" + Integer.toString(level++));
                    for (Trigger lTrigger : lJob.getTriggers()) {
                        TreeNodeBase triggerNode = new TreeNodeBase("trigger",
                                lTrigger.getKey().getName(), lTrigger.getKey().getName() + "|"
                                        + lJob.getName(), false);

                        jobNode.getChildren().add(triggerNode);
                    }
                    root.getChildren().add(jobNode);
                }

                TreeStateBase treeState = new TreeStateBase();

                treeState.expandPath(treePath.toArray(new String[0]));
                treeData.setTreeState(treeState);
                selectedTreeNode = root;
                current = "root";
            }
        }
        return treeData;
    }

    public final String SchedulerRunning() {
        isSchedulerRunning();

        return "admin.scheduler";
    }

    public final String reset() {
        setAddingJob(false);
        setAddingSchedule(false);
        setEditingJob(false);
        setEditingSchedule(false);
        setRemoveJob(false);
        setRemoveSchedule(false);
        setOnce(false);
        setRepeating(false);
        setAddDataMapValue(false);

        job = null;
        jobName = "";
        jobClassName = "";
        onceDate = null;
        triggerName = "";
        scheduleType = null;
        repeatType = null;
        timeOfDay = null;
        dayOfWeek = null;
        monthOfYear = null;
        dataMapValues = new HashMap<String, String>();

        return CANCELLED;
    }
    
    public final String cancelTriggerEdit() {
        setEditingSchedule(false);
        return CANCELLED;
    }

    public final String addJob() {
        jobName = "";
        jobClassName = "";

        setAddingJob(true);

        return "JobAdded";
    }

    public final String addSchedule() {
        setAddingSchedule(true);

        return "ScheduleAdded";
    }

    public final String removeJob() {
        String ret = "JobRemoved";
        if (removeJob) {
            try {
                infrastructureService.removeSchedulerJob(jobName);
                reset();
                treeData = null;
                jobs = new HashMap<String, SchedulerJob>();
            } catch (RemoteException re) {
                handleException(re);
                ret = null;
            }
        } else {
            setRemoveJob(true);
        }

        return ret;
    }

    public final String removeSchedule() {
        String ret = "ScheduleRemoved";
        if (removeSchedule) {
            try {
                infrastructureService.removeScheduledTrigger(triggerName, jobName);
                reset();
                treeData = null;
                jobs = new HashMap<String, SchedulerJob>();
            } catch (RemoteException re) {
                handleException(re);
                ret = null;
            }
        } else {
            setRemoveSchedule(true);
        }

        return ret;
    }

    public final String saveJob() {
        String ret = null;
        job = new SchedulerJob();
        if (jobName == null || jobName.trim().length() == 0) {
            DisplayUtil.displayError("Name is required.");
        } else if (jobClassName == null || jobClassName.trim().length() == 0) {
            DisplayUtil.displayError("Class Name is required.");
        } else {
            job.setName(jobName);
            try {
                job.setClassName(jobClassName);
                if (job.getJob().getJobClass() == null) {
                    DisplayUtil.displayError(jobClassName + 
                            " is not a valid class name or does not implement the Job interface.");
                } else {
                    infrastructureService.createSchedulerJob(job.getJob());
    
                    reset();
                    treeData = null;
                    jobs = new HashMap<String, SchedulerJob>();
                    ret = "JobSaved";
                }
            } catch (RemoteException re) {
                handleException(re);
                ret = null;
            }
        }
        
        return ret;
    }

    public final String saveSchedule() {
        String ret = "ScheduleSaved";
        try {
            if (editingSchedule) {
                // remove old schedule if schedule is being edited
                infrastructureService.removeScheduledTrigger(triggerName, jobName);
            }
            if (triggerName == null || triggerName.equals("")) {
            	DisplayUtil.displayError("Scheduler name is required.");
            	ret = null;
            }
            if (once) {
                if (onceDate == null) {
                    DisplayUtil.displayError("Day and Time is required for One Time schedule.");
                    ret = null;
                } else if (triggerName == null || triggerName.equals("")) {
                	ret = null;
                } else {
                    infrastructureService.scheduleOneTimeTrigger(triggerName,
                            onceDate, dataMapValues, job.getName());
                }
            } else if (repeatType == null) {
            	DisplayUtil.displayError("Scheduler type is required.");
            	ret = null;
            	
            } else if (triggerName == null || triggerName.equals("")) {
            	ret = null;
            }  else if (repeatType.compareTo("Daily") == 0) {
                if (timeOfDay == null) {
                    DisplayUtil.displayError("Time to run is required for Daily schedule.");
                    ret = null;
                } else {
                    if (setHourAndMinute()) {
                        infrastructureService.scheduleDailyTrigger(triggerName, hour,
                            minute, dataMapValues, job.getName());
                    }
                    
                }
            } else if (repeatType.compareTo("Weekly") == 0) {
                if (dayOfWeek == null) {
                    DisplayUtil.displayError("Day is required for Weekly schedule.");
                    ret = null;
                } else if (timeOfDay == null) {
                    DisplayUtil.displayError("Time to run is required for Weekly schedule.");
                    ret = null;
                } else {
                    if (setHourAndMinute()) {
                        infrastructureService.scheduleWeeklyTrigger(triggerName,
                                dayOfWeek, hour, minute, dataMapValues, job.getName());
                    }
                }
            } else if (repeatType.compareTo("Monthly") == 0) {
                if (dayOfMonth == null) {
                    DisplayUtil.displayError("Day of Month is required for Monthly schedule.");
                    ret = null;
                } else if (timeOfDay == null) {
                    DisplayUtil.displayError("Time to Run is required for Monthly schedule.");
                    ret = null;
                } else {
                    if (setHourAndMinute()) {
                        infrastructureService.scheduleMonthlyTrigger(triggerName,
                                dayOfMonth, hour, minute, dataMapValues, job.getName());
                    }
                }
            } else if (repeatType.compareTo("Yearly") == 0) {
                if (monthOfYear == null) {
                    DisplayUtil.displayError("Month is required for Yearly schedule.");
                    ret = null;
                } else if (dayOfMonth == null) {
                    DisplayUtil.displayError("Day of Month is required for Yearly schedule.");
                    ret = null;
                } else if (timeOfDay == null) {
                    DisplayUtil.displayError("Time to Run is required for Yearly schedule.");
                    ret = null;
                } else {
                    if (setHourAndMinute()) {
                        infrastructureService.scheduleYearlyTrigger(triggerName,
                                monthOfYear+1, dayOfMonth, hour, minute, dataMapValues, job.getName());
                    } else {
                        DisplayUtil.displayError("Invalid value for Time to Run.");
                        ret = null;
                    }
                }
            }
        } catch (RemoteException re) {
            handleException(re);
            ret = null;
        }

//        reset();

        if (ret != null) {
            treeData = null;
            editingSchedule = false;
            addingSchedule = false;
            jobs = new HashMap<String, SchedulerJob>();
        }

        return ret;
    }
    
    private boolean setHourAndMinute() {
        boolean ok = true;
        hour = null;
        minute = null;
        String temp = timeOfDay.substring(0, timeOfDay.indexOf(':'));
        if (temp != null) {
            hour = new Integer(temp);
        }

        temp = timeOfDay.substring(timeOfDay.indexOf(':') + 1,
                timeOfDay.length());
        if (temp != null) {
            minute = new Integer(temp);
        }
        
        if (hour == null || minute == null) {
            DisplayUtil.displayError("'" + timeOfDay + "' is not a valid entry " +
            		"for the Time to Run field. The value entered must formatted " +
            		"as hh:mm");
            ok = false;
        }
        return ok;
    }

    public final String editJob() {
        setEditingJob(true);

        return "JobEditable";
    }

    public final String editSchedule() {
        setEditingSchedule(true);

        return "ScheduleEditable";
    }

    public final boolean isAddingJob() {
        return addingJob;
    }

    public final void setAddingJob(boolean addingJob) {
        this.addingJob = addingJob;
    }

    public final boolean isEditingJob() {
        return editingJob;
    }

    public final void setEditingJob(boolean editingJob) {
        this.editingJob = editingJob;
    }

    private void getJobs() {
        SchedulerJob[] tempJobs = null;

        try {
            tempJobs = infrastructureService.retrieveSchedulerJobs();
        } catch (RemoteException re) {
            handleException(re);
        }

        if (tempJobs != null) {
            jobs = new HashMap<String, SchedulerJob>();

            for (SchedulerJob lJob : tempJobs) {
                jobs.put(lJob.getName(), lJob);
            }
        } else {
            schedulerRunning = false;
        }
    }

    public final String getJobClassName() {
        return jobClassName;
    }

    public final void setJobClassName(String jobClassName) {
        this.jobClassName = jobClassName;
    }

    public final String getJobName() {
        return jobName;
    }

    public final void setJobName(String jobName) {
        this.jobName = jobName;
    }

    public final String getTriggerName() {
        return triggerName;
    }

    public final void setTriggerName(String triggerName) {
        this.triggerName = triggerName;
    }

    public final String addDataMapValue() {
        setAddDataMapValue(true);

        return "dialog:addDataMap";
    }

    public final void addNewDataValue(ActionEvent actionEvent) {
        if (addDataMapValue) {
            if (this.dataMapValues == null) {
                this.dataMapValues = new HashMap<String, String>();
            }

            if ((dataMapName != null) && (dataMapValue != null)) {
                this.dataMapValues.put(dataMapName, dataMapValue);
                dataMapName = null;
                dataMapValue = null;
            }

            setAddDataMapValue(false);
        }

        FacesUtil.returnFromDialogAndRefresh();
        return;
    }

    public final void cancelNewDataValue(ActionEvent actionEvent) {
        setAddDataMapValue(false);

        AdfFacesContext.getCurrentInstance().returnFromDialog(null, null);
        return;
    }

    public final NameValue[] getDataMapValues() {
        ArrayList<NameValue> ret = new ArrayList<NameValue>();

        for (String key : dataMapValues.keySet()) {
            ret.add(new NameValue(key, dataMapValues.get(key)));
        }

        return ret.toArray(new NameValue[0]);
    }

    public final void addDataMapValues(NameValue nameValue) {
        if (this.dataMapValues == null) {
            this.dataMapValues = new HashMap<String, String>();
        }

        if (nameValue != null) {
            this.dataMapValues.put(nameValue.getName(), (String) nameValue
                    .getValue());
        }
    }

    public final void setDataMapValues(NameValue[] dataMapValues) {
        this.dataMapValues = new HashMap<String, String>();

        for (NameValue nameValue : dataMapValues) {
            this.dataMapValues.put(nameValue.getName(), (String) nameValue
                    .getValue());
        }
    }

    public final boolean isAddDataMapValue() {
        return addDataMapValue;
    }

    public final void setAddDataMapValue(boolean addDataMapValue) {
        this.addDataMapValue = addDataMapValue;
    }

    public final boolean isSchedulerRunning() {
        if (schedulerRunning == null) {
            try {
                schedulerRunning = infrastructureService.isSchedulerRunning();
            } catch (RemoteException re) {
                logger.error(re);
            }
        }

        if (!schedulerRunning) {
            DisplayUtil.displayError("Scheduler is not running, please start.");
        }

        return schedulerRunning;
    }

    public void processAttributeChange(AttributeChangeEvent event)
        throws AbortProcessingException {

        logger.error("Processing " + event.getAttribute());

        if (event.getAttribute().equals("scheduleType")) {
            setScheduleType((String) event.getNewValue());
        }

    }

    public final Integer getDayOfMonth() {
        return dayOfMonth;
    }

    public final void setDayOfMonth(Integer dayOfMonth) {
        this.dayOfMonth = dayOfMonth;
    }
    
    public final void deleteNameValueTableRows() {
        if (actionTable != null && actionTable.getValue() != null) {
            try {
                List<Object> delObjects = new ArrayList<Object>();
                Iterator<?> it = actionTable.getSelectionState().getKeySet().iterator();
                Object oldKey = actionTable.getRowKey();
                while (it.hasNext()) {
                    Object obj = it.next();
                    actionTable.setRowKey(obj);
                    delObjects.add(actionTable.getRowData());
                }
                for (Object o : delObjects) {
                    dataMapValues.remove(((NameValue)o).getName());
                }
        
                actionTable.setRowKey(oldKey);
                actionTable.getSelectionState().clear();
            } catch (ClassCastException e) {
                logger.error("Error attempting to delete row from Name Value table", e);
            }
        }
    }

}
