package us.oh.state.epa.stars2.app.emissionsReport;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.rmi.RemoteException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;

import javax.faces.event.ValueChangeEvent;
import javax.faces.model.SelectItem;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.apache.log4j.Logger;

import com.Ostermiller.util.ExcelCSVParser;

import oracle.adf.view.faces.model.UploadedFile;
import us.oh.state.epa.stars2.bo.EmissionsReportService;
import us.oh.state.epa.stars2.bo.InfrastructureService;
import us.oh.state.epa.stars2.database.dbObjects.document.Document;
import us.oh.state.epa.stars2.database.dbObjects.emissionReport.EmissionsReport;
import us.oh.state.epa.stars2.database.dbObjects.emissionReport.FireRow;
import us.oh.state.epa.stars2.database.dbObjects.infrastructure.SccCode;
import us.oh.state.epa.stars2.def.ActionsDef;
import us.oh.state.epa.stars2.def.FireVariableNamesDef;
import us.oh.state.epa.stars2.def.MaterialDef;
import us.oh.state.epa.stars2.def.PollutantDef;
import us.oh.state.epa.stars2.def.UnitDef;
import us.oh.state.epa.stars2.framework.config.Config;
import us.oh.state.epa.stars2.framework.exception.DAOException;
import us.oh.state.epa.stars2.framework.util.Utility;
import us.oh.state.epa.stars2.util.DocumentUtil;
import us.oh.state.epa.stars2.webcommon.AppBase;
import us.oh.state.epa.stars2.webcommon.DisplayUtil;
import us.oh.state.epa.stars2.webcommon.FacesUtil;
import us.oh.state.epa.stars2.webcommon.ServiceFactory;
import us.oh.state.epa.stars2.webcommon.ServiceFactoryException;
import us.oh.state.epa.stars2.webcommon.UploadedFileInfo;
import us.oh.state.epa.stars2.webcommon.facility.FacilityReference;
import us.oh.state.epa.stars2.webcommon.reports.ExpressionEval;

/*
 * The factorId field had to be defined as MS Excel type Special in order to store
 * (intreprete) all the digits rather than convert to scientific notation.
 * 
 * THIS SHOULD FIRST BE RUN IN analyse mode where it does not actually make any changes.
 * If that is OK then run in "updateMode".
 * If it should fail part way through when in updateMode, then there will be many rows
 * marked deprecated which should not be.  The input file should be fixed and run again.
 * Those new rows which had been processed will be reapplied (the only change being to
 * use the deprecated value from the new row.
 * Once this importer program has finally completed sucessfully, the results will be
 * the same as if it ran successfully the first time.
 * 
 * 
 * The critical fields that must be the same to be the same fire row are:
 * SCC (scc_id)
 * Material (material_cd)
 * Pollutant (pollutant_cd)
 * Factor
 * Factor units (emissions_unit_cd) -- note this must be 'LB': converted to upper case before checking
 * Formula
 * 
 * Other critical fields but must be the same for every entry with the same scc/measure:
 * Action (action_cd)
 * Material units (material_unit_cd): converted to upper case before checking
 * 
 * Action and Material units will not be placed in the where clause
 * because these are determined by the SCC and Material.
 * So the fields to match on are:  SCC, Material, Pollutant, Factor and Formula
 *
 * First all the fire rows are deprecated with the current year that are not already deprecated.
 *
 * If all the critical fields are the same then the fire row is updated with all the other
 * fields except it uses the fire_id from the oracle database.  (The update will be skipped
 * if all the other fields are the same--have not changed).
 * 
 * If any of these fields are different, then it is a new row being created and will use
 * a fire_id derived from the new row.  It may be that factor_id but if that is not unique
 * then a suffix is added until it is unique.
 * 
 * Note that it uses the deprecated field from the new row so that row is or is not deprecated
 * depending upon that row.  So there are two ways to deprecate a fire row.  One is to leave
 * the row out of the input file.  The other is to include it and have the deprecated field set.
 */
public class FireUpdater extends AppBase {
	private static String FIRE_IMPORT_PATH = File.separator + "FireImport";
    private static String logFileString = "FireImport" + File.separator + "fireLog.txt";
    private static String errFileString = "FireImport" + File.separator + "fireErr.txt";
    private static String HELP_FILE_NAME = "IMPACT-WebFIRE-Import-Tool.pdf";
    private transient Logger logger = Logger.getLogger(this.getClass());
    private static HashSet<String> variables = new HashSet<String>(100);

    private static boolean updateMode = false;
    private static boolean addMode = false;
    private static int year;
    private static EmissionsReportService erBO = null;
    private Integer activeRows = null;
    private Integer importChoice = null;
    String operation = "";
    private UploadedFile fireFileToUpload;
    private UploadedFileInfo fireFileInfo;
    private Integer fileStatus = new Integer(1); // 0=in progress, 1=previous results, 2=new results.
    private String refreshStr = " ";
    private String refreshStrOn = "<META HTTP-EQUIV=\"refresh\" CONTENT=\"5\" />"
        + "<META HTTP-EQUIV=\"Pragma\" CONTENT=\"no-cache\" />"
        + "<META HTTP-EQUIV=\"Expires\" CONTENT=\"-1\" />"
        + "<META HTTP-EQUIV=\"Cache-Control\" "
        + "CONTENT=\"no-cache, must-revalidate, pre-check=0, post-check=0, max-age=0\" />";
    private static long percentProg = 0;
    private static long fileLength = 1;
    private static long fileLengthRead = 0;
    private static FireUpdater fireUpdater = null;
    private String displayResults = "";
    private String displayErr = "";
    private String howFireWorks = "";
    private Document logFileDoc = new Document();
    private Document errFileDoc = new Document();
    private boolean noErrors = false;
    protected String popupRedirect = null;
    
 // FIRE Import selection criteria - 
    private String sccId = null; // SCC ID
    private List<String> materialCds = new ArrayList<String>(); // Material CD
    private List<String> pollutantCds = new ArrayList<String>(); // Pollutant CD
    
    private FacilityReference facilityReference;

