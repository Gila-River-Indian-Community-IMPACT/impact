package us.oh.state.epa.stars2.database.dbObjects.infrastructure;

import java.sql.ResultSet;

import org.quartz.Job;
import org.quartz.JobDetail;
import org.quartz.Trigger;
import org.quartz.impl.JobDetailImpl;

import us.oh.state.epa.stars2.database.dbObjects.BaseDB;

public class SchedulerJob extends BaseDB {
    private JobDetail job;
    private Trigger[] triggers;

    public SchedulerJob() {
        super();
    }

    public final Trigger getTrigger(String triggerName) {
        Trigger ret = null;

        if (triggers != null) {
            for (Trigger trigger : triggers) {
                if (trigger.getKey().getName().compareTo(triggerName) == 0) {
                    ret = trigger;
                    break;
                }
            }
        }

        return ret;
    }

    public final Trigger[] getTriggers() {
        return triggers;
    }

    public final void setTriggers(Trigger[] triggers) {
        this.triggers = triggers;
    }

    public final JobDetail getJob() {
        return job;
    }

    public final void setJob(JobDetail job) {
        this.job = job;
    }

    public final String getName() {
        String ret = null;

        if (job != null) {
            ret = job.getKey().getName();
        }

        return ret;
    }

    @SuppressWarnings("deprecation")
	public final void setName(String name) {
        if (job == null) {
            job = new JobDetailImpl(name,null);
        }
    }

    public final String getClassName() {
        String ret = null;

        if (job != null) {
            ret = job.getJobClass().getName();
        }

        return ret;
    }

    @SuppressWarnings("unchecked")
	public final void setClassName(String className) {
        if (job == null) {
            job = new JobDetailImpl();
        }

        Class<? extends Job> jobClass = null;

        try {
            jobClass = (Class<? extends Job>) Class.forName(className);
        } catch (ClassNotFoundException cnf) {
            logger.error(cnf.getMessage(), cnf);
        }

        if (jobClass != null) {
            try {
                ((JobDetailImpl)job).setJobClass(jobClass);
            } catch (IllegalArgumentException e) {
                logger.error(e.getMessage(), e);
            }
        }
    }

    public final void populate(ResultSet rs) {
    }
}
