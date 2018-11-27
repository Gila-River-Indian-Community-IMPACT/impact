package us.oh.state.epa.stars2.workflow.engine;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.PrintWriter;
import java.util.StringTokenizer;

/**
 * <p>
 * Title: TestAutoRetryCommand
 * </p>
 * 
 * <p>
 * Description: This is a test workflow command that fails an automatic activity
 * a fixed number of times and then succeeds.
 * </p>
 * 
 * <p>
 * <b>How to Use this Tool</b>
 * </p>
 * 
 * <p>
 * First, this command has to be associated with an "Activity Template" in the
 * workflow designer (Currently "Auto Retry Process"). If needed, use the "New
 * Activity Template" wizard in the workflow designer to create a new, automatic
 * (not manual) activity and make this class the "Auto Class Name" for that
 * activity. Also, assign non-zero values to "Number of Retry Attempts" and
 * "Retry Interval". Create a workflow that uses this activity and associate the
 * workflow with a product in the "Product Catalog".
 * </p>
 * 
 * <p>
 * This command reads a data file to see how many times it is supposed to fail,
 * and how many times it has failed this far. The data file should be located in
 * "$OSSMHOME/data/auto_retry.data". The data file consists of a single line:
 * </p>
 * 
 * <p>
 * [Number of times to fail] [Number of times it has failed]
 * </p>
 * 
 * For example, if you want auto retry to fail three times, create the file
 * (using "vi" or "notepad", etc.) as:
 * </p>
 * 
 * <p>
 * 3 0
 * </p>
 * 
 * <p>
 * Order the service with the workflow, and submit the order. On the "Provision"
 * tab, find the order and start provisioning it. Every time the activity is
 * executed, either automatically or manually, the failure count is incremented
 * and the data file is re-written. When the current failure count exceeds the
 * planned failure count, the retry request will succeed.
 * </p>
 * 
 * <p>
 * In order for this command to find the data file, the workflow engine must be
 * run with the following property set:
 * </p>
 * 
 * <p>
 * <tt>"-DOSSMHOME=[OSSM Home Directory]"</tt>
 * </p>
 * 
 * <p>
 * For example, if you have OSSMHOME set as a Unix environment variable, you
 * could set this as:
 * </p>
 * 
 * <p>
 * <tt>"-DOSSMHOME=$OSSMHOME"</tt>
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) 2004
 * </p>
 * 
 * <p>
 * Company: MentorGen, LLC
 * </p>
 * 
 * @author J. E. Collier
 * @version 1.0
 */
public class TestAutoRetryCommand extends AbstractWorkFlowCommand {
    private int plannedFailCnt;
    private int currentFailCnt;

    /**
     * Constructor.
     */
    public TestAutoRetryCommand() {
    }

    /**
     * Framework method. Called by the workflow engine whenever the associated
     * Activity is to be automatically executed.
     * 
     * @param wfData
     *            WorkFlowCmdData
     * 
     * @throws Exception
     *             Any number of reasons.
     */
    public void execute(WorkFlowCmdData wfData) throws Exception {
        readDataFile(); // Read the data file
        currentFailCnt++; // Increment the failure count
        writeDataFile(); // Write the data file

        // If we are still supposed to fail, fail.

        if (currentFailCnt <= plannedFailCnt) {
            throw new Exception("Life sucks and then you die.");
        }
    }

    /**
     * Reads the data file containing the planned failure count and the current
     * failure count. These values are stored in data members for processing.
     * 
     * @throws Exception
     *             Most likely a FileNotFoundException.
     */
    protected void readDataFile() throws Exception {
        // Get the full path name of the data file.

        String fileName = TestAutoRetryCommand.getFileName();

        System.out.println();
        System.out.println("Opening input data file: " + fileName);
        System.out.println();

        FileReader fr = null;
        BufferedReader br = null;
        String dataLine = null;

        // Open the file and read it.

        try {
            fr = new FileReader(fileName);
            br = new BufferedReader(fr);
            dataLine = br.readLine();
            System.out.println("Data read from file = [ " + dataLine + " ].");
        } catch (FileNotFoundException fnfe) {
            fnfe.printStackTrace();
            throw fnfe;
        } finally {
            if (br != null) {
                br.close();
            }
        }

        // The data file is expected to contain two strings: planned failure
        // count and current failure count. Read the two strings, providing
        // defaults should either of these be missing. Note that if the
        // data file is missing or empty, this guarantees a default behavior
        // of "pass".

        StringTokenizer st = new StringTokenizer(dataLine, " ");
        String tk1 = new String("");
        String tk2 = new String("");

        if (st.hasMoreTokens()) {
            tk1 = st.nextToken();
        }

        if (st.hasMoreTokens()) {
            tk2 = st.nextToken();
        }

        System.out.println("Token 1 = [" + tk1 + "].");
        System.out.println("Token 2 = [" + tk2 + "].");

        // Convert the strings to integers and save the values to data members.

        plannedFailCnt = parseInteger(tk1);
        currentFailCnt = parseInteger(tk2);
    }

    /**
     * Writes the data file so it can be read again on the next invocation. The
     * data file contents will consist of a single line containing the planned
     * failure count and the current failure count.
     * 
     * @throws Exception
     *             IOException writing file.
     */
    protected void writeDataFile() throws Exception {
        // Get the full path name of the data file.

        String fileName = TestAutoRetryCommand.getFileName();
        PrintWriter pw = null;

        // Write the data file.

        try {
            FileOutputStream fos = new FileOutputStream(fileName);
            pw = new PrintWriter(fos);
            pw.print(plannedFailCnt);
            pw.print(" ");
            pw.println(currentFailCnt);
            pw.flush();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            throw e;
        } finally {
            if (pw != null) {
                pw.close();
            }
        }
    }

    /**
     * Converts String "str" to an integer value. Converts a null or empty
     * String to zero.
     * 
     * @param str
     *            String Representation of an integer value.
     * 
     * @return int The integer value.
     * 
     * @throws Exception
     *             NumberFormatException
     */
    protected int parseInteger(String str) throws Exception {
        // If the string is null or empty, convert it to zero.
        int ret = 0;

        if ((str != null) && (str.length() > 0)) {
            // Make an integer out of "str".
            Integer foo = new Integer(str);
            ret = foo.intValue();
        }
        return ret;
    }

    /**
     * Returns the full path name to the data file.
     * 
     * @return String full path name.
     */
    static private String getFileName() {
        String dirName = System.getProperty("OSSMHOME", "C:/Projects/OSSM");
        dirName = dirName + "/data";
        String fileName = dirName + "/" + "auto_retry.data";

        return fileName;
    }
}
