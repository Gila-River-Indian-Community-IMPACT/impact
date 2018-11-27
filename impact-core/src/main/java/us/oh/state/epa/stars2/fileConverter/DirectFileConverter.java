package us.oh.state.epa.stars2.fileConverter;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

import org.apache.log4j.Logger;

import us.oh.state.epa.stars2.bo.DocumentService;
import us.oh.state.epa.stars2.bo.PermitService;
import us.oh.state.epa.stars2.database.dbObjects.document.Document;
import us.oh.state.epa.stars2.framework.config.ConfigManager;
import us.oh.state.epa.stars2.framework.config.ConfigManagerFactory;
import us.oh.state.epa.stars2.framework.config.Node;
import us.oh.state.epa.stars2.util.DocumentUtil;
import us.oh.state.epa.stars2.webcommon.ServiceFactory;
import us.oh.state.epa.stars2.webcommon.ServiceFactoryException;

/**
 * Class used to convert word perfect and word documents to pdf using adlib.
 * @author Pmontoto
 *
 */
public class DirectFileConverter {
    private File adlibInputDir = null;
    private File adlibOutputDir = null;
    private Logger logger;
    private HashMap<String, File> fileMap;
    private int skippedFiles = 0;
    private HashMap<String, Integer> extensionMap;
    

    public DirectFileConverter() {
        logger = Logger.getLogger(this.getClass());
    }
    
    /**
     * Initialize the FileConverter class. This must be done before calling run.
     * @return true if initialization is successful, false otherwise.
     */
    public boolean init() {
        boolean ok = true;
        ConfigManager cfgMgr = ConfigManagerFactory.configManager();
        Node root = cfgMgr.getNode("app.AdlibFileInputDir");
        adlibInputDir = new File(root.getAsString("value"));
        
        if (!adlibInputDir.isDirectory() || !adlibInputDir.canWrite()) {
            logger.error("Unable to write to Adlib input directory: " + 
                    adlibInputDir.getAbsolutePath());
            ok = false;
        }
        
        root = cfgMgr.getNode("app.AdlibFileOutputDir");
        adlibOutputDir = new File(root.getAsString("value"));
        
        if (!adlibOutputDir.isDirectory() || !adlibOutputDir.canWrite()) {
            logger.error("Unable to write to Adlib input directory: " + 
                    adlibOutputDir.getAbsolutePath());
            ok = false;
        }
        
        fileMap = new HashMap<String, File>();
        extensionMap = new HashMap<String, Integer>();
        
        return ok;
    }
    
    public void convertPermitFiles() {
        logger.debug("Converting permit files...");
        PermitService permitBO = null;
        try {
            permitBO = ServiceFactory.getInstance().getPermitService();
            Document docs[] = permitBO.retrievePermitIssuanceDocuments();
            convertDocuments(docs);
            
            for (String extension : extensionMap.keySet()) {
                logger.debug(extensionMap.get(extension) + " " + extension + " files total");
            }
        } catch (ServiceFactoryException sfe) {
            logger.error(sfe.getMessage(), sfe);
        } catch (RemoteException e) {
            logger.error("Error retrieving permit documents", e);
        }
        logger.debug("Done converting permit files.");
    }
    
    public void convertWPDFiles() {
        logger.debug("Converting word perfect files...");
        DocumentService documentBO = null;
        try {
            documentBO = ServiceFactory.getInstance().getDocumentService();
            
            // need to break documents up since there are too many for one query
            Document[] wpdDocuments = documentBO.getDocumentsByPath("%1.wpd");
            convertDocuments(wpdDocuments);
            updateDocExtensions(wpdDocuments, "wpd");
            wpdDocuments = documentBO.getDocumentsByPath("%2.wpd");
            convertDocuments(wpdDocuments);
            updateDocExtensions(wpdDocuments, "wpd");
            wpdDocuments = documentBO.getDocumentsByPath("%3.wpd");
            convertDocuments(wpdDocuments);
            updateDocExtensions(wpdDocuments, "wpd");
            wpdDocuments = documentBO.getDocumentsByPath("%4.wpd");
            convertDocuments(wpdDocuments);
            updateDocExtensions(wpdDocuments, "wpd");
            wpdDocuments = documentBO.getDocumentsByPath("%5.wpd");
            convertDocuments(wpdDocuments);
            updateDocExtensions(wpdDocuments, "wpd");
            wpdDocuments = documentBO.getDocumentsByPath("%6.wpd");
            convertDocuments(wpdDocuments);
            updateDocExtensions(wpdDocuments, "wpd");
            wpdDocuments = documentBO.getDocumentsByPath("%7.wpd");
            convertDocuments(wpdDocuments);
            updateDocExtensions(wpdDocuments, "wpd");
            wpdDocuments = documentBO.getDocumentsByPath("%8.wpd");
            convertDocuments(wpdDocuments);
            updateDocExtensions(wpdDocuments, "wpd");
            wpdDocuments = documentBO.getDocumentsByPath("%9.wpd");
            convertDocuments(wpdDocuments);
            updateDocExtensions(wpdDocuments, "wpd");
            wpdDocuments = documentBO.getDocumentsByPath("%0.wpd");
            convertDocuments(wpdDocuments);
            updateDocExtensions(wpdDocuments, "wpd");
            
            // frm documents are essentially wpd documents
            wpdDocuments = documentBO.getDocumentsByPath("%.frm");
            convertDocuments(wpdDocuments);
            updateDocExtensions(wpdDocuments, "frm");
            
        } catch (ServiceFactoryException sfe) {
            logger.error(sfe.getMessage(), sfe);
        } catch (RemoteException e) {
            logger.error("Error retrieving permit documents", e);
        }
        logger.debug("Done converting word perfect files.");
    }
    
