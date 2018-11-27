package us.oh.state.epa.stars2.scheduler;

import java.sql.Timestamp;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;
import java.util.Set;

import org.quartz.CronTrigger;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SimpleTrigger;
import org.quartz.Trigger;
import org.quartz.TriggerKey;
import org.quartz.impl.StdSchedulerFactory;
import org.quartz.impl.matchers.GroupMatcher;
import org.quartz.impl.triggers.CronTriggerImpl;
import org.quartz.impl.triggers.SimpleTriggerImpl;

import us.oh.state.epa.stars2.framework.daemon.ManagedComponent;
import us.oh.state.epa.stars2.framework.exception.UnableToStartException;

@SuppressWarnings("serial")
public class Stars2Scheduler extends ManagedComponent implements Runnable {
    private String instanceName;
    private Scheduler scheduler;
    public static final String recurringJobName = "WorkFlowRecurringJob";

    public boolean start(Properties params, String inInstanceName)
            throws UnableToStartException {

        boolean ret = super.start(params, inInstanceName);
        if (ret){
            this.instanceName = inInstanceName;
            super.setName("Stars2Scheduler-" + inInstanceName);
        }
        return ret;
    }

    public synchronized void run() {
        try {
            // Grab the Scheduler instance from the Factory
            scheduler = StdSchedulerFactory.getDefaultScheduler();

            // and start it off
            if (scheduler != null && !initShutdown()) {

                scheduler.start();
                init();

                while (!initShutdown()) {
                    wait();
                }

                if (!scheduler.isShutdown()) {
                    scheduler.shutdown(true);
                }
                logger.debug("Stars2 scheduler shutdown.");
            } else {
                logger.error("Unable to start Stars2 scheduler.");
            }
        } catch (Exception e) {
            if (!initShutdown()) {
                logger.error(e.getMessage(), e);
            }
        } finally {
            shutdownComplete();
        }
    }

    public final String getInstanceName() {
        return instanceName;
    }

    public final void addJob(JobDetail job) {
        try {
            scheduler.addJob(job, false);
        } catch (SchedulerException se) {
            logger.error(se.getMessage(), se);
        }
    }

    public final void removeJob(String jobName) {
        try {
        	JobKey jobKey = JobKey.jobKey(jobName, Scheduler.DEFAULT_GROUP);
            scheduler.deleteJob(jobKey);
        } catch (SchedulerException se) {
            logger.error(se.getMessage(), se);
        }
    }

    public final void removeTrigger(String triggerName, String jobName) {
        try {
        	TriggerKey triggerKey = 
        			TriggerKey.triggerKey(triggerName, jobName);
            scheduler.unscheduleJob(triggerKey);
        } catch (SchedulerException se) {
            logger.error(se);
        }
    }

    public final JobDetail[] getJobs() throws SchedulerException {
        ArrayList<JobDetail> jobs = new ArrayList<JobDetail>();

        try {
        	GroupMatcher<JobKey> matcher = 
        			GroupMatcher.jobGroupEquals(Scheduler.DEFAULT_GROUP);
            Set<JobKey> jobKeys = scheduler.getJobKeys(matcher);

            for (JobKey jobKey : jobKeys) {
            	jobs.add(scheduler.getJobDetail(jobKey));
            }
        } catch (SchedulerException se) {
            logger.error(se);
            throw se;
        }

        return jobs.toArray(new JobDetail[0]);
    }

    public final Trigger[] getTriggers(String jobName) throws SchedulerException {
        Trigger[] ret = new Trigger[0];

        try {
        	JobKey jobKey = JobKey.jobKey(jobName, Scheduler.DEFAULT_GROUP);
            List<? extends Trigger> triggers = scheduler.getTriggersOfJob(jobKey);
            ret = triggers.toArray(ret);
        } catch (SchedulerException se) {
            logger.error(se.getMessage(), se);
            throw se;
        }

        return ret;
    }

