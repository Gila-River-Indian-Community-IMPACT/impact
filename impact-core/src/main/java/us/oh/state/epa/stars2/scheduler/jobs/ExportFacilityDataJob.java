package us.oh.state.epa.stars2.scheduler.jobs;

import java.io.FileWriter;
import java.io.IOException;

import javax.annotation.Resource;

import org.apache.log4j.Logger;
import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.stereotype.Component;

import us.oh.state.epa.stars2.database.dao.FacilityDAO;
import us.oh.state.epa.stars2.database.dbObjects.facility.FacilityExport;
import us.oh.state.epa.stars2.framework.exception.DAOException;

@Component
public class ExportFacilityDataJob implements Job  {
    private transient Logger logger;
   
    @Resource private FacilityDAO readOnlyFacilityDAO;
    
    public ExportFacilityDataJob() {
        logger = Logger.getLogger(this.getClass());
        logger.debug("ExportFacilityDataJob constructed");
    }
    
    public void execute(JobExecutionContext context) throws JobExecutionException {        
        logger.debug("ExportFacilityDataJob is executing.");

        JobDataMap dataMap = context.getMergedJobDataMap();

        try {
//            FacilityDAO facDAO = (FacilityDAO)DAOFactory.getDAO("FacilityDAO");
            FacilityExport[] exports = null;
            
            try {
                exports = readOnlyFacilityDAO.retrieveFacilityExports();
            } catch (DAOException de) {
                logger.error(de.getMessage(), de);
            }
            
            String fileName = (String)dataMap.get("fileName");
            
            if (exports != null && fileName != null) {
                FileWriter outFile = null;

                try {
                    outFile = new FileWriter(fileName);

                    for (FacilityExport export : exports) {
                        String outString = buildExportString(export);

                        if (outString != null) {
                            outFile.write(outString);
                        }
                    }
                    
                    outFile.close();
                } catch (IOException ioe) {
                    logger.error(ioe.getMessage(), ioe);
                }
            } else {
                logger.error("No facility to export!");
            }
        }
        catch (Exception e) {
            logger.error("ExportFacilityDataJob failed. ", e);
            throw new JobExecutionException("ExportFacilityDataJob failed. " 
                                            + e.getMessage(), e, false);
        }

    }
    
    private static String buildExportString(FacilityExport export) {
        String ret = null;
        
        if (export != null) {
            StringBuffer outBuffer = new StringBuffer();
            
            outBuffer.append(export.getFacilityId() + "\t");
            outBuffer.append(export.getFacilityName() + "\t");
            outBuffer.append(export.getAddressLine1() + "\t");
            outBuffer.append(export.getAddressLine2() + "\t");
            outBuffer.append(export.getCity() + "\t");
            outBuffer.append(export.getZip5() + "\t");
            outBuffer.append(export.getCountyName() + "\t");
            outBuffer.append(export.getLastName() + ", " + export.getFirstName() + "\t");
            outBuffer.append(export.getPhoneNumber() + "\t");
            outBuffer.append(export.getOperatingStatus() + "\t");
            outBuffer.append(export.getNaics() + "\t");
            outBuffer.append(export.getPermitClassification() + "\t");
            outBuffer.append(export.getEmuID() + "\t");
            outBuffer.append(export.getEuDescription() + "\t");
            outBuffer.append(export.getProcessId() + "\t");
            outBuffer.append(export.getEuOperatingStatus() + "\t");
            outBuffer.append(export.getInitialInstallDate());
                        
            ret = outBuffer.toString();
        }
        
        return ret;
    }
}
