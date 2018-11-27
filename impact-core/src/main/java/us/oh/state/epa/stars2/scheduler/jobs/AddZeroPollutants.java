package us.oh.state.epa.stars2.scheduler.jobs;

// Put here to be able to run from scheduler.  Should not need to be run again.



import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.rmi.RemoteException;
import java.util.ArrayList;

import org.apache.log4j.Logger;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import us.oh.state.epa.stars2.bo.EmissionsReportBO;
import us.oh.state.epa.stars2.database.dbObjects.emissionReport.EmissionTotal;
import us.oh.state.epa.stars2.database.dbObjects.emissionReport.EmissionsReport;
import us.oh.state.epa.stars2.database.dbObjects.emissionReport.EmissionsReportSearch;
import us.oh.state.epa.stars2.def.EmissionReportsDef;
import us.oh.state.epa.stars2.def.ReportOfEmissionsStateDef;
import us.oh.state.epa.stars2.framework.exception.ApplicationException;
import us.oh.state.epa.stars2.util.DocumentUtil;
import us.oh.state.epa.stars2.webcommon.reports.EmissionRow;
import us.oh.state.epa.stars2.webcommon.reports.ReportTemplates;

public class AddZeroPollutants implements Job {
 
    protected static Logger logger;
    // protected static File currDir;
    protected static FileWriter outStream;
    private static String outFileName = "addZeroPollutantLog.txt";
    protected static FileWriter outStreamSQL;
    private static String outFileSQLName = "addZeroPollutantSQL.txt";
    private static EmissionsReportBO erBO = new EmissionsReportBO();
    protected static EmissionsReportSearch[] reports;
    protected static boolean viewOnly = true;  // TODO  change flag to change behavior
    
    public void execute(JobExecutionContext context)
    throws JobExecutionException  {
        String filePath  = DocumentUtil.getFileStoreRootPath();

        try{        
            outStream = new FileWriter(new File(filePath + File.separatorChar + outFileName));
            outStreamSQL = new FileWriter(new File(filePath + File.separatorChar + outFileSQLName));
            outStreamSQL.write("START\n");
            process(EmissionReportsDef.TV);
            process(EmissionReportsDef.SMTV);
            outStream.write("Complete\n");
            outStream.flush();
            outStream.close();
            outStreamSQL.write("FINISH\n");
            outStreamSQL.flush();
            outStreamSQL.close();
            System.exit(0);
        } catch(IOException ioe) {
            logger.error("execute failed", ioe);
        }
    }

