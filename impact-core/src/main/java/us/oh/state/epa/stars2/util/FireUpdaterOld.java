package us.oh.state.epa.stars2.util;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Calendar;

import javax.annotation.Resource;
import javax.faces.model.SelectItem;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;

import us.oh.state.epa.stars2.database.dao.EmissionsReportDAO;
import us.oh.state.epa.stars2.database.dao.InfrastructureDAO;
import us.oh.state.epa.stars2.database.dbObjects.emissionReport.FireRow;
import us.oh.state.epa.stars2.database.dbObjects.infrastructure.SccCode;
import us.oh.state.epa.stars2.def.ActionsDef;
import us.oh.state.epa.stars2.def.MaterialDef;
import us.oh.state.epa.stars2.def.PollutantDef;
import us.oh.state.epa.stars2.def.UnitDef;
import us.oh.state.epa.stars2.framework.exception.DAOException;
import us.oh.state.epa.stars2.webcommon.reports.ExpressionEval;

import com.Ostermiller.util.ExcelCSVParser;

/*
 * The factorId field had to be defined as MS Excel type Special in order to store
 * (intreprete) all the digits rather than convert to scientific notation.
 */
/*
 * THIS IS THE OLD VERION OF THE UPDATER PROGRAM.  THIS VERSION REQUIRED THAT THE
 * FIRE DATABASE BEING IMPORTED USED THE SAME FACTORS ALREADY IN ORACLE.
 */
@Component
public class FireUpdaterOld {
    private transient Logger logger = Logger.getLogger(this.getClass());

    private static boolean analyze = false;
    
    @Resource private EmissionsReportDAO readOnlyEmissionsReportDAO;
    @Resource private InfrastructureDAO readOnlyInfrastructureDAO;

    /**
     * @param args
     */
    public static void main(String[] args) {
        if (args[0].compareToIgnoreCase("analyze") == 0) {
            analyze = true;
        }

        FireUpdaterOld fi = new FireUpdaterOld();
        fi.importFireData(args[1], args[2]);
    }

