package us.oh.state.epa.stars2.scheduler.jobs;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.util.Calendar;

import org.apache.log4j.Logger;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import us.oh.state.epa.stars2.bo.ReportService;
import us.oh.state.epa.stars2.database.dbObjects.report.ApplicationCount;
import us.oh.state.epa.stars2.database.dbObjects.report.FacilityPermitCount;
import us.oh.state.epa.stars2.database.dbObjects.report.PBRCount;
import us.oh.state.epa.stars2.database.dbObjects.report.PermitCount;
import us.oh.state.epa.stars2.database.dbObjects.report.PermitTime;
import us.oh.state.epa.stars2.framework.exception.DAOException;
import us.oh.state.epa.stars2.util.DocumentUtil;
import us.oh.state.epa.stars2.webcommon.ServiceFactory;

public class ReportsJob implements Job {

    private transient Logger logger;

    private String reportDirName = "tmp" + File.separator + "reports" + File.separator;

    public ReportsJob() {
        logger = Logger.getLogger(this.getClass());
        try {
            DocumentUtil.mkDir(reportDirName);
            Calendar cal = Calendar.getInstance();
            String year = new String(Integer.toString(cal.get(Calendar.YEAR)));
            String month = new String(Integer.toString(cal.get(Calendar.MONTH) + 1));
            if (month.length() == 1) {
                month = "0" + month;
            }
            String day = new String(Integer.toString(cal.get(Calendar.DAY_OF_MONTH)));
            if (day.length() == 1) {
                day = "0" + day;
            }
            reportDirName = DocumentUtil.getFileStoreRootPath() + reportDirName 
                + year + "-" + month + "-" + day + "-"; 
        }
        catch (Exception e) {
            logger.error("Error while constructing the ReportsJob class: "
                         + e.getMessage(), e);
        }

    }

    public void execute(JobExecutionContext context) throws JobExecutionException {        
        logger.debug("ReportsJob is executing.");

        try {
            Calendar cal = Calendar.getInstance();

            cal.set(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH),
                    0, 0, 0);
            cal.set(Calendar.MILLISECOND, 0);
            Timestamp now = new Timestamp(cal.getTimeInMillis());

            cal.set(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH),
                    23, 59, 59);
            cal.set(Calendar.MILLISECOND, 999);
            Timestamp yesterday = new Timestamp(cal.getTimeInMillis());

            //getPBRCounts(yesterday, now);
            getFacilityCounts(now);

            cal.set(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), 1, 0, 0, 0);
            cal.set(Calendar.MILLISECOND, 0);
            yesterday = new Timestamp(cal.getTimeInMillis());

            cal.set(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH) + 1, 1, 23, 59, 59);
            cal.set(Calendar.MILLISECOND, 999);
            cal.set(Calendar.DAY_OF_MONTH, cal.get(Calendar.DAY_OF_MONTH) - 1);
            now = new Timestamp(cal.getTimeInMillis());

            getApplicationCounts(yesterday, now);
            getPermitCounts(yesterday, now);

            getPermitTimes();
        }
        catch (Exception e) {
            logger.error("ReportsJob failed. ", e);
            throw new JobExecutionException("ReportsJob failed. " 
                                            + e.getMessage(), e, false);
        }

    }
    
    private void readObject(ObjectInputStream in) 
        throws IOException, ClassNotFoundException {

        in.defaultReadObject();
        logger = Logger.getLogger(this.getClass());

    }