    public String startOperation() {
        fireUpdater = this;
        noErrors = true;
        displayErr = "";
        displayResults = "";
        
		if (Utility.isNullOrEmpty(sccId) && !getMaterialCds().isEmpty()) {
			DisplayUtil
					.displayError("Since materials are selected, SCC ID must be selected.");
			return "importFire";
		}
		if (Utility.isNullOrEmpty(sccId) && !getPollutantCds().isEmpty()) {
			DisplayUtil
					.displayError("Since pollutants are selected, SCC ID must be selected.");
			return "importFire";
		}
		
		// If no materials are selected, it is the same as selecting all.
		if (!Utility.isNullOrEmpty(sccId) && getMaterialCds().isEmpty()) {
			List<String> materialList = new ArrayList<String>();
			for (SelectItem s : getSccMaterials()) {
				materialList.add((String)s.getValue());
			}
			setMaterialCds(materialList);
		}
		
		// If no pollutants are selected, it is the same as selecting all.
		if (!Utility.isNullOrEmpty(sccId) && getPollutantCds().isEmpty()) {
			List<String> pollutantList = new ArrayList<String>();
			for (SelectItem s : getSccPollutants()) {
				pollutantList.add((String)s.getValue());
			}
			setPollutantCds(pollutantList);
		}
		
        if(fireFileToUpload == null || fireFileToUpload.getFilename().length() == 0) {
            DisplayUtil.displayError("No FIRE file to import is entered");
            return "importFire";
        }
        fireFileInfo = new UploadedFileInfo(fireFileToUpload);
        if(importChoice == null) {
            DisplayUtil.displayError("No operation picked.");
            return "importFire";
        }
        
        switch(importChoice) {
        case 1:
            addMode = true;
            updateMode = false;
            operation = "Analyze adding new entries only.";
            break;
        case 2:
            addMode = false;
            updateMode = false;
            operation = "Analyze doing a full FIRE Import.";
            break;
        case 3:
            addMode = true;
            updateMode = true;
            operation = "Add new entries only.";
            break;
        case 4:
            addMode = false;
            updateMode = true;
            operation = "Perform full FIRE Import.";
            break;
        }
        
        /* Re-enabled original functionality
        switch(importChoice) {
        case 1:
            addMode = false;
            updateMode = false;
            operation = "Analyze doing a FIRE Import.";
            break;
        case 2:
            addMode = false;
            updateMode = true;
            operation = "Perform FIRE Import.";
            break;
        }
        */
        year = Calendar.getInstance().get(Calendar.YEAR) - 1;
        fileStatus = new Integer(0); // in progress

//      try { // used to provide time to start the debugger
//      Thread.sleep(10000);  // sleep 10 seconds
//      } catch(InterruptedException e) {
//      System.err.println("Sleep exception");
//      }
        refreshStr = refreshStrOn;
        fileLength = fireFileInfo.getLength();
        fileLengthRead = 0;
        percentProg = 0;
        this.setValue(percentProg);
        this.setMaximum(100);
        RunImport importThread = new RunImport();
        importThread.start();
        return "importFire";
    }

    public String initImportFire() {
        year = Calendar.getInstance().get(Calendar.YEAR) - 1;
        importChoice = null;
        return "importFire";
    }
    public Integer getDefaultCreateDeprecate() {
        // Set in function initImportFire (called from admin-config.xml)
        return year;
    }

    private String[][] readRecord(FileWriter writer, FileWriter errWriter, BufferedReader br)
    throws IOException {
        String[][] values = null;
        StringBuffer recordBuf = null;
        try {
            String rec = br.readLine();
            updateProgress(rec);
            if(Utility.isNullOrEmpty(rec)) {
                return null;  // end of file
            }
            recordBuf = new StringBuffer(rec);
            values = ExcelCSVParser.parse(recordBuf.toString());
            while(values[0].length < 27) {
                rec = br.readLine();
                updateProgress(rec);
                if(rec == null) {
                    return values;
                }
                recordBuf.append(rec);
                values = ExcelCSVParser.parse(recordBuf.toString());
            }
        } catch(IOException ioe) {
            logger.error("Unable to read a FIRE record", ioe);
            populateErrorDisplay(writer, errWriter, ioe);
            throw ioe;
        }
        return values;
    }

