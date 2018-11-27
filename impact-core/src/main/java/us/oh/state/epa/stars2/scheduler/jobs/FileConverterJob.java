package us.oh.state.epa.stars2.scheduler.jobs;

import org.apache.log4j.Logger;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.StatefulJob;

import us.oh.state.epa.stars2.fileConverter.FileConverter;

public class FileConverterJob implements StatefulJob {
    private transient Logger logger;
    
    public FileConverterJob() {
        logger = Logger.getLogger(this.getClass());
    }

    public void execute(JobExecutionContext context)
            throws JobExecutionException {
        FileConverter converter = new FileConverter();
        if (converter.init()) {
            logger.debug("Converting permit files to PDF...");
            converter.convertPermitFiles();
            logger.debug("Done converting permit files. " 
                    + converter.getPermitFilesConverted() + " files converted");

            logger.debug("Converting other WPD files to PDF...");
            converter.convertWPDFiles();
            logger.debug("Done converting other WPD files. " + 
                    converter.getOtherWPDFilesConverted() + " files converted.");
            logger.debug("File extensions modified for " + converter.getFileExtensionsModified() + " files.");
        } else {
            logger.error("Failed initializing FileConverter");
        }
    }    
}
