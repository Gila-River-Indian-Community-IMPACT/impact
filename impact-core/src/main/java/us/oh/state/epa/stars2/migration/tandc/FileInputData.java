package us.oh.state.epa.stars2.migration.tandc;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.NoSuchElementException;
import java.util.Scanner;

/*
 * Base class for all facility id based file input classes. Assumes each logical
 * record has a row number and facility id.
 */
abstract class FileInputData {
    String row;
    String facilityId;
    String action_class_code;
    String action_id;
    String sect_id;
    String int_doc_id;
    boolean eof;

    FileInputData() {
        // null constructor
    }

}

abstract class IntDocIdBasedCurrentFileInput extends CurrentFileInput {
    protected FileInputData current;

    protected FileInputData readAheadOne;

    IntDocIdBasedCurrentFileInput(String fileName, File currDir,
            FileWriter ostream) throws IOException {
        super(fileName, currDir, ostream);
    }

    protected FileInputData getCurrent() {
        return (current);
    }

    protected void setCurrent(FileInputData m) {
        current = m;
    }

    protected FileInputData getReadAheadOne() {
        return (readAheadOne);
    }

    protected void setReadAheadOne(FileInputData m) {
        readAheadOne = m;
    }

    /*
     * Used in case we want next record and don't need logic to match on a
     * specified facility id.
     */
    public FileInputData conditionlessGetNextRecord() {
        return (advance());
    }

    /*
     * Primary get method. Gets next record for the specified facility id.
     * Assumes underlying file has been sorted in ascending facility id order.
     */
    public FileInputData get(String int_doc_id) {

        if (getCurrent() == null || getCurrent().int_doc_id == null) {
            return null;
        }
        
        // TODO get rid of this
        if (getCurrent().int_doc_id.startsWith("INT")) {
            advance();
        }
        
        Long requestedIntDocId = Long.parseLong(int_doc_id);
        Long currentIntDocId = Long.parseLong(getCurrent().int_doc_id);
        if (currentIntDocId.compareTo(requestedIntDocId) > 0) {
            return null;
        }

        FileInputData ret;
        Long retIntDocId;

        do {
            ret = advance();
            retIntDocId = Long.parseLong(ret.int_doc_id);
            if (!getCurrent().eof) {
                currentIntDocId = Long.parseLong(getCurrent().int_doc_id);
            }
        } while (ret.eof == false
                && (retIntDocId.compareTo(requestedIntDocId) < 0)
                && getCurrent().eof == false
                && (currentIntDocId.compareTo(requestedIntDocId) <= 0));

        /*
         * Did we find a matching record?
         */
        if (retIntDocId.compareTo(requestedIntDocId) == 0) {
            return (ret);
        }

        return null;
    }

    protected FileInputData advance() {
        FileInputData ret = getCurrent();
        setCurrent(getReadAheadOne());
        setReadAheadOne(getInstance());
        readLine(); // read next record
        return ret;
    }

    /*
     * Populates the values of 'readAheadOne'.
     */
    protected void readLine() {

        /*
         * If we are already at EOF, don't bother trying to read more data
         */
        if (getCurrent() != null && getCurrent().eof == true) {
            getReadAheadOne().eof = true;
            return;
        }

        try {
            readKeyFields();
            //
            // Let derived class figure out what other fields
            // will come after the key fields.
            //
            getRestOfRecord(s, getReadAheadOne());

            // if we get this far, must not have hit eof
            getReadAheadOne().eof = false;
        } catch (NoSuchElementException nse) {
            // Need entire line to consider the line a valid record.
            // Partial lines are considered same as eof.
            getReadAheadOne().eof = true;
        } catch (IllegalStateException ise) {
            String emsg = "Illegal state exception in readLine()"
                    + System.getProperty("line.separator");
            System.err.println(emsg);
            System.exit(1);
        }

        //
        // If we're not at eof, check to see if the row number value is valid
        //
        if (getReadAheadOne().eof == false) {
            int rowNum;
            try {
                // If the row number is preceded by a newline, the
                // call to trim() will get rid of it.
                String rNum = getReadAheadOne().row.trim();
                /*
                 * If the string begins with 'row', remove it
                 */
                if (rNum.substring(0, 3).equalsIgnoreCase("row")) {
                    rNum = rNum.substring(3);
                }
                rowNum = Integer.parseInt(rNum);
            } catch (NumberFormatException nfe) {
                rowNum = -1;
            }
            if (rowNum < 0 || rowNum > 2000000) {
                String emsg = "Potential format problem in " + getFname()
                        + " file, row does not begin with a valid row number: "
                        + getReadAheadOne().toString()
                        + System.getProperty("line.separator");
                System.err.println(emsg);
            }
        }
    }

    /*
     * This class assumes that the corresponding input file has row number as
     * the first field and facility id as the second field.
     */
    protected void readKeyFields() {
        getReadAheadOne().row = s.next();
        getReadAheadOne().facilityId = s.next();
        getReadAheadOne().action_class_code = s.next();
        getReadAheadOne().action_id = s.next();
        getReadAheadOne().sect_id = s.next();
        getReadAheadOne().int_doc_id = s.next();
    }