    public final void importFireData(UploadedFileInfo fireFileInfo, String logFile, String exceptionFile) {
        String[][] values;
        FireRow fTemp = null;
        FileWriter writer = null;
        FileWriter errWriter = null;
        try {
            erBO = ServiceFactory.getInstance().getEmissionsReportService();
        } catch (ServiceFactoryException sfe) {
            logger.error("Unable to get ServiceFactory instance", sfe);
            populateErrorDisplay(writer, errWriter, sfe);
            return;
        }

        try {
        	DocumentUtil.mkDir(FIRE_IMPORT_PATH);
            writer = new FileWriter(logFile);
            errWriter = new FileWriter(exceptionFile);
        } catch(IOException e) {
            logger.error("Unable to create log files", e);
            populateErrorDisplay(writer, errWriter, e);
            return;
        }

        try {
            activeRows = erBO.activeFireRows();
            if(activeRows == null) {
                logger.error("Unable to count active fire rows");
                populateErrorDisplay(writer, errWriter, "Unable to count active fire rows");
                return;
            }
        } catch (DAOException de) {
            logger.error("Unable to count active fire rows", de);
            populateErrorDisplay(writer, errWriter, de);
            return;
        } catch (RemoteException re) {
            logger.error("Unable to count active fire rows", re);
            populateErrorDisplay(writer, errWriter, re);
            return;
        }

        /* First we deprecate all the current entries in the DB */
        /* If SCC ID is not null, deprecate all the current entries
         * for the SCC ID, materials, and pollutants selected.
         */

        if (updateMode && !addMode) {
            try {
            	if (Utility.isNullOrEmpty(sccId)) {
            		erBO.deprecateFire(year);
            	} else {
            		erBO.deprecateFire(year, sccId, materialCds, pollutantCds);
            	}
            } catch (DAOException de) {
                logger.error("Unable to deprecate all fire rows", de);
                populateErrorDisplay(writer, errWriter, de);
                return;
            } catch (RemoteException re) {
                logger.error("Unable to deprcate all fire rows", re);
                populateErrorDisplay(writer, errWriter, re);
                return;
            }
        }

        /* Now read in all of the entries and process them */
        int rowCt = 0;
        String prevFactorId = null;
        try {
            BufferedReader br = new BufferedReader(fireFileInfo.getFileReader());
            String record; // DO NOT SKIP THE FIRST LINE    = br.readLine();
            //updateProgress(record); // DO NOT SKIP THE FIRST LINE  
            int newCnt = 0;
            int modifyCnt = 0;
            int skippedCnt = 0;  // action or measure differ for same scc and material
            int validationCnt = 0;
            int tooShortCnt = 0;
            int duplicateInDB = 0;
            values = readRecord(writer, errWriter, br);
            while ((values != null) & (rowCt < 2000000)) {
                if ((values != null) && (values[0].length >= 27)) {
                    if(values[0].length > 27) {
                        String errStr = "Row too long for FactorID=" + values[0][0] +
                        ", number of fields=" + values[0].length + ".  Extra fields ignored\n";
                        writer.write(errStr);
                        writer.flush();
                        writeError(errWriter, errStr);
                        errWriter.flush();
                    }
                    if(fTemp != null) {
                        prevFactorId = fTemp.getFactorId();
                    }
                    fTemp = new FireRow();
                    boolean failed = false;
                    String field = null;
                    try {
                        field = "field 1 FactorId";
                        fTemp.setFactorId(values[0][0]);
                        field = "field 2 SccId";
                        fTemp.setSccId(values[0][1]);
                        if(fTemp.getSccId().equalsIgnoreCase("generic")) {
                            fTemp.setSccId(null);
                        }
                        field = "field 3PollutantCd";
                        fTemp.setPollutantCd(nonNullValue(values[0][2]));
                        field = "field 4 Factor";
                        fTemp.setFactor(nonNullValue(values[0][3]));
                        field = "field 5 Formula";
                        fTemp.setFormula(nonNullValue(values[0][4]));
                        field = "field 6 Unit";
                        fTemp.setUnit(values[0][5]);
                        field = "field 7 Measure";
                        fTemp.setMeasure(values[0][6]);
                        field = "field 8 Material";
                        fTemp.setMaterial(values[0][7]);
                        field = "field 9 Action";
                        fTemp.setAction(values[0][8]);
                        field = "field 10 Notes";
                        String s = nonNullValue(values[0][9]);
                        // if (s.length() > 2000)
                        // s = s.substring(0, 1999);
                        fTemp.setNotes(s);
                        field = "field 11 Quality";
                        fTemp.setQuality(nonNullValue(values[0][10]));
                        field = "field 12 Origin";
                        fTemp.setOrigin(nonNullValue(values[0][11]));
                        field = "field 13 Cas";
                        fTemp.setCas(nonNullValue(values[0][12]));
                        field = "field 14 Pollutant";
                        fTemp.setPollutant(nonNullValue(values[0][13]));
                        field = "field 15 PollutantID";
                        fTemp.setPollutantID(nonNullValue(values[0][14]));
                        field = "field 16 OrigFactor";
                        fTemp.setOrigFactor(nonNullValue(values[0][15]));
                        field = "field 17 OrigFormula";
                        fTemp.setOrigFormula(nonNullValue(values[0][16]));
                        field = "field 18 OrigUnit";
                        fTemp.setOrigUnit(nonNullValue(values[0][17]));
                        field = "field 19 OrigMeasure";
                        fTemp.setOrigMeasure(nonNullValue(values[0][18]));
                        field = "field 20 OrigMaterial";
                        fTemp.setOrigMaterial(nonNullValue(values[0][19]));
                        field = "field 21 OrigAction";
                        fTemp.setOrigAction(nonNullValue(values[0][20]));
                        field = "field 22 OrigNotes";
                        s = nonNullValue(values[0][21]);

                        // if (s.length() > 2000)
                        // s = s.substring(0, 1999);
                        fTemp.setOrigNotes(s);
                        field = "field 23 OrigQuality";
                        fTemp.setOrigQuality(nonNullValue(values[0][22]));
                        field = "field 24 RefDesc";
                        s = nonNullValue(values[0][23]); 
                        // if (s.length() > 2000)
                        // s = s.substring(0, 1999);
                        fTemp.setRefDesc(s);
                        field = "field 25 Ap42Section";
                        fTemp.setAp42Section(nonNullValue(values[0][24]));
                        field = "field 26 Created";
                        fTemp.setCreated(getYear(values[0][25]));
                        field = "field 27 Deprecated";
                        fTemp.setDeprecated(convert(values[0][26]));
                    } catch (Exception e) {
                        String errStr = "Got Exception converting values read from file: " + e.getMessage() + ", at FactorId=" 
                        + fTemp.getFactorId() + "and previous FactorId=" +
                        prevFactorId + " for " + field + "\n\n";
                        writer.write(errStr);
                        writeError(errWriter, errStr);
                        validationCnt++;
                        failed = true;
                    }
                    if(failed) {
                        rowCt++;
                        record = br.readLine();
                        updateProgress(record);
                        continue;
                    }
                    StringBuffer msg = null;
                    try {
                        msg = validateRow(writer, errWriter, fTemp);
                    } catch(Exception e) {
                        if(displayErr.length() == 0) {
                            logger.error("Unable to validateRow()", e);
                            populateErrorDisplay(writer, errWriter, e);
                        }
                        return;
                    }
                    if (msg.length() == 0) {
                        // Determine if row is new or existing. If existing
                        // make sure all the key fields match and either
                        // update the row or flag as an exception

                        try {
                            // Get example of row not deprecated in "year"
                            FireRow sameSccMaterialRow = erBO.retrieveFireRow(
                                    fTemp.getSccId(), fTemp.getMaterial(), year);
                            // Look for matching row even if marked deprecated at beginning of import ("year").
                            // If addMode, don't include Factor and Formula when determining if there are
                            // duplicates.  This allows us to skip duplicates in the input file and avoid
                            // adding a row that matches an existing row except for factor or formula.
                            boolean omitFactorAndFormula = false;
                            if (addMode) {
                            	omitFactorAndFormula = true;
                            }
                            FireRow[] existingRow = erBO.retrieveFireRow(
                                    fTemp.getSccId(), fTemp.getMaterial(), fTemp.getPollutantCd(),
                                    fTemp.getFactor(), fTemp.getFormula(), fTemp.getMeasure(),
                                    fTemp.getAction(), year, omitFactorAndFormula);

                            if (existingRow.length == 1  && !addMode) { // found existing row matching critical fields
                                StringBuffer errorMsgSccMat = new StringBuffer();
                                // make sure that action and measure also match.
                                if(sameSccMaterialRow != null) {
                                    if (!compareStrings(sameSccMaterialRow.getAction(), fTemp.getAction())) {
                                        errorMsgSccMat.append("Action's don't match ");
                                    }

                                    if (!compareStrings(sameSccMaterialRow.getMeasure(), fTemp.getMeasure())) {
                                        errorMsgSccMat.append("Material Unit Code's don't match ");
                                    }
                                }
                                if (errorMsgSccMat.length() == 0) {
                                    String origId = fTemp.getFactorId();
                                    if (updateMode) {
                                        // Note that deprecated gotten from new Fire row
                                        // so it will override what is in database.

                                        // If the row does not have create date/year set, set it to 
                                        // the value already in the database
                                        // if it is set, then leave the creat date/year as is.
                                        // Note that if a row has deprecate date/year set, then leave it as it is.
                                        if(fTemp.getCreated() == null) {
                                            fTemp.setCreated(existingRow[0].getCreated());
                                        }
                                        fTemp.setFactorId(existingRow[0].getFactorId());  // Use factorId in database.
                                        erBO.modifyFireRow(fTemp);
                                    }
                                    String diff = existingRow[0].nonCriticalequal(fTemp, year);
                                    if(diff.length() != 0) {
                                        // even thou we update every row, count only those
                                        // that are changed.  Every row updated in order
                                        // to overwrite the deprecated that was added at
                                        // the beginning.
                                        modifyCnt++;
                                        writer.write("MODIFY Fire row with factorId(in file)=" + origId
                                                + " and factorId(in database)=" + existingRow[0].getFactorId() + " to the new values of: ");
                                        writeSccMaterial(writer, fTemp);
                                        writer.write(", ");
                                        writeRemainingCritical(writer, fTemp);
                                        writer.write(".  Changes in: " + diff);
                                        writer.write("\n\n");
                                    }
                                } else {
                                    String errStr = "SKIPPED factorId(in file)=" + fTemp.getFactorId()
                                    + " and factorId(in database)=" + existingRow[0].getFactorId()
                                    + ", because " + errorMsgSccMat.toString()
                                    + " the action/measure of other records already in FIRE with the same scc and material.  Values in file: ";
                                    writer.write(errStr);
                                    writeSccMaterial(writer, fTemp);
                                    writer.write(", ");
                                    writeRemainingCritical(writer, fTemp);
                                    writer.write(".  Values in database: ");
                                    writeSccMaterial(writer, sameSccMaterialRow);
                                    writer.write(", ");
                                    writeRemainingCritical(writer, sameSccMaterialRow);
                                    writer.write(".\n\n");

                                    writeError(errWriter, errStr);
                                    writeSccMaterial(errWriter, fTemp);
                                    writeError(errWriter, ", ");
                                    writeRemainingCritical(errWriter, fTemp);
                                    writeError(errWriter, ".  Values in database: ");
                                    writeSccMaterial(errWriter, sameSccMaterialRow);
                                    writeError(errWriter, ", ");
                                    writeRemainingCritical(errWriter, sameSccMaterialRow);
                                    writeError(errWriter, ".\n\n");
                                    skippedCnt++;
                                }
                            }
                            if (existingRow.length == 1  && addMode) { // Row already exists
                                // No action will be taken
                                String origId = fTemp.getFactorId();
                                writer.write("Skipped Fire row (since already in database) with factorId(in file)=" + origId
                                        + " and factorId(in database)=" + existingRow[0].getFactorId() + " with values of: ");
                                writeSccMaterial(writer, fTemp);
                                writer.write(", ");
                                writeRemainingCritical(writer, fTemp);
                                writer.write("\n\n");
                                duplicateInDB++;
                            } 
                            if(existingRow.length == 0){ // No existing row matching all critical fields.
                                StringBuffer errorMsgSccMat = new StringBuffer();
                                if(sameSccMaterialRow != null) { // found a row to check against
                                    // Confirm that Action and Measure match existing entries.
                                    // Only need to compare one since existing ones do match.
                                    if (!compareStrings(sameSccMaterialRow.getAction(), fTemp.getAction())) {
                                        errorMsgSccMat.append("Action's don't match ");
                                    }
                                    if (!compareStrings(sameSccMaterialRow.getMeasure(), fTemp.getMeasure())) {
                                        errorMsgSccMat.append("Material Unit Code's don't match ");
                                    }
                                }
                                if (errorMsgSccMat.length() != 0) {
                                    // Attempt to add row with different action or measure for same scc
                                    // material already in database--reject
                                    String errStr = "SKIPPED factorId=" + fTemp.getFactorId() + ", because "
                                    + errorMsgSccMat.toString() + " the action/measure of other records already in FIRE with the same scc and material.  Values in file: ";
                                    writer.write(errStr);
                                    writeSccMaterial(writer, fTemp);
                                    writer.write(", ");
                                    writeRemainingCritical(writer, fTemp);
                                    writer.write(".  Values in database: ");
                                    writeSccMaterial(writer, sameSccMaterialRow);
                                    writer.write(", ");
                                    writeRemainingCritical(writer, sameSccMaterialRow);
                                    writer.write(".\n\n");

                                    writeError(errWriter, errStr);
                                    writeSccMaterial(errWriter, fTemp);
                                    writeError(errWriter, ", ");
                                    writeRemainingCritical(errWriter, fTemp);
                                    writeError(errWriter, ".  Values in database: ");
                                    writeSccMaterial(errWriter, sameSccMaterialRow);
                                    writeError(errWriter, ", ");
                                    writeRemainingCritical(errWriter, sameSccMaterialRow);
                                    writeError(errWriter, ".\n\n");
                                    skippedCnt++;
                                } else {
                                    // locate a unique fire Id.
                                    boolean notUnique = true;
                                    String origId = fTemp.getFactorId();
                                    while(notUnique) {
                                        String id = fTemp.getFactorId();
                                        FireRow idFound = erBO.retrieveFireRow(fTemp.getFactorId());
                                        if(idFound == null) {
                                            notUnique = false;
                                        } else {
                                            // try another Id.
                                            writer.write("FactorId=" + id + " not unique.");
                                            int dotLoc = id.lastIndexOf('.');
                                            if(dotLoc < 0) {
                                                // no suffix
                                                fTemp.setFactorId(id + ".2");
                                            } else {
                                                // increment the suffix
                                                String num = id.substring(dotLoc + 1, id.length());
                                                int nxtNum = Integer.parseInt(num) + 1;
                                                fTemp.setFactorId(id.substring(0, dotLoc + 1) + nxtNum);
                                                writer.write(" ");

                                            }
                                            writer.write("  Try FactorId=" + fTemp.getFactorId() + "\n");
                                        }
                                    }
                                    // If the new row does not have create date/year set, set it to current year.
                                    // if it is set, then leave the creat date/year as is.
                                    // Note that if a new row has deprecate date/year set, then leave it as it is.
                                    if(fTemp.getCreated() == null) {
                                        fTemp.setCreated(year);
                                    }
                                    writer.write("CREATE new Fire row with factorId(in file)=" + origId
                                            + " and factorId(in database)=" + fTemp.getFactorId() + " to the values of: ");
                                    writeSccMaterial(writer, fTemp);
                                    writer.write(", ");
                                    writeRemainingCritical(writer, fTemp);
                                    writer.write("\n\n");
                                    if (updateMode) {   
                                        erBO.createFireRow(fTemp);
                                    }
                                    newCnt++;
                                }
                            } 
                            if(existingRow.length > 1){ // found mulitiples
                                String errS = "ERROR: duplicates found " +  existingRow.length + " FIRE rows with factorId(in file)=" +
                                fTemp.getFactorId()
                                + " and factorIds(in database)=" + existingRow[0].getFactorId();
                                String errS2 = "";
                                for(int i = 1; i < existingRow.length; i++) {
                                    errS2 = errS2 + ", " + existingRow[i].getFactorId();
                                }
                                String errS3 =  ";  Values of scc=" +
                                fTemp.getSccId() + ", materialCd=" + fTemp.getMaterial() + ", pollutantCd=" +
                                fTemp.getPollutantCd() + ", factor=" + nullToBlank(fTemp.getFactor()) + ", formula=" + 
                                nullToBlank(fTemp.getFormula()) + ".  This should never happen.\n\n";
                                writer.write(errS + errS2 + errS3);
                                writeError(errWriter, errS + errS2 + errS3);
                                duplicateInDB++;
                            }
                        }catch (DAOException de) {
                            String s = "\n\nDAOException:  prevFactorId=" + prevFactorId +
                            ", rowCt=" + (rowCt + 1) + "\n";
                            logger.error(s, de);
                            try {
                                writeError(errWriter, s);
                                errWriter.flush();
                            } catch(IOException ee) {
                                logger.error("Unable to write to log files", ee);
                            }
                            populateErrorDisplay(writer, errWriter, de);
                            return;
                        }
                    } else { // msg.length() > 0
                        validationCnt++;
                        String errStr = "Validation error with Factor ID " + fTemp.getFactorId() + " Has following Errors: " + msg + ".  Values in file: ";
                        writer.write(errStr);
                        writeSccMaterial(writer, fTemp);
                        writer.write(", ");
                        writeRemainingCritical(writer, fTemp);
                        writer.write("\n\n");

                        writeError(errWriter, errStr);
                        writeSccMaterial(errWriter, fTemp);
                        writeError(errWriter, ", ");
                        writeRemainingCritical(errWriter, fTemp);
                        writeError(errWriter, "\n\n");
                    }
                } else if(values != null) {
                    String errStr = "Row too short for FactorID=" + values[0][0] +
                    ", number of fields=" + values[0].length + ".  Row skipped\n\n";
                    writer.write(errStr);
                    writeError(errWriter, errStr);
                    tooShortCnt++;
                }
                rowCt++;
                values = readRecord(writer, errWriter, br);
            }

            FireRow[] deprecatedFireRows = null;
            if(!addMode) {
                try {

                    deprecatedFireRows = erBO.deprecatedFireRows(year);
                    if(deprecatedFireRows == null) {
                        populateErrorDisplay(writer, errWriter, "Unable to retrieve deprecated fire rows for year " + year);
                        return;
                    }
                } catch (DAOException de) {
                    logger.error("Unable to retrieve deprecated fire rows for year " + year, de);
                    populateErrorDisplay(writer, errWriter, de);
                    return;
                } catch (RemoteException re) {
                    logger.error("Unable to retrieve deprecated fire rows for year " + year, re);
                    populateErrorDisplay(writer, errWriter, re);
                    return;
                }


                for(FireRow fr : deprecatedFireRows) {
                    writer.write("Deprecated Fire row for " + year + " with factorId(in database)=" + fr.getFactorId() + " and values: ");
                    writeSccMaterial(writer, fr);
                    writer.write(", ");
                    writeRemainingCritical(writer, fr);
                    writer.write("\n\n");
                }
            }
            StringBuffer results = new StringBuffer(1000);
            results.append("Results of processing file <b>" + fireFileInfo.getFileName()  + "</b> and operation <b>" + operation + " :</b><br><br>");
            results.append(activeRows + " Number of active Fire rows before import (not deprecated)<br>");
            if(!addMode) {
                results.append(modifyCnt + " Modified records<br>");
            }
            results.append(newCnt + " New records<br>");
            results.append(skippedCnt + " Records Skipped because Action or Measure did not match existing records<br>"); // action or measure differ for same scc and material
            results.append(validationCnt + " Records skipped because of Validation Problems<br>");
            results.append(tooShortCnt + " Records skipped because too short (not enough fields)<br>");
            results.append(duplicateInDB + " Records skipped because duplicate records in database<br>");
            if(!addMode) {
                results.append(deprecatedFireRows.length + " Records with deprecated year of " + year + "<br>");
            }
            results.append(rowCt + " Total rows processed from import file<br>");

            writer.write("Results of processing file " + fireFileInfo.getFileName()  + " and operation " + operation + " :\n");
            writer.write("\n\n" + activeRows + " Number of active Fire rows before import (not deprecated)");
            if(!addMode) {
                writer.write("\n" + modifyCnt + " Modified records.  (This does not count rows that change create year)\n");
            }
            writer.write(newCnt + " New records\n");
            writer.write(skippedCnt + " Records Skipped because Action or Measure did not match existing records\n"); // action or measure differ for same scc and material
            writer.write(validationCnt + " Records skipped because of Validation Problems\n");
            writer.write(tooShortCnt + " Records skipped because too short (not enough fields)\n");
            writer.write(duplicateInDB + " Records skipped because duplicate records in database\n");
            if(!addMode) {
                writer.write(deprecatedFireRows.length + " Records with deprecated year of " + year + "\n");
            }
            writer.write(rowCt + " Total rows processed from import file\n");

            try {
                activeRows = erBO.activeFireRows();
                if(activeRows == null) {
                    logger.error("Unable to count active fire rows");
                    populateErrorDisplay(writer, errWriter, "Unable to count active fire rows");
                    return;
                }
            } catch (DAOException de) {
                logger.error("Unable to count active fire rows", de);
                populateErrorDisplay(writer, errWriter, de);
                return;
            } catch (RemoteException re) {
                logger.error("Unable to count active fire rows", re);
                populateErrorDisplay(writer, errWriter, re);
                return;
            }
            results.append(activeRows + " Number of active Fire rows after import (not deprecated)");
            displayResults = results.toString();
            writer.write(activeRows + " Number of active Fire rows after import (not deprecated)");

            writer.write("\n\nVariables used in formulas:\n");
            String blanks = "                ";  // 16 blanks
            for(String v : variables) {
                String myBlanks = "";
                if(v.length() >= 1) {
                    myBlanks = blanks.substring(v.length() - 1);
                }
                writer.write(v + myBlanks);
                String meaning = FireVariableNamesDef.getData().getItems().getItemDesc(v);
                if(meaning == null) {
                    meaning = "W A R N I N G :   NO DESCRIPTION GIVEN TO THE VARIABLE NAME " +
                    v + ".";
                }
                writer.write(meaning + "\n");
            }
            writer.write("\nGO TO ADMIN->DEFINITIONS->EMISSIONS INVENTORIES->FIRE FORMULA VARIABLES to add/change meanings/descriptions of the variables");
            writer.close();
            errWriter.close();
        } catch (IOException e) {
            String s = "\n\nIOException:  prevFactorId=" + prevFactorId +
            ", rowCt=" + (rowCt + 1) + "\n";
            logger.error(s, e);
            try {
                writeError(errWriter, s);
                errWriter.flush();
            } catch(IOException ee) {
                logger.error("Unable to write err log", ee);
            }
            populateErrorDisplay(writer, errWriter, e);
            return;
        } catch (Exception e) {
            logger.error("Exception generated: ", e);
            try {
                writeError(errWriter, "Exception generated: " + e.getMessage());
                errWriter.flush();
            } catch(IOException ee) {
                logger.error("Unable to write err log", ee);
            }
            populateErrorDisplay(writer, errWriter, e);
            return;
        }

        if (updateMode && !addMode) {
            try {
                erBO.removeInvalidFireRows(year);
            } catch (DAOException de) {
                logger.error("Unable to removeInvalieFireRows", de);
                populateErrorDisplay(writer, errWriter, de);
                return;
            } catch (RemoteException re) {
                logger.error("Unable to removeInvalieFireRows", re);
                populateErrorDisplay(writer, errWriter, re);
                return;
            }
        }

        return;
    }

