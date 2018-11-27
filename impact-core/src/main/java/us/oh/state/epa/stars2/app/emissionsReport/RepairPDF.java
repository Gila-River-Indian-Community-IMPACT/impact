package us.oh.state.epa.stars2.app.emissionsReport;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.rmi.RemoteException;
import java.util.HashMap;
import java.util.Map.Entry;

import us.oh.state.epa.stars2.bo.DocumentService;
import us.oh.state.epa.stars2.database.dbObjects.document.Document;
import us.oh.state.epa.stars2.webcommon.AppBase;
import us.oh.state.epa.stars2.webcommon.ServiceFactory;
import us.oh.state.epa.stars2.webcommon.ServiceFactoryException;

public class RepairPDF extends AppBase {

    /*
     * PDF files end in "%%EOF", however the number of these strings varies between
     * different PDF files and it is not determined by the PDF version of the file.
     * So far we have seen PDF-1.2, PDF-1.3, PDF-1.4 and PDF-1.5.  However there are
     * also PDF-1.0, PDF-1.6 and PDF-1.7 versions as well.
     * 
     * The problem is that the original files were copied  by reading and writing a buffer.
     * The last buffer was written in its entirely not just how much was read into that
     * last buffer.
     * 
     * The solution is to look for "%%EOF" and delete what follows.
     * 
     * Save these created files and examine each prior to making 
     * a final run which actually makes them the offical files.
     */

    private static File rootDir;
    private static String rootDirString;
    private static int maxFiles = 300000000;
    private static int eofCountDist[] = {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};  //index 0 - 16
    private static int eofCountDistMod[] = {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};  //index 0 - 16
    private static final int distSize = 600;
    private static int distOfEofMarkers[] = new int[distSize];
    private static int range = 512;
    private static PrintStream outStream;
    private static int cnt2;
    private static boolean junkAtEnd = false;
    private static int numFilesToFix = 300000000;
    
    // collect all pdf versions and file example
    private static HashMap<String, String> pdfVersions = new HashMap<String, String>();

    public static void main(String[] args) {
        RepairPDF rPDF = new RepairPDF();
        try {
            Thread.sleep(10000);  // sleep 10 seconds
            FileOutputStream fos = new FileOutputStream("repairPdfLog.txt");
            outStream = new PrintStream(fos);
            initDist();
            rPDF.run();
            prtDist();
            outStream.close();
            System.exit(0);
        } catch (RuntimeException re) {
            StackTraceElement[] ste = re.getStackTrace();
            for(StackTraceElement e : ste) {
                System.err.println(e.toString());
            }
        } catch(InterruptedException ie) {
            System.err.println("RemoteException:  " + ie.getMessage());
        } catch(IOException ioe) {
            System.err.println("IOException:  " + ioe.getMessage());
        } 
    }
    
    static void initDist() {
        for(int ndx = 0; ndx < distSize; ndx ++) {
            distOfEofMarkers[ndx] = 0;
        }
    }
    
    static void prtDist() {
        outStream.println("Distribution of first EOF marker when at least two EOF markers");
        outStream.println("Bytes in category is " + range);
        for(int ndx = 0; ndx < distSize; ndx ++) {
            outStream.println(ndx + " frequency=" + distOfEofMarkers[ndx]);
        }
    }
    
    static void addToDist(int cnt) {
        int ndx = cnt % range;
        if(ndx >= distSize) {
            ndx = distSize - 1;
        }
        distOfEofMarkers[ndx]++;
    }

