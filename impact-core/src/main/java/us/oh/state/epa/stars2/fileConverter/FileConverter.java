package us.oh.state.epa.stars2.fileConverter;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.rmi.RemoteException;

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
public class FileConverter {
    private File adlibInputDir = null;
    private Logger logger;
    private String adlibFileStoreRoot = null;
    private int filesConverted = 0;
    private int permitFilesConverted = 0;
    private int otherWPDFilesConverted = 0;
    private int fileExtensionsModified = 0;
    private String permitsIssuedDir = "P:\\permits_issued";
    
    private static final String ADLIB_CONVERSION_TEMPLATE =
"<?xml version=\"1.0\" encoding=\"ISO-8859-1\" ?>\n" +
"<?AdlibExpress applanguage=\"USA\" appversion=\"4.1.0\" dtdversion=\"2.5.2\" ?>\n" +
"<!DOCTYPE JOBS SYSTEM \"ADLIB_EXPRESS_DTD_FILE\"> \n" +
"<!-- The First 3 Lines are the header of an XML file, all XML files must have one\n" +
"     there should be no text before it -->\n" +
"<JOBS xmlns:JOBS=\"http://www.adlibsoftware.com\" xmlns:JOB=\"http://www.adlibsoftware.com\">\n" +
"  <JOB>\n" +
"  <!-- DOCINPUTS, this is where you specify the file you wish to convert and its location -->\n" +
"    <JOB:DOCINPUTS>\n" +
"      <JOB:DOCINPUT FILENAME=\"STARS2_INPUT_FILE_NAME\" FOLDER=\"STARS2_INPUT_FILE_DIR\" />\n" +
"    </JOB:DOCINPUTS>\n" +
"\n" +
"    <!-- DOCOUTPUTS, this is where you specify the file name and location of the converted file -->\n" +
"    <JOB:DOCOUTPUTS>\n" +
"      <JOB:DOCOUTPUT FILENAME=\"STARS2_OUTPUT_FILE_NAME\" FOLDER=\"STARS2_OUTPUT_FILE_DIR\" DOCTYPE=\"PDF\"/>\n" +
"      <JOB:DOCOUTPUT FILENAME=\"STARS2_OUTPUT_FILE_NAME\" FOLDER=\"PERMITS_ISSUED_DIR\" DOCTYPE=\"PDF\"/>\n" +
"    </JOB:DOCOUTPUTS>\n" +
" \n" +
"  </JOB>\n" +
"</JOBS>\n";
    
    private static final String ADLIB_PDF_COPY_TEMPLATE =
"<?xml version=\"1.0\" encoding=\"ISO-8859-1\" ?>\n" +
"<?AdlibExpress applanguage=\"USA\" appversion=\"4.1.0\" dtdversion=\"2.5.2\" ?>\n" +
"<!DOCTYPE JOBS SYSTEM \"ADLIB_EXPRESS_DTD_FILE\"> \n" +
"<!-- The First 3 Lines are the header of an XML file, all XML files must have one\n" +
"     there should be no text before it -->\n" +
"<JOBS xmlns:JOBS=\"http://www.adlibsoftware.com\" xmlns:JOB=\"http://www.adlibsoftware.com\">\n" +
"  <JOB>\n" +
"  <!-- DOCINPUTS, this is where you specify the file you wish to convert and its location -->\n" +
"    <JOB:DOCINPUTS>\n" +
"      <JOB:DOCINPUT FILENAME=\"STARS2_INPUT_FILE_NAME\" FOLDER=\"STARS2_INPUT_FILE_DIR\" />\n" +
"    </JOB:DOCINPUTS>\n" +
"\n" +
"    <!-- DOCOUTPUTS, this is where you specify the file name and location of the converted file -->\n" +
"    <JOB:DOCOUTPUTS>\n" +
"      <JOB:DOCOUTPUT FILENAME=\"STARS2_OUTPUT_FILE_NAME\" FOLDER=\"PERMITS_ISSUED_DIR\" DOCTYPE=\"PDF\"/>\n" +
"    </JOB:DOCOUTPUTS>\n" +
" \n" +
"  </JOB>\n" +
"</JOBS>\n";
    public FileConverter() {
        logger = Logger.getLogger(this.getClass());
    }
    
    /**
     * Initialize the FileConverter class. This must be done before calling run.
     * @return true if initialization is successful, false otherwise.
     */
    public boolean init() {
        boolean ok = true;
        ConfigManager cfgMgr = ConfigManagerFactory.configManager();
        Node root = cfgMgr.getNode("app.AdlibDTDFileName");
        
        root = cfgMgr.getNode("app.AdlibFileStoreRoot");
        adlibFileStoreRoot = root.getAsString("value");
        logger.debug("adlibFileStoreRoot = " + adlibFileStoreRoot);
        
        root = cfgMgr.getNode("app.AdlibInputDir");
        adlibInputDir = new File(root.getAsString("value"));
        
        root = cfgMgr.getNode("app.PermitsIssuedDir");
        if (root != null) {
            permitsIssuedDir = root.getAsString("value");
        }
        
        if (!adlibInputDir.isDirectory() || !adlibInputDir.canWrite()) {
            logger.error("Unable to write to Adlib input directory: " + 
                    adlibInputDir.getAbsolutePath());
            ok = false;
        }
        
        return ok;
    }
    
