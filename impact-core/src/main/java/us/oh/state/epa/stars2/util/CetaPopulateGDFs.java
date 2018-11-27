package us.oh.state.epa.stars2.util;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import us.oh.state.epa.stars2.bo.GDFService;
import us.oh.state.epa.stars2.database.dbObjects.ceta.GDF;
import us.oh.state.epa.stars2.webcommon.ValidationBase;

@SuppressWarnings("serial")
public class CetaPopulateGDFs extends ValidationBase implements Job {
    protected static FileWriter outStream;
    private static int idOffset = 0; // 50000;  dennis
    public static String directory = "C:/Projects";
    
    
    
    /*
 
    us.oh.state.epa.stars2.util.CetaPopulateGDFs
    
    select count(*) from stars2.ce_gdf;
    
    delete stars2.ce_gdf;
    
    select * from stars2.cm_sequence_def where sequence_nm = 'CE_gdf_id';
    update stars2.cm_sequence_def set last_used_num = 1000 where sequence_nm = 'CE_gdf_id';
  
     */
    private GDFService gDFService;

    public GDFService getGDFService() {
		return gDFService;
	}

	public void setGDFService(GDFService gDFService) {
		this.gDFService = gDFService;
	}

	public CetaPopulateGDFs() {
        super();
    }

    public void process() {
        try {
            outStream = new FileWriter(new File(directory + File.separator + "migrationData" + File.separator, "gdfMigrationLog.txt"));
            outStream.write("Starting migration of CETA GDFs\n");
            outStream.flush();
            CetaGetUserId.loadTable(directory);
            boolean rtn = CetaGdf.initialize("cetaGDFs.txt"); 
            if(!rtn) return;
            CetaGdf cGdf = new CetaGdf();
            while(true) {
                cGdf = CetaGdf.next();
                if(cGdf == null) break;
                GDF gdf = new GDF();

                gdf.setGdfId(cGdf.gdfId);
                
                String m = Integer.toString(cGdf.gdfsMonth);
                if(m.length() == 1) m = "0" + m;
                gdf.setMonth(m);
                gdf.setYear(Integer.toString(cGdf.gdfsYear));
                gdf.setStageOne(cGdf.stageI);
                gdf.setStageOneAndTwo(cGdf.stageI_II);
                gdf.setNonStageOneAndTwo(cGdf.nonStageI_II);
                
                // map the office.
                String doLaaCd = null;
                if(cGdf.officeId == 1) doLaaCd = "CDO";
                else if(cGdf.officeId == 2) doLaaCd = "NEDO";
                else if(cGdf.officeId == 3) doLaaCd = "NWDO";
                else if(cGdf.officeId == 4) doLaaCd = "TDES";
                else if(cGdf.officeId == 5) doLaaCd = "SWDO";
                else if(cGdf.officeId == 6) doLaaCd = "SEDO";
                else if(cGdf.officeId == 7) doLaaCd = "PLAA";
                else if(cGdf.officeId == 8) doLaaCd = "RAPCA";
                else if(cGdf.officeId == 13) doLaaCd = "CDAQ";
                else if(cGdf.officeId == 14) doLaaCd = "HCDOES";
                else if(cGdf.officeId == 15) doLaaCd = "CCHD";
                else if(cGdf.officeId == 16) doLaaCd = "ARAQMD";
                else if(cGdf.officeId == 20) doLaaCd = "Lake";  
                else if(cGdf.officeId == 21) doLaaCd = "MTAPC";
                else {
                    outStream.write("Unknown office: " + cGdf.officeId + " gdfId=" + cGdf.gdfId + " --Skipped\n");
                    outStream.flush();
                    continue;
                }
                gdf.setDoLaaCd(doLaaCd);

                //  create the GDF record
                GDF createdGdf = null;
                try {
                    // DENNIS OFFSET records to not disturn existing records.
                    gdf.setGdfId(cGdf.gdfId + idOffset);
                    createdGdf = getGDFService().createGDF(gdf);
                    if(createdGdf == null) {
                        outStream.write("Failed to create gdf.  gdfId=" + cGdf.gdfId + "\n");
                    }
                } catch(Exception e) {
                    outStream.write("Failed to create gdf with error " + e.getMessage() + ".  gdfId=" + cGdf.gdfId  + "\n");
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
        logger.error("INFO: CetaPopulateGDFs is executing.");
        try {
            directory = DocumentUtil.getFileStoreRootPath();
            this.process();
        } catch(Exception e) {
            logger.error(e.getMessage(), e);
        }
        logger.error("INFO: CetaPopulateGDFs has completed.");
    }
}
