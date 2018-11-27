package us.oh.state.epa.stars2.migration.tandc;

//needed for emissions reporting
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.TreeMap;
import java.util.regex.Pattern;

import us.oh.state.epa.stars2.framework.util.StopWatch;

class StatePermitInfo {
    String int_doc_id;
    String facility_id;
    String sect_id;
    TreeMap<String, StateEuLevel> euList;

    StatePermitInfo() {
        int_doc_id = null;
        euList = new TreeMap<String, StateEuLevel>();
    }
}

class StateEuLevel {
    String euId;
    classificationInputData classificationData;
    ArrayList<permit_1aInputData> state_1a;
    ArrayList<permit_dtlInputData> aPart;
    ArrayList<permit_dtlInputData> bPart;
    ArrayList<permit_dtlInputData> cPart;
    ArrayList<permit_dtlInputData> dPart;
    ArrayList<permit_dtlInputData> ePart;
    ArrayList<permit_dtlInputData> fPart;

    StateEuLevel() {
        init();
    }

    private void init() {
        euId = null;
        state_1a = new ArrayList<permit_1aInputData>();
        aPart = new ArrayList<permit_dtlInputData>();
        bPart = new ArrayList<permit_dtlInputData>();
        cPart = new ArrayList<permit_dtlInputData>();
        dPart = new ArrayList<permit_dtlInputData>();
        ePart = new ArrayList<permit_dtlInputData>();
        fPart = new ArrayList<permit_dtlInputData>();
    }

    StateEuLevel(String euId) {
        init();
        this.euId = euId;
    }
}

class TvPermitInfo {
    String int_doc_id;
    String facility_id;
    String sect_id;
    TreeMap<String, TvEuLevel> euList;

    TvPermitInfo() {
        int_doc_id = null;
        euList = new TreeMap<String, TvEuLevel>();
    }
}

class TvEuLevel {
    String euId;
    classificationInputData classificationData;
    ArrayList<permit_dtlInputData> faPart;
    ArrayList<permit_dtlInputData> fbPart;
    ArrayList<permit_1aInputData> stateAndFederally_1a;
    ArrayList<permit_dtlInputData> dtl_AX;
    ArrayList<permit_dtlInputData> dtl_A2;
    ArrayList<permit_dtlInputData> dtl_A3;
    ArrayList<permit_dtlInputData> dtl_A4;
    ArrayList<permit_dtlInputData> dtl_A5;
    ArrayList<permit_dtlInputData> dtl_A6;
    ArrayList<permit_1aInputData> stateOnly_1a;
    ArrayList<permit_dtlInputData> dtl_BX;
    ArrayList<permit_dtlInputData> dtl_B2;
    ArrayList<permit_dtlInputData> dtl_B3;
    ArrayList<permit_dtlInputData> dtl_B4;
    ArrayList<permit_dtlInputData> dtl_B5;
    ArrayList<permit_dtlInputData> dtl_B6;

    TvEuLevel() {
        init();
    }

    private void init() {
        euId = null;
        faPart = new ArrayList<permit_dtlInputData>();
        fbPart = new ArrayList<permit_dtlInputData>();
        stateAndFederally_1a = new ArrayList<permit_1aInputData>();
        dtl_AX = new ArrayList<permit_dtlInputData>();
        dtl_A2 = new ArrayList<permit_dtlInputData>();
        dtl_A3 = new ArrayList<permit_dtlInputData>();
        dtl_A4 = new ArrayList<permit_dtlInputData>();
        dtl_A5 = new ArrayList<permit_dtlInputData>();
        dtl_A6 = new ArrayList<permit_dtlInputData>();
        stateOnly_1a = new ArrayList<permit_1aInputData>();
        dtl_BX = new ArrayList<permit_dtlInputData>();
        dtl_B2 = new ArrayList<permit_dtlInputData>();
        dtl_B3 = new ArrayList<permit_dtlInputData>();
        dtl_B4 = new ArrayList<permit_dtlInputData>();
        dtl_B5 = new ArrayList<permit_dtlInputData>();
        dtl_B6 = new ArrayList<permit_dtlInputData>();
    }

    TvEuLevel(String euId) {
        init();
        this.euId = euId;
    }
}

