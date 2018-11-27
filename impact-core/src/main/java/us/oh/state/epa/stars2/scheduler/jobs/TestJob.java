package us.oh.state.epa.stars2.scheduler.jobs;

import java.io.IOException;
import java.io.ObjectInputStream;

import org.apache.log4j.Logger;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.StatefulJob;

public class TestJob implements StatefulJob {
    private transient Logger logger;

    public TestJob() {
        logger = Logger.getLogger(this.getClass());
        logger.debug("TestJob is constructed.");
    }

    public void execute(JobExecutionContext context)
            throws JobExecutionException {
        logger.debug("TestJob is executing.");
        JobDataMap dataMap = context.getMergedJobDataMap();

        logger.debug("datamap size is " + dataMap.size());
        
        Integer foo = dataMap.getInt("foo");
        
        logger.debug("foo is " + foo);
    }
    
    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject();

        logger = Logger.getLogger(this.getClass());
    }
}