    private void writeError(FileWriter errWriter, String str)
    throws IOException {
        noErrors = false;
        errWriter.write(str);
    }

    private void populateErrorDisplay(FileWriter writer, FileWriter errWriter, Exception e) {
        displayErr = "An error has occurred:  " +
        e.getMessage();
        try {
            writer.close();
            errWriter.close();
        } catch(Exception ee) {
            ;
        }
    }

    private void populateErrorDisplay(FileWriter writer, FileWriter errWriter, String s) {
        displayErr = "An error has occurred:  " + s;
        try {
            writer.close();
            errWriter.close();
        } catch(Exception e) {
            ;
        }
    }

    // Convert string to Integer. If invalid, return -1.
    // If not set, return null
    private Integer convert(String s) {
        Integer v;
        if (s != null) {
			s = s.replaceAll(" ", "");
		}
        if (null == s || s.trim().length() == 0) {
            return null;
        }
        try {
			v = Integer.valueOf(s);
		} catch (NumberFormatException e) {
			v = new Integer(-1);
		}
        
        return v;
    }
    
    private Integer getYear(String d) {
		int ret;
		
		if (d != null) {
			d = d.replaceAll(" ", "");
		}

		if (null == d || d.trim().length() == 0) {
			return null;
		}

		try {
			DateFormat df = new SimpleDateFormat("M/d/yyyy", Locale.ENGLISH);
			Calendar c = Calendar.getInstance();
			c.setTime(df.parse(d));
			ret = c.get(Calendar.YEAR);
		} catch (ParseException e) {
			if(d.trim().length() == 4) {
				// might already be year
				try {
					ret = Integer.parseInt(d);
				} catch (NumberFormatException ne) {
					ret = -1;
				}
			} else {
				ret = -1;
			}
		}

		return ret;
    }

