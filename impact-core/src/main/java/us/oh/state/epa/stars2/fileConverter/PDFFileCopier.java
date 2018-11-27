package us.oh.state.epa.stars2.fileConverter;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.rmi.RemoteException;

import org.apache.log4j.Logger;

import us.oh.state.epa.stars2.bo.PermitService;
import us.oh.state.epa.stars2.database.dbObjects.document.Document;
import us.oh.state.epa.stars2.util.DocumentUtil;
import us.oh.state.epa.stars2.webcommon.ServiceFactory;
import us.oh.state.epa.stars2.webcommon.ServiceFactoryException;

/**
 * Class used to convert word perfect and word documents to pdf using adlib.
 * @author Pmontoto
 *
 */
public class PDFFileCopier {
    private File destinationDir = null;
    private Logger logger;
    private int filesCopied = 0;
    private int filesAlreadyThere = 0;
    private int totalFilesCopied = 0;
    

    public PDFFileCopier() {
        logger = Logger.getLogger(this.getClass());
    }
    
    /**
     * Initialize the FileConverter class. This must be done before calling run.
     * @return true if initialization is successful, false otherwise.
     */
    public boolean init() {
        boolean ok = true;
        
        destinationDir = new File("P:\\permits_issued");
        
        if (!destinationDir.isDirectory() || !destinationDir.canWrite()) {
            logger.error("Unable to write to Adlib input directory: " + 
                    destinationDir.getAbsolutePath());
            ok = false;
        }
        
        return ok;
    }
    
    public void copyPermitFiles() {
        logger.debug("Copying permit files...");
        PermitService permitBO = null;
        try {
            permitBO = ServiceFactory.getInstance().getPermitService();
            Document docs[] = permitBO.retrievePermitIssuanceDocuments();
            logger.debug(docs.length + " documents to be processed.");
            copyDocuments(docs);
            logger.debug(filesCopied + " permit files copied");
            totalFilesCopied += filesCopied;
            logger.debug(filesAlreadyThere + " files were already present in the directory.");
            filesAlreadyThere = 0;
            logger.debug(totalFilesCopied + " total files copied");
            filesCopied = 0;
        } catch (ServiceFactoryException sfe) {
            logger.error(sfe.getMessage(), sfe);
        } catch (RemoteException e) {
            logger.error("Error retrieving permit documents", e);
        }
        logger.debug("Done copying permit files.");
    }
    
    private void copyDocuments(Document[] docs) {
        for (Document doc : docs) {
            try {
                if (copytoWebDir(doc)) {
                    if (++filesCopied % 1000 == 0) {
                        logger.debug(filesCopied + " files copied so far.");
                    }
                }
            } catch (IOException e) {
                logger.error("Failed converting file " + doc.getPath(), e);
            }
        }

    }
    
    public boolean copytoWebDir(Document doc) throws IOException {
        boolean fileConverted = false;
        File stars2File = new File(DocumentUtil.getFileStoreRootPath(), doc.getPath());

        if (stars2File.exists()) {
            fileConverted = copyToWebDir(stars2File.getName(), stars2File.getParent());
        }
        return fileConverted;
    }
    
    private boolean copyToWebDir(String fileName, String stars2FilePath) throws IOException {
        boolean ret = false;
        String fileExtension = DocumentUtil.getFileExtension(fileName);
        String fileNameMinusExtension = fileName.substring(0, fileName.length() - fileExtension.length());
        String pdfFileName = fileNameMinusExtension + "pdf";
        
        File stars2PdfFile = new File(stars2FilePath, pdfFileName);
        File webPdfFile = new File(destinationDir, pdfFileName);
        if (stars2PdfFile.exists() && !webPdfFile.exists()) {
          System.out.println("Copying file: " + stars2PdfFile.getAbsolutePath());
          copyFile(stars2PdfFile, webPdfFile);
          ret = true;
        } 
        else if (webPdfFile.exists()) {
            ++filesAlreadyThere;
        }
        
        
        return ret;
    }
    
    private void copyFile(File fromFile, File toFile) throws IOException {
        FileInputStream from = null;
        FileOutputStream to = null;
        try {
          from = new FileInputStream(fromFile);
          to = new FileOutputStream(toFile);
          byte[] buffer = new byte[4096];
          int bytesRead;

          while ((bytesRead = from.read(buffer)) != -1)
            to.write(buffer, 0, bytesRead); // write
        } finally {
          if (from != null)
            try {
              from.close();
            } catch (IOException e) {
              ;
            }
          if (to != null)
            try {
              to.close();
            } catch (IOException e) {
              ;
            }
        }
    }
    
    public static final void main(String[] args) {
        PDFFileCopier copier = new PDFFileCopier();
        if (copier.init()) {
            copier.copyPermitFiles();
        } else {
            System.err.println("Failed initializing PDFFileCopier");
        }
    }
}
