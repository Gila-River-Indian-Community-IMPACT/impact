package us.oh.state.epa.stars2.util;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import us.oh.state.epa.stars2.bo.ComplaintsService;
import us.oh.state.epa.stars2.database.dbObjects.ceta.Complaint;
import us.oh.state.epa.stars2.webcommon.ValidationBase;

@SuppressWarnings("serial")
public class CetaPopulateComplaints extends ValidationBase implements Job {
    protected static FileWriter outStream;
//    private static int idOffset = 0; // 50000;  dennis
    public static String directory = "C:/Projects";

    private ComplaintsService complaintsService;

	/*
 
    us.oh.state.epa.stars2.util.CetaPopulateComplaints
    
    select count(*) from ceta.dbo.complaints;
     
    select count(*) from stars2.ce_complaint;
    
    delete stars2.ce_complaint;
    
    select * from stars2.cm_sequence_def where sequence_nm = 'CE_complaint_id';
    update stars2.cm_sequence_def set last_used_num = 2000 where sequence_nm = 'CE_complaint_id';
  
     */

    public CetaPopulateComplaints() {
        super();
    }

    public ComplaintsService getComplaintsService() {
		return complaintsService;
	}

	public void setComplaintsService(ComplaintsService complaintsService) {
		this.complaintsService = complaintsService;
	}

	public void process() {
        try {
            outStream = new FileWriter(new File(directory + File.separator + "migrationData" + File.separator, "complaintMigrationLog.txt"));
            outStream.write("Starting migration of CETA GComplaints\n");
            outStream.flush();
            CetaGetUserId.loadTable(directory);
            boolean rtn = CetaComplaint.initialize("cetaComplaints.txt"); 
            if(!rtn) return;
            CetaComplaint cComplaint = new CetaComplaint();
            while(true) {
                cComplaint = CetaComplaint.next();
                if(cComplaint == null) break;
                Complaint complaint = new Complaint();

                complaint.setComplaintId(cComplaint.complaintsId);
                
                String m = Integer.toString(cComplaint.complaintsMonth);
                if(m.length() == 1) m = "0" + m;
                complaint.setMonth(m);
                complaint.setYear(Integer.toString(cComplaint.complaintsYear));
                complaint.setOpenBurning(cComplaint.openBurning);
                complaint.setHighPriority(cComplaint.hpv);
                complaint.setNonHighPriority(cComplaint.nonHpv);
                complaint.setOther(cComplaint.other);
                complaint.setAntiTamperingInspections(cComplaint.antiTampering);
                int tot = cComplaint.hpv + cComplaint.nonHpv + cComplaint.other;
                if(cComplaint.total1 != tot) {
                    outStream.write("Incorrect Total: complaintsId=" + cComplaint.complaintsId + "\n");
                }
                
                // map the office.
                String doLaaCd = null;
                if(cComplaint.officeId == 1) doLaaCd = "CDO";
                else if(cComplaint.officeId == 2) doLaaCd = "NEDO";
                else if(cComplaint.officeId == 3) doLaaCd = "NWDO";
                else if(cComplaint.officeId == 4) doLaaCd = "TDES";
                else if(cComplaint.officeId == 5) doLaaCd = "SWDO";
                else if(cComplaint.officeId == 6) doLaaCd = "SEDO";
                else if(cComplaint.officeId == 7) doLaaCd = "PLAA";
                else if(cComplaint.officeId == 8) doLaaCd = "RAPCA";
                else if(cComplaint.officeId == 13) doLaaCd = "CDAQ";
                else if(cComplaint.officeId == 14) doLaaCd = "HCDOES";
                else if(cComplaint.officeId == 15) doLaaCd = "CCHD";
                else if(cComplaint.officeId == 16) doLaaCd = "ARAQMD";
                else if(cComplaint.officeId == 20) doLaaCd = "Lake";  
                else if(cComplaint.officeId == 21) doLaaCd = "MTAPC";
                else {
                    outStream.write("Unknown office: " + cComplaint.officeId + " complaintsId=" + cComplaint.complaintsId + " --Skipped\n");
                    outStream.flush();
                    continue;
                }
                complaint.setDoLaaCd(doLaaCd);

                //  create the Complaint record
                Complaint createdComplaint = null;
                try {
                    createdComplaint = getComplaintsService().createComplaint(complaint);
                    if(createdComplaint == null) {
                        outStream.write("Failed to create complaint.  complaintsId=" + cComplaint.complaintsId + "\n");
                    }
                } catch(Exception e) {
                    outStream.write("Failed to create complaint with error " + e.getMessage() + ".  complaintsId=" + cComplaint.complaintsId  + "\n");
                }
                outStream.flush();
            }
        } catch (IOException ioe) {
            try {
                outStream.write("IOException: " + ioe.getMessage() + "\n");
                outStream.flush();
            } catch (IOException x) {
                ;
            }
        }
    }
    
    public void execute(JobExecutionContext context) throws JobExecutionException {        
        logger.error("INFO: CetaPopulateComplaints is executing.");
        try {
            directory = DocumentUtil.getFileStoreRootPath();
            this.process();
        } catch(Exception e) {
            logger.error(e.getMessage(), e);
        }
        logger.error("INFO: CetaPopulateComplaints has completed.");
    }

}