public class GeneratePermitFiles {
    public static final Pattern delimiterPattern = Pattern.compile("[|]");

    protected static File currDir;

    protected static FileWriter outStream; // Shows processing order

    private static String migrationFile = "generatePermitStream.txt";

    public static void main(String[] args) throws Exception {

        StopWatch wholeThingTimer = new StopWatch();
        wholeThingTimer.start();

        currDir = new File(System.getProperty("user.dir") + "/tandc/");
        outStream = new FileWriter(new File(currDir, migrationFile));

        classificationInput classInput = new classificationInput(currDir,
                outStream);
        permit_1aInput p_1aInput = new permit_1aInput(currDir, outStream);
        permit_dtlInput p_dtlInput = new permit_dtlInput(currDir, outStream);

        int classCnt = 0;
        classificationInputData classData = null;
        // record
        /*
         * This is the main processing loop
         */
        for (classData = classInput.get(); classData != null
                && classData.eof == false; classData = classInput.get(), classCnt++) {
            /*
             * Is this permit a Title V or a State permit?
             */
            if (classData.action_class_code.equalsIgnoreCase("TVP")) {
                // Title V permit

                TvPermitInfo permitInfo = new TvPermitInfo();
                TvEuLevel euL = null;
                permitInfo.int_doc_id = classData.int_doc_id;
                permitInfo.facility_id = classData.facilityId;
                permitInfo.sect_id = classData.sect_id;
                addEuToList(permitInfo, classData);
                /*
                 * Add an EU entry for the hard coded value 'FAC0' since this is
                 * how the facility level Terms and Conditions are stored in
                 * permit_dtl.
                 */
                permitInfo.euList.put("FAC0", new TvEuLevel("FAC0"));

                /*
                 * Fetch other classification records with the same int_doc_id
                 */
                for (classificationInputData cData = classInput
                        .get(classData.int_doc_id); cData != null
                        && cData.eof == false; cData = classInput
                        .get(classData.int_doc_id)) {
                    addEuToList(permitInfo, cData);
                }

                /*
                 * Read the permit_1a records for this int_doc_id
                 */
                for (permit_1aInputData permit_1aData = p_1aInput
                        .get(classData.int_doc_id); permit_1aData != null
                        && permit_1aData.eof == false; permit_1aData = p_1aInput
                        .get(classData.int_doc_id)) {
                    if ((euL = permitInfo.euList.get(permit_1aData.eu_oepa_id)) == null) {
                        outStream
                                .write("Warning - mismatch 1a/classification, euid="
                                        + permit_1aData.eu_oepa_id
                                        + System.getProperty("line.separator"));
                    } else {

                        /*
                         * May seem counterintuitive that S maps to
                         * stateAndFederal while F maps to stateOnly but that's
                         * the way it is....
                         */
                        if (permit_1aData.section.equalsIgnoreCase("S")) {
                            euL.stateAndFederally_1a.add(permit_1aData);
                        } else if (permit_1aData.section.equalsIgnoreCase("F")) {
                            euL.stateOnly_1a.add(permit_1aData);
                        }
                    }
                }

                /*
                 * Read the permit_dtl records for this int_doc_id
                 */
                for (permit_dtlInputData permit_dtlData = p_dtlInput
                        .get(classData.int_doc_id); permit_dtlData != null
                        && permit_dtlData.eof == false; permit_dtlData = p_dtlInput
                        .get(classData.int_doc_id)) {
                    /*
                     * System.out.println("dtl.section=" +
                     * permit_dtlData.section + "dtl.eu_oepa_id=" +
                     * permit_dtlData.eu_oepa_id + "dtl.row_num=" +
                     * permit_dtlData.row_num + "dtl.sub_part=" +
                     * permit_dtlData.sub_part);
                     */
                    if ((euL = permitInfo.euList.get(permit_dtlData.eu_oepa_id)) == null) {
                        outStream
                                .write("Warning - mismatch dtl/classification, euid="
                                        + permit_dtlData.eu_oepa_id
                                        + System.getProperty("line.separator"));
                    } else {

                        if (permit_dtlData.section.equalsIgnoreCase("FA")) {
                            euL.faPart.add(permit_dtlData);
                        } else if (permit_dtlData.section
                                .equalsIgnoreCase("FB")) {
                            euL.fbPart.add(permit_dtlData);
                        } else if (permit_dtlData.section
                                .equalsIgnoreCase("AX")) {
                            euL.dtl_AX.add(permit_dtlData);
                        } else if (permit_dtlData.section
                                .equalsIgnoreCase("A2")) {
                            euL.dtl_A2.add(permit_dtlData);
                        } else if (permit_dtlData.section
                                .equalsIgnoreCase("A3")) {
                            euL.dtl_A3.add(permit_dtlData);
                        } else if (permit_dtlData.section
                                .equalsIgnoreCase("A4")) {
                            euL.dtl_A4.add(permit_dtlData);
                        } else if (permit_dtlData.section
                                .equalsIgnoreCase("A5")) {
                            euL.dtl_A5.add(permit_dtlData);
                        } else if (permit_dtlData.section
                                .equalsIgnoreCase("A6")) {
                            euL.dtl_A6.add(permit_dtlData);
                        } else if (permit_dtlData.section
                                .equalsIgnoreCase("BX")) {
                            euL.dtl_BX.add(permit_dtlData);
                        } else if (permit_dtlData.section
                                .equalsIgnoreCase("B2")) {
                            euL.dtl_B2.add(permit_dtlData);
                        } else if (permit_dtlData.section
                                .equalsIgnoreCase("B3")) {
                            euL.dtl_B3.add(permit_dtlData);
                        } else if (permit_dtlData.section
                                .equalsIgnoreCase("B4")) {
                            euL.dtl_B4.add(permit_dtlData);
                        } else if (permit_dtlData.section
                                .equalsIgnoreCase("B5")) {
                            euL.dtl_B5.add(permit_dtlData);
                        } else if (permit_dtlData.section
                                .equalsIgnoreCase("B6")) {
                            euL.dtl_B6.add(permit_dtlData);
                        } else {
                            outStream.write("Warning, unexpected section = "
                                    + permit_dtlData.section + " int_doc_id = "
                                    + classData.int_doc_id);
                        }
                    }
                }

                try {
                    procTvPermitRecord(permitInfo);
                } catch (IOException ioe) {
                    outStream.write("Error, int_doc_id = "
                            + classData.int_doc_id + "IOException: ");
                    ioe.printStackTrace();
                }

            } else if (classData.action_class_code.equalsIgnoreCase("STP")) {
                // State permit

                StatePermitInfo permitInfo = new StatePermitInfo();
                StateEuLevel euL = null;
                permitInfo.int_doc_id = classData.int_doc_id;
                permitInfo.facility_id = classData.facilityId;
                permitInfo.sect_id = classData.sect_id;
                addStateEuToList(permitInfo, classData);

                /*
                 * Fetch other classification records with the same int_doc_id
                 */
                for (classificationInputData cData = classInput
                        .get(classData.int_doc_id); cData != null
                        && cData.eof == false; cData = classInput
                        .get(classData.int_doc_id)) {
                    addStateEuToList(permitInfo, cData);
                }

                /*
                 * Read the permit_1a records for this int_doc_id
                 */
                for (permit_1aInputData permit_1aData = p_1aInput
                        .get(classData.int_doc_id); permit_1aData != null
                        && permit_1aData.eof == false; permit_1aData = p_1aInput
                        .get(classData.int_doc_id)) {
                    if ((euL = permitInfo.euList.get(permit_1aData.eu_oepa_id)) == null) {
                        outStream
                                .write("Warning - mismatch 1a/classification, euid="
                                        + permit_1aData.eu_oepa_id
                                        + System.getProperty("line.separator"));
                    } else {
                        euL.state_1a.add(permit_1aData);
                    }
                }

                /*
                 * Read the permit_dtl records for this int_doc_id
                 */
                for (permit_dtlInputData permit_dtlData = p_dtlInput
                        .get(classData.int_doc_id); permit_dtlData != null
                        && permit_dtlData.eof == false; permit_dtlData = p_dtlInput
                        .get(classData.int_doc_id)) {
                    if ((euL = permitInfo.euList.get(permit_dtlData.eu_oepa_id)) == null) {
                        outStream
                                .write("Warning - mismatch dtl/classification, euid="
                                        + permit_dtlData.eu_oepa_id
                                        + System.getProperty("line.separator"));
                    } else {

                        if (permit_dtlData.section.equalsIgnoreCase("A2")) {
                            euL.aPart.add(permit_dtlData);
                        } else if (permit_dtlData.section.equalsIgnoreCase("B")) {
                            euL.bPart.add(permit_dtlData);
                        } else if (permit_dtlData.section.equalsIgnoreCase("C")) {
                            euL.cPart.add(permit_dtlData);
                        } else if (permit_dtlData.section.equalsIgnoreCase("D")) {
                            euL.dPart.add(permit_dtlData);
                        } else if (permit_dtlData.section.equalsIgnoreCase("E")) {
                            euL.ePart.add(permit_dtlData);
                        } else if (permit_dtlData.section.equalsIgnoreCase("F")) {
                            euL.fPart.add(permit_dtlData);
                        } else {
                            outStream
                                    .write("Warning, state: unexpected section = "
                                            + permit_dtlData.section
                                            + " int_doc_id = "
                                            + classData.int_doc_id);
                        }
                    }
                }

                try {
                    procStatePermitRecord(permitInfo);
                } catch (IOException ioe) {
                    outStream.write("Error, could not process State permit, int_doc_id = "
                            + classData.int_doc_id + "IOException: ");
                    ioe.printStackTrace();
                }

            } else {
                outStream.write("Error, invalid action_class_code = "
                        + classData.action_class_code
                        + System.getProperty("line.separator"));
            }
        }
        outStream.write("End of processing, processed " + classCnt
                + " records." + System.getProperty("line.separator"));
        
        /*
         * Generate 'Not found' file to handle case of missing permit doc.
         */
        FileWriter outFile = getResultFile("NoPermitFound");
        outFile
                .write("<html> <head> <title>No Permit Document Found</title> </head> <body> <h1>No Permit Document Found</h1> </body> </html>");
        outFile.close();

        wholeThingTimer.stop();
        String statusMsg = System.getProperty("line.separator") + "Complete "
                + wholeThingTimer.toString()
                + System.getProperty("line.separator");
        outStream.write(statusMsg);

        outStream.close();

    }