    private static boolean compareStrings(String stringOne, String stringTwo) {
        boolean ret = false;

        if (stringOne == null && stringTwo == null) {
            ret = true;
        } else if (stringTwo != null && (stringOne == null && stringTwo.equals(""))) {
            ret = true;
        } else if (stringOne != null && (stringOne.equals("") && stringTwo == null)) {
            ret = true;
        } else if (stringOne != null && stringOne.equals(stringTwo)) {
            ret = true;
        }

        return ret;
    }

    private void writeSccMaterial(FileWriter writer, FireRow r) throws IOException {
        writer.write("scc=" + r.getSccId() + ", MaterialCd=" + r.getMaterial());
    }

    private void writeRemainingCritical(FileWriter writer, FireRow r) throws IOException {
        writer.write("pollutantCd=" + r.getPollutantCd() + ", factor=" + nullToBlank(r.getFactor()) +
                ", formula=" + nullToBlank(r.getFormula()) + ", action=" + r.getAction() + 
                ", measure=" + r.getMeasure() +
                ", created=" + nullToBlank(r.getCreated()) + 
                ", deprecated=" + nullToBlank(r.getDeprecated()));
    }

    private String nonNullValue(String s) {
        String rtn = s;
        if(s == null) {
            rtn = "";
        }
        return rtn;
    }
    