    private void convertDocuments(Document[] docs) {
        int filesCopied = 0;
        for (Document doc : docs) {
            try {
                if (copyToConvertInput(doc)) {
                    ++filesCopied;
                    if (filesCopied % 30 == 0) {
                        // copy files from previous run
                        copyFilesFromConvertOutput();
                        if (fileMap.size() > 0) {
                            // sleep a bit to give adlib a chance to process some files.
                            try {
                                int sleepMins = fileMap.size()/3;
                                logger.debug("Sleeping for " + sleepMins + " minutes...");
                                Thread.sleep(sleepMins * 60 * 1000);
                            } catch (InterruptedException e) {
                                logger.error(e.getMessage(), e);
                            }
                            logger.debug("Awake.");
                        }
                    }
                }
            } catch (IOException e) {
                logger.error("Failed converting file " + doc.getPath(), e);
            }
        }

    }
    
    private void updateDocExtensions(Document[] docs, String extension) {
        DocumentService documentBO;
        try {
            documentBO = ServiceFactory.getInstance().getDocumentService();
            for (Document doc : docs) {
                File wpdFile = new File(DocumentUtil.getFileStoreRootPath(), doc.getPath());
                File pdfFile = new File(wpdFile.getParent(), wpdFile.getName().toLowerCase().replace("." + extension, ".pdf"));
                if (doc.getExtension() != "pdf" && pdfFile.exists()) {
                    // make sure file extension is lower case
                    doc.setBasePath(doc.getBasePath().substring(0, doc.getBasePath().lastIndexOf('.')) + ".pdf");
                    try {
                        documentBO.updateDocument(doc);
                    } catch (RemoteException e) {
                        logger.error("Exception while attempting to update extension for " 
                                + extension + "  document: " + doc.getDocumentID(), e);
                    }
                }
            }
        } catch (ServiceFactoryException e1) {
            logger.error("Error accessing documentBO", e1);
        }
    }
    
    public boolean copyToConvertInput(Document doc) throws IOException {
        boolean fileConverted = false;
        File stars2File = new File(DocumentUtil.getFileStoreRootPath(), doc.getPath());

        if (stars2File.exists()) {
            fileConverted = copyToConvertInput(stars2File.getName(), stars2File.getParent());
        }
        return fileConverted;
    }
    
    private boolean copyToConvertInput(String fileName, String stars2FilePath) throws IOException {
        String fileExtension = DocumentUtil.getFileExtension(fileName);
        String fileNameMinusExtension = fileName.substring(0, fileName.length() - fileExtension.length());
        String outputFileName = fileNameMinusExtension + "pdf";
        
        // skip wpd files for now
        if (fileName.toLowerCase().endsWith("wpd")) {
            return false;
        }
        
        Integer count = extensionMap.get(fileExtension);
        if (count == null) {
            extensionMap.put(fileExtension, new Integer(1));
        } else {
            int newCount = count + 1;
            if (newCount % 500 == 0) {
                logger.debug(newCount + " " + fileExtension + " files so far");
            }
            extensionMap.put(fileExtension, newCount);
        }
        
        if (fileExtension.equalsIgnoreCase("pdf")) {
            return false;
        }
        
//        if (!fileExtension.equalsIgnoreCase("html") && !fileExtension.equalsIgnoreCase("htm")) {
//            if (++skippedFiles % 100 == 0) {
//                logger.debug(skippedFiles + " files skipped so far");
//            }
//            return false;
//        }
        File sourceFile = new File(stars2FilePath, fileName);
        File targetFile = new File(stars2FilePath, outputFileName);
        if (targetFile.exists()) {
            logger.debug("Target file: " + targetFile.getAbsolutePath() + " already exists.");
            return false;
        }
 
        fileMap.put(fileName, targetFile);
        logger.debug("Added fileMap entry: " + fileName + " => " + targetFile.getAbsolutePath());

        File convertFile = new File(adlibInputDir, fileName);
        File convertedFile = new File(adlibOutputDir, fileName);
        if (!convertFile.exists() && !convertedFile.exists()) {
//            copyFile(sourceFile, convertFile);
            logger.debug("Copied file: " + sourceFile.getAbsolutePath() + " to Adlib file conversion dir");
        } else {
            return false;
        }
        
        
        return true;
    }
    
    private void copyFilesFromConvertOutput() {
        ArrayList<String> processedFiles = new ArrayList<String>();
        for (String fileName : fileMap.keySet()) {
            String convertedPdfFileName = fileName + ".pdf";
            File convertedFile = new File(adlibOutputDir, convertedPdfFileName);
            if (convertedFile.canWrite()) {
                File destFile = fileMap.get(fileName);
                if (destFile != null && !destFile.exists()) {
                    convertedFile.renameTo(destFile);
                    logger.debug("Completed conversion for file " +destFile.getAbsolutePath());
                    processedFiles.add(fileName);
                }
            } else {
                logger.error("Unable to write file: " + convertedFile.getAbsolutePath() + 
                        ". Cannot copy to prod at this time.");
            }
        }
        
        for (String fileName : processedFiles) {
            fileMap.remove(fileName);
        }
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
        DirectFileConverter converter = new DirectFileConverter();
        if (converter.init()) {
            converter.convertPermitFiles();
//            converter.convertWPDFiles();
        } else {
            System.err.println("Failed initializing FileConverter");
        }
    }
}