    public void convertPermitFiles() {
        logger.debug("Converting permit files...");
        PermitService permitBO = null;
        try {
            permitBO = ServiceFactory.getInstance().getPermitService();
            Document docs[] = permitBO.retrievePermitIssuanceDocuments();
            logger.debug(docs.length + " permit files to be converted.");
            convertDocuments(docs);
            permitFilesConverted = filesConverted;
            filesConverted = 0;
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
            
            otherWPDFilesConverted = filesConverted;
            filesConverted = 0;
        } catch (ServiceFactoryException sfe) {
            logger.error(sfe.getMessage(), sfe);
        } catch (RemoteException e) {
            logger.error("Error retrieving permit documents", e);
        }
        logger.debug("Done converting word perfect files.");
    }
    
    private void convertDocuments(Document[] docs) {
        for (Document doc : docs) {
            try {
                if (convertFile(doc, false)) {
                    ++filesConverted;
                    if (filesConverted % 50 == 0) {
                        logger.debug(filesConverted + " files converted so far");
                        // sleep a bit to give adlib a chance to process some files.
//                        try {
//                            logger.debug(new Timestamp(System.currentTimeMillis()).toString() + " sleeping for 10 minutes");
//                            Thread.sleep(10 * 60 * 1000);
//                        } catch (InterruptedException e) {
//                            logger.error(e.getMessage(), e);
//                        }
                    }
                }
            } catch (IOException e) {
                logger.error("Failed converting file " + doc.getPath(), e);
            }
        }

        logger.debug(filesConverted + " files converted total");

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
                        ++fileExtensionsModified;
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
    
    public boolean convertFile(Document doc, boolean copyPDF) throws IOException {
        boolean fileConverted = false;
        File stars2File = new File(DocumentUtil.getFileStoreRootPath(), doc.getPath());

        if (stars2File.exists()) {
            fileConverted = convertFile(stars2File.getName(), stars2File.getParent(), copyPDF);
        }
        return fileConverted;
    }
    
    private boolean convertFile(String fileName, String stars2FilePath, boolean copyPDF) throws IOException {
        String fileExtension = DocumentUtil.getFileExtension(fileName);
        String fileNameMinusExtension = fileName.substring(0, fileName.length() - fileExtension.length());
        String outputFileName = fileNameMinusExtension + "pdf";
        String adlibFilePath = stars2FilePath.replace(DocumentUtil.getFileStoreRootPath(), adlibFileStoreRoot);
        
        File targetFile = new File(stars2FilePath, outputFileName);
        String adLibTemplate = ADLIB_CONVERSION_TEMPLATE;
        if (targetFile.exists()) {
        	if (!copyPDF) {
        		// if pdf file already exists and we aren't in "copyPDF" mode, don't do anything
	            logger.debug("Target file: " + targetFile.getAbsolutePath() + " already exists.");
	            return false;
        	} else {
        		// if we are in "copyPDF" mode, copy the pdf file to the permits issued dir
        		File destFile = new File(permitsIssuedDir, outputFileName);
        		adLibTemplate = ADLIB_PDF_COPY_TEMPLATE;
        		if (destFile.exists()) {
        			// no need to copy file again if it's already there.
        			return false;
        		} else {
        			// Mantis 2960
        			// only need to copy the PDF file since it has already been converted.
        			fileName = outputFileName;
        		}
        	}
        }
        
        // convert file to PDF
        String ticketXML = adLibTemplate.replace("ADLIB_EXPRESS_DTD_FILE", 
                "C:\\Adlib Express\\Job Ticket Samples\\AdlibExpress.dtd");
        ticketXML = ticketXML.replace("STARS2_INPUT_FILE_NAME", fileName);
        ticketXML = ticketXML.replace("STARS2_INPUT_FILE_DIR", adlibFilePath);
        ticketXML = ticketXML.replace("STARS2_OUTPUT_FILE_NAME", outputFileName);
        ticketXML = ticketXML.replace("STARS2_OUTPUT_FILE_DIR", adlibFilePath);
        ticketXML = ticketXML.replace("PERMITS_ISSUED_DIR", permitsIssuedDir);
        
        logger.debug("Creating ticket with content: \n" + ticketXML);
        
        File adlibJobTicket = new File(adlibInputDir, fileNameMinusExtension + "xml");
        BufferedWriter bw = new BufferedWriter(new FileWriter(adlibJobTicket));
        bw.write(ticketXML);
        bw.flush();
        bw.close();
        
        return true;
    }
    
    public static final void main(String[] args) {
        FileConverter converter = new FileConverter();
        if (converter.init()) {
            converter.convertPermitFiles();
//            converter.convertWPDFiles();
        } else {
            System.err.println("Failed initializing FileConverter");
        }
    }

    public final int getPermitFilesConverted() {
        return permitFilesConverted;
    }

    public final int getOtherWPDFilesConverted() {
        return otherWPDFilesConverted;
    }

    public final int getFileExtensionsModified() {
        return fileExtensionsModified;
    }
}