    public final void scheduleOneTimeTrigger(String triggerName, Date dateToRun,
            HashMap<String, String> dataMap, String jobName) throws SchedulerException {
        try {
        	@SuppressWarnings("deprecation")
			SimpleTriggerImpl trigger = new SimpleTriggerImpl(triggerName, jobName, dateToRun);

            trigger.setJobName(jobName);
            trigger.setJobGroup(Scheduler.DEFAULT_GROUP);

            if ((dataMap != null) && (dataMap.size() > 0)) {
                trigger.setJobDataMap(new JobDataMap(dataMap));
            }

            if (scheduler.checkExists(trigger.getKey())) {
            	scheduler.rescheduleJob(trigger.getKey(), trigger);
            } else {
            	scheduler.scheduleJob(trigger);
            }
            
            Calendar cal = Calendar.getInstance();
            Timestamp currentTime = new Timestamp(cal.getTimeInMillis());
            logger.error("Schedule Info:  triggerName=" + triggerName + ", jobName=" + jobName +
                    ", date=" + dateToRun + ", currentTime=" + currentTime);
        } catch (SchedulerException se) {
            logger.error(se.getMessage(), se);
            throw se;
        }
    }

    public final void scheduleDailyTrigger(String triggerName, int hour, int minute,
            HashMap<String, String> dataMap, String jobName) throws SchedulerException {
        try {
            java.util.Calendar startTime = java.util.Calendar.getInstance();
            startTime.set(java.util.Calendar.HOUR_OF_DAY, hour);
            startTime.set(java.util.Calendar.MINUTE, minute);
            startTime.set(java.util.Calendar.SECOND, 0);
            startTime.set(java.util.Calendar.MILLISECOND, 0);

            // if the startTime will be before the current time, move to next
            // day
            if (startTime.getTime().before(new Date())) {
                startTime.add(java.util.Calendar.DAY_OF_MONTH, 1);
            }

            @SuppressWarnings("deprecation")
			SimpleTriggerImpl trigger = new SimpleTriggerImpl(triggerName, jobName);
            trigger.setRepeatCount(SimpleTrigger.REPEAT_INDEFINITELY);
            trigger.setStartTime(startTime.getTime());

            trigger.setRepeatInterval(24L * 60L * 60L * 1000L);

            trigger.setJobName(jobName);
            trigger.setJobGroup(Scheduler.DEFAULT_GROUP);

            if ((dataMap != null) && (dataMap.size() > 0)) {
                trigger.setJobDataMap(new JobDataMap(dataMap));
            }

            if (scheduler.checkExists(trigger.getKey())) {
            	scheduler.rescheduleJob(trigger.getKey(), trigger);
            } else {
            	scheduler.scheduleJob(trigger);
            }
        } catch (SchedulerException se) {
            logger.error(se.getMessage(), se);
            throw se;
        }
    }

    public final void scheduleWeeklyTrigger(String triggerName, int dayOfWeek,
            int hour, int minute, HashMap<String, String> dataMap, String jobName)
            throws SchedulerException {
        try {
            java.util.Calendar startTime = java.util.Calendar.getInstance();
            startTime.set(java.util.Calendar.HOUR_OF_DAY, hour);
            startTime.set(java.util.Calendar.MINUTE, minute);
            startTime.set(java.util.Calendar.SECOND, 0);
            startTime.set(java.util.Calendar.MILLISECOND, 0);
            startTime.set(java.util.Calendar.DAY_OF_WEEK, dayOfWeek);

            // if the startTime will be before the current time, move to next
            // week
            if (startTime.getTime().before(new Date())) {
                startTime.add(java.util.Calendar.DAY_OF_MONTH, 7);
            }

            @SuppressWarnings("deprecation")
			SimpleTriggerImpl trigger = new SimpleTriggerImpl(triggerName, jobName);
            trigger.setRepeatCount(SimpleTrigger.REPEAT_INDEFINITELY);
            trigger.setStartTime(startTime.getTime());

            trigger.setRepeatInterval(7L * 24L * 60L * 60L * 1000L);

            trigger.setJobName(jobName);
            trigger.setJobGroup(Scheduler.DEFAULT_GROUP);

            if ((dataMap != null) && (dataMap.size() > 0)) {
                trigger.setJobDataMap(new JobDataMap(dataMap));
            }

            if (scheduler.checkExists(trigger.getKey())) {
            	scheduler.rescheduleJob(trigger.getKey(), trigger);
            } else {
            	scheduler.scheduleJob(trigger);
            }
        } catch (SchedulerException se) {
            logger.error(se.getMessage(), se);
            throw se;
        }
    }
    