    void run() {
        rootDirString = "S:\\prod\\attachments";
        rootDir = new File(rootDirString);
        outStream.println("Root Directory is " + rootDirString + " (" + rootDir.getAbsolutePath() + ")");
        Document[] docs;
        try {
            DocumentService dBO = ServiceFactory.getInstance().getDocumentService();
            docs = dBO.getMigratedPdfDocuments();
            int i = 0;
            for(Document d : docs) {
                File f = new File(rootDir, d.getPath());
                processFile(f);
                i++;
                if(i >= maxFiles) {
                    System.err.println("Reached File Limit");
                    outStream.println("Reached File Limit");
                    break;
                }
            }
            outStream.println("Processed " + i + " files");
            for(int inx = 0; inx < eofCountDist.length; inx++) {
                outStream.println(eofCountDist[inx] + " files had " + inx + " eof markers");
            }
            outStream.println("Number changed is " + i + " files");
            for(int inx = 0; inx < eofCountDistMod.length; inx++) {
                outStream.println(eofCountDistMod[inx] + " files had " + inx + " eof markers");
            }
            
        } catch (ServiceFactoryException sfe) {
            System.err.println("ServiceFactoryException:  " + sfe.getMessage());
            outStream.println("ServiceFactoryException:  " + sfe.getMessage());
        } catch (RemoteException re) {
            System.err.println("RemoteException:  " + re.getMessage());
            outStream.println("RemoteException:  " + re.getMessage());
        } 
        for(Entry e : pdfVersions.entrySet()) {
            outStream.println("PDF Version:" + e.getKey() +
                    ", Example file: " + e.getValue());
        }
    }

    private static void processFile(File file) {
        byte[] header = new byte[16];
        FileInputStream in = null;
        try {
            for(int i = 0; i < 16; i++) {
                header[i] = 0;
            }
            in = new FileInputStream(file);
            int len = in.read(header, 0, 16);

            if(len != 16) {

            }
            // look for first white space.
            int j; // j is first white space or 16
            int v;
            for(j = 0; j < 16; j++) {
                v = header[j];
                v = v & 255;
                if(v <= '\u0020') {
                    break;
                }
            }

            boolean isPdf = false;
            // look for PDF in header.
            int k;
            for(k=0; k < j; k++) {
                int vv = header[k];
                vv = vv & 255;
                if(vv == 'P' || vv == 'p') {
                    isPdf = true;
                    break;
                }
            }
            if(isPdf) {
                isPdf = false;
                k++;
                int vv = header[k];
                vv = vv & 255;
                if(vv == 'D' || vv == 'd') {
                    isPdf = true;
                }
            }
            if(isPdf) {
                isPdf = false;
                k++;
                int vv = header[k];
                vv = vv & 255;
                if(vv == 'F' || vv == 'f') {
                    isPdf = true;
                }
            }
            if(isPdf) {
                // keep track of version and example
                String version = new String(header, 0, j);
                pdfVersions.put(version, file.getAbsolutePath());
                in.close();
                copyFile(version, file);
            } else {
                in.close();
                System.err.println("ERROR, not a PDF file: " + file.getAbsolutePath());
                outStream.println("ERROR, not a PDF file: " + file.getAbsolutePath());
            }
            outStream.flush();
        } catch (FileNotFoundException e) {
            System.err.println("FileNotFoundException:  " + e.getMessage());
            outStream.println("FileNotFoundException:  " + e.getMessage());
        } catch (IOException e) {
            System.err.println("IOException:  " + e.getMessage());
            outStream.println("IOException:  " + e.getMessage());
        } catch (RuntimeException e) {
            System.err.println("RuntimeException:  " + e.getMessage());
            outStream.println("RuntimeException:  " + e.getMessage());
        }
    }

    protected static void copyFile(String version, File file) {
        try {
            int cnt = countFile(version, file);
            if(cnt != cnt2) {
                outStream.print(" ERROR: cnts are not the same:");
            }
            outStream.println("Count of %%EOF is " + cnt + "(" + cnt2 + "), " + version + " " + file.getAbsolutePath());

            if(cnt != 0 && junkAtEnd && numFilesToFix > 0) {
                copyFile(version, cnt, file);
                numFilesToFix--;
            }
            if(cnt >= eofCountDist.length) {
                cnt = eofCountDist.length - 1;
            }
            eofCountDist[cnt]++;
            if(junkAtEnd) {
                eofCountDistMod[cnt]++;
            }
        } catch(FileNotFoundException e) {
            System.err.println("FileNotFoundException:  " + e.getMessage());
            outStream.println("FileNotFoundException:  " + e.getMessage());
        }catch(IOException e) {
            System.err.println("IOException:  " + e.getMessage());
            outStream.println("IOException:  " + e.getMessage());
        }
    }
    
