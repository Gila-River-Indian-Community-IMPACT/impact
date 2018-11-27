package us.oh.state.epa.stars2.webcommon;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;

import org.apache.log4j.Logger;

import oracle.adf.view.faces.model.UploadedFile;

/**
 * This class stores the information from an UploadedFile object so it will
 * not be lost after the form it is in is submitted. This is particularly useful
 * in cases where validation of the file upload window needs to occur on the
 * server because UploadedFile information is lost when the form is submitted
 * for validation and the UploadedFile's input field will be blank when the
 * window is repainted when validation fails.
 *
 */
public class UploadedFileInfo {
    private transient Logger logger = Logger.getLogger(this.getClass());
    private String fileName;
    private File tempFile;
    private static final int BUFFER_SIZE = 8192;
    
    public UploadedFileInfo (UploadedFile uploadedFile) {
        if (uploadedFile != null) {
            fileName = uploadedFile.getFilename();
            String prefix = "DOC";
            String suffix = "txt";
            String[] fileNameParts = fileName.split("\\.");
            if (fileNameParts.length >= 2) {
                prefix = fileNameParts[0];
                for (int i=0; i<fileNameParts.length; i++) {
                    suffix = "." + fileNameParts[i];
                }
            } else {
                prefix = fileName;
            }
            try {
                // make sure prefix is at least 3 characters long
                if (prefix.length() < 3) {
                    prefix = prefix + "DOC";
                }
                tempFile = File.createTempFile(prefix, suffix);
                InputStream is = uploadedFile.getInputStream();
                FileOutputStream fos = new FileOutputStream(tempFile);

                byte[] buffer = new byte[BUFFER_SIZE];
                int len;
                while ((len = is.read(buffer)) > 0) {
                    fos.write(buffer, 0, len);
                }
                fos.close();
            } catch (IOException e) {
                logger.error("Failed creating temp file for uploaded file: " + fileName, e);
            }
            
        }
        
    }
    
    public UploadedFileInfo(String fileName, File tempFile) {
    	this.fileName = fileName;
    	this.tempFile = tempFile;
    }

    public final String getFileName() {
        return fileName;
    }

    public final InputStream getInputStream() throws FileNotFoundException {
        InputStream is = null;
        if (tempFile != null && tempFile.canRead()) {
            is = new FileInputStream(tempFile);
        }
        return is;
    }
    
    public final FileReader getFileReader() throws FileNotFoundException {
        FileReader fr = null;
        if (tempFile != null && tempFile.canRead()) {
            fr = new FileReader(tempFile);
        }
        return fr;
    }
    
    public final long getLength() {
        long len = tempFile.length();
        return len;
    }
    
    /**
     * Clear out the contents of this object. The reference to this object should
     * always be set to null after calling this method.
     */
    public final void cleanup() {
        if (tempFile != null) {
            tempFile.delete();
        }
        fileName = null;
    }
}