/*
    private void getPBRCounts(Timestamp startDt, Timestamp endDt)
        throws DAOException {

        FileOutputStream fos = null;

        try {

            ReportService reportBO = ServiceFactory.getInstance().getReportService();
            PBRCount[] pbrCounts = reportBO.retrievePbrCountsByType(startDt, endDt);

            Calendar cal = Calendar.getInstance();
            cal.setTimeInMillis(endDt.getTime());
            
            File pbrFile = new File(reportDirName + "PBRCount.txt");
            
            fos = new FileOutputStream(pbrFile);

            StringBuffer sb = new StringBuffer();

            DateFormat df = DateFormat.getDateInstance(DateFormat.MEDIUM);
            String startDate = df.format(startDt);
            sb.append("PBRs Received or Processed on " + startDate + ".\n");

            sb.append("DO/LAA\tTotalCount (ex. Superseded)\tPBR Count By Type\n");
            for (PBRCount dolaaCount : pbrCounts) {
                sb.append(dolaaCount.getDoLaaName() + "\t");
                sb.append(dolaaCount.getTotalCount() + "\t");
                sb.append("PBR Type\tReceived\tAccepted\tDenied\tSuperseded\tAverage Processing Time (Days)\n");
                for (PBRCount.CodeAndDisp cand : dolaaCount.getCounts()) {
                    sb.append("\t\t");
                    sb.append(cand.getPbrTypeDsc() + "\t");
                    sb.append(cand.getReceived() + "\t");
                    sb.append(cand.getAccepted() + "\t");
                    sb.append(cand.getDenied() + "\t");
                    sb.append(cand.getSuperseded() + "\t");
                    sb.append(cand.getAvgDays() + "\n");
                }
                sb.append("\n");
            }
            String outStr = sb.toString();
            fos.write(outStr.getBytes());

        }
        catch (Exception e) {
            throw new DAOException("Problem with PBR count function: " + e.getMessage(), e);
        }
        finally {
            if (fos != null) {
                try {
                    fos.flush();
                    fos.close();
                }
                catch (Exception e) {
                    // Ignore.
                }
            }
        }

    }
*/
    private void getFacilityCounts(Timestamp asOf) throws DAOException {

        FileOutputStream fos = null;

        try {

            ReportService reportBO = ServiceFactory.getInstance().getReportService();
            FacilityPermitCount[] fpcs = reportBO.retrieveFacilityCountsByPermitType(asOf);

            Calendar cal = Calendar.getInstance();
            cal.setTimeInMillis(asOf.getTime());
            
            File facilityPermitFile = new File(reportDirName + "FacilityCount.txt");
            
            fos = new FileOutputStream(facilityPermitFile);

            StringBuffer sb = new StringBuffer();

            DateFormat df = DateFormat.getDateInstance(DateFormat.MEDIUM);
            String startDate = df.format(asOf);
            sb.append("Total Number of Facilities on " + startDate + ".\n");

            sb.append("Permit Type\tNumber of Facilities With Active Permits\t");
            sb.append("Number of Facilities With Expired Permits\tNumber of Facilities With Extended Permits\n");

            for (FacilityPermitCount fpc : fpcs) {
                sb.append(fpc.getPermitType() + "\t");
                sb.append(fpc.getActive() + "\t");
                sb.append(fpc.getExpired() + "\t");
                sb.append(fpc.getExtended() + "\n");
            }
            String outStr = sb.toString();
            fos.write(outStr.getBytes());

        }
        catch (Exception e) {
            throw new DAOException("Problem with Facility - Permit count function: "
                                   + e.getMessage(), e);
        }
        finally {
            if (fos != null) {
                try {
                    fos.flush();
                    fos.close();
                }
                catch (Exception e) {
                    // Ignore.
                }
            }
        }

    }

    private void getApplicationCounts(Timestamp startDt, Timestamp endDt)
        throws DAOException {

        FileOutputStream fos = null;

        try {

            ReportService reportBO = ServiceFactory.getInstance().getReportService();
            ApplicationCount[] apps = reportBO.retrieveApplicationCountByType(startDt, endDt);

            File appFile = new File(reportDirName + "ApplicationCount.txt");
            
            fos = new FileOutputStream(appFile);

            StringBuffer sb = new StringBuffer();

            DateFormat df = DateFormat.getDateInstance(DateFormat.MEDIUM);
            String startDate = df.format(startDt);
            String endDate = df.format(endDt);
            sb.append("Applications Received Between " + startDate + " and " + endDate + ".\n");

            sb.append("Do/Laa\tPBR Applications Received\tPTIO Applications Received\t"
                      + "Title V Permit Applications Received\tRPC Applications Received\t"
                      + "RPE Applications Received\tRPR Applications Received\n");

            for (ApplicationCount appCount : apps) {
                sb.append(appCount.getDoLaaName() + "\t");
                sb.append(appCount.getPbr() + "\t");
                sb.append(appCount.getPtio() + "\t");
                sb.append(appCount.getTvPto() + "\t");
                sb.append(appCount.getRpc() + "\t");
                sb.append(appCount.getRpe() + "\t");
                sb.append(appCount.getRpr() + "\n");
            }

            String outStr = sb.toString();
            fos.write(outStr.getBytes());

        }
        catch (Exception e) {
            throw new DAOException("Problem with application count function: "
                                   + e.getMessage(), e);
        }
        finally {
            if (fos != null) {
                try {
                    fos.flush();
                    fos.close();
                }
                catch (Exception e) {
                    // Ignore.
                }
            }
        }

    }

    private void getPermitCounts(Timestamp startDt, Timestamp endDt)
        throws DAOException {

        FileOutputStream fos = null;

        try {

            ReportService reportBO = ServiceFactory.getInstance().getReportService();
            PermitCount[] permitCounts = reportBO.retrieveIssuedFinalPermitsCountByType(startDt, endDt);

            File appFile = new File(reportDirName + "PermitCount.txt");
            
            fos = new FileOutputStream(appFile);

            StringBuffer sb = new StringBuffer();

            DateFormat df = DateFormat.getDateInstance(DateFormat.MEDIUM);
            String startDate = df.format(startDt);
            String endDate = df.format(endDt);
            sb.append("Permits Issued Between " + startDate + " and " + endDate + ".\n");

            sb.append("\t\t\t\tPTIO Permits\t\t\t" 
                      + "\t\t\tTitle V PTI Permits\t\t\t"
                      + "\t\t\tTitle V Permit Permits\t\t\t"
                      + "\t\t\tState PTO Permits\t\t\n");

            sb.append("DO/LAA\t"
                      + "Total Issued Final\tIssued On Time\tOn Time (Avg Processing Days)\tIssued Over Time\tOver Time (Avg Processing Days\tTotal Denied\t"
                      + "Total Issued Final\tIssued On Time\tOn Time (Avg Processing Days)\tIssued Over Time\tOver Time (Avg Processing Days\tTotal Denied\t"
                      + "Total Issued Final\tIssued On Time\tOn Time (Avg Processing Days)\tIssued Over Time\tOver Time (Avg Processing Days\tTotal Denied\t"
                      + "Total Issued Final\tIssued On Time\tOn Time (Avg Processing Days)\tIssued Over Time\tOver Time (Avg Processing Days\tTotal Denied\n");

            for (PermitCount pCount : permitCounts) {
                sb.append(pCount.getDoLaaName() + "\t");

                sb.append(pCount.getFinalPtio() + "\t");
                sb.append(pCount.getPtioOnTimeCount() + "\t");
                sb.append(pCount.getPtioOnTimeAverage() + "\t");
                sb.append(pCount.getPtioOverTimeCount() + "\t");
                sb.append(pCount.getPtioOverTimeAverage() + "\t");
                sb.append(pCount.getDeniedPtio() + "\t");

                sb.append(pCount.getFinalTvPti() + "\t");
                sb.append(pCount.getTvPtiOnTimeCount() + "\t");
                sb.append(pCount.getTvPtiOnTimeAverage() + "\t");
                sb.append(pCount.getTvPtiOverTimeCount() + "\t");
                sb.append(pCount.getTvPtiOverTimeAverage() + "\t");
                sb.append(pCount.getDeniedTvPti() + "\t");

                sb.append(pCount.getFinalTvPto() + "\t");
                sb.append(pCount.getTvPtoOnTimeCount() + "\t");
                sb.append(pCount.getTvPtoOnTimeAverage() + "\t");
                sb.append(pCount.getTvPtoOverTimeCount() + "\t");
                sb.append(pCount.getTvPtoOverTimeAverage() + "\t");
                sb.append(pCount.getDeniedTvPto() + "\t");

                sb.append(pCount.getFinalStatePto() + "\t");
                sb.append(pCount.getStateOnTimeCount() + "\t");
                sb.append(pCount.getStateOnTimeAverage() + "\t");
                sb.append(pCount.getStateOverTimeCount() + "\t");
                sb.append(pCount.getStateOverTimeAverage() + "\t");
                sb.append(pCount.getDeniedStatePto() + "\n");
            }

            String outStr = sb.toString();
            fos.write(outStr.getBytes());

        }
        catch (Exception e) {
            throw new DAOException("Problem with permit count function: "
                                   + e.getMessage(), e);
        }
        finally {
            if (fos != null) {
                try {
                    fos.flush();
                    fos.close();
                }
                catch (Exception e) {
                    // Ignore.
                }
            }
        }

    }

    public void getPermitTimes() throws DAOException {

        FileOutputStream fos = null;

        try {

            ReportService reportBO = ServiceFactory.getInstance().getReportService();
            PermitTime[] permitTimes = reportBO.retrieveInProcessPermitTime();

            Calendar cal = Calendar.getInstance();
            
            File appFile = new File(reportDirName + "PermitsInProcess.txt");
            
            fos = new FileOutputStream(appFile);

            StringBuffer sb = new StringBuffer();

            DateFormat df = DateFormat.getDateInstance(DateFormat.MEDIUM);
            df.setCalendar(cal);
            String asOf = df.format(new Timestamp(cal.getTimeInMillis()));
            sb.append("List of Permits In Process on " + asOf + ".\n");

            sb.append("Permit Number\tPermit Type\t"
                      + "Elapsed Time (Days)\t"
                      + "Agency Time (Days)\t"
                      + "Applicant Time (Days)\t"
                      + "Other Time\n");

            for (PermitTime pt : permitTimes) {
                sb.append(pt.getPermitNbr() + "\t");
                sb.append(pt.getPermitType() + "\t");
                sb.append(pt.getElapsedTime() + "\t");
                sb.append(pt.getAgencyTime() + "\t");
                sb.append(pt.getApplicantTime() + "\t");
                sb.append(pt.getOtherTime() + "\n");
            }

            String outStr = sb.toString();
            fos.write(outStr.getBytes());

        }
        catch (Exception e) {
            throw new DAOException("Problem with permit count function: "
                                   + e.getMessage(), e);
        }
        finally {
            if (fos != null) {
                try {
                    fos.flush();
                    fos.close();
                }
                catch (Exception e) {
                    // Ignore.
                }
            }
        }

    }

}