    protected static int countFile(String version, File file)
    throws IOException {
        final int endPhraseSize = 7;
        int lookingForIndex = 0;
        int lookingForIndex2 = 0;
        int firstEOF = -1;
        int lastEOF = -1;
        int cnt = 0;
        cnt2 = 0;
        int fileByteCount = 0;
        FileInputStream bIn = new FileInputStream(file);
        byte[] buffer = new byte[10000];
        int length = 10000;
        int amountRead = 0;
        boolean atEOF = false;
        outStream.print("EOFs at ");
        while (!atEOF) {
            amountRead = bIn.read(buffer, 0, length);
            if(amountRead == -1) {
                atEOF = true;
                outStream.print("file size " + fileByteCount + "; lookingForIndex=" + lookingForIndex);
                if(lookingForIndex == 6) {
                    outStream.print(", ERROR, matched at end of file");
                    cnt++;
                    lastEOF = fileByteCount;
                    if(cnt == 1) {
                        firstEOF = fileByteCount;
                    }
                    if(cnt == 2) {
                        addToDist(firstEOF);
                    }
                }
                junkAtEnd = false;
                if((fileByteCount != lastEOF) && (lastEOF != -1)) {
                    outStream.print(", junk at end.");
                    junkAtEnd = true;
                }
                outStream.println();
            } else {              
                for(int i=0; i < amountRead; i++) {
                    fileByteCount++;
                    if(!matches(lookingForIndex, buffer[i])) {
                        if(lookingForIndex != 0) {
                            // start over by checking same byte
                            lookingForIndex = 0;
                            if(!matches(lookingForIndex, buffer[i])) {
                                lookingForIndex = 0; // still mismatch--start over
                                ; //continue
                            } else {
                                lookingForIndex++;
                            }
                        } else {
                            ; // continue
                        }
                    } else {
                        lookingForIndex++;
                        if(lookingForIndex >= endPhraseSize ||
                                (lookingForIndex >= endPhraseSize - 1) &&
                                "%PDF-1.2".equals(version)) {
                            cnt++; // found an endphrase
                            lastEOF = fileByteCount;
                            if(cnt == 1) {
                                firstEOF = fileByteCount;
                            }
                            if(cnt == 2) {
                                addToDist(firstEOF);
                            }
                            lookingForIndex = 0;
                            outStream.print(fileByteCount + ", ");
                        }
                    }
                    
                    if(!matches2(lookingForIndex2, buffer[i])) {
                        lookingForIndex2 = 0;
                    } else {
                        lookingForIndex2++;
                        
                        if(lookingForIndex2 >= 2) {
                            outStream.print(""); // for breakpoint
                        }
                        
                        
                        if(lookingForIndex2 >= 5) {
                            cnt2++;
                            if(lookingForIndex != 6 &&
                                    !("%PDF-1.2".equals(version) &&
                                            lookingForIndex == 0)) {
                                // we are not finding _%%EOF_
                                if(i < 5) {
                                    outStream.println("\nERROR DID NOT FIND, i=" + i);
                                } else {
                                    outStream.println("\nERROR DID NOT FIND(lookingForIndex=" + lookingForIndex + "): " + 
                                            (char)buffer[i-5] + "(" +  (byte)buffer[i-5] +  ")" +
                                            (char)buffer[i-4] + "(" +  (byte)buffer[i-4] +  ")" +
                                            (char)buffer[i-3] + "(" +  (byte)buffer[i-3] +  ")" +
                                            (char)buffer[i-2] + "(" +  (byte)buffer[i-2] +  ")" +
                                            (char)buffer[i-1] + "(" +  (byte)buffer[i-1] +  ")" +
                                            (char)buffer[i]   + "(" +  (byte)buffer[i]   +  ")" +
                                            (char)buffer[i+1] + "(" +  (byte)buffer[i+1] +  ")" );
                                }
                            }
                            lookingForIndex2 = 0;
                        }
                    }
                    
                    
                }
            }
        }
        bIn.close();
        return cnt;
    }
    