    public final void scheduleMonthlyTrigger(String triggerName, int dayOfMonth,
            int hour, int minute, HashMap<String, String> dataMap, String jobName)
        throws SchedulerException {
        try {
            java.util.Calendar startTime = java.util.Calendar.getInstance();
            startTime.set(java.util.Calendar.HOUR_OF_DAY, hour);
            startTime.set(java.util.Calendar.MINUTE, minute);
            startTime.set(java.util.Calendar.SECOND, 0);
            startTime.set(java.util.Calendar.MILLISECOND, 0);
            startTime.set(java.util.Calendar.DAY_OF_MONTH, dayOfMonth);

            // if the startTime will be before the current time, move to next
            // month
            if (startTime.getTime().before(new Date())) {
                startTime.add(java.util.Calendar.MONTH, 1);
            }

            @SuppressWarnings("deprecation")
			CronTriggerImpl trigger = new CronTriggerImpl(triggerName, jobName);
            trigger.setStartTime(startTime.getTime());

            String cronExpression = "0 "    // seconds
                + minute     + " "
                + hour       + " "
                + dayOfMonth + " "
                + "* "                      // month
                + "? "                      // day of week
                ;
            try {
                trigger.setCronExpression(cronExpression);
                trigger.setJobName(jobName);
                trigger.setJobGroup(Scheduler.DEFAULT_GROUP);

                if ((dataMap != null) && (dataMap.size() > 0)) {
                    trigger.setJobDataMap(new JobDataMap(dataMap));
                }

                if (scheduler.checkExists(trigger.getKey())) {
                	scheduler.rescheduleJob(trigger.getKey(), trigger);
                } else {
                	scheduler.scheduleJob(trigger);
                }
            } catch (ParseException e) {
                throw new SchedulerException("Invalid Cron Expression: " + cronExpression, e);
            }
        } catch (SchedulerException se) {
            logger.error(se.getMessage(), se);
            throw se;
        }
        
    }
    
    public final void scheduleYearlyTrigger(String triggerName, int monthOfYear, int dayOfMonth,
            int hour, int minute, HashMap<String, String> dataMap, String jobName)
        throws SchedulerException {
        try {
            java.util.Calendar startTime = java.util.Calendar.getInstance();
            startTime.set(java.util.Calendar.HOUR_OF_DAY, hour);
            startTime.set(java.util.Calendar.MINUTE, minute);
            startTime.set(java.util.Calendar.SECOND, 0);
            startTime.set(java.util.Calendar.MILLISECOND, 0);
            startTime.set(java.util.Calendar.DAY_OF_MONTH, dayOfMonth);

            // if the startTime will be before the current time, move to next
            // month
            if (startTime.getTime().before(new Date())) {
                startTime.add(java.util.Calendar.MONTH, 1);
            }

            @SuppressWarnings("deprecation")
			CronTriggerImpl trigger = new CronTriggerImpl(triggerName, jobName);
            trigger.setStartTime(startTime.getTime());

            String cronExpression = "0 "    // seconds
                + minute      + " "
                + hour        + " "
                + dayOfMonth  + " "
                + monthOfYear + " "
                + "? "                      // day of week
                ;
            try {
                trigger.setCronExpression(cronExpression);
                trigger.setJobName(jobName);
                trigger.setJobGroup(Scheduler.DEFAULT_GROUP);

                if ((dataMap != null) && (dataMap.size() > 0)) {
                    trigger.setJobDataMap(new JobDataMap(dataMap));
                }

                if (scheduler.checkExists(trigger.getKey())) {
                	scheduler.rescheduleJob(trigger.getKey(), trigger);
                } else {
                	scheduler.scheduleJob(trigger);
                }
            } catch (ParseException e) {
                throw new SchedulerException("Invalid Cron Expression: " + cronExpression, e);
            }
        } catch (SchedulerException se) {
            logger.error(se.getMessage(), se);
            throw se;
        }
        
    }

    public final Scheduler getScheduler() {
        return scheduler;
    }

    public final boolean init() {
        logger.debug("Scheduler Started and Initialized");
        return true;
    }

    public static String buildTriggerName(Integer processId,
            Integer activityId, Integer loopCnt) {
        return processId + "." + activityId + "." + loopCnt;
    }
}
