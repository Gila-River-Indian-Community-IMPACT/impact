package us.oh.state.epa.stars2.fileIndexer;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.text.SimpleDateFormat;

import org.apache.log4j.Logger;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.poi.hssf.extractor.ExcelExtractor;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hwpf.HWPFDocument;
import org.apache.poi.hwpf.extractor.WordExtractor;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.xssf.extractor.XSSFExcelExtractor;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.poi.xwpf.extractor.XWPFWordExtractor;
import org.apache.poi.xwpf.usermodel.XWPFDocument;

/** A utility for making Lucene Documents from a File. */

public class FileDocument {
    static Logger logger = Logger.getLogger(FileDocument.class);
    public static final SimpleDateFormat DateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    
    public static Document Document(File file, us.oh.state.epa.stars2.database.dbObjects.document.Document inDoc) throws java.io.FileNotFoundException {
        Document doc = new Document();
        
        // Set the meta-data for this file.
        doc.add(new Field("DocumentId", inDoc.getDocumentID().toString(), Field.Store.YES, Field.Index.NOT_ANALYZED));
        doc.add(new Field("FacilityId", inDoc.getFacilityID(), Field.Store.YES, Field.Index.NOT_ANALYZED));
        doc.add(new Field("Description", inDoc.getDescription(), Field.Store.YES, Field.Index.NOT_ANALYZED));
        doc.add(new Field("Path", inDoc.getBasePath(), Field.Store.YES, Field.Index.NOT_ANALYZED));
        if (inDoc.getLastModifiedBy() != null) {
            doc.add(new Field("LastModifiedBy", inDoc.getLastModifiedBy().toString(), Field.Store.YES, Field.Index.NOT_ANALYZED));
        }
        if (inDoc.getLastModifiedTS() != null) { 
            doc.add(new Field("LastModifiedTS", DateFormat.format(inDoc.getLastModifiedTS()), Field.Store.YES, Field.Index.NOT_ANALYZED));
        }

        // now add the "contents" of the document.
        ParseContents(file, doc);
        
        return doc;
    }

//    public static Document Document(File f) throws java.io.FileNotFoundException {
//        Document doc = new Document();
//
//        // Add meta-data for this file.
//        doc.add(new Field("Path", f.getPath(), Field.Store.YES, Field.Index.NOT_ANALYZED));
//
//        ParseContents(f, doc);
//        
//        return doc;
//    }

    private static boolean ParseContents(File f, Document doc) {
        boolean ret = false;
        
        try {
            if (f.getName().endsWith(".doc")) {
                FileInputStream docfin = new FileInputStream(f.getAbsolutePath());
                WordExtractor docextractor = new WordExtractor(new HWPFDocument(docfin));
                String content = docextractor.getText();
                doc.add(new Field("contents", content, Field.Store.NO, Field.Index.ANALYZED));
                ret = true;
            } else if (f.getName().endsWith(".docx")) {
                FileInputStream docfin = new FileInputStream(f.getAbsolutePath());
                OPCPackage pkg = OPCPackage.open(docfin);
                XWPFDocument tempDoc = new XWPFDocument(pkg);
                XWPFWordExtractor docextractor = new XWPFWordExtractor(tempDoc);
                String content = docextractor.getText();
                doc.add(new Field("contents", content, Field.Store.NO, Field.Index.ANALYZED));
                ret = true;
            } else if (f.getName().endsWith(".xls")) {
                FileInputStream docfin = new FileInputStream(f.getAbsolutePath());
                ExcelExtractor docextractor = new ExcelExtractor(new HSSFWorkbook(docfin));
                String content = docextractor.getText();
                doc.add(new Field("contents", content, Field.Store.NO, Field.Index.ANALYZED));
                ret = true;
            } else if (f.getName().endsWith(".xlsx")) {
                XSSFExcelExtractor docextractor = new XSSFExcelExtractor(new XSSFWorkbook(f.getAbsolutePath()));
                String content = docextractor.getText();
                doc.add(new Field("contents", content, Field.Store.NO, Field.Index.ANALYZED));
                ret = true;
            } else if (f.getName().endsWith(".txt")) {
                doc.add(new Field("contents", new FileReader(f)));
                ret = true;
            } else if (f.getName().endsWith(".html")) {
                /**
                FileInputStream fis = new FileInputStream(f);
                HTMLParser parser = new HTMLParser(fis);
                  
                // Add the tag-stripped contents as a Reader-valued Text field so it will
                // get tokenized and indexed.
                doc.add(new Field("contents", parser.getReader()));

                // Add the summary as a field that is stored and returned with
                // hit documents for display.
                doc.add(new Field("summary", parser.getSummary(), Field.Store.YES, Field.Index.NO));

                // Add the title as a field that it can be searched and that is stored.
                doc.add(new Field("title", parser.getTitle(), Field.Store.YES, Field.Index.ANALYZED));                
                */
            } else if (f.getName().endsWith(".pdf")) {
            	
            	throw new RuntimeException("Not implemented in IMPACT");

            	// commented out because the pdfbox lib is causing issues
            	// because of the imbedded bouncycastle crypto lib

//            	FileInputStream docfin = new FileInputStream(f.getAbsolutePath());
//            	Document pdfDoc = null;
//            	try{
//            		pdfDoc = LucenePDFDocument.getDocument(docfin);
//            		doc.add(pdfDoc.getField("contents"));
//            		ret = true;
//            	} catch (NoSuchFieldError e) {
//            		logger.error(e.getMessage(), e);
//            	}
            } else if (f.getName().endsWith(".wpd")) {
                // doc.add(new Field("contents", new FileReader(f)));
                logger.debug("Wordperfect parsing not implemented yet.");
            } else {
                logger.error("Unknown document format " + f.getPath());
            }
        } catch (IOException ioe) {
            logger.error(ioe.getMessage() + " file=" + f.getName() + "[" + f.getAbsolutePath() + "]", ioe);
        } catch (Exception e) {
            logger.error(e.getMessage()  + " file=" + f.getName() + "[" + f.getAbsolutePath() + "]", e);
        }  
        
        return ret;
    }
    
    private FileDocument() {
    }
}