    private static void procTvPermitRecord(TvPermitInfo permitInfo)
            throws IOException {

        BufferedWriter outFile = new BufferedWriter(getResultFile(permitInfo.int_doc_id));
        TvPermitFormatter formatter = new TvPermitFormatter();
        formatter.writeData(permitInfo, outFile);
        outFile.flush();
        outFile.close();
    }

    private static void procStatePermitRecord(StatePermitInfo permitInfo)
            throws IOException {

        BufferedWriter outFile = new BufferedWriter(getResultFile(permitInfo.int_doc_id));
        StatePermitFormatter formatter = new StatePermitFormatter();
        formatter.writeData(permitInfo, outFile);
        outFile.flush();
        outFile.close();
    }

    private static FileWriter getResultFile(String int_doc_id)
            throws IOException {
        return (new FileWriter(new File(currDir, int_doc_id + ".html")));
    }

    private static void addEuToList(TvPermitInfo permitInfo,
            classificationInputData cData) throws IOException {
        String cl = cData.classification;
        if (cl.equalsIgnoreCase("FA") || cl.equalsIgnoreCase("PA")) {
            permitInfo.euList.put(cData.eu_oepa_id, new TvEuLevel(
                    cData.eu_oepa_id));
        } else {
            outStream.write("did not add euId=" + cData.eu_oepa_id
                    + " classification=" + cl);
        }
    }

    private static void addStateEuToList(StatePermitInfo permitInfo,
            classificationInputData cData) throws IOException {
        String cl = cData.classification;
        if (cl.equalsIgnoreCase("FA") || cl.equalsIgnoreCase("PA")) {
            permitInfo.euList.put(cData.eu_oepa_id, new StateEuLevel(
                    cData.eu_oepa_id));
        } else {
            outStream.write("State: did not add euId=" + cData.eu_oepa_id
                    + " classification=" + cl);
        }
    }
}
