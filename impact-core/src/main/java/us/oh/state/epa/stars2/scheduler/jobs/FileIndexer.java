package us.oh.state.epa.stars2.scheduler.jobs;

import org.apache.log4j.Logger;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.StatefulJob;

import us.oh.state.epa.stars2.fileIndexer.IndexFiles;

public class FileIndexer implements StatefulJob {
    private transient Logger logger;
    
    public FileIndexer() {
        logger = Logger.getLogger(this.getClass());
    }

    public void execute(JobExecutionContext context)
            throws JobExecutionException {
        IndexFiles indexer = new IndexFiles();
        if (indexer.isOk()) {
            logger.debug("Indexing files for keyword search...");
            indexer.run();
            logger.debug("Done indexing files for keyword search. " +
                    indexer.getFilesProcessed() + " files indexed.");
        } else {
            logger.error("Error initializing keyword search FileIndexer");
        }
    }    
}