    public final void importFireData(String inputFile, String exceptionFile) {
        String[][] values;
        FireRow fTemp = null;
        FileReader reader = null;
        FileWriter writer = null;
//        EmissionsReportDAO emissionRptDAO = (EmissionsReportDAO) DAOFactory.getDAO("EmissionsReportDAO");

        /* First we deprecate all the current entries in the DB */

        if (!analyze) {
            try {
            	readOnlyEmissionsReportDAO.deprecateFire(Calendar.getInstance().get(Calendar.YEAR));
            } catch (DAOException de) {
                throw new RuntimeException(de);
            }
        }

        /* Now read in all of the entries and process them */
        try {
            reader = new FileReader(inputFile);
            writer = new FileWriter(exceptionFile);

            BufferedReader br = new BufferedReader(reader);
            String record = br.readLine();
            int rowCt = 0;
            int newCnt = 0;
            int modifyCnt = 0;
            int nonMatchingCnt = 0;
            int validationCnt = 0;
            while ((record != null) & (rowCt < 2000000)) {
                values = ExcelCSVParser.parse(record);
                logger.debug(values.length);

                if ((values != null) && (values[0].length > 26)) {
                    fTemp = new FireRow();
                    String savFactorId = null;
                    try {
                        fTemp.setFactorId(values[0][0]);
                        savFactorId = fTemp.getFactorId();
                        fTemp.setSccId(values[0][1]);
                        fTemp.setPollutantCd(values[0][2]);
                        if (null == fTemp.getPollutantCd()) {
                            fTemp.setPollutantCd("");
                        }
                        fTemp.setFactor(values[0][3]);
                        if (null == fTemp.getFactor()) {
                            fTemp.setFactor("");
                        }
                        fTemp.setFormula(values[0][4]);
                        if (null == fTemp.getFormula()) {
                            fTemp.setFormula("");
                        }
                        fTemp.setUnit(values[0][5]);
                        fTemp.setMeasure(values[0][6]);
                        fTemp.setMaterial(values[0][7]);
                        fTemp.setAction(values[0][8]);
                        String s = values[0][9];
                        // if (s.length() > 2000)
                        // s = s.substring(0, 1999);
                        fTemp.setNotes(s);
                        fTemp.setQuality(values[0][10]);
                        fTemp.setOrigin(values[0][11]);
                        fTemp.setCas(values[0][12]);
                        fTemp.setPollutant(values[0][13]);
                        fTemp.setPollutantID(values[0][14]);
                        fTemp.setOrigFactor(values[0][15]);
                        fTemp.setOrigFormula(values[0][16]);
                        fTemp.setOrigUnit(values[0][17]);
                        fTemp.setOrigMeasure(values[0][18]);
                        fTemp.setOrigMaterial(values[0][19]);
                        fTemp.setOrigAction(values[0][20]);
                        s = values[0][21];
                        // if (s.length() > 2000)
                        // s = s.substring(0, 1999);
                        fTemp.setOrigNotes(s);
                        fTemp.setOrigQuality(values[0][22]);
                        s = values[0][23];
                        // if (s.length() > 2000)
                        // s = s.substring(0, 1999);
                        fTemp.setRefDesc(s);
                        fTemp.setAp42Section(values[0][24]);
                        fTemp.setCreated(convert(values[0][25]));
                        fTemp.setDeprecated(convert(values[0][26]));
                    } catch (Exception e) {
                        System.err.println("Got Exception " + e.getMessage() + ", at savFacId " + savFactorId + ", FactorId " + fTemp.getFactorId());
                    }

                    if (fTemp.getDeprecated() == null) {
                        StringBuffer msg = validateRow(fTemp);
                        if (msg.length() == 0) {
                            // Determine if row is new or existing. If existing
                            // make sure all the key fields match and either
                            // update the row or flag as an exception

                            try {
                                FireRow existingRow = readOnlyEmissionsReportDAO.retrieveFireRow(fTemp.getFactorId());

                                if (existingRow != null) {
                                    StringBuffer errorMsg = new StringBuffer();

                                    if (!compareStrings(existingRow.getSccId(), fTemp.getSccId())) {
                                        errorMsg.append("SCC's don't match ");
                                    }

                                    if (!compareStrings(existingRow.getMaterial(), fTemp.getMaterial())) {
                                        errorMsg.append("Material's don't match ");
                                    }

                                    if (!compareStrings(existingRow.getAction(), fTemp.getAction())) {
                                        errorMsg.append("Action's don't match ");
                                    }

                                    if (!compareStrings(existingRow.getMeasure(), fTemp.getMeasure())) {
                                        errorMsg.append("Material Unit Code's don't match ");
                                    }

                                    if (!compareStrings(existingRow.getFactor(), fTemp.getFactor())) {
                                        errorMsg.append("Factor's don't match ");
                                    }

                                    if (!compareStrings(existingRow.getUnit(), fTemp.getUnit())) {
                                        errorMsg.append("Factor Unit Cd's don't match ");
                                    }

                                    if (!compareStrings(existingRow.getFormula(), fTemp.getFormula())) {
                                        errorMsg.append("Formula's don't match ");
                                    }
                                    
                                    if (!compareStrings(existingRow.getPollutantCd(), fTemp.getPollutantCd())) {
                                        errorMsg.append("Pollutant Cd's don't match ");
                                    }

                                    if (errorMsg.length() == 0) {
                                        fTemp.setDeprecated(null);

                                        if (!analyze) {
                                        	readOnlyEmissionsReportDAO.modifyFireRow(fTemp);
                                        }
                                        modifyCnt++;
                                    } else {
                                        writer.write(fTemp.getFactorId() + " " + errorMsg.toString() + "\n");
                                        nonMatchingCnt++;
                                    }
                                } else {
                                    if (!analyze) {
                                    	readOnlyEmissionsReportDAO.createFireRow(fTemp);
                                    }
                                    newCnt++;
                                }
                            } catch (DAOException de) {
                                throw new RuntimeException(de);
                            }
                        } else {
                            if (msg.length() > 0) {
                                validationCnt++;
                                writer.write("Validation error with Factor ID " + fTemp.getFactorId() + " Has following Errors: " + msg + "\n");
                            }
                        }
                    } else {
                        writer.write("Row too short for FactorID " + values[0][0] + "\n");
                    }
                }

                rowCt++;
                record = br.readLine();
            }
            writer.close();
            System.out.println("Modified records " + modifyCnt);
            System.out.println("New records " + newCnt);
            System.out.println("Non-Matching records " + nonMatchingCnt);
            System.out.println("Validation Problems " + validationCnt);
            System.out.println("Total rows processed " + (rowCt - 1));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        if (!analyze) {
            try {
            	readOnlyEmissionsReportDAO.removeInvalidFireRows(Calendar.getInstance().get(Calendar.YEAR));
            } catch (DAOException de) {
                throw new RuntimeException(de);
            }
        }

        return;
    }

    // Convert string to Integer. If invalid, return -1.
    // If not set, return null
    private Integer convert(String s) {
        int v;
        if (null == s || s.trim().length() == 0) {
            return null;
        }
        try {
            v = Integer.valueOf(s);
        } catch (NumberFormatException e) {
            v = -1;
        }
        return new Integer(v);
    }

    private boolean compareStrings(String stringOne, String stringTwo) {
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

    private StringBuffer validateRow(FireRow row) {
        StringBuffer failMess = new StringBuffer();
        // Check SCC
        try {
//            InfrastructureDAO infraDAO = (InfrastructureDAO) DAOFactory.getDAO("InfrastructureDAO");
            SccCode sccCode = readOnlyInfrastructureDAO.retrieveSccCode(row.getSccId());
            if (sccCode == null) {
                failMess.append("\tSCC value \"" + row.getSccId() + "\" not found. ");
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
            failMess.append("\tNo code for material \"" + row.getMaterial() + "\" found. ");
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
        String meas_desc = UnitDef.getData().getItems().getItemDesc(row.getMeasure());
        if (null == meas_desc) {
            failMess.append("\tMeasure \"" + row.getMeasure() + "\" not found. ");
        }

        // Check pollutant
        if (row.getPollutantCd().length() == 0) {
            if (row.getFactor().length() != 0 || row.getFormula().length() != 0 || row.getUnit().length() != 0) {
                failMess.append("\tPollutant code not specified, but factor, formula or units included ");
            }
        } else {
            String pollutant_desc = PollutantDef.getData().getItems().getItemDesc(row.getPollutantCd());
            if (null == pollutant_desc) {
                failMess.append("\tPollutant code \"" + row.getPollutantCd() + "\" not found. ");
            }

            // Check emissions units
            row.setUnit(row.getUnit().toUpperCase());
            String unit_desc = UnitDef.getData().getItems().getItemDesc(row.getUnit());
            if (null == unit_desc) {
                failMess.append("\tUnit code \"" + row.getUnit() + "\" not found. ");
            }
        }

        // TODO Check factor field

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

        // TODO Check that each material has only one action and material_units

        return failMess;
    }
}