    protected static boolean matches(int lookingForIndex, byte b) {
        final char[] endPhrase = {'%', '%', 'E', 'O', 'F'};
        boolean rtn = false;
        if(lookingForIndex > 6) {
            System.err.println("ERROR:  Outbounds, lookingForIndex = " + lookingForIndex);
            outStream.println("ERROR:  Outbounds, lookingForIndex = " + lookingForIndex);
            return false;
        }

        if(lookingForIndex == 0 || lookingForIndex == 6) {
            if(b <= ' ') {
                rtn = true;
            } else {  // checking for data format
                if(lookingForIndex == 6 && !rtn) {
                    outStream.print(" {byte at position 6 is " + (byte)b + "} ");
                }
            }
        } else {
            if(b == endPhrase[lookingForIndex - 1]) {
                rtn = true;
            }
        }
        return rtn;
    }
    
    protected static boolean matches2(int lookingForIndex2, byte b) {
        final char[] endPhrase = {'%', '%', 'E', 'O', 'F'};
        boolean rtn = false;
        if(b == (byte)endPhrase[lookingForIndex2]) {
            rtn = true;
        }
        return rtn;
    }

    // Remove extra junk after last %%EOF
    protected static void copyFile(String version, int cnt, File file)
    throws IOException {
        final int endPhraseSize = 7;
        int lookingForIndex = 0;
        int countdown = cnt;
        FileInputStream bIn = new FileInputStream(file);
        byte[] buffer = new byte[10000];
        int length = 10000;
        int amountRead = 0;
        boolean atEOF = false;
        String fileName = file.getAbsolutePath() + "-REPAIRED-PDF";
        String movedFileName = "J:\\SHARED\\UNICON\\SavedOrigMigratedPDF" + file.getAbsolutePath().substring(19);
        int lstX = movedFileName.lastIndexOf('\\');
        if(lstX == -1) {
            lstX = movedFileName.lastIndexOf('/');
        }
        String movedFileDirectory = movedFileName.substring(0, lstX);
        outStream.println("Moved Location: " + movedFileName + ", Moved DIR: " + movedFileDirectory);
        File newFile = new File(fileName);
        File movedFile = new File(movedFileName);
        File movedFileDir = new File(movedFileDirectory);
        FileOutputStream out = new FileOutputStream(newFile);
        outStream.println("REPAIR FILE: " + file.getAbsolutePath());
        while (!atEOF) {
            amountRead = bIn.read(buffer, 0, length);
            if(amountRead == -1) {
                // unexpected EOF
                System.err.println("ERROR:  Unexpected EOF cnt=" + cnt + ", countdown=" + countdown);
                outStream.println("ERROR:  Unexpected EOF cnt=" + cnt + ", countdown=" + countdown);
                atEOF = true;
            } else {
                // look for match or
                // continue looking for match (split over buffers)
                for(int i=0; i < amountRead; i++) {
                    if(!matches(lookingForIndex, buffer[i])) {
                        if(lookingForIndex != 0) {
                            // start over by checking same byte
                            lookingForIndex = 0;
                            if(!matches(lookingForIndex, buffer[i])) {
                                lookingForIndex = 0; // still mismatch--start over
                                ; //continue
                            } else {
                                lookingForIndex++;
                            }
                        } else {
                            ; // continue
                        }
                    } else {
                        lookingForIndex++;
                        if(lookingForIndex >= endPhraseSize ||
                                (lookingForIndex >= endPhraseSize - 1) &&
                                "%PDF-1.2".equals(version)) {
                            countdown--; // found an endphrase
                            lookingForIndex = 0;
                            if(countdown == 0) { // reached as far as is valid
                                atEOF = true;
                                amountRead = i+1;
                                break;
                            }
                        }
                    }
                }
                out.write(buffer, 0, amountRead);
            }
        }
        bIn.close();
        out.close();
        movedFileDir.mkdirs();
        boolean success = file.renameTo(movedFile);
        if(success) {
            success = newFile.renameTo(file);
            if(!success) {
                System.err.println("ERROR: Failed to rename file: " + newFile.getAbsolutePath());
                outStream.println("ERROR: Failed to rename file: " + newFile.getAbsolutePath());
            }
        } else {
            System.err.println("ERROR: Failed to delete/move file: " + file.getAbsolutePath());
            outStream.println("ERROR: Failed to delete/move file: " + file.getAbsolutePath());
        }
        
    }
}