    private String nullToBlank(Integer i) {
        if(i == null) {
            return "";
        } else {
            return i.toString();
        }
    }
    
    private String nullToBlank(String s) {
        if(s == null) {
            return "";
        } else {
            return s;
        }
    }

    private StringBuffer validateRow(FileWriter writer, FileWriter errWriter, FireRow row) 
    throws Exception {
        StringBuffer failMess = new StringBuffer();
        // Check SCC
        if(row.getSccId() != null && row.getSccId().length() > 0) {
            try {
                InfrastructureService dBO = ServiceFactory.getInstance().getInfrastructureService();
                SccCode sccCode = dBO.retrieveSccCode(row.getSccId());
                if (sccCode == null) {
                    failMess.append("\tSCC value \"" + row.getSccId() + "\" not found. ");
                }

            } catch (DAOException e) {
                logger.error("Unable to retrieveSccCode " + row.getSccId(), e);
                populateErrorDisplay(writer, errWriter, e);
                throw e;
            } catch (ServiceFactoryException sfe) {
                logger.error("Unable to retrieveSccCode " + row.getSccId(), sfe);
                populateErrorDisplay(writer, errWriter, sfe);
                throw sfe;
            } catch (RemoteException re) {
                logger.error("Unable to retrieveSccCode " + row.getSccId(), re);
                populateErrorDisplay(writer, errWriter, re);
                throw re;
            }
        }

        // Check Material
        boolean found = false;
        boolean isNumber = true;
        try {
            int isNum = Integer.parseInt(row.getMaterial());
        } catch (NumberFormatException e) {
            isNumber = false;
        }
        if(isNumber) {
            for (SelectItem i : MaterialDef.getData().getItems().getAllItems()) {
                if (i.getValue().toString().equalsIgnoreCase(row.getMaterial())) {
                    found = true;
                    break;
                }
            }
            if (!found) {
                failMess.append("\tMaterial code \"" + row.getMaterial() + "\" not found. ");
            }
        } else { // Covert from description to code
            for (SelectItem i : MaterialDef.getData().getItems().getAllItems()) {
                if (i.getLabel().equalsIgnoreCase(row.getMaterial())) {
                    row.setMaterial(i.getValue().toString());
                    found = true;
                    break;
                }
            }
            if (!found) {
                failMess.append("\tNo code for material \"" + row.getMaterial() + "\" found. ");
            }
        }

        // Check Action
        found = false;
        for (SelectItem i : ActionsDef.getData().getItems().getAllItems()) {
            if (i.getLabel().equalsIgnoreCase(row.getAction())) {
                found = true;
                break;
            }
        }
        String action = ActionsDef.getData().getItems().getItemDesc(row.getAction());
        if (null == action) {
            failMess.append("\tNo code for action \"" + row.getAction() + "\" found. ");
        }

        // Check material units
        row.setMeasure(row.getMeasure().toUpperCase());
        boolean foundMeasure = false;
        
        for (SelectItem i : UnitDef.getData().getItems().getAllItems()) {
            if (i.getValue().equals(row.getMeasure())) {
                row.setMeasure(i.getValue().toString());
                foundMeasure = true;
                break;
            }
        }
        if (!foundMeasure) {
        	failMess.append("\tMeasure \"" + row.getMeasure() + "\" not found. ");
        }
        
        // Previous method used to check measure
        // This method was dependent on the IMPACT measure code being used
//        String meas_desc = UnitDef.getData().getItems().getItemDesc(row.getMeasure());
//        if (null == meas_desc) {
//            failMess.append("\tMeasure \"" + row.getMeasure() + "\" not found. ");
//        }

        // Check pollutant
        if (row.getPollutantCd().length() == 0) {
            if (row.getFactor().length() != 0 || row.getFormula().length() != 0 || row.getUnit().length() != 0) {
                // failMess.append("\tPollutant code not specified, but factor, formula or units included ");
            }
        } else {
            String pollutant_desc = PollutantDef.getData().getItems().getItemDesc(row.getPollutantCd());
            if (null == pollutant_desc) {
                failMess.append("\tPollutant code \"" + row.getPollutantCd() + "\" not found. ");
            }
        }

        // Check emissions units
        row.setUnit(row.getUnit().toUpperCase());
        String unit_desc = UnitDef.getData().getItems().getItemDesc(row.getUnit());
        if (null == unit_desc) {
            if(row.getPollutantCd().length() != 0) {
                failMess.append("\tUnit code \"" + row.getUnit() + "\" not found. ");
            }
        } else if(unit_desc.equals("LB")) {
            failMess.append("\tUnit code \"" + row.getUnit() + "\" is not pounds (LB). ");
        }

        // Check factor field
        if(row.getFactor() != null && row.getFactor().length() != 0) {
            try {
                double v = EmissionsReport.convertStringToNum(row.getFactor(), logger);
                if(v < 0) {
                    failMess.append("\tfactor cannot be negative :  " + row.getFactor());
                }
            } catch(Exception e) {
                failMess.append("\tfactor problem :  " + row.getFactor());
            }
        }

        // Check formula field
        if (row.getFormula().length() != 0) {
            String s = ExpressionEval.lowerToUpper(row.getFormula());
            if (!ExpressionEval.upperToLower(s).equals(row.getFormula())) {
                failMess.append("\tformula problem :  Cannot preserve lower case letters.  Attempt to Substitute lower case letter with upper case letter followed by \"__\" (two underscores) resulted in:  \"" + s + "\".  Original formula is \"" + row.getFormula() + "\".");
            } else {
                ExpressionEval eE = ExpressionEval.createFromFactory();
                eE.setExpression(row.getFormula());
                String[] vars = eE.getVariables();
                if (eE.getErr() != null) {
                    failMess.append("\tformula problem :  " + eE.getErr());
                }

                if (eE.getErr() == null) {
                    // Put values in variables to test formula
                    for (int i = 0; i < vars.length; i++) {
                        eE.setVariable(vars[i], (i + 1));
                        if (eE.getErr() != null) {
                            failMess.append("\tformula problem :  " + eE.getErr());
                            break;
                        } else {
                            variables.add(ExpressionEval.upperToLower(vars[i])); // keep track of all variables
                        }
                    }
                }

                if (eE.getErr() == null) {
                    if (eE.getErr() != null) {
                        failMess.append("\tformula problem :  " + eE.getErr());
                    }
                }
            }
        }

        // Check create year
        if (row.getCreated() != null && row.getCreated().intValue() == -1) {
            failMess.append("\tcreated year is not an integer ");
        }

        // Check depreacated year
        if (row.getDeprecated() != null && row.getDeprecated().intValue() == -1) {
            failMess.append("\tdeprecated year is not an integer ");
        }
        
		if (!Utility.isNullOrEmpty(getSccId())) {
			if (!Utility.isNullOrEmpty(row.getSccId())) {
				if (!row.getSccId().trim().equalsIgnoreCase(getSccId())) {
					failMess.append("\tSCC value \"" + row.getSccId() + "\" does not match selected SCC ID \""
							+ getSccId() + "\". ");
				}
			}

			if (!Utility.isNullOrEmpty(getMaterialCds())) {
				if (!Utility.isNullOrEmpty(row.getMaterial())) {
					if (!getMaterialCds().contains(row.getMaterial())) {
						failMess.append("\tMaterial value \"" + row.getMaterial()
								+ "\" does not match any selected Material \"" + getMaterialCds().toString() + "\". ");
					}

				}
			}

			if (!Utility.isNullOrEmpty(getPollutantCds())) {
				if (!Utility.isNullOrEmpty(row.getPollutantCd())) {
					if (!getPollutantCds().contains(row.getPollutantCd())) {
						failMess.append("\tPollutant value \"" + row.getPollutantCd()
								+ "\" does not match any selected Pollutant \"" + getPollutantCds().toString()
								+ "\". ");
					}

				}
			}
		}

        return failMess;
    }

