package us.oh.state.epa.stars2.database.dbObjects.emissionReport;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import javax.faces.model.SelectItem;

import org.apache.log4j.Logger;

import us.oh.state.epa.stars2.database.dao.EmissionsReportDAO;
import us.oh.state.epa.stars2.database.dao.InfrastructureDAO;
import us.oh.state.epa.stars2.database.dao.Transaction;
import us.oh.state.epa.stars2.database.dao.TransactionFactory;
import us.oh.state.epa.stars2.database.dbObjects.infrastructure.SccCode;
import us.oh.state.epa.stars2.def.ActionsDef;
import us.oh.state.epa.stars2.def.MaterialDef;
import us.oh.state.epa.stars2.def.PollutantDef;
import us.oh.state.epa.stars2.def.UnitDef;
import us.oh.state.epa.stars2.framework.exception.DAOException;
import us.oh.state.epa.stars2.webcommon.reports.ExpressionEval;
import us.wy.state.deq.impact.App;

import com.Ostermiller.util.ExcelCSVParser;

/*
 * This was used to generate INSERT statements to create the fire database
 * the first time.  Only used to initialize the fire database before STARS2
 * was turned on the first time.
 * 
 * The factorId field had to be defined as MS Excel type Special in order to store
 * (intreprete) all the digits rather than convert to scientific notation.
 */
public class FireImporter {
    private transient Logger logger = Logger.getLogger(this.getClass());
    