    abstract String getFname();

    abstract void getRestOfRecord(Scanner s, FileInputData meid);

    abstract FileInputData getInstance();

}

class CurrentFileInput {
    protected final String fileName;
    protected final File file;
    protected Scanner s;

    // Initialize to read the flat file.
    CurrentFileInput(String fileName, File currDir, FileWriter ostream)
            throws IOException {
        this.fileName = fileName;
        file = new File(currDir, fileName);
        try {
            s = new Scanner(file)
                    .useDelimiter(GeneratePermitFiles.delimiterPattern);
        } catch (FileNotFoundException e) {
            ostream.write("Could not find file " + currDir.toString() + "/"
                    + fileName + System.getProperty("line.separator"));
            ostream.close();
            System.err.println("Failure to find file " + currDir.toString()
                    + "/" + fileName);
            System.exit(7);
        }
    }
}

class classificationInputData extends FileInputData {

    String effective_start;
    String effective_end;
    String tmsp_created;
    String eu_oepa_id;
    String classification;
    String used_in_permit;

    classificationInputData() {
        // null constructor
    }

}

class classificationInput extends IntDocIdBasedCurrentFileInput {

    private final static String fname = "permit_classification.txt";

    classificationInput(File currDir, FileWriter ostream) throws IOException {
        super(fname, currDir, ostream);

        advance(); // populates readAheadOne with first record
        advance(); // moves first record to current and populates
        // readAheadOne
        // with second record
    }

    public String getFname() {
        return (fname);
    }

    public classificationInputData getInstance() {
        return (new classificationInputData());
    }

    public classificationInputData get() {
        return ((classificationInputData) super.conditionlessGetNextRecord());
    }

    public classificationInputData get(String int_doc_id) {
        return ((classificationInputData) super.get(int_doc_id));
    }

    protected void getRestOfRecord(Scanner s, FileInputData id_id) {
        classificationInputData inData = (classificationInputData) id_id;
        inData.effective_start = s.next();
        inData.effective_end = s.next();
        inData.tmsp_created = s.next();
        inData.eu_oepa_id = s.next();
        inData.classification = s.next();
        inData.used_in_permit = s.next();
    }
}

class permit_1aInputData extends FileInputData {

    String eu_oepa_id;
    String section;
    String seq_num;
    String ops_props_equip;
    String reqt_text;
    String limit_ctrl;

    permit_1aInputData() {
        // null constructor
    }

}

class permit_1aInput extends IntDocIdBasedCurrentFileInput {

    private final static String fname = "permit_1a.txt";

    permit_1aInput(File currDir, FileWriter ostream) throws IOException {
        super(fname, currDir, ostream);

        advance(); // populates readAheadOne with first record
        advance(); // moves first record to current and populates
        // readAheadOne
        // with second record
    }

    public String getFname() {
        return (fname);
    }

    public permit_1aInputData getInstance() {
        return (new permit_1aInputData());
    }

    public permit_1aInputData get(String int_doc_id) {
        return ((permit_1aInputData) super.get(int_doc_id));
    }

    protected void getRestOfRecord(Scanner s, FileInputData id_id) {
        permit_1aInputData inData = (permit_1aInputData) id_id;
        inData.eu_oepa_id = s.next();
        inData.section = s.next();
        inData.seq_num = s.next();
        inData.ops_props_equip = s.next();
        inData.reqt_text = s.next();
        inData.limit_ctrl = s.next();
    }
}

class permit_dtlInputData extends FileInputData {

    String eu_oepa_id;
    String section;
    String seq_num;
    String row_num;
    String sub_part;
    String reqt_text;
    String permit_type;

    permit_dtlInputData() {
        // null constructor
    }

    public String toString() {
        String s = "row=" + row + " facility_id=" + facilityId + " int_doc_id="
                + int_doc_id;
        return (s);
    }
}

class permit_dtlInput extends IntDocIdBasedCurrentFileInput {

    private final static String fname = "permit_dtl.txt";

    permit_dtlInput(File currDir, FileWriter ostream) throws IOException {
        super(fname, currDir, ostream);

        advance(); // populates readAheadOne with first record
        advance(); // moves first record to current and populates
        // readAheadOne
        // with second record
    }

    public String getFname() {
        return (fname);
    }

    public permit_dtlInputData getInstance() {
        return (new permit_dtlInputData());
    }

    public permit_dtlInputData get(String int_doc_id) {
        return ((permit_dtlInputData) super.get(int_doc_id));
    }

    protected void getRestOfRecord(Scanner s, FileInputData id_id) {
        permit_dtlInputData inData = (permit_dtlInputData) id_id;
        inData.eu_oepa_id = s.next();
        inData.section = s.next();
        inData.seq_num = s.next();
        inData.row_num = s.next();
        inData.sub_part = s.next();
        inData.reqt_text = s.next();
        inData.permit_type = s.next();
    }
}