    void updateProgress(String rec) {
        if(rec != null) {
            fileLengthRead = fileLengthRead + rec.length();
            percentProg = (fileLengthRead * 100)/fileLength;
            this.setValue(percentProg);
        }
    }

    public Integer getImportChoice() {
        return importChoice;
    }

    public void setImportChoice(Integer importChoice) {
        this.importChoice = importChoice;
    }

    public Integer getFileStatus() {
        return fileStatus;
    }

    public UploadedFile getFireFileToUpload() {
        return fireFileToUpload;
    }

    public void setFireFileToUpload(UploadedFile fireFileToUpload) {
        this.fireFileToUpload = fireFileToUpload;
    }

    public void setFileStatus(Integer fileStatus) {
        this.fileStatus = fileStatus;
    }

    class RunImport extends Thread {
        public void run() {
            String filePath  = getNodeValue("app.fileStore.rootPath");
            String logFile = filePath + File.separatorChar + logFileString;
            String errFile = filePath + File.separatorChar + errFileString;
            fireUpdater.importFireData(fireFileInfo, logFile, errFile);
            fileStatus = new Integer(2); // have results
            setPopupRedirect("importFire");
            refreshStr = " ";
        }
    }
    
	private String getNodeValue(String node) {
		// TODO should use ConfigManager or Config???
		// ConfigManager cfgMgr = ConfigManagerFactory.configManager();

		String value = null;
		String jndiName = Config.findNode(node).getAsString("jndiName");
		try {
			// TODO potential optimization opportunity?
			Context initContext = new InitialContext();
			Context envContext = (Context) initContext.lookup("java:/comp/env");
			value = (String) envContext.lookup(jndiName);
			logger.debug("node value = " + value);
		} catch (NamingException ne) {
			throw new RuntimeException(ne);
		}
		return value;
	}

    public String getRefreshStr() {
        return refreshStr;
    }

    public long getPercentProg() {
        return percentProg;
    }

    public String getDisplayResults() {
        return displayResults;
    }

    public Document getLogFileDoc() {
        logFileDoc.setBasePath(logFileString);
        return logFileDoc;
    }

