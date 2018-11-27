package us.oh.state.epa.stars2.scheduler.jobs;

import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;

import javax.annotation.Resource;

import org.apache.log4j.Logger;
import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.stereotype.Component;

import us.oh.state.epa.stars2.database.dao.ApplicationDAO;
import us.oh.state.epa.stars2.database.dbObjects.application.PBRExport;
import us.oh.state.epa.stars2.framework.exception.DAOException;

@Component
public class ExportPBRDataJob implements Job  {
    private transient Logger logger;
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");
    
    @Resource private ApplicationDAO readOnlyApplicationDAO;
   
    public ExportPBRDataJob() {
        logger = Logger.getLogger(this.getClass());
        logger.debug("ExportPBRDataJob constructed");
    }
    
    public void execute(JobExecutionContext context) throws JobExecutionException {        
        logger.debug("ExportPBRDataJob is executing.");

        JobDataMap dataMap = context.getMergedJobDataMap();
        String fileName = (String)dataMap.get("fileName");
        try {
            writeReportFile(fileName, null);
        }
        catch (Exception e) {
            logger.error("ExportPBRDataJob failed. ", e);
            throw new JobExecutionException("ExportPBRDataJob failed. " 
                                            + e.getMessage(), e, false);
        }
        
        logger.debug("ExportPBRDataJob completed.");
    }
    
    private String buildExportString(PBRExport export) {
        String ret = null;
        
        if (export != null) {
            StringBuffer outBuffer = new StringBuffer();
            
            outBuffer.append(export.getFacilityId() + "\t");
            outBuffer.append(export.getFacilityName() + "\t");
            if (export.getReceivedDate() != null) {
                outBuffer.append(dateFormat.format(export.getReceivedDate()));
            }
            outBuffer.append("\t");
            outBuffer.append(export.getEuId() + "\t");
            if (export.getEuIdDescription() != null) {
                String replaceStr = "\\n";
                outBuffer.append(export.getEuIdDescription().replaceAll(replaceStr, " ") + "\t");
            } else {
                outBuffer.append("\t");
            }
            outBuffer.append(export.getPbrType() + "\t");
            outBuffer.append(export.getStatus() + "\t");
            if (export.getStatusDate() != null) {
                outBuffer.append(dateFormat.format(export.getStatusDate()));
            }
            outBuffer.append("\n");
                        
            ret = outBuffer.toString();
        }
        
        return ret;
    }
    
    public void writeReportFile(String fileName, String facilityId) {
        try {
//            ApplicationDAO appDAO = (ApplicationDAO)DAOFactory.getDAO("ApplicationDAO");
            PBRExport[] exports = readOnlyApplicationDAO.retrievePBRExport();
            
            if (exports != null && fileName != null) {
                FileWriter outFile = null;

                try {
                    outFile = new FileWriter(fileName);
                    logger.debug("Writing PBR Report to file: " + fileName);
                    logger.debug("Processing " + exports.length + " PBR records...");
                    String headerString = new String("Facility ID\tFacility Name\t" +
                    "Received Date\tEUID\tEUID Description\tPBR Type\tStatus\tStatus Date\n");
                    outFile.write(headerString);
                    for (PBRExport export : exports) {
                        // filter by facility id if specified
                        if (facilityId != null && !facilityId.equals(export.getFacilityId())) {
                            continue;
                        }
                        String outString = buildExportString(export);
                        if (outString != null) {
                            outFile.write(outString);
                        }
                    }

                    outFile.close();
                    logger.debug("Done writing records");
                } catch (IOException ioe) {
                    logger.error("Exception retrieving PBR data", ioe);
                }
            } else {
                logger.error("No PBR data to export!");
            }
        } catch (DAOException de) {
            logger.error(de.getMessage(), de);
        }
        
    }

    /**
     * For testing only
     * @param args
     */
    public static final void main(String[] args) {
        ExportPBRDataJob job = new ExportPBRDataJob();
        job.writeReportFile("C:\\projects\\PBRReport.txt", null);
    }
}
