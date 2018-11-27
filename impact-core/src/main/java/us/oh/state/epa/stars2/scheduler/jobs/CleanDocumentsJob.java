package us.oh.state.epa.stars2.scheduler.jobs;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.rmi.RemoteException;

import org.apache.log4j.Logger;
import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import us.oh.state.epa.stars2.bo.DocumentService;
import us.oh.state.epa.stars2.database.dbObjects.document.Document;
import us.oh.state.epa.stars2.util.DocumentUtil;
import us.oh.state.epa.stars2.webcommon.ServiceFactory;
import us.oh.state.epa.stars2.webcommon.ServiceFactoryException;

public class CleanDocumentsJob implements Job {
    private transient Logger logger;

    public CleanDocumentsJob() {
        logger = Logger.getLogger(this.getClass());
        logger.debug("CleanDocumentsJob is constructed.");
    }

    public void execute(JobExecutionContext context) throws JobExecutionException {
        
        logger.debug("CleanDocumentsJob is executing.");

        String key = "removeTmpFilesOlderThan";
        JobDataMap dataMap = context.getMergedJobDataMap();
        Integer daysOld = null;
        if (dataMap.containsKey(key)) {
        	daysOld = dataMap.getIntFromString(key);
        } else {
        	int defaultValue = 4;
            logger.error("Misconfigured job!  Cannot locate 'removeTmpFilesOlderThan' parameter.  " + 
            		"Using default value of: " + defaultValue);
        	daysOld = defaultValue; //default
        }
        try {
            DocumentService docBO = ServiceFactory.getInstance().getDocumentService();
            logger.debug("Deleting temporary documents...");
            docBO.removeTemporaryDocuments(daysOld);
            DocumentUtil.cleanTmpDirs(daysOld);
            logger.debug("Done deleting temporary documents...");
            logger.debug("Checking for missing files for last 60 days...");
            checkForMissingFiles(60);
            logger.debug("Done checking for missing files.");
        } catch (Exception e) {
            logger.error(e);
            JobExecutionException qrtze = new JobExecutionException(e);
            logger.debug("Refire count: " + context.getRefireCount());
            if (context.getRefireCount() < 2) {
            	logger.debug("Setting refireImmediately = true");
            	qrtze.setRefireImmediately(true);
            }
        	logger.debug("Throwing JobExecutionException");
            throw qrtze;
        }

    }
    
    private void checkForMissingFiles(Integer daysOld) {
        DocumentService docBO;
        try {
            docBO = ServiceFactory.getInstance().getDocumentService();
            Document[] allDocs = docBO.retrieveNonTemporaryDocuments(daysOld);
            for (Document doc : allDocs) {
                String docPath = doc.getPath();
                // if file has a ".", uses the string up to the "."
                // to find files matching that pattern
                // This is needed because some file names end with "."
                // and the DocumentUtil.canRead() method fails to find those files.
                boolean found = false;
                int lastDotIdx = docPath.lastIndexOf('.');
                if (lastDotIdx > 0) {
                    File origFile = new File(DocumentUtil.getFileStoreRootPath() + docPath);
                    File parentDir = origFile.getParentFile();
                    lastDotIdx = origFile.getName().lastIndexOf('.');
                    final String fileName = origFile.getName().substring(0, lastDotIdx) + ".*";
                    File[] matchingFiles = parentDir.listFiles(new FilenameFilter() {
                                public boolean accept(File dir, String name) {
                                    return name.matches(fileName);
                                }
                    });
                    if (matchingFiles != null && matchingFiles.length > 0) {
                        found = true;
                    }
                } else {
                    try {
                        found = DocumentUtil.canRead(docPath);
                    } catch (IOException e) {
                        logger.error("Exception looking for file with document id: " + doc.getDocumentID() +
                                ", path = " + DocumentUtil.getFileStoreRootPath() + docPath, e);
                    }
                }
                if (!found) {
                    logger.error("File missing for document id: " + doc.getDocumentID() +
                            ". No file found at " + DocumentUtil.getFileStoreRootPath() + docPath);
                }
            }
        } catch (ServiceFactoryException e1) {
            logger.error("Exception accessing document service", e1);
        } catch (RemoteException e) {
            logger.error("Exception retrieving non-temporary documents", e);
        }
    }
    
    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject();

        logger = Logger.getLogger(this.getClass());
    }
    
    public static void main (String[] args) {
        CleanDocumentsJob job = new CleanDocumentsJob();
        job.checkForMissingFiles(30);
    }
}