    /**
     * @param args
     */
    public static void main(String[] args) {
//        for (SelectItem i : MaterialDef.getData().getItems().getAllItems()) {
//            System.err.println(i.getLabel() + ", " + i.getValue().toString());
//        }
//
//        for (SelectItem i : UnitDef.getData().getItems().getAllItems()) {
//            System.err.println(i.getLabel() + ", " + i.getValue().toString());
//        }

        FileWriter outStream = null;

        String fireMessages = "fireMessages.txt";

        try {
            outStream = new FileWriter(new File(fireMessages));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        // TODO Auto-generated method stub

        FireImporter fi = new FireImporter();
        FireRow[] rows = fi.importFireData("c:\\projects\\fire.csv", outStream);
        try {
            outStream.write("Done importing. " + rows.length
                    + " rows processed\n");
            outStream.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public final FireRow[] importFireData(String filePath, FileWriter outStream) {
        String[][] values;
        FireRow[] fr = null;
        Transaction trans = null;
        FireRow fTemp = null;

//      EmissionsReportDAO emissionsRptDAO = (EmissionsReportDAO) DAOFactory
//        .getDAO("EmissionsReportDAO");
        //TODO db object calling a dao!?!?!
      EmissionsReportDAO emissionsRptDAO = App.getApplicationContext().getBean(EmissionsReportDAO.class);


        trans = TransactionFactory.createTransaction();
        emissionsRptDAO.setTransaction(trans);
        StringBuffer fullFile = new StringBuffer();
        try {
            FileReader reader = new FileReader(filePath);
            BufferedReader br = new BufferedReader(reader);
            String record = br.readLine();
            int rowCt = 0;
            while ((record != null) & (rowCt < 2000000)) {
                fullFile.append("\n");
                fullFile.append(record);
                rowCt++;
                record = br.readLine();
            }
        } catch(FileNotFoundException e){
            throw new RuntimeException(e);
        } catch(IOException e){
            throw new RuntimeException(e);
        }
        values = ExcelCSVParser.parse(fullFile.toString());
        logger.debug(values.length);
        if ((values != null) && (values.length > 0)) {
            fr = new FireRow[values.length];
            for (int i = 0; i < values.length; i++) {
                fTemp = new FireRow();
                String savFactorId = null;
                try {
                    fTemp.setFactorId(values[i][0]);
                    savFactorId = fTemp.getFactorId();
                    fTemp.setSccId(values[i][1]);
                    fTemp.setPollutantCd(values[i][2]);
                    if(null == fTemp.getPollutantCd()) {
                        fTemp.setPollutantCd("");
                    }
                    fTemp.setFactor(values[i][3]);
                    if(null == fTemp.getFactor()) {
                        fTemp.setFactor("");
                    }
                    fTemp.setFormula(values[i][4]);
                    if(null == fTemp.getFormula()) {
                        fTemp.setFormula("");
                    }
                    fTemp.setUnit(values[i][5]);
                    fTemp.setMeasure(values[i][6]);
                    fTemp.setMaterial(values[i][7]);
                    fTemp.setAction(values[i][8]);
                    String s = values[i][9];
                    //if (s.length() > 2000)
                    //    s = s.substring(0, 1999);
                    fTemp.setNotes(s);
                    fTemp.setQuality(values[i][10]);
                    fTemp.setOrigin(values[i][11]);
                    fTemp.setCas(values[i][12]);
                    fTemp.setPollutant(values[i][13]);
                    fTemp.setPollutantID(values[i][14]);
                    fTemp.setOrigFactor(values[i][15]);
                    fTemp.setOrigFormula(values[i][16]);
                    fTemp.setOrigUnit(values[i][17]);
                    fTemp.setOrigMeasure(values[i][18]);
                    fTemp.setOrigMaterial(values[i][19]);
                    fTemp.setOrigAction(values[i][20]);
                    s = values[i][21];
                    //if (s.length() > 2000)
                    //    s = s.substring(0, 1999);
                    fTemp.setOrigNotes(s);
                    fTemp.setOrigQuality(values[i][22]);
                    s = values[i][23];
                    //if (s.length() > 2000)
                    //    s = s.substring(0, 1999);
                    fTemp.setRefDesc(s);
                    fTemp.setAp42Section(values[i][24]);
                    fTemp.setCreated(convert(values[i][25]));
                    fTemp.setDeprecated(convert(values[i][26]));
                }
                catch (Exception e) {
                    try {
                        outStream.write("Got Exception " + e.getMessage() + 
                                ", at savFacId " + savFactorId +
                                ", FactorId " + fTemp.getFactorId());
                        for(StackTraceElement elm : e.getStackTrace()) {
                            outStream.write(elm.toString() + "\n");
                        }
                    } catch(Exception ex) {

                    }
                }

                /*
                 * for(int j = 0; j < 25; j++) { System.out.println(j + ":<" +
                 * values[i][j]+ ">(" + values[i][j].length() + ")"); }
                 */

                fr[i] = fTemp;
                StringBuffer msg = validateRow(fTemp);
                if (msg.length() == 0) {
                    // create the fire row
                    try {
                   //     emissionsRptDAO.createFireRow(fTemp);

                        outStream.write("INSERT INTO rp_fire_factor " + 
                            "(fire_id, scc_id, pollutant_cd, factor, formula, material_unit_cd, emissions_unit_cd, material_cd, " +
                            "action_cd, notes, quality, origin, cas, fire_pollutant, fire_pollutant_id, " +
                            "orig_factor, orig_formula, orig_material_unit, orig_emissions_unit_cd, orig_material, " +
                            "orig_action_cd, ap42_section, ref_desc, orig_notes, orig_quality, created, deprecated) " +
                            "VALUES (" + "'" +
                            fTemp.getFactorId().replace("'", "''") + "', '" +
                            fTemp.getSccId().replace("'", "''") + "', '" +
                            fTemp.getPollutantCd().replace("'", "''") + "', '" +
                            fTemp.getFactor().replace("'", "''") + "', '" +
                            fTemp.getFormula().replace("'", "''") + "', '" +
                            fTemp.getMeasure().replace("'", "''") + "', '" +
                            fTemp.getUnit().replace("'", "''") + "', '" +
                            fTemp.getMaterial().replace("'", "''") + "', '" +
                            fTemp.getAction().replace("'", "''") + "', '" +
                            fTemp.getNotes().replace("'", "''") + "', '" +
                            fTemp.getQuality().replace("'", "''") + "', '" +
                            fTemp.getOrigin().replace("'", "''") + "', '" +
                            fTemp.getCas().replace("'", "''") + "', '" +
                            fTemp.getPollutant().replace("'", "''") + "', '" +
                            fTemp.getPollutantID().replace("'", "''") + "', '" +
                            fTemp.getOrigFactor().replace("'", "''") + "', '" +
                            fTemp.getOrigFormula().replace("'", "''") + "', '" +
                            fTemp.getOrigMeasure().replace("'", "''") + "', '" +
                            fTemp.getOrigUnit().replace("'", "''") + "', '" +
                            fTemp.getOrigMaterial().replace("'", "''") + "', '" +
                            fTemp.getOrigAction().replace("'", "''") + "', '" +
                            fTemp.getAp42Section().replace("'", "''") + "', '" +
                            fTemp.getRefDesc().replace("'", "''") + "', '" +
                            fTemp.getOrigNotes().replace("'", "''") + "', '" +
                            fTemp.getOrigQuality().replace("'", "''") + "', '" +
                            (fTemp.getCreated()!=null?fTemp.getCreated():"") + "', '" +
                            (fTemp.getDeprecated()!=null?fTemp.getDeprecated():"") + "');\n" );
                    
                    } catch (DAOException e) {
                        try {
                        outStream.write("\nRow " + i +
                                " with Factor ID \"" + fTemp.getFactorId()
                                + "\" got DAOException: " + e);
                        } catch(IOException re){
                            throw new RuntimeException(re);
                        }
                        writeError(fTemp, i, outStream);
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    } 
                } else {
                    System.out.println("Validation failed on row " + i);
                    if (msg.length() > 0) {
                        try {
                            System.out.println("\nRow " + i +
                                    " with Factor ID \"" + fTemp.getFactorId()
                                    + "\" Has following Errors:\n" + msg);
                            System.out.flush();
                            outStream.write("\nRow " + i +
                                    " with Factor ID \"" + fTemp.getFactorId()
                                    + "\" Has following Errors:\n" + msg);
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    }
                }
            }
        }
        try {
            outStream.close();
            trans.complete();
        } catch (DAOException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return fr;
    }

    // Convert string to Integer.  If invalid, return -1.
    // If not set, return null
    Integer convert(String s) {
        int v;
        if(null == s || s.trim().length() == 0) {
            return null;
        }
        try {
            v = Integer.valueOf(s);
        }
        catch(NumberFormatException e) {
			if(s.trim().length() == 4) {
				// might already be year
				try {
					v = Integer.parseInt(s);
				} catch (NumberFormatException ne) {
					v = -1;
				}
			} else {
				v = -1;
			}
        }
        return new Integer(v);
    }

    private StringBuffer validateRow(FireRow row) {
        StringBuffer failMess = new StringBuffer();
        // Check SCC
        try {
//            InfrastructureDAO infraDAO = (InfrastructureDAO) DAOFactory
//            .getDAO("InfrastructureDAO");
            //TODO db object calling a dao!?!?!
        	InfrastructureDAO infrastructureDAO = App.getApplicationContext().getBean(InfrastructureDAO.class);
            SccCode sccCode = infrastructureDAO.retrieveSccCode(row.getSccId());
            if (sccCode == null) {
                failMess.append("\tSCC value \"" + row.getSccId()
                        + "\" not found.\n");
            }

        } catch (DAOException e) {
            throw new RuntimeException(e);
        }

        // Check Material
        boolean found = false;
        for (SelectItem i : MaterialDef.getData().getItems().getAllItems()) {
            if (i.getLabel().equalsIgnoreCase(row.getMaterial())) {
                row.setMaterial(i.getValue().toString());
                found = true;
                break;
            }
        }
        if (!found) {
            failMess.append("\tNo code for material \"" + row.getMaterial()
                    + "\" found.\n");
        }

        // Check Action
        found = false;
        for (SelectItem i : ActionsDef.getData().getItems().getAllItems()) {
            if (i.getLabel().equalsIgnoreCase(row.getAction())) {
                found = true;
                break;
            }
        }
        String action = ActionsDef.getData().getItems().getItemDesc(
                row.getAction());
        if (null == action) {
            failMess.append("\tNo code for action \"" + row.getAction()
                    + "\" found.\n");
        }

        // Check material units
        row.setMeasure(row.getMeasure().toUpperCase());
        String meas_desc = UnitDef.getData().getItems().getItemDesc(
                row.getMeasure());
        if (null == meas_desc) {
            failMess.append("\tMeasure \"" + row.getMeasure()
                    + "\" not found.\n");
        }
        
//      Check pollutant
        if(row.getPollutantCd().length() == 0) {
            if(row.getFactor().length() != 0 || row.getFormula().length() != 0){
                //    || row.getUnit().length() != 0){
                failMess.append("\tPollutant code not specified, but factor, formula or units included\n");
            }
            row.setUnit("");
        } else {
            String pollutant_desc = PollutantDef.getData().getItems().getItemDesc(
                    row.getPollutantCd());
            if (null == pollutant_desc) {
                failMess.append("\tPollutant code \"" + row.getPollutantCd()
                        + "\" not found.\n");
            }
            
//          Check emissions units
            row.setUnit(row.getUnit().toUpperCase());
            String unit_desc = UnitDef.getData().getItems().getItemDesc(
                    row.getUnit());
            if (null == unit_desc) {
                failMess.append("\tUnit code \"" + row.getUnit()
                        + "\" not found.\n");
            }
        }

        // TODO Check factor field

        // Check formula field
        if(row.getFormula().length() != 0) {
            String s = ExpressionEval.lowerToUpper(row.getFormula());
            if(!ExpressionEval.upperToLower(s).equals(row.getFormula())) {
                failMess.append("\tformula problem :  Cannot preserve lower case letters.  Attempt to Substitute lower case letter with upper case letter followed by \"__\" (two underscores) resulted in:  \"" +
                        s + "\".  Original formula is \"" + row.getFormula() + "\".");
            } else {
                ExpressionEval eE = ExpressionEval.createFromFactory();
                eE.setExpression(row.getFormula());
                String[] vars = eE.getVariables();
                if(eE.getErr() != null) {
                    failMess.append("\tformula problem :  " + eE.getErr());
                }
                
                if(eE.getErr() == null) {
                    // Put values in variables to test formula
                    for(int i=0; i < vars.length; i++) {
                        eE.setVariable(vars[i], (i+ 1));
                        if(eE.getErr() != null) {
                            failMess.append("\tformula problem :  " + eE.getErr());
                            break;
                        }
                    }
                }
                
                if(eE.getErr() == null) {
                    if(eE.getErr() != null) {
                        failMess.append("\tformula problem :  " + eE.getErr());
                    }
                }
            }
        }

        // Check create year
        if(row.getCreated() != null && row.getCreated().intValue() == -1) {
            failMess.append("\tcreated year is not an integer\n");
        }

        // Check depreacated year
        if(row.getDeprecated() != null && row.getDeprecated().intValue() == -1) {
            failMess.append("\tdeprecated year is not an integer\n");
        }

        // TODO Check that each material has only one action and material_units

        return failMess;
    }

    void writeError(FireRow fTemp,int i, FileWriter outStream) {
        try {
            outStream.write("row " + i + "");
            outStream.write("factorId=<" + fTemp.getFactorId() + ">("
                    + fTemp.getFactorId().length() + ")\n");
            outStream.write("sccId=<" + fTemp.getSccId() + ">("
                    + fTemp.getSccId().length() + ")\n");
            outStream.write("pollutantCd=<" + fTemp.getPollutantCd() + ">("
                    + fTemp.getPollutantCd().length() + ")\n");
            outStream.write("factor=<" + fTemp.getFactor() + ">("
                    + fTemp.getFactor().length() + ")\n");
            outStream.write("formula=<" + fTemp.getFormula() + ">("
                    + fTemp.getFormula().length() + ")\n");
            outStream.write("unit=<" + fTemp.getUnit() + ">("
                    + fTemp.getUnit().length() + ")\n");
            outStream.write("measure=<" + fTemp.getMeasure() + ">("
                    + fTemp.getMeasure().length() + ")\n");
            outStream.write("material=<" + fTemp.getMaterial() + ">("
                    + fTemp.getMaterial().length() + ")\n");
            outStream.write("action=<" + fTemp.getAction() + ">("
                    + fTemp.getAction().length() + ")\n");
            outStream.write("notes=<" + fTemp.getNotes() + ">("
                    + fTemp.getNotes().length() + ")\n");
            outStream.write("quality=<" + fTemp.getQuality() + ">("
                    + fTemp.getQuality().length() + ")\n");
            outStream.write("origin=<" + fTemp.getOrigin() + ">("
                    + fTemp.getOrigin().length() + ")\n");
            outStream.write("cas=<" + fTemp.getCas() + ">("
                    + fTemp.getCas().length() + ")\n");
            outStream.write("pollutant=<" + fTemp.getPollutant() + ">("
                    + fTemp.getPollutant().length() + ")\n");
            outStream.write("pollutantId=<" + fTemp.getPollutantID() + ">("
                    + fTemp.getPollutantID().length() + ")\n");
            outStream.write("origFactor=<" + fTemp.getOrigFactor() + ">("
                    + fTemp.getOrigFactor().length() + ")\n");
            outStream.write("origFormula=<" + fTemp.getOrigFormula() + ">("
                    + fTemp.getOrigFormula().length() + ")\n");
            outStream.write("origUnit=<" + fTemp.getOrigUnit() + ">("
                    + fTemp.getOrigUnit().length() + ")\n");
            outStream.write("origMeasure=<" + fTemp.getOrigMeasure() + ">("
                    + fTemp.getOrigMeasure().length() + ")\n");
            outStream.write("origMaterial=<" + fTemp.getOrigMaterial()
                    + ">(" + fTemp.getOrigMaterial().length() + ")\n");
            outStream.write("origAction=<" + fTemp.getOrigAction() + ">("
                    + fTemp.getOrigAction().length() + ")\n");
            outStream.write("origNotes=<" + fTemp.getOrigNotes() + ">("
                    + fTemp.getOrigNotes().length() + ")\n");
            outStream.write("origQuality=<" + fTemp.getOrigQuality() + ">("
                    + fTemp.getOrigQuality().length() + ")\n");
            outStream.write("ap42Section=<" + fTemp.getAp42Section() + ">("
                    + fTemp.getAp42Section().length() + ")\n");
            outStream.write("refDesc=<" + fTemp.getRefDesc() + ">("
                    + fTemp.getRefDesc().length() + ")\n");
            outStream.write("created=<" + fTemp.getCreated() + ">" + "\n");
            outStream.write("deprecated=<" + fTemp.getDeprecated() + ">"
                    + "\n");
        } catch(Exception e) {

        }
    }
}