    static void process(String type) throws IOException {
        reports = getEmissionsReports(false);
        outStream.write("Total number of " + type + " reports is " + reports.length + "\n");
        outStream.flush();
        for(EmissionsReportSearch ers : reports) {
            // Read report
            EmissionsReport report = null;
  
            try {
                report = erBO.retrieveEmissionsReport(ers.getEmissionsRptId(), false);
            } catch(RemoteException re) {
                outStream.write("Failed on retrieveEmissionsReport(): report " + ers.getEmissionsRptId() + 
                        " for facility " + ers.getFacilityId() +
                        " and year " + ers.getYear() + "\n");
                continue;
            }
            if(report == null) {
                outStream.write("Failed to read report " + ers.getEmissionsRptId() + 
                        " for facility " + ers.getFacilityId() +
                        " and year " + ers.getYear() + "\n");
                continue;
            }

            if(ReportOfEmissionsStateDef.NOT_FILED.equals(report.getRptReceivedStatusCd())) {
                continue;  // skip reports not yet submitted.
            }

            ReportTemplates locatedScRpts = null;
            try {
                locatedScRpts = erBO.retrieveSCEmissionsReports(report.getReportYear(),
                		report.getContentTypeCd(),
                		ers.getFacilityId());
            } catch(RemoteException re) {
                outStream.write("Failed on retrieveSCEmissionsReports(): report " + ers.getEmissionsRptId() + 
                        " for facility " + ers.getFacilityId() +
                        " and year " + ers.getYear() + 
                        " and content type " + ers.getContentTypeCd() + "\n");
                continue;
            }
            if(locatedScRpts == null) {
                outStream.write("Failed to retrieveSCEmissionsReports for report " + ers.getEmissionsRptId() + 
                        " for facility " + ers.getFacilityId() +
                        " and year " + ers.getYear() + 
                        " and content type " + ers.getContentTypeCd() + "\n");
                continue;
            }
            for(String s : locatedScRpts.getDisplayMsgs()) {
                outStream.write("(Failed?): Display Msg for report " + ers.getEmissionsRptId() + 
                        " for facility " + ers.getFacilityId() +
                        " and year " + ers.getYear() + 
                        " and content type " + ers.getContentTypeCd() + " " + s + "\n");
            }
            if(locatedScRpts.isFailed()) {
                outStream.write("Failed to locate report definitions for report " + ers.getEmissionsRptId() + 
                        " for facility " + ers.getFacilityId() +
                        " and year " + ers.getYear() + 
                        " and content type " + ers.getContentTypeCd() + "\n");
                continue;
            }

            ArrayList<EmissionRow> reportEmissions = null;
            try {
                // get report rollup of emissions
                reportEmissions = EmissionRow.getEmissions(report, false,
                        true, new Integer(1), locatedScRpts, logger, false);
            } catch(ApplicationException ae) {
                outStream.write("Failed, Application Exception for report " + ers.getEmissionsRptId() + 
                        " for facility " + ers.getFacilityId() +
                        " and year " + ers.getYear() + 
                        " and content type " + ers.getContentTypeCd() +  "\n");
                continue;
            }
            // does not update database
            erBO.createTotalsRows(report, reportEmissions);
            EmissionTotal[] eShouldBe = report.getEmissionTotalsTreeSet().toArray(new EmissionTotal[0]);

            EmissionTotal[] eHave= erBO.retrieveEmissionTotals(report.getEmissionsRptId());
            // See what missing and what does not match.
            // Should find only zero emissions missing.
            // Go through each pollutant that the report specifies to see if it matches one it
            // should have and has the same value.
            for(EmissionTotal e : eHave) {
                EmissionTotal eFound = null;
                for(EmissionTotal eF : eShouldBe) {
                    if(e.getPollutantCd().equals(eF.getPollutantCd())) {
                        eFound = eF;
                        break;
                    }
                }
                if(eFound != null) {
                    // Does its value match?
                    if(!e.getTotalEmissions().equals(eFound.getTotalEmissions())) {
                        // First check to see if both are zero
                        float v1 = Float.parseFloat(e.getTotalEmissions());
                        float v2 = Float.parseFloat(eFound.getTotalEmissions());
                        if(v1 != 0.f || v2 != 0.f) {
                            // ERROR--pollutant amounts should have been the same
                            outStream.write("ERROR: Report " + ers.getEmissionsRptId() + ", pollutant " + e.getPollutantCd() + " value mismatch--" +
                                    e.getTotalEmissions() + " and " + eFound.getTotalEmissions() + "/n");
                        }
                    }
                    eFound.setPollutantCd(null);
                } else {
                    // Should have had this one.
                    outStream.write("ERROR: Report " + ers.getEmissionsRptId() + ", missing pollutant " +
                            e.getPollutantCd() + "/n");
                }
            }
            
            // Go through any pollutants that were not found and add them.
            for(EmissionTotal e : eShouldBe) {
                if(e.getPollutantCd() == null) continue;
                if(!"0.".equals(e.getTotalEmissions()) && !"0".equals(e.getTotalEmissions())) {
                    //ERROR--should have had this one
                    // See if it is just smaller than 0.00000001  (1.0E-8)
                    outStream.write("ERROR: Report " + ers.getEmissionsRptId() + ", missing pollutant that was not zero " +
                            e.getPollutantCd() + "/n");
                } else {
                    // Add an INSERT command for the zero amount
                    outStreamSQL.write("INSERT INTO rp_report_pollutant_totals (emissions_rpt_id, pollutant_cd, total_emissions) VALUES (" +
                            ers.getEmissionsRptId() + ", '" + e.getPollutantCd() + "', '0.');\n");
                }
            }
        }
    }

    // Return reports
    protected static EmissionsReportSearch[] getEmissionsReports(
    		boolean staging) throws RemoteException {
        EmissionsReportSearch searchObj = new EmissionsReportSearch();
        searchObj.setUnlimitedResults(true);
        reports = erBO.searchEmissionsReports(searchObj, staging);
        return reports;
    }

}