    public Document getErrFileDoc() {
        errFileDoc.setBasePath(errFileString);
        return errFileDoc;
    }

    public boolean isLogFileExists(){
    	if(DocumentUtil.isFileExists(File.separator+logFileString)){
    		return true;
    	}else{
    		return false;
    	}
    }

    public boolean isErrFileExists(){
    	if(DocumentUtil.isFileExists(File.separator+errFileString)){
    		return true;
    	}else{
    		return false;
    	}
    }


    public String getHowFireWorks() {
        if(howFireWorks == null || howFireWorks.length() == 0) {
            StringBuffer operate = new StringBuffer(1000);
            operate.append("The YEAR value used for create and deprecate is the current year minus one (" + 
                    year + ").<br>First all FIRE rows in the database have the deprecate field set to " + 
                    year + " if null (not already deprecated)<br><br>Each record of the input file is read and processed one at a time:<br><br>");
            operate.append("The FIRE database is queried for any rows that match the input record in SCC, Material, Pollutant, Factor and Formula and which are currently not deprecated.&nbsp; &nbsp;(Not deprecated means the deprecate field is null or has value larger than " + 
                    year + "--all the active rows will initally appear deprecated).<br><br>If there is such a row retrieved it means that the critical Fire row information has not changed, it is re-written with these same values and the remaining field values from the input record.<br><br>EXCEPT:");
            operate.append("<ul><li>If the Action or Measure do not match the input record is skipped.</li>");
            operate.append("<li>If the input record contains a non null create value, that value replaces the value in the database record retrieved.&nbsp; &nbsp;(Normally the input record will have no value for created and whatever value was in the database row will remain).</li>");
            operate.append("<li>Note also that the deprecate field is set from the input record.&nbsp; &nbsp;This allows you to explicitly set the deprecate year but normally this is left empty so the fire row is valid into the future.</li></ul>");
            operate.append("Rewriting this Fire row has three purposes:");
            operate.append("<ul><li>Undo the effect of the deprecate done in the 1st step for this Fire row,</li>");
            operate.append("<li>Update all the unimportant fields in case they have changed and</li>");
            operate.append("<li>Provide an opportunity to manually supply create or deprecate although normally they will be left blank and set appropriately by this import program.</li></ul>");
            operate.append("The fire row is rewritten with the same Fire_id that it had in the database, not the value from the input record.<br><br>");
            operate.append("If there is no such row (no row retrived), then this is a new Fire row.&nbsp; &nbsp;A check is made to locate a non-deprecated row from the FIRE  database that has the same SCC and Material.&nbsp; &nbsp;If such a row exists, then; if it does not match in Action and Measure, the input record is skipped.&nbsp; &nbsp;It is written to the database as a new Fire row:");
            operate.append("<ul><li>If the create field is empty then " + 
                    year + " is used (normally it will be empty but this allows you to explicitly set the create year).</li>");
            operate.append("<li>Whatever is in the deprecate field is written (it is normally empty to indicate valid into the future.)</li>");
            operate.append("<li>The Fire_id used will be the value from the input record, if it is unique, otherwise the id has a suffix (\".n\") appended to the end--where the integer \"n\" makes it unique.</li></ul>");
            operate.append("Note that at the end of this import, Fire rows are left deprecated that are not updated--did not exist in the input import file processed.<br><br>");
            operate.append("First FIRE Import should be run in analyze mode multilple times until all problems are corrected.&nbsp; &nbsp;Then it should be run to actually perform the updates.<br><br>");
            operate.append("If for some reason there are still errors in the input import file or the program fails to complete, it can be rerun until it runs to completion with no error messages (If adding new records only, those already added will fail to be added a second time and marked as duplicate in the error log).<br><br>");
            operate.append("Caution: during the brief time that a FIRE Import is being run, some, many or all the fire rows will be marked deprecated.&nbsp; &nbsp;It should be run when users are least likely to be updating SMTV and TV emissions inventories.<br><br>");
            operate.append("If instead of a full import, only new entries are added, then everything else is ignored.<br><br>");
            operate.append("Note that if only analyze is being performed there may be some errors recognized that are not actually errors.&nbsp;&nbsp;This is because the first step of deprecating all active rows is not performed.");
            howFireWorks = operate.toString();
        }
        return howFireWorks;
    }

    public boolean isNoErrors() {
        return noErrors;
    }

    public String getDisplayErr() {
        return displayErr;
    }  
    
	public String getPopupRedirect() {
		if (popupRedirect != null) {
			FacesUtil.setOutcome(null, popupRedirect);
			popupRedirect = null;
		}
		return null;
	}
	
	public final void setPopupRedirect(String popupRedirect) {
		this.popupRedirect = popupRedirect;
	}
	
	public String getHelpFilePath() {
		String filePath = getNodeValue("app.fileStore.urlPath");
		String helpFile = DocumentUtil.getFileStoreBaseURL() + FIRE_IMPORT_PATH
				+ File.separatorChar + HELP_FILE_NAME;
		return helpFile;
	}
	
	public String initReportsImportFire() {
		initImportFire();
		return "reportsImportFire";
	}
	
	public String getSccId() {
		return sccId;
	}
	
	public void setSccId(String sccId) {
		this.sccId = sccId;
	}

	public void setMaterialCds(List<String> materialCds) {
		this.materialCds = materialCds;
	}
	
	public List<String> getMaterialCds() {
		if(null == materialCds) {
			setMaterialCds(new ArrayList<String>());
		}
		return materialCds;
	}
	
	public void setPollutantCds(List<String> pollutantCds) {
		this.pollutantCds = pollutantCds;
	}
	
	public List<String> getPollutantCds() {
		if(null == pollutantCds) {
			setPollutantCds(new ArrayList<String>());
		}
		return pollutantCds;
	}
	
	public FacilityReference getFacilityReference() {
		return facilityReference;
	}

	public void setFacilityReference(FacilityReference facilityReference) {
		this.facilityReference = facilityReference;
	}
	
	public final List<SelectItem> getSccMaterials() {
		List<SelectItem> ret = MaterialDef.getSccMaterials(sccId);
		for (SelectItem si : ret)
			si.setDisabled(false);
		return ret;
	}
	
	public final List<SelectItem> getSccPollutants() {
		List<SelectItem> ret = PollutantDef.getSccPollutants(sccId, getMaterialCds());
		for (SelectItem si : ret)
			si.setDisabled(false);
		return ret;
	}
	
	public void sccChanged(ValueChangeEvent event) {
		setMaterialCds(null);
		setPollutantCds(null);
		setImportChoice(null);
	}
	
	public int getMaterialCdsLength() {
		return getMaterialCds().size();
	}
	
	public int getPollutantCdsLength() {
		return getPollutantCds().size();
	}
	
	
}
