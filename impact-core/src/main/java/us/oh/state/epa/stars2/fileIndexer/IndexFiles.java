package us.oh.state.epa.stars2.fileIndexer;

import java.io.File;
//import java.io.FileNotFoundException;
import java.io.IOException;

import org.apache.log4j.Logger;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;

import us.oh.state.epa.stars2.bo.PermitService;
import us.oh.state.epa.stars2.framework.config.ConfigManager;
import us.oh.state.epa.stars2.framework.config.ConfigManagerFactory;
import us.oh.state.epa.stars2.framework.config.Node;
import us.oh.state.epa.stars2.framework.util.StopWatch;
import us.oh.state.epa.stars2.util.DocumentUtil;
import us.oh.state.epa.stars2.webcommon.ServiceFactory;
import us.oh.state.epa.stars2.webcommon.ServiceFactoryException;

/** Index all text files under a directory. */
public class IndexFiles {
    private Directory indexDir;
    private File backupIndexDir;
    private StopWatch timer;
    private Logger logger;
    private boolean ok = false;
    private int filesProcessed = 0;
    
    public IndexFiles() {
        ok = init(null, null);
    }

    public IndexFiles(String indexDir, String backupIndexDir) {
        ok = init(indexDir, backupIndexDir);
    }

    private boolean init(String indexDir, String backupIndexDir) {
    	boolean ok = false;
        logger = Logger.getLogger(this.getClass());
        timer = new StopWatch();
        
        try {
			ok = initIndexDir(indexDir, backupIndexDir);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return ok;
    }

    public void run() {
        timer.start();
        try {
            IndexWriter writer = new IndexWriter(indexDir, new StandardAnalyzer(Version.LUCENE_30), 
            		true, IndexWriter.MaxFieldLength.LIMITED);
            logger.error("Indexing to directory '" + indexDir + "'");
            indexDocs(writer);
            logger.error("Optimizing...");
            writer.optimize();
            writer.close();

            timer.stop();
            logger.error("Elasped time : " + timer.toString());
        } catch (IOException ioe) {
            logger.error(ioe.getMessage(), ioe);
        }
    }

    private void indexDocs(IndexWriter writer) throws IOException {
        PermitService permitBO = null;
        
        try {
            permitBO = ServiceFactory.getInstance().getPermitService();
        } catch (ServiceFactoryException sfe) {
            logger.error(sfe.getMessage(), sfe);
        }
        
        us.oh.state.epa.stars2.database.dbObjects.document.Document docs[] = permitBO.retrievePermitIssuanceDocuments();
        
        logger.error("Found " + docs.length + " documents to process");
        filesProcessed = 0;
        int missingFiles = 0;
        
        for (us.oh.state.epa.stars2.database.dbObjects.document.Document doc : docs) {
            String fileName = doc.getPath();
            fileName = DocumentUtil.getFileStoreRootPath() + fileName;
            
            // All word, pdf (or frm) and html documents should have a pdf version
            if (fileName.toLowerCase().endsWith(".doc") || fileName.toLowerCase().endsWith(".docx") ||
                    fileName.toLowerCase().endsWith(".wpd") || fileName.toLowerCase().endsWith(".frm") ||
                    fileName.toLowerCase().endsWith(".html") || fileName.toLowerCase().endsWith(".htm")) {
                fileName = fileName.substring(0, fileName.lastIndexOf('.')) + ".pdf";
                doc.setBasePath(doc.getBasePath().substring(0, doc.getBasePath().lastIndexOf('.')) + ".pdf");
            }    
            
            if (fileName.toLowerCase().endsWith(".zip")) {
                logger.error("Skipping ZIP document. File " + fileName
                        + ".");
                continue;
            }
            
            logger.debug(fileName);
            File file = new File(fileName);
            
            if (file.canRead()) {
                try {
                    writer.addDocument(FileDocument.Document(file, doc));
                    if (++filesProcessed % 100 == 0) {
                        logger.debug(filesProcessed + " files processed.");
                    }
                } catch (IOException e) {
                    logger.error("Exception adding document to index", e);
                }
            } else {
                ++missingFiles;
                logger.error("Unable to read permit file " + fileName);
            }
        }
        
        logger.error("Number of files processed " + filesProcessed);
        logger.error(missingFiles + " files could not be processed because they did not exist.");
    }

    private boolean initIndexDir(String indexDirName, String backupIndexDirName) throws IOException {
        boolean success = false;
        
        if (indexDirName == null) {
            ConfigManager cfgMgr = ConfigManagerFactory.configManager();
            Node root = cfgMgr.getNode("app.IndexDirectoryLocation");
            indexDirName = root.getAsString("value");
        }
        
        if (backupIndexDirName == null) {
            ConfigManager cfgMgr = ConfigManagerFactory.configManager();
            Node root = cfgMgr.getNode("app.BackupIndexDirectoryLocation");
            backupIndexDirName = root.getAsString("value");
        }

        if (indexDirName != null && indexDirName.length() > 0) {
        	logger.debug(" Keyword Search:  index dir = " + indexDirName);
        	File indexDirFile = new File(indexDirName);
            backupIndexDir = new File(backupIndexDirName);
            if (indexDirFile.exists()) {
                if (backupIndexDir.exists() && !rmDir(backupIndexDir)) {
                    logger.error("Keyword Search:  Unable to delete backup index directory: " + backupIndexDirName);
                } else {
                    success = indexDirFile.renameTo(backupIndexDir);
                }
            } else {
                success = true;
            }
            indexDir = FSDirectory.open(indexDirFile);
        }

        return success;
    }
    
    private boolean rmDir(File dir) {
        boolean ret = true;
        if (dir == null || !dir.exists()) {
        	if(dir == null) logger.error("IndexFiles.rmDir--encountered null");
        	else logger.error("IndexFiles.rmDir(" + dir.getAbsolutePath() + ") says does not exist");
            return false;
        }

        if (dir.isDirectory()) {
            for (File dirEntry : dir.listFiles()) {
                if (dirEntry.isDirectory()) {
                    if (!rmDir(dirEntry)) {
                    	logger.error("IndexFiles.rmDir(" + dirEntry.getAbsolutePath() + ") returned false");
                        ret = false;
                        break;
                    }
                }
                else {
                    ret = dirEntry.delete();
                }
            }
            if (ret) {
                ret = dir.delete();
            }
        } else {
        	logger.error("IndexFiles.rmDir(" + dir.getAbsolutePath() + ") says not a directory");
            ret = false;
        }
        return ret;
    }
    
    public static void main (String[] args) {
        IndexFiles indexer = new IndexFiles();
        if (indexer.isOk()) {
            System.out.println("Indexing files for keyword search...");
            indexer.run();
            System.out.println("Done indexing files for keyword search. " +
                    indexer.getFilesProcessed() + " files indexed.");
        } else {
            System.err.println("Error initializing keyword search FileIndexer");
        }
    }

    public final boolean isOk() {
        return ok;
    }

    public final void setOk(boolean ok) {
        this.ok = ok;
    }

    public final int getFilesProcessed() {
        return filesProcessed;
    }

    public final void setFilesProcessed(int filesProcessed) {
        this.filesProcessed = filesProcessed;
    }
}
